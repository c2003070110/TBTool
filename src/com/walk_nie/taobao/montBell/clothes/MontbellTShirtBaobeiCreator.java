package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellTShirtBaobeiCreator  {

	public static void main(String[] args) throws 
			IOException {
		new MontbellTShirtBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("tshirt_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellTShirtBaobeiProducer db = new MontbellTShirtBaobeiProducer();
		db
		        // Tシャツ（半袖/長袖）
        		.addScanCategory("46500") // 天然素材<メリノウールプラス>
        		.addScanCategory("45550") // 機能素材<ウイックロンZEOサーマル>
                .addScanCategory("45500") // 機能素材<ウイックロンZEO>
                .addScanCategory("42000") // 機能素材<ウイックロン>
                .addScanCategory("44000") // 機能素材<ウイックロンクール>
                .addScanCategory("46000") // cotton
                
                // シャツ
                .addScanCategory("32300") // 長袖シャツ＜厚手＞
                .addScanCategory("32200") // 長袖シャツ＜中厚手＞
                .addScanCategory("32100") // 長袖シャツ＜薄手＞
                .addScanCategory("31200") // 半袖シャツ＜中厚手＞
                .addScanCategory("31100") // 半袖シャツ＜薄手＞
                .addScanCategory("31500") // スウェットシャツ＆パーカ
                .addScanCategory("31400") // 作務衣
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
