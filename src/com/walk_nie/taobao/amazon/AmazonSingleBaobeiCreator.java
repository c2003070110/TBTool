package com.walk_nie.taobao.amazon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.util.NieConfig;
/**
 */
public class AmazonSingleBaobeiCreator  {

	public static void main(String[] args) throws IOException {
		new AmazonSingleBaobeiCreator().process();
		System.exit(0);
	}
	public void process() throws IOException{
		File outFile = new File(NieConfig.getConfig("amazon.root.out.folder"), 
				String.format("baobei_%s.csv",
						DateUtils.formatDate(Calendar.getInstance().getTime(), 
								"yyyy_MM_dd_HH_mm_ss")));
		List<String> urls = Lists.newArrayList();
		File tempFile0 = new File(NieConfig.getConfig("amazon.root.folder"), "urls-in.txt");
		urls = Files.readLines(tempFile0, Charset.forName("UTF-8"));
		//
		double currencyRate = 0.060 + 0.005;
		double benefitRate = 0.08;
		AmazonSingleBaobeiProducer db = new AmazonSingleBaobeiProducer();
		db
                .addUrls(urls) //  
                
                .setOutputFile(outFile)
                .setCurrencyRate(currencyRate)
                .setBenefitRate(benefitRate)
                .process();

	}

}
