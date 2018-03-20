package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellTShirtBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellTShirtBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_tshirt_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.062 + 0.004;
		double benefitRate = 0.08;
		MontbellTShirtBaobeiProducer db = new MontbellTShirtBaobeiProducer();
		db
        		.addScanCategory("46500") // 天然素材<メリノウールプラス>
        		.addScanCategory("45550") // 機能素材<ウイックロンZEOサーマル>
                .addScanCategory("44000") // 機能素材<ウイックロンクール>
                .addScanCategory("45500") // 機能素材<ウイックロンZEO>
                .addScanCategory("42000") // 機能素材<ウイックロン>
                
                .addScanCategory("32200") // 長袖シャツ＜中厚手＞
                .addScanCategory("32100") // 長袖シャツ＜薄手＞
                .addScanCategory("31500") // スウェットシャツ＆パーカ
                .addScanCategory("31400") // 作務衣
                .addScanCategory("31200") // 3半袖シャツ＜中厚手＞
                .addScanCategory("31100") // 半袖シャツ＜薄手＞
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
