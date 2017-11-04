package com.walk_nie.taobao.montBell;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellStockChecker {

	public static void main(String[] args) throws Exception {
		new MontbellStockChecker().process();
	}

	public void process() throws Exception {
		String publishedBaobeiFile = "c:/temp/montbell-underware-20171023.csv";
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiListPublished = BaobeiUtil
				.readInPublishedBaobei(file);

		List<String> productIdListPublished = toGoodsObject(baobeiListPublished);
		Collections.sort(productIdListPublished);
		List<String> stockLines = Lists.newArrayList();
		for (String productId : productIdListPublished) {
			List<StockObject> stockListMontbell = getMontbellStockInfo(productId);
			List<StockObject> stockListTaobao = getTaobaoStockInfo(productId,
					baobeiListPublished);
			Collections.sort(stockListTaobao, new Comparator<StockObject>(){
				@Override
				public int compare(StockObject arg0, StockObject arg1) {
					return arg0.colorName.compareTo(arg1.colorName);
				}
				
			});
			report(productId, stockListMontbell, stockListTaobao, stockLines);
		}
		String fileName = String.format("StockCheck-%s.txt", DateUtils
				.formatDate(Calendar.getInstance().getTime(),
						"yyyy_MM_dd_HH_mm_ss"));
		FileUtils.writeLines(new File(MontBellUtil.rootPathName, fileName),
				stockLines);
	}

	private void report(String productId, List<StockObject> stockListMontbell,
			List<StockObject> stockListTaobao, List<String> stockLines) {

		// StringBuffer sb = new StringBuffer();
		List<String> lines = Lists.newArrayList();
		// sb.append("----" + productId + "----").append("\n");
		if (stockListMontbell.size() != stockListTaobao.size()) {
			// sb.append("--颜色大小有变化").append("\n");
		}
		boolean stockChange = false;
		boolean allNoStock = true;
		String fmt = "%s\t%s\t%s\t%s";
		for (StockObject stockTaobao : stockListTaobao) {
			boolean hasOne = false;
			String stockChgName = "";
			for (StockObject stockMontbell : stockListMontbell) {
				if (stockTaobao.colorName
						.equalsIgnoreCase(stockMontbell.colorName)
						&& stockTaobao.sizeName
								.equalsIgnoreCase(stockMontbell.sizeName)) {
					hasOne = true;
					if (stockTaobao.isStock && stockMontbell.isStock) {
						stockChgName = "没变化";
						allNoStock = false;
					}
					if (!stockTaobao.isStock && !stockMontbell.isStock) {
						stockChgName = "没变化";
						allNoStock = false;
					}
					if (!stockTaobao.isStock && stockMontbell.isStock) {
						stockChgName = "有库存";
						allNoStock = false;
						stockChange = true;
					}
					if (stockTaobao.isStock && !stockMontbell.isStock) {
						stockChgName = "没库存";
						stockChange = true;
					}
					break;
				}
			}
			if (hasOne) {
				if (!"没变化".equals(stockChgName)) {
					lines.add(String.format(fmt, productId,
							stockTaobao.colorName, stockTaobao.sizeName,
							stockChgName));
				}
			} else if (!stockTaobao.isStock) {
				// sb.append(
				// String.format(fmt, productId,stockTaobao.colorName,
				// stockTaobao.sizeName, "没变化"));
			} else {
				lines.add(String.format(fmt, productId, stockTaobao.colorName,
						stockTaobao.sizeName, "官网无"));
				stockChange = true;
			}
		}
		if (!stockChange) {
			stockLines.add(String.format(fmt, productId, "", "", "库存没有变化"));
		}else if(allNoStock){
			stockLines.add(String.format(fmt, productId, "", "", "官网无"));
		} else {
			stockLines.addAll(lines);
		}
	}

	private List<StockObject> getTaobaoStockInfo(String productId,
			List<BaobeiPublishObject> baobeiListPublished) {
		List<StockObject> stockObjList = Lists.newArrayList();
		BaobeiPublishObject tbObj = null;
		for (BaobeiPublishObject baobeiObj : baobeiListPublished) {
			String productId1 = getProductId(baobeiObj);
			if (productId.equals(productId1)) {
				tbObj = baobeiObj;
			}
		}
		if (tbObj == null) {
			return stockObjList;
		}
		// String cateProps = tbObj.cateProps.replaceAll("\"", "");
		String propAlias = tbObj.propAlias.replaceAll("\"", "");// size
		String inputCustomCpv = tbObj.input_custom_cpv.replaceAll("\"", ""); // color
		String skuProps = tbObj.skuProps.replaceAll("\"", "");// stock

		String[] sizesProp = propAlias.split(";");
		Map<String, String> sizeMap = Maps.newHashMap();
		for (String str : sizesProp) {
			if (str.indexOf("20509:") > -1) {
				String[] insizes = str.split(":");
				sizeMap.put(insizes[1], insizes[2]);
			}
		}
		String[] colrsProp = inputCustomCpv.split(";");
		Map<String, String> colrMap = Maps.newHashMap();
		for (String str : colrsProp) {
			if (str.indexOf("1627207:") > -1) {
				String[] incolors = str.split(":");
				colrMap.put(incolors[1], incolors[2]);
			}
		}
		String[] stockProp = skuProps.split(";");
		boolean colrFlag = false, sizeFlag = false;
		String num = "", colr = "", size = "";
		for (String str : stockProp) {
			if (str.indexOf("1627207:") > -1) {
				String[] incolors = str.split(":");
				num = incolors[1];
				colr = incolors[4];
				colrFlag = true;
			}
			if (str.indexOf("20509:") > -1) {
				String[] insizes = str.split(":");
				size = insizes[1];
				sizeFlag = true;
			}
			if (colrFlag && sizeFlag) {
				StockObject stock = new StockObject();
				stock.colorName = colrMap.get(colr);
				stock.sizeName = toSizeName(size, sizeMap);
				stock.isStock = !"0".equals(num) ? true : false;
				colrFlag = false;
				sizeFlag = false;
				num = "";
				colr = "";
				size = "";
				stockObjList.add(stock);
			}
		}

		return stockObjList;
	}

	private String toSizeName(String size, Map<String, String> sizeMap) {
		String siz = sizeMap.get(size);
		if (siz != null && !"".equals(siz)) {
			return siz;
		}
		if ("28313".equals(size)) {
			return "XS";
		}
		if ("28314".equals(size)) {
			return "S";
		}
		if ("28315".equals(size)) {
			return "M";
		}
		if ("28316".equals(size)) {
			return "L";
		}
		if ("28317".equals(size)) {
			return "XL";
		}
		if ("28318".equals(size)) {
			return "XL";
		}
		return siz;
	}

	private List<StockObject> getMontbellStockInfo(String productId)
			throws Exception {

		List<StockObject> stockObjList = Lists.newArrayList();
		String url = MontBellUtil.productUrlPrefix + productId;
		scanStock(stockObjList, url);
		url = MontBellUtil.productUrlPrefix_fo + productId;
		scanStock(stockObjList, url);

		return stockObjList;
	}

	private void scanStock(List<StockObject> stockList, String url)
			throws Exception {
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Element mainRightEle = null;
		try {
			mainRightEle = doc.select("div.rightCont").get(0);
		} catch (Exception e) {
			return;
		}
		Elements sizes = mainRightEle.select("div.sizeChoice").select("select")
				.select("option");
		for (int i = 1; i < sizes.size(); i++) {
			String zie = sizes.get(i).text();
			Elements divs = mainRightEle.select("div");
			Element colorEle = null;
			for (Element div : divs) {
				if (("size_" + zie).equals(div.attr("id"))) {
					colorEle = div;
					break;
				}
			}
			Elements colors = colorEle.select("table.dataTbl").select("tr");
			for (int j = 1; j < colors.size(); j++) {
				StockObject stock = new StockObject();
				Element color = colors.get(j);
				stock.colorName = color.select("p.colorName").text();
				stock.sizeName = zie;
				String stockN = color.select("p.sell").text();
				if ("在庫あり".equals(stockN)) {
					stock.isStock = true;
				}
				if ("直営店在庫あり".equals(stockN)) {
					stock.isStock = true;
				}
				if ("入荷待ち（受付可）".equals(stockN)) {
					stock.isStock = true;
				}
				boolean alreadyScan = false;
				for (StockObject obj : stockList) {
					if (obj.sizeName.equalsIgnoreCase(stock.sizeName)
							&& obj.colorName.equalsIgnoreCase(stock.colorName)) {
						alreadyScan = true;
					}
				}
				if (!alreadyScan)
					stockList.add(stock);
			}
		}
	}

	private List<String> toGoodsObject(
			List<BaobeiPublishObject> baobeiListPublished) {
		List<String> goodsObjListPublished = Lists.newArrayList();
		for (BaobeiPublishObject baobeiObj : baobeiListPublished) {
			goodsObjListPublished.add(getProductId(baobeiObj));
		}
		return goodsObjListPublished;
	}

	private String getProductId(BaobeiPublishObject baobeiObj) {
		String outer_id = baobeiObj.outer_id.replace("\"", "");
		if (outer_id.startsWith("MTBL_")) {
			String[] split = outer_id.split("-");
			return split[split.length - 1];
		} else {
			return outer_id;
		}
	}
}
