package wdc.LabelingTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;

public class LabelUtils {
	
	public ArrayList<String> tokenizer(String text){
		  
		ArrayList<String> tokens=new ArrayList<String>();
		PTBTokenizer<Word> tokenizer=PTBTokenizer.newPTBTokenizer(new BufferedReader(new StringReader(text)));

		while (tokenizer.hasNext()) {
		    Word nextToken=tokenizer.next();
		    tokens.add(nextToken.toString());
		  }
	    System.out.println("tokens:" + tokens);
		return tokens;  
	}	
	
	public JSONObject createJSON(Annotation label){
		
		JSONObject obj = new JSONObject();
		obj.put("id_warc", label.getWarc_id());
		obj.put("id_self", label.getId());
		obj.put("GTIN",  label.getGtin());
		obj.put("MPN", label.getMpn());
		obj.put("url", label.getUrl());
		obj.put("category_google", label.getCategory_google());
		obj.put("category_GS1", label.getCategory_gs1());
		obj.put("category_page", label.getCategory_page());
		obj.put("normalized_product_name", label.getNormalized_product_name());
		obj.put("product-name", label.getProduct_name());
		obj.put("product-description", label.getProduct_description());
		obj.put("title_atts", label.getTitle_atts());
		obj.put("desc_atts", label.getDesc_atts());
		obj.put("table_atts", label.getTable_atts());
		obj.put("list_atts", label.getList_atts());
		obj.put("atts_map", label.getAtts_map());
	 
	    //JSONObject json = new JSONObject(obj);
	    return obj;
	}

	public String langDetector(String text){

		try {
			Detector detector = DetectorFactory.create();
	        detector.append(text);
	        Locale lang = new Locale(detector.detect());
	        String langName = lang.getDisplayLanguage();
	        return  langName;
	        
		} catch (LangDetectException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * @return
	 * Creates a HashMap with all the predefined attributes for every kind of product
	 * in order to check later for any new attributes that need to be mapped
	 */
	public HashMap<String,ArrayList<String>> defineAttributes(){
		HashMap<String, ArrayList<String>> attributes = new HashMap<String,ArrayList<String>>();
		attributes.put("television", new ArrayList<String>(){		
			private static final long serialVersionUID = 1L;
		{
			add("compatible_channels");
			add("compatible_computer_operating_system");
			add("compatible_display_resolution");
			add("compatible_display_type");
			add("compatible_usb_version");
			add("compatible_wattage");
			add("core_count");
			add("count");
			add("display_resolution");
			add("display_type");
			add("usb_version");
			add("water_resistance");
			add("brand");
			add("compatible_brand");
			add("refresh_rate");
			add("speed");
			add("tagline");
			add("size");
			add("color");
			add("modelnum");
			add("compatible_refresh_rate");
			add("weight");
			add("compatible_color");
			add("wattage");
			add("compatible_weight");
			add("compatible_modelnum");
			add("compatible_size");
			add("compatible_display_size");
			add("power_supply");
			add("voltage");
			add("usb_ports");
			add("product_type");
			add("compatible_power_supply");
			add("compatible_usb_ports");
			add("memory");
			add("product code");
			
		}});
		
		attributes.put("laptop", new ArrayList<String>(){
			private static final long serialVersionUID = 1L;

		{
			add("compatible_core_count");
			add("count");
			add("power_source");
			add("usb_version");
			add("compatible_product_type");
			add("display_size");
			add("core_count");
			add("product_type");
			add("usb_ports");
			add("brand");
			add("computer_processor_type");
			add("computer_operating_system");
			add("tagline");
			add("computer_cpu_speed");
			add("speed");
			add("compatible_display_size");
			add("hard_disk_size");
			add("compatible_hard_disk_size");
			add("compatible_brand");
			add("compatible_speed");
			add("color");
			add("size");
			add("compatible_computer_processor_type");
			add("ram");
			add("compatible_size");
			add("modelnum");
			add("voltage");
			add("memory");
			add("compatible_color");
			add("power_supply");
			add("wattage");
			add("compatible_ram");
			add("compatible_memory");
			add("compatible_computer_operating_system");
			add("compatible_computer_cpu_speed");
			add("product code");
		}});
		
		attributes.put("mobile_phone", new ArrayList<String>(){			 
			private static final long serialVersionUID = 1L;
		{
			
			add("ram");
			add("water_resistance");
			add("memory");
			add("brand");
			add("phone_type");
			add("computer_operating_system");
			add("phone_carrier");
			add("product_type");
			add("tagline");
			add("core_count");
			add("processor_type");
			add("color");
			add("wattage");
			add("power_supply");
			add("display_size");
			add("display_resolution");
			add("voltage");
			add("modelnum_mpn");
			add("modelnum");
			add("width");
			add("height");
			add("depth");
			add("weight");
			add("dimensions");
			add("body_material");
			add("rear_cam_resolution");
			add("front_cam_resolution");
			add("package_dimensions");
			add("product_code");
			add("product_gtin");
			add("manufacturer");

		}});
		
		return attributes;
	}
		
	public static void main (String args[]){
		LabelUtils utils = new LabelUtils();
		utils.countUniqueValuesinList("resources/delete.txt");
	}	
		
	public void countUniqueValuesinList(String filepath){
		 try
		  {
		  FileInputStream in = new FileInputStream(filepath);
		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
		  String line;
		  HashMap<String,Integer> uniqueValues = new HashMap<String,Integer>();
		  
		  while((line = br.readLine())!= null)
		  {
			  if(uniqueValues.containsKey(line))
				  uniqueValues.put(line, uniqueValues.get(line)+1);
			  else
				  uniqueValues.put(line, 1);
		  }
		  for (Map.Entry<String,Integer> e: uniqueValues.entrySet()){
				 System.out.println(e.getKey()+"|"+e.getValue());
			 }
		  br.close();
		  }
		 catch(Exception e){
		   System.out.println(e);
		  }
		 
		 
		
	}
}
