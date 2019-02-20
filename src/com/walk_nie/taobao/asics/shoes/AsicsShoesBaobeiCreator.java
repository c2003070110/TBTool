package com.walk_nie.taobao.asics.shoes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieConfig;

public class AsicsShoesBaobeiCreator  {

	public static void main(String[] args) throws IOException {

		//String taobeiTemplateFile = "in/asicsShoes_baobeiTemplate.csv";
		//String miaoshuTemplateFile = "in/asicsShoes_miaoshu_template.html";
		File outputFile = new File(NieConfig.getConfig("asics.out.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));

		AsicsShoesBaobeiProducer db = new AsicsShoesBaobeiProducer();
		db
				.setOutputFile(outputFile)
                .process();

		System.exit(0);
	}

}
