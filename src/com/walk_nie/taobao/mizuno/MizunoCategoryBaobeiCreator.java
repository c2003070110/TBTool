package com.walk_nie.taobao.mizuno;

public class MizunoCategoryBaobeiCreator {

	public static void main(String[] args) {
		String url = "https://www.mizunoshop.net/f/dsg-660939";
		String outputFile = "out/mizuno_category_baobei_%s.csv";
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
