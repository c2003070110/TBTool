package com.walk_nie.taobao.akasugu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.util.TaobaoUtil;

public class AkasuguProductParser extends BaseBaobeiParser {
	// change me!!
	double rate = 0.080;
	// private final static double benefitRate = 0.001;

	String urlPrefix = "http://akasugu.fcart.jp";

	boolean detailDisp = true;

	public List<AkasuguGoodsObject> scanItem() throws IOException {

		List<AkasuguGoodsObject> goodsList = new ArrayList<AkasuguGoodsObject>();

		String url = "http://akasugu.fcart.jp/eysm00001/?Int_id=image_aka_ysm00001";
		
		Document doc = TaobaoUtil.urlToDocument(url,"shift_jis");
		Elements prodEles = doc.select("div.searchListCassetteWrapper")
				.select("div.item").select("p.name").select("a");

		for (Element prodEle : prodEles) {
			// AkasuguGoodsObject obj = new AkasuguGoodsObject();
			String href = prodEle.attr("href");
//			if (!"/g0R9B550301/".equals(href))
//				continue;
			String pUrl = urlPrefix + href;
			Document pDoc = TaobaoUtil.urlToDocument(pUrl,"shift_jis");
			Elements scriptEles = pDoc.select("script");
			for (Element ele : scriptEles) {
				String str = ele.toString();
				String strDI = "var json = '";
				if (str.indexOf(strDI) > 0) {
					Node node = ele.childNode(0);
					String json = node.toString();
					//System.out.println(json);
					String trueJson = json.substring(strDI.length() + 1,
							json.length() - 3);
					//System.out.println(trueJson);
					List<Map<String, Object>> objs = JSON.decode(trueJson);
					AkasuguGoodsObject newObj = allToOne(objs, pDoc);
					newObj.productId = href.replace("/", "");
					newObj.price = convertToCNY(newObj);
					goodsList.add(newObj);
				}
			}
		}
		return goodsList;
	}

	private String convertToCNY(AkasuguGoodsObject item) {

		String price = item.salePriceIncludedTax;
		if(price == null || "".equals(price)){
			price = item.priceIncludedTax;
		}
		price = price.replace(",", "");
		price = price.replace("￥", "");
		int priceI = Integer.parseInt(price);
		int emsFee = 0;
		double priceCNY = (priceI + emsFee) * rate;
		return String.valueOf(Math.round(priceCNY));
	}

	private AkasuguGoodsObject allToOne(List<Map<String, Object>> objs,
			Document pDoc) {
		AkasuguGoodsObject rslt = new AkasuguGoodsObject();
		Map<String, Object> first = objs.get(0);
		rslt.itemName = (String) first.get("item_name");
		rslt.priceOrg = (String) first.get("price");
		rslt.priceIncludedTax = (String) first.get("price_included_tax");
		rslt.salePrice = (String) first.get("sale_price");
		rslt.salePriceIncludedTax = (String) first
				.get("sale_price_included_tax");

		rslt.sku = (String) first.get("sku");

		List<AkasuguColorObject> colorList = new ArrayList<AkasuguColorObject>();
		List<String> colors = new ArrayList<String>();
		List<String> sizeList = new ArrayList<String>();
		List<String> pictureList = new ArrayList<String>();

		int i = 0;

		for (Map<String, Object> obj : objs) {
			List<Map<String, String>> images = (List<Map<String, String>>) obj
					.get("images");
			String colorCd = (String) obj.get("color_cd");
			if (colors.contains(colorCd)) {
				continue;
			}
			
			AkasuguColorObject colorObj = new AkasuguColorObject();
			colorObj.colorName = "颜色" + i;
			colorObj.colorPicture = findColorPicture(pDoc, colorCd, images);
			colorList.add(colorObj);
			colors.add(colorCd);
			i++;

			String sizeName = (String) obj.get("size_name");
			if (!sizeList.contains(sizeName)) {
				sizeList.add(sizeName);
			}

			for (Map<String, String> pic : images) {
				String img = (String) pic.get("img_900x900");
				if (!pictureList.contains(img)
						&& !isExistInColorPicture(colorList, pic)) {
					pictureList.add(img);
				}
			}
		}
		rslt.colorList = colorList;
		rslt.pictureList = pictureList;
		return rslt;
	}

	private boolean isExistInColorPicture(List<AkasuguColorObject> colorList,
			Map<String, String> pic) {
		for (AkasuguColorObject color : colorList) {
			if (color.colorPicture.equals(pic.get("img_900x900"))) {
				return true;
			}
		}
		return false;
	}

	private String findColorPicture(Document pDoc, String color_cd,
			List<Map<String, String>> images) {

		Elements elements = pDoc.select("ul.thumbList").select("li")
				.select("img");

		String picName = "";
		for (Element el : elements) {
			String colorCd = el.attr("alt").replace("(", "").replace(")", "");
			if (colorCd.equals(color_cd)) {
				String src = el.attr("src");
				picName = src.substring(src.lastIndexOf("/") + 1);
				picName = picName.substring(0, picName.lastIndexOf("_"));
				break;
			}
		}
		for (Map<String, String> pic : images) {
			if (pic.get("img_900x900").indexOf(picName) > 0) {
				return pic.get("img_900x900");
			}
		}
		return "";
	}

}
