package com.walk_nie.taobao.montBell.gear;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * 背包
 *
 */
public class MontbellBackpackBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_backpack_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellBackpackBaobeiProducer db = new MontbellBackpackBaobeiProducer();
		db
                .addScanCategory("263000") // 小型ザック（5～25L）
                
                .addScanCategory("262000") // 中型ザック（30～45L）
                .addScanCategory("261000") // 大型ザック（50～120L）
                .addScanCategory("263500") // ザック（ポケッタブル）
                //.addScanCategory("265000") // キャメルバック
                
                .addScanCategory("264000") // ザックカバー
                .addScanCategory("") //  
                .addScanCategory("") //  
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
