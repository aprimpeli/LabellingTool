package wdc.LabelingTool;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ldif.entity.NodeTrait;

import org.webdatacommons.structureddata.EntityProcessor;
import org.webdatacommons.structureddata.model.Entity;

import wdc.Utils.NQProcessor;

import com.cybozu.labs.langdetect.DetectorFactory;

import de.dwslab.dwslib.util.io.InputUtil;
import de.dwslab.dwslib.util.io.OutputUtil;
import de.jollyday.config.Which;
import de.wbsg.loddesc.util.DomainUtils;

public class EntitiesInspectorNQuads extends EntityProcessor{
	
	public EntitiesInspectorNQuads(String inputFolder, String outputFolder,
			int threads) throws Exception {
		super(inputFolder, outputFolder, threads);
		// TODO Auto-generated constructor stub
	}

	static BufferedWriter temp_writer;
	static BufferedWriter writer;
	static ArrayList<String> products;
	static ArrayList<String> forbiddenWords;
	static LabelUtils utils;
	static int entitiesProcessed=0;
	static String currentNQFile;
	static int entitiesTobeSearched=0;
	static String resultFile= "NQuadsProductResultsFiltered.txt";
	static String whichProducts;
	static ArrayList<String> subjects;
	
	public static void run(String whichProducts, String quadsFolder) throws Exception{			
		//for the language detection
		DetectorFactory.loadProfile("resources\\LanguageDetection\\profiles");			

		logger=new PrintWriter(new File("resources","LOGGER_NQuadsProductResults.txt"));;
		temp_writer= OutputUtil.getGZIPBufferedWriter(new File("resources","allNames_Description.zip"));
	    writer = new BufferedWriter(new FileWriter(new File("resources","NQuadsProductResults.txt")));
		utils= new LabelUtils();
		
		products= getEntityNames(whichProducts);
		products = changeToLowerCase(products);
		//findProductsInQuadsFile(quadsFolder);
		findProductsInQuadsFileQuadHelper(quadsFolder);
		
		writer.flush();
		temp_writer.flush();
		writer.close();
		temp_writer.close();
		System.out.println("Processed Entities:"+entitiesProcessed);
	}

	public static void main(String[] args) throws Exception {
		//get which products you want to retrieve (test, tv, laptop, mobiles, all)
		whichProducts="mobile_phone";
		if(!(whichProducts.equals("headset")||whichProducts.equals("television")||whichProducts.equals("mobile_phone"))){
			System.out.println("Check the product category. It can only be headset, mobile_phone or television");
			System.exit(0);
		}
			
		//path to the guads file
		String quadsFolder= "C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\phone-data\\nq";
		
		//for the language detection
		DetectorFactory.loadProfile("resources\\LanguageDetection\\profiles");			

		logger=new PrintWriter(new File("resources","LOGGER_NQuadsProductResults.txt"));;
		temp_writer= OutputUtil.getGZIPBufferedWriter(new File("resources","allNames_Description.zip"));
	    writer = new BufferedWriter(new FileWriter(new File("resources",resultFile)));
		utils= new LabelUtils();
		
		products= getEntityNames(whichProducts);
		products = changeToLowerCase(products);
		forbiddenWords = getForbiddenWords(whichProducts);
		//findProductsInQuadsFile(quadsFolder);
		findProductsInQuadsFileQuadHelper(quadsFolder);
		
		writer.flush();
		temp_writer.flush();
		writer.close();
		temp_writer.close();
		System.out.println("Processed Entities:"+entitiesProcessed);
	}

	
	private static ArrayList<String> getForbiddenWords(String whichProducts) {
		forbiddenWords= new ArrayList<String>();
		if(whichProducts.equals("mobile_phone")){
			forbiddenWords.add("battery");
			forbiddenWords.add("cable");
			forbiddenWords.add("charg");
			forbiddenWords.add("digitizer");
			forbiddenWords.add("case");
			forbiddenWords.add("headphone");
			forbiddenWords.add("protect");
			forbiddenWords.add("smartphones");
			forbiddenWords.add("bluetooth");
			forbiddenWords.add("lens");
			forbiddenWords.add("adapter");
			forbiddenWords.add("holder");
			forbiddenWords.add("earphone");
			forbiddenWords.add("headset");
			forbiddenWords.add("tripod");
			forbiddenWords.add("armband");
		}
		else if (whichProducts.equals("headset")){
			
		}
		else if (whichProducts.equals("television")){
			
		}
		else System.out.println("The type of products "+whichProducts+" cannot be processed by the labelling tool. Please check spelling");
		return forbiddenWords;
	}

