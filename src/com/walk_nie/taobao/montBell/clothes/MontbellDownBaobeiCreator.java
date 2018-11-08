package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * ダウン 羽绒服
 *
 */
public class MontbellDownBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellDownBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_down_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montnell-1023.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellDownBaobeiProducer db = new MontbellDownBaobeiProducer();
		db
                .addScanCategory("131000") // ダウンジャケット
                
                .addScanCategory("137000") // ダウンジャケット（軽量シリーズ）
                .addScanCategory("137500") // 半袖ダウンジャケット
                .addScanCategory("134000") // ダウンベスト
                .addScanCategory("136000") // コート（中綿入り）
                .addScanCategory("138000") // ダウンパンツ
                .addScanCategory("136500") //  ダウンはんてん（半纏）
                .addScanCategory("132000") //  ダウン（極地用）
                
                .addScanCategory("625000") //USモデル ダウンジャケット
                
                .addScanCategory("138600") //  ダウンマフラー/ブランケット
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
