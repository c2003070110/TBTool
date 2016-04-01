package com.walk_nie.taobao.yonex.badminton;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexBadBaobeiProducer {
	
	private String taobeiTemplateFile = "";
	private String publishedBaobeiFile = "";
	private String miaoshuTemplateFile = "";
	private String outputFile = "";
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> itemIdList = new YonexBadProductParser().scanItem();
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

			for (GoodsObject obj : itemIdList) {
				downloadPicture(obj, csvFile.getName().replace(".csv", ""));
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
	protected void downloadPicture(GoodsObject goods,String outFilePathPrice) {
		int i= 0;
		for(String picUrl : goods.pictureList){
			try{
				String picName = "yonex_" + goods.kataban+"_" +i ;
				TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
				goods.pictureNameList.add(picName);
				i++;
			} catch (Exception ex) { 
			}
		}
	}
	protected void writeOut(BufferedWriter priceBw, GoodsObject item)
			throws Exception {
		BaobeiPublishObject baobeiTemplate = new BaobeiPublishObject();
		
		BufferedReader br = null;
		try {
			File file = new File(taobeiTemplateFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					baobeiTemplate = TaobaoUtil.readBaobeiIn(str);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		
		priceBw.write(composeBaobeiLine(item, baobeiTemplate));
		priceBw.flush();
	}
	protected String composeBaobeiLine(GoodsObject item,BaobeiPublishObject baobeiTemplate) throws Exception {
		BaobeiPublishObject obj = TaobaoUtil.copyTaobaoTemplate(baobeiTemplate);

		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
		// 宝贝价格
		obj.price = item.price;
		// 宝贝数量
		obj.num = "9999";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		
		// 0:宝贝属性 1:销售属性组合 2:销售属性别名
//		String[] props = composeBaobeiPropColor(item,baobeiTemplate);
//		// 宝贝属性
//		String str = baobeiTemplate.cateProps + props[0];
//		str = str.replaceAll("\"\"", "");
//		str = props[0];
//		obj.cateProps = str;
//		// 销售属性组合
//		if("\"\"".equals(baobeiTemplate.skuProps)){
//			obj.skuProps = props[1];
//		}else{
//			obj.skuProps = baobeiTemplate.skuProps + props[1];
//		}
//		obj.skuProps = props[1];
		// 商家编码
		obj.outer_id = item.kataban;
//		// 销售属性别名
//		if("\"\"".equals(baobeiTemplate.skuProps)){
//			obj.propAlias = props[2];
//		}else{
//			obj.propAlias = baobeiTemplate.propAlias + props[2];
//		}
//		obj.propAlias = props[2];
		// 商品条形码
		//obj.barcode = item.sku;
		String[] picProp = composeBaobeiPropPicture(item, baobeiTemplate);
		// 图片状态
		obj.picture_status = picProp[0];
		// 新图片
		obj.picture = picProp[1];
		// 宝贝卖点
		obj.subtitle = composeBaobeiSubtitle(item);
		
		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private String composeBaobeiSubtitle(GoodsObject item) {
		return "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleJP + "!" + item.kataban + "!" + "\"";
	}
	private String composeBaobeiTitle(GoodsObject item) {
		String title = "\"日本直邮 Yonex/尤尼克斯 ";
		title += " " + item.title;
//		String suffix = "/包邮";
//		if (title.length() + suffix.length() < 60) {
//			title += suffix;
//		}
		return title + "\"";
	}
	private String[] composeBaobeiPropPicture(GoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		String picSts = "";
		String pics = "";
		for(int i=0;i<item.pictureNameList.size();i++){
			if(i==5) break;
			pics += item.pictureNameList.get(i) + ":1:" + i +":|;";
			picSts +="2;";
		}
		return new String[] { "\"" + picSts + "\"", "\"" + pics + "\"" };
	}
	
	protected String[] composeBaobeiPropColor(GoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		List<String> taobaoColors = Lists.newArrayList();
		taobaoColors.add("28320");taobaoColors.add("28340");taobaoColors.add("3232479");
		taobaoColors.add("3232478");taobaoColors.add("3232482");taobaoColors.add("60092");
		taobaoColors.add("30156");taobaoColors.add("28332");taobaoColors.add("90554");
		taobaoColors.add("3232481");taobaoColors.add("3232484");taobaoColors.add("3232483");
		int i = 0;
		// 颜色值:28320 28324 28326 28327 28329 28332 28340 28338 28335
		// 宝贝属性 -销售属性组合- 销售属性别名
		String cateProps = "";String skuProps = "";String propAlias = "";
		for(String color :item.colorList){
			if(i>=taobaoColors.size())break;
			// 宝贝属性格式  1627207:28320;
			cateProps +="1627207:"+taobaoColors.get(i)+";";
			// 销售属性组合格式 价格:数量:SKU:1627207:28320;
			skuProps += item.price +":9999"+":"+":1627207"+":"+taobaoColors.get(i)+";20549:44911;";
			// 销售属性别名格式 1627207:28320:颜色1;
			//propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
			propAlias += "1627207:" + taobaoColors.get(i) + ":" + color + ";";
			i++;
		}
		propAlias += "20549:44911:请留言;";
		return new String[]{"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" };
	}
	
	protected  String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			File file = new File(getMiaoshuTemplateFile());
			String str = null;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} finally {
			if (br != null)
				br.close();
		}
		String productInfo = item.detailDisp;
		if(productInfo == null){
			productInfo = "";
		}
		String desp = sb.toString().replace("$detail_disp$", productInfo);
		desp = desp.replaceAll("\"", "\"\"");
		return "\"" + desp + "\"";
	}
	
	public YonexBadBaobeiProducer setTaobeiTemplateFile(String taobeiTemplateFile) {
		this.taobeiTemplateFile = taobeiTemplateFile;
		return this;
	}

	public YonexBadBaobeiProducer setPublishedBaobeiFile(String publishedBaobeiFile) {
		this.publishedBaobeiFile = publishedBaobeiFile;
		return this;
	}

	public YonexBadBaobeiProducer setMiaoshuTemplateFile(
			String miaoshuTemplateFile) {
		this.miaoshuTemplateFile = miaoshuTemplateFile;
		return this;
	}

	public String getMiaoshuTemplateFile() {
		return this.miaoshuTemplateFile;
	}

	public YonexBadBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

}
