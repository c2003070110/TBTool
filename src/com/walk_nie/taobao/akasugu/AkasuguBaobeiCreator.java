package com.walk_nie.taobao.akasugu;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class AkasuguBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String taobeiTemplateFile = "in/akasugu_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/akasugu_miaoshu_template.html";
		String outputFile = "out/akasugu_baobei_%s.csv";
		String publishedBaobeiFile = "";

		AkasuguBaobeiProducer db = new AkasuguBaobeiProducer();
		db.setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				.setTaobeiTemplateFile(taobeiTemplateFile)
				.setPublishedBaobeiFile(publishedBaobeiFile).process();

		System.exit(0);
	}

}
