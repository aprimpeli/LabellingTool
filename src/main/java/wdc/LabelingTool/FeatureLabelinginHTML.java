package wdc.LabelingTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class FeatureLabelinginHTML {
	
	static BufferedWriter  json_obj ;
	static BufferedWriter  tempHTML ;
	static BufferedWriter specificationquads;
	static BufferedWriter nqFilemappings;
	static Annotation label;
	
	static String outputFile="resources/JSON_LabelingOutput.txt";
	static String productsPath="C:\\Users\\Anna\\Google Drive\\Master_Thesis\\DataToBeUsed\\CrawlerData\\phones\\part5_nq.txt";
	static String specFile = "resources/specificationQUADS.txt";
	static String nqFileMap = "resources/nqFileMap.txt";
	static String warcPath= "C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-data\\warc";
	
	static HashMap<String, ArrayList<String>> definedAttributes = new HashMap<String, ArrayList<String>>();
	static LabelUtils utils;
	static String currentProductType="mobile_phone";
	static String sep="\\|\\|";
	static ArrayList<String> labeledUrls = new ArrayList<String>();
	private static Scanner input;
	static Scanner sc;
	
	public static void main (String args[]) throws IOException, LangDetectException{
		
		sc = new Scanner(System.in);
		FeatureLabelinginHTML labeling = new FeatureLabelinginHTML();
		BufferedReader reader= new BufferedReader(new FileReader(new File(productsPath)));
		json_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
		specificationquads = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(specFile), "UTF-8"));
		nqFilemappings = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nqFileMap), "UTF-8"));
		String line = "";
		String previousFile="";
		String answer="yes";
		
		//just to keep the names of the html pages
		HashMap<String, Integer> htmlPagesNames = new HashMap<String,Integer>();
		
		while (( line = reader.readLine()) != null) {
			try{
				
			
			//subject+"|"+product+"|"+url+"|"+pld+"|gtin13:"+gtin13+"|gtin14:"+gtin14+"|title:"+title+"|description:"+
			//description+"|"+lang+"|"+currentNQFile
			String language = line.split(sep)[8];
			if(!language.equals("English")) continue;
			String url=line.split(sep)[2];
			String pld= line.split(sep)[3];
			if(pld.equals("ebay.com")) continue;
			if(labeledUrls.contains(url)) System.out.println("URL: "+url+" appears twice! Please check!");
			labeledUrls.add(url);
			String nqFile=line.split(sep)[9];
			String title= line.split(sep)[6].toLowerCase();
			String productName= line.split(sep)[1];
		
			System.out.println("Node: "+line.split(sep)[0]);

			if(nqFile.equals(previousFile)&& answer.equals("no")) {
				previousFile=nqFile;
				continue;
			}
			if(!previousFile.equals(nqFile)){
				previousFile=nqFile;
				System.out.println(nqFile);
				System.out.println("Proceed? yes/no");
				//answer=sc.next();
				answer="yes";//to be replaced with the line above
				if (answer.equals("no")) continue;
			}
			
//			
			
			//get the html content of the current url and store it in a temporal file
			Integer count = htmlPagesNames.get(productName);
			if(null == count) htmlPagesNames.put(productName, 1);
			else htmlPagesNames.put(productName, count+1);

			String htmlContent = EntitiesInspectorWARC.getHTMLContentOfWARC(warcPath, nqFile.replace(".nq", ""), url);
			tempHTML = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resources/htmlPages/"+
			productName+"_"+htmlPagesNames.get(productName)+".html"), "UTF-8"));
			
			//keep track of the mapping between the files and the nq lines
			nqFilemappings.append(productName+"_"+htmlPagesNames.get(productName)+".html"+"||||"+line);
			nqFilemappings.newLine();
			
			tempHTML.append(htmlContent);
			tempHTML.flush();
			tempHTML.close();
			//search in warc
			System.out.println("Does it include specifications? : resources/tempHTML.html");
			//String includes=sc.next(); 
			String includes="yes"; //to be deleted replace with the line above
			if(includes.equals("yes")){
				specificationquads.append(line);
				specificationquads.newLine();
				
				System.out.println("Label the product?");
				//if(sc.next().equals("yes")){
				if(false){//replace with the line above
					System.out.println("The defined page includes specifications of mobile_phone, laptop or television? Type the relevant word");
					currentProductType=sc.next();
					//get the predefined Attributes
					utils= new LabelUtils();
					definedAttributes= utils.defineAttributes();
					
					while (!(currentProductType.equals("mobile_phone") || currentProductType.equals("laptop") 
							|| currentProductType.equals("television"))){
						System.out.println("Please type mobile_phone OR laptop or television");
						currentProductType=sc.next();
					}
					labeling.labelProduct(line, "resources/tempHTML.html");	
				}} 
			}
			catch(Exception e){
				System.out.println("Line: "+line+" could not be parsed");
				continue;
			}
		}
		reader.close();
		sc.close();
