package com.walk_nie.taobao.yonex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.walk_nie.taobao.support.BaseBaobeiParser;

public class YonexProductListProducer {

	public static void main(String[] args) {
		YonexProductListProducer producer = new YonexProductListProducer();
		// 0:all;1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
		producer.process(0);
	}
	
	public void process(int categoryType) {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> itemIdList = ((YonexProductParser) getParser()).setCategoryType(categoryType)
					.scanItem();
			if (itemIdList.isEmpty())
				return;
			StringBuffer sbProduct = new StringBuffer();
			for (GoodsObject obj : itemIdList) {
				sbProduct.append(obj.categoryType + "\t" + obj.title + "\t" + obj.kataban + "\n");
				//sbProduct.append(obj.title + "\t" + obj.kataban  + "\tCOLOR=["+ toString(obj.colorList) + "]\tSIZE=[" +toString(obj.sizeList) +"]\n" );
			}
			File out = new File(YonexUtil.productListFile);
			FileUtils.writeStringToFile(out, sbProduct.toString(),Charset.forName("UTF-8"));
			System.out.println("Saved to File = " + out.getCanonicalPath());
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
	private String toString(List<String> list) {
		String str = "";
		for (String obj : list) {
			str += obj+" ";
		}
		return str;
	}
	public BaseBaobeiParser getParser() {
		return new YonexProductParser();
	}

}
