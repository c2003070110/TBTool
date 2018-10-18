package com.walk_nie.taobao.righton;

public class RightonSingleBaobeiCreator {

	public static void main(String[] args) {
		String url = "https://right-on.co.jp/product/1000112001014?index=2";
		String outputFile = "out/mizuno_ringhton_baobei_%s.csv";
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
