package com.walk_nie.taobao.akasugu;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieConfig;

public class AkasuguBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String taobeiTemplateFile = "in/akasugu_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/akasugu_miaoshu_template.html";
		File outputFile = new File(NieConfig.getConfig("akasugu.out.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		//String publishedBaobeiFile = "";

		AkasuguBaobeiProducer db = new AkasuguBaobeiProducer();
		db.setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				//.setTaobeiTemplateFile(taobeiTemplateFile)
				//.setPublishedBaobeiFile(publishedBaobeiFile)
				.process();

		System.exit(0);
	}

}
