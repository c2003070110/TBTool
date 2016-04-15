package com.walk_nie.taobao.kakaku.explore.earphone.amp;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.kakaku.AbstractMainListItemId;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.kakaku.explore.earphone.EarphoneUtil;

public class MainListItemId extends AbstractMainListItemId {

	public static void main(String[] args) {
		String exploreUrlFile = "in/earphone_amp_explore_urls.txt";
		String publishedFile = "in/earphone_amp_publishedBaobei.csv";
		String outputFile = "out/earphone_amp_item_%s.txt";
		
		new MainListItemId().setExploreUrlFile(exploreUrlFile)
				.setOutputFile(outputFile).setPublishedFile(publishedFile)
				.setUpdate(false).process();
	}

	@Override
	protected String translateType(String type) {
		return EarphoneUtil.translateType(type);
	}

	@Override
	protected String translateMaker(String maker) {
		return KakakuUtil.translateMaker(maker);
	}
	@Override
	protected boolean isAllowToParse(KakakuObject obj)
			throws ClientProtocolException, IOException {
		return true;
	}

}
