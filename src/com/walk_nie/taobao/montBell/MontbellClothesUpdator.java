package com.walk_nie.taobao.montBell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;
import org.eclipse.jetty.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellClothesUpdator {
	// String taobeiTemplateFile = "in/montBell_baobeiTemplate.csv";
	String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
	String outputFile = "out/montBell_baobei_%s.csv";
	String publishedBaobeiFile = "c:/temp/montbell-d-a.csv";
	List<GoodsObject> reScanGoods = Lists.newArrayList();

	File csvFile = new File(String.format(outputFile, DateUtils
			.formatDate(Calendar.getInstance().getTime(),
					"yyyy_MM_dd_HH_mm_ss")));

	public static void main(String[] args) throws Exception {
		new MontbellClothesUpdator().process();
	}

	public void process() throws Exception {

		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil
				.readInPublishedBaobei(file);
		List<String> categoryIds = Lists.newArrayList();
		for (BaobeiPublishObject baobei : baobeiList) {
			String outer_id = baobei.outer_id.replace("\"", "");
			if (!outer_id.startsWith("MTBL_")) {
				System.out
						.println("[ERROR]FORMAT ERROR!outer_id = " + outer_id);
				continue;
			}
			String[] sp = outer_id.substring("MTBL_".length()).split("-");
			if (!categoryIds.contains(sp[0]))
				categoryIds.add(sp[0]);
		}

		reScanGoods = new MontbellProductParser().scanItem(categoryIds);

		List<BaobeiPublishObject> newBaobeiList = new ArrayList<BaobeiPublishObject>();
		for (BaobeiPublishObject baobei : baobeiList) {
			BaobeiPublishObject updateBaobei = updateBaobei(baobei);
			if (updateBaobei != null) {
				newBaobeiList.add(updateBaobei);
			}
		}

		BufferedWriter priceBw = null;
		try {
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			for (BaobeiPublishObject obj : newBaobeiList) {
				priceBw.write(TaobaoUtil.composeTaobaoLine(obj));
				priceBw.flush();
			}
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
		System.out.println("-------- FINISH--------");
	}

	protected BaobeiPublishObject updateBaobei(BaobeiPublishObject baobei) throws IOException {
		String outer_id = baobei.outer_id.replace("\"", "");
		String productId = "";
		if(!outer_id.startsWith("MTBL_")){
			System.out.println("[ERROR]FORMAT ERROR!outer_id = " + outer_id);
			return null;
		}
		String[] sp = outer_id.substring("MTBL_".length()).split("-");
		productId = sp[1];
		
		GoodsObject goodsObj = null;
		for(GoodsObject obj :reScanGoods){
			if(obj.productId.equals(productId)){
				goodsObj = obj;
				break;
			}
		}
		if(goodsObj == null || StringUtil.isBlank(goodsObj.priceOrg)){
			System.out.println("[ERROR]NOT EXISTED!product_id = " + outer_id);
			return null;
		}
        MontBellUtil.downloadPicture(goodsObj, MontBellUtil.rootPathName);
        String taobaoPicFolder = TaobaoUtil.getPictureFolder(csvFile);
        TaobaoUtil.copyFiles(goodsObj.pictureNameList,MontBellUtil.rootPathName, taobaoPicFolder);
		
		// FIXME change 
		double currencyRate = 0.0683;
		double benefitRate = 0.07;
		goodsObj.priceCNY =  MontBellUtil.convertToCNY(goodsObj,currencyRate,benefitRate);
		baobei.price = goodsObj.priceCNY;
		
		// 用户输入名-值对
		//baobei.inputValues="\"montbell,*,"+goodsObj.productId + "\"";
		// 宝贝描述
		//baobei.description = composeBaobeiMiaoshu(goodsObj);
		// Array[0]:宝贝属性;1:销售属性组合;2:销售属性别名;
		String[] props = composeBaobeiPropColor(goodsObj);
		baobei.cateProps = props[0];
		baobei.skuProps = props[1];
		//baobei.propAlias = props[2];
		// 图片状态
		MontBellUtil.composeBaobeiPictureStatus(goodsObj,baobei,taobaoColors);
		// 新图片
		MontBellUtil.composeBaobeiPicture(goodsObj,baobei,taobaoColors);
		// 用户输入名-值对
		baobei.inputValues = composeBaobeiInputValues(goodsObj);
		// 自定义属性值
		baobei.input_custom_cpv = composeBaobeiInputCustomCpv(goodsObj);
		return baobei;
	}

	private String composeBaobeiInputCustomCpv(GoodsObject item) {
		String rslt = "";
		for (int i = 0; i < item.colorList.size(); i++) {
			if (i >= taobaoColors.size())
				break;
			rslt += "1627207:" + taobaoColors.get(i) + ":"
					+ item.colorList.get(i) + ";";
		}
		return "\"" + rslt + "\"";
	}

	private String composeBaobeiInputValues(GoodsObject item) {

		String rslt = "\"montbell,"+item.productId + ",1234";
		if(item.colorList.size() > 0){
			rslt = rslt + ",";
		}
		for(int i=0 ;i<item.colorList.size();i++){
			rslt = rslt + item.colorList.get(i) +  ";颜色分类;";
		}
		return rslt +"\"";
	}

	private String[] composeBaobeiPropColor(GoodsObject item) {
		
		// 宝贝属性 -销售属性组合- 销售属性别名
		String cateProps = "20000:6217823;21548:42581;122216608:29923;13021751:25448447;6103476:3231061;";
		String skuProps = "";String propAlias = "";
		int maxColorLen =  Math.min(item.colorList.size(), taobaoColors.size());
		int maxSizeLen =  Math.min(item.sizeList.size(), clothesSizes.size());
		for(int i=0 ;i<maxColorLen;i++){
			// 宝贝属性格式  1627207=color; 20509=衣服大小; 20549=鞋码
			cateProps +="1627207:"+taobaoColors.get(i)+";";
			//propAlias += "1627207:" + taobaoColors.get(i) + ":" + item.colorList.get(i) + ";";
		}
		for(int i=0 ;i<maxSizeLen;i++){
			// 宝贝属性格式  1627207=color; 20509=衣服大小; 20549=鞋码
			cateProps +="20509:"+clothesSizes.get(i)+";";
			//propAlias += "20509:" + clothesSizes.get(i) + ":" + item.sizeList.get(i) + ";";
		} 
		
		for (int i = 0; i < maxSizeLen; i++) {
			for (int j = 0; j < maxColorLen; j++) {
				if(i>=taobaoColors.size())break;
				// 销售属性组合格式 价格:数量:SKU:1627207:28320;
				skuProps += item.priceCNY +":9999"+":"+":1627207"+":"+taobaoColors.get(j)+";20509:"+clothesSizes.get(i)+";";
			}
		}
		
		return new String[]{
				"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\""  };
	}

	protected String composeBaobeiMiaoshu(GoodsObject item) throws IOException {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		try {
			File file = new File(miaoshuTemplateFile);
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
		String productInfo = item.detailScreenShotPicFile;
		if (productInfo == null) {
			productInfo = "";
		}
		String desp = sb.toString().replace("$detail_disp$", productInfo);
		desp = desp.replaceAll("\"", "\"\"");
		return "\"" + desp + "\"";
	}

	List<String> taobaoColors = Lists.newArrayList();{
	// 1627207
	taobaoColors.add("-1001");taobaoColors.add("-1002");taobaoColors.add("-1003");
	taobaoColors.add("-1004");taobaoColors.add("-1005");taobaoColors.add("-1006");
	//taobaoColors.add("30156");taobaoColors.add("28332");taobaoColors.add("90554");
	//taobaoColors.add("3232481");taobaoColors.add("3232484");taobaoColors.add("3232483");
	}
	List<String> clothesSizes = Lists.newArrayList();{
	// 20509
	clothesSizes.add("28313");clothesSizes.add("28314");clothesSizes.add("28315");
	clothesSizes.add("28316");clothesSizes.add("28317");clothesSizes.add("28318");
	}
}
