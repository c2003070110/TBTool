package com.walk_nie.taobao.edwin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

/**
 * edwin
 * 
 */
public class EdwinManBaobeiCreator2 {
	double currencyRate = 0.061 + 0.005;
	double benefitRate = 0.05;

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		EdwinManBaobeiCreator2 createor = new EdwinManBaobeiCreator2();
		//createor.processEdwinMan();
		//createor.processEdwinWoman();
		//createor.processEdwinKids();
		createor.processLeeMan();
		createor.processLeeWoman();
		createor.processLeeKids();
		createor.processSomething();

		System.exit(0);
	}

	public void processEdwinMan() throws IOException {

		String outputFile = "out/edwin_man_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdEdwin)
		        .setSexVal(EdwinUtil.sex_val_man)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate)
				.process();
	}

	public void processEdwinWoman() throws IOException {

		String outputFile = "out/edwin_woman_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdEdwin)
				.setSexVal(EdwinUtil.sex_val_woman)
				.setOutputFile(outputFile)
				.setCurrencyRate(currencyRate).setBenefitRate(benefitRate)
				.process();
	}

	public void processEdwinKids() throws IOException {

		String outputFile = "out/edwin_kids_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdEdwin)
				.setSexVal(EdwinUtil.sex_val_kids)
				.setOutputFile(outputFile)
				.setCurrencyRate(currencyRate).setBenefitRate(benefitRate)
				.process();
	}

	public void processLeeMan() throws IOException {

		String outputFile = "out/lee_man_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdLee).setSexVal(EdwinUtil.sex_val_man)

		.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}

	public void processLeeWoman() throws IOException {

		String outputFile = "out/lee_woman_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdLee).setSexVal(EdwinUtil.sex_val_woman)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}

	public void processLeeKids() throws IOException {

		String outputFile = "out/lee_kids_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdLee).setSexVal(EdwinUtil.sex_val_kids)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}

	public void processSomething() throws IOException {

		String outputFile = "out/something_baobei_%s.csv";
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdSomething).setSexVal(EdwinUtil.sex_val_woman)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}


}
