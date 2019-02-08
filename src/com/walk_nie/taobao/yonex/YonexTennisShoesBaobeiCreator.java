package com.walk_nie.taobao.yonex;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieConfig;

public class YonexTennisShoesBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String taobeiTemplateFile = "in/yonex_badmin_pad_baobeiTemplate.csv";
		//String miaoshuTemplateFile = "in/yonex_badmin_miaoshu_template.html";
		File outputFile = new File(NieConfig.getConfig("yonex.out.root.folder"), 
				String.format("tennis_shoes_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));

		YonexBaobeiProducer db = new YonexBaobeiProducer();
		db
				.setCategoryType(4)
				.setOutputFile(outputFile)
				.process();

		System.exit(0);
	}

}
