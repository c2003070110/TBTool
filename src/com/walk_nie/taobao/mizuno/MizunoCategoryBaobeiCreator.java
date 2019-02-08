package com.walk_nie.taobao.mizuno;

import java.io.File;
import java.util.Calendar;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieConfig;

public class MizunoCategoryBaobeiCreator {

	public static void main(String[] args) {
		String url = "https://www.mizunoshop.net/f/dsg-660939";
		File outputFile = new File(NieConfig.getConfig("mizuno.out.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		double currencyRate = 0.062 + 0.003;
		double benefitRate = 0.08;
		int weight = 500;
		
		MizunoBaobeiProducer db = new MizunoBaobeiProducer();
		db.addCategoryUrl(url)
		        .setWeight(weight)
				.setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate)
				.setOutputFile(outputFile)
				.process();
	}
}
