package com.walk_nie.taobao.righton;

import java.io.File;
import java.util.Calendar;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieConfig;

public class RightonSingleBaobeiCreator {

	public static void main(String[] args) {
		String url = "https://right-on.co.jp/product/1000112001014?index=2";
		File outputFile = new File(NieConfig.getConfig("righton.out.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		double currencyRate = 0.063 + 0.005;
		double benefitRate = 0.07;
		
		RightonBaobeiProducer db = new RightonBaobeiProducer();
		db.addProductUrl(url)
				.setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate)
				.setOutputFile(outputFile)
				.process();
	}
}
