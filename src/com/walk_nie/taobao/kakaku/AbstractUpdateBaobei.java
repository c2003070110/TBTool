package com.walk_nie.taobao.kakaku;

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
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public abstract class AbstractUpdateBaobei {

	private String baobeiListFile = "";

	private String outputFile = "";

	public AbstractUpdateBaobei setBaobeiListFile(String baobeiListFile) {
		this.baobeiListFile = baobeiListFile;
		return this;
	}

	public AbstractUpdateBaobei setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}
	
	protected abstract String convertToCNY(KakakuObject colorObj);
	
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
			
			for(BaobeiPublishObject baobei:baobeiList){
				update(baobei);
			}

			writeOut(baobeiList);

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

	private void writeOut(List<BaobeiPublishObject> baobeiList) throws IOException {
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
		} finally {
			if (priceBw != null)
				priceBw.close();
		}
	}

	private void update(BaobeiPublishObject baobei) throws Exception {

		String kakakuId = baobei.outer_id;
		if(StringUtil.isBlank(kakakuId)){
			System.out.println("[WARN][NO kakakuId] title = " + baobei.title);
			return;
		}

		String kakakuUril =KakakuUtil.kakakuUrlPrefix + kakakuId;
		Document doc = KakakuUtil.urlToDocumentKakaku(kakakuUril);
		KakakuObject kakakuObj = new KakakuObject();
		KakakuUtil.parseItemMaker(doc, kakakuObj);
		KakakuUtil.parseItemPrice(doc, kakakuObj);
		//Util.parseItemVariation(doc, kakakuObj);
		
		//update price
		updatePrice(baobei,kakakuObj);
	}

	private void updatePrice(BaobeiPublishObject baobei, KakakuObject kakakuObj) {
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
						child0[0] = convertToCNY(colorObj);
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
		baobei.price = convertToCNY(kakakuObj);
	}


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
