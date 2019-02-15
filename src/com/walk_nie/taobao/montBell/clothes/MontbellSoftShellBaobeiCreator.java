package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellSoftShellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellSoftShellBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("softshell_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellSoftShellBaobeiProducer db = new MontbellSoftShellBaobeiProducer();
		db
                .addScanCategory("22000") // ソフトシェル>ソフトシェルジャケット
                
                //.addScanCategory("61200") // トレッキングパンツ＜厚手＞
                //.addScanCategory("61100") // トレッキングパンツ＜中厚手＞
                //.addScanCategory("61000") // トレッキングパンツ＜薄手＞
                
                //.addScanCategory("63000") // リラックスパンツ
                
                .addScanCategory("25000") // ソフトシェル>ライトシェルジャケット/ベスト
                .addScanCategory("22500") // ソフトシェル>ソフトシェルパンツ
                .addScanCategory("23000") // ソフトシェル>ライトシェルパンツ
                
                .addScanCategory("") // 
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
