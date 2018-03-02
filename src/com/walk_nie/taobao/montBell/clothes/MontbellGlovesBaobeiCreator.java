package com.walk_nie.taobao.montBell.clothes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellGlovesBaobeiCreator  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		new MontbellGlovesBaobeiCreator().process();

		System.exit(0);
	}
	public void process() throws IOException{

		//String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
		String outputFile = "out/montBell_gloves_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-gloves.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.08;
		MontbellGlovesBaobeiProducer db = new MontbellGlovesBaobeiProducer();
		db
                //.addScanCategory("85000") // ウールグローブ
                .addScanCategory("81000") // 防水グローブ
                .addScanCategory("82000") // 防滴グローブ
                .addScanCategory("87000") // 防風グローブ
                .addScanCategory("1108763") // ダウンミトン
                .addScanCategory("88750") // アイスクライミンググローブ
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
