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

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public abstract class AbstractMainListItemId {

	private String exploreUrlFile = "";
	
	private String publishedFile = "";

	private String outputFile = "";
	// 宝贝发布后，新产品的更新
	private boolean isUpdate = false;
	
	private List<BaobeiPublishObject> publishedBaobei = Lists.newArrayList();
	
	private List<String> visited = Lists.newArrayList();
	
	public void process() {
		try {
			System.out.println("-------- START --------");
			init();
			readPublishedBaobei();
			
			List<String> urls = readIn();

			List<KakakuObject> itemList = parse(urls);

			writeOut(itemList);

			destory();

			System.out.println("-------- FINISH--------");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void readPublishedBaobei() {

		BufferedReader br = null;
		File file = new File(publishedFile);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					BaobeiPublishObject obj = TaobaoUtil.readBaobeiIn(str);
					if(obj != null){
						publishedBaobei.add(obj);
					}
				}
			}
		}catch(IOException e){
			System.out.println("[info] published baobei NOT fount!file = " + file.getAbsolutePath());
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	private void writeOut(List<KakakuObject> itemList) throws IOException,
			FileNotFoundException {

		BufferedWriter priceBw = null;
		try {

			String outFilePathPrice = String.format(outputFile , DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFilePathPrice), "UTF-8"));
			for (KakakuObject item : itemList) {
				priceBw.write(composeWriteLine(item));
			}
			priceBw.flush();
		} finally {
			if (priceBw != null)
				priceBw.close();
		}
	}

	private String composeWriteLine(KakakuObject item) {

		StringBuffer sb = new StringBuffer();
		String split = "\t";
		sb.append(item.id).append(split);
		sb.append(item.itemMaker).append(split);
		sb.append(item.itemType).append(split);
		if (item.priceYodobashi != null) {
			sb.append(item.priceYodobashi.price).append(split);
		}
		if (item.priceMin != null) {
			sb.append(item.priceMin.price).append(split);
		}

		return sb.append("\n").toString();
	}

	private List<String> readIn() throws IOException {

		File file = new File(exploreUrlFile);
		List<String> urls = new ArrayList<String>();

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!"".equals(str) && str.startsWith("http")) {
					urls.add(str);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		
		return urls;
	}

	protected abstract String translateType(String substring) ;

	protected abstract String translateMaker(String maker) ;
	
	protected abstract boolean isAllowToParse(KakakuObject obj) throws  IOException;

	private boolean isPublished(KakakuObject kakakuObj) {
		for(BaobeiPublishObject baobei :publishedBaobei){
			if(kakakuObj.id.equals(baobei.outer_id)){
				return true;
			}
		}
		return false;
	}

	private void init() {

	}

	public List<KakakuObject> parse(List<String> urls) throws Exception {
		List<KakakuObject> list = new ArrayList<KakakuObject>();
		for (String url : urls) {
			List<KakakuObject> rslt = KakakuUtil.parseCategoryUrl(url);
			for(KakakuObject obj:rslt){
				if(!isPublished(obj)){
					list.add(obj);
				}
			}
		}
		return list;
	}

	private void destory() throws IOException {

	}
	public AbstractMainListItemId setUpdate(boolean isUpdate){
		this.isUpdate = isUpdate;
		return this;
	}
	public AbstractMainListItemId setExploreUrlFile(String exploreUrlFile){
		this.exploreUrlFile = exploreUrlFile;
		return this;
	}
	public AbstractMainListItemId setPublishedFile(String publishedFile){
		this.publishedFile = publishedFile;
		return this;
	}
	public AbstractMainListItemId setOutputFile(String outputFile){
		this.outputFile = outputFile;
		return this;
	}
}
