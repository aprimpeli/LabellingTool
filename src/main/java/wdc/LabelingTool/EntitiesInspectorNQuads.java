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

import com.cybozu.labs.langdetect.DetectorFactory;

import de.dwslab.dwslib.util.io.InputUtil;
import de.dwslab.dwslib.util.io.OutputUtil;
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
	static LabelUtils utils;
	static int entitiesProcessed=0;
	static String currentNQFile;
	static int entitiesTobeSearched=0;
	static String resultFile= "NQuadsProductResultsFiltered.txt";
	
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
		String whichProducts="mobile_phone";
		//path to the guads file
		String quadsFolder= "C:\\Users\\Anna\\Documents\\Student Job - DWS\\LabellingTool\\data";
		
		//for the language detection
		DetectorFactory.loadProfile("resources\\LanguageDetection\\profiles");			

		logger=new PrintWriter(new File("resources","LOGGER_NQuadsProductResults.txt"));;
		temp_writer= OutputUtil.getGZIPBufferedWriter(new File("resources","allNames_Description.zip"));
	    writer = new BufferedWriter(new FileWriter(new File("resources",resultFile)));
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

	//alternative implementation using QuadHelper project
	private static void findProductsInQuadsFileQuadHelper(String folderPath) throws Exception{

		//list all files in quads directory		
		EntitiesInspectorNQuads processor= new EntitiesInspectorNQuads(folderPath,"resources/output.txt",1);
		List<File> inputFiles = processor.fillListToProcess();		
		for (File f:inputFiles){
			System.out.println("Process file:"+f.getName());
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

						if (descriptionAsArray.containsAll(productAsArray) || titleAsArray.containsAll(productAsArray) ){
						//replace with the previous line if you want to be more flexible when it comes to the title
//						if ((description.toLowerCase().trim().contains(product.toLowerCase())) || 
//								title.toLowerCase().trim().contains(product.toLowerCase())){
						//choose one of the three if clauses above
//								if (predicate.endsWith("/name") && object.toLowerCase().equals(product.toLowerCase())){
//									//get uri and pld
							if (pr_title.contains("battery") || pr_title.contains("cable") 
									||pr_title.contains("charg") || pr_title.contains("digitizer") || pr_title.contains("case")||
									pr_title.contains("headphone") || pr_title.contains("protect") ||
									pr_title.contains("smartphones") || pr_title.contains("bluetooth") || pr_title.contains("lens") || pr_title.contains("adapter")
									||pr_title.contains("holder") || pr_title.contains("earphone") || pr_title.contains("headset") || pr_title.contains("tripod")|| 
									pr_title.contains("armband")) continue;
							//URL
							String url=e.getGraph();
							String domain = de.wbsg.loddesc.util.DomainUtils.getDomain(url);
							String pld = DomainUtils.getPayLevelDomain(domain);	
							
							String gtin13= (predicates.containsKey("http://schema.org/Product/gtin13")) ? predicates.get("http://schema.org/Product/gtin13").get(0).toString() : "";
							String gtin14 = (predicates.containsKey("http://schema.org/Product/gtin14")) ? predicates.get("http://schema.org/Product/gtin14").get(0).toString() : "";

							//detect language of object
							String lang=utils.langDetector(title+" "+description);
							if(null==pld|| pld.equals("")) 
								System.out.println(url+" Could not retrieve PLD");
//							System.out.println(subject+"|"+product+"|"+url+"|"+pld+"|gtin13:"+gtin13+"|gtin14:"+gtin14+"|title:"+title+"|description:"+
//								description+"|"+lang+"|"+currentNQFile);
							//check for forbidden words - comment out if you dont wont it
							
							
							String toBeAppended= subject+"||"+product+"||"+url+"||"+pld+"||gtin13:"+gtin13+"||gtin14:"+gtin14+"||title:"+title+"||description:"+
									description+"||"+lang+"||"+currentNQFile;
							writer.append(toBeAppended.replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n"));
							writer.newLine();						
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
			    add("VIZIO E231i-B1");
			    add("VIZIO P602ui-B3");
			    add("Vizio M471I-A");
			    add("VIZIO E390-B1E");
			    add("Samsung UN39FH5000");
			    add("Samsung PN51F4500");
			    add("Samsung UN32EH5300");
			    add("Samsung PN51F5300");
			    add("Samsung UN60EH600");
			    add("LG LA6200");
			    add("LG M2752D-PZ");
			    add("LG LN575V");
			    add("LG LS349C");
			    add("LG PN450P");
			    add("Toshiba 50L2200U");
			    add("Toshiba 40L5200U");
			    add("Toshiba 50PN6500");
			    add("LG 60PN6500");
			    add("Sharp LC60LE640U");
			    add("Sharp LC60LE650U");
			    add("Panasonic TC-P60S60");
			    add("Sony KDL32R400A");
			    add("Viewsonic PJD5134");
			    add("Philips PFL4508");
			    add("Philips PFL4505D");
			}};
			allProducts.addAll(requiredList);
		}
		if(whichEntities.equals("laptop") || whichEntities.equals("all")){
			requiredList= new ArrayList<String>(){
				private static final long serialVersionUID = 1L;{
				add("Apple MacBook Pro MC371B/A");
				add("Apple MacBook Pro ME664B/A");
				add("Apple MacBook Pro (13”/15”/17”)");
				add("Macbook Pro Md313ll/A ");
				add("Macbook Pro MGXA2LL/A");
				add("Lenovo Essential G50");
				add("Lenovo ThinkPad Edge E440 20C50052U");
				add("Lenovo ThinkPad L440");
				add("Lenovo Thinkpad L540");
				add("Lenovo ThinkPad T440s");
				add("Lenovo ThinkPad W540");
				add("Lenovo ThinkPad X140e");
				add("Lenovo Y50");
				add("HP EliteBook 820 G1");
				add("HP EliteBook 840 G1");
				add("Sony VAIO VPCYB35KX/P");
				add("Sony Vaio VGN-TT11");
				add("ASUS Zenbook UX32A");
				add("ASUS C200MA-DS01");
				add("ASUS X200CA-DH21T");
				add("HP Pavilion QD992UAR");
				add("Toshiba CB35-A");
				add("Acer Aspire One AO756-B847Xbb");
				add("Acer Aspire V5-531P-987B4G50");
				add("Acer Aspire One AO756-987BXkk");
			}};
			allProducts.addAll(requiredList);

		}
		if(whichEntities.equals("mobile_phone") || whichEntities.equals("all")){
			requiredList= new ArrayList<String>(){
				private static final long serialVersionUID = 1L;{
					//deleted memory details from i-phones- check google docs
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
