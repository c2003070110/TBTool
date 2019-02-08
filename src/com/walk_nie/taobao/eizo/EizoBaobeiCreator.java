package com.walk_nie.taobao.eizo;

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
 *
 */
public class EizoBaobeiCreator {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		new EizoBaobeiCreator().process();

		System.exit(0);
	}

	public void process() throws IOException {

		File outputFile = new File(NieConfig.getConfig("eizo.out.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		File file = new File(NieConfig.getConfig("eizo.taobao.pulishedbaibei.filepath"));
		List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(file);
		//
		//double currencyRate = 0.060 + 0.005;
		//double benefitRate = 0.15;
		EizoBaobeiProducer db = new EizoBaobeiProducer();
		db.setOutputFile(outputFile)
				//.setCurrencyRate(currencyRate)
				//.setBenefitRate(benefitRate)
				.setPublishedbaobeiList(baobeiList)
				.process();
	}

}
