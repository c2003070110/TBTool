package com.walk_nie.taobao.montBell;

import com.walk_nie.taobao.util.TaobaoUtil;



public class MontBellUtil {
	static String pictureUrlFmt = "http://webshop.montbell.jp/common/images/product/prod_k/m_k_%s_%s.jpg";
	static String pictureUrlFmt1 = "http://webshop.montbell.jp/common/images/product/prod_k/k_%s_%s.jpg";

	
	public static void downloadPicture(GoodsObject goods,String outFilePathPrice) {
		
		for(String color : goods.colorList){
			String picUrl = String.format(pictureUrlFmt, goods.productId,color);
			String picName = goods.productId + "_" + color;
			try {
				TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
				goods.pictureNameList.add(picName);
			} catch (Exception ex) {
				picUrl = String.format(pictureUrlFmt1, goods.productId, color);
				try {
					TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
					goods.pictureNameList.add(picName);
				} catch (Exception ex1) {

				}
			}
		}
	}
}
