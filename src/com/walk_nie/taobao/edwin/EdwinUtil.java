package com.walk_nie.taobao.edwin;

import java.io.File;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class EdwinUtil {
	public static String categoryURLFmt = "http://edwin-ec.jp/disp/CSfDispListPage_001.jsp?rowPerPage=100&brandCd=%s&ff=goods_sex_cd&fv=%s&ff=goods_series_search_cd&fv=%s";
	public static String sizeTblUrl = "http://edwin-ec.jp/disp/CSfSizeTable.jsp?GOODS_NO=%s";
	public static String colorUrlFmt = "http://image.edwin-ec.jp/mgen/web:goods_%s-%s_01.jpg";
	public static String brandCdEdwin = "101"; // edwin
	public static String brandCdLee = "201"; // lee
	public static String sex_val_man = "001"; // man
	public static String sex_val_woman = "002"; // woman

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

	public static List<String> edwinManPants = Lists.newArrayList();
	{
		edwinManPants.add("001001011001001002");// レギュラー・ストレート
		edwinManPants.add("001001011001001003");// タイト・ストレート
		edwinManPants.add("001001011001001004");// ルーズ／ワイド・ストレート
		edwinManPants.add("001001011001001005");// スキニー／スリム
		edwinManPants.add("001001011001001006");// テーパード
		edwinManPants.add("001001011001001009");// ショーツ
		edwinManPants.add("001001011001001011");// チノパンツ
		edwinManPants.add("001001011001001012");// スラックス／トラウザー
		edwinManPants.add("001001011001001013");// ワーク／ミリタリー
		edwinManPants.add("001001011001001014");// イージーパンツ
	}

	public static List<String> edwinManJacket = Lists.newArrayList();
	{
		edwinManJacket.add("001001011001002002");// アウター
		edwinManJacket.add("001001011001002003");// デニムジャケット
		edwinManJacket.add("001001011001002004");// 長袖シャツ
		edwinManJacket.add("001001011001002006");// 長袖Ｔシャツ
		edwinManJacket.add("001001011001002007");// 半袖Ｔシャツ
		edwinManJacket.add("001001011001002008");// タンクトップ
		edwinManJacket.add("001001011001002010");// スウェット／パーカ
		edwinManJacket.add("001001011001002011");// ニット／セーター
		edwinManJacket.add("001001011001002012");// 大きいサイズ
	}
	public static List<String> edwinKidsPants = Lists.newArrayList();
	{
		edwinKidsPants.add("00100101100300100");// デニムパンツ

		edwinKidsPants.add("001001011003001003");// スウェットパンツ
		edwinKidsPants.add("001001011003001004");// イージーパンツ
		edwinKidsPants.add("001001011003001005");// チノパンツ
		edwinKidsPants.add("001001011003001007");// ショートパンツ
		edwinKidsPants.add("001001011003001014");// 大きいサイズ
		edwinKidsPants.add("001001011003001015");// キュロット
	}
	public static List<String> edwinKidsJacket = Lists.newArrayList();
	{
		edwinKidsJacket.add("001001011003002002");// アウター
		edwinKidsJacket.add("001001011003002006");// 長袖Ｔシャツ
		edwinKidsJacket.add("001001011003002010");// スウェット／パーカ
		edwinKidsJacket.add("001001011003002012");// ワンピース
	}

	public static List<String> edwinLadyPants = Lists.newArrayList();
	{
		edwinLadyPants.add("001001011002001002");// レギュラー・ストレート
		edwinLadyPants.add("001001011002001003");// タイト・ストレート
		edwinLadyPants.add("001001011002001004");// ルーズ／ワイド・ストレート
		edwinLadyPants.add("001001011002001005");// スキニー／スリム
		edwinLadyPants.add("001001011002001006");// テーパード
		edwinLadyPants.add("001001011002001012");// スラックス／トラウザー
		edwinLadyPants.add("001001011002001013");// ワーク／ミリタリー
		edwinLadyPants.add("001001011002001014");// イージーパンツ
		edwinLadyPants.add("001001011002001017");// 大きいサイズ
		edwinLadyPants.add("001001011002001018");// スカート
	}

	public static List<String> edwinLadyJacket = Lists.newArrayList();
	{
		edwinLadyJacket.add("001001011002002003");// デニムジャケット
		edwinLadyJacket.add("001001011002002004");// 長袖シャツ
	}
	public static List<String> leeManPants = Lists.newArrayList();
	{
		leeManPants.add("001001015001001002");// レギュラー・ストレート

		leeManPants.add("001001015001001003");// タイト・ストレート

		leeManPants.add("001001015001001004");// ルーズ/ワイド・ストレート

		leeManPants.add("001001015001001005");// スキニー／スリム

		leeManPants.add("001001015001001006");// テーパード

		leeManPants.add("001001015001001007");// ブーツカット／ベルボトム

		leeManPants.add("001001015001001008");// クロップド

		leeManPants.add("001001015001001010");// オーバーオール／ツナギ

		leeManPants.add("001001015001001011");// チノパンツ

		leeManPants.add("001001015001001012");// スラックス
		leeManPants.add("001001015001002002");// アウター
	}
	public static List<String> leeManJacket = Lists.newArrayList();
	{
		leeManJacket.add("001001015001002003");// デニムジャケット
		leeManJacket.add("001001015001002004");// 長袖シャツ
		leeManJacket.add("001001015001002006");// 長袖Ｔシャツ
		leeManJacket.add("001001015001002007");// 半袖Ｔシャツ
		leeManJacket.add("001001015001002010");// スウェット／パーカ
	}
	public static List<String> leeLadyPants = Lists.newArrayList();
	{
		leeLadyPants.add("001001015002001002");// レギュラー・ストレート
		leeLadyPants.add("001001015002001004");// ルーズ/ワイド・ストレート
		leeLadyPants.add("001001015002001005");// スキニー／スリム
		leeLadyPants.add("001001015002001006");// テーパード
		leeLadyPants.add("001001015002001007");// ブーツカット/フレア
		leeLadyPants.add("001001015002001010");// オーバーオール／サロペット
		leeLadyPants.add("001001015002001012");// スラックス／トラウザー
		leeLadyPants.add("001001015002001013");// ワーク／ミリタリー
		leeLadyPants.add("001001015002001015");// レギンスパンツ
		leeLadyPants.add("001001015002001018");// スカート
	}
	public static List<String> leeLadyJacket = Lists.newArrayList();
	{
		leeLadyJacket.add("001001015002002002");// アウター
		leeLadyJacket.add("001001015002002003");// デニムジャケット
		leeLadyJacket.add("001001015002002004");// 長袖シャツ
		leeLadyJacket.add("001001015002002007");// 半袖Ｔシャツ
		leeLadyJacket.add("001001015002002010");// スウェット／パーカ
		leeLadyJacket.add("001001015002002012");// ワンピース
	}
	public static List<String> leeKidsPants = Lists.newArrayList();
	{
		leeKidsPants.add("001001015003001002");// >デニムパンツ
		leeKidsPants.add("001001015003001003");// >スウェットパンツ
		leeKidsPants.add("001001015003001004");// >イージーパンツ
		leeKidsPants.add("001001015003001007");// >ショートパンツ
		leeKidsPants.add("001001015003001008");// >オーバーオール／サロペット
		leeKidsPants.add("001001015003001010");// >スカート
		leeKidsPants.add("001001015003001014");// >ベビー
	}
	public static List<String> leeKidsJacket = Lists.newArrayList();
	{
		leeKidsJacket.add("001001015003002002");// >アウター
		leeKidsJacket.add("001001015003002004");// >長袖シャツ
		leeKidsJacket.add("001001015003002006");// >長袖Ｔシャツ
		leeKidsJacket.add("001001015003002007");// >半袖Ｔシャツ
		leeKidsJacket.add("001001015003002008");// >タンクトップ／キャミソール
		leeKidsJacket.add("001001015003002010");// >スウェット／パーカ
		leeKidsJacket.add("001001015003002012");// >ワンピース
		leeKidsJacket.add("001001015003002014");// >ロンパース
		leeKidsJacket.add("001001015003002015");// >甚平
	}
	public static List<String> somethingPants = Lists.newArrayList();
	{
		somethingPants.add("001001012002001002");// >レギュラー・ストレート
		somethingPants.add("001001012002001003");// >タイト・ストレート
		somethingPants.add("001001012002001004");// >ルーズ／ワイド・ストレート
		somethingPants.add("001001012002001005");// >スキニー／スリム
		somethingPants.add("001001012002001006");// >テーパード
		somethingPants.add("001001012002001007");// >ブーツカット／フレア
		somethingPants.add("001001012002001010");// >オーバーオール／サロペット
		somethingPants.add("001001012002001012");// >スラックス／トラウザー
		somethingPants.add("001001012002001017");// >大きいサイズ
		somethingPants.add("001001012002001018");// >スカート
	}
	public static List<String> somethingJacket = Lists.newArrayList();
	{
		somethingJacket.add("001001012002002002");// >アウター
		somethingJacket.add("001001012002002003");// >デニムジャケット
		somethingJacket.add("001001012002002004");// >長袖シャツ
		somethingJacket.add("001001012002002006");// >長袖Ｔシャツ
		somethingJacket.add("001001012002002007");// >半袖Ｔシャツ
		somethingJacket.add("001001012002002008");// >タンクトップ／キャミソール
		somethingJacket.add("001001012002002011");// >ニット／セーター
		somethingJacket.add("001001012002002013");// >大きいサイズ
	}

}
