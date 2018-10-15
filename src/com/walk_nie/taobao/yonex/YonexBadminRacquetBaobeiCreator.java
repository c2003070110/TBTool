package com.walk_nie.taobao.yonex;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class YonexBadminRacquetBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String taobeiTemplateFile = "in/yonex_badmin_pad_baobeiTemplate.csv";
		//String miaoshuTemplateFile = "in/yonex_badmin_miaoshu_template.html";
		String outputFile = "out/yonex_badmin_racquet_baobei_%s.csv";

		YonexBaobeiProducer db = new YonexBaobeiProducer();
		db
				.setOutputFile(outputFile)
				.setCategoryType(1)
				.process();

		System.exit(0);
	}

}
