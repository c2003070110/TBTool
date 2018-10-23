package com.walk_nie.taobao.mizuno;


public class MizunoUtil {
    public static String rootPathName = "out/mizuno/";
    public static String urlPrefix = "http://www.mizuno.co.jp";
    public static String serieListFileName = "serieList.txt";


	public static String getPictureSavePath(GoodsObject obj) {
		return MizunoUtil.rootPathName + "/" ;
	}
 
}
