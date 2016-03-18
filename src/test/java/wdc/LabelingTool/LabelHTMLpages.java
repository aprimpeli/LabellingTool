package wdc.LabelingTool;

import java.io.IOException;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class LabelHTMLpages {

	@Test
	public void labelManyPages() throws LangDetectException{
		try {
			
			FeatureLabelinginHTML labeling = new FeatureLabelinginHTML();
			labeling.labelAllHTMLinDir("C:\\Users\\Anna\\Google Drive\\Master_Thesis\\DataToBeUsed\\"
					+ "CrawlerData\\phones\\February_part4\\HTMLToLabel","mobile_phone");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
