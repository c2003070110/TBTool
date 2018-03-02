package com.walk_nie.taobao.edwin;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
/**
 * edwin
 *
 */
public class EdwinManBaobeiCreator  {
	double currencyRate = 0.060 + 0.005;
	double benefitRate = 0.05;

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new EdwinManBaobeiCreator().processEdwinMan();

		System.exit(0);
	}
	public void processEdwinMan() throws IOException{

		String outputFile = "out/edwin_man_baobei_%s.csv";
		//String publishedBaobeiFile = "c:/temp/edwin-all.csv";
		//File file = new File(publishedBaobeiFile);
		//List<BaobeiPublishObject> baobeiList = BaobeiUtil
		//		.readInPublishedBaobei(file);
		//
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db
		        .setBrandCd(EdwinUtil.brandCdEdwin)
		        .setSexVal(EdwinUtil.sex_val_man)
				.addScanSeriesName("1101")// INTERNATIONAL BASIC
				/*
				.addScanSeriesName("1102")// 503
				.addScanSeriesName("1120")// JERSEYS
				.addScanSeriesName("1130")// EDWIN E STANDARD
				.addScanSeriesName("1109")// XV
				.addScanSeriesName("1110")// WORK
				.addScanSeriesName("1114")// CHINO
				.addScanSeriesName("1121")// Vintage Collection
				.addScanSeriesName("1126")// DENIM is EDWIN
				.addScanSeriesName("1128")// EUROPE LINE
				.addScanSeriesName("1131")// SEN
				.addScanSeriesName("1123")// WILD FIRE
				.addScanSeriesName("1127")// AIR IN DENIM
				.addScanSeriesName("9992")// EDWIN その他
				*/
				//.addScanSeriesName("1299")// MissEDWIN その他
				//.addScanSeriesName("9999")// その他
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedbaobeiList(baobeiList)
                .process();
	}
	public void processEdwinWoman() throws IOException{

		String outputFile = "out/edwin_woman_baobei_%s.csv";
		//String publishedBaobeiFile = "c:/temp/edwin-all.csv";
		//File file = new File(publishedBaobeiFile);
		//List<BaobeiPublishObject> baobeiList = BaobeiUtil
		//		.readInPublishedBaobei(file);
		//
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db
		        .setBrandCd(EdwinUtil.brandCdEdwin)
		        .setSexVal(EdwinUtil.sex_val_woman)
		        /*
				.addScanSeriesName("1101")// INTERNATIONAL BASIC
				.addScanSeriesName("1120")// JERSEYS
				.addScanSeriesName("1130")// EDWIN E STANDARD
				.addScanSeriesName("1127")// AIR IN DENIM
				.addScanSeriesName("9992")// EDWIN その他
				.addScanSeriesName("1299")// MissEDWIN その他
				*/
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedbaobeiList(baobeiList)
                .process();
	}
	public void processLeeMan() throws IOException{

		String outputFile = "out/lee_man_baobei_%s.csv";
		//String publishedBaobeiFile = "c:/temp/edwin-all.csv";
		//File file = new File(publishedBaobeiFile);
		//List<BaobeiPublishObject> baobeiList = BaobeiUtil
		//		.readInPublishedBaobei(file);
		//
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db
		        .setBrandCd(EdwinUtil.brandCdLee)
		        .setSexVal(EdwinUtil.sex_val_man)
.addScanSeriesName("2102")//AMERICAN RIDERS
.addScanSeriesName("2104")//AMERICAN STANDARD
.addScanSeriesName("2106")//DUNGAREES
.addScanSeriesName("2108")//LEENS
.addScanSeriesName("2109")//ARCHIVES
.addScanSeriesName("2113")//CLASSICS
//.addScanSeriesName("2114")//JEGGERS
//.addScanSeriesName("2118")//HI-STANDARD
//.addScanSeriesName("2199")//Lee MEN'S その他
//.addScanSeriesName("2399")//Lee その他
//.addScanSeriesName("2123")//CHETOPA
//.addScanSeriesName("2130")//WINTER EDITION
//.addScanSeriesName("2119")//RIDERS ORIGINAL
.addScanSeriesName("2321")//Lee KID'S
//.addScanSeriesName("9999")//その他
.addScanSeriesName("2000")//Lee
//.addScanSeriesName("2125")//Riders LASTIC
.addScanSeriesName("2120")//Lee Mens TOPS
//.addScanSeriesName("2126")//101+
.addScanSeriesName("2320")//Lee KID'S TOPS

                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedbaobeiList(baobeiList)
                .process();
	}
	public void processLeeWoman() throws IOException{

		String outputFile = "out/lee_woman_baobei_%s.csv";
		//String publishedBaobeiFile = "c:/temp/edwin-all.csv";
		//File file = new File(publishedBaobeiFile);
		//List<BaobeiPublishObject> baobeiList = BaobeiUtil
		//		.readInPublishedBaobei(file);
		//
		EdwinManBaobeiProducer db = new EdwinManBaobeiProducer();
		db
		        .setBrandCd(EdwinUtil.brandCdLee)
		        .setSexVal(EdwinUtil.sex_val_woman)
.addScanSeriesName("2101")//HERITAGE EDITION
.addScanSeriesName("2103")//JEGGINGS
.addScanSeriesName("2106")//DUNGAREES
//.addScanSeriesName("2299")//Lee LADIES' その他
//.addScanSeriesName("2399")//Lee その他
.addScanSeriesName("2231")//HERITAGE LASTIC
.addScanSeriesName("2230")//HERITAGE ORIGINAL
.addScanSeriesName("2235")//SEASONAL EDITION
.addScanSeriesName("2220")//Lee Lady's TOPS
.addScanSeriesName("2232")//HERITAGE SEASON
//.addScanSeriesName("2321")//Lee KID'S
//.addScanSeriesName("9999")//その他
//.addScanSeriesName("2120")//Lee Mens TOPS
//.addScanSeriesName("2320")//Lee KID'S TOPS
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedbaobeiList(baobeiList)
                .process();
	}


}
