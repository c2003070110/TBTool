package com.walk_nie.taobao.mizuno;

public class MizunoSingleBaobeiCreator {

	public static void main(String[] args) {
		String url = "https://www.mizunoshop.net/f/dsg-660939";
		String outputFile = "out/mizuno_single_baobei_%s.csv";
		double currencyRate = 0.063 + 0.005;
		double benefitRate = 0.07;
		
		MizunoBaobeiProducer db = new MizunoBaobeiProducer();
		db.addProductUrl(url)
				.setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate)
				.setOutputFile(outputFile)
				.process();
	}
}
