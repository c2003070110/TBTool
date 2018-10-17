package com.walk_nie.taobao.yonex;


public class YonexUtil {
	public static String productListFile = "res/YonexProductList.txt";
	public static String priceListFile = "res/YonexCNYPrice.txt";
    public static String rootPathName = "out/Yonex/";
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
