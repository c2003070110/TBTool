package com.walk_nie.taobao;

import java.io.IOException;

import com.walk_nie.taobao.kakaku.explore.earphone.EarphoneBaobeiProducer;


public class BaobeiCreateor {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	    createEarphone();
	}
	
	private static void createEarphone() throws IOException{
	      String scanUrlsFile = "in/earphone_scan_urls.txt";
	        String publishedBaobeiFile = "in/publishedBaobeiFile.csv";
	        String outputFile = "out/montBell_baobei_%s.csv";
	        EarphoneBaobeiProducer producer = new EarphoneBaobeiProducer();
	        producer
	            .setScanUrlsFile(scanUrlsFile)
	            .setOutputFile(outputFile)
	            .setPublishedBaobeiFile(publishedBaobeiFile)
	            .process();
	}
	 

}
