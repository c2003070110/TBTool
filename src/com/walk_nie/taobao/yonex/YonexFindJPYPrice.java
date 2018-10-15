package com.walk_nie.taobao.yonex.badminton;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.Files;
import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexFindJPYPrice  {

	public static void main(String[] args) throws ClientProtocolException,
			IOException {
		String productFile = "res/YonexProduct.txt";
		
		String url = "http://search.rakuten.co.jp/search/inshop-mall/%s/-/sid.207677-st.A";
		String outFmt = "%s\t%s\t%s\t%s\t";
		List<String> productList = Files.readLines(new File(productFile), Charset.forName("UTF-8"));
		StringBuffer sb = new StringBuffer();
		for(String line : productList){
			String[] spl = line.split("\t");
			Document doc = null;
			try {
				doc = TaobaoUtil.urlToDocumentByUTF8(String.format(url, spl[1]));
			} catch (Exception e) {
				String rslt = String.format(outFmt, spl[0], spl[1], "URL失败", "");
				sb.append(rslt).append("\n");
				continue;
			}
			Elements trs = doc.select("div").select("#tableSarch").select("table").select("tr");
			if(trs.isEmpty()){
				String rslt = String.format(outFmt, spl[0], spl[1], "无售", "");
				sb.append(rslt).append("\n");
				continue;
			}
			String title = "";
			String price = "";
			for(int i=1;i<trs.size();i++){
				Element tr = trs.get(i);
				Elements tds = tr.select("td");
				if(tds.size()<6)continue;
				title = title + "|" + tds.get(3).text();
				price = price + "|" + tds.get(4).text();
			}
			title = replace(title);
			price = replace(price);
			String rslt = String.format(outFmt, spl[0], spl[1] ,title,price);
			//System.out.println(rslt);
			sb.append(rslt).append("\n");
		}
		Files.write(sb.toString(), new File("res/YonexJPYPrice.txt"), Charset.forName("UTF-8"));
		
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
