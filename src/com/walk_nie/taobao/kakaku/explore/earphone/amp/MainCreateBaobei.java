package com.walk_nie.taobao.kakaku.explore.earphone.amp;

import java.io.IOException;

import org.apache.commons.beanutils.BeanUtils;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.kakaku.AbstractCreateBaobei;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.kakaku.explore.earphone.EarphoneUtil;
import com.walk_nie.taobao.kakaku.explore.earphone.SpecObject;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MainCreateBaobei extends AbstractCreateBaobei {
	
	public static void main(String[] args) {
		String scanUrlsFile = "in/earphone_amp_toBaobeiItmes.txt";
		String taobeiTemplateFile = "in/earphone_amp_baobeiTemplate.csv";
		String miaoshuTemplateFile = "in/earphone_amp_miaoshu_template.html";
		String outputFile = "out/earphone_amp_baobei_%s.csv";
		
		new MainCreateBaobei().setMiaoshuTemplateFile(miaoshuTemplateFile)
				.setOutputFile(outputFile)
				.setTaobeiTemplateFile(taobeiTemplateFile)
				.setScanUrlsFile(scanUrlsFile).process();
		System.exit(0);
	}

	@Override
	protected String composeBaobeiLine(KakakuObject item,BaobeiPublishObject baobeiTemplate) throws Exception {
		BaobeiPublishObject obj = new BaobeiPublishObject();
		BeanUtils.copyProperties(obj, baobeiTemplate);

		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
		// 宝贝价格
		obj.price = EarphoneUtil.convertToCNY(item,0.065,0.1);
		// 宝贝数量
		obj.num = "9999";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		
		// 商家编码
		obj.outer_id = item.id;
		
		// 商品条形码
		obj.barcode = item.sku;
		// 宝贝卖点
		obj.subtitle = composeBaobeiMaidian(item);

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private String composeBaobeiTitle(KakakuObject item) {
		SpecObject spec = (SpecObject) item.spec;
		String title = "\"日本直邮/";
		title += item.itemMaker + " " + item.itemType;
		if (!StringUtil.isBlank(spec.setType)) {
			title += "/" + spec.setType;
		}
		if (!StringUtil.isBlank(spec.driverType)) {
			title += "/" + spec.driverType;
		}
		String suffix = "/便携耳放/包邮/日行";
		title += suffix;
		return title + "\"";
	}

	private String composeBaobeiMaidian(KakakuObject item) {
		String str ="\"";
		str +="包邮！东京直邮！100%正品！日本行货！";
		return str +"\"";
	}


	@Override
	protected boolean isAllowToBaobei(KakakuObject obj) {
		if(obj.priceMin != null){
			if(obj.priceMin.price >1500){
				return true;
			}else{
				System.out.println("[WARN][price is Behind] " + 1500 + "[" + obj + "]");
				return false;
			}
		}else{
			System.out.println("[WARN][price is ZERO] " + obj );
			return false;
		}
	}
	@Override
	protected void parseItemSpec(Document doc, String itemUrl, KakakuObject obj) throws IOException {
		obj.spec  = new SpecObject();
		
		super.parseItemBasicSpec(doc,  obj);
		
		Document docSpecDetail = KakakuUtil.urlToDocumentKakaku(itemUrl + "/spec");
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
	protected void parsePictureFromMaker(KakakuObject obj) throws  IOException {

		if (StringUtil.isBlank(obj.spec.productInfoUrl)
				&& StringUtil.isBlank(obj.spec.specInfoUrl)) {
			return;
		}
		String specUrl = !StringUtil.isBlank(obj.spec.productInfoUrl) ? obj.spec.productInfoUrl
				: obj.spec.specInfoUrl;
		if (specUrl.indexOf("audio-technica.co.jp") > 1) {
			parsePictureFromAudioTechnica(obj, specUrl);
		} else if (specUrl.indexOf("yamaha.com") > 1) {
			//parsePictureFromYamaha(obj, specUrl);
		} else if (specUrl.indexOf("denon.jp") > 1) {
			parsePictureFromDenon(obj, specUrl);
		}
	}
	@Override
	protected String getExtraBaobeiMiaoshu(KakakuObject item) throws IOException {
		String specUrl = !StringUtil.isBlank(item.spec.productInfoUrl)?item.spec.productInfoUrl:item.spec.specInfoUrl;
		if (StringUtil.isBlank(specUrl)) {
			return "";
		}
		return "";
	}

	private void parsePictureFromAudioTechnica(KakakuObject obj, String specUrl) throws  IOException {
		Document doc = KakakuUtil.urlToDocumentKakaku(specUrl);
		Elements picSelect = doc.select("table").select("#photo_selecter").select("img");
		int idx = 1;
		for(Element element:picSelect){
			String picUrl = element.attr("src");
			// http://www.audio-technica.co.jp/
			TaobaoUtil.downloadPicture(outputPicFolder, picUrl, obj.id +"_" +idx);
			idx++;
		}
	}
	private void parsePictureFromDenon(KakakuObject obj, String specUrl) throws IOException {
		Document doc = KakakuUtil.urlToDocumentKakaku(specUrl);
		Elements picSelect = doc.select("div").select(".prodNav").select("li");
		int idx = 1;
		for (Element element : picSelect) {
			String picUrl = element.attr("data-image");
			TaobaoUtil.downloadPicture(outputPicFolder, picUrl, obj.id + "_" + idx);
			idx++;
		}
	}

}
