package nlpproject;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
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

public class ReviewAnalyzer {


	public static void evaluateSentence(String text,Map<String, Integer> features_score_map,Map<String, Integer> features_count_map,JSONObject synonymsList){
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

		StanfordCoreNLP pipeline= new StanfordCoreNLP(props);

		Annotation reviewText = new Annotation(text);
		pipeline.annotate(reviewText);

		List<CoreMap> sentences = reviewText.get(CoreAnnotations.SentencesAnnotation.class);
		if(sentences != null && !sentences.isEmpty()){
			for(int i=0;i<sentences.size();i++){
				CoreMap sentence = sentences.get(i);
				Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String sentimentName = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
				Set<String> synonyms = synonymsList.keySet();
				for(String synonym : synonyms){
					if(sentence.toString().contains(synonym)){
						String feature = (String) synonymsList.get(synonym);
						features_score_map.put(feature, features_score_map.get(feature)+sentiment);
						Integer feature_review_count = features_count_map.get(feature) + 1;
						features_count_map.put(feature,feature_review_count);
					}
				}
				System.out.println("Sentiment of \n> \""+sentence.get(CoreAnnotations.TextAnnotation.class)+"\"\nis: " + sentiment+" (i.e., "+sentimentName+")");
			}

		}
	}

	public static boolean checkFeature(Set<String> features,String reviewText){
		boolean hasFeature=false;
		for(String feature : features){
			if(reviewText.contains(feature)){
				hasFeature=true;
				System.out.println("This sentence is analyzed as it has the feature "+feature);
				System.out.println(reviewText);
			}

		}
		return hasFeature;
	}

	public static void clearStuff(HashMap<String, Integer> features_score_map, HashMap<String, Integer> features_count_map,JSONObject synonymsList){
//		String[] features = {"camera","battery","display","service","service","lag","performance","flipkart"};
		@SuppressWarnings("unchecked")
		Collection<String> features = synonymsList.values();

		for(String feature : features){
			features_score_map.put(feature, 0);
			features_count_map.put(feature, 0);
		}
	}

	public static void writeDataToFile(JSONObject review,BufferedWriter outputFile) throws IOException{

		review.remove("sentences");
		outputFile.write(review.toJSONString()+"\n");


	}


	public static HashMap<String, Float> computeRating(HashMap<String, Integer> features_score_map, HashMap<String, Integer> features_count_map){
		Set<String> featuresList = features_score_map.keySet();
		HashMap<String, Float> feature_rating = new HashMap<String, Float>();
		for(String feature : featuresList){
			Float rating = 0.0f;
			if(features_count_map.get(feature) != 0)
				rating = ((float)features_score_map.get(feature) / features_count_map.get(feature));
			DecimalFormat df = new DecimalFormat("#.#");
			feature_rating.put(feature, Float.valueOf(df.format(rating)));
		}
		return feature_rating;
	}


	public static void main(String[] args) throws ClassNotFoundException, IOException, ParseException {

		String[] allowedPOS = {"JJ","NN","RB","RBR","RBS","VB","NNP"};
		List<String> allowedPOSList = Arrays.asList(allowedPOS);

		FileReader reader = new FileReader("sampleReviews_Final.json");
		org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
		JSONObject object = (JSONObject) parser.parse(reader);
		
		reader = new FileReader("synonyms.json");
		JSONObject synonymsList = (JSONObject) parser.parse(reader);
		
		FileWriter newFile = new FileWriter("reviewsAnalyzed.json");
		BufferedWriter outputFile = new BufferedWriter(newFile);

		JSONArray reviewsArray = (JSONArray) object.get("reviews");
//		MaxentTagger tagger = new MaxentTagger("taggers/wsj-0-18-left3words-distsim.tagger");

		//regex pattern for number matching
		String regex="(.)*(\\d)(.)*";
		Pattern pattern = Pattern.compile(regex);
		// features evaluation map
		HashMap<String, Integer> features_score_map = new HashMap<String, Integer>();
		HashMap<String,Integer> features_count_map = new HashMap<String, Integer>();

		for(int i=0;i<reviewsArray.size();i++){

			clearStuff(features_score_map, features_count_map,synonymsList);
			// Pick a review
			JSONObject review = (JSONObject) reviewsArray.get(i);
			System.out.println("count is "+review.get("count"));
			String reviewText = (String) review.get("desc");
			
//			JSONArray sentences = (JSONArray) review.get("sentences");
			
			reviewText = reviewText.toLowerCase();
			if(checkFeature(synonymsList.keySet(), reviewText)){
				evaluateSentence(reviewText, features_score_map,
						features_count_map,synonymsList);
			}
			
			
//			// traverse through sentences
//			for(int j=0;j<sentences.size();j++){
//				String sentence = (String) ((JSONObject)sentences.get(j)).get("sent");
//				System.out.println("Sentence is \n"+sentence);
//				String tagged = tagger.tagTokenizedString(sentence);
//				System.out.println("After tagging\n "+tagged);
//				String[] words = tagged.split(" ");
//				StringBuffer newSentenceBuffer = new StringBuffer();
//				for(int k=0;k<words.length;k++){
//					String text = words[k].split("_")[0];
//					String pos = words[k].split("_")[1];
//					if(allowedPOSList.contains(pos)){
//						newSentenceBuffer.append(text+" ");
//					}
//				}
//				//				System.out.println("After selecting certain POS\n"+newSentenceBuffer);
//				String newSentence = sentence.toString().toLowerCase();
//				newSentence=sentence.replace(".","").toLowerCase();
//				//				if (!pattern.matcher(newSentence).matches()) {
//				if (checkFeature(features_score_map.keySet(), newSentence)) {
//					evaluateSentence(newSentence, features_score_map,
//							features_count_map);
//
//				} else {
//					System.out.println("this sentence is not analyzed as it doesn't have any features");
//				}
//			} 
			//			else
			//				{
			//					System.out.println("This sentence is not analyzed as it has numbers");
			//				}

			HashMap<String, Float> feature_rating_map = computeRating(features_score_map, features_count_map);
			HashMap<String, Integer> new_feature_count_map = new HashMap<String, Integer>();
			
			for(String feature : features_count_map.keySet()){
				if(features_count_map.get(feature) == 0){
					feature_rating_map.remove(feature);
				}
			}
			for(String feature : features_count_map.keySet()){
				if(features_count_map.get(feature) != 0)
					new_feature_count_map.put(feature, features_count_map.get(feature));
			}
			JSONObject metadata = new JSONObject();
			metadata.put("rating", feature_rating_map);
			metadata.put("frequency", new_feature_count_map);
			review.put("Metadata", metadata);

			System.out.println("featuremap scores are \n" +features_score_map);
			System.out.println("Count of reviews containing our keywords are \n"+features_count_map);

			writeDataToFile(review, outputFile);

		}
		//		}
		/*
		JSONObject analyzed = new JSONObject();
		analyzed.put("rating", features_score_map);
		analyzed.put("count",features_count_map);
		outputFile.write(analyzed.toString());
		 */
		outputFile.close();
	}
}
