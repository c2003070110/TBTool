package com.walk_nie.taobao.montBell;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

public class MontbellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_baobei_%s.csv";
		String publishedBaobeiFile = "";

		MontbellBaobeiProducer db = new MontbellBaobeiProducer();
		db 
				.setOutputFile(outputFile)
                .setPublishedBaobeiFile(publishedBaobeiFile)
                .addScanCategory("1000") // レインウェア
                .addScanCategory("8800") // レインウェア（ゴアテックス製）
                .addScanCategory("8000") // レインウェア（自転車用）
           
				.addScanCategory("22000") // ソフトシェルジャケット soft shell jacket
                .addScanCategory("25000") // ライトシェルジャケット/ベスト
                .addScanCategory("22500") // ソフトシェルパンツ
                .addScanCategory("23000") // ライトシェルパンツ
                
                .addScanCategory("11500") // 超軽量ウインドブレーカー
                .addScanCategory("11000") // ウインドブレーカー
                
                .addScanCategory("141000") // ハードシェル>ジャケット（保温材なし hard shell jacket
                .addScanCategory("142000") // ハードシェル>ジャケット（保温材入り）
                .addScanCategory("145000") // ハードシェル>パンツ(保温材なし)
                .addScanCategory("146000") // ハードシェル>パンツ(保温材入り)
                
                .addScanCategory("44000") // Tシャツ（半袖/長袖）>機能素材<ウイックロンクール>
                .addScanCategory("45500") // Tシャツ（半袖/長袖）>機能素材<ウイックロンZEO>
                .addScanCategory("46500") // Tシャツ（半袖/長袖）>天然素材<メリノウールプラス>
                
                .addScanCategory("97000") // アルパインソックス
                .addScanCategory("91000") // トレッキングソックス
                .addScanCategory("93000") // ウォーキングソックス
                
                .addScanCategory("122000") // フリースジャケット FLEECE JACKET
                .addScanCategory("121000") // 防風性フリース
                .addScanCategory("126000") // フリースプルオーバー
                .addScanCategory("124000") // フリースベスト
                .addScanCategory("123000") // フリースパンツ
                .addScanCategory("") // 
                .addScanCategory("") // 
              
                .process();

		System.exit(0);
	}

}
