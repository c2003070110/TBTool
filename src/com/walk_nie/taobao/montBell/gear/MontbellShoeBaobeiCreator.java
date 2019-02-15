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
public class MontbellShoeBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("shoe_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.006;
		double benefitRate = 0.05;
		MontbellShoeBaobeiProducer db = new MontbellShoeBaobeiProducer();
		db
                .addScanCategory("241000") //  登山靴（アルパイン）
                .addScanCategory("241100") //  登山靴（トレッキング）
                .addScanCategory("241200") //  登山靴（ハイキング）
                .addScanCategory("244500") //  スノーブーツ
                .addScanCategory("243000") //  スパッツ
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
