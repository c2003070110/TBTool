package com.walk_nie.taobao.montBell;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.WebDriver;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellBaobeiPriceAnor  {
	private static String taobaoUrl = "https://s.taobao.com/search?imgfile=&commend=all&ssid=s5-e&search_type=item&sourceId=tb.index&ie=utf8&q=%s";

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		String outputFile = "out/montBell_underware_baobei_%s.csv";
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		for (BaobeiPublishObject baobeiObj : baobeiList) {
			String publisedProductId = "";
			String outer_id = baobeiObj.outer_id.replace("\"", "");
			if (outer_id.startsWith("MTBL_")) {
				String[] split = outer_id.split("-");
				publisedProductId = split[split.length - 1];
			} else {
				publisedProductId = outer_id;
			}
			String searchUrl = String.format(taobaoUrl,publisedProductId);
			WebDriver webDriver = WebDriverUtil.getWebDriver(searchUrl);
		}
		//
		double currencyRate = 0.061 + 0.004;
		double benefitRate = 0.08;
		MontbellUnderwareBaobeiProducer db = new MontbellUnderwareBaobeiProducer();
		db
//                .addScanCategory("71300") //  アンダーシャツ
//                .addScanCategory("71400") //  タイツ
                .addScanCategory("75200") // スーパーメリノウール（厚手／エクスペディション）
                .addScanCategory("75100") // スーパーメリノウール（中厚手／ミドルウエイト）
                .addScanCategory("75000") // スーパーメリノウール（薄手／ライトウエイト）
                .addScanCategory("71200") // ジオライン（厚手／エクスペディション）
                .addScanCategory("71100") // ジオライン（中厚手／ミドルウエイト）
                .addScanCategory("71000") // ジオライン（薄手／ライトウエイト）
                .addScanCategory("") // 
                .addScanCategory("") // 
                .addScanCategory("") // 
                .addScanCategory("") // 
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
