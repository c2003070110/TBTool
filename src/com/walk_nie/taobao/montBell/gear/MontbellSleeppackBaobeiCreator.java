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
 * ダウン 羽绒服
 *
 */
public class MontbellSleeppackBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("hardshell_sleeppack_%s.csv",
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
		MontbellSleeppackBaobeiProducer db = new MontbellSleeppackBaobeiProducer();
		db
                .addScanCategory("235500") //  ダウンモデル
                .addScanCategory("235600") //  化繊綿（エクセロフト）モデル
                
                .addScanCategory("223500") // ダウンハガー900 
                .addScanCategory("222500") //  ダウンハガー800
                .addScanCategory("236000") //  アルパイン ダウンハガー800
                .addScanCategory("221500") //  ダウンハガー650
                .addScanCategory("220500") //  アルパインダウンハガー650
                
                .addScanCategory("") //  
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

		System.exit(0);
	}

}
