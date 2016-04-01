package com.walk_nie.taobao.mangazenkan;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;


public class MangazenkanBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String taobeiTemplateFile = "in/manga_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/mangazenkan_miaoshu_template.html";
		String outputFile = "out/manga_baobei_%s.csv";
		String publishedBaobeiFile = "";

		MangazenkanProductParser  p = new MangazenkanProductParser();
		
//		String href ="http://www.mangazenkan.com/item/10328334.html" ;
//		p.parseBook(href );
		
		MangazenkanBaobeiProducer db = new MangazenkanBaobeiProducer();
		db.setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				.setTaobeiTemplateFile(taobeiTemplateFile)
				.setPublishedBaobeiFile(publishedBaobeiFile).process();

		System.exit(0);
	}

}
