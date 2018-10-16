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
import com.walk_nie.taobao.mizuno.shoes.MiznunoShoesParser;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiParser;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MizunoSingleBaobeiProducer extends BaseBaobeiProducer {
	
	public void mian(String[] arg){
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
			
			GoodsObject productObj = ((MiznunoShoesParser) getParser())
					.parseProductByProductUrl(productUrl);
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			String picFolder = TaobaoUtil.getPictureFolder(csvFile);
		
			downloadPicture(productObj, picFolder);
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
	protected void downloadPicture(GoodsObject goods,String outFilePathPrice) {
		int i= 0;
		for(String picUrl : goods.colorPicUrlList){
			try {
				String picName = "yonex_" + goods.kataban + "-" + goods.colorNameList.get(i) + "_" + i;
				TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
				goods.colorPicLocalNameList.add(picName);
				i++;
			} catch (Exception ex) { 
			}
		}
		for(String picUrl : goods.dressOnPicsUrlList){
			try {
				String picName = "yonex_" + goods.kataban + "_" + i;
				TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
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
		findPrice(item,obj);
		// 宝贝数量
		obj.num = "99";
        // 邮费模版ID
        obj.postage_id = "// TODO";
        // 用户输入ID串;
		//obj.inputPids = "\"13021751,6103476,1627207\"";
		// 用户输入名-值对
		obj.inputValues = "\"" + TaobaoUtil.composeBaobeiInputValues(item.colorNameList, taobaoColors) + "\"";
		// 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
        // 宝贝属性
        obj.cateProps ="\"" + TaobaoUtil.composeBaobeiCateProps(item.colorNameList, item.sizeNameList,taobaoColors,taobaoSizes) + "\"";
		// 销售属性组合
		obj.skuProps = "\""
				+ TaobaoUtil.composeBaobeiSkuProps(item.colorNameList, item.sizeNameList, taobaoColors, taobaoSizes, obj.price)
				+ "\"";
		// 商家编码
		obj.outer_id = "YONEX_" + item.kataban;
		// 销售属性别名
		obj.propAlias = "\"" + TaobaoUtil.composeBaobeiPropAlias(item.sizeNameList, taobaoSizes,"") + "\"";
		// 图片状态
		obj.picture_status = "\"" + TaobaoUtil.composeBaobeiPictureStatus(item.colorNameList, item.colorPicLocalNameList, taobaoColors) + "\"";
		// 新图片
		obj.picture = "\"" + TaobaoUtil.composeBaobeiPictureStatus(item.colorNameList, item.colorPicLocalNameList, taobaoColors) + "\"";
        // 自定义属性值
		obj.input_custom_cpv = "\"" + TaobaoUtil.composeBaobeiInputCustomCpv(item.colorNameList, taobaoColors) + "\"";
        // 宝贝卖点
        composeBaobeiSubtitle(item, obj);
		
		return TaobaoUtil.composeTaobaoLine(obj);
	}
	private void composeBaobeiSellerCids(GoodsObject item, BaobeiPublishObject obj) {
		String cid = "";
		
		obj.seller_cids = cid;
	}
	private void composeBaobeiCId(GoodsObject item, BaobeiPublishObject obj) {
		String cid = "";
		
		obj.cid = cid;
	}
	private void findPrice(GoodsObject item, BaobeiPublishObject obj) {
		String price = "";
		
		obj.price = price;
	}
	private void composeBaobeiSubtitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.titleJP + "!" + item.kataban;
		obj.subtitle = "\"" + title + "\"";
	}

	private void composeBaobeiTitle(GoodsObject item, BaobeiPublishObject obj) {
		String title = "日本直邮 Yonex/尤尼克斯";
		// String suffix = "/包邮";
		// if (title.length() + suffix.length() < 60) {
		// title += suffix;
		// }
		obj.title = "\"" + title + "\"";
	}
	
	protected String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
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
		return "\"" + detailSB.toString() + "\"";
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
		return new MiznunoShoesParser();
	}  

}
