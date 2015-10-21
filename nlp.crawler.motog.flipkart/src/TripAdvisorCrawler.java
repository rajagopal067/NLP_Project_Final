
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TripAdvisorCrawler {

	public static void crawlCities() {

		String base = "http://www.flipkart.com/moto-g-2nd-generation";
		Map<String, String> segments = new LinkedHashMap<String, String>();

		segments.put("first",
				"/product-reviews/ITME7YBANGAWQZZX?pid=MOBDYGZ6SHNB7RFC&type=all");
		segments.put("rest",
				"/product-reviews/ITME7YBANGAWQZZX?pid=MOBDYGZ6SHNB7RFC&rating=1,2,3,4,5&reviewers=all&type=all&sort=most_helpful&start=");
		CrawlSeedUrl retrieveSeedUrl = new CrawlSeedUrl(base);
		List<Review> reviewList = new ArrayList<Review>();
		for (String city : segments.keySet()) {
			 reviewList.addAll(retrieveSeedUrl.crawlThingsToDo(segments
					.get(city),city));
			 }
		CreateJsonRead obj = new CreateJsonRead();
		int count = 0;
		int fileCount = 1;
		JSONArray lists = new JSONArray();
		for (Review rev : reviewList) {
			lists.add(obj.createJsonObect(rev));
			if(count > 500){
				JSONObject root = new JSONObject();
				root.put("filpkart", lists);
				obj.writeToFile(root, "/home/vijay/Desktop/motog"+fileCount+".json");
				count = 0;
				fileCount++;
				lists.clear();
			}
			count++;
		}
	}

	public static void main(String[] args) {
		crawlCities();
	/*	String rootFlder = "/home/vijay/csci548";
		List<String> cities = new ArrayList<String>();
		cities.add("NewYork");
		cities.add("Chicago");
		cities.add("London");
		cities.add("LosAngeles");
		List<JSONObject> ll;
		;
		for (String string : cities) {
			ll = processCrawledWebPages(string, rootFlder + "/" + string);
			JSONArray arr = new JSONArray();
			arr.addAll(ll);
			JSONObject citi = new JSONObject();
			citi.put(string, arr);
			new CreateJsonRead().writeToFile(citi, rootFlder + "/result/"
					+ string);

		}*/
	}

	
}
