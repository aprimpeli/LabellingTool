package wdc.LabelingTool;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class EBayWrapper {
	@Test
	public void getTableElements() throws IOException{
		File input = new File("C:\\Users\\Anna\\Google Drive\\Master_Thesis\\DataToBeUsed\\CrawlerData\\February_part3\\HTML_Pages_Februarypart3\\iphone 6 plus_1.html");
		Document doc = Jsoup.parse(input,"UTF-8") ;
		Element firstTable = doc.getElementsByClass("itemAttr").first();
		Elements items = firstTable.select("tr");
		for(Element item:items){
			Elements values= item.select("td");
			if(values.size()!= 4) continue;
			System.out.println(values.get(0).text()+values.get(1).text());
			System.out.println(values.get(2).text()+values.get(3).text());

			}
		
		//prodDetailSec
		Element secondTable = doc.getElementsByClass("prodDetailSec").first();
		Elements detaileditems = secondTable.select("tr");
		for(Element item:detaileditems){
			Elements values = item.select("td");
			if(values.size()!=2) continue;
			else {
				if (values.get(1).text().isEmpty() || values.get(1).text()==null ) continue;
				System.out.println(values.get(0).text()+":"+values.get(1).text());
			}
		}
		
		
	}
}
