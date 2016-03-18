package wdc.Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.dwslab.dwslib.util.io.InputUtil;
import de.dwslab.dwslib.util.io.OutputUtil;

public class NQProcessor {

	public static void main(String[] args) throws IOException {
		NQProcessor process= new NQProcessor();
		process.sortLinesOfFile("C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\tv-data\\test\\bestbuy.com0.warc.nq.gz");

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
		
		BufferedWriter writer= OutputUtil.getGZIPBufferedWriter(new File("resources","sortedNQFile.gz"));

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
}
