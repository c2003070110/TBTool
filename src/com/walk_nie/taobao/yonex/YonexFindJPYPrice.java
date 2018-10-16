package com.walk_nie.taobao.yonex;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.Files;
import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexFindJPYPrice  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		
		String url = "http://search.rakuten.co.jp/search/inshop-mall/%s/-/sid.207677-st.A";
		String outFmt = "%s\t%s\t%s\t%s\t%s";
		List<String> productList = Files.readLines(new File(YonexUtil.productListFile), Charset.forName("UTF-8"));
		StringBuffer sb = new StringBuffer();
		for(String line : productList){
			String[] spl = line.split("\t");
			int prodType = Integer.parseInt(spl[0]);
			Document doc = null;
			try {
				doc = TaobaoUtil.urlToDocumentByUTF8(String.format(url, spl[2]));
			} catch (Exception e) {
				String rslt = String.format(outFmt, spl[1], spl[2], "URL失败", "", "", "");
				sb.append(rslt).append("\n");
				continue;
			}
			Elements rsultItems = doc.select("div.searchresultitem");
			if(rsultItems.isEmpty()){
				String rslt = String.format(outFmt, spl[1], spl[2], "无售", "", "", "");
				sb.append(rslt).append("\n");
				continue;
			}
			String title = rsultItems.get(0).select("div.title").text();
			String price = "";
			for (int i = 0; i < rsultItems.size(); i++) {
				Element tr = rsultItems.get(i);
				Elements priceEl = tr.select("div.price").select("span");
				for(Element el:priceEl){
					String str = el.text();
					if(str.indexOf("円") != 0){
						str = str.replaceAll(",", "");
						str = str.replaceAll("円", "");
						price = price + str + ";";
						break;
					}
				}
			}
			title = replace(title);
			price = replace(price);
			String rslt = String.format(outFmt, spl[1], spl[2] ,title,price,maxPriceCNY(prodType,price));
			//System.out.println(rslt);
			sb.append(rslt).append("\n");
		}
		File out = new File(YonexUtil.priceListFile);
		FileUtils.writeStringToFile(out, sb.toString(),Charset.forName("UTF-8"));
		System.out.println("Saved to File = " + out.getCanonicalPath());
		System.out.println("-------- FINISH--------");
	}

	private static String maxPriceCNY(int prodType, String price) {
		String[] spl = price.split(";");
		int maxPrice = 0;
		for(String s:spl){
			int p = Integer.parseInt(s);
			if(p > maxPrice) maxPrice = p;
		}
		double currency = 0.062 + 0.005;
		double brate = 0.05;
		int emsFee = 0;
		if (prodType == 1) {
			emsFee = 2000;
		} else if (prodType == 2) {
			emsFee = 3300;
		} else if (prodType == 3) {
			emsFee = 2000;
		} else if (prodType == 4) {
			emsFee = 3300;
		}
				
		int rmb = new BigDecimal((maxPrice+emsFee) * (1+brate) * currency).intValue();
		return String.valueOf(rmb);
	}

	private static String replace(String o) {
		String str = o.replaceAll("ポイント10倍", "");
		str = str.replaceAll("送料無料", "");
		str = str.replaceAll("ガット張り無料", "");
		str = str.replaceAll("ガット張り無料", "");
		str = str.replaceAll("【】", "");
		str = str.replaceAll("『即日出荷』", "");
		//str = str.replaceAll("ポイント 10倍", "");
		//str = str.replaceAll("、", "");
		return str;
	}

}
