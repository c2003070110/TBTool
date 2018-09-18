package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
/**
 * 水杯
 *
 */
public class MontbellCapHatBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellCapHatBaobeiCreator().process();
		System.exit(0);
	}
	public void process() throws IOException{
		String outputFile = "out/montBell_caphat_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//baobeiList.clear();
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.08;
		MontbellCapHatBaobeiProducer db = new MontbellCapHatBaobeiProducer();
		db
                .addScanCategory("103000") //  防寒キャップ/ハット/耳あて
                .addScanCategory("102000") // ハット 
                .addScanCategory("112100") //  ハンチング/キャスケット/ワークキャップ
                .addScanCategory("101000") //  キャップ
                .addScanCategory("112000") //  レインキャップ/ハット
                .addScanCategory("1151100") //  バラクラバ
                .addScanCategory("") //  
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();

	}

}
