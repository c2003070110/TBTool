package com.walk_nie.taobao.kakaku.explore.earphone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MainUpdateBaobei {

	// taobao workbench export file!
	private String baobeiListFile = "in/earphone_updateBaobei.csv";

	private String outputFile = "out/earphone_baobei_%s.csv";
	
	private String recordFile = "out/earphone_record_%s.csv";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new MainUpdateBaobei().process();
	}

	public MainUpdateBaobei() {
	}

	public void process() {
		BufferedReader br = null;
		try {
			System.out.println("-------- START --------");
			init();
			List<BaobeiPublishObject> baobeiList = readIn();
			if(baobeiList == null || baobeiList.isEmpty()){
				System.out.println("[WARN] NO Baobei record to update!");
				return;
			}
			List<String> priceRecords = Lists.newArrayList();
			priceRecords.add("Title,修正前,修正後,１安価,YO価,BIC価,YA価,EE価,EE価");
			for(BaobeiPublishObject baobei:baobeiList){
				update(baobei,priceRecords);
			}

			writeOut(baobeiList,priceRecords);

			destory();

			System.out.println("-------- FINISH--------");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	private void writeOut(List<BaobeiPublishObject> baobeiList,List<String> priceRecords) throws IOException {
		BufferedWriter priceBw = null;
		try {

			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFilePathPrice), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			
			for (BaobeiPublishObject obj : baobeiList) {
				priceBw.write(TaobaoUtil.composeTaobaoLine(obj));
			}
			priceBw.flush();
			priceBw.close();

			String recordFilePath = String.format(recordFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(recordFilePath), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			
			for (String str : priceRecords) {
				priceBw.write(str);
			}
			priceBw.flush();
		} finally {
			if (priceBw != null)
				priceBw.close();
		}
	}

	private void update(BaobeiPublishObject baobei,List<String> priceRecords) throws Exception {

		String kakakuId = baobei.outer_id.replace("\"", "");
		if(StringUtil.isBlank(kakakuId)){
			System.out.println("[WARN][NO kakakuId] title = " + baobei.title);
			return;
		}
		kakakuId = kakakuId.replace("\"", "");
		String kakakuUril =EarphoneUtil.kakakuUrlPrefix + kakakuId;
		Document doc = EarphoneUtil.urlToDocumentKakaku(kakakuUril);
		KakakuObject kakakuObj = new KakakuObject();
		EarphoneUtil.parseItemMaker(doc, kakakuObj);
		EarphoneUtil.parseItemPrice(doc, kakakuObj);

		EarphoneUtil.addTaobaoInfo(kakakuObj);
		
		//Util.parseItemVariation(doc, kakakuObj);
		// "TITLE,修正前,修正後,１安価,YODOBASHI価,BIC価,YAMADA価,EEARPHONE価,AMAZON価"
		String fmt = "%s,%s,%s,%s,%s,%s,%s,%s,%s";
		String oldPrice = baobei.price;
		String newPrice =  EarphoneUtil.convertToCNY(kakakuObj);
		//update price
		baobei.price = newPrice;

		// 宝贝描述
		String miaoshuTemplateFile = "in/earphone_miaoshu_template.html";
		//baobei.description = composeBaobeiMiaoshu(kakakuObj);
		
		//updatePrice(baobei,kakakuObj);
		
		StringBuffer sb = new StringBuffer();
		sb.append(String.format(fmt,
					baobei.title,
					kakakuObj.itemType,
					oldPrice,
					newPrice,
					kakakuObj.priceMin == null ? "NaN" : kakakuObj.priceMin.price,
					kakakuObj.priceYodobashi == null ? "NaN" : kakakuObj.priceYodobashi.price,
					kakakuObj.priceBiccamera == null ? "NaN" : kakakuObj.priceBiccamera.price,
					kakakuObj.priceYamada == null ? "NaN" : kakakuObj.priceYamada.price,
					kakakuObj.priceEEarPhone == null ? "NaN" : kakakuObj.priceEEarPhone.price,
					kakakuObj.priceAmazon == null ? "NaN" : kakakuObj.priceAmazon.price
					));
		sb.append(",");
		sb.append(EarphoneUtil.composeTaobaoPrice(kakakuObj,","));
		
		priceRecords.add(sb.toString());
	}
	/*
	private void updatePrice(BaobeiObject baobei, KakakuObject kakakuObj) {
		baobei.price = Util.convertToCNY(kakakuObj);
		 销售属性廃止
		// 销售属性组合
		String[] props = baobei.skuProps.split(";"); 
		//销售属性别名
		String[] alias = baobei.propAlias.split(";");
		List<String[]> newPropList = Lists.newArrayList();
		// TODO remove&add color
		for(String prop :props){
			String[] child0 = prop.split(":");
			for(String alia : alias){
				String[] child1 = alia.split(":");
				if(child0[3].equals(child1[0]) && child0[4].equals(child1[1])){
					String id = child1[2].substring(child1[2].indexOf("[") +2 ,child1[2].length()-2);
					KakakuObject colorObj = null;
					for(KakakuObject o:kakakuObj.colorList){
						if(o.id.equals(id)){
							colorObj = o;
							break;
						}
					}
					if (colorObj != null) {
						child0[0] = Util.convertToCNY(colorObj);
					}
					break;
				}
			}
			newPropList.add(child0);
		}
		String newProps = "";
		for(String[] prop : newPropList){
			for(String p :prop){
				newProps += p + ":";
			}
			newProps += ";";
		}
		baobei.skuProps = newProps.substring(0,newProps.length()-1);
		
	}*/

	private List<BaobeiPublishObject> readIn() throws IOException {

		List<BaobeiPublishObject> baobeiList = new ArrayList<BaobeiPublishObject>();
		BufferedReader br = null;
		try {
			File file = new File(baobeiListFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					BaobeiPublishObject obj = TaobaoUtil.readBaobeiIn(str);
					if(obj != null){
						baobeiList.add(obj);
					}
				}
			}
		} finally {
			if (br != null)
				br.close();
		}

		return baobeiList;
	}

	private void init() {
	}

	private void destory() throws IOException {

	}
}
