package com.walk_nie.taobao.yonex.badminton;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class YonexBadBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String taobeiTemplateFile = "in/yonex_badmin_pad_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/yonex_badmin_miaoshu_template.html";
		String outputFile = "out/yonex_badmin_baobei_%s.csv";

		YonexBadBaobeiProducer db = new YonexBadBaobeiProducer();
		db.setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				.setTaobeiTemplateFile(taobeiTemplateFile)
				.process();

		System.exit(0);
	}

}
