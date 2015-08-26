import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class TestPOS {


	public static void evaluateSentence(String text,Map<String, Integer> features_score_map,Map<String, Integer> features_count_map){
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

		StanfordCoreNLP pipeline= new StanfordCoreNLP(props);

		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);

		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		if(sentences != null && !sentences.isEmpty()){
			for(int i=0;i<sentences.size();i++){
				CoreMap sentence = sentences.get(i);
				Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
				Set<String> features = features_score_map.keySet();
				for(String feature : features){
					if(text.contains(feature)){
						features_score_map.put(feature, features_score_map.get(feature)+sentiment);
						Integer feature_review_count = features_count_map.get(feature) + 1;
						features_count_map.put(feature,feature_review_count);
					}
				}
				System.out.println("Sentiment of \n> \""+sentence.get(CoreAnnotations.TextAnnotation.class)+"\"\nis: " + sentiment+" (i.e., "+sentimentName+")");
			}

		}
	}

	public static boolean checkFeature(Set<String> features,String sentence){
		boolean hasFeature=false;
		for(String feature : features){
			if(sentence.contains(feature)){
				hasFeature=true;
				System.out.println("This sentence is analyzed as it has the feature "+feature);
				System.out.println(sentence);
			}
				
		}
		return hasFeature;
	}
	


	public static void main(String[] args) throws ClassNotFoundException, IOException, ParseException {

		String[] allowedPOS = {"JJ","NN","RB","RBR","RBS","VB","NNP"};
		List<String> allowedPOSList = Arrays.asList(allowedPOS);

		String[] features = {"camera","battery","display","screen","service","lag","flipkart"};
		List<String> featuresList = Arrays.asList(features);

		FileReader reader = new FileReader("reviewsFile_new.json");
		org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
		JSONObject object = (JSONObject) parser.parse(reader);

		FileWriter newFile = new FileWriter("reviewsAnalyzed.json");
		BufferedWriter outputFIle = new BufferedWriter(newFile);

		JSONArray reviewsArray = (JSONArray) object.get("reviews");
		MaxentTagger tagger = new MaxentTagger("taggers/wsj-0-18-left3words-distsim.tagger");
		
		//regex pattern for number matching
		String regex="(.)*(\\d)(.)*";
		Pattern pattern = Pattern.compile(regex);
		
		// features evaluation map
		HashMap<String, Integer> features_score_map = new HashMap<String, Integer>();
		HashMap<String,Integer> features_count_map = new HashMap<String, Integer>();
		for(String feature : featuresList){
			features_score_map.put(feature, 0);
			features_count_map.put(feature, 0);
		}
		for(int i=0;i<15;i++){

			// Pick a review
			JSONObject review = (JSONObject) reviewsArray.get(i);
			System.out.println("count is "+review.get("count"));
			JSONArray sentences = (JSONArray) review.get("sentences");
			
			// traverse through sentences
			for(int j=0;j<sentences.size();j++){
				String sentence = (String) ((JSONObject)sentences.get(j)).get("sent");
				System.out.println("Sentence is \n"+sentence);
				String tagged = tagger.tagTokenizedString(sentence);
				System.out.println("After tagging\n "+tagged);
				String[] words = tagged.split(" ");
				StringBuffer newSentenceBuffer = new StringBuffer();
				for(int k=0;k<words.length;k++){
					String text = words[k].split("_")[0];
					String pos = words[k].split("_")[1];
					if(allowedPOSList.contains(pos)){
						newSentenceBuffer.append(text+" ");
					}
				}
//				System.out.println("After selecting certain POS\n"+newSentenceBuffer);
				String newSentence = sentence.toString().toLowerCase();
				newSentence=sentence.replace(".","").toLowerCase();
//				if (!pattern.matcher(newSentence).matches()) {
					if (checkFeature(features_score_map.keySet(), newSentence)) {
						evaluateSentence(newSentence, features_score_map,
								features_count_map);

					} else {
						System.out.println("this sentence is not analyzed as it doesn't have any features");
					}
				} 
//			else
//				{
//					System.out.println("This sentence is not analyzed as it has numbers");
//				}
				System.out.println("featuremap scores are \n" +features_score_map);
				System.out.println("Count of reviews containing our keywords are \n"+features_count_map);
				
			}
//		}
		JSONObject analyzed = new JSONObject();
		analyzed.put("rating", features_score_map);
		analyzed.put("count",features_count_map);
		outputFIle.write(analyzed.toString());

		outputFIle.close();
	}
}
