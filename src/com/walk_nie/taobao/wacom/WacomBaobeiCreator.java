package com.walk_nie.taobao.wacom;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.util.NieConfig;

/**
 *
 */
public class WacomBaobeiCreator {

	public static void main(String[] args) throws IOException {
		new WacomBaobeiCreator().process();

		System.exit(0);
	}

	public void process() throws IOException {

		File outputFile = new File(NieConfig.getConfig("wacom.work.root.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		File file = new File(NieConfig.getConfig("wacom.taobao.pulishedbaibei.filepath"));
		List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(file);
		//
		//double currencyRate = 0.060 + 0.005;
		//double benefitRate = 0.15;
		WacomBaobeiProducer db = new WacomBaobeiProducer();
		db.addUrlList(readProductUrls())
				//.setCurrencyRate(currencyRate)
				//.setBenefitRate(benefitRate)
				.setOutputFile(outputFile)
				.setPublishedbaobeiList(baobeiList)
				.process();
	}

	private List<String> readProductUrls() throws IOException {
		String scanFile = NieConfig.getConfig("wacom.scan.url.file");

		List<String> adrs = Files.readLines(new File(scanFile), Charset.forName("UTF-8"));
		List<String> outputList = Lists.newArrayList();
		for (String line : adrs) {
			if ("".equals(line)) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			outputList.add(line);
		}
		return outputList;
	}

}
