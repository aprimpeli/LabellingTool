package wdc.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Preprocessor {
	
	public static HashSet<String> warcFiles;
	
	public static void main (String args[]) throws IOException{
		warcFiles= new HashSet<String>();
		getDistinctWARCs(args[0]);
		printTheList();
	}
	
	private static void printTheList() {

		for (String s:warcFiles)
			System.out.println(s);
	}

	public static void getDistinctWARCs(String filePath) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		 
		String line = null;
		while ((line = br.readLine()) != null) {
			String warcFile = line.split("\\|\\|")[9];
			if (warcFile.equals("description:")) System.out.println(line);
			warcFiles.add(warcFile);
		}
	 
		br.close();
	}
}
