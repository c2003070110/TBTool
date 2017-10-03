package com.walk_nie.taobao.montBell.gear;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * ダウン 羽绒服
 *
 */
public class MontbellShoeBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_shoe_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellShoeBaobeiProducer db = new MontbellShoeBaobeiProducer();
		db
                .addScanCategory("241000") //  登山靴（アルパイン）
                .addScanCategory("241100") //  登山靴（トレッキング）
                .addScanCategory("241200") //  登山靴（ハイキング）
                .addScanCategory("244500") //  スノーブーツ
                .addScanCategory("243000") //  スパッツ
                .addScanCategory("") //  
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
