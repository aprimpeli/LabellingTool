package wdc.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.dwslab.dwslib.util.io.InputUtil;
import de.dwslab.dwslib.util.io.OutputUtil;

public class NQProcessor {

	public static void main(String[] args) throws IOException {
		NQProcessor process= new NQProcessor();
		//process.sortLinesOfFile("C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\tv-data\\test\\bestbuy.com0.warc.nq.gz");
		process.getDifferenceOfNQFiles("C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\tvs\\EntitiesFromNQ_new.txt", "C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\tvs\\EntitiesFromNQ.txt");
	}
	
	/**
	 * @param filepath
	 * Some of the nq files are unsorted. Quad Helper can only create nodes with their properties if
	 * the nodes of same id are together - one after another
	 * This method reads the unzipped nq file, replaces _:node with node, sorts the lines and writes them back in
	 * a zipped file
	 * @throws IOException 
	 */
	public void sortLinesOfFile(String filepath) throws IOException{
		
	
		
		BufferedWriter writer= OutputUtil.getGZIPBufferedWriter(new File("resources","sortedNQ"));

		BufferedReader br = null;
		String line = "";
		br = InputUtil.getBufferedReader(new File(filepath));
		ArrayList<ArrayList<String>> nodes = new ArrayList<ArrayList<String>>();
		while ((line = br.readLine()) != null) {
			String nodeId= line.split(" ")[0].replace("_:", "");
			ArrayList<String> tmp = new ArrayList<String>();
			tmp.add(nodeId);
			tmp.add(line);
			nodes.add(tmp);			
		}		
		//sort the 2d arraylist
		Collections.sort(nodes, new Comparator<ArrayList<String>>() {    
		        @Override
		        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
		            return o1.get(0).compareTo(o2.get(0));
		        }               
		});
		for(ArrayList<String>n:nodes){
			writer.append(n.get(1));
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	/**
	 * @param file1
	 * @param file2
	 * Receive 2 nq files - store in a new file the lines that the two file DO NOT have in common
	 * @throws IOException 
	 */
	public void getDifferenceOfNQFiles(String bigFile, String smallFile) throws IOException{
		
		BufferedReader br = null;
		String line = "";
		br = InputUtil.getBufferedReader(new File(smallFile));
		HashMap <String,String> smallNodes = new HashMap<String,String>();
		ArrayList<String> differentLines =new ArrayList<String>();
		
		while ((line = br.readLine()) != null) {
			smallNodes.put(line.split("\\|\\|")[0], line);
		}	
		br.close();
		br = InputUtil.getBufferedReader(new File(bigFile));
		while ((line = br.readLine()) != null) {
			String nodeName= line.split("\\|\\|")[0];
			if(!smallNodes.containsKey(nodeName)) differentLines.add(line);
		}	
		BufferedWriter writer= new BufferedWriter(new FileWriter(new File("resources","differenceOfNQFiles.txt")));
		for(String l:differentLines){
			writer.append(l);
			writer.newLine();
		}
		writer.flush();
		writer.close();
		br.close();

	}
}
