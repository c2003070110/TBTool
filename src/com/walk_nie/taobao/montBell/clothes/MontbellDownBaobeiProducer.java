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

public class MontbellDownBaobeiProducer extends BaseBaobeiProducer {

	private List<String> scanCategoryIds = Lists.newArrayList();
	private List<String> taobaoColors = Lists.newArrayList();
	{
		taobaoColors.add("-1001");
		taobaoColors.add("-1002");
		taobaoColors.add("-1003");
		taobaoColors.add("-1004");
		taobaoColors.add("-1005");
		taobaoColors.add("-1006");
		taobaoColors.add("-1007");
		taobaoColors.add("-1008");
		taobaoColors.add("-1009");
		taobaoColors.add("-1010");
		taobaoColors.add("-1011");
		taobaoColors.add("-1012");
		// taobaoColors.add("3741675");taobaoColors.add("3574624");taobaoColors.add("3579132");
		// taobaoColors.add("3614432");taobaoColors.add("28635738");taobaoColors.add("28635737");
		// taobaoColors.add("7928402");taobaoColors.add("85186009");taobaoColors.add("366446425");
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
				MontbellProductParser parser = new MontbellProductParser();
				parser.setPublishedbaobeiList(this.publishedbaobeiList);
				itemIdList = parser.scanItem(scanCategoryIds);
			}
			if (itemIdList.isEmpty())
				return;
			priceBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

