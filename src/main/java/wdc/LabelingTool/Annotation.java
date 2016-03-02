package wdc.LabelingTool;

import java.util.ArrayList;
import java.util.Collection;

public class Annotation {

		private String warc_id="";
		private String id="";
		private String gtin="";
		private String mpn="";
		private String url="";
		private String category_google="";
		private String category_gs1="";
		private String category_page="";
		private String normalized_product_name="";
		private String product_description="";
		private String product_name="";
		private ArrayList<String> title_atts = new ArrayList<String>();
		private ArrayList<String> desc_atts= new ArrayList<String>();
		private ArrayList<String> table_atts= new ArrayList<String>();
		private ArrayList<String> list_atts= new ArrayList<String>();
		private ArrayList<String> atts_map= new ArrayList<String>();
		
		public String getFlag_list_table() {
			return flag_list_table;
		}
		public void setFlag_list_table(String flag_list_table) {
			this.flag_list_table = flag_list_table;
		}
		private String flag_list_table;
		public String getWarc_id() {
			return warc_id;
		}
		public void setWarc_id(String warc_id) {
			this.warc_id = warc_id;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getProduct_name() {
			return product_name;
		}
		public void setProduct_name(String product_name) {
			this.product_name = product_name;
		}
		public ArrayList<String> getTitle_atts() {
			return title_atts;
		}
		public void setTitle_atts(ArrayList<String> title_atts) {
			this.title_atts = title_atts;
		}
		public ArrayList<String> getDesc_atts() {
			return desc_atts;
		}
		public void setDesc_atts(ArrayList<String> desc_atts) {
			this.desc_atts = desc_atts;
		}
		public ArrayList<String> getTable_atts() {
			return table_atts;
		}
		public void setList_atts(ArrayList<String> list_atts) {
			this.list_atts = list_atts;
		}
		
		public ArrayList<String> getList_atts() {
			return list_atts;
		}
		public void setTable_atts(ArrayList<String> table_atts) {
			this.table_atts = table_atts;
		}
		public ArrayList<String> getAtts_map() {
			return atts_map;
		}
		public void setAtts_map(ArrayList<String> atts_map) {
			this.atts_map = atts_map;
		}
		public String getGtin() {
			return gtin;
		}
		public void setGtin(String gtin) {
			this.gtin = gtin;
		}
		public String getMpn() {
			return mpn;
		}
		public void setMpn(String mpn) {
			this.mpn = mpn;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getCategory_google() {
			return category_google;
		}
		public void setCategory_google(String category_google) {
			this.category_google = category_google;
		}
		public String getCategory_gs1() {
			return category_gs1;
		}
		public void setCategory_gs1(String category_gs1) {
			this.category_gs1 = category_gs1;
		}
		public String getCategory_page() {
			return category_page;
		}
		public void setCategory_page(String category_page) {
			this.category_page = category_page;
		}
		public String getNormalized_product_name() {
			return normalized_product_name;
		}
		public void setNormalized_product_name(String normalized_product_name) {
			this.normalized_product_name = normalized_product_name;
		}
		public String getProduct_description() {
			return product_description;
		}
		public void setProduct_description(String product_description) {
			this.product_description = product_description;
		}
		
}
