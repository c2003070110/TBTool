package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.walk_nie.taobao.kakaku.explore.earphone.EarphoneBaobeiProducer;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;


public class BaobeiUpdator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	    updateEarphone();
	}
	
	private static void updateEarphone() throws IOException{
        double currencyRate = 0.0595 + 0.003;
	    double benefitRate = 0.1;
        String toUpdateFile = "res/updateBaobei.csv";
        List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(new File(toUpdateFile));

        String outputFile = "out/earphone_baobei-upd_%s.csv";
        
        EarphoneBaobeiProducer producer = new EarphoneBaobeiProducer();
        producer
            .setToUpdateBaobeiList(baobeiList)
            .enablePriceUpdate()
            .enableOutIdUpdate()
            .setBenefitRate(benefitRate)
            .setCurrencyRate(currencyRate)
            .setOutputFile(outputFile)
            .process();
        
	}

	 

}
