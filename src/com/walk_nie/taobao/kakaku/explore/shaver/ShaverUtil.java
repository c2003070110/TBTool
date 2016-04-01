package com.walk_nie.taobao.kakaku.explore.shaver;

import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.util.TaobaoUtil;


public class ShaverUtil {
	// FIXME change me!!
	public final static double currencyRate = 0.065;
	private final static double benefitRate = 0.08;

	public static void resolveSpecDetail(String text, String value,
			KakakuObject obj) {
		text = text.replaceAll(" ", "");
		text = text.replaceAll(" ", "");
		value = value.replaceAll(" ", "");
		value = value.replaceAll("　", "");
		SpecObject spec = (SpecObject)obj.spec;
		if ("刃の枚数".equals(text) || "刃の枚数 ".equals(text)) {
			spec.knifeAmount = convertKnifeAmount(value);
		} else if ("駆動方式".equals(text) || "駆動方式 ".equals(text)) {
			spec.driverType = convertDriverType(value);
		} else if ("電源方式".equals(text) || "電源方式 ".equals(text)) {
			spec.rechageType = value;
		} else if ("充電時間".equals(text)) {
			spec.rechageTime = value;
		} else if ("使用日数".equals(text)) {
			spec.usableDays = value;
		} else if ("自動洗浄機能".equals(text)) {
			if ("○".equals(value))
				spec.isAutoWash = true;
		} else if ("洗浄機能".equals(text)) {
			spec.washType = value;
		} else if ("水洗い可".equals(text)) {
			if ("○".equals(value))
				spec.isWashable = true; 
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
	private static String convertDriverType(String value) {
		if ("往復式".equals(value)) {
			return "往復式"; 
		}
		return value;
	}
	private static String convertKnifeAmount(String value) {
		if ("3枚刃".equals(value) || "3枚刃".equals(value)) {
			return "3刀头";
		} else if ("4枚刃".equals(value) || "4枚刃".equals(value)) {
			return "4刀头";
		} else if ("5枚刃".equals(value) || "5枚刃".equals(value)) {
			return "5刀头"; 
		}
		return value;
	}
	public static String translateType(String type) {
		type = type.replaceAll("", "");
		int idx  = type.indexOf("[");
		if(idx >0){
			type = type.substring(0,idx);
		}
		type = type.replaceAll("ラムダッシュ ", "");
		type = type.replaceAll("アミューレオム ", "");
		type = type.replaceAll("スティックシェーバー ", "");
		type = type.replaceAll("システムスムーサー ", "");
		type = type.replaceAll("ツインエクス ", "");
		type = type.replaceAll("カードシェーバーアイト ", "");
		type = type.replaceAll("スピンネット", "");
		type = type.replaceAll("スピンネット・リチウム", "");
		type = type.replaceAll("スーパーレザー", "");
		type = type.replaceAll("システムスムーサーライト ", "");
		type = type.replaceAll("・リチウム", "");
		
		type = type.replaceAll("ブラウン シリーズ9 ", "");
		type = type.replaceAll("ブラウン シリーズ7 ", "");
		type = type.replaceAll("ブラウン シリーズ5 ", "");
		type = type.replaceAll("ブラウン シリーズ3 ", "");
		type = type.replaceAll("ブラウン シリーズ1 ", "");
		type = type.replaceAll("クールテック ", "");
		type = type.replaceAll("ブラウン ウォーターフレックス ", "");
		type = type.replaceAll("ブラウン クルーザー5 ", "");
		type = type.replaceAll("ブラウン クルーザー6 ", "");
		type = type.replaceAll("ブラウン ", "");
		type = type.replaceAll("モバイルシェーブ ", "");
		type = type.replaceAll("プロソニック ", "");
		
		type = type.replaceAll("9000シリーズ ", "");
		type = type.replaceAll("センソタッチ ", "");
		type = type.replaceAll("スタイルシェーバー ", "");
		type = type.replaceAll("アクアタッチ ", "");
		type = type.replaceAll("ポータブルシェーバー ", "");
		type = type.replaceAll("クリックアンドスタイル ", "");
		type = type.replaceAll("パワータッチ ", "");
		
		type = type.replaceAll("ロータリージーソード ", "");
		type = type.replaceAll("", "");
		type = type.replaceAll("", "");
		type = type.replaceAll("", "");
		type = type.replaceAll("", "");
		return type;
	}


	public static String convertToCNY(KakakuObject item) {
		int price = 0;
		if(item.priceYodobashi != null){
			price = item.priceYodobashi.price;
		}else{
			if(item.priceAmazon != null && item.priceAmazon.isStock){
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
		}

		int emsFee = 99999;
		if (item.spec.weigth == 0) {
			item.spec.weigth = 1400;
		}
		if (item.spec.weigth != 0) {
			emsFee = TaobaoUtil.getEmsFee(item.spec.weigth + 250);
		}
		if (emsFee == 9999) {
			System.out.println("[warn] the EMS FEE was NOT rightly set! kakakuId = " + item.id);
		}
		double priceCNY = (price + emsFee) * currencyRate;
		priceCNY = priceCNY + priceCNY * benefitRate;
		return String.valueOf(Math.round(priceCNY));
	}
}
