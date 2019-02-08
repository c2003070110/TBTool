package com.walk_nie.taobao.righton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class RightonBaobeiProducer extends BaseBaobeiProducer {

	protected List<String> taobaoColors = Lists.newArrayList();
    {
        taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
        taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
        taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
        taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
    }

    protected List<String> taobaoSizes = Lists.newArrayList();
    {
    	 // XS,S,M,L,XL,XXL,
        taobaoSizes.add("28313");taobaoSizes.add("28314");taobaoSizes.add("28315");
        taobaoSizes.add("28316");taobaoSizes.add("28317");taobaoSizes.add("28318");
    }
    protected List<String> picturesForDetailPage = Lists.newArrayList();

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> productObjs = Lists.newArrayList();
			
			if (!categoryUrlList.isEmpty()) {
				List<GoodsObject> productObjList = ((RightonParser) getParser())
						.parseProductByCategoryUrl(categoryUrlList);
				productObjs.addAll(productObjList);
			}
			
			if (!productUrlList.isEmpty()) {
				List<GoodsObject> productObjList = ((RightonParser) getParser())
						.parseProductByProductUrl(productUrlList);
				productObjs.addAll(productObjList);
			}
			
			writeOut(priceBw,productObjs);
		
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
	protected void downloadPicture(GoodsObject goods,String outFilePath) {
		int i= 0;
		for(String picUrl : goods.colorPicUrlList){
			try {
				String picName = "righton_" + goods.kataban + "-" + goods.colorNameList.get(i) + "_" + i;
				TaobaoUtil.downloadPicture(outFilePath, picUrl, picName);
				goods.colorPicLocalNameList.add(picName);
				i++;
			} catch (Exception ex) { 
			}
		}
		for(String picUrl : goods.dressOnPicsUrlList){
			try {
				String picName = "righton_" + goods.kataban + "_" + i;
				File f = TaobaoUtil.downloadPicture(outFilePath, picUrl, picName);
				picturesForDetailPage.add(f.getAbsolutePath());
				goods.dressOnPicLocalNameList.add(picName);
				i++;
			} catch (Exception ex) { 
			}
		}
	}

	protected void writeOut(BufferedWriter priceBw, List<GoodsObject> productObjList) throws IOException {

		priceBw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outputFile), "UTF-16"));
		for(GoodsObject productObj : productObjList){
			downloadPicture(productObj, RightonUtil.getPictureSavePath(productObj));
		}

		priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
		String taobaoPicFolder = TaobaoUtil.getPictureFolder(outputFile);

		for (GoodsObject productObj : productObjList) {
			TaobaoUtil.copyFiles(productObj.colorPicLocalNameList, RightonUtil.getPictureSavePath(productObj),
					taobaoPicFolder);
			TaobaoUtil.copyFiles(productObj.dressOnPicLocalNameList, RightonUtil.getPictureSavePath(productObj),
					taobaoPicFolder);

			writeBaobeiContentLine(priceBw, productObj);
		}
	}

	protected void writeBaobeiContentLine(BufferedWriter priceBw, GoodsObject item) throws IOException {
		priceBw.write(composeBaobeiLine(item));
		priceBw.flush();
	}
	
	protected String composeBaobeiLine(GoodsObject item)  {
		
        BaobeiPublishObject obj = new BaobeiPublishObject();
        BaobeiUtil.setBaobeiCommonInfo(obj);
        
		// 宝贝名称
		composeBaobeiTitle(item, obj);
		// 宝贝类目;
		composeBaobeiCId(item, obj);
		// 店铺类目;
		composeBaobeiSellerCids(item, obj);
        // 省
        obj.location_state = "\"日本\"";
		// 宝贝价格
		composeBaobeiPrice(item, obj);
		// 宝贝数量
		obj.num = "99";
        // 邮费模版ID
		composeBaobeiPostageId(item, obj);
        // 用户输入ID串;
		composeBaobeiInputPids(item, obj);
		// 用户输入名-值对
		composeBaobeiInputValues(item, obj);
		// 宝贝描述
		composeBaobeiMiaoshu(item, obj);
        // 宝贝属性
		composeBaobeiCateProps(item, obj);
		// 销售属性组合
		composeBaobeiSkuProps(item, obj);
		// 商家编码
		composeBaobeiOuter_id(item, obj);
		// 销售属性别名
		composeBaobeiPropAlias(item, obj);
		// 图片状态
		composeBaobeiPictureStatus(item, obj);
		// 新图片
		composeBaobeiPicture(item, obj);
        // 自定义属性值
		composeBaobeiInputCustomCpv(item, obj);
        // 宝贝卖点
        composeBaobeiSubtitle(item, obj);
		
		return TaobaoUtil.composeTaobaoLine(obj);
	}

	protected void composeBaobeiPostageId(GoodsObject item, BaobeiPublishObject obj) {
        obj.postage_id = "// TODO";
		String str = "";
		obj.postage_id = "\"" + str + "\"";
	}
	protected void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiInputCustomCpv(item.colorNameList, taobaoColors);
		obj.input_custom_cpv = "\"" + str + "\"";
	}

	protected void composeBaobeiPicture(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		List<String> baobeiPictureNameList = Lists.newArrayList();
		baobeiPictureNameList.addAll(item.colorPicLocalNameList);
		baobeiPictureNameList.addAll(item.dressOnPicLocalNameList);
		str += TaobaoUtil.composeBaobeiPicture(baobeiPictureNameList, item.colorNameList, taobaoColors);
		obj.picture = "\"" + str + "\"";
	}

	protected void composeBaobeiPictureStatus(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		List<String> baobeiPictureNameList = Lists.newArrayList();
		baobeiPictureNameList.addAll(item.colorPicLocalNameList);
		baobeiPictureNameList.addAll(item.dressOnPicLocalNameList);
		str += TaobaoUtil.composeBaobeiPictureStatus(baobeiPictureNameList, item.colorNameList, taobaoColors);
		obj.picture_status = "\"" + str + "\"";
	}

	protected void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiPropAlias(item.sizeNameList, taobaoSizes, "20509");
		obj.propAlias = "\"" + str + "\"";
	}

	protected void composeBaobeiOuter_id(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += "MIZUNO_" + item.kataban;
		obj.outer_id = "\"" + str + "\"";
	}

	protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiSkuProps(item.colorNameList, item.sizeNameList, taobaoColors, taobaoSizes, obj.price);
		obj.skuProps = "\"" + str + "\"";
	}

	protected void composeBaobeiInputValues(GoodsObject item, BaobeiPublishObject obj) {
		String cid = "";
		cid += TaobaoUtil.composeBaobeiInputValues(item.colorNameList, taobaoColors);
		obj.inputValues = "\"" + cid + "\"";
	}

	protected void composeBaobeiInputPids(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		obj.inputPids = "\"" + str + "\"";
	}

	protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
		String str = "20000:84533669;";

		// 宝贝属性
		str += TaobaoUtil.composeBaobeiCateProps(item.colorNameList, item.sizeNameList, taobaoColors, taobaoSizes,
				"20509");

		obj.cateProps = "\"" + str + "\"";
	}

	protected void composeBaobeiSellerCids(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		obj.seller_cids = "\"" + str + "\"";
	}

	protected void composeBaobeiCId(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		
		obj.cid = "\"" + str + "\"";
	}

	protected void composeBaobeiPrice(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		int emsFee = 3000;
		double price = Integer.parseInt(item.price) * (1 + benefitRate);
		price += emsFee;
		price = price * currencyRate;
		str = new BigDecimal(price).setScale(0, RoundingMode.CEILING).toPlainString();
		obj.price = "\"" + str + "\"";
	}
	protected void composeBaobeiSubtitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleJP + "!" + item.kataban;
		obj.subtitle = "\"" + title + "\"";
	}

	protected void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "日本直邮 ";
		title += " " + item.brand + transitBrand(item.brand);
		title += " 包邮";
		// if (title.length() + suffix.length() < 60) {
		// title += suffix;
		// }
		obj.title = "\"" + title + "\"";
	}
	
	private String transitBrand(String brand) {
		// TODO 自動生成されたメソッド・スタブ
		return "";
	}
	protected void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) {
		StringBuffer detailSB = new StringBuffer();

		// 宝贝描述
		if (!"".equals(item.detailScreenShotPicFile)) {
			detailSB.append(composeDetailScreenShot(item));
		}
		// Size
		if (!"".equals(item.sizeTipScreenShotPicFile)) {
			detailSB.append(composeSizeTiplScreenShot(item));
		}
		// 着装图片
		if (!picturesForDetailPage.isEmpty()) {
			detailSB.append(composePictureForDetailMiaoshu(item));
		}
		// zhi you
		detailSB.append(BaobeiUtil.getExtraMiaoshu());
		obj.description = "\"" + detailSB.toString() + "\"";
	}
	protected Object composePictureForDetailMiaoshu(GoodsObject item) {
		StringBuffer detailSB = new StringBuffer();
		detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝图片</h3>");
		detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		for (String pic : picturesForDetailPage) {
			detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
					+ pic + "\"/></p>");
		}
		detailSB.append("</div>");
		return detailSB.toString();
	}

	protected String composeDetailScreenShot(GoodsObject item) {

		StringBuffer detailSB = new StringBuffer();
		detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
		detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
				+ item.detailScreenShotPicFile + "\"/></p>");
		detailSB.append("</div>");
		return detailSB.toString();
	}

	private Object composeSizeTiplScreenShot(GoodsObject item) {

		StringBuffer detailSB = new StringBuffer();
			
            detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">尺寸参考</h3>");
            detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
            detailSB.append("<p style=\"text-indent:2.0em;\">下单前，请认真比对尺寸大小！<span style=\";color:red;font-weight:bold\">店主本职工作很忙，请尽量自己比对哦。</span></p>");
            detailSB.append("<p style=\"text-indent:2.0em;\">如纠结 请咨询店主。店主有空时，第一时间回复。</p>");
            //detailSB.append("<p style=\"text-indent:2.0em;\">图片中标有<span style=\";color:red;font-weight:bold\">1,2，3。。。A,B，C。。。 </span>表格中有对应的数据！非常简单明了的Size表哦。不要被日语吓到了！</p>");
            
            detailSB.append("<p style=\"text-indent:2.0em;\"><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///" + item.sizeTipScreenShotPicFile + "\"/></p>");    
            
            detailSB.append("</div>");
		return detailSB.toString();
	}

	@Override
	public BaseBaobeiParser getParser() {
		return new RightonParser();
	}  

	private List<String> productUrlList = Lists.newArrayList();
	public RightonBaobeiProducer addProductUrl(String productUrl) {
		productUrlList.add(productUrl);
		return this;
	}

	private List<String> categoryUrlList = Lists.newArrayList();
	public RightonBaobeiProducer addCategoryUrl(String categoryUrl) {
		categoryUrlList.add(categoryUrl);
		return this;
	}

}
