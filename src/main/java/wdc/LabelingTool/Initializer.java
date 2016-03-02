package wdc.LabelingTool;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Initializer {

	static Scanner sc;

	public static void main (String args[]) throws Exception{
		
		sc = new Scanner(System.in);

		System.out.println("Provide the path to the .nq files");
		String path_nq = sc.next();
		while(!Files.exists(Paths.get(path_nq))){
			System.out.println("Path does not exist. Provide the path to the .nq files");
			path_nq=sc.next();
		}
			
		System.out.println("Provide the path to the .warc files");
		String path_warc = sc.next();
		while(!Files.exists(Paths.get(path_warc))){
			System.out.println("Path does not exist. Provide the path to the .warc files");
			path_warc=sc.next();
		}
		System.out.println("Type television or laptop or mobile_phone or all to "
				+ "retrieve and label products of the equivalent type");
		String products = sc.next();
		while (!(products.equals("television") || products.equals("mobile_phone")|| products.equals("laptop") 
				|| products.equals("all"))){
			System.out.println("Wrong input. Type television or laptop or mobile_phone or all");
			products=sc.next();
		}
		System.out.println("The defined products will be searched in the .nq files");
		EntitiesInspectorNQuads.run(products, path_nq);
		System.out.println("The results after searching the .nq files are stored in the file resources/NQuadsProductResults.txt");
		System.out.println("Labeling process starts");
		FeatureLabelinginHTML labeling = new FeatureLabelinginHTML();
		labeling.run(path_warc);
	}
}
