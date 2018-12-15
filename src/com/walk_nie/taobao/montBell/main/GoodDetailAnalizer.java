package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.montBell.GoodsObject;
import com.walk_nie.taobao.montBell.MontbellProductParser;
import com.walk_nie.taobao.montBell.StockObject;
import com.walk_nie.util.NieConfig;

public class GoodDetailAnalizer {
	private String outFileName = NieConfig.getConfig("montbell.out.root.folder") + "/analizy_%s.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		GoodDetailAnalizer an = new GoodDetailAnalizer();
		an.process();
	}

	public void process() throws IOException {

		//List<String> scanCategoryIds = getDownCategorys();
		List<String> scanCategoryIds = getFreeceCategorys();
		//List<String> scanCategoryIds = getShellCategorys();
		//List<String> scanCategoryIds = getTShirtCategorys();
		MontbellProductParser parser = new MontbellProductParser();
		List<GoodsObject> itemList = parser.scanItem(scanCategoryIds);
		writeOut(itemList);
	}

	private void writeOut(List<GoodsObject> itemList) throws IOException {

		String outFilePath = String.format(outFileName, DateUtils.formatDate(
				Calendar.getInstance().getTime(), "yyyy_MM_dd_HH_mm_ss"));
		File oFile = new File(outFilePath);
		String fmt = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
		String str = String.format(fmt, "分类ID",
			    "商品ID", "商品名", "价格",
				"颜色", "Size",
				"在库", "面料",
				"重量", "特色1", "特色2");
		FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		for (GoodsObject itemObj : itemList) {
			 str = String.format(fmt, itemObj.cateogryObj.categoryId,
					itemObj.productId, itemObj.titleOrg, itemObj.priceJPY,
					toColorString(itemObj.colorList), itemObj.sizeOrg,
					toStockString(itemObj.stockList), itemObj.materialOrg,
					itemObj.weight+"", itemObj.specialOrg, itemObj.functionOrg);
			FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		}
	}

	private Object toColorString(List<String> colorList) {

		String str = "";
		for (int i = 0; i < colorList.size() - 1; i++) {
			String obj = colorList.get(i);
			if (i == 0) {
				str = obj;
			} else {
				str += "#" + obj;
			}
		}
		return str;
	}

	private String toStockString(List<StockObject> stockList) {
		String str = "";
		for (int i = 0; i < stockList.size() - 1; i++) {
			StockObject obj = stockList.get(i);
			if (i == 0) {
				str = obj.sizeName + ";" + obj.colorName + ";"
						+ obj.stockStatus;
			} else {
				str += "#" + obj.sizeName + ";" + obj.colorName + ";"
						+ obj.stockStatus;
			}
		}
		return str;
	}

	private List<String> getDownCategorys() {
		List<String> categoryIds = Lists.newArrayList();
		categoryIds.add("131000"); // ダウンジャケット
		categoryIds.add("137000"); // ダウンジャケット（軽量シリーズ）
		categoryIds.add("137500"); // 半袖ダウンジャケット
		categoryIds.add("134000"); // ダウンベスト
		categoryIds.add("136000"); // コート（中綿入り）
		categoryIds.add("138000"); // ダウンパンツ
		categoryIds.add("136500"); // ダウンはんてん（半纏）
		categoryIds.add("132000"); // ダウン（極地用）
		categoryIds.add("625000"); // USモデル ダウンジャケット
		categoryIds.add("138600"); // ダウンマフラー/ブランケット
		return categoryIds;
	}

	private List<String> getFreeceCategorys() {
		List<String> categoryIds = Lists.newArrayList();
		categoryIds.add("122000"); // フリースジャケット FLEECE JACKET
		categoryIds.add("121000"); // 防風性フリース
		categoryIds.add("126000"); // フリースプルオーバー
		categoryIds.add("124000"); // フリースベスト
		categoryIds.add("123000"); // フリースパンツ
		categoryIds.add("129000"); // フリーススカート
		return categoryIds;
	}

	private List<String> getShellCategorys() {
		List<String> categoryIds = Lists.newArrayList();
		categoryIds.add("142000"); // ハードシェル>ジャケット（保温材入り）
        categoryIds.add("141000"); // ハードシェル>ジャケット（保温材なし）
        categoryIds.add("146000"); // ハードシェル>パンツ(保温材入り)
        categoryIds.add("145000"); // ハードシェル>パンツ(保温材なし)
        
        categoryIds.add("22000"); // ソフトシェル>ソフトシェルジャケット
        
        categoryIds.add("61200"); // トレッキングパンツ＜厚手＞
        categoryIds.add("61100"); // トレッキングパンツ＜中厚手＞
        categoryIds.add("61000"); // トレッキングパンツ＜薄手＞
        
        categoryIds.add("63000"); // リラックスパンツ
        
        categoryIds.add("25000"); // ソフトシェル>ライトシェルジャケット/ベスト
        categoryIds.add("22500"); // ソフトシェル>ソフトシェルパンツ
        categoryIds.add("23000"); // ソフトシェル>ライトシェルパンツ
        
        categoryIds.add("8800"); // レインウェア（ゴアテックス製）
        categoryIds.add("1000"); // レインウェア
        categoryIds.add("2000"); // レインコート
        
        categoryIds.add("8000"); // レインウェア（自転車用）
        categoryIds.add("8700"); // レインウェア（釣り用）
        categoryIds.add("8500"); // レインウェア（バイク用）
        categoryIds.add("8600"); // レインウェア（農作業用）
        
        categoryIds.add("9000"); // レインポンチョ
        categoryIds.add("4000"); // 傘
        categoryIds.add("3000"); // レインキャップ/ハット
        
		return categoryIds;
	}

	private List<String> getTShirtCategorys() {
		List<String> categoryIds = Lists.newArrayList();
		categoryIds.add("46500"); // 天然素材<メリノウールプラス>
		categoryIds.add("45550"); // 機能素材<ウイックロンZEOサーマル>
        categoryIds.add("44000"); // 機能素材<ウイックロンクール>
        categoryIds.add("45500"); // 機能素材<ウイックロンZEO>
        categoryIds.add("42000"); // 機能素材<ウイックロン>
        
		return categoryIds;
	}

}
