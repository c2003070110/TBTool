package com.walk_nie.taobao.montBell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;

public class MontbellShoesBaobeiProducer extends MontbellBaseBaobeiProducer {

	protected String composeBaobeiSubtitle(GoodsObject item) {
		return "\"日本直邮！100%正品！真正的日本代购！包邮！" + item.goodTitleOrg + "\"";
	}
	
	@Override
	protected String composeBaobeiTitle(GoodsObject item) {
		String title = "\"日本直邮";
		if(item.gender != null && !item.gender.equals("")){
			title += " " + item.gender;
		}
		title += " mont-bell #" + item.productId;
		title += " " + item.goodTitleCN;
//		String suffix = "/包邮";
//		if (title.length() + suffix.length() < 60) {
//			title += suffix;
//		}
		return title + "\"";
	}

	@Override
	protected String[] composeBaobeiPropColor(GoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		List<String> taobaoColors = Lists.newArrayList();
		taobaoColors.add("28320");taobaoColors.add("28340");taobaoColors.add("3232479");
		taobaoColors.add("3232478");taobaoColors.add("3232482");taobaoColors.add("60092");
		taobaoColors.add("30156");taobaoColors.add("28332");taobaoColors.add("90554");
		taobaoColors.add("3232481");taobaoColors.add("3232484");taobaoColors.add("3232483");
		// 颜色值:28320 28324 28326 28327 28329 28332 28340 28338 28335
		// 宝贝属性 -销售属性组合- 销售属性别名
		String cateProps = "";String skuProps = "";String propAlias = "";
		String picStatus = "";String skuPropPic = "";
		for(int i=0;i<item.pictureNameList.size();i++){
			if(i==5) break;
			skuPropPic += item.pictureNameList.get(i) + ":1:" + i +":|;";
			picStatus +="2;";
		}
		int i = 0;
		for(String color :item.colorList){
			if(i>=taobaoColors.size())break;
			// 宝贝属性格式  1627207:28320;
			cateProps +="1627207:"+taobaoColors.get(i)+";";
			// 销售属性组合格式 价格:数量:SKU:1627207:28320;
			skuProps += item.priceCNY +":9999"+":"+":1627207"+":"+taobaoColors.get(i)+";20549:44911;";
			// 销售属性别名格式 1627207:28320:颜色1;
			//propAlias +="1627207:"+taobaoColors.get(i)+":" +Util.convertColor(color)+";";
			propAlias += "1627207:" + taobaoColors.get(i) + ":" + color + ";";
			if(item.pictureNameList.size() == item.colorList.size()){
				skuPropPic += item.pictureNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
				picStatus +="2;";
			}
			i++;
		}
		
		propAlias += "20549:44911:请留言;";
		return new String[]{
				"\""+cateProps+"\"","\""+skuProps+"\"" ,"\""+propAlias+"\"" 
				,"\""+picStatus+"\"" ,"\""+skuPropPic+"\"" };
	}

	@Override
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
		String productInfo ="";
		if(productInfo == null){
			productInfo = "";
		}
		String desp = sb.toString().replace("$detail_disp$", productInfo);
		desp = desp.replaceAll("\"", "\"\"");
		return "\"" + desp + "\"";
	}
	@Override
	protected String[] composeBaobeiPropPicture(GoodsObject item,
			BaobeiPublishObject baobeiTemplate) {
		// TODO Auto-generated method stub
		return null;
	}
}
