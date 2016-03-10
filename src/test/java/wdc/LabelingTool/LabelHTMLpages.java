package wdc.LabelingTool;

import java.io.IOException;

import org.junit.Test;

public class LabelHTMLpages {

	@Test
	public void labelManyPages(){
		try {
			
			FeatureLabelinginHTML labeling = new FeatureLabelinginHTML();
			labeling.labelAllHTMLinDir("C:\\Users\\Anna\\Google Drive\\Master_Thesis\\DataToBeUsed\\"
					+ "CrawlerData\\February_part1\\HTML_February_part1","mobile_phone");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
