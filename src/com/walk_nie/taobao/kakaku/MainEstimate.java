package com.walk_nie.taobao.kakaku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;

import com.walk_nie.taobao.kakaku.taobao.KakakuTaobaoPriceObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MainEstimate {

	// private CloseableHttpClient client = null;
	
	String outputTitle = ""
			+ "淘宝1\t淘宝2\t淘宝3\t淘宝4\t淘宝5"
			+  "\tkakakuId\t品牌\t型号"
	        +   "\tY价\tM价\tEMS"
			+ "\t淘宝标题";

	private String itemsFile = "in/item_id_estimate.txt";
	private String outputFile = "out/estimate_%s.txt";
	private String split = "\t";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainEstimate().process();
		System.exit(0);
	}

	public MainEstimate() {
	}

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			init();

			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFilePathPrice), "UTF-8"));
			priceBw.write(outputTitle);

			List<KakakuObject> itemIdList = readIn();

			for (KakakuObject obj : itemIdList) {
				String url = KakakuUtil.kakakuUrlPrefix + obj.id;
				parseItem(url, obj);
				writeOut(priceBw, obj);
			}


			System.out.println("-------- FINISH--------");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (priceBw != null)
				try {
					priceBw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private void writeOut(BufferedWriter priceBw, KakakuObject item)
			throws IOException, FileNotFoundException {
		priceBw.write(composeWriteLine(item));
		priceBw.flush();
	}

	private void composeTaobaoPrice(StringBuffer sb,KakakuObject item) {

		if (item.taobaoList.size() > 0) {
			sb.append(item.taobaoList.get(0).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 1) {
			sb.append(item.taobaoList.get(1).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 2) {
			sb.append(item.taobaoList.get(2).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 3) {
			sb.append(item.taobaoList.get(3).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 4) {
			sb.append(item.taobaoList.get(4).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		/*
		if (item.taobaoList.size() > 5) {
			sb.append(item.taobaoList.get(5).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 6) {
			sb.append(item.taobaoList.get(6).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 7) {
			sb.append(item.taobaoList.get(7).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 8) {
			sb.append(item.taobaoList.get(8).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		if (item.taobaoList.size() > 9) {
			sb.append(item.taobaoList.get(9).toPrice()).append(split);
		} else {
			sb.append("").append(split);
		}
		*/
	}
	

	private String composeWriteLine(KakakuObject item) {

		StringBuffer sb = new StringBuffer();
		
		composeTaobaoPrice(sb,item);
		
		sb.append(item.id).append(split);
		sb.append(item.itemMaker).append(split);
		sb.append(item.itemType).append(split);

		if(item.priceYodobashi !=null){
			sb.append(item.priceYodobashi.price).append(split);
		}else{
			sb.append("YNoSell").append(split);
		}
		sb.append(item.priceMin.price).append(split);
		//sb.append(item.priceGap).append(split);
		int emsFee = 99999;
		if(item.spec.weigth != 0){
			emsFee = TaobaoUtil.getEmsFee(item.spec.weigth + 100);
		}
		sb.append(emsFee).append(split);

		for (KakakuTaobaoPriceObject taobao : item.taobaoList) {
			sb.append(taobao.title).append("|");
		}
		sb.append(split);
		sb.append(item.sellStartDate).append(split);

		sb.append(split);

		return sb.append("\n").toString();
	}

	private List<KakakuObject> readIn() throws IOException {

		File file = new File(itemsFile);
		List<KakakuObject> itemIdList = new ArrayList<KakakuObject>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					KakakuObject obj = new KakakuObject();
					obj.id = str;
					itemIdList.add(obj);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		return itemIdList;
	}

	private void parseItem(String itemUrl, KakakuObject obj) throws Exception {

		Document doc = KakakuUtil.urlToDocumentKakaku(itemUrl);

		KakakuUtil.parseItemMaker(doc, obj);

		KakakuUtil.parseItemPrice(doc, obj);

		KakakuUtil.parseItemPicture(doc, obj);
		
		//parseItemSpec(doc, obj);

		//Util.parseItemVariation(doc, obj);

		KakakuUtil.addTaobaoInfo(obj);
	}
 

	private void init() {
	} 
}
