package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class SkuLister {
	// taobao workbench export file!
	private static String baobeiListFile = "./res/all.csv";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(new File(baobeiListFile));
		listSku(baobeiList);
	}
	private static void listSku(List<BaobeiPublishObject> baobeiList) throws IOException {
		for (BaobeiPublishObject baobei : baobeiList) {
			System.out.println(romoveDlbQuto(baobei.title) + "\t" + romoveDlbQuto(baobei.outer_id));
		}
	}

	private static String romoveDlbQuto(String str){
		String tmp = str.substring(1, str.length());
		tmp = tmp.substring(0, tmp.length()-1);
		return tmp;
	}

}
