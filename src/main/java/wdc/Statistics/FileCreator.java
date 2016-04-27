package wdc.Statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FileCreator {
	
	String fileNQ="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\february_version_fromNQ.txt";
	String labelledFile="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\3.MatchingModels\\testInput\\phones\\labelled.txt";


	/**
	 * gets the nq file maps only for the labelled entities as well as the correspondent HTML pages.
	 * Stores under the Statistics folder of resources
	 */
	public void getNQFileMapsforLabelled(){}
	
	/**
	 * @param fileNQ
	 * @param labelledFile
	 * @throws JSONException
	 * @throws IOException
	 * Gets as input the huge nq files and writes the quads that correspond to the labelled entities
	 */
	public void getNQForLabelled(String fileNQ, String labelledFile) throws JSONException, IOException{
		
		File output = new File("resources/Statistics/labelledNQs.txt");
		FileWriter fw = new FileWriter(output.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		

				
		Set<String> nodes = new HashSet<String>();
		
		JSONArray labelled = new JSONArray(fileToText(labelledFile));
		for(int i = 0 ; i < labelled.length() ; i++){
			JSONObject entity = labelled.getJSONObject(i);
			nodes.add(entity.getString("id_self"));
		}
		for (String node :nodes){
			FileInputStream fis = new FileInputStream(fileNQ);
			 
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		 
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.split("\\|\\|")[0].equals(node)){
					bw.write(line);
					System.out.println(line);
					break;
				}
			}
		 
			br.close();
		}
		bw.close();

	}
	
	public static String fileToText (String filepath) throws IOException{
		
		byte[] encoded = Files.readAllBytes(Paths.get(filepath));
		  return new String(encoded, StandardCharsets.UTF_8);	
	}
}
