package com.walk_nie.taobao.montBell;

import java.util.List;

import com.beust.jcommander.internal.Lists;

public class MontbellPriceMain {
	double todayCurrencyRate = 0.064;
	double currencyRate = todayCurrencyRate + 0.003;
	double benefitRate1 = 0.0;
	double benefitRate2 = 0.05;
	double benefitRate3 = 0.1;
	double benefitRate4 = 0.15;
	double benefitRate5 = 0.2;

	public static void main(String[] args) throws Exception {
		new MontbellPriceMain().process();
	}

	public void process() throws Exception {

		List<GoodsObject> goodsObjList = Lists.newArrayList();
		CategoryObject category = new CategoryObject();
		category.categoryId = "131000";
		String cateogryUrl = MontBellUtil.categoryUrlPrefix
				+ category.categoryId;
		new MontbellProductParser().scanItemByCategory(goodsObjList, category,
				cateogryUrl);
		// new
		// MontbellProductParser().scanItemByCategory(goodsObjList,category);
		String outputFmt = "%s,%s,%s,%s,%s,%s";
		for (GoodsObject goodObj : goodsObjList) {
			String price1 = MontBellUtil.convertToCNY(goodObj, currencyRate,
					benefitRate1);
			String price2 = MontBellUtil.convertToCNY(goodObj, currencyRate,
					benefitRate2);
			String price3 = MontBellUtil.convertToCNY(goodObj, currencyRate,
					benefitRate3);
			String price4 = MontBellUtil.convertToCNY(goodObj, currencyRate,
					benefitRate4);
			String price5 = MontBellUtil.convertToCNY(goodObj, currencyRate,
					benefitRate5);
			System.out.println(String.format(outputFmt, goodObj.productId,
					price1, price2, price3, price4, price5));
		}
		System.out.println("-------- FINISH--------");
	}

}
