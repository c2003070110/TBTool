package com.walk_nie.taobao.montBell.gear;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellSandalsBaobeiCreator {

	public static void main(String[] args) throws 
			IOException {
		new MontbellSandalsBaobeiCreator().process();
	}

	public void process() throws IOException {

		File outputFile = new File(MontBellUtil.rootPathName, 
				String.format("sandals_baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		String publishedBaobeiFile = "c:/temp/montbell-all.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.05;
		MontbellSandalsBaobeiProducer db = new MontbellSandalsBaobeiProducer();
		db.addScanCategory("242000")
				// サンダル
				.setOutputFile(outputFile).setCurrencyRate(currencyRate)
				.setBenefitRate(benefitRate).setPublishedbaobeiList(baobeiList)
				.process();

	}

}
