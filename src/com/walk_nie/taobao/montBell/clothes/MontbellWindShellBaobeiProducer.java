package com.walk_nie.taobao.montBell.clothes;

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

public class MontbellWindShellBaobeiProducer extends BaseBaobeiProducer {

	private List<String> scanCategoryIds = Lists.newArrayList();
	private List<String> taobaoColors = Lists.newArrayList();
	{
		// taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
		// taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
		// taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
		// taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
		taobaoColors.add("3741675");
		taobaoColors.add("3574624");
		taobaoColors.add("3579132");
		taobaoColors.add("3614432");
		taobaoColors.add("28635738");
		taobaoColors.add("28635737");
		taobaoColors.add("7928402");
		taobaoColors.add("366446425");
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
		taobaoSizes.add("3727387");
	}

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> itemIdList = Lists.newArrayList();
			if (scanCategoryIds.isEmpty()) {

			} else {
				MontbellProductParser parser = new MontbellProductParser();
				parser.setPublishedbaobeiList(this.publishedbaobeiList);
				parser.scanFOFlag = true;
				itemIdList = parser.scanItem(scanCategoryIds);
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
		//  户外风衣
		obj.cid = "50013933";
		// 店铺类目
		obj.seller_cids = "1372086399";
		if (!StringUtil.isBlank(MontBellUtil.spececialCateId)) {
			obj.seller_cids += "," + MontBellUtil.spececialCateId;
		}
		// 省
		obj.location_state = "日本";
		// 宝贝价格
		//obj.price = MontBellUtil.convertToCNYWithEmsFee(item, this.currencyRate,
		//		this.benefitRate);
		composeBaobeiPrice(item, obj);
		// 宝贝数量
		obj.num = "99";
		
        // 邮费模版ID
        obj.postage_id = MontBellUtil.composePostageId(item);

		// 用户输入ID串;
		//obj.inputPids = "\"20000,13021751,6103476,1627207\"";
		obj.inputPids = "\"6103476,13021751,1627207\"";

		// 用户输入名-值对
		// obj.inputValues = "\"montbell,"+item.productId+",*\"";
		composeBaobeiInputValues(item, obj);

		// 宝贝描述
		//obj.description = composeBaobeiMiaoshu(item);
		composeBaobeiMiaoshu(item, obj);

		// 宝贝属性
		composeBaobeiCateProps(item, obj);
		// 销售属性组合
		composeBaobeiSkuProps(item, obj);
		// 商家编码
		obj.outer_id = "MTBL_" + item.cateogryObj.categoryId + "-"
				+ item.productId;
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
		obj.add_qualification = "1";

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private void composeBaobeiPrice(GoodsObject item, BaobeiPublishObject obj) {
		String priceStr = item.priceJPY;
        try {
            int price = Integer.parseInt(priceStr);
			if (price < 2000){
				price = price + 200;
			}else if (price < 5000){
				price = price + 400;
			}else if (price < 8000){
				price = price + 200;
			}else if (price < 10000){
				price = price + 100;
			}
            long priceTax  = Math.round(price*1.08);
            double priceCNY = priceTax * currencyRate;
			priceCNY = priceCNY + 70;
			obj.price =  String.valueOf(Math.round(priceCNY));
        } catch (Exception ex) {
            ex.printStackTrace();
            obj.price = "XXXXXX";
        }
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject baobei) {
		String title = "\"";
		if (canPinyou(item)) {
			title += " 日本拼邮免邮";
		}else{
			title += " 日本直邮";
		}
	
		//title += "" + item.titleCN;
		title += " MontBell";
		String categoryId = item.cateogryObj.categoryId;
		if ("11500".equals(categoryId)) {
			// 超軽量ウインドブレーカー
			title += " 超轻皮肤衣";
		} else if ("11000".equals(categoryId)) {
			// ウインドブレーカー
			title += " 户外风衣";
		}
        if(!StringUtil.isBlank(item.titleEn)){
            title += " " + item.titleEn ;
        }
		title += " " + item.productId;
        //if(!StringUtil.isBlank(MontBellUtil.spececialProductId)){
        //    title += MontBellUtil.spececialProductId ;
        //}
		if (!StringUtil.isBlank(item.gender)) {
			title += " " + item.gender;
		}

		baobei.title = title + "\"";
	}

	protected void composeBaobeiCateProps(GoodsObject item,
			BaobeiPublishObject obj) {
		String cateProps = "";
		cateProps += "20000:84533669;122216608:29923;21548:42580;";
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
		obj.cateProps = cateProps;
	}

	protected void composeBaobeiSkuProps(GoodsObject item,
			BaobeiPublishObject obj) {
		// skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
		String skuProps = "";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			if (item.sizeList.isEmpty()) {
				String num = "99";
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
						+ taobaoColors.get(i) + ";";
			}else{
			for (int j = 0; j < item.sizeList.size(); j++) {
				if (j >= taobaoSizes.size())
					break;
				String num = MontBellUtil.getStock(item, item.colorList.get(i),
						item.sizeList.get(j));
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
						+ taobaoColors.get(i) + ";20509:" + taobaoSizes.get(j)
						+ ";";

			}
			}
		}
		obj.skuProps = skuProps;
	}

	private void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
		// MONTBELL,1106570,1234,color1;颜色分类;color2;颜色分类;color3
		String inputValues = "\"" + obj.price + "," + item.productId + ",";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			inputValues += item.colorList.get(i) + "颜色分类;";
		}
		obj.inputValues = inputValues + "\"";
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
		obj.propAlias = propAlias;
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
		obj.input_custom_cpv = inputCustomCpv;
	}

	public MontbellWindShellBaobeiProducer addScanCategory(String scanCategoryId) {

		this.scanCategoryIds.add(scanCategoryId);
		return this;
	}

	@Override
	public BaseBaobeiParser getParser() {
		return new MontbellProductParser();
	}

	protected void composeBaobeiMiaoshu(GoodsObject item,
			BaobeiPublishObject obj) {
        StringBuffer detailSB = new StringBuffer();

        // 包邮
		if (canPinyou(item)) {
			detailSB.append(MontBellUtil.composePingyouMiaoshu(0));
		} else {
			detailSB.append(MontBellUtil.composeBaoyouMiaoshu());
		}
        // 关税
		detailSB.append(MontBellUtil.composeHaigaiMiaoshu());

        // 尺寸描述
        detailSB.append(MontBellUtil.composeSizeTipMiaoshu(item));
        
        // 着装图片
        detailSB.append(MontBellUtil.composeDressOnMiaoshu(item.dressOnPics));
        
        // 宝贝描述
        item.specialPageScreenShotPicFile.add("https://img.alicdn.com/imgextra/i3/3910559931/O1CN01OXrRTZ2NERpOojV7x_!!3910559931.jpg");
        item.specialPageScreenShotPicFile.add("https://img.alicdn.com/imgextra/i2/3910559931/O1CN012DYmIM2NERpROxoQu_!!3910559931.jpg");
        item.specialPageScreenShotPicFile.add("https://img.alicdn.com/imgextra/i3/3910559931/O1CN010Riyg62NERpSatIFB_!!3910559931.jpg");
        detailSB.append(MontBellUtil.composeProductInfoMiaoshu(item));
        
        //String extraMiaoshu = MontBellUtil.composeExtraMiaoshu();
        String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
        obj.description =  "\"" + detailSB.toString() + extraMiaoshu1+ "\"";
	}

	private boolean canPinyou(GoodsObject item) {
		if (Integer.parseInt(item.priceJPY) < 10000) {
			return true;
		}
		return false;
	}

}
