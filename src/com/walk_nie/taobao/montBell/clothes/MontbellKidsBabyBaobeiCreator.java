package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * kids & baby
 *
 */
public class MontbellKidsBabyBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_kidsbaby_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.010;
		double benefitRate = 0.00;
		MontbellKidsBabyBaobeiProducer db = new MontbellKidsBabyBaobeiProducer();
		db
                .addScanCategory("159000") //スノースポーツ（キッズ）
                .addScanCategory("153000") // ダウン/化繊綿入り（キッズ）
                .addScanCategory("152000") // ジャケット/ベスト（キッズ）
                
                .addScanCategory("154000") // フリース（キッズ）
                //.addScanCategory("157000") // 帽子（キッズ）
                
                .addScanCategory("173700") // スノースポーツ（ベビー） 
                .addScanCategory("173000") //  ジャケット/ベスト（ベビー）
                .addScanCategory("171000") // 雨具（ベビー） 
                .addScanCategory("173500") //  フリース（ベビー）
                .addScanCategory("175000") //  パンツ（ベビー）
                //.addScanCategory("176000") // 帽子（ベビー）
                
                //.addScanCategory("342000") //  ブーツ/サンダル（子供用）
                
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
