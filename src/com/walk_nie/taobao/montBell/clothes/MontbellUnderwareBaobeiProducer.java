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

public class MontbellUnderwareBaobeiProducer extends BaseBaobeiProducer {

	private List<String> scanCategoryIds = Lists.newArrayList();
	private List<String> taobaoColors = Lists.newArrayList();
	{
		// taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
		// taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
		// taobaoColors.add("-1007");taobaoColors.add("-1008");taobaoColors.add("-1009");
		// taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
		taobaoColors.add("3741675");taobaoColors.add("3574624");taobaoColors.add("3579132");
		taobaoColors.add("3614432");taobaoColors.add("28635738");taobaoColors.add("28635737");
		taobaoColors.add("7928402");taobaoColors.add("	");taobaoColors.add("366446425");
	}
	// private List<String> taobaoSizes = Lists.newArrayList();
	// {
	// taobaoSizes.add("28381");taobaoSizes.add("28313");taobaoSizes.add("28314");
	// taobaoSizes.add("28315");taobaoSizes.add("28316");taobaoSizes.add("28317");
	// taobaoSizes.add("28319");
	// }
	private List<String> taobaoSizes = Lists.newArrayList();
	{
		// XS,S,M,L,XL,XXL,
		taobaoSizes.add("28313");taobaoSizes.add("28314");taobaoSizes.add("28315");
		taobaoSizes.add("28316");taobaoSizes.add("28317");taobaoSizes.add("28318");
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
		obj.cid = "121408015";
		// 店铺类目
		obj.seller_cids = "1372086398";
		if (!StringUtil.isBlank(MontBellUtil.spececialCateId)) {
			obj.seller_cids += "," + MontBellUtil.spececialCateId;
		}
		// 省
		obj.location_state = "日本";
		// 宝贝价格
		//obj.price = MontBellUtil.convertToCNYWithEmsFee(item, this.currencyRate,
		//		this.benefitRate);
		composeBaobeiPrice(item, obj);
		// obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "999";

        // 邮费模版ID
        obj.postage_id = MontBellUtil.composePostageId(item);

		// 用户输入ID串;
		obj.inputPids = "\"13021751,6103476,1627207\"";

		// 用户输入名-值对
		// obj.inputValues = "\"montbell,"+item.productId+",*\"";
		composeBaobeiInputValues(item, obj);

		// 宝贝描述
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
		composeBaobeiPictureStatus(item, obj);
		// 新图片
		composeBaobeiPicture(item, obj);
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
		String title = "\"日本";
		title += "拼邮免邮";
		//title += "" + item.titleCN;
		title += " MontBell";
		if (item.titleOrg.indexOf("Tシャツ") > 0) {
			title += " 内衣";
		} else if (item.titleOrg.indexOf("ブラ") > 0) {
			title += " 文胸";
		} else if (item.titleOrg.indexOf("シャツ") > 0) {
			title += " 内衣";
		} else if (item.titleOrg.indexOf("タイツ") > 0) {
			title += " 紧身裤";
		} else if (item.titleOrg.indexOf("トランクス") > 0) {
			title += " 内裤";
		} else if (item.titleOrg.indexOf("ブリーフ") > 0) {
			title += " 内裤";
		} else if (item.titleOrg.indexOf("ショーツ") > 0) {
			title += " 内裤";
		//} else if (item.titleOrg.indexOf("ウエストウォーマー") > 0) {
		//	title += " 护腰";
		} else if (item.titleOrg.indexOf("") > 0) {
			title += " ";
		} else if (item.titleOrg.indexOf("") > 0) {
			title += " ";
		}
		String categoryId = item.cateogryObj.categoryId;
		if("75200".equals(categoryId)){
			// スーパーメリノウール（厚手／エクスペディション）
			title += " 厚手 美丽奴羊毛";
		}
		if("75100".equals(categoryId)){
			// スーパーメリノウール（中厚手／ミドルウエイト）
			title += " 中厚手 美丽奴羊毛";
		}
		if("75000".equals(categoryId)){
			// スーパーメリノウール（薄手／ライトウエイト）
			title += " 薄厚手 美丽奴羊毛";
		}
		if("71200".equals(categoryId)){
			//  ジオライン（厚手／エクスペディション）
			title += " 厚手 ZEOLINE";
		}
		if("71100".equals(categoryId)){
			//  ジオライン（中厚手／ミドルウエイト）
			title += " 中厚 ZEOLINE";
		}
		if("71000".equals(categoryId)){
			//  ジオライン（薄手／ライトウエイト）
			title += " 薄手 ZEOLINE";
		}
		if("71700".equals(categoryId)){
			//  ウエストウォーマー
			title += " 护腰";
		}
		if("76000".equals(categoryId)){
			//  スペリオルシルク（薄手）
			title += " 薄手 高级丝绸";
		}
//        if(!StringUtil.isBlank(item.titleEn)){
//            title += " " + item.titleEn ;
//        }
		title += " " + item.productId;
        if(!StringUtil.isBlank(MontBellUtil.spececialProductId)){
            title += MontBellUtil.spececialProductId ;
        }
		if (!StringUtil.isBlank(item.gender)) {
			title += " " + item.gender;
		}

		baobei.title = title + "\"";
	}

