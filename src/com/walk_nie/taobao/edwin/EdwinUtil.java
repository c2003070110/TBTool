package com.walk_nie.taobao.edwin;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class EdwinUtil {
	public static String categoryURLFmt = "http://edwin-ec.jp/disp/CSfDispListPage_001.jsp?rowPerPage=100&brandCd=%s&ff=goods_sex_cd&fv=%s&ff=goods_series_search_cd&fv=%s";
	public static String dispNoUrlFmt = "http://edwin-ec.jp/disp/CSfDispListPage_001.jsp?dispNo=%s&brandCd=%s&cateCd=%s";
	public static String sizeTblUrl = "http://edwin-ec.jp/disp/CSfSizeTable.jsp?GOODS_NO=%s";
	public static String colorUrlFmt = "http://image.edwin-ec.jp/mgen/web:goods_%s-%s_01.jpg";
	public static String brandCdEdwin = "101"; // edwin
	public static String brandCdSomething = "102"; // something
	public static String brandCdLee = "201"; // lee
	public static String sex_val_man = "001"; // man
	public static String sex_val_woman = "002"; // woman
	public static String sex_val_kids = "003"; // kids

	public static String rootPathName = "out/edwin/";

	public static void downloadPicture(GoodsObject goods, String outFilePath) {

		int i = 0;
		for (String colorUrl : goods.colorPictureUrlList) {
			String picName = goods.ecCd + "_color_"
					+ goods.colorNameList.get(i++);
			try {
				TaobaoUtil.downloadPicture(outFilePath, colorUrl, picName);
				goods.colorPictureFileNameList.add(picName);
			} catch (Exception ex) {

			}
		}
		i = 0;
		for (String colorUrl : goods.bodyPictureUrlList) {
			String picName = goods.ecCd + "_body_" + (i++);
			try {
				File file = TaobaoUtil.downloadPicture(outFilePath, colorUrl,
						picName);
				goods.bodyPictureFileNameList.add(file.getAbsolutePath());
			} catch (Exception ex) {

			}
		}
	}

	public static String convertToCNYWithEmsFee(GoodsObject item,
			double curencyRate, double benefitRate) {

		try {
			int price = item.goods_real_price;
			if (price < 2000) {
				price = price + 500;
			} else if (price < 5000) {
				price = price + 400;
			} else if (price < 8000) {
				price = price + 300;
			} else if (price < 10000) {
				price = price + 200;
			}
			long priceTax = Math.round(price * 1);
			int emsFee = 1400;
			int transFeeJP = 540;
			double priceCNY = (priceTax + emsFee + transFeeJP) * curencyRate;
			priceCNY = priceCNY + priceCNY * benefitRate;
			return String.valueOf(Math.round(priceCNY) - 60);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "XXXXXX";
		}
	}

	public static String composePostageId(GoodsObject item) {
		// 首重90 25递增
		return "11306442590";
	}

	public static void composeBaobeiSubtitle(GoodsObject item,
			BaobeiPublishObject baobei) {
		baobei.subtitle = "\"日本直邮！100%正品！真正的日本代购！" + item.titleOrg + "！" + "\"";
	}

	public static void composeBaobeiPictureStatus(GoodsObject item,
			BaobeiPublishObject obj, List<String> taobaoColors) {
		String picStatus = "";
		// picture_status 图片状态：2;2;2;2;2;2;2;2;2;2;
		// 宝贝主图 main picture
		for (int i = 0; i < item.colorPictureFileNameList.size(); i++) {
			if (i == 5)
				break;
			picStatus += "2;";
		}
		int maxColorLen = Math.min(item.colorPictureFileNameList.size(),
				taobaoColors.size());
		// 销售属性图片
		for (int i = 0; i < maxColorLen; i++) {
			if (item.colorPictureFileNameList.size() == item.colorPictureFileNameList
					.size()) {
				// color picture
				picStatus += "2;";
			}
		}
		obj.picture_status = "\"" + picStatus + "\"";
	}

	public static void composeBaobeiPicture(GoodsObject item,
			BaobeiPublishObject obj, List<String> taobaoColors) {
		String picture = "";
		// picture　新图片：1128533_dkfs:1:0:|;1128533_mst:1:1:|;1128533_scl:1:2:|;1128533_tq:1:3:|;1128533_umr:1:4:|;1128533_dkfs:2:0:1627207:28320|;1128533_mst:2:0:1627207:28340|;1128533_scl:2:0:1627207:3232479|;1128533_tq:2:0:1627207:3232478|;1128533_umr:2:0:1627207:3232482|;
		// 宝贝主图 main picture
		for (int i = 0; i < item.colorPictureFileNameList.size(); i++) {
			if (i == 5)
				break;
			picture += item.colorPictureFileNameList.get(i) + ":1:" + i + ":|;";
		}
		int maxColorLen = Math.min(item.colorPictureFileNameList.size(),
				taobaoColors.size());
		// 销售属性图片
		for (int i = 0; i < maxColorLen; i++) {
			// if(item.colorPictureNameList.size() == item.colorList.size()){
			// color picture
			picture += item.colorPictureFileNameList.get(i) + ":2:0:1627207:"
					+ taobaoColors.get(i) + "|;";
			// }
		}

		obj.picture = "\"" + picture + "\"";
	}

	public static String getStock(GoodsObject item, String colorName,
			String sizeName) {
		boolean isStock = false;
		for (StockObject stockObj : item.stockList) {
			if ((stockObj.colorName.equals(colorName))
					&& (stockObj.sizeName.equals(sizeName))) {
				isStock = stockObj.isStock;
				break;
			}
		}
		return isStock ? "99" : "0";
	}

	public static String composeBaoyouMiaoshu() {
		StringBuffer detailSB = new StringBuffer();
		detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">各位亲们</h3>");
		detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		detailSB.append("<p style=\"text-indent:2.0em;color:red;font-weight:bold\">日本直邮。100%正品，日货料好质高，低调奢华，性价比高！</p>");
		detailSB.append("<p style=\"text-indent:2.0em;color:red;font-weight:bold\">买家一旦下单付款，不能取消订单 退款。不接受调换尺码颜色。下单之前请务必三思三思三思。</p>");
		detailSB.append("<p style=\"text-indent:2.0em;color:red;font-weight:bold\">本店不支持7天无理由退换货。也不能衣服不喜欢，颜色不好，大小不和，有线头等等理由退换货。下单之前请务必三思三思三思。</p>");
		detailSB.append("<p style=\"text-indent:2.0em;color:red;font-weight:bold\">图片均是官方提供，颜色以实物为准。色差介意者。下单之前请务必三思三思三思。</p>");
		detailSB.append("<p style=\"text-indent:2.0em;color:red;font-weight:bold\">颜色尺码，本店的意见仅供参考，没试过没看过，最终买家自己决定。下单之前请务必三思三思三思。</p>");
		detailSB.append("<p style=\"text-indent:2.0em;color:red;font-weight:bold\">时尚服装品牌，库存少，走货快，下单前，本店不回答有货否的问题。下单后才去确认哦！无货全额退款！</p>");
		// detailSB.append("<p style=\"text-indent:2.0em;\"><span style=\";color:red;font-weight:bold\">如果不幸被海关查到，由买家报关通关，不能报关的买家，请不要下单！！</span></p>");
		detailSB.append("</div>");
		return detailSB.toString();
	}

	public static String composeSizeTipMiaoshu(String sizeTipPic) {
		StringBuffer detailSB = new StringBuffer();
		if (!StringUtil.isBlank(sizeTipPic)) {
			detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">尺寸参考</h3>");
			detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
			detailSB.append("<p style=\"text-indent:2.0em;\">下单前，请认真比对尺寸大小！请 参考尺码表 和 模特效果。</p>");
			// detailSB.append("<p style=\"text-indent:2.0em;\"></p>");
			detailSB.append("<p style=\"text-indent:2.0em;\">图片中标有<span style=\";color:red;font-weight:bold\">1,2，3。。。A,B，C。。。 </span>表格中有对应的数据！不要被日语吓到了！</p>");
			detailSB.append("<p style=\"text-indent:2.0em;\"><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
					+ sizeTipPic + "\"/></p>");
			detailSB.append("</div>");
		}
		return detailSB.toString();
	}

	public static String composeDressOnMiaoshu(String modelTipInfo,
			List<String> dressOnPics) {
		StringBuffer detailSB = new StringBuffer();
		if (!dressOnPics.isEmpty()) {
			detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝图片</h3>");
			detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");

			if (!StringUtil.isBlank(modelTipInfo)) {
				detailSB.append("<p style=\"text-indent:2.0em;\">"
						+ modelTipInfo + "</p>");
			}
			for (String pic : dressOnPics) {
				detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
						+ pic + "\"/></p>");
			}
			detailSB.append("</div>");
		}
		return detailSB.toString();
	}
	
//	public static boolean isInchSize(GoodsObject item){
//		if(CollectionUtils.isEmpty(item.sizeNameList)){
//			return false;
//		}
//		if(item.sizeNameList.get(0).equalsIgnoreCase("inch")){
//			return true;
//		}
//		return false;
//	}
	
	public static boolean isXSMLSize(GoodsObject item){
		if(CollectionUtils.isEmpty(item.sizeNameList)){
			return false;
		}
		for(String str:item.sizeNameList){
			if(str.equalsIgnoreCase("xs")){
				return true;
			}
			if(str.equalsIgnoreCase("s")){
				return true;
			}
			if(str.equalsIgnoreCase("m")){
				return true;
			}
			if(str.equalsIgnoreCase("l")){
				return true;
			}
			if(str.equalsIgnoreCase("xl")){
				return true;
			}
		}
		return false;
	}

}
