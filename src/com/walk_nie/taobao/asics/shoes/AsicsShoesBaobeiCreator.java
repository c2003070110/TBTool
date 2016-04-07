package com.walk_nie.taobao.asics.shoes;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class AsicsShoesBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String taobeiTemplateFile = "in/asicsShoes_baobeiTemplate.csv";
		//String miaoshuTemplateFile = "in/asicsShoes_miaoshu_template.html";
		String outputFile = "out/asicsShoes_baobei_%s.csv";
		String publishedBaobeiFile = "";

		AsicsShoesBaobeiProducer db = new AsicsShoesBaobeiProducer();
		db
				.setOutputFile(outputFile)
                .setPublishedBaobeiFile(publishedBaobeiFile)              
                .process();

		System.exit(0);
	}

}
