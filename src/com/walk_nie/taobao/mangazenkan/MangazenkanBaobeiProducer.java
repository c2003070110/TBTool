package com.walk_nie.taobao.mangazenkan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MangazenkanBaobeiProducer {
	
	private String taobeiTemplateFile = "";
	private String publishedBaobeiFile = "";
	private String miaoshuTemplateFile = "";
	private String outputFile = "";
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<MangazenkanGoodsObject> itemIdList = new MangazenkanProductParser().scanItem();
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

			for (MangazenkanGoodsObject obj : itemIdList) {
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
	protected void downloadPicture(MangazenkanGoodsObject goods,String outFilePathPrice) {
		int i= 0;
		for(int j=goods.pictureList.size()-1;j>=0;j--){
			if(i>5)break;
			String picUrl = goods.pictureList.get(j);
			try {
				String picName = "Mangazenkan_" + goods.productId + "_" + i;
				TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
				goods.pictureNameList.add(picName);
				i++;
			} catch (Exception ex) { 
			}
		}
	}
	
	protected void writeOut(BufferedWriter priceBw, MangazenkanGoodsObject item)
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
	protected String composeBaobeiLine(MangazenkanGoodsObject item,BaobeiPublishObject baobeiTemplate) throws Exception {
		BaobeiPublishObject obj = TaobaoUtil.copyTaobaoTemplate(baobeiTemplate);

		// 店铺类目
		obj.seller_cids=composeStoreCategory(item);
		
		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
		// 宝贝价格
		obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "9999";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		
		// 0:宝贝属性 1:销售属性组合 2:销售属性别名
		//String[] props = composeBaobeiPropColor(item,baobeiTemplate);
		// 宝贝属性
//		String str = baobeiTemplate.cateProps + props[0];
//		str = str.replaceAll("\"\"", "");
		//obj.cateProps = props[0];
//		// 销售属性组合
//		if("\"\"".equals(baobeiTemplate.skuProps)){
//			obj.skuProps = props[1];
//		}else{
//			obj.skuProps = baobeiTemplate.skuProps + props[1];
//		}
		//obj.skuProps = props[1];
		// 商家编码
		obj.outer_id = item.productId;
//		// 销售属性别名
//		if("\"\"".equals(baobeiTemplate.skuProps)){
//			obj.propAlias = props[2];
//		}else{
//			obj.propAlias = baobeiTemplate.propAlias + props[2];
//		}
		//obj.propAlias = props[2];
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
	 
	private String composeStoreCategory(MangazenkanGoodsObject item) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("少年漫画", "1084402668");
		map.put("青年漫画", "1184687163");
		map.put("少女漫画", "1184687164");
		map.put("女性漫画", "1184687165");
		map.put("ライトノベル", "1184687166");
		map.put("BL(ボーイズラブ)", "1184687167");
		map.put("TL(ティーンズラブ)", "1184687168");
		String name = item.categoryName;
		
		String cate = map.get(name);
		if(cate == null || "".equals(cate)){
			cate = map.get("少年漫画");
		}
		if(item.finished){
			cate = cate +",1184779052";
		}
		return cate;
	}
	private String composeBaobeiSubtitle(MangazenkanGoodsObject item) {
		//return "\"日本直邮代购！Mangazenkan！" +  item.productId + "!"+ item.itemName + "!" + "\"";
		return "\"日本直邮代购！日文原版漫画！" +  item.productId + " ! "+ item.name + " ! " + "\"";
	}
	private String composeBaobeiTitle(MangazenkanGoodsObject item) {
		String title = "\"";
		title += "" + item.title;
		if(item.finished){
			title += " 完结!全"+ item.volCount +"卷";
		}else{
			title += " 即刊"+ item.volCount +"卷";
		}
//		String suffix = "/包邮";
//		if (title.length() + suffix.length() < 60) {
//			title += suffix;
//		}
		return title + " 日本直邮代购！日文原版！\"";
	}
	private String[] composeBaobeiPropPicture(MangazenkanGoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		String picSts = "";
		String pics = "";
		// 主图
		int cnt = 0;
		for(int i=item.pictureNameList.size()-1;i>=0;i--){
			if(cnt==5) break;
			pics += item.pictureNameList.get(i) + ":1:" + cnt +":|;";
			picSts +="2;";
			cnt ++;
		}
//		// TODO宝贝详细图
//		for(int i=0;i<item.colorPictureNameList.size();i++){
//			pics += item.colorPictureNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
//			picSts +="2;";
//		}
		return new String[] { "\"" + picSts + "\"", "\"" + pics + "\"" };
	}
	
	protected String[] composeBaobeiPropColor(MangazenkanGoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		
		String cateProps = "";String skuProps = "";String propAlias = "";
//		cateProps = "20017:81121134;3138517:14138246;134942334:41517;134942334:-1;134942334:35168552;1627207:3232483;";
//
//		// EMS SAL 船运
//		skuProps = item.priceEMS+":9999::134942334:41517;1627207:3232483;";
//		skuProps += item.priceSAL+":9999::134942334:35168552;1627207:3232483;";
//		skuProps += item.priceSEA+":9999::134942334:-1;1627207:3232483;";
//
//		propAlias += "134942334:41517:EMS7天到手;134942334:35168552:SAL20天到手;134942334:-1:海运40天到手;1627207:3232483:日本直邮";
		
		// skuProp 123:777::-1:-1;456:888::-1:-2;789:999::-1:-3;
		// cateProps 3138517:20213;20017:81121134
		
		//cateProps = "20017:81121134;3138517:20213";
		//skuProps = "123:777:20213:-1:-1;456:888:20213:-1:-2;789:999:20213:-1:-3;";
		//propAlias = "-1:-1:EMS7天到手;-1:-2:SAL20天到手;-1:-3:海运40天到手;3138517:20213:日本直邮";
		//cateProps = "3138517:20213;20017:81121134";
		//skuProps = "123:777::-1:-1;456:888::-1:-2;789:999::-1:-3;";
		//propAlias = "";
		return new String[]{"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" };
	}
	
	protected  String composeBaobeiMiaoshu(MangazenkanGoodsObject item) throws IOException {
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
		String feeUrl ="https://item.taobao.com/item.htm?id=520103103244";
		String bookCoverUrl = "https://item.taobao.com/item.htm?id=527036141181";
		String bookCover =  "<a target=\"blank\" href=\""+bookCoverUrl+"\" >"+"包邮！透明书皮链接"+"</a>";
		String extra = "W_"+item.totalWeight +":Y_"+item.price +"<br>";
		extra +=  "<p>------------------------------------------<p><br>";
		extra +=  "<p style=\";color:red;font-weight:bold\">日本直邮。需要另外运费。根据您自己的意愿，按下表补运费！无运费！不发货！<p><br>";
		extra +=  "<p style=\";color:red;font-weight:bold\">(1)EMS（3-5天到手 特色 快 贵） = "+item.priceCNYEMS+"<p><br>";
		extra +=  "<p style=\";color:red;font-weight:bold\">(2)SAL（20天到手 较快 不便宜不贵） = "+item.priceCNYSAL+"<p><br>";
		extra +=  "<p style=\";color:red;font-weight:bold\">(3)船运（40天左右到手 特色是慢 而且便宜） = "+item.priceCNYSEA+"<p><br>";
		extra +=  "<p>补运费链接>>>><a target=\"blank\" href=\""+feeUrl+"\" >"+feeUrl+"</a><p><br>";
		extra +=  "<p>------------------------------------------<p><br>";
		extra +=  "日本书名: "+item.name +"<br>";
		extra +=  "出版社　: "+item.brand +"<br>";
		extra +=  "作者　　　: "+item.author +"<br>";
		extra +=  "最終巻発売日　: "+item.latestPublishDate +"<br>";
		extra +=  "版型　　　: "+item.bookPublishType + ">>>" +bookCover + "<br>";
		extra +=  "概要　　　: " +"<br>";
		String desp = sb.toString().replace("$detail_disp$", extra + "　　　　"+productInfo);
		desp = desp.replaceAll("\"", "\"\"");
		return "\"" + desp + "\"";
	}

	public MangazenkanBaobeiProducer setTaobeiTemplateFile(String taobeiTemplateFile) {
		this.taobeiTemplateFile = taobeiTemplateFile;
		return this;
	}

	public MangazenkanBaobeiProducer setPublishedBaobeiFile(String publishedBaobeiFile) {
		this.publishedBaobeiFile = publishedBaobeiFile;
		return this;
	}

	public MangazenkanBaobeiProducer setMiaoshuTemplateFile(
			String miaoshuTemplateFile) {
		this.miaoshuTemplateFile = miaoshuTemplateFile;
		return this;
	}

	public String getMiaoshuTemplateFile() {
		return this.miaoshuTemplateFile;
	}

	public MangazenkanBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

}
