package com.walk_nie.taobao.kakaku.explore.earphone;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.kakaku.AbstractCreateBaobei;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MainCreateBaobei extends AbstractCreateBaobei  {
	
	public static void main(String[] args) {

		String scanUrlsFile = "in/earphone_scan_urls.txt";
		String taobeiTemplateFile = "in/earphone_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/earphone_miaoshu_template.html";
		String outputFile = "out/earphone_baobei_%s.csv";
		String publishedBaobeiFile = "in/earphone_publishedItems.txt";
		new MainCreateBaobei().setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				.setTaobeiTemplateFile(taobeiTemplateFile)
				.setPublishedBaobeiFile(publishedBaobeiFile)
				.setScanUrlsFile(scanUrlsFile).process();
		System.exit(0);
	}

	@Override
	protected String composeBaobeiLine(KakakuObject item,BaobeiPublishObject baobeiTemplate) throws Exception {
		BaobeiPublishObject obj = TaobaoUtil.copyTaobaoTemplate(baobeiTemplate);

		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
		// 宝贝价格
		obj.price = EarphoneUtil.convertToCNY(item);
		// 宝贝数量
		obj.num = "9999";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		// FIXME 宝贝属性暂停！
		// 0:宝贝属性 1:销售属性组合 2:销售属性别名
		String[] props = composeBaobeiPropColor(item,baobeiTemplate);
		 //宝贝属性
		String str = baobeiTemplate.cateProps + props[0];
		str = str.replaceAll("\"\"", "");
		obj.cateProps = str;
		// 销售属性组合
		if("\"\"".equals(baobeiTemplate.skuProps)){
			obj.skuProps = props[1];
		}else{
			obj.skuProps = baobeiTemplate.skuProps + props[1];
		}
		// 商家编码
		obj.outer_id = item.id;
		// 销售属性别名
		if("\"\"".equals(baobeiTemplate.skuProps)){
			obj.propAlias = props[2];
		}else{
			obj.propAlias = baobeiTemplate.propAlias + props[2];
		}
		// 商品条形码
		obj.barcode = item.sku;
		// 宝贝卖点
		obj.subtitle = composeBaobeiMaidian(item);
		
		String[] picProp = composeBaobeiPropPicture(item, baobeiTemplate);
		// 图片状态
		obj.picture_status = picProp[0];
		// 新图片
		obj.picture = picProp[1];

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	@Override
	protected String getExtraBaobeiMiaoshu(KakakuObject item) throws IOException {

		String specUrl = !StringUtil.isBlank(item.spec.productInfoUrl)?item.spec.productInfoUrl:item.spec.specInfoUrl;
		if (StringUtil.isBlank(specUrl)) {
			return "";
		}
		if (specUrl.indexOf("audio-technica.co.jp") > 1) {
			return getExtraBaobeiMiaoshuFromATH(item,specUrl);
		} else if (specUrl.indexOf("yamaha.com") > 1) {
			//parsePictureFromYamaha(obj, specUrl);
		} else if (specUrl.indexOf("denon.jp") > 1) {
			//parsePictureFromDenon(obj, specUrl);
		}
		return "";
	}

	private String getExtraBaobeiMiaoshuFromATH(KakakuObject item,String specUrl) throws ClientProtocolException, IOException {
		Document doc = TaobaoUtil.urlToDocumentByUTF8(specUrl);
		Elements spec = doc.select("div#spec").select("table");
		if(spec.isEmpty()){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String html = spec.outerHtml();
		html = html.replaceAll("\"","\"\"");
		html = html.replaceAll("\r\n","");
		html = html.replaceAll("\n","");
				
		sb.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝详细参数</h3>");
		sb.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		sb.append("<p>" + html + "</p>");
		sb.append("</div>");
		
		return sb.toString();
	}

	private String[] composeBaobeiPropPicture(KakakuObject item,
			BaobeiPublishObject baobeiTemplate) {
		String picSts = "";
		String pics = "";
		// 主图
		if (!item.colorList.isEmpty()) {
			// 主图
			for(int i=0;i<item.colorList.size();i++){
				if(i==5) break;
				pics += item.colorList.get(i).id + ":1:" + i +":|;";
			}
			// 颜色配图
			for(int i=0;i<item.colorList.size();i++){
				if(i>=taobaoColors.size())break;
				pics += item.colorList.get(i).id + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
				picSts +="2;";
			}
		}else{
			pics += item.id + ":1:" + 0 +":|;";
		} 
		return new String[] { "\"" + picSts + "\"", "\"" + pics + "\"" };
	}

	private List<String> taobaoColors = Lists.newArrayList();
	{
		taobaoColors.add("28320");taobaoColors.add("28324");taobaoColors.add("28326");
		taobaoColors.add("28327");taobaoColors.add("28329");taobaoColors.add("28332");
		taobaoColors.add("28340");taobaoColors.add("28338");taobaoColors.add("28335");
	}
	private String[] composeBaobeiPropColor(KakakuObject item,
			BaobeiPublishObject baobeiTemplate) {
		int i = 0;
		// 颜色值:28320 28324 28326 28327 28329 28332 28340 28338 28335
		// 宝贝属性 -销售属性组合- 销售属性别名
		String cateProps = "";String skuProps = "";String propAlias = "";
		cateProps += getBaobeiCategory(item);
		for(KakakuObject colorObj :item.colorList){
			if(i>=taobaoColors.size())break;
			// 宝贝属性格式  1627207:28320;
			cateProps +="1627207:"+taobaoColors.get(i)+";";
			// 销售属性组合格式 价格:数量:SKU:1627207:28320;
			skuProps += EarphoneUtil.convertToCNY(colorObj) +":9999:"+colorObj.sku+":1627207:"+taobaoColors.get(i)+";";
			// 销售属性别名格式 1627207:28320:颜色1;
			//propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
			propAlias +="1627207:"+taobaoColors.get(i)+":" +EarphoneUtil.convertColor(colorObj.colorName)+ "[K"+colorObj.id+"8]"+";";
			i++;
			
		}
		return new String[]{"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" };
	}

	private String getBaobeiCategory(KakakuObject item) {
		if("Sony/索尼".equals(item.itemMaker)){
			return "20000:10752;";
		}
		if("Audio Technica/铁三角".equals(item.itemMaker)){
			return "20000:21980;";
		}
		if("JVC/杰伟世".equals(item.itemMaker)){
			//return "20000:27287;";
			return "20000:58615113;";
		}
		if("Pioneer/先锋".equals(item.itemMaker)){
			return "20000:20804;";
		}
		if("Yamaha/雅马哈".equals(item.itemMaker)){
			return "20000:27207;";
		}
		if("Philips/飞利浦".equals(item.itemMaker)){
			return "20000:10246;";
		}
		if("DENON/天龙".equals(item.itemMaker)){
			return "20000:27269;";
		}
		if("Onkyo/安桥".equals(item.itemMaker)){
			return "20000:27273;";
		}
		if("Panasonic/松下".equals(item.itemMaker)){
			return "20000:81147;";
		}
		return "";
	}

	private String composeBaobeiTitle(KakakuObject item) {
		SpecObject spec = (SpecObject)item.spec;
		String title = "\"东京直邮/";
		title += item.itemMaker + " " + item.itemType;
		if (!StringUtil.isBlank(spec.setType)) {
			title += "/" + spec.setType;
		}
		//if (!StringUtil.isBlank(spec.driverType)) {
		//	title += "/" + spec.driverType;
		//}
		String suffix = "/日行耳机";
		if (title.length() + suffix.length() < 60) {
			title += suffix;
		}
		suffix = "/包邮";
		if (title.length() + suffix.length() < 60) {
			title += suffix;
		}
		return title + "\"";
	}

	private String composeBaobeiMaidian(KakakuObject item) {
		SpecObject spec = (SpecObject)item.spec;
		String str ="\"";
		if(!StringUtil.isBlank(spec.setType)){
			str += spec.setType +"!";
		}
		if(!StringUtil.isBlank(spec.driverType)){
			str += spec.driverType +"!";
		}
		if(!StringUtil.isBlank(spec.wirelessType)){
			str += "无线耳机!";
		}
		if(spec.hasMicro){
			str += "有麦!";
		}
		str +="包邮！东京直邮！100%正品！日本行货！";
		return str +"\"";
	}

	@Override
	protected void parseItemSpec(Document doc, String itemUrl, KakakuObject obj) throws ClientProtocolException, IOException {
		obj.spec  = new SpecObject();
		
		super.parseItemBasicSpec(doc,  obj);
		
		Document docSpecDetail = EarphoneUtil.urlToDocumentKakaku(itemUrl + "/spec");
		Elements specDetail = docSpecDetail.select("div").select("#mainLeft")
				.select("table").select("tr");
		for (Element spec : specDetail) {
			if (StringUtil.isBlank(spec.attr("class"))
					&& spec.children().size() == 4) {
				EarphoneUtil.resolveSpecDetail(
						spec.child(0).text(), spec.child(1).text(), obj);
				
				EarphoneUtil.resolveSpecDetail(
						spec.child(2).text(), spec.child(3).text(), obj);
			}
		}
	}

	@Override
	protected boolean isAllowToBaobei(KakakuObject obj) {
 
//		if(obj.priceYodobashi == null){
//			return false;
//		}
//		double d = (obj.priceYodobashi.price - obj.priceYodobashi.price * 0.1);
//		if(d >  obj.priceMin.price){
//			return false;
//		}
//		if(obj.priceYodobashi.price < 1500){
//			return false;
//		}	
		if(obj.priceMin.price < 1500){
			return false;
		}
		return true;
		/*
		if(obj.priceYodobashi == null){
			if(obj.itemMaker.equals("SONY")){
				return false;
			}
			if(obj.itemMaker.equals("Audio Technica/铁三角")){
				return false;
			}
		}
		if(obj.priceMin != null){
			if(obj.priceMin.price < 1500){
				System.out.println("[WARN][price is Behind] " + 1500 + "[" + obj + "]");
				return false;
			}
			return true;
		}else{
			System.out.println("[WARN][price is ZERO] " + obj );
			return false;
		}
		*/
	}

	@Override
	protected void parsePictureFromMaker(KakakuObject obj) throws ClientProtocolException, IOException {
		if (StringUtil.isBlank(obj.spec.productInfoUrl)
				&& StringUtil.isBlank(obj.spec.specInfoUrl)) {
			return;
		}
		String specUrl = !StringUtil.isBlank(obj.spec.productInfoUrl) ? obj.spec.productInfoUrl
				: obj.spec.specInfoUrl;
		if (specUrl.indexOf("audio-technica.co.jp") > 1) {
			//parsePictureFromAudioTechnica(obj, specUrl);
		} else if (specUrl.indexOf("yamaha.com") > 1) {
			//parsePictureFromYamaha(obj, specUrl);
		} else if (specUrl.indexOf("denon.jp") > 1) {
			//parsePictureFromDenon(obj, specUrl);
		}
	}

	private void parsePictureFromDenon(KakakuObject obj, String specUrl) throws ClientProtocolException, IOException {
		Document doc = TaobaoUtil.urlToDocumentByWebDriver(specUrl);
		Elements picSelect = doc.select("div").select(".prodNav").select("li");
		int idx = 1;
		for (Element element : picSelect) {
			String picUrl = element.attr("data-image");
			TaobaoUtil.downloadPicture(obj.itemType, picUrl, obj.id + "_" + idx);
			idx++;
		}
	}

	private void parsePictureFromYamaha(KakakuObject obj, String specUrl) throws ClientProtocolException, IOException {
		Document doc = TaobaoUtil.urlToDocumentByWebDriver(specUrl);
		Elements picSelect = doc.select("div").select("#picturesBox").select("li").select("img");
		int idx = 1;
		for(Element element:picSelect){
			String picUrl = element.attr("src");
			int sub =picUrl.indexOf("images");
			if(sub == -1)continue;
			String[] suffixs = picUrl.substring(sub).split("/");
			String newPicUrl = picUrl.substring(0,sub) + "/" + suffixs[0] + "/" +suffixs[1]+"/"  +suffixs[2]+"/"+suffixs[1]+"_" +suffixs[2] +"_1" +".jpg";
			TaobaoUtil.downloadPicture(obj.itemType, newPicUrl, obj.id +"_" +idx);
			idx++;
		}
	}

	private void parsePictureFromAudioTechnica(KakakuObject obj, String specUrl) throws ClientProtocolException, IOException {
		Document doc = TaobaoUtil.urlToDocumentByWebDriver(specUrl);
		Elements picSelect = doc.select("table").select("#photo_selecter").select("img");
		int idx = 1;
		for(Element element:picSelect){
			String picUrl = element.attr("src");
			picUrl = "http://www.audio-technica.co.jp/" + picUrl;
			TaobaoUtil.downloadPicture(obj.itemType, picUrl, obj.id +"_" +idx);
			idx++;
		}
	}
}
