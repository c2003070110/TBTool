package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellWindShellBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellWindShellBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		String outputFile = "out/montBell_windshell_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellWindShellBaobeiProducer db = new MontbellWindShellBaobeiProducer();
		db
                .addScanCategory("11500") // 超軽量ウインドブレーカー
                .addScanCategory("11000") // ウインドブレーカー
                .addScanCategory("") // 
                
                .setOutputFile(outputFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .setPublishedbaobeiList(baobeiList)
                .process();
	}

}
