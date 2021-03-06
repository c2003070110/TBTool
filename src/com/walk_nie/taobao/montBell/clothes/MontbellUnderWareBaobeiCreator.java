package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;


public class MontbellUnderWareBaobeiCreator  {

	public static void main(String[] args) throws 
			IOException {
		new MontbellUnderWareBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("underware_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
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
                .addScanCategory("") // zeoline shape
                .addScanCategory("") // zeoline cool mesh
                
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