			for (GoodsObject obj : itemIdList) {
				MontBellUtil.downloadPicture(obj);
			}
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(outputFile);
			for (GoodsObject obj : itemIdList) {
				TaobaoUtil.copyFiles(obj.pictureNameList, MontBellUtil.getWebShopPicFolder(obj),
						taobaoPicFolder);
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

	protected void writeOut(BufferedWriter priceBw, GoodsObject item) throws Exception {

		priceBw.write(composeBaobeiLine(item));
		priceBw.flush();
	}

	protected String composeBaobeiLine(GoodsObject item) throws Exception {

		BaobeiPublishObject obj = new BaobeiPublishObject();
		BaobeiUtil.setBaobeiCommonInfo(obj);
		// 宝贝名称
		composeBaobeiTitle(item, obj);
		// 宝贝类目
		obj.cid = "50014798";
		// 店铺类目
		obj.seller_cids = "1372086392";
		if (!StringUtil.isBlank(MontBellUtil.spececialCateId)) {
			obj.seller_cids += "," + MontBellUtil.spececialCateId;
		}
		// 省
		obj.location_state = "\"日本\"";
		// 宝贝价格
		obj.price = MontBellUtil.convertToCNYWithEmsFee(item, this.currencyRate, this.benefitRate);
		// obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "99";

		// 邮费模版ID
		obj.postage_id = MontBellUtil.composePostageId(item);
		// obj.postage_id = "1780373930";

		// 用户输入ID串;
		// obj.inputPids = "\"20000,13021751,6103476\"";
		// ダウンジャケット
		// obj.inputPids = "\"20000,13021751,6103476,1627207\"";
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
		obj.outer_id = MontBellUtil.composeOuter_id(item);
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

		// 闪电发货
		obj.is_lighting_consigment = "80";
		// 新品
		obj.is_xinpin = "247";

		// 商品资质
		obj.qualification = "%7B%7D";
		// 增加商品资质
		obj.add_qualification = "0";

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject baobei) {
		String title = "\"日本直邮 MontBell";
		// title += " " + item.titleCN ;
		String cateId = item.cateogryObj.categoryId;
		if ("131000".equals(cateId)) {
			// ダウンジャケット
			title += " 羽绒衣";
		} else if ("137000".equals(cateId)) {
			// ダウンジャケット（軽量シリーズ）
			title += " 超轻羽绒衣";
		} else if ("137500".equals(cateId)) {
			// 半袖ダウンジャケット
			title += " 短袖羽绒衣";
		} else if ("134000".equals(cateId)) {
			// ダウンベスト
			title += " 羽绒背心";
		} else if ("136000".equals(cateId)) {
			// コート（中綿入り）
			title += " 羽绒风衣";
		} else if ("138000".equals(cateId)) {
			// ダウンパンツ
			title += " 羽绒裤";
		} else if ("136500".equals(cateId)) {
			// ダウンはんてん（半纏）
			title += " 羽绒内衣";
		} else if ("132000".equals(cateId)) {
			// ダウン（極地用）
			title += " 极地羽绒服";
		} else if ("138600".equals(cateId)) {
			// ダウンマフラー/ブランケット
			title += " 羽绒垫";
		}
		if (!StringUtil.isBlank(item.titleEn)) {
			title += " " + item.titleEn;
			title = title.replace(" Down", "");
		}
		title += " " + item.productId;
		if (!StringUtil.isBlank(MontBellUtil.spececialProductId)) {
			title += MontBellUtil.spececialProductId;
		}
		if (!StringUtil.isBlank(item.gender)) {
			title += " " + item.gender;
		}
		// String suffix = "/包邮";
		// if (title.length() + suffix.length() < 60) {
		// title += suffix;
		// }
		baobei.title = title + "\"";
	}

	protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
		// cateProps
		// 宝贝属性：1627207:-1001;1627207:-1002;1627207:-1003;1627207:-1004;1627207:-1005;1627207:-1006;1627207:-1007;1627207:-1008;1627207:-1009;20509:28381;20509:28313;20509:28314;20509:28315;20509:28316;20509:28317;20509:28319
		//String prodCId = item.cateogryObj.categoryId;
		String cateProps = "";
		// ダウンジャケット
		// cateProps +=
		// "20000:6217823;13021751:61043120;6103476:3231061;122216608:29923;21548:38488;";
		// cateProps +=
		// "20000:84533669;13021751:61043120;6103476:3231061;122216608:29923;21548:38488;";
		// cateProps +=
		// "20000:84533669;13021751:124459527;6103476:3375990;122216608:29923;21548:38488;";
		cateProps += "20000:84533669;122216608:29923;21548:38488;6861561:112402;141750102:36229752;148158672:20347329;";

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
		// skuProps
		// 销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
		String skuProps = "";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			if (item.sizeList.isEmpty()) {
				String num = "99";
				skuProps += obj.price + ":" + num + ":" + ":1627207" + ":" + taobaoColors.get(i) + ";";
			} else {
				for (int j = 0; j < item.sizeList.size(); j++) {
					if (j >= taobaoSizes.size())
						break;
					String num = MontBellUtil.getStock(item, item.colorList.get(i), item.sizeList.get(j));
					skuProps += obj.price + ":" + num + ":" + ":1627207" + ":" + taobaoColors.get(i) + ";20509:"
							+ taobaoSizes.get(j) + ";";
					// skuProps += "20509:" + taobaoSizes.get(j) +":"+ obj.price
					// + ":9999" + ":" + ":1627207" + ":" + taobaoColors.get(i)
					// + ";";
				}
			}
		}
		obj.skuProps = skuProps;
	}

	private void composeBaobeiInputValues(GoodsObject item, BaobeiPublishObject obj) {
		// ダウンジャケット MONTBELL,1101464,1234,GRL;颜色分类;GML
		String inputValues = "\"" + item.productId + "," + obj.price + ",";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			inputValues += item.colorList.get(i) + ";颜色分类;";
		}
		obj.inputValues = inputValues + "\"";
	}

	protected void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
		// propAlias
		// 销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
		String propAlias = "";
		// 销售属性别名
		for (int i = 0; i < item.sizeList.size(); i++) {
			if (i >= taobaoSizes.size())
				break;
			propAlias += "20509:" + taobaoSizes.get(i) + ":" + item.sizeList.get(i) + ";";
		}
		obj.propAlias = propAlias;
	}

	protected void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
		String inputCustomCpv = "";
		// 自定义属性值
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			// 1627207:-1001:color1;
			inputCustomCpv += "1627207:" + taobaoColors.get(i) + ":" + item.colorList.get(i) + ";";
		}
		obj.input_custom_cpv = inputCustomCpv;
	}

	protected void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) {
		StringBuffer detailSB = new StringBuffer();
		// 包邮
		detailSB.append(MontBellUtil.composeBaoyouMiaoshu());
        // 关税
		detailSB.append(MontBellUtil.composeHaigaiMiaoshu());

		// 尺寸描述
		detailSB.append(MontBellUtil.composeSizeTipMiaoshu(item));

		// 宝贝描述
		detailSB.append(MontBellUtil.composeProductInfoMiaoshu(item));

		// 着装图片
		detailSB.append(MontBellUtil.composeDressOnMiaoshu(item.dressOnPics));

		// String extraMiaoshu = MontBellUtil.composeExtraMiaoshu();
		String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
		obj.description = "\"" + detailSB.toString() + extraMiaoshu1 + "\"";
	}


	public MontbellDownBaobeiProducer addScanCategory(String scanCategoryId) {

		this.scanCategoryIds.add(scanCategoryId);
		return this;
	}

	@Override
	public BaseBaobeiParser getParser() {
		return new MontbellProductParser();
	}

	protected void composeBaobeiPictureStatus(GoodsObject item, BaobeiPublishObject publishedBaobei) {
		MontBellUtil.composeBaobeiPictureStatus(item, publishedBaobei, this.taobaoColors);
	}

	protected void composeBaobeiPicture(GoodsObject item, BaobeiPublishObject publishedBaobei) {
		MontBellUtil.composeBaobeiPicture(item, publishedBaobei, this.taobaoColors);
	}

}