	protected void composeBaobeiCateProps(GoodsObject item,
			BaobeiPublishObject obj) {
		// cateProps　宝贝属性：1627207:-1001;1627207:-1002;1627207:-1003;1627207:-1004;1627207:-1005;1627207:-1006;1627207:-1007;1627207:-1008;1627207:-1009;20509:28381;20509:28313;20509:28314;20509:28315;20509:28316;20509:28317;20509:28319
		String cateProps = "";

		cateProps += "20000:84533669;122216608:29923;21548:42440;";

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

	protected void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
		// skuProps　销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
		String skuProps = "";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			for (int j = 0; j < item.sizeList.size(); j++) {
				if (j >= taobaoSizes.size())
					break;
                String num  = MontBellUtil.getStock(item, item.colorList.get(i),
                		item.sizeList.get(j));
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":"
						+ taobaoColors.get(i) + ";20509:" + taobaoSizes.get(j)
						+ ";";
				// skuProps += "20509:" + taobaoSizes.get(j) +":"+ obj.price +
				// ":9999" + ":" + ":1627207" + ":" + taobaoColors.get(i)
				// + ";";
			}
		}
		obj.skuProps = skuProps;
	}

	private void composeBaobeiInputValues(GoodsObject item,
			BaobeiPublishObject obj) {
		// ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
		String inputValues = "\"" + item.productId + "," + obj.price
				+ ",";
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

	protected void composeBaobeiMiaoshu(GoodsObject item,
			BaobeiPublishObject obj)   {

		StringBuffer detailSB = new StringBuffer();
        // 包邮
        //detailSB.append(MontBellUtil.composeBaoyouMiaoshu());
        detailSB.append(MontBellUtil.composePingyouMiaoshu(0));
        // 关税
		detailSB.append(MontBellUtil.composeHaigaiMiaoshu());

        // 尺寸描述
        detailSB.append(MontBellUtil.composeSizeTipMiaoshu(item));
        
        // 着装图片
        detailSB.append(MontBellUtil.composeDressOnMiaoshu(item.dressOnPics));
        
        // 宝贝描述
        item.specialPageScreenShotPicFile.add("https://img.alicdn.com/imgextra/i1/3910559931/O1CN01py1L0u2NERpO8f7yt_!!3910559931.jpg");
        item.specialPageScreenShotPicFile.add("https://img.alicdn.com/imgextra/i4/3910559931/O1CN01SWZmwh2NERpQdseFW_!!3910559931.jpg");
        item.specialPageScreenShotPicFile.add("https://img.alicdn.com/imgextra/i3/3910559931/O1CN01PvskT62NERpRR5zx4_!!3910559931.jpg");
        detailSB.append(MontBellUtil.composeProductInfoMiaoshu(item));
		 
		//String extraMiaoshu = MontBellUtil.composeExtraMiaoshu();
		String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
		obj.description =  "\"" + detailSB.toString() 
				+ extraMiaoshu1 + "\"";
	}

	public MontbellUnderwareBaobeiProducer addScanCategory(String scanCategoryId) {

		this.scanCategoryIds.add(scanCategoryId);
		return this;
	}

	@Override
	public BaseBaobeiParser getParser() {
		return new MontbellProductParser();
	}

	protected void composeBaobeiPictureStatus(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		MontBellUtil.composeBaobeiPictureStatus(item, publishedBaobei,
				this.taobaoColors);
	}

	protected void composeBaobeiPicture(GoodsObject item,
			BaobeiPublishObject publishedBaobei) {
		MontBellUtil.composeBaobeiPicture(item, publishedBaobei, this.taobaoColors);
	}

}
