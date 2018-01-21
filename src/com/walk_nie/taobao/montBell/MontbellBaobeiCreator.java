package com.walk_nie.taobao.montBell;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class MontbellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_baobei_%s.csv";
		//String publishedBaobeiFile = "";

		MontbellBaobeiProducer db = new MontbellBaobeiProducer();
		db
                .addScanCategory("22000") 
                .addScanCategory("") // 
                .addScanCategory("") //
                
                .setOutputFile(outputFile)
                .setCurrencyRate(0.069)
                .setBenefitRate(0.1)
                .process();

		System.exit(0);
	}

}
