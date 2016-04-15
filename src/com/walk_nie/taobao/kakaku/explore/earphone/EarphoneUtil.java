package com.walk_nie.taobao.kakaku.explore.earphone;

import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class EarphoneUtil {

	public static String translateType(String type) {
		int idx  = type.indexOf("[");
		if(idx >0){
			type = type.substring(0,idx);
		}
		type = type.replaceAll("SOLID BASS", "");
		type = type.replaceAll("GLAMORCY", "");
		type = type.replaceAll("SONICSPORT", "");
		type = type.replaceAll("SonicFuel", "");
		type = type.replaceAll("Maestoso", "");
		type = type.replaceAll("EARSUIT", "");
		type = type.replaceAll("PREMIUM COMPACT", "");
		type = type.replaceAll("JUICY", "");
		type = type.replaceAll("ソニック ", "");
		type = type.replaceAll("Jienne", "");
		type = type.replaceAll("XTREME", "");
		type = type.replaceAll("XPLOSIVES", "");
		type = type.replaceAll("esnsy", "");
		type = type.replaceAll("リワインドBe!", "");
		type = type.replaceAll("COLOR COLLECT", "");
		type = type.replaceAll("BASS HEAD", "");
		type = type.replaceAll("STEEZ", "");
		type = type.replaceAll("CitiScape Jetts", "");
		type = type.replaceAll("CitiScape Frames", "");

		type = type.replaceAll("ActionFit", "");
		type = type.replaceAll("Fittone", "");
		type = type.replaceAll("EAR DROPS Jewel", "");
		type = type.replaceAll("PINK PINK PINK", "");
		type = type.replaceAll("EAR DROPS noir Accent", "");
		type = type.replaceAll("Actrail", "");
		type = type.replaceAll("marina", "");
		type = type.replaceAll("ALUMINIUM", "");
		type = type.replaceAll(" SPORTS", "");
		type = type.replaceAll("ALUMINIUM", "");
		type = type.replaceAll(" AMPERIOR", "");
		type = type.replaceAll("PowerfulBe!", "");
		type = type.replaceAll("ALUMINIUM", "");
		// type = type.replaceAll(" ", "");
		type = type.trim();
		return type;
	}

	public static void resolveSpecDetail(String text, String value,
			KakakuObject obj) {
		text = text.replaceAll(" ", "");
		text = text.replaceAll(" ", "");
		value = value.replaceAll(" ", "");
		value = value.replaceAll("　", "");
		SpecObject spec = (SpecObject)obj.spec;
		if ("装着方式".equals(text) || "装着方式 ".equals(text)) {
			spec.setType = convertSetType(value);
		} else if ("駆動方式".equals(text)) {
			spec.driverType = convertDriverType(value);
		} else if ("構造".equals(text) || "構造 ".equals(text)) {
			spec.constructType = value;
		} else if ("再生周波数帯域".equals(text)) {
			spec.waveBand = value;
		} else if ("インピーダンス".equals(text)) {
			spec.impedance = value;
		} else if ("音圧感度".equals(text)) {
			spec.voiceDepth = value;
		} else if ("ワイヤレス".equals(text)) {
			spec.wirelessType = value;
		} else if ("マイク".equals(text)) {
			if ("○".equals(value))
				spec.hasMicro = true;
		} else if ("ハイレゾ".equals(text)) {
			if ("○".equals(value))
				spec.isHighRes = true;
		} else if ("コード長".equals(text)) {
			spec.codeLength = value;
		} else if ("重量".equals(text)) {
			if (!"".equals(value)) {
				value = value.replaceAll("　", "");
				value = value.replaceAll("g", "");
				try {
					spec.weigth = ((int) Double.parseDouble(value));
				} catch (Exception e) {
					System.out.println("[ERROR]when to get the weight!");
				}
			}
		}
	}

	public static String convertToCNY(KakakuObject item,double currencyRate,double benefitRate) {
		int price = 0;
		double extraFee = 0;
		if(item.priceYodobashi != null){
			price = item.priceYodobashi.price;
		}else{
			KakakuObject colorY = getPriceFromColorList(item);
			if(colorY != null){
				price = colorY.priceYodobashi.price;
			}else if(item.priceAmazon != null && item.priceAmazon.isStock){
				price = KakakuUtil.getPriceForOther(item.priceAmazon);
			}else if(item.priceBiccamera != null && item.priceBiccamera.isStock){
				price = KakakuUtil.getPriceForOther(item.priceBiccamera);
			}else if(item.priceYamada != null && item.priceYamada.isStock){
				price = KakakuUtil.getPriceForOther(item.priceYamada);
			}else if(item.priceEEarPhone != null && item.priceEEarPhone.isStock){
				price = KakakuUtil.getPriceForOther(item.priceEEarPhone);
			}else if(item.priceMin !=null){
				price = KakakuUtil.getPriceForOther(item.priceMin);
			}
			extraFee = price * 0.1;
		}

		int emsFee = 999999999;
		if(item.spec.weigth != 0){
			emsFee = TaobaoUtil.getEmsFee(item.spec.weigth + 150);
		}
		if(emsFee == 999999999){
			System.out.println("[warn] the EMS FEE was NOT rightly set! kakakuId = " + item.id);
		}
		double priceCNY = (price + extraFee + emsFee) * currencyRate;
		if(item.itemMaker.toLowerCase().indexOf("sony") > 0){
			priceCNY = priceCNY + priceCNY * (benefitRate*0.8);
		}else{
			priceCNY = priceCNY + priceCNY * benefitRate;
		}
		return String.valueOf(Math.round(priceCNY));
	}

	private static KakakuObject getPriceFromColorList(KakakuObject item) {
		if (item.colorList.isEmpty()) {
			return null;
		} else {
			int price = 0;
			KakakuObject colorObj = null;
			for (KakakuObject o : item.colorList) {
				if (o.priceYodobashi != null) {
					if (price < o.priceYodobashi.price) {
						price = o.priceYodobashi.price;
						colorObj = o;
					}
				}
			}
			return colorObj;
		}
	}

	public static String convertSetType(String setTypeJp) {
		if ("オーバーヘッド".equals(setTypeJp)) {
			return "头戴式";
		} else if ("カナル型".equals(setTypeJp)) {
			return "入耳式";
		} else if ("耳かけ".equals(setTypeJp)) {
			return "耳挂式";
		} else if ("インナーイヤー".equals(setTypeJp)) {
			return "耳塞式";
		} else if ("ネックバンド".equals(setTypeJp)) {
			return "后挂式";
		}
		return setTypeJp;
	}

	public static String convertDriverType(String src) {
		if ("ダイナミック型".equals(src)) {
			return "动圈";
		} else if ("バランスド・アーマチュア型".equals(src)) {
			return "动铁";
		} else if ("バランスド・アーマチュア+エキサイター".equals(src)) {
			return "动铁";
		} else if (src.startsWith("ハイブリッド型")) {
			return "圈铁";
		} else if (src.equals("バランスドアーマチュア+ダイナミック")) {
			return "圈铁";
		} else if (src.equals("バランスドアーマチュアドライバー+ダイナミックドライバー")) {
			return "圈铁";
		} else if ("オープンエアーダイナミック型".equals(src)) {
			return "动圈";
		} else if ("密閉ダイナミック型".equals(src)) {
			return "动圈";
		} else if ("ダイナミック密閉型".equals(src)) {
			return "动圈";
		} else if ("ダイナミック+エキサイター".equals(src)) {
			return "动圈";
		} else if ("ネックバンド".equals(src)) {
			return "后挂式";
		}
		return "";
	}

    public static String getExtraMiaoshu() {
        StringBuffer miaoshu = new StringBuffer();
        miaoshu.append("<H3 style=\"FONT-SIZE: large; BORDER-TOP: #e19d63 1px solid; HEIGHT: 26px; BORDER-RIGHT: #e19d63 1px solid; BACKGROUND: #ff8f2d repeat-x 0px 0px; BORDER-BOTTOM: #d07428 1px solid; COLOR: #ffffff; PADDING-BOTTOM: 0px; PADDING-TOP: 3px; PADDING-LEFT: 10px; BORDER-LEFT: #e19d63 1px solid; PADDING-RIGHT: 0px\">关于耳机推荐</H3>");
        miaoshu.append("<DIV style=\"FONT-SIZE: large; BORDER-TOP: #b0bec7 1px solid; BORDER-RIGHT: #b0bec7 1px solid; BACKGROUND: #f8f9fb repeat-x 50% top; BORDER-BOTTOM: #b0bec7 1px solid; PADDING-BOTTOM: 10px; PADDING-TOP: 10px; PADDING-LEFT: 10px; BORDER-LEFT: #b0bec7 1px solid; PADDING-RIGHT: 10px\">");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\">耳机产品中琳琅满目，如何挑选满意的耳机，是困难的事。</P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\">根据个人爱好（重低音，无线），预算，音质等平衡下，挑选适合您自己的耳机。</P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\">所以，本店不推荐耳机！不回答 哪款耳机好 等问题。</P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\">推荐亲们,性能，请看官方参数和评测报告！试听，请移步实体店！</P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\"><SPAN style=\"FONT-WEIGHT: bold; COLOR: red\">本店为您代购100%真品的耳机！而且价格合适！</SPAN></P>");
        miaoshu.append("</DIV>");
        miaoshu.append("<H3 style=\"FONT-SIZE: large; BORDER-TOP: #e19d63 1px solid; HEIGHT: 26px; BORDER-RIGHT: #e19d63 1px solid; BACKGROUND: #ff8f2d repeat-x 0px 0px; BORDER-BOTTOM: #d07428 1px solid; COLOR: #ffffff; PADDING-BOTTOM: 0px; PADDING-TOP: 3px; PADDING-LEFT: 10px; BORDER-LEFT: #e19d63 1px solid; PADDING-RIGHT: 0px\">好评如潮！</H3>");
        miaoshu.append("<DIV style=\"FONT-SIZE: large; BORDER-TOP: #b0bec7 1px solid; BORDER-RIGHT: #b0bec7 1px solid; BACKGROUND: #f8f9fb repeat-x 50% top; BORDER-BOTTOM: #b0bec7 1px solid; PADDING-BOTTOM: 10px; PADDING-TOP: 10px; PADDING-LEFT: 10px; BORDER-LEFT: #b0bec7 1px solid; PADDING-RIGHT: 10px\">");
        miaoshu.append("<p style=\"PADDING-BOTTOM: 10px; PADDING-TOP: 10px; PADDING-LEFT: 10px; PADDING-RIGHT: 10px\">宝贝采购于日本知名电器量贩店！可提供购物小票。 </p>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\"><IMG style=\"PADDING-BOTTOM: 2px; PADDING-TOP: 2px; PADDING-LEFT: 2px; PADDING-RIGHT: 2px\" src=\"https://img.alicdn.com/imgextra/i4/2498620403/TB2Du41epXXXXaSXXXXXXXXXXXX_!!2498620403.jpg_620x10000.jpg\"></P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\"><IMG style=\"PADDING-BOTTOM: 2px; PADDING-TOP: 2px; PADDING-LEFT: 2px; PADDING-RIGHT: 2px\" src=\"https://img.alicdn.com/imgextra/i3/2498620403/TB2tOB4epXXXXaNXXXXXXXXXXXX_!!2498620403.jpg_620x10000.jpg\"></P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\"><IMG style=\"PADDING-BOTTOM: 2px; PADDING-TOP: 2px; PADDING-LEFT: 2px; PADDING-RIGHT: 2px\" src=\"https://img.alicdn.com/imgextra/i4/2498620403/TB2C_0_epXXXXXFXXXXXXXXXXXX_!!2498620403.jpg_620x10000.jpg\"></P>");
        miaoshu.append("<P style=\"TEXT-INDENT: 2em\"><IMG style=\"PADDING-BOTTOM: 2px; PADDING-TOP: 2px; PADDING-LEFT: 2px; PADDING-RIGHT: 2px\" src=\"https://img.alicdn.com/imgextra/i3/2498620403/TB2x0R3epXXXXa4XXXXXXXXXXXX_!!2498620403.jpg_620x10000.jpg\"></P>");
        miaoshu.append("</DIV>");
        
        return miaoshu.toString();
    }
}