	//alternative implementation using QuadHelper project
	private static void findProductsInQuadsFileQuadHelper(String folderPath) throws Exception{

		//list all files in quads directory		
		EntitiesInspectorNQuads processor= new EntitiesInspectorNQuads(folderPath,"resources/output.txt",1);
		subjects=new ArrayList<String>();
		List<File> inputFiles = processor.fillListToProcess();	
		NQProcessor sort = new NQProcessor();
		for (File f:inputFiles){
			System.out.println("Process file:"+f.getName());
			System.out.println(f.getName());
			//the process method only loads the map of entities 
			currentNQFile=f.getName();
			processor.process(f);			
		}
		
	}
	
	@Override
	protected void processEntity(Entity e) {
			entitiesProcessed++;
			try{
				
				String subject = e.getSubject().value();
		
				String title="";
				String description="";
				Map<String,List<NodeTrait>> predicates = e.getProperties();
				boolean toBeSearched=false;
				String url=e.getGraph();

				
					
//						for(Map.Entry<String, List<NodeTrait>> p: predicates.entrySet()){
//							System.out.println(p.getKey());
//							for(NodeTrait n:p.getValue()){
//								System.out.println(n.toString());
//								System.out.println(n.value());
//							}
//						}
				

				
				if(predicates.containsKey("http://schema.org/Product/name")){
					toBeSearched=true;
					for (NodeTrait o: predicates.get("http://schema.org/Product/name")){
						title+=o.toString()+" ";
					}
				}
				if(predicates.containsKey("http://schema.org/Product/description")){
					toBeSearched=true;

					for (NodeTrait o: predicates.get("http://schema.org/Product/description")){
						description+=o.toString()+" ";
					}
				}
				if(toBeSearched){
					entitiesTobeSearched++;
					for (String product:products){
											
						
						//calculate containment of arraylists
						String pr_description = description.toLowerCase().trim();
						
						String pr_title = title.toLowerCase().trim();
						ArrayList<String> productAsArray = new ArrayList<String>(Arrays.asList(product.toLowerCase().split(" ")));
						ArrayList<String> descriptionAsArray = new ArrayList<String>(Arrays.asList(pr_description.split(" ")));
						ArrayList<String> titleAsArray = new ArrayList<String>(Arrays.asList(pr_title.split(" ")));

						//WATCH OUT - the url condition should apply only for the tvs
						if ((descriptionAsArray.containsAll(productAsArray) || titleAsArray.containsAll(productAsArray)) ||
								(pr_description.contains(product.toLowerCase()) || 
										(url.toLowerCase().contains(product.toLowerCase())&& whichProducts.equals("television")) )	)		
						{
						//replace with the previous line if you want to be more flexible when it comes to the title
//							if ((description.toLowerCase().trim().contains(product.toLowerCase())) || 
//									title.toLowerCase().trim().contains(product.toLowerCase())){
						//choose one of the three if clauses above
//									if (predicate.endsWith("/name") && object.toLowerCase().equals(product.toLowerCase())){
//										//get uri and pld
							boolean containsCrappyWord=false;
							for(String forbidden:forbiddenWords){
								if (pr_title.contains(forbidden)) {
									containsCrappyWord=true;
									break;
								}
							}
							//get accessories by putting negation here
							if (!containsCrappyWord) continue;
							//URL
							String domain = de.wbsg.loddesc.util.DomainUtils.getDomain(url);
							String pld = DomainUtils.getPayLevelDomain(domain);	
							
							String gtin13= (predicates.containsKey("http://schema.org/Product/gtin13")) ? predicates.get("http://schema.org/Product/gtin13").get(0).toString() : "";
							String gtin14 = (predicates.containsKey("http://schema.org/Product/gtin14")) ? predicates.get("http://schema.org/Product/gtin14").get(0).toString() : "";

							//detect language of object
							String lang=utils.langDetector(title+" "+description);
							if(null==pld|| pld.equals("")) 
								System.out.println(url+" Could not retrieve PLD");
//								System.out.println(subject+"|"+product+"|"+url+"|"+pld+"|gtin13:"+gtin13+"|gtin14:"+gtin14+"|title:"+title+"|description:"+
//									description+"|"+lang+"|"+currentNQFile);
							//check for forbidden words - comment out if you dont wont it
							
							
							String toBeAppended= subject+"||"+product+"||"+url+"||"+pld+"||gtin13:"+gtin13+"||gtin14:"+gtin14+"||title:"+title+"||description:"+
									description+"||"+lang+"||"+currentNQFile;
							if(!subjects.contains(subject)){
								EntitiesInspectorNQuads.subjects.add(subject);
								writer.append(toBeAppended.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n"));
								writer.newLine();
							}
													
													
						}
					}
				}
				
				
			}
			
			catch (Exception exc){
				System.out.println(exc.getMessage());
			}
		
	}
	private static void findProductsInQuadsFile(
			String folderPath) {

		try{
			File folder = new File(folderPath);
			File[] listOfFiles = folder.listFiles();
			//list all files in quads directory
			
		    for (int i = 0; i < listOfFiles.length; i++) {		    	
		    	BufferedReader br = null;
				br = InputUtil.getBufferedReader(listOfFiles[i]);
				System.out.println(listOfFiles[i]);
				String line;
				while ((line = br.readLine()) != null) {
					String predicate = line.split("<")[1].split(">")[0];
					String subject= line.split("<")[0].trim();
					//the current line refers to the schema.org title or description of the product - look at the object and compare
					if (predicate.startsWith("http://schema.org/")&&
							(predicate.endsWith("/name")||(predicate.endsWith("/description")))){
						//append to the writer-just for stats reasons
						temp_writer.append(line);
						temp_writer.newLine();
						
						//separate the object value
						String object = line.substring(line.indexOf(">")+1, line.lastIndexOf("<")).replace("\"","").trim();
						
						ArrayList<String> objectAsArray = new ArrayList<String>(Arrays.asList(object.toLowerCase().split(" ")));
						
						//make everything lowercase and search for the listed products in the objects
						//check intersection of arraylists
						for (String product:products){
							ArrayList<String> productAsArray = new ArrayList<String>(Arrays.asList(product.toLowerCase().split(" ")));
							//if (objectAsArray.containsAll(productAsArray)){
							//replace with the previous line if you want to be more flexible when it comes to the title
//							if ((predicate.endsWith("/description") && object.toLowerCase().contains(product.toLowerCase())) || 
//									predicate.endsWith("/name") && object.toLowerCase().equals(product.toLowerCase())){
							//choose one of the three if clauses above
							if (predicate.endsWith("/name") && object.toLowerCase().equals(product.toLowerCase())){
								//get uri and pld
								URI uri = new URI(line.substring(line.lastIndexOf("<")+1, line.lastIndexOf(">")).trim());
								String host = uri.getHost();
								System.out.println(product+"|"+line+"|"+host+"|"+uri);
								writer.append(product+"|"+line+"|"+host+"|"+uri);
								writer.newLine();
							}
								
						}						
					}
				}
		    }
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

	}

	public static ArrayList<String> changeToLowerCase(ArrayList<String> strings)
	{
	    ListIterator<String> iterator = strings.listIterator();
	    while (iterator.hasNext())
	    {
	        iterator.set(iterator.next().toLowerCase());
	    }
	    return strings;
	}
	/**
	 * @param whichEntities
	 * @return
	 * based on a key word returns the list of products to be searched 
	 * !!!the product names are transformed to LOWERCASE
	 */
	public static ArrayList<String> getEntityNames(String whichEntities){
		
		ArrayList<String> requiredList=new ArrayList<String>();
		ArrayList<String> allProducts= new ArrayList<String>();
		//for test purposes
		if (whichEntities.equals("test")){
			requiredList = new ArrayList<String>(){
				private static final long serialVersionUID = 1L;
				{
				add("Nokia");
				add("Motorola");
				add("MacBook");	
				add("Samsung");
				add("Lenovo");
			}};
		}
		if(whichEntities.equals("television") || whichEntities.equals("all") ){
			requiredList = new ArrayList<String>() {
				private static final long serialVersionUID = 1L;
			{
			    add("XBR43X830C");
			    add("55EG9600");
			    add("65EG9600");
			    add("XBR55X850D");
			    add("UN50HU6900F");
			    add("UN40JU6400F");
			    add("Tde1384b");
			    add("UN60JS7000F");
			    add("D43-C1");
			    add("43UF6400");
			    
			}};
			allProducts.addAll(requiredList);
		}
		if(whichEntities.equals("headset") || whichEntities.equals("all")){
			requiredList= new ArrayList<String>(){
				private static final long serialVersionUID = 1L;{
				add("Shure SE215");
				add("Audio Technica ATH-M50X");
				add("Shure SRH440");
				add("Grado SR 60e");
				add("Sennheiser CX 3.00");
				add("Sennheiser Momentum 2.0");
				add("OPPO PM-3 Closed Back Planar");
				add("Shure SE425");
				add("Sennheiser HD 650");
				add("AKG K712 Pro");
				
			}};
			allProducts.addAll(requiredList);

		}
		if(whichEntities.equals("mobile_phone") || whichEntities.equals("all")){
			requiredList= new ArrayList<String>(){
				private static final long serialVersionUID = 1L;{
				add("iphone 4");;
				add("iphone 6");
				add("iphone 6 plus");
				add("Samsung Galaxy S4");
				add("Samsung Galaxy S5");
				add("Samsung Galaxy S6 Edge");
				add("Samsung Galaxy S6 Edge+");
				add("Samsung Galaxy S6");
				add("LG G4");
				add("Nexus 6P");
				add("Sony Xperia Z5");
				add("HTC One M9");
				add("Nokia Lumia 920");

//				add("Motorola Moto G");
//				add("Motorola Razr");
//				add("Motorola Moto X");
//				add("Nokia Lumia 800");
//				add("Nokia Lumia 1520");
//				add("Nokia Lumia 1320");
//				add("Nokia Lumia 1020");
//				add("Nokia Lumia 925");
//				add("Nokia Lumia 720");
//				add("LG Optimus L7");
//				add("LG Optimus G2");
//				add("LG Nexus");
//				add("LG Spectrum");


		}};
			allProducts.addAll(requiredList);
		}
		if(whichEntities.equals("new_phones") ||  whichEntities.equals("all")){
			requiredList= new ArrayList<String>(){
				private static final long serialVersionUID = 1L;{
					//deleted memory details from i-phones- check google docs
				add("Samsung Galaxy S6 Edge");
				add("Samsung Galaxy S6 Edge+");
				add("LG G4");
				add("Sony Xperia Z5");
				add("HTC One M9");
				add("Nexus 6P");

		}};
			allProducts.addAll(requiredList);
		}
		if(whichEntities.equals("all")) return changeToLowerCase(allProducts);
		else return changeToLowerCase(requiredList);
	}

	
}
