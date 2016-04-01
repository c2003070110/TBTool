package com.walk_nie.taobao.montBell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;

public abstract class MontbellBaseBaobeiProducer {
	protected String pictureUrlFmt = "http://webshop.montbell.jp/common/images/product/prod_k/m_k_%s_%s.jpg";
	protected String pictureUrlFmt1 = "http://webshop.montbell.jp/common/images/product/prod_k/k_%s_%s.jpg";
	
	private String taobeiTemplateFile = "";
	private String publishedBaobeiFile = "";
	private String miaoshuTemplateFile = "";
	private String outputFile = "";
	
	public void process() {
		BufferedWriter priceBw = null;
		try {
			System.out.println("-------- START --------");
			List<GoodsObject> itemIdList = new MontbellProductParser().scanItem();
			if (itemIdList.isEmpty())
				return;
			String outFilePathPrice = String.format(outputFile, DateUtils
					.formatDate(Calendar.getInstance().getTime(),
							"yyyy_MM_dd_HH_mm_ss"));
			File csvFile = new File(outFilePathPrice);
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

			for (GoodsObject obj : itemIdList) {
				downloadPicture(obj, csvFile.getName().replace(".csv", ""));
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
	
	protected void downloadPicture(GoodsObject goods,String outFilePathPrice) {
		
		for(String color : goods.colorList){
			String picUrl = String.format(pictureUrlFmt, goods.productId,color);
			String picName = goods.productId + "_" + color;
			try {
				TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
				goods.pictureNameList.add(picName);
			} catch (Exception ex) {
				picUrl = String.format(pictureUrlFmt1, goods.productId, color);
				try {
					TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
					goods.pictureNameList.add(picName);
				} catch (Exception ex1) {

				}
			}
		}
	}
	
	protected void writeOut(BufferedWriter priceBw, GoodsObject item)
			throws Exception {
		BaobeiPublishObject baobeiTemplate = new BaobeiPublishObject();
		
		BufferedReader br = null;
		try {
			File file = new File(taobeiTemplateFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			while ((str = br.readLine()) != null) {
				if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
					baobeiTemplate = TaobaoUtil.readBaobeiIn(str);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}
		
		priceBw.write(composeBaobeiLine(item, baobeiTemplate));
		priceBw.flush();
	}
	protected String composeBaobeiLine(GoodsObject item,BaobeiPublishObject baobeiTemplate) throws Exception {
		BaobeiPublishObject obj = TaobaoUtil.copyTaobaoTemplate(baobeiTemplate);

		// 宝贝名称
		obj.title = composeBaobeiTitle(item);
		// 宝贝价格
		obj.price = item.price;
		// 宝贝数量
		obj.num = "9999";
		// FIXME 宝贝描述
		obj.description = composeBaobeiMiaoshu(item);
		
		// Array[0]:宝贝属性;1:销售属性组合;2:销售属性别名;3:宝贝主图;4:销售属性图片
		String[] props = composeBaobeiPropColor(item,baobeiTemplate);
		// 宝贝属性
		String str = baobeiTemplate.cateProps + props[0];
		str = str.replaceAll("\"\"", "");
		str = props[0];
		obj.cateProps = str;
		// 销售属性组合
		if("\"\"".equals(baobeiTemplate.skuProps)){
			obj.skuProps = props[1];
		}else{
			obj.skuProps = baobeiTemplate.skuProps + props[1];
		}
		obj.skuProps = props[1];
		// 商家编码
		obj.outer_id = item.productId;
		// 销售属性别名
		if("\"\"".equals(baobeiTemplate.skuProps)){
			obj.propAlias = props[2];
		}else{
			obj.propAlias = baobeiTemplate.propAlias + props[2];
		}
		obj.propAlias = props[2];
		// 商品条形码
		//obj.barcode = item.sku;
		// 图片状态
		obj.picture_status = props[3];
		// 新图片
		obj.picture = props[4];
		// 宝贝卖点
		obj.subtitle = composeBaobeiSubtitle(item);

		return TaobaoUtil.composeTaobaoLine(obj);
	}

	protected abstract String composeBaobeiSubtitle(GoodsObject item);

	protected abstract String composeBaobeiTitle(GoodsObject item);

	protected abstract String[] composeBaobeiPropColor(GoodsObject item,
			BaobeiPublishObject baobeiTemplate);

	protected abstract String[] composeBaobeiPropPicture(GoodsObject item,
			BaobeiPublishObject baobeiTemplate);
	
	protected  String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			File file = new File(getMiaoshuTemplateFile());
			String str = null;
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-8"));
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
		} finally {
			if (br != null)
				br.close();
		}
		String productInfo = item.detailDisp;
		if(productInfo == null){
			productInfo = "";
		}
		String desp = sb.toString().replace("$detail_disp$", productInfo);
		desp = desp.replaceAll("\"", "\"\"");
		return "\"" + desp + "\"";
	}
	
	public MontbellBaseBaobeiProducer setTaobeiTemplateFile(String taobeiTemplateFile) {
		this.taobeiTemplateFile = taobeiTemplateFile;
		return this;
	}

	public MontbellBaseBaobeiProducer setPublishedBaobeiFile(String publishedBaobeiFile) {
		this.publishedBaobeiFile = publishedBaobeiFile;
		return this;
	}

	public MontbellBaseBaobeiProducer setMiaoshuTemplateFile(
			String miaoshuTemplateFile) {
		this.miaoshuTemplateFile = miaoshuTemplateFile;
		return this;
	}

	public String getMiaoshuTemplateFile() {
		return this.miaoshuTemplateFile;
	}

	public MontbellBaseBaobeiProducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

}
