package com.walk_nie.taobao.kakaku.explore.cameraflash;

import java.io.IOException;


import com.walk_nie.taobao.kakaku.AbstractMainListItemId;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.kakaku.explore.earphone.EarphoneUtil;

public class MainListItemId extends AbstractMainListItemId {
 
	public static void main(String[] args) {
		String exploreUrlFile = "out/cameraflash_explore_urls.txt";
		String outputFile = "out/cameraflash_item_%s.txt";
		String publishedFile = "in/cameraflash_amp_publishedBaobei.csv";
		
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
			throws IOException {
		return true;
	}
}
