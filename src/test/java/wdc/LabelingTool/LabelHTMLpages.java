package wdc.LabelingTool;

import java.io.IOException;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class LabelHTMLpages {

	@Test
	public void labelManyPages() throws LangDetectException{
		try {
			
			FeatureLabelinginHTML labeling = new FeatureLabelinginHTML();
			labeling.labelAllHTMLinDir("C:\\Users\\Anna\\Google Drive\\Master_Thesis\\1.PreparationOfData\\CrawlerData\\tvs\\HTML_part3_ebay","television");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
