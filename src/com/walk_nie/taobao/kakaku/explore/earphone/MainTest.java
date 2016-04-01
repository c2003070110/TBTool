package com.walk_nie.taobao.kakaku.explore.earphone;

import java.io.BufferedWriter;
import java.io.IOException;

import org.jsoup.nodes.Document;

public class MainTest {
	private String itemUrlPrefix = "http://kakaku.com/item/";

	//private String itemsFile = "item_list.txt";
	private String itemsFile = "test.txt";
	private String outputFile = "test_%s.txt";
	private String split = "\t";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainTest().process();
		System.exit(0);
	}

	public MainTest() {
	}

	public void process() {
		BufferedWriter priceBw = null;
		try {
			
			String itemUrl = "http://kakaku.com/item/J0000000399/";
			Document doc = EarphoneUtil.urlToDocumentKakaku(itemUrl);
			downloadPicture(doc);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (priceBw != null)
				try {
					priceBw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private void downloadPicture(Document doc) {
		// TODO Auto-generated method stub
		
	}

}
