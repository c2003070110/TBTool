package com.walk_nie.taobao.kakaku.explore.earphone;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;

import com.walk_nie.taobao.kakaku.AbstractMainListItemId;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;

public class MainListItemId  extends AbstractMainListItemId {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		 String exploreUrlFile = "in/earphone_explore_urls.txt";
		 String publishedFile = "in/earphone_publishedBaobei.csv";
		 String outputFile = "out/earphone_item_%s.txt";
		 
		new MainListItemId().setExploreUrlFile(exploreUrlFile)
		.setOutputFile(outputFile).setPublishedFile(publishedFile)
		.setUpdate(false).process();
	}

	@Override
	protected String translateType(String type) {
		return com.walk_nie.taobao.kakaku.explore.earphone.EarphoneUtil.translateType(type);
	}

	@Override
	protected String translateMaker(String maker) {
		return KakakuUtil.translateMaker(maker);
	}
	
	@Override
	protected boolean isAllowToParse(KakakuObject obj) throws ClientProtocolException, IOException {
		String itemUrl = KakakuUtil.kakakuUrlPrefix + obj.id;
		Document doc = KakakuUtil.urlToDocumentKakaku(itemUrl);
		KakakuUtil.parseItemPrice(doc, obj);
		if(obj.priceYodobashi == null){
			return false;
		}
//		double d = (obj.priceYodobashi.price - obj.priceYodobashi.price * 0.1);
//		if(d >  obj.priceMin.price){
//			return false;
//		}
		return true;
	}
 
}
