package com.walk_nie.taobao.eizo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.util.NieConfig;

public class EizoBaobeiProducer extends BaseBaobeiProducer {

	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<EizoProductObject> itemIdList = Lists.newArrayList();

			List<String> productUrls = readProductUrls();
			if (!productUrls.isEmpty()) {
				EizoProductParser parser = new EizoProductParser();
				parser.setPublishedbaobeiList(this.publishedbaobeiList);
				itemIdList = parser.scanItemByProductUrlList(productUrls);
			}
			if (itemIdList.isEmpty())
				return;
			priceBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-16"));

			String taobaoPicFolder = TaobaoUtil.getPictureFolder(outputFile);
			downloadAndCopyPicture(itemIdList, taobaoPicFolder);

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			for (EizoProductObject obj : itemIdList) {
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

	protected void writeOut(BufferedWriter priceBw, EizoProductObject item) throws Exception {

		priceBw.write(composeBaobeiLine(item));
		priceBw.flush();
	}

	protected String composeBaobeiLine(EizoProductObject item) throws Exception {

		BaobeiPublishObject obj = new BaobeiPublishObject();
		BaobeiUtil.setBaobeiCommonInfo(obj);
		// 宝贝名称
		composeBaobeiTitle(item, obj);
		// 宝贝类目
		obj.cid = "110502";
		// 店铺类目
		obj.seller_cids = "1423155504";
		// 省
		obj.location_state = "\"日本\"";
		// 宝贝价格
		obj.price = EizoUtil.convertToCNYWithEmsFee(item);
		// obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "99";

		// 邮费模版ID
		obj.postage_id = "13528017580";

		// 用户输入ID串;
		obj.inputPids = "\"10016,20000\"";

		// 用户输入名-值对
		composeBaobeiInputValues(item, obj);

		// 宝贝描述
		composeBaobeiMiaoshu(item, obj);

		// 宝贝属性
		composeBaobeiCateProps(item, obj);
		// 销售属性组合
		composeBaobeiSkuProps(item, obj);
		// 商家编码
		obj.outer_id = "EIZO_" + item.categoryName + "-" + item.kataban;
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
		// MontBellUtil.composeBaobeiSubtitle(item, obj);
		// 库存计数
		obj.sub_stock_type = "1";

		// 闪电发货
		obj.is_lighting_consigment = "16";
		// 新品
		obj.is_xinpin = "244";

		// 商品资质
		obj.qualification = "%7B%20%20%7D";
		// 增加商品资质
		obj.add_qualification = "0";

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private void composeBaobeiTitle(EizoProductObject item, BaobeiPublishObject baobei) {
		String title = "日本直邮代购 EIZO/艺卓";
		
		title += " " + getCategoryName(item.categoryName);
		title += " " + item.productName;
		title += " 包邮";
	
		baobei.title = "\"" +title + "\"";
	}

	protected void composeBaobeiCateProps(EizoProductObject item, BaobeiPublishObject baobei) {
		// cateProps
		String cateProps = "";
		cateProps += "20000:42797655;";
		cateProps += "20879:21456;21299:27023;21433:79940;";
		cateProps += "28099:220706768;29029:78185;29652:78181;29656:80020;";
		cateProps += "122216427:41892234;122276283:3250781";
		baobei.cateProps = "\"" +cateProps + "\"";
	}

	protected void composeBaobeiSkuProps(EizoProductObject item, BaobeiPublishObject obj) {
		// skuProps
		String skuProps = "";

		obj.skuProps = "\"" +skuProps + "\"";
	}

	private void composeBaobeiInputValues(EizoProductObject item, BaobeiPublishObject obj) {
		String inputValues = "";
		inputValues += item.kataban;
		inputValues += ",EIZO/艺卓;型号*;";
		inputValues += item.kataban;

		obj.inputValues = "\"" + inputValues + "\"";
	}

	protected void composeBaobeiPropAlias(EizoProductObject item, BaobeiPublishObject obj) {
		// propAlias
		String propAlias = "";
		// 销售属性别名

		obj.propAlias = "\"" +propAlias + "\"";
	}

	protected void composeBaobeiInputCustomCpv(EizoProductObject item, BaobeiPublishObject obj) {
		String inputCustomCpv = "";
		// 自定义属性值
		obj.input_custom_cpv = "\"" +inputCustomCpv + "\"";
	}

	protected void composeBaobeiMiaoshu(EizoProductObject item, BaobeiPublishObject obj) {
		StringBuffer detailSB = new StringBuffer();
		
		// 包邮
		detailSB.append(EizoUtil.composeBaoyouMiaoshu());

		// 宝贝描述
		detailSB.append(EizoUtil.composeProductInfoMiaoshu(item));

		String extraMiaoshu1 = BaobeiUtil.getExtraMiaoshu();
		obj.description = "\"" + detailSB.toString() + extraMiaoshu1 + "\"";
	}
	
	@Override
	public BaseBaobeiParser getParser() {
		return new EizoProductParser();
	}

	protected void composeBaobeiPictureStatus(EizoProductObject item, BaobeiPublishObject publishedBaobei) {
		String picStatus = TaobaoUtil.composeBaobeiPictureStatus(item.taobaoMainPicNameList, null, null);

		publishedBaobei.picture_status = "\"" + picStatus + "\"";
	}

	protected void composeBaobeiPicture(EizoProductObject item, BaobeiPublishObject publishedBaobei) {
		String picture = TaobaoUtil.composeBaobeiPicture(item.taobaoMainPicNameList, null, null);

		publishedBaobei.picture = "\"" + picture + "\"";
	}

	private void downloadAndCopyPicture(List<EizoProductObject> itemIdList, String taobaoPicFolder) throws IOException
			 {

		String picRoot = EizoUtil.getPictureRootFolder();
		for (EizoProductObject obj : itemIdList) {
			int i = 0;
			for (String picUrl : obj.productGalaryPicUrlList) {
				String picName = obj.kataban + "_" + i;
				try {
					File saveTo = TaobaoUtil.downloadPicture(picRoot, picUrl, picName);
					obj.taobaoMainPicNameList.add(picName);
					obj.productGalaryPicFileList.add(saveTo.getCanonicalPath());
					i++;
				} catch (Exception e) {
					System.out.println("[ERROR][DOWNLOAD]" + picUrl);
				}
			}
			TaobaoUtil.copyFiles(obj.taobaoMainPicNameList, EizoUtil.getPictureRootFolder(), taobaoPicFolder);
		}
	}

	private List<String> readProductUrls() throws IOException {
		String scanFile = NieConfig.getConfig("eizo.scan.url.file");

		List<String> adrs = Files.readLines(new File(scanFile), Charset.forName("UTF-8"));
		List<String> outputList = Lists.newArrayList();
		for (String line : adrs) {
			if ("".equals(line)) {
				continue;
			}
			if (line.startsWith("#")) {
				continue;
			}
			outputList.add(line);
		}
		return outputList;
	}

	private String getCategoryName(String nm) {
		if("lcd".equals(nm)){
			return "FlexScan";
		}
		if("ce".equals(nm)){
			return "ColorEdge";
		}
		return "Not Support";
	}
// cateProps
//	20000:42797655;20879:21456;21299:27023;21433:79940;28099:220706768;29029:78185;29652:78181;29656:80020;122216427:41892234;122276283:3250781
// inputPids
//	10016,20000
	// inputValues
//	CG319X,EIZO/艺卓;型号*;CG319X


}