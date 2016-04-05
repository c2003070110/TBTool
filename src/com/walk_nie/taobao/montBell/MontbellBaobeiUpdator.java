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
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellBaobeiUpdator {
	// String taobeiTemplateFile = "in/montBell_baobeiTemplate.csv";
	String miaoshuTemplateFile = "in/montBell_miaoshu_template.html";
	String outputFile = "out/montBell_baobei_%s.csv";
	String publishedBaobeiFile = "E:/temp/montbell-羽绒衣-all.csv";

	File csvFile = new File(String.format(outputFile, DateUtils
			.formatDate(Calendar.getInstance().getTime(),
					"yyyy_MM_dd_HH_mm_ss")));

	public static void main(String[] args) throws Exception {
		new MontbellBaobeiUpdator().process();
	}

	public void process() throws Exception {
		List<BaobeiPublishObject> baobeiList = new ArrayList<BaobeiPublishObject>();

		BufferedReader br = null;
		try {
			File file = new File(publishedBaobeiFile);
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					file), "UTF-16"));
			String str = null;
			int i = 0;
			while ((str = br.readLine()) != null) {
				i++;
				if(i<4){
					continue;
				}
				BaobeiPublishObject baobei = TaobaoUtil.readBaobeiIn(str);
				BaobeiPublishObject updateBaobei = updateBaobei(baobei);
				if (updateBaobei != null) {
					baobeiList.add(updateBaobei);
				}
			}
		} finally {
			if (br != null)
				br.close();
		}

		BufferedWriter priceBw = null;
		try {
			priceBw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(csvFile), "UTF-16"));

			priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());
			for (BaobeiPublishObject obj : baobeiList) {
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
		String productId = baobei.outer_id.replace("\"", "");
		GoodsObject goodsObj = new GoodsObject();
		goodsObj.productId = productId;
		new MontbellProductParser().scanSingleItem(goodsObj);
		if(StringUtil.isBlank(goodsObj.priceOrg)){
			return null;
		}

		MontBellUtil.downloadPicture(goodsObj, csvFile.getName().replace(".csv", ""));
		
		// 用户输入名-值对
		baobei.inputValues="\"montbell,*,"+goodsObj.productId + "\"";
		// 宝贝描述
		baobei.description = composeBaobeiMiaoshu(goodsObj);
		// Array[0]:宝贝属性;1:销售属性组合;2:销售属性别名;
		String[] props = composeBaobeiPropColor(goodsObj);
		baobei.cateProps = props[0];
		baobei.skuProps = props[1];
		baobei.propAlias = props[2];

		String[] picProp = composeBaobeiPropPicture(goodsObj);
		// 图片状态
		baobei.picture_status = picProp[0];
		// 新图片
		baobei.picture = picProp[1];
		return baobei;
	}
	private String[] composeBaobeiPropPicture(GoodsObject item) {
		String picSts = "";
		String pics = "";
		for(int i=0;i<item.pictureNameList.size();i++){
			if(i==5) break;
			pics += item.pictureNameList.get(i) + ":1:" + i +":|;";
			picSts +="2;";
		}
		return new String[] { "\"" + picSts + "\"", "\"" + pics + "\"" };
	}

	private String[] composeBaobeiPropColor(GoodsObject item) {
		List<String> taobaoColors = Lists.newArrayList();
		taobaoColors.add("28320");taobaoColors.add("28340");taobaoColors.add("3232479");
		taobaoColors.add("3232478");taobaoColors.add("3232482");taobaoColors.add("60092");
		taobaoColors.add("30156");taobaoColors.add("28332");taobaoColors.add("90554");
		taobaoColors.add("3232481");taobaoColors.add("3232484");taobaoColors.add("3232483");
		
		List<String> clothesSizes = Lists.newArrayList();
		clothesSizes.add("28317");clothesSizes.add("28316");clothesSizes.add("28315");
		clothesSizes.add("28314");clothesSizes.add("28313");clothesSizes.add("28381");
		
		// 宝贝属性 -销售属性组合- 销售属性别名
		String cateProps = "21548:38488;122216608:29923;";String skuProps = "";String propAlias = "";
		for(int i=0 ;i<item.colorList.size();i++){
			if(i>=clothesSizes.size())break;
			// 宝贝属性格式  1627207=color; 20509=衣服大小; 20549=鞋码
			cateProps +="1627207:"+clothesSizes.get(i)+";";
			propAlias += "1627207:" + taobaoColors.get(i) + ":" + item.colorList.get(i) + ";";
		}
		for(int i=0 ;i<item.sizeList.size();i++){
			if(i>=taobaoColors.size())break;
			// 宝贝属性格式  1627207=color; 20509=衣服大小; 20549=鞋码
			cateProps +="20509:"+clothesSizes.get(i)+";";
			propAlias += "20509:" + clothesSizes.get(i) + ":" + item.sizeList.get(i) + ";";
		} 
		for(int i=0 ;i<item.colorList.size();i++){
			if(i>=clothesSizes.size())break;
			for(int j=0 ;j<item.colorList.size();j++){
				if(i>=taobaoColors.size())break;
				// 销售属性组合格式 价格:数量:SKU:1627207:28320;
				skuProps += item.priceCNY +":9999"+":"+":1627207"+":"+taobaoColors.get(j)+";20549:"+clothesSizes.get(i)+";";
				j++;
			}
			i++;
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
		String productInfo = item.detailDisp;
		if (productInfo == null) {
			productInfo = "";
		}
		String desp = sb.toString().replace("$detail_disp$", productInfo);
		desp = desp.replaceAll("\"", "\"\"");
		return "\"" + desp + "\"";
	}

}
