package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellHardShellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_hardshell_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellHardShellBaobeiProducer db = new MontbellHardShellBaobeiProducer();
		db
                .addScanCategory("142000") // ハードシェル>ジャケット（保温材入り）
                .addScanCategory("141000") // ハードシェル>ジャケット（保温材なし）
                .addScanCategory("146000") // ハードシェル>パンツ(保温材入り)
                .addScanCategory("145000") // ハードシェル>パンツ(保温材なし)
                
                
                .addScanCategory("61200") // トレッキングパンツ＜厚手＞
                .addScanCategory("61100") // トレッキングパンツ＜中厚手＞
                .addScanCategory("61000") // トレッキングパンツ＜薄手＞
                
                .addScanCategory("63000") // リラックスパンツ
                
                .addScanCategory("") // 
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
