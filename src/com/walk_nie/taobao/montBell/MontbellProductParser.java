package com.walk_nie.taobao.montBell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellProductParser extends BaseBaobeiParser {
	public boolean scanFOFlag = true;

	List<SizeTipObject> sizeTipList = null;
	Map<String, String> enTitleMap = Maps.newHashMap();

	public List<GoodsObject> scanItem(List<String> categoryIds)
			throws IOException {
		List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
		for (String categoryId : categoryIds) {
			if (StringUtil.isBlank(categoryId))
				continue;
			CategoryObject categoryObj = new CategoryObject();
			categoryObj.categoryId = categoryId;
			scanEnglishByCategory(categoryObj);
		}

		for (String categoryId : categoryIds) {
			if (StringUtil.isBlank(categoryId))
				continue;
			CategoryObject categoryObj = new CategoryObject();
			categoryObj.categoryId = categoryId;
			scanItemByCategory(goodsList, categoryObj);
		}

		List<GoodsObject> filteredProdList = filter(goodsList);

		for (GoodsObject goodsObj : filteredProdList) {
			scanSingleItem(goodsObj);
		}

		// translate(filteredProdList);

		saveSizeTip(filteredProdList);

		return filteredProdList;
	}

	public void scanSingleItem(GoodsObject goodsObj) throws IOException {

		String url = MontBellUtil.productUrlPrefix + goodsObj.productId;
		scanSingleItem(goodsObj, url);
		if (scanFOFlag) {
			url = MontBellUtil.productUrlPrefix_fo + goodsObj.productId;
			scanSingleItem(goodsObj, url);
		}
	}

	public void scanSingleItem(GoodsObject goodsObj, String url)
			throws IOException {
		Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
		if (doc.select("div.rightCont").size() == 0) {
			System.err.println("[ERROR] This is NOT product URL." + url);
			return;
		}
		Element mainRightEle = doc.select("div.rightCont").get(0);

		scanSingleItemForSize(goodsObj, mainRightEle);
		scanSingleItemForColor(goodsObj, mainRightEle);
		scanSingleItemForDetailInfo(goodsObj, doc);

		screenshotProductDetailDesp(goodsObj);
		processProductSizeTable(goodsObj, mainRightEle);

		Element mainLeftEle = doc.select("div.leftCont").get(0);

		processProductDressOnPicture(goodsObj, mainLeftEle);
	}

	private void scanSingleItemForDetailInfo(GoodsObject goodsObj, Document doc) {

		Elements els = doc.select("div#contents").select("div.type01");
		for (int j = 0; j < els.size(); j++) {
			Element el = els.get(j);
			String h4Text = el.select("h4").text();
			if ("仕様".equals(h4Text)) {
				String ptext = el.select("p").text();
				boolean brkFlg = true;
				while (brkFlg) {
					int i1 = ptext.indexOf("【");
					ptext = ptext.substring(i1 + 1);
					int i2 = ptext.indexOf("】");
					String key = ptext.substring(i1, i2);
					ptext = ptext.substring(i2 + 1);
					i1 = ptext.indexOf("【");
					String val = "";
					if (i1 == -1) {
						val = ptext;
						brkFlg = false;
					} else {
						val = ptext.substring(0, i1);
						ptext = ptext.substring(i1);
					}
					if ("素材".equals(key)) {
						goodsObj.materialOrg = val;
					} else if ("カラー".equals(key)) {
						goodsObj.colorOrg = val;
					} else if ("サイズ".equals(key)) {
						goodsObj.sizeOrg = val;
					} else if ("特長".equals(key)) {
						goodsObj.specialOrg = val;
					} else if ("機能".equals(key)) {
						goodsObj.functionOrg = val;
					}
				}
			}
			if ("機能".equals(h4Text)) {
				Elements elss = el.select("p").select("img");
				for (int i = 0; i < elss.size(); i++) {
					goodsObj.functionOrg = elss.attr("alt") + ";";
				}
			}
		}

	}

	private void scanSingleItemForColor(GoodsObject goodsObj,
			Element mainRightEle) {
		boolean hasSize = !goodsObj.sizeList.isEmpty();
		Elements colors = mainRightEle.select("table.dataTbl").select("tr");
		for (int j = 1; j < colors.size(); j++) {
			Element color = colors.get(j);
			String colorName = color.select("p.colorName").text();
			if ("－".equals(colorName))
				continue;
			if (!goodsObj.colorList.contains(colorName)) {
				goodsObj.colorList.add(colorName);
			}
			if (!hasSize) {
				StockObject stock = new StockObject();
				stock.colorName = colorName;
				stock.sizeName = null;
				String stockN = color.select("p.sell").text();
				if ("在庫あり".equals(stockN)) {
					stock.isStock = true;
				}
				if ("直営店在庫あり".equals(stockN)) {
					stock.isStock = true;
				}
				goodsObj.stockList.add(stock);
			}
		}
	}

	private void scanSingleItemForSize(GoodsObject goodsObj,
			Element mainRightEle) {

		Elements sizes = mainRightEle.select("div.sizeChoice").select("select")
				.select("option");

		for (int i = 1; i < sizes.size(); i++) {
			String zie = sizes.get(i).text();
			if (!goodsObj.sizeList.contains(zie)) {
				goodsObj.sizeList.add(zie);
			}
			try {
				Elements divs = mainRightEle.select("div");
				Element colorEle = null;
				for (Element div : divs) {
					if (("size_" + zie).equals(div.attr("id"))) {
						colorEle = div;
						break;
					}
				}
				Elements colors = colorEle.select("table.dataTbl").select("tr");
				// Elements colors = mainRightEle.select("div#size_" + zie)
				// .select("table.dataTbl").select("tr");
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
					goodsObj.stockList.add(stock);
				}
			} catch (Exception ex) {
				System.err.println("[ERROR]product Id = " + goodsObj.productId
						+ " Size = " + zie);
				continue;
			}
		}
	}

	private void processProductDressOnPicture(GoodsObject goodsObj,
			Element mainLeftEle) throws ClientProtocolException, IOException {
		Elements hiddEles = mainLeftEle.select("div.img_hidden");
		if (hiddEles.isEmpty())
			return;
		Elements aEles = mainLeftEle.select("a.fancy_largelink");
		if (aEles.isEmpty())
			return;
		String picRoot = MontBellUtil.rootPathName + "/dressOn/"
				+ goodsObj.cateogryObj.categoryId;
		String fileNameFmt = "dressOn_%s_%d";
		int i = 0;
		for (Element a : aEles) {
			String url = a.attr("href");
			if (url.endsWith("no_image.gif"))
				continue;
			if (!url.startsWith("http")) {
				url = "http://webshop.montbell.jp" + url;
			}
			i++;
			String picName = String
					.format(fileNameFmt, goodsObj.productId, i++);
			try {
				File picFile = TaobaoUtil.downloadPicture(picRoot, url,
						picName, true);
				if (!goodsObj.dressOnPics.contains(picFile.getAbsolutePath())) {
					goodsObj.dressOnPics.add(picFile.getAbsolutePath());
				}
			} catch (Exception ex) {
				System.err.println("[ERROR]Download picture URL = " + url);
			}
		}

	}

	protected void screenshotProductDetailDesp(GoodsObject goodsObj)
			throws ClientProtocolException, IOException {
		String fileNameFmt = "detail_%s.png";
		String fileName = String.format(fileNameFmt, goodsObj.productId);
		File despFile = new File(MontBellUtil.rootPathName + "/"
				+ goodsObj.cateogryObj.categoryId, fileName);
		if (!despFile.exists()) {
			String url = MontBellUtil.productUrlPrefix + goodsObj.productId;
			WebDriver webDriver = WebDriverUtil.getWebDriver(url);
			// List<WebElement> ele =
			// webDriver.findElements(By.className("ttlType02"));
			List<WebElement> ele = webDriver.findElements(By
					.className("column1"));
			if (!ele.isEmpty()) {
				WebDriverUtil.screenShot(webDriver, ele,
						despFile.getAbsolutePath());
			}
		}
		goodsObj.detailScreenShotPicFile = despFile.getAbsolutePath();
	}

	protected void processProductSizeTable(GoodsObject goodsObj, Element rootEl)
			throws ClientProtocolException, IOException {
		List<SizeTipObject> sizeTips = getExistedSizeTips(goodsObj);
		if (sizeTips != null && !sizeTips.isEmpty()) {
			for (SizeTipObject sizeTip : sizeTips) {
				if (!goodsObj.sizeTipPics.contains(sizeTip.pictureFileName))
					goodsObj.sizeTipPics.add(sizeTip.pictureFileName);
			}
			return;
		}
		File rootPath = new File(MontBellUtil.rootPathName + "/sizeTable/"
				+ goodsObj.cateogryObj.categoryId);
		String fileNameFmt = "sizeTable_%s_%d";
		try {
			// Elements aboutSize =
			// rootEl.select("p.aboutSize").select("select").select("option");
			// Elements sizeA = aboutSize.select("a");
			// if (sizeA == null || sizeA.isEmpty()) {
			// return ;
			// }
			String sizeUrl = MontBellUtil.productSizeUrlPrefix
					+ goodsObj.productId;
			Document docSize = TaobaoUtil.urlToDocumentByUTF8(sizeUrl);
			Elements sizePics = docSize.select("div.innerCont").select("img");
			int i = 0;
			for (Element sizePic : sizePics) {
				String url = MontBellUtil.urlPrefix + sizePic.attr("src");
				String picName = String.format(fileNameFmt, goodsObj.productId,
						i++);

				File picFile = TaobaoUtil.downloadPicture(
						rootPath.getAbsolutePath(), url, picName);
				goodsObj.sizeTipPics.add(picFile.getAbsolutePath());

				SizeTipObject obj = new SizeTipObject();
				obj.productId = goodsObj.productId;
				obj.pictureFileName = picFile.getCanonicalPath();
				obj.pictureUrl = url;
				sizeTipList.add(obj);
			}
		} catch (Exception ex) {

		}
	}

	private List<SizeTipObject> getExistedSizeTips(GoodsObject goodsObj)
			throws IOException {
		if (sizeTipList == null) {
			sizeTipList = Lists.newArrayList();
			File file = new File(MontBellUtil.rootPathName,
					MontBellUtil.sizeTipFileName);
			if (!file.exists())
				return null;
			List<String> lines = Files
					.readLines(file, Charset.forName("UTF-8"));
			for (String line : lines) {
				String[] splits = line.split("\t");
				SizeTipObject obj = new SizeTipObject();
				obj.productId = splits[0];
				obj.pictureFileName = splits[1];
				obj.pictureUrl = splits[2];
				sizeTipList.add(obj);
			}
		}
		List<SizeTipObject> rtnList = Lists.newArrayList();
		for (SizeTipObject tip : sizeTipList) {
			if (tip.productId.equals(goodsObj.productId)) {
				rtnList.add(tip);
			}
		}
		return rtnList;
	}

	private void saveSizeTip(List<GoodsObject> filteredProdList)
			throws IOException {
		if (sizeTipList == null) {
			return;
		}
		Collections.sort(sizeTipList, new Comparator<SizeTipObject>() {

			@Override
			public int compare(SizeTipObject o1, SizeTipObject o2) {
				return o1.productId.compareTo(o2.productId);
			}
		});
		BufferedWriter priceBw = null;
		try {
			File file = new File(MontBellUtil.rootPathName,
					MontBellUtil.sizeTipFileName);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			for (SizeTipObject obj : sizeTipList) {
				String line = obj.productId + "\t" + obj.pictureFileName + "\t"
						+ obj.pictureUrl + "\n";
				priceBw.write(line);
			}
			priceBw.flush();
		} finally {

			if (priceBw != null) {
				try {
					priceBw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<GoodsObject> filter(List<GoodsObject> prodList) {
		List<GoodsObject> filterdList = Lists.newArrayList();
		List<String> productId = Lists.newArrayList();
		productId.add("1128587");
		productId.add("1128516");
		productId.add("1128508");
		productId.add("1128509");

		productId.add("1129386");
		// productId.add("");

		productId.add("1108723");
		for (GoodsObject prod : prodList) {
			if (!productId.contains(prod.productId)
			// && MontBellUtil.getPublishedBaobei(prod,
			// this.publishedbaobeiList) != null) {
					&& MontBellUtil.getPublishedBaobei(prod,
							this.publishedbaobeiList) == null) {
				productId.add(prod.productId);
				filterdList.add(prod);
			}
		}
		return filterdList;
	}

	// private void translate(List<GoodsObject> prodList) {
	// for(GoodsObject prod :prodList){
	// prod.titleCN = translateTitle(prod);

	// (price + emsfee)*rate + benefit
	// prod.priceCNY = convertToCNY(prod);
	// System.out.println(""+prod.titleCN +":" + prod.titleOrg);

	// }
	// }

	private void scanEnglishByCategory(CategoryObject categoryObj)
			throws ClientProtocolException, IOException {
		String cateogryUrl = MontBellUtil.categoryUrlPrefix_fo_en
				+ categoryObj.categoryId;
		Document doc = TaobaoUtil.urlToDocumentByUTF8(cateogryUrl);
		scanEnTitleByCategory(doc, categoryObj);

		cateogryUrl = MontBellUtil.categoryUrlPrefix_en
				+ categoryObj.categoryId;
		doc = TaobaoUtil.urlToDocumentByUTF8(cateogryUrl);
		scanEnTitleByCategory(doc, categoryObj);
		Elements pages = doc.select("div.resultArea").select("div.leftArea")
				.select("p");
		int minP = 3;
		if (pages.size() > minP) {
			for (int i = minP; i < pages.size() - 1; i++) {
				String pagedCategoryUrl = cateogryUrl + "&page=" + (i - 1);
				Document docPage = TaobaoUtil
						.urlToDocumentByUTF8(pagedCategoryUrl);
				scanEnTitleByCategory(docPage, categoryObj);
			}
		}
	}

	private void scanEnTitleByCategory(Document doc, CategoryObject categoryObj) {

		Elements goods = doc.select("div.unit");
		for (Element goodsElement : goods) {
			Elements ttl = goodsElement.select(".ttlType03");
			String title = translateTitle(ttl.text());
			Elements desp = goodsElement.select(".description").select("p");
			String productId = desp.get(1).text().trim().replace("No. #", "");
			enTitleMap.put(productId, title.trim());
		}
	}

	public void scanItemByCategory(List<GoodsObject> goodsList,
			CategoryObject categoryObj) throws IOException {
		String cateogryUrl = MontBellUtil.categoryUrlPrefix
				+ categoryObj.categoryId;
		scanItemByCategory(goodsList, categoryObj, cateogryUrl);
		// outlet
		if (scanFOFlag) {
			cateogryUrl = MontBellUtil.categoryUrlPrefix_fo
					+ categoryObj.categoryId;
			scanItemByCategory(goodsList, categoryObj, cateogryUrl);
		}
	}

	protected void scanItemByCategory(List<GoodsObject> goodsList,
			CategoryObject category, String cateogryUrl) throws IOException {
		Document doc = TaobaoUtil.urlToDocumentByUTF8(cateogryUrl);
		scanItemByCategory(doc, goodsList, category);
		Elements pages = doc.select("div.resultArea").select("div.leftArea")
				.select("p");
		int minP = 3;
		if (pages.size() > minP) {
			for (int i = minP; i < pages.size() - 1; i++) {
				String pagedCategoryUrl = cateogryUrl + "&page=" + (i - 1);
				Document docPage = TaobaoUtil
						.urlToDocumentByUTF8(pagedCategoryUrl);
				scanItemByCategory(docPage, goodsList, category);
			}
		}
	}

	protected void scanItemByCategory(Document doc,
			List<GoodsObject> goodsList, CategoryObject category)
			throws IOException {
		Elements goods = doc.select("div.unit");
		for (Element goodsElement : goods) {
			GoodsObject goodsObj = new GoodsObject();
			goodsObj.cateogryObj = category;

			Elements ttl = goodsElement.select(".ttlType03");
			goodsObj.titleOrg = ttl.text();
			goodsObj.titleJP = translateTitle(ttl.text());

			// goodsObj.goodTitleCN = translateTitle(goodsObj, ttl.text());

			String gender = ttl.select("img").attr("alt");
			goodsObj.genderOrg = gender;
			goodsObj.gender = translateGender(gender);

			Elements desp = goodsElement.select(".description").select("p");
			goodsObj.priceOrg = desp.get(0).text();
			scanItemForPriceJPY(goodsObj);

			String productId = desp.get(1).text().trim().replace("品番", "");
			goodsObj.productId = productId.replace("#", "").trim();

			goodsObj.titleEn = this.enTitleMap.get(goodsObj.productId);

			goodsObj.brand = desp.get(2).text().replace("ブランド", "").trim();
			if (!goodsObj.brand.equals("モンベル")) {
				continue;
			}

			String weight = desp.get(3).text();
			scanItemForWeight(goodsObj, weight);

			// scanItemForSize(goodsObj, goodsElement);

			// scanItemForColor(goodsObj, goodsElement);

			if (!isScanReadyProduct(goodsList, goodsObj)) {
				addProduct(goodsList, goodsObj);
			}
		}
	}

	private String translateTitle(String ttl) {
		String title = ttl.replaceAll(" W'S", "");
		title = title.replaceAll(" M'S", "");
		title = title.replaceAll(" K'S", "");
		title = title.replaceAll(" MEN'S", "");
		title = title.replaceAll(" WOMEN'S", "");
		title = title.replaceAll(" KID'S", "");
		title = title.replaceAll(" FLEECE", "");
		title = title.replaceAll(" JACKET", "茄克");
		title = title.replaceAll(" SHIRT", "T恤");
		title = title.replaceAll(" US", "");
		title = title.replaceAll(" Men's", "");
		title = title.replaceAll(" Women's", "");
		title = title.replaceAll(" Kid's", "");
		title = title.replaceAll(" Baby's", "");
		title = title.replaceAll(" 100-120", "1");
		title = title.replaceAll(" 130-160", "2");
		title = title.replaceAll("U.L.", "");
		title = title.replaceAll("US", "");
		title = title.replaceAll(" Pants", "");
		title = title.replaceAll(" PANTS", "");
		title = title.replaceAll("Lining", "");
		title = title.replaceAll(" LINING", "");
		//title = title.replaceAll(" ", "");
		title = title.trim();
		return title;
	}

	private String translateGender(String gender) {
		if ("Unisex".equals(gender)) {
			return "";
			// return "男女通用";
		} else if ("Men".equals(gender)) {
			// return "男士";
			return "男";
		} else if ("Women".equals(gender)) {
			// return "女士";
			return "女";
		} else if ("Child".equals(gender)) {
			return "儿童";
		} else {
			return "";
		}
	}

	private void addProduct(List<GoodsObject> goodsList, GoodsObject goodsObj) {
		goodsList.add(goodsObj);
	}

	private boolean isScanReadyProduct(List<GoodsObject> goodsList,
			GoodsObject goodsObj) {
		return findGoodsObject(goodsList, goodsObj.productId) == null ? false
				: true;
	}

	protected GoodsObject findGoodsObject(List<GoodsObject> goodsList,
			String productId) {
		for (GoodsObject goods : goodsList) {
			if (goods.productId.equals(productId)) {
				return goods;
			}
		}
		return null;
	}

	private void scanItemForWeight(GoodsObject goodsObj, String weight) {
		goodsObj.weightOrg = weight;
		int doubleFl = 1;
		if (weight.indexOf("片足") != -1) {
			doubleFl = 2;
		}
		if (weight.indexOf("（") != -1) {
			weight = weight.substring(0, weight.indexOf("（"));
		}

		weight = weight.replace("【重量】", "");
		weight = weight.replace("【平均重量】", "");
		weight = weight.replace("【総重量】", "");
		weight = weight.replace("【本体重量】", "");
		weight = weight.replace(",", "");
		if (weight.indexOf("kg") > 0) {
			weight = weight.substring(0, weight.indexOf("kg"));
			weight = weight.replace(",", "");
			BigDecimal b = new BigDecimal(weight);
			weight = String
					.valueOf(b.multiply(new BigDecimal(1000)).intValue());
		}
		if (weight.indexOf("g") > 0) {
			weight = weight.substring(0, weight.indexOf("g"));
			weight = weight.replace(",", "");
		}
		try {
			goodsObj.weight = new BigDecimal(weight).intValue() * doubleFl;
		} catch (Exception ex) {
			System.err.println("[ERROR]parse weight!category="
					+ goodsObj.cateogryObj.categoryId + " product="
					+ goodsObj.productId);
			goodsObj.weight = 500;
		}
		goodsObj.weightExtra = 150;
	}

	private void scanItemForPriceJPY(GoodsObject goodsObj) {
		String price = goodsObj.priceOrg.replace("価格 ¥", "");
		price = price.replace(" +税", "");
		price = price.replace("アウトレット", "");
		price = price.replace(",", "");
		if (price.indexOf("～") > 0) {
			price = price.substring(price.indexOf("～") + 1);
		}
		goodsObj.priceJPY = price;
	}

	// private void scanItemForSize(GoodsObject goodsObj, Element good) {
	//
	// List<String> rslt = new ArrayList<String>();
	// Elements size = good.select(".spec").select(".size").select("p");
	// if (size.isEmpty() || size.size() == 1) {
	// rslt.add("-");
	// goodsObj.sizeList = rslt;
	// return;
	// }
	// String sizeS = size.get(1).text();
	// String tmp[] = sizeS.split(" ");
	// for (String str : tmp) {
	// str = str.replace("/", "");
	// str = str.replace(" ", "");
	// rslt.add(str);
	// }
	// goodsObj.sizeList = rslt;
	// }
	//
	// private void scanItemForColor(GoodsObject goodsObj, Element good) {
	//
	// List<String> rslt = new ArrayList<String>();
	// Elements color = good.select(".spec").select(".color").select("p");
	// if (color.isEmpty() || color.size() == 1) {
	// rslt.add("-");
	// goodsObj.colorList = rslt;
	// return;
	// }
	//
	// //String itemId = goodsObj.productId;
	// for (int i = 0; i < color.size(); i++) {
	// Elements pic = color.get(i).select("img");
	// if (pic == null || pic.isEmpty()) {
	// continue;
	// }
	// rslt.add(pic.get(0).attr("alt"));
	// // String tcolorS = color.get(i).select("a").attr("onclick");
	// // if (tcolorS.indexOf("s_" + itemId) > 0) {
	// // String tc = tcolorS.substring(tcolorS.indexOf("s_" + itemId)
	// // + ("s_" + itemId).length() + 1);
	// // String trueS = tc.substring(0, tc.indexOf("."));
	// // rslt.add(trueS);
	// // } else {
	// // rslt.add("-");
	// // }
	// }
	// goodsObj.colorList = rslt;
	// }

}
