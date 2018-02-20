package com.walk_nie.taobao.edwin;

public class EdwinMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String url = "http://edwin-ec.jp/disp/CSfGoodsPage_001.jsp?GOODS_NO=102638";
		new EdwinProductParser().parseByItemURL(url);
	}

}
