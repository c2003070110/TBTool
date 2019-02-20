package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * ダウン 羽绒服
 *
 */
public class MontbellDownBaobeiCreator  {

	public static void main(String[] args) throws 
			IOException {
		new MontbellDownBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("down_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
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