//		json_obj.flush();
//		json_obj.close();
		specificationquads.flush();
		specificationquads.close();
		nqFilemappings.flush();
		nqFilemappings.close();
		System.out.println("The JSON objects are stored in the directory: resources/JSON.txt");
		System.out.println("End of labeling process");

	}
	
	public void labelAllHTMLinDir (String directory, String productType) throws IOException, LangDetectException{
		
		DetectorFactory.loadProfile("resources\\LanguageDetection\\profiles");			
		utils = new LabelUtils();
		sc = new Scanner (System.in);
		json_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		System.out.println("Number of html files in the directory:"+listOfFiles.length);
		boolean passed= false;

		for (int i = 0; i < listOfFiles.length; i++) {
	    	
	    	BufferedReader reader= new BufferedReader(new FileReader(new File(nqFileMap)));
			String line="";
			while ((  line = reader.readLine()) != null) {
				String fileName= line.split("\\|\\|\\|\\|")[0];
				if(fileName.equals(listOfFiles[i].getName())){
					if (fileName.equals("iphone 5_14.html")) passed=true;
					if(!passed) continue;
					System.out.println(line);
					String []info = line.split("\\|\\|\\|\\|")[1].split("\\|\\|");
				    String lang = info[info.length-2].trim();
				    String pld = info[3];
			    
				    if (lang.equals("English")  ){
				    	System.out.println(listOfFiles[i].getAbsolutePath());
				    	System.out.println("Label the product?");
				    	
						if(sc.next().equals("yes")){
							//check for german entries in table- ask user and if it indeed german ignore this html page				    
						    if(pld.equals("overstock.com")){
						    	System.out.println("The labels of the table are going to be checked for language consistency. Please proceed only if all the labels are in English.");
								boolean onlyEnglish = utils.checkOverstockTable(listOfFiles[i].getPath());
								if (!onlyEnglish){
									System.out.println("Should you disregard the entry?(yes/no)");
									if(sc.next().equals("yes")){
										continue;
									}							
								}
							}
							//get the predefined Attributes
							utils= new LabelUtils();
							definedAttributes= utils.defineAttributes();
							labelProduct(line.split("\\|\\|\\|\\|")[1], listOfFiles[i].getPath() );	
							break;

						}
						}
					else break;
 
					} 
			}
			reader.close();
			

		}
		System.out.println("The JSON objects are stored in the directory: resources/JSON.txt");
		System.out.println("End of labeling process");
	
	}
	public void run (String warcPath) throws IOException, LangDetectException{
		
				
		BufferedReader reader= new BufferedReader(new FileReader(new File(productsPath)));
		json_obj = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
		
		String line = "";
		while (( line = reader.readLine()) != null) {
			//subject+"|"+product+"|"+url+"|"+pld+"|gtin13:"+gtin13+"|gtin14:"+gtin14+"|title:"+title+"|description:"+
			//description+"|"+lang+"|"+currentNQFile
			String url=line.split(sep)[2];
			String nqFile=line.split(sep)[9];
			//get the html content of the current url and store it in a temporal file
			String htmlContent = EntitiesInspectorWARC.getHTMLContentOfWARC(warcPath, nqFile.replace(".nq", ""), url);
			tempHTML = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("resources/tempHTML.html"), "UTF-8"));
			tempHTML.append(htmlContent);
			tempHTML.flush();
			tempHTML.close();
			//search in warc
			System.out.println("Does it include specifications? : resources/tempHTML.html");
			String includes=sc.next();
			if(includes.equals("yes")){
				System.out.println("The defined page includes specifications of mobile_phone, laptop or television? Type the relevant word");
				currentProductType=sc.next();
				//get the predefined Attributes
				utils= new LabelUtils();
				definedAttributes= utils.defineAttributes();
				
				while (!(currentProductType.equals("mobile_phone") || currentProductType.equals("laptop") 
						|| currentProductType.equals("television"))){
					System.out.println("Please type mobile_phone OR laptop or television");
					currentProductType=sc.next();
				}
				labelProduct(line, "resources/tempHTML.html");			
			} 
		}
		reader.close();
		json_obj.flush();
		json_obj.close();
		System.out.println("The JSON objects are stored in the directory: resources/JSON.txt");
		System.out.println("End of labeling process");
		sc.close();


	}
	public void labelProduct(String line, String htmlpage) throws IOException {
		
		//subject+"|"+product+"|"+url+"|"+pld+"|gtin13:"+gtin13+"|gtin14:"+gtin14+"|title:"+title+"|description:"+
		//description+"|"+lang	
		String specs[] = line.split(sep);
		String title=(specs[6].split("title:").length==2) ? 
				specs[6].split("title:")[1] : "";
		String description=(specs[7].split("description:").length==2) ?
				specs[7].split("description:")[1] : "";
		
				
		label = new Annotation();
		label.setWarc_id(specs[9]);
		label.setId(specs[0]);
		if(specs[4].split("gtin13:").length==1) {
			if(specs[5].split("gtin14:")[1].length()>1) label.setGtin(specs[5].split("gtin14:")[1]);
		}
		if (specs[4].split("gtin13:").length>1) label.setGtin(specs[4].split("gtin13:")[1]);
		label.setUrl(specs[2]);
		label.setNormalized_product_name(specs[1]);
		label.setProduct_name(title);
		label.setProduct_description(description);

		//create the jsoup doc object
		//File input = new File(htmlpage);
		
		//Document doc = Jsoup.parse(input, "UTF-8");		
		utils = new LabelUtils();

		System.out.println("Title:"+title);
		System.out.println("Label the title? yes/no");
		if(sc.next().equals("yes"))
			parseText(title, true);

		System.out.println("Description:"+description);
		System.out.println("Label the description? yes/no");
		if(sc.next().equals("yes"))
			parseText(description, false);
	
		System.out.println("Label the tables? yes/no");
		if(sc.next().equals("yes")) wrapperParse("table",htmlpage,specs[3]);
			//ownParse("table");
			//parseTable(doc);
		
		System.out.println("Label the lists? yes/no");
		if(sc.next().equals("yes"))  ownParse("list");
			//parseList(doc);
					
		JSONObject obj = utils.createJSON(label);
		System.out.println(obj);
		json_obj.append(obj.toString());
		json_obj.newLine();	
	}

	public  void parseTable(Document doc){

		if(sc.equals(null))
		  sc= new Scanner(System.in);
		
		Elements tableAmount = doc.select("table");
		System.out.println("The html page contains "+tableAmount.size()+" tables.");
		Set<String> labels = new HashSet<String>();

		for (Element table : doc.select("table")) {
		     System.out.println(table.html());
			 System.out.println("Is it a specification table? yes/no");
			 String answer = sc.next();
				if (answer.equals("yes")){
					ArrayList<String> elementsOfTable = new ArrayList<String>();
					System.out.println("If attribute names are structured in rows type tr otherwise type td");
					String structure= sc.next();
					while (!(structure.equals("tr")||structure.equals("td"))){
						System.out.println("Type tr for attribute names being in rows or td for attribute names in columns");
						structure=sc.next();
					}
					System.out.println("Annotate the elements of the table"); 
					for (Element attribute : table.select(structure)){
						String label=attribute.ownText();
						String valueName="";
						for (Element value : attribute.getAllElements()){
							valueName+=value.ownText();
						}
						System.out.println("Should the following labeling be added (yes/no)? "+label+":"+valueName);
						String labelanswer= sc.next();
						if (labelanswer.equals("yes")) {
							elementsOfTable.add(label+":"+valueName);
							labels.add(label);
						}
						else {
							System.out.println("Please add your own feature-value pair instead in the form <feature>:<value>");
							String spec= sc.next();
							elementsOfTable.add(spec);
							labels.add(spec.split(":")[0]);
						}
					}
					mapUndefinedLabels(labels);
				}
		}
	}
	
	public void wrapperParse(String structure, String file, String pld) throws IOException{
		
		boolean mapsDone=false;
		ArrayList<String> elementsOfTable = new ArrayList<String>();
		Set<String> labels = new HashSet<String>();
		
		if(pld.equals("ebay.com")){
			File file_ = new File(file);
			Document doc = Jsoup.parse(file_,"UTF-8") ;
			Element firstTable = doc.getElementsByClass("itemAttr").first();
			if(null!=firstTable){
				Elements items = firstTable.select("tr");
				for(Element item:items){
					Elements values= item.select("td");
					if(values.size()!= 4) continue;
					elementsOfTable.add(values.get(0).text()+values.get(1).text());
					elementsOfTable.add(values.get(2).text()+values.get(3).text());
					labels.add(values.get(0).text().replace(":", ""));
					labels.add(values.get(2).text().replace(":", ""));
					System.out.println(values.get(0).text()+values.get(1).text());
					System.out.println(values.get(2).text()+values.get(3).text());
					
					if(values.get(0).text().replace(":", "").equals("mpn")) label.setMpn(values.get(1).text());
					if(values.get(2).text().replace(":", "").equals("mpn")) label.setMpn(values.get(3).text());

					//add to the list 
					if(structure.equals("table")){
						ArrayList<String> tempList= label.getTable_atts();
						tempList.add(values.get(0).text()+values.get(1).text());
						tempList.add(values.get(2).text()+values.get(3).text());
						label.setTable_atts(tempList);	
					}
					}
				
			} 					
			//detailed table
			Element secondTable = doc.getElementsByClass("prodDetailSec").first();
			if (null!=secondTable){
				Elements detaileditems = secondTable.select("tr");
				for(Element item:detaileditems){
					Elements values = item.select("td");
					if(values.size()!=2) continue;
					else {
						if (values.get(1).text().isEmpty() || values.get(1).text()==null ) continue;
						elementsOfTable.add(values.get(0).text()+":"+values.get(1).text());
						labels.add(values.get(0).text());
						System.out.println(values.get(0).text()+":"+values.get(1).text());
						if(values.get(0).text().equals("mpn")) label.setMpn(values.get(1).text());
						if(structure.equals("table")){
							ArrayList<String> tempList= label.getTable_atts();
							tempList.add(values.get(0).text()+":"+values.get(1).text());
							label.setTable_atts(tempList);	
						}
					}
				}
			}
			System.out.println("Are the tables complete? (yes/no)");
			input = new Scanner(System.in);
			if(input.nextLine().equals("no")) ownParse("table");
			else {
				mapsDone = mapUndefinedLabels(labels);
				if(!mapsDone)
					System.out.println("Continue with labelling");
			}
						
		}
		else if (pld.equals("overstock.com")){
			File file_ = new File(file);
			Document doc = Jsoup.parse(file_,"UTF-8") ;
			Element firstTable = doc.select("table[class=table table-dotted table-extended table-header translation-table]").first();
			Element content = firstTable.select("tbody").first();
			if(null!=content){
				Elements items = content.select("tr");
				for(Element item:items){
					Elements values= item.select("td");
					if(values.size()!= 2) continue;
					
					elementsOfTable.add(values.get(0).text()+values.get(1).text());
					labels.add(values.get(0).text());
					System.out.println(values.get(0).text()+":"+values.get(1).text());
					
					if(values.get(0).text().equals("mpn")) label.setMpn(values.get(1).text());

					//add to the list 
					if(structure.equals("table")){
						ArrayList<String> tempList= label.getTable_atts();
						tempList.add(values.get(0).text()+values.get(1).text());
						label.setTable_atts(tempList);	
					}
					}	
				Element secondTable = doc.select("table[class=table table-dotted table-header]").first();
				Elements seconditems = secondTable.select("tr");
				if(null!=content){
					for(Element item:seconditems){
						Elements values= item.select("td");
						if(values.size()!= 2) continue;		
						elementsOfTable.add(values.get(0).text()+values.get(1).text());
						labels.add(values.get(0).text());
						System.out.println(values.get(0).text()+":"+values.get(1).text());		
						if(values.get(0).text().equals("mpn")) label.setMpn(values.get(1).text());

						//add to the list 
						if(structure.equals("table")){
							ArrayList<String> tempList= label.getTable_atts();
							tempList.add(values.get(0).text()+values.get(1).text());
							label.setTable_atts(tempList);	
						}
					}
					
				} 
			} 	
			System.out.println("Are the tables complete? (yes/no)");
			input = new Scanner(System.in);
			if(input.nextLine().equals("no")) ownParse("table");
			else {
				mapsDone = mapUndefinedLabels(labels);
				if(!mapsDone)
					System.out.println("Continue with labelling");
			}
		}
		if (pld.equals("tesco.com")){
			File file_ = new File(file);
			Document doc = Jsoup.parse(file_,"UTF-8") ;
			Element table = doc.select("div[id=product-spec-section-content]").first();
			if(null!=table){

				Elements labelCells = table.select("div[class=product-spec-cell product-spec-label]");
				Elements labelValues = table.select("div[class=product-spec-cell product-spec-value]");
				if (labelCells.size()!= labelValues.size()) System.out.println("The labels size and the value size do not match. Please check the Tesco wrapper.");
				for(int i=0; i<labelCells.size(); i++){
					String value= labelValues.get(i).text();
					String label_ = labelCells.get(i).text();
					elementsOfTable.add(label_+":"+value);
					labels.add(label_);
					System.out.println(label_+":"+value);
					
					if(label.equals("mpn")) label.setMpn(value);

					//add to the list 
					if(structure.equals("table")){
						ArrayList<String> tempList= label.getTable_atts();
						tempList.add(label_+":"+value);
						label.setTable_atts(tempList);	
					}
				}	
				System.out.println("Are the tables complete? (yes/no)");
				input = new Scanner(System.in);
				if(input.nextLine().equals("no")) ownParse("table");
				else {
					mapsDone = mapUndefinedLabels(labels);
					if(!mapsDone)
						System.out.println("Continue with labelling");
				}
			}
		}
		else ownParse("table");
				
	}
	/**
	 * @param structure
	 * Searching one by one the tables and lists is time conuming. Better to do this manually
	 * @throws IOException 
	 */
	public  void ownParse(String  structure) throws IOException{

		boolean mapsDone=false;
		input = new Scanner(System.in);
		ArrayList<String> elementsOfTable = new ArrayList<String>();
		Set<String> labels = new HashSet<String>();
		input.nextLine();
		while (!mapsDone){
			String answer_="yes";

			while(answer_.equals("yes")){
				System.out.println("Please add your own feature-value pair instead in the form <feature>:<value>");
				String spec= input.nextLine();
				while(spec.equals("")) spec= input.nextLine();
				elementsOfTable.add(spec);
				labels.add(spec.split(":")[0]);
				if(spec.split(":")[0].toLowerCase().equals("mpn")) label.setMpn(spec.substring(spec.indexOf(":")));
				//add to the list 
				if(structure.equals("table")){
					ArrayList<String> tempList= label.getTable_atts();
					tempList.add(spec);
					label.setTable_atts(tempList);	
				}
				else{
					ArrayList<String> tempList= label.getList_atts();
					tempList.add(spec);
					label.setList_atts(tempList);
				}
				
				System.out.println("Do you want to continue with "+structure+" specifications?(yes/no)");
				answer_=sc.nextLine();
				while(answer_.equals("")) answer_=sc.nextLine(); 
			}
			mapsDone = mapUndefinedLabels(labels);
			if(!mapsDone)
				System.out.println("Continue with labelling");
		}
		
	}
	public  void parseList(Document doc){
		
		Set<String> labels = new HashSet<String>();
		
		if(sc.equals(null))
		 sc= new Scanner(System.in);
		//print all lists and ask after every printing if it is a specification one
		Elements listAmount = doc.select("ul");
		System.out.println("The html page contains "+listAmount.size()+" lists.");
		for (Element list: doc.select("ul")){
			System.out.println(list.html());
			System.out.println("Is it a specification list? yes/no");
			String answer = sc.next();
			if (answer.equals("yes")){
				System.out.println("Annotate the elements of the list");
				
				ArrayList<String> elementsOfList = new ArrayList<String>();
				System.out.println("Give for every token the right labelling");
				for (Element listElement : list.select("li")){
					System.out.print("Create specification for the following element: "+listElement.text()+" in the form <feature>:<value>");
					String labeledItem = sc.next();
					String feature = labeledItem.split(":")[0];
					labels.add(feature);
					elementsOfList.add(labeledItem);
				}
				label.getTable_atts().addAll(elementsOfList);				
			}
			boolean mapsDone = mapUndefinedLabels(labels);
		}
//		String list_=input.split(":")[0];
//		boolean isClass= input.split(":")[1].equals("class");
//		String elementName= input.split(":")[2];
//		
//		for (Element list : doc.select("<ul>")) {			
//			if (isClass && !list.hasClass(elementName)) continue;
//		     for (Element li : list.select("li")) {		    	 
//		        if (!isClass && !li.text().contains(elementName)) break;
//		        else{
//		        	System.out.println(li.text());
//		        	System.out.println("Annotate the above elements like <attribute>:\"<value>\"");
//		        	String annot=sc.next();
//		        	label.getTable_atts().add(annot);
//		        	System.out.println("Add an extra attribute mapping like <defineAttribute>:\"NEWattribute\" or type next");
//		        	String extra=sc.next();
//		        	if(!extra.equals("next")) label.getAtts_map().add(extra);
//		        }
//		         
//		     }
//		}
	}
	
	public  void parseText(String text, boolean isTitle){
		
		ArrayList<String> tokensWithLabels = new ArrayList<String>();
		ArrayList<String> tokens = new ArrayList<String>();
		tokens=utils.tokenizer(text);
		Set<String> labels = new HashSet<String>();
		System.out.println("Give for every token the right labelling. Type next if you don't want to label the current token");
		for (String t:tokens){
			System.out.print(t+":");
			String label = sc.next();
			if(label.equals("next")) continue;
			tokensWithLabels.add(label+":"+t);
			labels.add(label);
		}
		if(isTitle) label.getTitle_atts().addAll(tokensWithLabels);
		else label.getDesc_atts().addAll(tokensWithLabels);
		mapUndefinedLabels(labels);
	}

	private  boolean mapUndefinedLabels(Set<String> labels) {
		
		ArrayList<String> definedLabels = new ArrayList<String>();
		definedLabels = definedAttributes.get(currentProductType);
		ArrayList<String> mappedAttributes = new ArrayList<String>();
		//get undefined feature names and ask the user to map them
		labels.removeAll(definedLabels);
		if (labels.size()>0)
			System.out.println("Map the undefined features to defined attributes of the product type:"+currentProductType +" or type \"go back\" to continue with the elements");
		for (String undefined:labels){
			System.out.println(undefined+":");
			String mapped = sc.next();
			if (mapped.equals("back"))
				{
					return false;
				}
			mappedAttributes.add(undefined+":"+mapped);
		}
		label.getAtts_map().addAll(mappedAttributes);
		return true;
	}
}
