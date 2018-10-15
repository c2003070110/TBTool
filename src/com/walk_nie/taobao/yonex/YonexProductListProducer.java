package com.walk_nie.taobao.yonex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.walk_nie.taobao.support.BaseBaobeiParser;

public class YonexProductListProducer {
	// 0:all;1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
	private int categoryType = 0;    
	
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> itemIdList = ((YonexProductParser) getParser()).setCategoryType(categoryType)
					.scanItem();
			if (itemIdList.isEmpty())
				return;
			StringBuffer sbProduct = new StringBuffer();
			for (GoodsObject obj : itemIdList) {
				sbProduct.append(obj.title + "\t" + obj.kataban + "\n");
			}
			FileUtils.writeStringToFile(new File(YonexUtil.productListFile), sbProduct.toString(),Charset.forName("UTF-8"));
			System.out.println("-------- FINISH--------");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (priceBw != null)
				try {
					priceBw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	public BaseBaobeiParser getParser() {
		return new YonexProductParser();
	}

}
