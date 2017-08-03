package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellDownBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_down_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.061 + 0.004;
		double benefitRate = 0.05;
		MontbellDownBaobeiProducer db = new MontbellDownBaobeiProducer();
		db
                .addScanCategory("131000") // ダウンジャケット
                .addScanCategory("137000") // ダウンジャケット（軽量シリーズ）
                .addScanCategory("137500") // 半袖ダウンジャケット
                .addScanCategory("134000") // ダウンベスト
                .addScanCategory("136000") // コート（中綿入り）
                .addScanCategory("138000") // ダウンパンツ
                .addScanCategory("625000") //USモデル ダウンジャケット
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
