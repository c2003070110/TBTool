package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.walk_nie.taobao.kakaku.explore.earphone.EarphoneBaobeiProducer;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.yonex.badminton.YonexBaobeiProducer;


public class BaobeiUpdator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
	    //updateEarphone();
	    updateYonex();
	}
	
	protected static void updateEarphone() throws IOException{
        double currencyRate = 0.0605 + 0.003;
	    double benefitRate = 0.1;
        String toUpdateFile = "res/updateBaobei.csv";
        List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(new File(toUpdateFile));

        String outputFile = "out/earphone_baobei-upd_%s.csv";
        
        EarphoneBaobeiProducer producer = new EarphoneBaobeiProducer();
        producer
            .setToUpdateBaobeiList(baobeiList)
            .enablePriceUpdate()
            .enableOutIdUpdate()
            .enableDescriptionUpdate()
            .setBenefitRate(benefitRate)
            .setCurrencyRate(currencyRate)
            .setOutputFile(outputFile)
            .process();
        
	}
	protected static void updateYonex() throws IOException{
       
        String toUpdateFile = "res/updateBaobei-Yonex.csv";
        List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(new File(toUpdateFile));

        String outputFile = "out/yonex_baobei-upd_%s.csv";
        
        YonexBaobeiProducer producer = new YonexBaobeiProducer();
        producer
            .setToUpdateBaobeiList(baobeiList)
            
            .enablePriceUpdate()
            .enableOutIdUpdate()
            .enableDescriptionUpdate()
            .setOutputFile(outputFile)
            .process();
		
	}

	 

}
