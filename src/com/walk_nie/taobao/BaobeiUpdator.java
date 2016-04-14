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
        String toUpdateFile = "in/updateBaobei.csv";
        List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(new File(toUpdateFile));

        String publishedBaobeiFile = "in/publishedBaobeiFile.csv";
        String outputFile = "out/earphone_baobei_%s.csv";
        
        EarphoneBaobeiProducer producer = new EarphoneBaobeiProducer();
        producer
            .setOutputFile(outputFile)
            .setPublishedBaobeiFile(publishedBaobeiFile)
            .setToUpdateBaobeiList(baobeiList)
            .enablePriceUpdate()
            .enableOutIdUpdate()
            .process();
        
	}

	 

}
