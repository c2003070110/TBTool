package com.walk_nie.taobao.montBell;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class MontbellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String taobeiTemplateFile = "in/montBell_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_baobei_%s.csv";
		String publishedBaobeiFile = "";

		MontbellClothesBaobeiProducer db = new MontbellClothesBaobeiProducer();
		db.setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				.setTaobeiTemplateFile(taobeiTemplateFile)
				.addScanCategory("8800") //
				.setPublishedBaobeiFile(publishedBaobeiFile).process();

		System.exit(0);
	}

}
