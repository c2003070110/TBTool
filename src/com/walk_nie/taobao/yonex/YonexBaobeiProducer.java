package com.walk_nie.taobao.yonex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexBaobeiProducer extends BaseBaobeiProducer {

	//private String taobeiTemplateFile = "in/yonex_badmin_pad_baobeiTemplate.csv";
	private String miaoshuTemplateFile = "in/yonex_badmin_miaoshu_template.html";
	
	private File priceFile =  new File("res/YonexCNYPrice.txt");
	private List<String> priceList = Lists.newArrayList();
	// 1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis shoes;
	private int categoryType = 0;
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			if(priceFile.exists()){
				priceList = Files.readLines(priceFile, Charset.forName("UTF-8"));
			}
			List<GoodsObject> itemIdList = ((YonexProductParser) getParser()).setCategoryType(categoryType)
					.scanItem();
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			String picFolder = TaobaoUtil.getPictureFolder(csvFile);
			StringBuffer sbProduct = new StringBuffer();
			for (GoodsObject obj : itemIdList) {
				for (BaobeiPublishObject taobao : this.toUpdatebaobeiList) {
					if (taobao.outer_id.equals(obj.kataban)) {
						obj.isUpdate = true;
					}
				}
				sbProduct.append(obj.title + "\t" + obj.kataban + "\n");
				if (!obj.isUpdate) {
					downloadPicture(obj, picFolder);
				}
				writeOut(priceBw, obj);
			}
			FileUtils.writeStringToFile(new File("res/YonexProduct.txt"), sbProduct.toString(),Charset.forName("UTF-8"));
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
		priceBw.write(composeBaobeiLine(item));
		priceBw.flush();
	}
	
	protected String composeBaobeiLine(GoodsObject item) throws Exception {
		
        BaobeiPublishObject obj = new BaobeiPublishObject();
        BaobeiUtil.setBaobeiCommonInfo(obj);
        
		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
		// 宝贝类目;
		obj.cid = composeBaobeiCId(item);
		// 店铺类目;
		obj.seller_cids = composeBaobeiSellerCids(item);
		// 宝贝价格
		obj.price = findPrice(item);
		// 宝贝数量
		obj.num = "99";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		// 商家编码
		obj.outer_id = "YONEX_" + item.kataban;
		String[] picProp = composeBaobeiPropPicture(item);
		// 图片状态
		obj.picture_status = picProp[0];
		// 新图片
		obj.picture = picProp[1];
		// 宝贝卖点
		obj.subtitle = composeBaobeiSubtitle(item);
		
		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private String composeBaobeiSellerCids(GoodsObject item) {
		if(item.categoryType == 1){
			// "羽毛球拍"
			return "1184433503";
		}else if(item.categoryType == 2){
			// "羽毛球鞋/"
			return "1184433505";
		}else if(item.categoryType == 3){
			// 网球球拍
			return "1184433506";
		}else if(item.categoryType == 4){
			//网球鞋
			return "1184433507";
		}else {
			return "";
		}
	}
	private String composeBaobeiCId(GoodsObject item) {
		if(item.categoryType == 1){
			// "羽毛球拍"
			return "50012323";
		}else if(item.categoryType == 2){
			// "羽毛球鞋/"
			return "50012331";
		}else if(item.categoryType == 3){
			// 网球球拍
			return "50012323";
		}else if(item.categoryType == 4){
			//网球鞋
			return "50012037";
		}else {
			return "";
		}
	}
	private String findPrice(GoodsObject item) {
		for(String line : priceList){
			String[] spl = line.split("\t");
			if(item.kataban.equals(spl[1])){
				return spl[2].replaceAll(",","");
			}
		}
		return "XXX";
	}
	private String composeBaobeiSubtitle(GoodsObject item) {
		return "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleJP + "!" + item.kataban + "!" + "\"";
	}
	private String composeBaobeiTitle(GoodsObject item) {
		String title = "\"日本直邮 Yonex/尤尼克斯";
		title += " " + item.title;
//		String suffix = "/包邮";
//		if (title.length() + suffix.length() < 60) {
//			title += suffix;
//		}
		return title + "\"";
	}
	private String[] composeBaobeiPropPicture(GoodsObject item) {
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
	
	protected String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
		StringBuffer detailSB = new StringBuffer();

        // 包邮 TAX
		// 宝贝描述
		detailSB.append("");
		// 尺寸描述
		detailSB.append("");
		// chanpin teshe!
		detailSB.append(getSeriesSpec(item));
		//　zhi you
		detailSB.append(BaobeiUtil.getExtraMiaoshu());
		return "\"" + detailSB.toString() + "\"";
	}

	private Object getSeriesSpec(GoodsObject item) {
		// TODO 自動生成されたメソッド・スタブ
        StringBuffer miaoshu = new StringBuffer();
        miaoshu.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">购物须知</h3>");
        miaoshu.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        miaoshu.append("<ol>");
        miaoshu.append("<li style=\"padding:10.0px;\"><span style=\";color:red;font-weight:bold\">产地：</span>日本产的非常少。大多数是东南亚和中国制造。<p>大家都知道的。<span style=\";color:red;font-weight:bold\">就算是同条生产线，面向日本本土，要比其他国家的质量要好很多。</span></p></li>");
        miaoshu.append("</ol>");
        miaoshu.append("</div>");
        return miaoshu.toString();
	}
	public String getMiaoshuTemplateFile() {
		return this.miaoshuTemplateFile;
	}

	public YonexBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}
	public YonexBaobeiProducer setCategoryType(int categoryType){
		this.categoryType = categoryType;
		return this;
	}
	@Override
	public BaseBaobeiParser getParser() {
		return new YonexProductParser();
	}

}
