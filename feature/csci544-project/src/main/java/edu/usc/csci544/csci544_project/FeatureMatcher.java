package edu.usc.csci544.csci544_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class FeatureMatcher {

	HashMap<String, HashMap<String,List<String>>> featureSentenes = new HashMap<String, HashMap<String,List<String>>>();

	StanfordCoreNLP pipeline;
	public static final String[] SCREEN = {"screen" , "graphics", "touch","brightness" , "lcd","gorrila","touch"};
	public static final String[] MEMORY = {"memory" , "upgradable", "RAM"};
	public static final String[] BATTERY = {"battery" , "temperature", "heating","standby" , "stand by"};
	public static final String[] CAMERA = {"camera" , "cam", "front cam","rear cam" , "picture" , "picture quality" , "image quality" ,"video","photos"};
	public static final String[] ACCESSORIES = {"headset" , "head set", "charger","charger" , "ports","data cable","cable","headphone" ,  "headphone jack" , "headphones jack"};
	public static final String[] SOFTWARE = {"software" , "ui", "os updates","os" , "os features","os compatibility","file manager","gaming" ,  "android experience" };
	public static final String[] RESPONSIVE = {"responsive" , "speed", "lag","hang" , "freeze" , "processor"};
	public static final String NO_FEAT_FOUND = "np";
	public static HashMap<String,List<String>> synonyms;

	public FeatureMatcher()
	{
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		pipeline = new StanfordCoreNLP(props);

		if(synonyms==null)
		{
			synonyms = new HashMap<String, List<String>>();

			synonyms.put("screen", Arrays.asList(SCREEN));
			synonyms.put("memory", Arrays.asList(MEMORY));
			synonyms.put("battery", Arrays.asList(BATTERY));
			synonyms.put("camera", Arrays.asList(CAMERA));
			synonyms.put("accessories", Arrays.asList(ACCESSORIES));
			synonyms.put("software", Arrays.asList(SOFTWARE));
			synonyms.put("responsive", Arrays.asList(RESPONSIVE));


		}


	}
	
	public void persist(String folder) throws IOException
	{
		for(String feature : featureSentenes.keySet())
		{
			File file = new File(folder + feature);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(String user : featureSentenes.get(feature).keySet())
			{
				StringBuilder reviewLine = new StringBuilder();
				for (String line: featureSentenes.get(feature).get(user))
				{
					reviewLine.append(line);
					if(!line.trim().endsWith("."))
						reviewLine.append(".");
				}
				bw.write(reviewLine.toString()+"\n");
						
			}
			
			bw.close();
		}
	}
	public  void mapFeatures(String reviewID,List<String> sentences)
	{
		for(String sentence : sentences)
		{
			Annotation document = new Annotation(sentence);
			pipeline.annotate(document);

			List<String> tokens = new ArrayList<String>();
			
			
			
			
			for (CoreLabel token: document.get(TokensAnnotation.class)) {
				tokens.add(token.get(TextAnnotation.class).toLowerCase());
			}
			List<String> feat = getFeat(tokens);
			if(feat.size()!=0)
			{
				for(String f : feat)
				{
					if (!featureSentenes.containsKey(f))
						featureSentenes.put(f, new HashMap<String, List<String>>());
					if(!(featureSentenes.get(f)).containsKey(reviewID))
					{
						featureSentenes.get(f).put(reviewID, new ArrayList<String>());

					}
					featureSentenes.get(f).get(reviewID).add(sentence); 			
				}
				
				    			    	  


			}

		}

	}


	public static List<String> getFeat(List<String> tokens)
	{
		List<String> res = new ArrayList<String>();
		for(String key : synonyms.keySet())
		{
			for(String token: tokens)
			{
				if(synonyms.get(key).contains(token))
					res.add(key);
			}
		}
		return res;
	}
	public static void main(String[] args) throws IOException {

		FeatureMatcher fm = new FeatureMatcher();
		String IPfolderName = "/home/lk/Desktop/github/NLP_PROJECT/SentenceCorrection/output/";
		String OPfolderName = "/home/lk/Desktop/github/NLP_PROJECT/SentenceCorrection/output_features/";
		
	
		String line;
		
		File folder = new File(IPfolderName);
		File[] listOfFiles = folder.listFiles();

		    for (int i = 0; i < listOfFiles.length; i++)
		    {
		    	HashMap<String,List<String>> sentences = new HashMap();
		      if (listOfFiles[i].isFile()) {
		    
		  		String fileName =listOfFiles[i].getName();
		  		System.out.println(fileName);
		    	  //System.out.println("File " + listOfFiles[i].getName());
		    	  
		  		int cnt = 0;
				try {
					FileReader fileReader = new FileReader(IPfolderName+listOfFiles[i].getName());
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String reviewID = "";
					ArrayList<String> reviewSenetnces = null ;
					while((line = bufferedReader.readLine()) != null) {
						
						//System.out.println(line);
						if (line.equals("{"))
							cnt++;
						else if(cnt==1)
						{
							cnt++;
							reviewID = line;
							reviewSenetnces  =  new ArrayList<String>();
						}
						else if(!line.equals("}"))
						{
							
							line = line.replaceAll("[^\\p{ASCII}]", "").replaceAll("\\.+","\\.");
							line = line.toLowerCase();
							Annotation document = new Annotation(line);
							fm.pipeline.annotate(document);
							
							for (CoreMap sentense: document.get(SentencesAnnotation.class)) {
								reviewSenetnces.add(sentense.get(TextAnnotation.class).toLowerCase());
							}
						}
						else
						{
							cnt = 0;
							sentences.put(reviewID, reviewSenetnces);
						}
					}   
					bufferedReader.close();         
				}
				catch(FileNotFoundException ex) {
					System.out.println("Unable to open file '" + 	fileName + "'");                
				}
				catch(IOException ex) {
					System.out.println("Error reading file '" + fileName + "'");                  
					}

				for(String key : sentences.keySet())
				{
					fm.mapFeatures(key,sentences.get(key));
				}
		      
		      } 
		    }
		fm.persist(OPfolderName);

	}
	
}
