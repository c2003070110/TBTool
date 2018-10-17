package com.walk_nie.taobao.mizuno;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.mizuno.shoes.MinunoShoesParser;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MizunoSingleBaobeiProducer extends BaseBaobeiProducer {
	
	public static void main(String[] args) {
		String url = "https://www.mizunoshop.net/f/dsg-660939";
		String outputFile = "out/mizuno_single_baobei_%s.csv";
		
		MizunoSingleBaobeiProducer db = new MizunoSingleBaobeiProducer();
		db.setOutputFile(outputFile).setProductUrl(url).process();
	}

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
        taobaoSizes.add("28313");taobaoSizes.add("28314");taobaoSizes.add("28315");
        taobaoSizes.add("28316");taobaoSizes.add("28317");taobaoSizes.add("28318");
    }
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			
			GoodsObject productObj = ((MinunoShoesParser) getParser())
					.parseProductByProductUrl(productUrl);
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));
			
			downloadPicture(productObj, MizunoUtil.getPictureSavePath(productObj));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
			
			TaobaoUtil.copyFiles(productObj.colorPicLocalNameList,
					MizunoUtil.getPictureSavePath(productObj),
					taobaoPicFolder);
			TaobaoUtil.copyFiles(productObj.dressOnPicLocalNameList,
					MizunoUtil.getPictureSavePath(productObj),
					taobaoPicFolder);
		
			writeOut(priceBw, productObj);
		
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
				String picName = "mizuno_" + goods.kataban + "-" + goods.colorNameList.get(i) + "_" + i;
				TaobaoUtil.downloadPicture(outFilePath, picUrl, picName);
				goods.colorPicLocalNameList.add(picName);
				i++;
			} catch (Exception ex) { 
			}
		}
		for(String picUrl : goods.dressOnPicsUrlList){
			try {
				String picName = "mizuno_" + goods.kataban + "_" + i;
				TaobaoUtil.downloadPicture(outFilePath, picUrl, picName);
				goods.dressOnPicLocalNameList.add(picName);
				i++;
			} catch (Exception ex) { 
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
		str += TaobaoUtil.composeBaobeiInputCustomCpv(item.colorNameList, taobaoColors);
		obj.input_custom_cpv = "\"" + str + "\"";
	}

	private void composeBaobeiPicture(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		List<String> baobeiPictureNameList = Lists.newArrayList();
		baobeiPictureNameList.addAll(item.colorPicLocalNameList);
		baobeiPictureNameList.addAll(item.dressOnPicLocalNameList);
		str += TaobaoUtil.composeBaobeiPicture(item.colorNameList, baobeiPictureNameList, taobaoColors);
		obj.picture = "\"" + str + "\"";
	}

	private void composeBaobeiPictureStatus(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		List<String> baobeiPictureNameList = Lists.newArrayList();
		baobeiPictureNameList.addAll(item.colorPicLocalNameList);
		baobeiPictureNameList.addAll(item.dressOnPicLocalNameList);
		str += TaobaoUtil.composeBaobeiPictureStatus(item.colorNameList, baobeiPictureNameList, taobaoColors);
		obj.picture_status = "\"" + str + "\"";
	}

	private void composeBaobeiPropAlias(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiPropAlias(item.sizeNameList, taobaoSizes, "20509");
		obj.propAlias = "\"" + str + "\"";
	}

	private void composeBaobeiOuter_id(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += "MIZUNO_" + item.kataban;
		obj.outer_id = "\"" + str + "\"";
	}

	private void composeBaobeiSkuProps(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		str += TaobaoUtil.composeBaobeiSkuProps(item.colorNameList, item.sizeNameList, taobaoColors, taobaoSizes, obj.price);
		obj.skuProps = "\"" + str + "\"";
	}

	private void composeBaobeiInputValues(GoodsObject item, BaobeiPublishObject obj) {
		String cid = "";
		cid += TaobaoUtil.composeBaobeiInputValues(item.colorNameList, taobaoColors);
		obj.inputValues = "\"" + cid + "\"";
	}

	private void composeBaobeiInputPids(GoodsObject item, BaobeiPublishObject obj) {
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

	private void composeBaobeiSellerCids(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		obj.seller_cids = "\"" + str + "\"";
	}

	private void composeBaobeiCId(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		
		obj.cid = "\"" + str + "\"";
	}

	private void composeBaobeiPrice(GoodsObject item, BaobeiPublishObject obj) {
		String str = "";
		
		obj.price = "\"" + str + "\"";
	}
	private void composeBaobeiSubtitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleJP + "!" + item.kataban;
		obj.subtitle = "\"" + title + "\"";
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "日本直邮 Mizuno/";
		title += "/包邮";
		// if (title.length() + suffix.length() < 60) {
		// title += suffix;
		// }
		obj.title = "\"" + title + "\"";
	}
	
	protected void composeBaobeiMiaoshu(GoodsObject item, BaobeiPublishObject obj) throws IOException {
		StringBuffer detailSB = new StringBuffer();

        // 包邮 TAX
		// 宝贝描述
		detailSB.append("");
		// 尺寸描述
		detailSB.append("");
		// chanpin teshe!
		detailSB.append(getSeriesSpec(item));
		//　zhi you
		detailSB.append(BaobeiUtil.getExtraMiaoshu());
		obj.description =  "\"" + detailSB.toString() + "\"";
	}

	private Object getSeriesSpec(GoodsObject item) {
		// TODO 自動生成されたメソッド・スタブ
        StringBuffer miaoshu = new StringBuffer();
        miaoshu.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">购物须知</h3>");
        miaoshu.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        miaoshu.append("<ol>");
        miaoshu.append("<li style=\"padding:10.0px;\"><span style=\";color:red;font-weight:bold\">产地：</span>日本产的非常少。大多数是东南亚和中国制造。<p>大家都知道的。<span style=\";color:red;font-weight:bold\">就算是同条生产线，面向日本本土，要比其他国家的质量要好很多。</span></p></li>");
        miaoshu.append("</ol>");
        miaoshu.append("</div>");
        return miaoshu.toString();
	}

	public MizunoSingleBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}
	private String productUrl = "";
	public MizunoSingleBaobeiProducer setProductUrl(String productUrl) {
		this.productUrl = productUrl;
		return this;
	}
	@Override
	public BaseBaobeiParser getParser() {
		return new MinunoShoesParser();
	}  

}
