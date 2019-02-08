package com.walk_nie.taobao.amazon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.walk_nie.taobao.object.StockObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;

public class AmazonGoodsPageParser extends BaseBaobeiParser {

	public List<AmazonGoodsObject> scanItemUrls(List<String> urls)
			throws IOException {
		List<AmazonGoodsObject> goodsList = new ArrayList<AmazonGoodsObject>();
		for (String url : urls) {
			AmazonGoodsObject obj = scanItemUrl(url);
			if(obj != null){
				goodsList.add(obj);
			}
		}
		return goodsList;
	}

	public AmazonGoodsObject scanItemUrl(String url) {
		AmazonGoodsObject obj = new AmazonGoodsObject();

		for (StockObject st : obj.stockList) {
			if (!StringUtil.isBlank(st.sizeName) && !obj.sizeNameList.contains(st.sizeName)) {
				obj.sizeNameList.add(st.sizeName);
			}
			if (!StringUtil.isBlank(st.colorName) && !obj.colorNameList.contains(st.colorName)) {
				obj.colorNameList.add(st.colorName);
			}
		}
		return obj;
	}

}
