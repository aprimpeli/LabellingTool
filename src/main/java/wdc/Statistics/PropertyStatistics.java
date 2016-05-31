package wdc.Statistics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class PropertyStatistics {
	
	static String productCategory="headphone";
	String labelledFile="C:\\Users\\Johannes\\Google Drive\\Master_Thesis\\2.ProfilingOfData\\LabelledDataProfiling\\CorrectedLabelledEntities\\HeadphonesLabelledEntitiesProcessed.txt";
	static StatisticsItem stats;
	
	public static void main (String args[]) throws JSONException, IOException, URISyntaxException{
		stats = new StatisticsItem();
		PropertyStatistics get_stats= new PropertyStatistics();
		get_stats.getPropertyStatistics();
	}

	public void getPropertyStatistics() throws JSONException, IOException, URISyntaxException{
		
		
		JSONArray labelled = new JSONArray(FileCreator.fileToText(labelledFile));
		stats.setTotalEntities(labelled.length());
		for(int i = 0 ; i < labelled.length() ; i++){
			JSONObject entity = labelled.getJSONObject(i);

			//distinct plds
			URI uri = new URI(entity.getString("url"));
			String pld = uri.getHost();
			Integer pldCount = stats.getDistinctPLDs().get(pld);
			if(null==pldCount) pldCount=0;
			stats.getDistinctPLDs().put(pld, ++pldCount);
			
			//product category
			String product[] = entity.getString("normalized_product_name").split(";");
			for (int j=0;j<product.length;j++){
				Integer productCount = stats.getUniqueProductsCount().get(product[j]);
				if(null==productCount) productCount=0;
				stats.getUniqueProductsCount().put(product[j], ++productCount);
			}
			
			
			//have description
			String description= entity.getString("product-description");
			if(!description.isEmpty()) stats.setHaveDescription(stats.getHaveDescription()+1);
			
			//have title
			String title= entity.getString("product-name");
			if(!title.isEmpty()) stats.setHaveTitle(stats.getHaveTitle()+1);
		
			//have GTIN
			String gtin=entity.getString("GTIN");
			if(!gtin.isEmpty()) stats.setHaveGTIN(stats.getHaveGTIN()+1);
			
			//Check the properties if the items are not null
			JSONArray atts_title=entity.getJSONArray("title_atts");
			if(null!=atts_title) getPropertiesCount(atts_title, false, null, "title");
			
			JSONArray atts_desc=entity.getJSONArray("desc_atts");
			if(null!=atts_desc) getPropertiesCount(atts_desc, false, null,"description");
			
			JSONArray atts_map = entity.getJSONArray("atts_map");
			JSONArray atts_table = entity.getJSONArray("table_atts");
			if(atts_table.length()!=0) {
				stats.setHaveTable(stats.getHaveTable()+1);
				getPropertiesCount(atts_table, true, atts_map,"table");
			}
		
			JSONArray atts_list = entity.getJSONArray("list_atts");
			if(atts_list.length()!=0) {
				stats.setHaveList(stats.getHaveList()+1);
				getPropertiesCount(atts_list, true, atts_map,"list");
			}
		}
		//get the information
		stats.getInfo();
	}
	
	public static void getPropertiesCount(JSONArray element_atts, boolean isTableOrList, JSONArray atts_map, String elementType){
		
		String feature_value_pairs[][] = new String [element_atts.length()][2];

		for(int i = 0 ; i < element_atts.length() ; i++){
			feature_value_pairs[i][0]=element_atts.get(i).toString().split(":")[0];
			feature_value_pairs[i][1]=element_atts.get(i).toString().replace(feature_value_pairs[i][0]+":", "");			
		}
		
		//get attributes map as hashTable
		HashMap<String,ArrayList<String>> mapped_attributes = new HashMap<String,ArrayList<String>>();
		if(isTableOrList){
			for (int i=0; i<atts_map.length();i++){
				ArrayList<String> existing_values= mapped_attributes.get(atts_map.get(i).toString().split(":")[0]);
				if(null==existing_values) existing_values=new ArrayList<String>();
				existing_values.add(atts_map.get(i).toString().split(":")[1]);
				mapped_attributes.put(atts_map.get(i).toString().split(":")[0], existing_values);
			}
		}
		ArrayList<String> properties=getPropertiesFromFile();
		for(int i = 0 ; i < feature_value_pairs.length ; i++){
			ArrayList<String> propertyValues = new ArrayList<String>();
			//if table or list I need to get the mapped property
			if(isTableOrList){
				propertyValues=mapped_attributes.get(feature_value_pairs[i][0]);			
			}
			else{
				propertyValues.add(feature_value_pairs[i][0]);

			}
			
			if(null!=propertyValues){
				for(String propertyValue:propertyValues){
					if(!properties.contains(propertyValue))System.out.println("The defined properties do not contain the property:"+propertyValue);				
					if(elementType.equals("title")){
						Integer count= stats.getPropertiesFrequencyInTitle().get(propertyValue);
						if(null==count) count=0;
						stats.getPropertiesFrequencyInTitle().put(propertyValue, ++count);
					}
					else if (elementType.equals("description")){
						Integer count= stats.getPropertiesFrequencyInDescription().get(propertyValue);
						if(null==count) count=0;
						stats.getPropertiesFrequencyInDescription().put(propertyValue, ++count);
					}
					else if (elementType.equals("table")){
						Integer count= stats.getPropertiesFrequencyInTables().get(propertyValue);
						if(null==count) count=0;
						stats.getPropertiesFrequencyInTables().put(propertyValue, ++count);
					}
					else {
						Integer count= stats.getPropertiesFrequencyInLists().get(propertyValue);
						if(null==count) count=0;
						stats.getPropertiesFrequencyInLists().put(propertyValue, ++count);
					}
					if (elementType.equals("table")){
					if (null==stats.getPropertiesValues().get(propertyValue)) {

						stats.getPropertiesValues().put(propertyValue, new HashSet<String>());
					}
					stats.getPropertiesValues().get(propertyValue).add(feature_value_pairs[i][1]);
					}
				}
			}
			
			
		}	
			
			
	}
	
	/**
	 * @param atts_title
	 * @return
	 * For title and description - for table and list I need to get the equivalences
	 * from the mapped attributes
	 * To be used if we correct the JSON file
	 */
//	public static HashMap<String,Integer> getPropertiesCount(JSONArray atts_title, boolean isTableOrList, JSONArray atts_map){
//		
//		int propertiesEntered=0;
//		ArrayList<String> properties=getPropertiesFromFile();
//		HashMap<String, Integer> countUniqueProperties = new HashMap<String,Integer>();
//		for(int i = 0 ; i < atts_title.length() ; i++){
//			
//			for (String property: properties){
//				//if table or list I need to get the mapped property
//				if(isTableOrList){
//					boolean mapped_found=false;
//					
//					for(int j=0;j<atts_map.length();j++){
//						if(mapped_found) break;
//						JSONObject mapped_pair = atts_map.getJSONObject(j);
//						
//						Map<String,String> map = new HashMap<String,String>();
//					    Iterator<String> iter = mapped_pair.keys();
//					    while(iter.hasNext()){
//					        String key = (String)iter.next();
//					        String value = mapped_pair.getString(key);
//					        map.put(key,value);
//					    }
//					    for(Map.Entry<String, String> mapEntry:map.entrySet()){
//					    	if(mapEntry.getValue().equals(property)) {
//					    		property=mapEntry.getKey();
//					    		mapped_found=true;
//					    		break;
//					    	}
//					    }
//					}
//					 
//				}
//				String value= atts_title.getJSONObject(i).getString(property);
//				if (value.equals(null)) continue;
//				else {
//					Integer count= countUniqueProperties.get(property);
//					if(count.equals(null)) count=0;
//					countUniqueProperties.put(property, count++);
//					propertiesEntered++;
//				}
//			}
//		}
//		if(atts_title.length()!=propertiesEntered)
//			System.out.println("The properties count does not fit with the elements of the table. Please check the Statistics creation");
//		
//		return countUniqueProperties;
//	}
	
	public static ArrayList<String> getPropertiesFromFile(){
		String filename="";
		ArrayList<String> properties = new ArrayList<String>();
		if(productCategory.equals("phone")){
			filename="resources/Statistics/definedVocabularies/phone_vocab.txt";
		}
		else if (productCategory.equals("headphone")){
			filename="resources/Statistics/definedVocabularies/headphone_vocab.txt";
		}
		else if (productCategory.equals("tv"))		{
			filename="resources/Statistics/definedVocabularies/tv_vocab.txt";
		}			
		else {
			System.out.println("Cannot retrieve properties for "+productCategory+". The vocabularies stored can handle the following product categories: phones, headphone and tv ");			
			System.exit(0);
		}
		 try
		 {
			 FileInputStream in = new FileInputStream(filename);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String strLine;
		 
			  while((strLine = br.readLine())!= null)
			  {
				  properties.add(strLine);
			  }
			  br.close();
			  return properties;
		 }
		 catch(Exception e){
			 System.out.println(e);
			 return null;
		 }
	}
}
