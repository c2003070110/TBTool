package com.walk_nie.taobao.edwin;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieConfig;

/**
 * edwin
 * 
 */
public class EdwinManBaobeiCreator2 {
	double currencyRate = 0.061 + 0.005;
	double benefitRate = 0.05;

	public static void main(String[] args) throws IOException {
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

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("edwin_man_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdEdwin)
		        .setSexVal(EdwinUtil.sex_val_man)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate)
				.process();
	}

	public void processEdwinWoman() throws IOException {

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("edwin_woman_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdEdwin)
				.setSexVal(EdwinUtil.sex_val_woman)
				.setOutputFile(outputFile)
				.setCurrencyRate(currencyRate).setBenefitRate(benefitRate)
				.process();
	}

	public void processEdwinKids() throws IOException {

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("edwin_kids_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdEdwin)
				.setSexVal(EdwinUtil.sex_val_kids)
				.setOutputFile(outputFile)
				.setCurrencyRate(currencyRate).setBenefitRate(benefitRate)
				.process();
	}

	public void processLeeMan() throws IOException {

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("lee_man_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdLee).setSexVal(EdwinUtil.sex_val_man)

		.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}

	public void processLeeWoman() throws IOException {

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("lee_woman_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdLee).setSexVal(EdwinUtil.sex_val_woman)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}

	public void processLeeKids() throws IOException {

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("lee_woman_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdLee).setSexVal(EdwinUtil.sex_val_kids)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}

	public void processSomething() throws IOException {

		File outputFile = new File(NieConfig.getConfig("edwin.out.root.folder"), 
				String.format("something_man_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db.setBrandCd(EdwinUtil.brandCdSomething).setSexVal(EdwinUtil.sex_val_woman)
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).process();
	}


}
