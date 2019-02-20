package com.walk_nie.taobao.kakaku.explore.shaver;

import java.io.IOException;

import com.walk_nie.taobao.kakaku.AbstractMainListItemId;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;

public class MainListItemId extends AbstractMainListItemId {

	public static void main(String[] args) {

		String exploreUrlFile = "in/earphone_scan_urls.txt";

		String publishedFile = "in/shaver_publishedBaobei.csv";

		String outputFile = "out/shaver_item_%s.txt";
		new MainListItemId().setExploreUrlFile(exploreUrlFile)
				.setOutputFile(outputFile).setPublishedFile(publishedFile)
				.setUpdate(false).process();
	}

	@Override
	protected String translateType(String type) {
		return ShaverUtil.translateType(type);
	}

	@Override
	protected String translateMaker(String maker) {
		return KakakuUtil.translateMaker(maker);
	}
	@Override
	protected boolean isAllowToParse(KakakuObject obj)
			throws  IOException {
		return true;
	}

}
