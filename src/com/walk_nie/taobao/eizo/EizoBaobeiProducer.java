package com.walk_nie.taobao.eizo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
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

			List<String> productUrls = NieConfig.getConfigByPrefix("eizo.parse.product.url.");
			if (!productUrls.isEmpty()) {
				EizoProductParser parser = new EizoProductParser();
				parser.setPublishedbaobeiList(this.publishedbaobeiList);
				itemIdList = parser.scanItemByProductUrlList(productUrls);
			}
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile,
					DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-16"));

			String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
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
		obj.cid = "50014798";
		// 店铺类目
		obj.seller_cids = "1372086392";
		// 省
		obj.location_state = "\"日本\"";
		// 宝贝价格
		obj.price = EizoUtil.convertToCNYWithEmsFee(item);
		// obj.price = item.priceCNY;
		// 宝贝数量
		obj.num = "99";

		// 邮费模版ID
		// obj.postage_id = "1780373930";

		// 用户输入ID串;
		obj.inputPids = "\"13021751,6103476,1627207\"";

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
		obj.is_lighting_consigment = "80";
		// 新品
		obj.is_xinpin = "247";

		// 商品资质
		obj.qualification = "%7B%7D";
		// 增加商品资质
		obj.add_qualification = "0";

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	private void composeBaobeiTitle(EizoProductObject item, BaobeiPublishObject baobei) {
		String title = "日本直邮 Eizo";

		title += " " + item.productName;
	
		baobei.title = "\"" +title + "\"";
	}

	protected void composeBaobeiCateProps(EizoProductObject item, BaobeiPublishObject baobei) {
		// cateProps
		// 宝贝属性：1627207:-1001;1627207:-1002;1627207:-1003;1627207:-1004;1627207:-1005;1627207:-1006;1627207:-1007;1627207:-1008;1627207:-1009;20509:28381;20509:28313;20509:28314;20509:28315;20509:28316;20509:28317;20509:28319
		String cateProps = "";

		baobei.cateProps = "\"" +cateProps + "\"";
	}

	protected void composeBaobeiSkuProps(EizoProductObject item, BaobeiPublishObject obj) {
		// skuProps
		// 销售属性组合：0:0::1627207:-1001;20509:28381;0:0::1627207:-1001;20509:28313;0:0::1627207:-1001;20509:28314;0:0::1627207:-1001;20509:28315;0:0::1627207:-1001;20509:28316;0:0::1627207:-1001;20509:28317;0:0::1627207:-1001;20509:28319;0:0::1627207:-1002;20509:28381;0:0::1627207:-1002;20509:28313;0:0::1627207:-1002;20509:28314;0:0::1627207:-1002;20509:28315;0:0::1627207:-1002;20509:28316;0:0::1627207:-1002;20509:28317;0:0::1627207:-1002;20509:28319;0:0::1627207:-1003;20509:28381;0:0::1627207:-1003;20509:28313;0:0::1627207:-1003;20509:28314;0:0::1627207:-1003;20509:28315;0:0::1627207:-1003;20509:28316;0:0::1627207:-1003;20509:28317;0:0::1627207:-1003;20509:28319;0:0::1627207:-1004;20509:28381;0:0::1627207:-1004;20509:28313;0:0::1627207:-1004;20509:28314;0:0::1627207:-1004;20509:28315;0:0::1627207:-1004;20509:28316;0:0::1627207:-1004;20509:28317;0:0::1627207:-1004;20509:28319;0:0::1627207:-1005;20509:28381;0:0::1627207:-1005;20509:28313;0:0::1627207:-1005;20509:28314;0:0::1627207:-1005;20509:28315;0:0::1627207:-1005;20509:28316;0:0::1627207:-1005;20509:28317;0:0::1627207:-1005;20509:28319;0:0::1627207:-1006;20509:28381;0:0::1627207:-1006;20509:28313;0:0::1627207:-1006;20509:28314;0:0::1627207:-1006;20509:28315;0:0::1627207:-1006;20509:28316;0:0::1627207:-1006;20509:28317;0:0::1627207:-1006;20509:28319;0:0::1627207:-1007;20509:28381;0:0::1627207:-1007;20509:28313;0:0::1627207:-1007;20509:28314;0:0::1627207:-1007;20509:28315;0:0::1627207:-1007;20509:28316;0:0::1627207:-1007;20509:28317;0:0::1627207:-1007;20509:28319;0:0::1627207:-1008;20509:28381;0:0::1627207:-1008;20509:28313;0:0::1627207:-1008;20509:28314;0:0::1627207:-1008;20509:28315;0:0::1627207:-1008;20509:28316;0:0::1627207:-1008;20509:28317;0:0::1627207:-1008;20509:28319;0:0::1627207:-1009;20509:28381;0:0::1627207:-1009;20509:28313;0:0::1627207:-1009;20509:28314;0:0::1627207:-1009;20509:28315;0:0::1627207:-1009;20509:28316;0:0::1627207:-1009;20509:28317;0:0::1627207:-1009;20509:28319;
		String skuProps = "";

		obj.skuProps = "\"" +skuProps + "\"";
	}

	private void composeBaobeiInputValues(EizoProductObject item, BaobeiPublishObject obj) {
		String inputValues = "";

		obj.inputValues = "\"" + inputValues + "\"";
	}

	protected void composeBaobeiPropAlias(EizoProductObject item, BaobeiPublishObject obj) {
		// propAlias
		// 销售属性别名：20509:28381:size1;20509:28313:size2;20509:28314:size3;20509:28315:size4;20509:28316:size5;20509:28317:size6;20509:28319:size7
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

	private void downloadAndCopyPicture(List<EizoProductObject> itemIdList, String taobaoPicFolder)
			throws IOException {

		String picRoot = EizoUtil.getPictureRootFolder();
		for (EizoProductObject obj : itemIdList) {
			int i = 0;
			for (String picUrl : obj.productGalaryPicUrlList) {
				String picName = obj.kataban + "_" + i;
				File saveTo;
				saveTo = TaobaoUtil.downloadPicture(picRoot, picUrl, picName);
				obj.taobaoMainPicNameList.add(picName);
				obj.productGalaryPicFileList.add(saveTo.getCanonicalPath());
				i++;
			}
			TaobaoUtil.copyFiles(obj.taobaoMainPicNameList, EizoUtil.getPictureRootFolder(), taobaoPicFolder);
		}
	}

}