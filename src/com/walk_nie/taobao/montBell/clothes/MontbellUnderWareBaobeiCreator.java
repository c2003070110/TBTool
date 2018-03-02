package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;


public class MontbellUnderWareBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellUnderWareBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_underware_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.1;
		MontbellUnderwareBaobeiProducer db = new MontbellUnderwareBaobeiProducer();
		db
                .addScanCategory("75200") // スーパーメリノウール（厚手／エクスペディション）
                
                .addScanCategory("75100") // スーパーメリノウール（中厚手／ミドルウエイト）
                .addScanCategory("75000") // スーパーメリノウール（薄手／ライトウエイト）
                .addScanCategory("71200") // ジオライン（厚手／エクスペディション）
                .addScanCategory("71100") // ジオライン（中厚手／ミドルウエイト）
                .addScanCategory("71000") // ジオライン（薄手／ライトウエイト）
                .addScanCategory("73000") // ジオライン（薄手／クールメッシュ）
                .addScanCategory("78000") // ジオライン（薄手／シェイプ）
                .addScanCategory("76000") // スペリオルシルク（薄手）
                
                .addScanCategory("71700") // ウエストウォーマー
                
/*
                .addScanCategory("71300") //  アンダーシャツ
                .addScanCategory("71400") //  タイツ
                .addScanCategory("71600") // トランクス・ブリーフ
                .addScanCategory("71700") // ウエストウォーマー
                .addScanCategory("71800") // タンクトップ・キャミトップ
                .addScanCategory("78500") // スポーツブラ
                .addScanCategory("71500") // ショーツ
*/                .addScanCategory("") // 
                .addScanCategory("") // 
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
