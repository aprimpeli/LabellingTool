package wdc.Statistics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class StatisticsItem {
	
	
	private HashMap<String, HashSet<String>> propertiesValues;

	public StatisticsItem(){
		 this.distinctPLDs = new HashMap<String,Integer>();
		 this.uniqueProductsCount= new HashMap<String,Integer>();
		 this.haveDescription=0;
		 this.haveTitle=0;
		 this.haveTable=0;
		 this.haveList=0;
		 this.haveGTIN=0;
		 this.haveMPN=0;
		 this.propertiesFrequency= new HashMap<String,Integer>();
		 this.propertiesFrequencyInTitle= new HashMap<String,Integer>();
		 this.propertiesFrequencyInDescription= new HashMap<String,Integer>();
		 this.propertiesFrequencyInTables= new HashMap<String,Integer>();
		 this.propertiesFrequencyInLists= new HashMap<String,Integer>();
		 this.setPropertiesValues(new HashMap<String, HashSet<String>>());
	}
	public HashMap<String, Integer> getDistinctPLDs() {
		return distinctPLDs;
	}
	public void setDistinctPLDs(HashMap<String, Integer> distinctPLDs) {
		this.distinctPLDs = distinctPLDs;
	}
	public HashMap<String, Integer> getDistinctCategoriesCount() {
		return distinctCategoriesCount;
	}
	public void setDistinctCategoriesCount(
			HashMap<String, Integer> distinctCategoriesCount) {
		this.distinctCategoriesCount = distinctCategoriesCount;
	}
	public HashMap<String, Integer> getUniqueProductsCount() {
		return uniqueProductsCount;
	}
	public void setUniqueProductsCount(HashMap<String, Integer> uniqueProductsCount) {
		this.uniqueProductsCount = uniqueProductsCount;
	}
	public int getHaveDescription() {
		return haveDescription;
	}
	public void setHaveDescription(int haveDescription) {
		this.haveDescription = haveDescription;
	}
	public int getHaveTitle() {
		return haveTitle;
	}
	public void setHaveTitle(int haveTitle) {
		this.haveTitle = haveTitle;
	}
	public int getHaveTable() {
		return haveTable;
	}
	public void setHaveTable(int haveTable) {
		this.haveTable = haveTable;
	}
	public int getHaveList() {
		return haveList;
	}
	public void setHaveList(int haveList) {
		this.haveList = haveList;
	}
	public int getHaveGTIN() {
		return haveGTIN;
	}
	public void setHaveGTIN(int haveGTIN) {
		this.haveGTIN = haveGTIN;
	}
	public int getHaveMPN() {
		return haveMPN;
	}
	public void setHaveMPN(int haveMPN) {
		this.haveMPN = haveMPN;
	}
	public HashMap<String, Integer> getPropertiesFrequency() {
		return combineMaps();
	}
	
	private HashMap<String, Integer> combineMaps() {
		
		HashMap<String, Integer> combinedMap = new HashMap<String,Integer>();
		   for (Map.Entry<String, Integer> entry:propertiesFrequencyInDescription.entrySet()) {
			  Integer count = combinedMap.get(entry.getKey());
			  if (null==count) count=0;
			   combinedMap.put(entry.getKey(), count+entry.getValue());
		   }
		   for (Map.Entry<String, Integer> entry:propertiesFrequencyInTitle.entrySet()) {
			   Integer count = combinedMap.get(entry.getKey());
			  if (null==count) count=0;
			   combinedMap.put(entry.getKey(), count+entry.getValue());
		   }
		   for (Map.Entry<String, Integer> entry:propertiesFrequencyInTables.entrySet()) {
			   Integer count = combinedMap.get(entry.getKey());
			    if (null==count) count=0;
			   combinedMap.put(entry.getKey(), count+entry.getValue());
		   }
		   for (Map.Entry<String, Integer> entry:propertiesFrequencyInLists.entrySet()) {
			   Integer count = combinedMap.get(entry.getKey());
				  if (null==count) count=0;
				   combinedMap.put(entry.getKey(), count+entry.getValue());		   }
		
		return combinedMap;
	}
	public HashMap<String, Integer> getPropertiesFrequencyInTitle() {
		return propertiesFrequencyInTitle;
	}
	public void setPropertiesFrequencyInTitle(
			HashMap<String, Integer> propertiesFrequencyInTitle) {
		this.propertiesFrequencyInTitle = propertiesFrequencyInTitle;
	}
	public HashMap<String, Integer> getPropertiesFrequencyInDescription() {
		return propertiesFrequencyInDescription;
	}
	public void setPropertiesFrequencyInDescription(
			HashMap<String, Integer> propertiesFrequencyInDescription) {
		this.propertiesFrequencyInDescription = propertiesFrequencyInDescription;
	}
	public HashMap<String, Integer> getPropertiesFrequencyInTables() {
		return propertiesFrequencyInTables;
	}
	public void setPropertiesFrequencyInTables(
			HashMap<String, Integer> propertiesFrequencyInTables) {
		this.propertiesFrequencyInTables = propertiesFrequencyInTables;
	}
	public HashMap<String, Integer> getPropertiesFrequencyInLists() {
		return propertiesFrequencyInLists;
	}
	public void setPropertiesFrequencyInLists(
			HashMap<String, Integer> propertiesFrequencyInLists) {
		this.propertiesFrequencyInLists = propertiesFrequencyInLists;
	}
	public int getTotalEntities() {
		return totalEntities;
	}
	public void setTotalEntities(int totalEntities) {
		this.totalEntities = totalEntities;
	}
	private int totalEntities;
	private HashMap<String,Integer> distinctPLDs;
	private HashMap<String, Integer> distinctCategoriesCount;
	private HashMap<String, Integer> uniqueProductsCount;
	private int haveDescription;
	private int haveTitle;
	private int haveTable;
	private int haveList;
	private int haveGTIN;
	private int haveMPN;
	private HashMap<String, Integer> propertiesFrequency;
	private HashMap<String, Integer> propertiesFrequencyInTitle;
	private HashMap<String, Integer> propertiesFrequencyInDescription;
	private HashMap<String, Integer> propertiesFrequencyInTables;
	private HashMap<String, Integer> propertiesFrequencyInLists;
	
	public void getInfo(){
		System.out.println("---STATISTICS REPORT---");
		System.out.println("Total labelled entities in file:"+this.totalEntities);
		System.out.println("The following PLDs appear in the labelled set:");
		
		for (Map.Entry<String, Integer> pld:this.distinctPLDs.entrySet())
			System.out.println(pld.getKey()+":"+pld.getValue()+" times");
		System.out.println("The following unique products appear in the labelled set:");
		
		for (Map.Entry<String, Integer> product:this.uniqueProductsCount.entrySet())
			System.out.println(product.getKey()+":"+product.getValue()+" times");
		System.out.println(((double)this.haveGTIN/(double)this.totalEntities)*100+"% of the total entities contain GTIN annotated with Microdata");
		System.out.println(((double)this.haveTitle/(double)this.totalEntities)*100+"% of the total entities contain Title annotated with Microdata");
		System.out.println(((double)this.haveDescription/(double)this.totalEntities)*100+"% of the total entities contain Description annotated with Microdata");
		System.out.println(((double)this.haveTable/(double)this.totalEntities)*100+"% of the total entities contain at least one specification table");
		System.out.println(((double)this.haveList/(double)this.totalEntities)*100+"% of the total entities contain at least one specification list");

		
		System.out.println("---FREQUENCY OF PROPERTIES---");
		System.out.println("--IN TITLE--");
		for (Map.Entry<String, Integer> prop:this.propertiesFrequencyInTitle.entrySet())
			System.out.println(prop.getKey()+":"+prop.getValue()+" times");
		System.out.println("--IN DESCRIPTION--");
		for (Map.Entry<String, Integer> prop:this.propertiesFrequencyInDescription.entrySet())
			System.out.println(prop.getKey()+":"+prop.getValue()+" times");
		System.out.println("--IN TABLES--");
		for (Map.Entry<String, Integer> prop:this.propertiesFrequencyInTables.entrySet())
			System.out.println(prop.getKey()+":"+prop.getValue()+" times");
		System.out.println("--IN LISTS--");
		for (Map.Entry<String, Integer> prop:this.propertiesFrequencyInLists.entrySet())
			System.out.println(prop.getKey()+":"+prop.getValue()+" times");
		
		System.out.println("--OVERALL--");
		for (Map.Entry<String, Integer> prop:getPropertiesFrequency().entrySet())
			System.out.println(prop.getKey()+":"+prop.getValue()+" times");
		
		System.out.println("--Feature Value Pairs--");
		for (Map.Entry<String, HashSet<String>> prop:getPropertiesValues().entrySet()){
			String allValues ="";
			for (String s:prop.getValue()) allValues+=s+";;;";
			System.out.println(prop.getKey()+":"+allValues);

		}
		
	}
	
	public HashMap<String, HashSet<String>> getPropertiesValues() {
		return propertiesValues;
	}
	public void setPropertiesValues(HashMap<String, HashSet<String>> hashMap) {
		this.propertiesValues = hashMap;
	}
}
