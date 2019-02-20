package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
/**
 * kids & baby
 *
 */
public class MontbellKidsBabyBaobeiCreator  {

	public static void main(String[] args) throws 
			IOException {
		new MontbellKidsBabyBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("hardshell_kidsbaby_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		//String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		//File file = new File(publishedBaobeiFile);
		//List<BaobeiPublishObject> baobeiList = BaobeiUtil
		//		.readInPublishedBaobei(file);
		//
		double currencyRate = 0.062 + 0.005;
		double benefitRate = 0.1;
		MontbellKidsBabyBaobeiProducer db = new MontbellKidsBabyBaobeiProducer();
		db
                .addScanCategory("159000") //スノースポーツ（キッズ）
                .addScanCategory("153000") // ダウン/化繊綿入り（キッズ）
                .addScanCategory("152000") // ジャケット/ベスト（キッズ）
                
                .addScanCategory("154000") // フリース（キッズ）
                //.addScanCategory("157000") // 帽子（キッズ）
                
                .addScanCategory("173700") // スノースポーツ（ベビー） 
                .addScanCategory("173000") //  ジャケット/ベスト（ベビー）
                .addScanCategory("171000") // 雨具（ベビー） 
                .addScanCategory("173500") //  フリース（ベビー）
                .addScanCategory("175000") //  パンツ（ベビー）
                //.addScanCategory("176000") // 帽子（ベビー）
                
                //.addScanCategory("342000") //  ブーツ/サンダル（子供用）
                
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                //.setPublishedbaobeiList(baobeiList)
                .process();
	}

}
