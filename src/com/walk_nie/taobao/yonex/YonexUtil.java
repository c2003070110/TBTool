package com.walk_nie.taobao.yonex;

import com.walk_nie.util.NieConfig;

public class YonexUtil {
    public static String rootPathName = NieConfig.getConfig("yonex.work.root.folder") ;
	public static String productListFile = rootPathName  + "/YonexProductList.txt";
	public static String priceListFile = rootPathName + "/YonexCNYPrice.txt";
    public static String urlPrefix = "http://www.yonex.co.jp";


	public static String getPictureSavePath(GoodsObject obj) {
		return YonexUtil.rootPathName + "/" + YonexUtil.getCategoryTypeName(obj.categoryType);
	}
    public static String getCategoryTypeName(int categoryType) {
		if (categoryType == 1) {
			return "badminRacquets";
		} else if (categoryType == 2) {
			return "badminShoes";
		} else if (categoryType == 3) {
			return "tennisRacquets";
		} else if (categoryType == 4) {
			return "tennisShoes";
		}
		return "";
	}
}
