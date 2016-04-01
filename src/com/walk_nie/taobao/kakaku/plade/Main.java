package com.walk_nie.taobao.kakaku.plade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {
	private Log log = LogFactory.getLog(Main.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main().process();
	}

	public void process(){

		BufferedReader br = null;
		BufferedWriter priceBw = null;
		try {

			// 商品のid http://kakaku.com/item/ J0000009614 
			// FileのFormat：　item_id改行item_id改行item_id…
			File file = new File("itemid_list.txt");
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
			List<String> itemIds = new ArrayList<String>();
			String str = null;
			while ((str = br.readLine()) != null) {
				if(!"".equals(str)&& !str.startsWith("#"))
					itemIds.add(str);
			}
			
			// 出力先ファイル名: price_20140315092012
			String outFilePathPrice = String.format("price_%s.txt", DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			priceBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFilePathPrice),"UTF-8"));
			for(String itemId:itemIds){
				
				outputPrice(itemId,priceBw);

			}
			priceBw.flush();
		}catch (Exception ex){
			log.error(ex);
		} finally {
			try {
				if (br != null)
					br.close();
				if (priceBw != null)
					priceBw.close();
			} catch (Exception ex) {
				log.error(ex);
			}
		}
	}

	private void outputPrice(String itemId,
			BufferedWriter pricelistBw)throws Exception {

		String outputFmt ="%s,%s,%s,%s\n";
		
		String url = "http://kakaku.com/item/" + itemId;
		log.info("[START]parse URL =" + url);
		HttpClient client = new DefaultHttpClient();
		HttpUriRequest req = new HttpGet(url);
		HttpResponse res = client.execute(req);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res
				.getEntity().getContent(),"shift_jis"));
		String line = "";
		StringBuffer sbHtml = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			sbHtml.append(line);
			sbHtml.append("\n");
		}
		client.getConnectionManager().closeExpiredConnections();
		client.getConnectionManager().shutdown();
		Document doc = Jsoup.parse(sbHtml.toString());
		log.info("[END]parse URL =" + url);
		
		String minPrice = doc.select("#priceBox").select("#minPrice").select("a").text();
		minPrice = minPrice.replaceAll("¥","");
		minPrice = minPrice.replaceAll(",","");
		String priceUp = doc.select("#priceBox").select(".priceRate").select(".priceUp").text();
		String priceDown = doc.select("#priceBox").select(".priceRate").select(".priceDown").text();
		String priceRate = "".equals(priceUp)?priceDown:priceUp;
		priceRate = priceRate.replaceAll("円","");
		priceRate = priceRate.replaceAll("↓","");
		priceRate = priceRate.replaceAll("↑","");
		priceRate = priceRate.replaceAll(",","");
		pricelistBw.write(String.format(outputFmt, itemId,url,minPrice,priceRate));
	}

}
