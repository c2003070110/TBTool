package com.walk_nie.taobao.montBell.gear;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.montBell.GoodsObject;
import com.walk_nie.taobao.montBell.MontBellUtil;
import com.walk_nie.taobao.montBell.MontbellProductParser;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellEyeGrassBaobeiProducer extends BaseBaobeiProducer {

	private List<String> scanCategoryIds = Lists.newArrayList();
	private List<String> taobaoColors = Lists.newArrayList();
	{
		taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
		taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
		taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
		taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
	}
	private List<String> taobaoSizes = Lists.newArrayList();
	{
		// XS,S,M,L,XL,XXL,
		taobaoSizes.add("28313");
		taobaoSizes.add("28314");
		taobaoSizes.add("28315");
		taobaoSizes.add("28316");
		taobaoSizes.add("28317");
		taobaoSizes.add("28318");
	}

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> itemIdList = Lists.newArrayList();
			if (scanCategoryIds.isEmpty()) {

			} else {
				MontbellProductParser parer = new MontbellProductParser();
				parer.setPublishedbaobeiList(this.publishedbaobeiList);
				itemIdList = parer.scanItem(scanCategoryIds);
			}
			if (itemIdList.isEmpty())
				return;
			 
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

			for (GoodsObject obj : itemIdList) {
				MontBellUtil.downloadPicture(obj);
			}
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(outputFile);
			for (GoodsObject obj : itemIdList) {
				TaobaoUtil.copyFiles(obj.pictureNameList,
						MontBellUtil.getWebShopPicFolder(obj), taobaoPicFolder);
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

	protected void writeOut(BufferedWriter priceBw, GoodsObject item)
			throws Exception {

		priceBw.write(composeBaobeiLine(item));
		priceBw.flush();
	}

	protected String composeBaobeiLine(GoodsObject item) throws Exception {
		BaobeiPublishObject obj = new BaobeiPublishObject();
		BaobeiUtil.setBaobeiCommonInfo(obj);

		// 宝贝名称
		composeBaobeiTitle(item, obj);
		// 宝贝类目
		String categoryId = item.cateogryObj.categoryId;
		if("561000".equals(categoryId)){
			obj.cid = "50010368";
		}
		if("562000".equals(categoryId)){
			obj.cid = "50012517";
		}
		// 店铺类目
		obj.seller_cids = "1372090602";
		// 省
		//obj.location_state = "日本";
		// 宝贝价格
		obj.price = MontBellUtil.convertToCNYWithEmsFee(item, this.currencyRate,
				this.benefitRate);
		// obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "999";
		
        // 邮费模版ID
        obj.postage_id = MontBellUtil.composePostageId(item);
        
		// 宝贝属性
		composeBaobeiCateProps(item, obj);

		// 用户输入ID串;
		obj.inputPids = "\"20000\"";

		// 用户输入名-值对
		composeBaobeiInputValues(item, obj);

		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		// 销售属性组合
		composeBaobeiSkuProps(item, obj);
		// 商家编码
		obj.outer_id = "\"" + "MTBL_" + item.cateogryObj.categoryId + "-"
				+ item.productId +"\""  ;
		// 销售属性别名
		composeBaobeiPropAlias(item, obj);
		// 商品条形码
		// obj.barcode = item.sku;
		// 图片状态
		MontBellUtil.composeBaobeiPictureStatus(item, obj, taobaoColors);
		// 新图片
		MontBellUtil.composeBaobeiPicture(item, obj, taobaoColors);
		// 自定义属性值
		composeBaobeiInputCustomCpv(item, obj);
		// 宝贝卖点
		MontBellUtil.composeBaobeiSubtitle(item, obj);
		// 库存计数
		obj.sub_stock_type = "1";
		// 商品资质
		obj.qualification = "%7B%7D";
		// 增加商品资质
		//obj.add_qualification = "1";

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject baobei) {
		String title = "日本直邮 全场90";
		if (!StringUtil.isBlank(item.titleCN)) {
			title += " " + item.titleCN;
		}
		String categoryId = item.cateogryObj.categoryId;
		if("561000".equals(categoryId)){
			title += " 太阳镜";
		}
		if("562000".equals(categoryId)){
			title += " 护目镜";
		}
		if (!StringUtil.isBlank(item.titleEn)) {
			title += " " + item.titleEn;
		}
		if (!StringUtil.isBlank(item.gender)) {
			title += " " + item.gender;
		}
		title += " " + item.productId;
		// String suffix = "/包邮";
		// if (title.length() + suffix.length() < 60) {
		// title += suffix;
		// }
		baobei.title = "\"" + title + "\"";
	}

	protected void composeBaobeiCateProps(GoodsObject item,
			BaobeiPublishObject obj) {
		// cateProps　宝贝属性：1627207:-1001;1627207:-1002;1627207:-1003;1627207:-1004;1627207:-1005;1627207:-1006;1627207:-1007;1627207:-1008;1627207:-1009;20509:28381;20509:28313;20509:28314;20509:28315;20509:28316;20509:28317;20509:28319
		String categoryId = item.cateogryObj.categoryId;
		String cateProps = "";
		if(!"562000".equals(categoryId)){
			cateProps += "148326888:31867;";
		}

		// 宝贝属性
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			cateProps += "1627207:" + taobaoColors.get(i) + ";";
		}
		for (int i = 0; i < item.sizeList.size(); i++) {
			if (i >= taobaoSizes.size())
				break;
			cateProps += "20509:" + taobaoSizes.get(i) + ";";
		}
		obj.cateProps = "\"" + cateProps + "\"";
	}

	protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
		// skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
		String skuProps = "";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
				String num = "999";
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
						+ taobaoColors.get(i) +  ";";
		}
		obj.skuProps = "\"" + skuProps + "\"";
	}

	protected void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
		// montbell;型号;1109136
		String inputValues = "montbell;型号;" + item.productId 
				+ ",";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			inputValues += item.colorList.get(i) + "颜色分类;";
		}
		obj.inputValues = "\"" + inputValues + "\"";
	}

	protected void composeBaobeiPropAlias(GoodsObject item,
			BaobeiPublishObject obj) {
		// propAlias　销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
		String propAlias = "";
		// 销售属性别名
		for (int i = 0; i < item.sizeList.size(); i++) {
			if (i >= taobaoSizes.size())
				break;
			propAlias += "20509:" + taobaoSizes.get(i) + ":"
					+ item.sizeList.get(i) + ";";
		}
		obj.propAlias = "\"" + propAlias + "\"";
	}

	protected void composeBaobeiInputCustomCpv(GoodsObject item,
			BaobeiPublishObject obj) {
		String inputCustomCpv = "";
		// 自定义属性值
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			// 1627207:-1001:color1;
			inputCustomCpv += "1627207:" + taobaoColors.get(i) + ":"
					+ item.colorList.get(i) + ";";
		}
		obj.input_custom_cpv = "\"" + inputCustomCpv + "\"";
	}

	private String composeBaobeiMiaoshu(GoodsObject item) throws IOException {

		StringBuffer detailSB = new StringBuffer();
		if (!item.detailScreenShotPicFile.isEmpty()) {
			detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
			detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
			for (String productInfo : item.detailScreenShotPicFile) {
			detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///"
					+ productInfo + "\"/></p>");
			detailSB.append("</div>");
			}
		}
		StringBuffer sizeTips = new StringBuffer();
		if (!item.sizeTipPics.isEmpty()) {
			detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">尺寸参考</h3>");
			detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
			detailSB.append("<p>下单前，请认真比对尺寸大小！<span style=\";color:red;font-weight:bold\">不能因为尺寸问题 不能取消订单！！不能退款！！！</span></p>");
			for (String sizeTip : item.sizeTipPics) {
				detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;\" src=\"FILE:///"
						+ sizeTip + "\"/></p>");
			}
			detailSB.append("</div>");
		}
		//String extraMiaoshu = MontBellUtil.composeExtraMiaoshu();
		String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
		return "\"" + detailSB.toString() + sizeTips.toString() 
				+ extraMiaoshu1 + "\"";
	}

	public MontbellEyeGrassBaobeiProducer addScanCategory(String scanCategoryId) {

		this.scanCategoryIds.add(scanCategoryId);
		return this;
	}

	@Override
	public BaseBaobeiParser getParser() {
		return new MontbellProductParser();
	}

}
