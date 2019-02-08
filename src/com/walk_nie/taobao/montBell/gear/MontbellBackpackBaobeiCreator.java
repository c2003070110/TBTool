package com.walk_nie.taobao.montBell.gear;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
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
		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("hardshell_backpack_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.006;
		double benefitRate = 0.07;
		MontbellBackpackBaobeiProducer db = new MontbellBackpackBaobeiProducer();
		db
		//--ザック
                .addScanCategory("263000") // 小型ザック（5～25L）
                .addScanCategory("262000") // 中型ザック（30～45L）
                .addScanCategory("261000") // 大型ザック（50～120L）
                .addScanCategory("263500") // ザック（ポケッタブル）
                //.addScanCategory("265000") // キャメルバック
                
                .addScanCategory("264000") // ザックカバー
                
                //-- バッグ
                .addScanCategory("275000") //  トートバッグ
                .addScanCategory("283000") //  バッグ（ポケッタブル）
                .addScanCategory("282000") //  ショルダーバッグ
                .addScanCategory("281000") //  ウエストバッグ
                .addScanCategory("274000") //  ポーチ
                .addScanCategory("274100") // 財布 
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
