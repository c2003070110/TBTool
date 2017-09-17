package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * 化繊綿 棉服
 *
 */
public class MontbellSyntheticBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_synthetic_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-synthetic.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.06;
		MontbellSyntheticBaobeiProducer db = new MontbellSyntheticBaobeiProducer();
		db
                .addScanCategory("139000") //  化繊綿ジャケット
                .addScanCategory("139500") //  化繊綿ジャケット（軽量シリーズ）
                .addScanCategory("139700") //  化繊綿ベスト
                .addScanCategory("133000") //  化繊綿パンツ
                .addScanCategory("133500") //  化繊綿スカート
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
