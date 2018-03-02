package com.walk_nie.taobao.edwin;

import java.io.IOException;
import java.util.List;

import com.beust.jcommander.internal.Lists;

public class EdwinMain {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//String url = "http://edwin-ec.jp/disp/CSfGoodsPage_001.jsp?GOODS_NO=102638";
		//new EdwinProductParser().parseByItemURL(url);
		//new EdwinProductParser().getSizeTable("108978");
		// 
		//new EdwinProductParser().parseByCategorys(edwinManCategoryList());

		EdwinManBaobeiCreator creator = new EdwinManBaobeiCreator();
		creator.processEdwinMan();
		//creator.processEdwinWoman();
		//creator.processLeeMan();
		//creator.processLeeWoman();
	}

	public static List<String> edwinManCategoryList(){
		String brandCd="101"; //edwin
		String fv="001"; // man
		List<String> categoryList = Lists.newArrayList();
		/*
		List<String> dispNoList = Lists.newArrayList();
		dispNoList.add("001001011001001002"); //レギュラー・ストレート
		dispNoList.add("001001011001001003"); //タイト・ストレート
		dispNoList.add("001001011001001004"); //ルーズ／ワイド・ストレート
		dispNoList.add("001001011001001005"); //スキニー／スリム
		dispNoList.add("001001011001001006"); //テーパード
		dispNoList.add("001001011001001009"); //ショーツ
		dispNoList.add("001001011001001011"); //チノパンツ
		dispNoList.add("001001011001001012"); //スラックス／トラウザー
		dispNoList.add("001001011001001013"); //ワーク／ミリタリー
		dispNoList.add("001001011001001014"); //イージーパンツ
		dispNoList.add("001001011001002002"); //アウター
		dispNoList.add("001001011001002003"); //デニムジャケット
		dispNoList.add("001001011001002004"); //長袖シャツ
		dispNoList.add("001001011001002006"); //長袖Ｔシャツ
		dispNoList.add("001001011001002007"); //半袖Ｔシャツ
		dispNoList.add("001001011001002008"); //タンクトップ
		dispNoList.add("001001011001002010"); //スウェット／パーカ
		dispNoList.add("001001011001002011"); //ニット／セーター
		dispNoList.add("001001011001002012"); //大きいサイズ
		*/

		List<String> serialList = Lists.newArrayList();
		//serialList.add("1101");// INTERNATIONAL BASIC
		serialList.add("1102");// 503
		//serialList.add("1120");// JERSEYS
		//serialList.add("1130");// EDWIN E STANDARD
		//serialList.add("1123");// WILD FIRE
		
		/*  all
		serialList.add("1101");// INTERNATIONAL BASIC
		serialList.add("1102");// 503
		serialList.add("1120");// JERSEYS
		serialList.add("1130");// EDWIN E STANDARD
		serialList.add("1109");// XV
		serialList.add("1110");// WORK
		serialList.add("1114");// CHINO
		serialList.add("1121");// Vintage Collection
		serialList.add("1126");// DENIM is EDWIN
		serialList.add("1128");// EUROPE LINE
		serialList.add("1131");// SEN
		serialList.add("1123");// WILD FIRE
		serialList.add("1127");// AIR IN DENIM
		serialList.add("9992");// EDWIN その他
		serialList.add("1299");// MissEDWIN その他
		serialList.add("9999");// その他
		*/
		for (String serial : serialList) {
			categoryList.add(String.format(EdwinUtil.categoryURLFmt, brandCd, fv,
					serial));
		}
		
		return categoryList;
	}
}
