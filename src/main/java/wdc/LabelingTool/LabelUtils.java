package wdc.LabelingTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			add("computer_operating_system");
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
			add("viewable_size");
			add("total_size");
			add("color");
			add("modelnum");
			add("compatible_refresh_rate");
			add("weight");
			add("compatible_color");
			add("wattage_operational");
			add("wattage_standby");
			add("yearly_consumption");
			add("compatible_weight");
			add("compatible_modelnum");
			add("compatible_size");
			add("compatible_display_size");
			add("power_supply");
			add("voltage");
			add("usb_ports");
			add("hdmi_ports");
			add("product_type");
			add("compatible_power_supply");
			add("compatible_usb_ports");
			add("memory");
			add("product code");
			add("series");	
			add("tv_tuner");
			add("pc_interface");
			add("video_interface");
			add("timer_functions");
			add("width");
			add("depth");
			add("height");
			add("image_aspect_ratio");	
			add("motion_enhancement_technology");
			add("24p_technology");
			add("backlight_technology");
			add("widescreen_modes");
			add("viewing_angle");
			add("supported_languages");
			add("commercial_features");
			add("stand_design");
			add("stand_color");
			add("analog_tv_tuner");
			add("stereo_reception _system");
			add("digital_tv_tuner");
			add("input_video_formats");
			add("supported_computer_resolution");
			add("supported_video_formats");
			add("supported_audio_formats");
			add("supported_picture_formats");
			add("remote_control_model");
			add("digital_audio_format");
			add("sound_effects");
			add("speakers_qty");
			add("speakers_type");
			add("connectivity");
			add("wifi_protocol");
			add("warranty");
			add("dimensions_with_stand");
			add("dimensions_without_stand");
			add("package_width");
			add("package_depth");
			add("package_height");
			add("package_weight");
			add("mpn");
			add("product_gtin");
			add("3d_technology");
			add("internet_services");
			add("image_contrast");
			add("brightness");
			add("response_time");
			add("supported_memory_cards");
			add("hdcp_compatability");
			add("stand");
			add("secondary_audio_program");
			add("channel_lock");
			add("closed_caption_capability");
			add("audio_surround");
			add("dlna");
			add("batteries_included");
			add("3d");
			add("builtin_dvd_player");

		}});
		
		attributes.put("headphone", new ArrayList<String>(){
			private static final long serialVersionUID = 1L;

		{
			add("product_type");
			add("width");
			add("depth");
			add("height");
			add("compatibility");
			add("series");
			add("weight");
			add("color");
			add("headphones_form_factor");
			add("headphones_cup_type");
			add("microphone_sensitivity");
			add("microphone_response");
			add("microphone_audio_details");
			add("headphones_technology");
			add("connectivity_technology");
			add("sound_output_mode");
			add("frequency_response");
			add("max_input_power");
			add("sensitivity");
			add("thd");
			add("diaphragm");
			add("impedance");
			add("magnet_material");
			add("controls");
			add("cables_included");
			add("included_accessories");
			add("compliant_standards");
			add("warranty");
			add("brand");
			add("mpn");
			add("product_gtin");
			add("color");
			add("foldable");
			add("microphone");
			add("detachable_cable");


		}});
		
		attributes.put("mobile_phone", new ArrayList<String>(){			 
			private static final long serialVersionUID = 1L;
		{
			
			add("ram");
			add("alert_types");
			add("audio_connectors");
			add("band_mode");
			add("battery_type");
			add("bluetooth_type");
			add("body_material");
			add("brand");
			add("box");
			add("browser_type");
			add("bundled_items");
			add("camera_flash");
			add("camera_type");
			add("card_slot");
			add("cellular");
			add("chipset");
			add("color");
			add("computer_operating_system");
			add("condition");
			add("connectivity");
			add("core_count");
			add("depth");
			add("design");
			add("dimensions");
			add("display_color");
			add("display_format");
			add("display_resolution");
			add("display_size");
			add("display_protection");
			add("display_type");
			add("display_technology");
			add("front_cam_resolution");
			add("general_memory");
			add("gpu");
			add("gps_type");
			add("language");
			add("launch_status");
			add("launch_date");
			add("loudspeaker");
			add("height");
			add("manufacturer");
			add("manufacturer_origin");
			add("memory");
			add("messaging");
			add("modelnum");
			add("mpn");
			add("network_technology");
			add("network_generation");	
			add("package_height");
			add("package_unit_type");
			add("package_weight");
			add("package_dimensions");
			add("phone_carrier");
			add("phone_type");
			add("power_supply");
			add("processor_type");
			add("processor_manufacturer");
			add("product_code");
			add("product_gtin");
			add("product_type");
			add("price");
			add("radio");
			add("rear_cam_resolution");
			add("sensors");
			add("shipping");
			add("SIM_card_quantity");
			add("SIM_type");
			add("tagline");
			add("store_options");
			add("standby_time");
			add("software");
			add("talk_time");
			add("touchscreen_type");
			add("video_definition");
			add("video_quality");
			add("voltage");
			add("wattage");
			add("weight");
			add("width");
			add("wlan");
			add("usb");
			add("unlocked");
			add("multitouch");
			add("nfc");
			add("infrared_port");
			add("is_customized");
			add("java");
			add("google_play");
			add("3.5mm_jack");
			add("bluetooth");
			add("gps");
			add("water_resistance");
			add("browser");
			add("speakerphone");
			add("email");
			add("digital_camera");
			add("touch_screen");
			add("QWERTY_physical_keyboard");
			add("wifi");
			add("auto_focus");

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

	
	public boolean checkOverstockTable(String path) throws IOException {
		File file_ = new File(path);
		Document doc = Jsoup.parse(file_,"UTF-8") ;
		//div[class=listing category_templates clearfix shelfListing  multiSaveListing]
		Element firstTable = doc.select("table[class=table table-dotted table-extended table-header translation-table]").first();
		Element content = firstTable.select("tbody").first();
		boolean onlyEnglish= true;
		if(null!=content){
			Elements items = content.select("tr");
			for(Element item:items){
				Elements values= item.select("td");
				if(values.size()!= 2) continue;			
				String lang= langDetector(values.get(0).text());
				if(!lang.equals("English")){
					System.out.println(values.get(0).text()+":"+lang);
					onlyEnglish=false;
				}
			}			
		} 
		//table table-dotted table-header
		Element secondTable = doc.select("table[class=table table-dotted table-header]").first();
		Elements seconditems = secondTable.select("tr");
		if(null!=content){
			for(Element item:seconditems){
				Elements values= item.select("td");
				if(values.size()!= 2) continue;			
				String lang= langDetector(values.get(0).text());
				if(!lang.equals("English")){
					System.out.println(values.get(0).text()+":"+lang);
					onlyEnglish=false;
				}
			}
			
		} 
		return onlyEnglish;
	}
}
