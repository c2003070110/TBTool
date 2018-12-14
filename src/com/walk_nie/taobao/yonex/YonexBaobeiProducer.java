package com.walk_nie.taobao.yonex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class YonexBaobeiProducer extends BaseBaobeiProducer {

	private File priceFile = new File(YonexUtil.priceListFile);
	private List<String> priceList = Lists.newArrayList();
	// 0:all;1:badminton racquets;2:badminton shoes;3:tennis racquets;4:tennis
	// shoes;
	private int categoryType = 0;
	private List<String> taobaoColors = Lists.newArrayList();
	{
		taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");taobaoColors.add("-1004");
		taobaoColors.add("-1005");taobaoColors.add("-1006");taobaoColors.add("-1007");taobaoColors.add("-1008");
		taobaoColors.add("-1009");taobaoColors.add("-1010");taobaoColors.add("-1011");taobaoColors.add("-1012");
	}

	private List<String> taobaoSizes = Lists.newArrayList();
	{
		// XS,S,M,L,XL,XXL,
		taobaoSizes.add("28313");taobaoSizes.add("28314");taobaoSizes.add("28315");taobaoSizes.add("28316");
		taobaoSizes.add("28317");taobaoSizes.add("28318");
	}

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			if (priceFile.exists()) {
				priceList = Files.readLines(priceFile, Charset.forName("UTF-8"));
			}
			List<GoodsObject> itemIdList = ((YonexProductParser) getParser()).setCategoryType(categoryType).scanItem();
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile,
					DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-16"));

			for (GoodsObject obj : itemIdList) {
				downloadPicture(obj, YonexUtil.getPictureSavePath(obj));
			}
			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
			for (GoodsObject obj : itemIdList) {
				// FIXME 鞋码 -> Discard
				obj.sizeList.clear();
				if (obj.categoryType == 1) {
					obj.sizeList.add("空拍");
				} else if (obj.categoryType == 2) {
					obj.sizeList.add("鞋码留言 厘米单位");
				} else if (obj.categoryType == 3) {
					obj.sizeList.add("空拍");
				} else if (obj.categoryType == 4) {
					obj.sizeList.add("鞋码留言 厘米单位");
				}
				TaobaoUtil.copyFiles(obj.pictureNameList, YonexUtil.getPictureSavePath(obj), taobaoPicFolder);

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

	protected void downloadPicture(GoodsObject goods, String outputFile) {
		int i = 0;
		for (String picUrl : goods.pictureList) {
			try {
				String picName = "yonex_" + goods.kataban + "_" + i;
				TaobaoUtil.downloadPicture(outputFile, picUrl, picName);
				goods.pictureNameList.add(picName);
				i++;
			} catch (Exception ex) {
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
		obj.postage_id = "// TODO";
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

	private void composeBaobeiInputCustomCpv(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiInputCustomCpv(item.colorList, taobaoColors);
		obj.input_custom_cpv = "\"" + str + "\"";
	}

	private void composeBaobeiPicture(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiPicture(item.pictureNameList, item.colorList, taobaoColors);
		obj.picture = "\"" + str + "\"";
	}

	private void composeBaobeiPictureStatus(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiPictureStatus(item.pictureNameList, item.colorList, taobaoColors);
		obj.picture_status = "\"" + str + "\"";
	}

	private void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiPropAlias(item.sizeList, taobaoSizes, "20509");
		obj.propAlias = "\"" + str + "\"";
	}

	private void composeBaobeiOuter_id(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += "YONEX_" + item.kataban;
		obj.outer_id = "\"" + str + "\"";
	}

	private void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiSkuProps(item.colorList, item.sizeList, taobaoColors, taobaoSizes, obj.price);
		obj.skuProps = "\"" + str + "\"";
	}

	private void composeBaobeiInputValues(GoodsObject item, BaobeiPublishObject obj) {
		String cid = "";
		cid += TaobaoUtil.composeBaobeiInputValues(item.colorList, taobaoColors);
		obj.inputValues = "\"" + cid + "\"";
	}

	private void composeBaobeiInputPids(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		obj.inputPids = "\"" + str + "\"";
	}

	protected void composeBaobeiCateProps(GoodsObject item, BaobeiPublishObject obj) {
		String str = "20000:84533669;";

		// 宝贝属性
		str += TaobaoUtil.composeBaobeiCateProps(item.colorList, item.sizeList, taobaoColors, taobaoSizes,
				"20509");

		obj.cateProps = "\"" + str + "\"";
	}

	private void composeBaobeiSellerCids(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		obj.seller_cids = "\"" + str + "\"";
	}

	private void composeBaobeiCId(GoodsObject item, BaobeiPublishObject obj) {
		String cid = "";
		if (item.categoryType == 1) {
			// "羽毛球拍"
			cid = "50012323";
		} else if (item.categoryType == 2) {
			// "羽毛球鞋/"
			cid = "50012331";
		} else if (item.categoryType == 3) {
			// 网球球拍
			cid = "50012323";
		} else if (item.categoryType == 4) {
			// 网球鞋
			cid = "50012037";
		} else {
			cid = "";
		}
		obj.cid = "\"" + cid + "\"";
	}

	private void composeBaobeiPrice(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		for (String line : priceList) {
			String[] spl = line.split("\t");
			if (item.kataban.equals(spl[1])) {
				str = spl[4].replaceAll(",", "");
				break;
			}
		}
		obj.price = "\"" + str + "\"";
	}

	private void composeBaobeiSubtitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "\"日本直邮！100%正品！日本代购！包邮！" + item.titleJP + "!" + item.kataban;
		obj.subtitle = "\"" + title + "\"";
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "日本直邮 Yonex/尤尼克斯";
		title += item.title + " " + item.kataban;
		title += " " + translateTitle(item);
		//title += " 包邮";
		if(item.producePlace.equals("日本"))title +=  " " + item.producePlace + "制";
		obj.title = "\"" + title + "\"";
	}

	private String translateTitle(GoodsObject goodsObj) {
		if (goodsObj.categoryType == 1) {
			return "羽毛球拍" ;
		} else if (goodsObj.categoryType == 2) {
			return "羽毛球鞋";
		} else if (goodsObj.categoryType == 3) {
			return "网球拍";
		} else if (goodsObj.categoryType == 4) {
			return "网球鞋";
		} else {
			return "";
		}
	}

	protected void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) throws IOException {
		StringBuffer detailSB = new StringBuffer();

		// 宝贝描述
		if (!"".equals(item.detailScreenShotPicFile)) {
			detailSB.append(composeDetailScreenShot(item));
		}
		// 着装图片
		if (!item.pictureList.isEmpty()) {
			detailSB.append(composeDressOnMiaoshu(item));
		}
		// zhi you
		detailSB.append(BaobeiUtil.getExtraMiaoshu());
		obj.description = "\"" + detailSB.toString() + "\"";
	}

	private Object composeDressOnMiaoshu(GoodsObject item) {
		StringBuffer detailSB = new StringBuffer();
		detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝图片</h3>");
		detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		for (String pic : item.pictureList) {
			detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
					+ pic + "\"/></p>");
		}
		detailSB.append("</div>");
		return detailSB.toString();
	}

	private String composeDetailScreenShot(GoodsObject item) {

		StringBuffer detailSB = new StringBuffer();
		detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
		detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
				+ item.detailScreenShotPicFile + "\"/></p>");
		detailSB.append("</div>");
		return detailSB.toString();
	}

	public YonexBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	public YonexBaobeiProducer setCategoryType(int categoryType) {
		this.categoryType = categoryType;
		return this;
	}

	@Override
	public BaseBaobeiParser getParser() {
		return new YonexProductParser();
	}

}