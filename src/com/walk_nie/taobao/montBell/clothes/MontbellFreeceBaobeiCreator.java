package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellFreeceBaobeiCreator  {

	public static void main(String[] args) throws 
			IOException {
		new MontbellFreeceBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("freece_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montnell-1023.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//String publishedBaobeiFile = "";
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.06;
		MontbellFreeceBaobeiProducer db = new MontbellFreeceBaobeiProducer();
		db
                .addScanCategory("122000") // フリースジャケット FLEECE JACKET
                .addScanCategory("121000") // 防風性フリース
                .addScanCategory("126000") // フリースプルオーバー
                .addScanCategory("124000") // フリースベスト
                .addScanCategory("123000") // フリースパンツ
                .addScanCategory("129000") // フリーススカート
                .addScanCategory("") //
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
