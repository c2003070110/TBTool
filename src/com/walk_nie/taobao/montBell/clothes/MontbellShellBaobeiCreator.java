package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellShellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_shell_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellShellBaobeiProducer db = new MontbellShellBaobeiProducer();
		db
                .addScanCategory("142000") // ハードシェル>ジャケット（保温材入り）
                .addScanCategory("141000") // ハードシェル>ジャケット（保温材なし）
                .addScanCategory("146000") // ハードシェル>パンツ(保温材入り)
                .addScanCategory("145000") // ハードシェル>パンツ(保温材なし)
                
                .addScanCategory("22000") // ソフトシェル>ソフトシェルジャケット
                
                .addScanCategory("61200") // トレッキングパンツ＜厚手＞
                .addScanCategory("61100") // トレッキングパンツ＜中厚手＞
                .addScanCategory("61000") // トレッキングパンツ＜薄手＞
                
                .addScanCategory("63000") // リラックスパンツ
                
                .addScanCategory("25000") // ソフトシェル>ライトシェルジャケット/ベスト
                .addScanCategory("22500") // ソフトシェル>ソフトシェルパンツ
                .addScanCategory("23000") // ソフトシェル>ライトシェルパンツ
                
                .addScanCategory("8800") // レインウェア（ゴアテックス製）
                .addScanCategory("1000") // レインウェア
                .addScanCategory("2000") // レインコート
                
                .addScanCategory("8000") // レインウェア（自転車用）
                .addScanCategory("8700") // レインウェア（釣り用）
                .addScanCategory("8500") // レインウェア（バイク用）
                .addScanCategory("8600") // レインウェア（農作業用）
                
                .addScanCategory("9000") // レインポンチョ
                .addScanCategory("4000") // 傘
                .addScanCategory("3000") // レインキャップ/ハット
                
                .addScanCategory("") // 
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
