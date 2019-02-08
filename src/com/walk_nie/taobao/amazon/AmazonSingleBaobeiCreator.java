package com.walk_nie.taobao.amazon;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.util.NieConfig;
/**
 */
public class AmazonSingleBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new AmazonSingleBaobeiCreator().process();
		System.exit(0);
	}
	public void process() throws IOException{
		File outFile = new File(NieConfig.getConfig("amazon.out.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.08;
		AmazonSingleBaobeiProducer db = new AmazonSingleBaobeiProducer();
		db
                .addScanCategory("103000") //  防寒キャップ/ハット/耳あて
                .addScanCategory("102000") // ハット 
                .addScanCategory("112100") //  ハンチング/キャスケット/ワークキャップ
                .addScanCategory("101000") //  キャップ
                .addScanCategory("112000") //  レインキャップ/ハット
                .addScanCategory("1151100") //  バラクラバ
                .addScanCategory("") //  
                
                .setOutputFile(outFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .process();

	}

}
