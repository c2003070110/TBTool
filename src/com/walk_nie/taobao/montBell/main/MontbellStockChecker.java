package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
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
import org.openqa.selenium.json.Json;
import org.seleniumhq.jetty9.util.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.object.StockObject;
import com.walk_nie.taobao.object.TaobaoOrderProductInfo;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MontbellStockChecker {
	private String inFileName = "StockCheck-in.txt";
	private String outFileName = "StockCheck-out.txt";

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			new MontbellStockChecker().processByFile();
		} else if (args.length == 1) {
			new MontbellStockChecker().processByProductId(args[0]);
		}
	}

	public void processByProductId(String productId) throws Exception {

		List<StockObject> stockListMontbell = getMontbellStockInfo(productId);
		Collections.sort(stockListMontbell, new Comparator<StockObject>() {
			@Override
			public int compare(StockObject arg0, StockObject arg1) {
				return arg0.colorName.compareTo(arg1.colorName);
			}
		});
		List<String> stockLines = Lists.newArrayList();
		String fmt = "[PRODUCT:%s][COLOR:%-6s][SIZE:%-6s][STOCK:%s]";
		for (StockObject stockObj : stockListMontbell) {
			String line = String.format(fmt, productId, stockObj.colorName,
					stockObj.sizeName,stockObj.stockStatus);
			stockLines.add(line);
			System.out.println(line);
		}
		if (stockListMontbell.get(0).sizeName != null
				&& !"".equals(stockListMontbell.get(0).sizeName)) {
			Collections.sort(stockListMontbell, new Comparator<StockObject>() {
				@Override
				public int compare(StockObject arg0, StockObject arg1) {
					return arg0.sizeName.compareTo(arg1.sizeName);
				}
			});
			fmt = "[PRODUCT:%s][SIZES:%-6s][COLOR:%-6s][STOCK:%s]";
			for (StockObject stockObj : stockListMontbell) {
				String line = String.format(fmt, productId, stockObj.sizeName,
						stockObj.colorName, stockObj.stockStatus);
				stockLines.add(line);
				System.out.println(line);
			}
		}
		
		File oFile = new File(MontBellUtil.rootPathName,outFileName);
		NieUtil.appendToFile(oFile, stockLines);
	}

	public void processByFile() throws Exception {
		String publishedBaobeiFile = "c:/temp/montbell-down-20171221.csv";
		String outFile = "StockCheck-%s.txt";
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
			Collections.sort(stockListTaobao, new Comparator<StockObject>() {
				@Override
				public int compare(StockObject arg0, StockObject arg1) {
					return arg0.colorName.compareTo(arg1.colorName);
				}
			});
			report(productId, stockListMontbell, stockListTaobao, stockLines);
		}
		String fileName = String.format(outFile, DateUtils.formatDate(Calendar
				.getInstance().getTime(), "yyyy_MM_dd_HH_mm_ss"));
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
		} else if (allNoStock) {
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

	public List<StockObject> getMontbellStockInfo(String productId)
			throws Exception {

		List<StockObject> stockObjList = Lists.newArrayList();
		String url = MontBellUtil.productUrlPrefix + productId;
		scanStock(stockObjList, url);
		url = MontBellUtil.productUrlPrefix_fo + productId;
		scanStock(stockObjList, url);
		
		for (StockObject st : stockObjList) {
			st.productId = productId;
		}

		return stockObjList;
	}

	private void scanStock(List<StockObject> stockList, String url) throws  IOException
			{
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		Element mainRightEle = null;
		try {
			mainRightEle = doc.select("div.rightCont").get(0);
		} catch (Exception e) {
			return;
		}
		
		Elements priceEls = mainRightEle.select("p.price");
		String price ="" ;
		if(!priceEls.isEmpty()){
			price = priceEls.get(0).text();
			price = price.replace("+税", "");
			price = price.replace("価格", "");
			price = price.replace(",", "");
			price = price.replace("¥", "");
			price = price.replace("アウトレット", "");
			price = price.trim();
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
			if (colorEle == null) {
				continue;
			}
			Elements colors = colorEle.select("table.dataTbl").select("tr");
			for (int j = 1; j < colors.size(); j++) {
				StockObject stock = new StockObject();
				Element color = colors.get(j);
				stock.priceJPY = price;
				stock.colorName = color.select("p.colorName").text();
				stock.sizeName = zie;
				String stockN = color.select("p.sell").text();
				if ("".equals(stockN)) {
					stockN = "完売";
				} else if ("在庫あり".equals(stockN)) {
					stock.isStock = true;
				} else if ("直営店在庫あり".equals(stockN)) {
					stock.isStock = true;
				} else if ("入荷待ち（受付可）".equals(stockN)) {
					stock.isStock = true;
				}
				stock.stockStatus = stockN;
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
		if(stockList.isEmpty()){
	        Elements colors = mainRightEle.select("div#size_").select("table.dataTbl")
	                .select("tr");
	        for (int i = 1; i < colors.size(); i++) {
				StockObject stock = new StockObject();
	        	String colorName = colors.get(i).select("p.colorName").text().toLowerCase();
	            if("－".equals(colorName))continue;
	        	String stockN = colors.get(i).select("p.sell").text().toLowerCase();
				stock.stockStatus = stockN;
				stockList.add(stock);
				stock.priceJPY = price;
				stock.colorName = colorName;
				stock.sizeName = MontBellUtil.sizeNameDefault;
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

	public void processByTaobaoOrderProduct() throws Exception {

		List<TaobaoOrderProductInfo> productInfos = Lists.newArrayList();
		List<String> targets = Files.readLines(
				new File(MontBellUtil.rootPathName, inFileName),
				Charset.forName("UTF-8"));
		for (String target : targets) {
			if (target.startsWith("#")) {
				continue;
			}
			String itemSplitter = ",";
			String[] pis = target.split(itemSplitter);
			for (String line : pis) {
				TaobaoOrderProductInfo pinfo = MontBellUtil.readTaobaoProductInfo(line);

				productInfos.add(pinfo);
			}
		}
		processByTaobaoOrderProduct(productInfos);
	}

	private void processByTaobaoOrderProduct(
			List<TaobaoOrderProductInfo> productInfos) throws Exception {
		String fmt =  "[PRODUCT:%s][PRICE:%6s][COLOR:%-6s][SIZE:%-6s][STOCK:%s]";
		String fmt2 = "[PRODUCT:%s][PRICE:%6s][SIZES:%-6s][COLOR:%-6s][STOCK:%s]";
		String fmt3 = "[PRODUCT:%s][PRICE:%6s][COLOR:%-6s][STOCK:%s]";
		String fmt4 = "[PRODUCT:%s][PRICE:%6s][SIZE:%-6s][STOCK:%s]";
		List<StockObject> stocks = Lists.newArrayList();
		List<String> productIds = Lists.newArrayList();
		for (TaobaoOrderProductInfo pinfo : productInfos) {
			String productId = pinfo.productId;
			if(productIds.contains(productId)){
				continue;
			}
			productIds.add(productId);
			List<StockObject> stockListMontbell = getMontbellStockInfo(productId);
			Collections.sort(stockListMontbell, new Comparator<StockObject>() {
				@Override
				public int compare(StockObject arg0, StockObject arg1) {
					if(arg0.productId.compareTo(arg1.productId) !=0){
						return arg0.productId.compareTo(arg1.productId);
					}
					return arg0.colorName.compareTo(arg1.colorName);
				}
			});
			stocks.addAll(stockListMontbell);
		}
		List<String> stockLines = Lists.newArrayList();
		for (TaobaoOrderProductInfo pinfo : productInfos) {
			String productId = pinfo.productId;
			for (StockObject st : stocks) {
				if (!productId.equals(st.productId)) {
					continue;
				}
				if (!StringUtil.isBlank(pinfo.colorName)
						|| !StringUtil.isBlank(pinfo.sizeName)) {
					if (!StringUtil.isBlank(pinfo.colorName)
							&& !StringUtil.isBlank(pinfo.sizeName)
							&& st.colorName.equals(pinfo.colorName)
							&& st.sizeName.equals(pinfo.sizeName)) {
						String line = String.format(fmt, productId,
								st.priceJPY, st.colorName, st.sizeName,
								st.stockStatus);
						stockLines.add(line);
						System.out.println(line);
						continue;
					}
					if (!StringUtil.isBlank(pinfo.colorName)
							&& StringUtil.isBlank(pinfo.sizeName)
							&& st.colorName.equals(pinfo.colorName)) {
						String line = String.format(fmt3, productId,
								st.priceJPY, st.colorName, st.stockStatus);
						stockLines.add(line);
						System.out.println(line);
						continue;
					}
					if (StringUtil.isBlank(pinfo.colorName)
							&& !StringUtil.isBlank(pinfo.sizeName)
							&& st.sizeName.equals(pinfo.sizeName)) {
						String line = String.format(fmt4, productId,
								st.priceJPY, st.sizeName, st.stockStatus);
						stockLines.add(line);
						System.out.println(line);
						continue;
					}
				} else {

					String line = String.format(fmt, productId, st.priceJPY,
							st.colorName, st.sizeName, st.stockStatus);
					stockLines.add(line);
					System.out.println(line);
					if (stocks.get(0).sizeName != null
							&& !"".equals(stocks.get(0).sizeName)) {
						Collections.sort(stocks, new Comparator<StockObject>() {
							@Override
							public int compare(StockObject arg0,
									StockObject arg1) {
								return arg0.sizeName.compareTo(arg1.sizeName);
							}
						});
						String line2 = String.format(fmt2, productId,
								st.priceJPY, st.sizeName, st.colorName,
								st.stockStatus);
						stockLines.add(line2);
						System.out.println(line2);
					}
				}
			}
		}
		File oFile = new File(MontBellUtil.rootPathName,outFileName);
		NieUtil.appendToFile(oFile,stockLines);
	}

	public void processForWebService() throws Exception {
		StockObject stockInfo = readInStockInfoFromWebService();
		if (stockInfo == null)
			return;
		List<StockObject> stockListMontbell = getMontbellStockInfo(stockInfo.productId);
		for (StockObject st : stockListMontbell) {

			if (!StringUtil.isBlank(stockInfo.colorName) && stockInfo.colorName.equalsIgnoreCase(st.colorName)
					&& !StringUtil.isBlank(stockInfo.sizeName) && stockInfo.sizeName.equalsIgnoreCase(st.sizeName)) {
				stockInfo.priceJPY = st.priceJPY;
				stockInfo.stockStatus = st.stockStatus;
				break;
			}
			if (!StringUtil.isBlank(stockInfo.colorName) && stockInfo.colorName.equalsIgnoreCase(st.colorName)
					&& StringUtil.isBlank(stockInfo.sizeName)) {
				stockInfo.priceJPY = st.priceJPY;
				stockInfo.stockStatus = st.stockStatus;
				break;
			}
			if (StringUtil.isBlank(stockInfo.colorName)
					&& !StringUtil.isBlank(stockInfo.sizeName) && stockInfo.sizeName.equalsIgnoreCase(st.sizeName)) {
				stockInfo.priceJPY = st.priceJPY;
				stockInfo.stockStatus = st.stockStatus;
				break;
			}
			if (StringUtil.isBlank(stockInfo.colorName)
					&& StringUtil.isBlank(stockInfo.sizeName)) {
				stockInfo.priceJPY = st.priceJPY;
				stockInfo.stockStatus = st.stockStatus;
				break;
			}
		}
		if (StringUtil.isBlank(stockInfo.priceJPY)) {
			stockInfo.priceJPY = "-999";
			stockInfo.stockStatus = "断货";
			//System.out.println("[ERROR]failure to get price! productId=" + stockInfo.productId);
			//return;
		}
		reactStockResultToWebServer(stockInfo);
		
	}

	private void reactStockResultToWebServer(StockObject stockInfo) {
		Map<String,String> param = Maps.newHashMap();
		param.put("action", "updateProductInfoByStock");
		param.put("uid", stockInfo.uid);
		param.put("priceOffTax", stockInfo.priceJPY);
		if (!StringUtil.isBlank(stockInfo.stockStatus)) {
			param.put("stock", stockInfo.stockStatus);
		}
		// /myphp/mymontb/action.php?action=updateMBOrderNo
		NieUtil.httpGet(NieConfig.getConfig("montbell.orderforChina.orderInfo.react.url"), param);
	}

	private StockObject readInStockInfoFromWebService() {
		Map<String,String> param = Maps.newHashMap();
		param.put("action", "listProductInfoByEmptyPriceOne");
		// /myphp/mymontb/action.php?action=listOrderByEmptyMBOrderOne
		String orderLine = NieUtil.httpGet(NieConfig.getConfig("montbell.orderforChina.orderInfo.get.url"), param);
		if(StringUtil.isBlank(orderLine)){
			//System.out.println("[INFO]Nothing to stock");
			return null;
		}
		if(orderLine.indexOf("ERROR") != -1){
			System.out.println("[ERROR]" + orderLine);
			return null;
		}
		Json j = new Json();
		Map<String,Object> objMap = null ;
		try{
			objMap = j.toType(orderLine, Map.class);
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}

		StockObject obj = new StockObject();
		obj.uid = (String)objMap.get("uid");
		String pid = (String)objMap.get("productId");
			String[] newP = pid.split("-");
			if (newP.length > 2) {
				pid = newP[2];
			}else if (newP.length > 1) {
				pid = newP[1];
			} else {
				pid = newP[0];
			}
		obj.productId = pid;
		obj.colorName = (String)objMap.get("colorName");
		obj.sizeName = (String)objMap.get("sizeName");
		return obj;
	}
}
