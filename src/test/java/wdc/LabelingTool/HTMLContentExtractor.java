package wdc.LabelingTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;



public class HTMLContentExtractor {
	
	@Test
	public void getHTMLPagesFromLabelledEntities() throws IOException{
		
		String labelledFile="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\2.ProfilingOfData\\LabelledDataProfiling\\CorrectedLabelledEntities\\PhonesLabelledEntitiesProcessed.txt";
		String warcPath="C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-data\\warc";
		String htmlPath="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\2.ProfilingOfData\\LabelledDataProfiling\\HTML_Pages\\phones\\";
		byte[] encoded = Files.readAllBytes(Paths.get(labelledFile));
		JSONArray labelled = new JSONArray(new String(encoded, StandardCharsets.UTF_8));
		ArrayList<String> nodes = new ArrayList<String>();
		for(int i = 0 ; i < labelled.length() ; i++){
			
			JSONObject entity = labelled.getJSONObject(i);
			String url = entity.getString("url");
			String node= entity.getString("id_self");
			if(nodes.contains(node)) System.out.println("Dublicate:"+node);
			else nodes.add(node);
			File htmlFile = new File(htmlPath+"\\"+node+".html");
			if(htmlFile.exists()) {
				//System.out.println(htmlFile+" exists");
				continue;
			}
			String id_warc=entity.getString("id_warc");
			File f = new File(warcPath+"\\"+id_warc.replace(".nq", ""));
			if(!f.exists()) {
				warcPath="C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-dec-data\\warc";
				f = new File(warcPath+"\\"+id_warc.replace(".nq", ""));
				warcPath="C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-data\\warc";
				if(!f.exists()) {
					System.out.println(node+" not found but will search more");
					searchMore(node, htmlPath);
					continue;
				}
			}
			String htmlContent = 
					EntitiesInspectorWARC.getHTMLContentOfWARC
					(warcPath, id_warc.replace(".nq", ""), url);
			
			if(null== htmlContent ) {
				warcPath="C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-dec-data";
				htmlContent = 
						EntitiesInspectorWARC.getHTMLContentOfWARC
						(warcPath, id_warc.replace(".nq", ""), url);
				warcPath="C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-data\\warc";
				if(null== htmlContent ){
					System.out.println(node+" not found -CONTENT but will search more");
					searchMore(node, htmlPath);
					continue;
				}
			}
			BufferedWriter tempHTML = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlPath+
			node+".html"), "UTF-8"));
			System.out.println("Created in "+htmlPath+
					node+".html");
			//keep track of the mapping between the files and the nq lines
			tempHTML.append(htmlContent);
			tempHTML.flush();
			tempHTML.close();
			
		
			
		}
		
	}

	private void searchMore(String node, String pathToWriteHTML) throws IOException {
		String nq1="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part1\\february_part1_FileNQMap.txt";
		String nq2="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part2\\february_part2_FileNQMap.txt";
		String nq3="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part3\\february_part3_FileNQMap.txt";
		String nq4="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part4\\nqFileMap.txt";
		String nq5="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part5(non-overstock)\\nqFileMap.txt";
		String htmlFolder1="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part1\\HTML_February_part1";
		String htmlFolder2="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part1\\HTML_February_part2";
		String htmlFolder3="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part1\\HTML_February_part3";
		String htmlFolder4="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part1\\HTML_February_part4";
		String htmlFolder5="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\phones\\February_part1\\HTML_February_part5";
		String matchedhtmlName="";
		String htmlPath="";
		String[][] pathsToRead = new String[5][2];
		pathsToRead[0][0]=nq1;
		pathsToRead[0][1]=htmlFolder1;
		pathsToRead[1][0]=nq2;
		pathsToRead[1][1]=htmlFolder2;
		pathsToRead[2][0]=nq3;
		pathsToRead[2][1]=htmlFolder3;
		pathsToRead[3][0]=nq4;
		pathsToRead[3][1]=htmlFolder4;
		pathsToRead[4][0]=nq5;
		pathsToRead[4][1]=htmlFolder5;
		
		boolean found=false;
		for(int i=0;i<=4;i++){
			if (found) break;
			File file = new File(pathsToRead[i][0]);
			BufferedReader reader = new BufferedReader(new FileReader(file));

		    String line;
		    while ((line = reader.readLine()) != null) {
		        String nodeOfNQ= line.split("\\|\\|\\|\\|")[1].split("\\|\\|")[0];
		        if (nodeOfNQ.equals(node)){
		        	matchedhtmlName=line.split("\\|\\|\\|\\|")[0];
		        	reader.close();
		        	htmlPath= pathsToRead[i][1]+"\\"+matchedhtmlName;
		        	found=true;
		        	break;
		        }
		    }

		}
		if (found){
			File file = new File(htmlPath);
			File destinationDir = new File(pathToWriteHTML+node+".html");
			FileUtils.copyFileToDirectory(file, destinationDir);
		}
		else System.out.println("Could not be AT ALL found "+node);
	}
}
