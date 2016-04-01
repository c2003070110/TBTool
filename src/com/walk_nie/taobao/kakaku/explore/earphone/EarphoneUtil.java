package com.walk_nie.taobao.kakaku.explore.earphone;

import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class EarphoneUtil extends com.walk_nie.taobao.kakaku.KakakuUtil {

	// FIXME change me!!
	public final static double currencyRate = 0.065;
	private final static double benefitRate = 0.08;
	private final static double benefitRateSony = benefitRate - 0.02;


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

	public static String convertToCNY(KakakuObject item) {
		int price = 0;
		double extraFee = 0;
		if(item.priceYodobashi != null){
			price = item.priceYodobashi.price;
		}else{
			KakakuObject colorY = getPriceFromColorList(item);
			if(colorY != null){
				price = colorY.priceYodobashi.price;
			}else if(item.priceAmazon != null && item.priceAmazon.isStock){
				price = getPriceForOther(item.priceAmazon);
			}else if(item.priceBiccamera != null && item.priceBiccamera.isStock){
				price = getPriceForOther(item.priceBiccamera);
			}else if(item.priceYamada != null && item.priceYamada.isStock){
				price = getPriceForOther(item.priceYamada);
			}else if(item.priceEEarPhone != null && item.priceEEarPhone.isStock){
				price = getPriceForOther(item.priceEEarPhone);
			}else if(item.priceMin !=null){
				price = getPriceForOther(item.priceMin);
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
			priceCNY = priceCNY + priceCNY * benefitRateSony;
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
}
