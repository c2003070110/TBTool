package com.walk_nie.taobao.montBell.clothes;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class MontbellFreeceBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_freece_baobei_%s.csv";
		//String publishedBaobeiFile = "";
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.1;
		MontbellFreeceBaobeiProducer db = new MontbellFreeceBaobeiProducer();
		db
                .addScanCategory("122000") // フリースジャケット FLEECE JACKET
                .addScanCategory("121000") // 防風性フリース
                .addScanCategory("126000") // フリースプルオーバー
                .addScanCategory("124000") // フリースベスト
                .addScanCategory("123000") // フリースパンツ
                .addScanCategory("129000") // フリーススカート
                .addScanCategory("") //
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedBaobeiFile(publishedBaobeiFile)
                .process();

		System.exit(0);
	}

}
