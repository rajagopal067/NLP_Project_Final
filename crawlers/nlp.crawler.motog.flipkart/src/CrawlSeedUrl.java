
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlSeedUrl {

	private String url;
	private List<String> visitedUrl;

	public CrawlSeedUrl(String url) {
		this.url = url;
		this.visitedUrl = new ArrayList<String>();
	}

	public CrawlSeedUrl() {

	}

	private Document retrieveDocumentFromUrl(String url) throws IOException {
		Document doc = null;
		doc = Jsoup.connect(url).timeout(10 * 1000).get();
		return doc;
	}

	private Document retrieveDocumentLocal(File url) throws IOException {
		Document doc = null;
		doc = Jsoup.parse(url, null);
		return doc;
	}

	public List<Review> crawlThingsToDo(String segment, String key) {
		int count = 10;
		String page = this.url + segment+count;
		boolean listComments = false;
		
		List<Review> reviews = new ArrayList<Review>();
           while(true){
        	   
			Document doc;
			try {
				System.out.println(page);
				doc = retrieveDocumentFromUrl(page);
				Elements memberList = doc
						.select("div.fclear.fk-review.fk-position-relative.line");

				for (Element element : memberList) {
					Review comment = new Review();
					comment.setReviewComment(element.select("span.review-text")
							.text());
					comment.setRating(element.select("div.fk-stars").attr("title"));
					comment.setName(element.select("div.line").select("a[profile_name]").text());
					if(comment.getName().equals("")){
						comment.setName(element.select("div.line").get(0).select("span.fk-color-title.fk-font-11.review-username").text());
					}
					comment.setDate(element.select(
							"div.date.line.fk-font-small").text());
				    reviews.add(comment);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		/*
		 * Map<String, String> members = new HashMap<>(); while (nextPage &&
		 * members.size() < 250) { try { // get members for (Element element :
		 * memberList) { String tt = element.select("a").attr("href"); if
		 * (!members.containsValue(url + tt) && element.select("a").text() !=
		 * null) { members.put(element.select("a").text(), url + tt);
		 * 
		 * } else continue; } this.visitedUrl.add(page); Elements pageList =
		 * doc.select("div#pager_top").select("a"); String val =
		 * pageList.get(pageList.size() - 1).attr("href"); if
		 * (this.visitedUrl.contains(url + val)) { nextPage = false; } page =
		 * url + val; }
		 * 
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
			
			if (key.equals("first") || reviews.size() > 10000 ) { //11992
				break;
			}
			count = count+10;
			if(key.equals("rest")){
				page = this.url+segment+count;
			}
			
           }
		return reviews;
	}

	public void clearVisitedUrl() {
		this.visitedUrl.clear();
	}

	public void saveSummary(String location, String key, String sumUrl) {
		try {

			Document doc = retrieveDocumentFromUrl(sumUrl);
			String summaryContents = doc.toString();

			if (summaryContents.length() > 0) {
				PrintWriter writer = new PrintWriter(location + key + ".html",
						"UTF-8");
				writer.println(summaryContents);
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
}
