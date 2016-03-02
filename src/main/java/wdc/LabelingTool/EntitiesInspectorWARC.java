package wdc.LabelingTool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveReaderFactory;
import org.archive.io.ArchiveRecord;
import org.archive.io.ArchiveRecordHeader;
import org.archive.io.HeaderedArchiveRecord;
import org.archive.io.arc.ARCRecord;
import org.archive.io.warc.WARCReader;
import org.archive.io.warc.WARCReaderFactory;
import org.archive.io.warc.WARCRecord;
import org.jsoup.Jsoup;

import de.dwslab.dwslib.util.io.InputUtil;


public class EntitiesInspectorWARC {
	
	static BufferedWriter writer;
	static String sep ="||";

	public static void main (String args[]) throws FileNotFoundException, IOException, URISyntaxException{
		//get which products you want to retrieve (test, tv, laptop, mobiles, all)
		String products=args[0];
		//path to warc folder
		String folderPath= args[1];
	    writer = new BufferedWriter(new FileWriter(new File("resources","productsInWARCFiles.txt")));

		ArrayList<String> processedProductNames= EntitiesInspectorNQuads.getEntityNames(products);

		EntitiesInspectorWARC warcProcess = new EntitiesInspectorWARC();
		warcProcess.processFiles(folderPath, processedProductNames);
		
		//flush and close writer
		writer.flush();
		writer.close();
	}
	
	public static void run(String products, String folderPath) throws FileNotFoundException, IOException, URISyntaxException{
	    
		writer = new BufferedWriter(new FileWriter(new File("resources","productsInWARCFiles.txt")));
		ArrayList<String> processedProductNames= EntitiesInspectorNQuads.getEntityNames(products);

		EntitiesInspectorWARC warcProcess = new EntitiesInspectorWARC();
		warcProcess.processFiles(folderPath, processedProductNames);
		
		//flush and close writer
		writer.flush();
		writer.close();
	}
	
	private void processFiles(String folderPath,ArrayList<String> products)  {
		
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		String [] files = new String[listOfFiles.length];
	    
		for (int i = 0; i < listOfFiles.length; i++) {
	    	System.out.println(listOfFiles[i].toString());
	    	files[i]=listOfFiles[i].toString();
	    	try{
		    	processWarcFile(listOfFiles[i],listOfFiles[i].toString());

	    	}
	    	catch(Exception e){
	    		System.out.println("Error in processFiles: "+e.getMessage());
	    	}
	    }	
		
	}
	
	private void processWarcFile(String inputFile, ArrayList<String> products) throws IOException, URISyntaxException{
		
		BufferedReader br = null;
		String line = "";
		boolean isNextContent=false;
		String uri="";
		String content="";
		int countHeaders=0;
		try{
			br = InputUtil.getBufferedReader(new File(inputFile));
			while ((line = br.readLine()) != null) {
				if(line.startsWith("WARC/1.0")){
					countHeaders++;
					isNextContent=false;
					//if it's not the first record process the current recors, search for products
					if(!content.equals("")){
						searchForProduct(content, products, uri, inputFile);
					}
					//new record - reiinitialize content
					content="";
				}
				else if(line.startsWith("WARC-Target-URI:"))
					uri= line.split("WARC-Target-URI:")[1].trim();
				
				if(isNextContent){
					content+=line+"\n";
				}
				if(line.startsWith("WARC-Type:"))
					isNextContent=true;
				
			}
			br.close();
			System.out.println("Headers in file:"+countHeaders);
		}
		catch (Exception e){
			System.out.println("Error while processing the WARC files:"+e.getMessage());
		}
		
	}
	
	private void searchForProduct(String content, ArrayList<String> products, String page, String warcFile) throws URISyntaxException, IOException {
		
		String documentNOHtml = Jsoup.parse(content).text().toLowerCase();
		//search for the exact product name
		for (String product:products){
			
			//product name is found somewhere in the html response
			if(documentNOHtml.contains(product)){
				URI uri = new URI(page);
				String host = uri.getHost();
				// we only write if its valid
				if (host == null) {
					System.out.println("Could not get HOST out of the URI:"+uri);
					continue;
				}
				writer.append(product+sep+warcFile+sep+host+sep+uri);
				writer.newLine();
			}
		}	
		
	}

	private void processWarcFile(File file,
			String inputFileKey) throws Exception{
		// get archive reader
		WARCReader reader = WARCReaderFactory.get(file);
		List<ArchiveRecordHeader> headers = null;
		headers = reader.validate(1);
		reader.close();
		
		reader = WARCReaderFactory.get(file);
		for (int i = headers.size() - 1; i >= 0; i--) {
			ArchiveRecordHeader h = (ArchiveRecordHeader)headers.get(i);
			System.out.println(h.getUrl());
			ArchiveRecord r = reader.get(h.getOffset());
			String mimeType = r.getHeader().getMimetype();
		}
		reader.close();
		
//		Iterator<ArchiveRecord> readerIt = reader.iterator();
//		while (readerIt.hasNext()) {
//			System.out.println("next");
//			ArchiveRecord record = readerIt.next();
//			ArchiveRecordHeader header = record.getHeader();
//			URI uri;
//			uri = new URI(header.getUrl());
//			System.out.println(uri);
//			String host = uri.getHost();
//			// we only write if its valid
//			if (host == null) {
//				continue;
//			}
//			HeaderedArchiveRecord hRecord = new HeaderedArchiveRecord(record);
//		    ArchiveRecordHeader archiveHeader = hRecord.getHeader();
//		    System.out.println(archiveHeader.getUrl());
//			byte[] bytes = IOUtils.toByteArray(getPayload(record));
//			String content= new String(bytes, "UTF-8");
//			record.close();
//		}
		}

	 public static InputStream getPayload(ArchiveRecord record) throws IOException {
	        if(record instanceof ARCRecord) return new BufferedInputStream((ARCRecord) record);
	        if(record instanceof WARCRecord) return new BufferedInputStream((WARCRecord) record);
	        return null;
	  }
	 
	 public static String getHTMLContentOfWARC(String warcPath, String warcFile, String uri){
		
		BufferedReader br = null;
		String line = "";
		boolean isNextContent=false;
		String content="";
		try{
			System.out.println(warcPath+"/"+warcFile);
			br = InputUtil.getBufferedReader(new File(warcPath+"/"+warcFile));
			String currenturi="";
			while ((line = br.readLine()) != null) {
				if(line.startsWith("WARC/1.0")){
					isNextContent=false;
					//if it's not the first record process the current recors, search for products
					if(!content.equals("")){
						return content;						
					}
					//new record - reiinitialize content
					content="";
				}
				else if(line.startsWith("WARC-Target-URI:")){
					currenturi= line.split("WARC-Target-URI:")[1].trim();
					//if(!uri.equals(currenturi)) continue;
				}
				
				if(isNextContent){
					content+=line+"\n";
				}
				if(line.startsWith("WARC-Type:") && uri.equals(currenturi))
					isNextContent=true;
				
			}
			br.close();
		}
		catch (Exception e){
			System.out.println("Error while processing the WARC files:"+e.getMessage());
			System.exit(0);
		}
		return null;		
	 }
}

