package wdc.LabelingTool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

public class OverstockWrapper {
	@Test
	public void getTableElements() throws IOException, LangDetectException{
		DetectorFactory.loadProfile("resources\\LanguageDetection\\profiles");			

		LabelUtils utils = new LabelUtils();
		File file_ = new File("C:\\Users\\Anna\\Google Drive\\Master_Thesis\\DataToBeUsed\\CrawlerData\\phones\\February_part4\\HTMLToLabel\\iphone 5s_7.html");
		Document doc = Jsoup.parse(file_,"UTF-8") ;
		//div[class=listing category_templates clearfix shelfListing  multiSaveListing]
		Element firstTable = doc.select("table[class=table table-dotted table-extended table-header translation-table]").first();
		Element content = firstTable.select("tbody").first();
		if(null!=content){
			Elements items = content.select("tr");
			System.out.println(items.size());
			for(Element item:items){
				Elements values= item.select("td");
				if(values.size()!= 2) continue;			
				System.out.println(values.get(0).text()+":"+values.get(1).text());		
				System.out.println(values.get(0).text()+":"+utils.langDetector(values.get(0).text()));
			}			
		} 
		//table table-dotted table-header
		Element secondTable = doc.select("table[class=table table-dotted table-header]").first();
		Elements seconditems = secondTable.select("tr");
		if(null!=content){
			System.out.println(seconditems.size());
			for(Element item:seconditems){
				Elements values= item.select("td");
				if(values.size()!= 2) continue;			
				System.out.println(values.get(0).text()+":"+values.get(1).text());		
				System.out.println(values.get(0).text()+":"+utils.langDetector(values.get(0).text()));

			}
			
		} 
	}
	
}
