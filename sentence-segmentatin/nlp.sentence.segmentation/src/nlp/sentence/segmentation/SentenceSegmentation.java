package nlp.sentence.segmentation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SentenceSegmentation {

	public static void main(String[] args) {
		try {
			new SentenceSegmentation().main();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	PrintWriter writeOut;
	int count = 0;

	public void main() throws FileNotFoundException {
		writeOut = new PrintWriter("processed.txt");
		String[] fileNames = new File("flipkart").list();
		for (String file : fileNames) {
			readJsonData("flipkart" + File.separator + file, "filpkart", "name");
		}
		/*
		 * fileNames = new File("amaon").list(); for (String file : fileNames) {
		 * readJsonData(file,"amazon","author"); }
		 */
	}

	public void readJsonData(String file, String category, String id) {
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(file));
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray reviewList = (JSONArray) jsonObject.get(category);
			Iterator<JSONObject> iterator = reviewList.iterator();
			String id1;
			while (iterator.hasNext()) {
				id1 = "unknown";
				JSONObject next = iterator.next();
				writeOut.println("{");
				if ((String) next.get(id) != null
						&& ((String) next.get(id)).length() > 1)
					id1 = (String) next.get(id);
				writeOut.println(id1);
				List<String> sentences = performSegmentation((String) next
						.get("comment"));
				for (String sentence : sentences) {
					writeOut.println(sentence);
				}
				writeOut.println("}");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		writeOut.close();
	}

	public List<String> performSegmentation(String review) throws IOException {
		SentenceDetector _sentenceDetector = null;
		InputStream modelIn = null;
		try {
			// Loading sentence detection model
			modelIn = new FileInputStream("resources/en-sent.bin");
			final SentenceModel sentenceModel = new SentenceModel(modelIn);
			modelIn.close();
			_sentenceDetector = new SentenceDetectorME(sentenceModel);
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (final IOException e) {
				} // oh well!
			}
		}
		String[] result = _sentenceDetector.sentDetect(review);
		List<String> sentences = new ArrayList<String>();
		for (String string : result) {
			sentences.add(string);
		}
		return sentences;
	}
}
