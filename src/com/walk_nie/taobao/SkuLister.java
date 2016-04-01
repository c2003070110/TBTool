package com.walk_nie.taobao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class SkuLister {
	// taobao workbench export file!
	private static String baobeiListFile = "E:/temp/erji_published_20160226.csv";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		List<BaobeiPublishObject> baobeiList = readIn();
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

	private static List<BaobeiPublishObject> readIn() throws IOException {

		List<BaobeiPublishObject> baobeiList = new ArrayList<BaobeiPublishObject>();
		BufferedReader br = null;
		try {
			File file = new File(baobeiListFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					BaobeiPublishObject obj = TaobaoUtil.readBaobeiIn(str);
					if(obj != null){
						baobeiList.add(obj);
					}
				}
			}
		} finally {
			if (br != null)
				br.close();
		}

		return baobeiList;
	}
}
