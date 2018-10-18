package com.walk_nie.taobao.mizuno;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.mizuno.shoes.MinunoShoesParser;

public class MizunoBaobeiProducer extends MizunoBaseBaobeiProducer {
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> productObjs = Lists.newArrayList();
			
			if (!categoryUrlList.isEmpty()) {
				List<GoodsObject> productObjList = ((MinunoShoesParser) getParser())
						.parseProductByCategoryUrl(categoryUrlList);
				productObjs.addAll(productObjList);
			}
			
			if (!productUrlList.isEmpty()) {
				List<GoodsObject> productObjList = ((MinunoShoesParser) getParser())
						.parseProductByProductUrl(productUrlList);
				productObjs.addAll(productObjList);
			}
			
			writeOut(priceBw,productObjs);
		
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

	private List<String> productUrlList = Lists.newArrayList();
	public MizunoBaobeiProducer addProductUrl(String productUrl) {
		productUrlList.add(productUrl);
		return this;
	}

	private List<String> categoryUrlList = Lists.newArrayList();
	public MizunoBaobeiProducer addCategoryUrl(String categoryUrl) {
		categoryUrlList.add(categoryUrl);
		return this;
	}

}
