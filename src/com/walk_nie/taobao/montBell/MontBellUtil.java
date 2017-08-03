package com.walk_nie.taobao.montBell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;



public class MontBellUtil {

    public static String rootPathName = "out/MontBell/";
    public static String sizeTipFileName = "sizeTipList.txt";
    
    public static String urlPrefix = "http://webshop.montbell.jp";
    
    public static String pictureUrlFmt = "http://webshop.montbell.jp/common/images/product/prod_k/m_k_%s_%s.jpg";
    public static String pictureUrlFmt1 = "http://webshop.montbell.jp/common/images/product/prod_k/k_%s_%s.jpg";

	public static String categoryUrlPrefix = "http://webshop.montbell.jp/goods/list.php?category=";

	public static String categoryUrlPrefix_en = "http://en.montbell.jp/products/goods/list.php?category=";

    public static String categoryUrlPrefix_fo = "http://webshop.montbell.jp/goods/list_fo.php?category=";

    public static String productUrlPrefix = "http://webshop.montbell.jp/goods/disp.php?product_id=";

    public static String productUrlPrefix_fo = "http://webshop.montbell.jp/goods/disp_fo.php?product_id=";

    public static String productSizeUrlPrefix = "http://webshop.montbell.jp/goods/size/?product_id=";
    
    public static void downloadPicture(GoodsObject goods,String outFilePath) {
        
        for(String color : goods.colorList){
            color = color.replaceAll("/", "-").toLowerCase();
            String picUrl = String.format(pictureUrlFmt, goods.productId,color);
            String picName = goods.productId + "_" + color;
            try {
                TaobaoUtil.downloadPicture(outFilePath, picUrl, picName);
                goods.pictureNameList.add(picName);
            } catch (Exception ex) {
                picUrl = String.format(pictureUrlFmt1, goods.productId, color);
                try {
                    TaobaoUtil.downloadPicture(outFilePath, picUrl, picName);
                    goods.pictureNameList.add(picName);
                } catch (Exception ex1) {

                }
            }
        }
    }
    public static String getExtraMiaoshu() {
        StringBuffer miaoshu = new StringBuffer();
        miaoshu.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">montbell直营店采购现场！直播 WX(tokyoson)</h3>");
        miaoshu.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        miaoshu.append("<p style=\"text-indent:2.0em;\">本宝贝价格，适用于中国大陆和澳门！台湾 香港 韩国的买家另外报价！</p>");
        miaoshu.append("</div>");
        miaoshu.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        miaoshu.append("<p style=\"text-indent:2.0em;\"><img style=\"border:#666666 2px solid;padding:2px;\" src=\"http://img.alicdn.com/imgextra/i2/2498620403/TB2.eGxkXXXXXXJXpXXXXXXXXXX_!!2498620403.jpg\" /></p>");
        miaoshu.append("<p style=\"text-indent:2.0em;\"><img style=\"border:#666666 2px solid;padding:2px;\" src=\"http://img.alicdn.com/imgextra/i3/2498620403/TB2SLKFkXXXXXc9XXXXXXXXXXXX_!!2498620403.jpg\" /></p>");
        miaoshu.append("<p style=\"text-indent:2.0em;\"><img style=\"border:#666666 2px solid;padding:2px;\" src=\"http://img.alicdn.com/imgextra/i3/2498620403/TB2WvGSkXXXXXbuXXXXXXXXXXXX_!!2498620403.jpg\" /></p>");
        miaoshu.append("<p style=\"text-indent:2.0em;\"><img style=\"border:#666666 2px solid;padding:2px;\" src=\"http://img.alicdn.com/imgextra/i3/2498620403/TB2AtmtkXXXXXX1XpXXXXXXXXXX_!!2498620403.jpg\" /></p>");
        miaoshu.append("</div>");
        return miaoshu.toString();
    }

    public static String convertToCNY(GoodsObject item,double curencyRate,double benefitRate) {
        String priceStr = item.priceJPY;
        try {
            int price = Integer.parseInt(priceStr);
			if (price < 10000)
				price = price + 1000;
            long priceTax  = Math.round(price*1.08);
            int emsFee = TaobaoUtil.getEmsFee(item.weight + item.weightExtra);
            double priceCNY = (priceTax + emsFee) * curencyRate;
            priceCNY = priceCNY + priceCNY * benefitRate;
            return String.valueOf(Math.round(priceCNY));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "XXXXXX";
        }
    }

    public static String convertToCNYNoneEMSFee(GoodsObject item,double curencyRate,double benefitRate) {
        String priceStr = item.priceJPY;
        try {
            int price = Integer.parseInt(priceStr);
            long priceTax  = Math.round(price*1.08);
            double priceCNY = (priceTax ) * curencyRate;
            priceCNY = priceCNY + priceCNY * benefitRate;
            return String.valueOf(Math.round(priceCNY));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "XXXXXX";
        }
    }
    public static boolean isCateogryClothes(String categoryId) {
        return isCateogryRainClothes(categoryId)
                || isCateogrySoftShell1(categoryId)
                || isCateogrySoftShell2(categoryId)
                || isCateogryWindBreaker(categoryId)
                || isCateogryHardShell1(categoryId)
                || isCateogryHardShell2(categoryId)
                || isCateogryFreece1(categoryId)
                || isCateogryFreece2(categoryId)
                || isCateogryTShirt(categoryId)
                || isCateogryWoolTShirt(categoryId)
                || isCateogrySocks(categoryId)
                || isCateogryFeatherCloth(categoryId);
    }
    
    public static boolean isCateogryShoes(String categoryId) {
        return isCateogrySnowShoes(categoryId)
                || isCateogryClimbShoes(categoryId)
                || isCateogryRuningShoes(categoryId);
    }
    
    public static boolean isCateogryDownClothes(String categoryId) {
        if ("131000".equals(categoryId) || "137000".equals(categoryId)) {
            // ダウンジャケット
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryRainClothes(String categoryId) {
        if ("1000".equals(categoryId) || "8800".equals(categoryId)|| "8000".equals(categoryId)) {
            // レインウェア 户外雨衣
            return true;
        }
        return false;
    }
    
    public static boolean isCateogrySoftShell1(String categoryId) {
        if ("22000".equals(categoryId) || "25000".equals(categoryId)) {
            // ソフトシェルジャケット
            return true;
        }
        return false;
    }
    
    public static boolean isCateogrySoftShell2(String categoryId) {
        if ("22500".equals(categoryId) || "23000".equals(categoryId)) {
            // ソフトシェルパンツ
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryWindBreaker(String categoryId) {
        if ("11500".equals(categoryId) || "11000".equals(categoryId)) {
            // ウインドブレーカー
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryHardShell1(String categoryId) {
        if ("141000".equals(categoryId) || "142000".equals(categoryId)) {
            // ハードシェル>ジャケット
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryHardShell2(String categoryId) {
        if ("145000".equals(categoryId) || "146000".equals(categoryId)) {
            // ハードシェル>パンツ
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryFreece1(String categoryId) {
        if ("122000".equals(categoryId) || "121000".equals(categoryId) || "126000".equals(categoryId)|| "124000".equals(categoryId)) {
            // フリース 抓绒衣
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryFreece2(String categoryId) {
        if ("123000".equals(categoryId)|| "129000".equals(categoryId)) {
            // フリースパンツ 抓毛裤
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryTShirt(String categoryId) {
        if ("44000".equals(categoryId) || "45500".equals(categoryId)) {
            // Tシャツ（半袖/長袖）
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryWoolTShirt(String categoryId) {
        if ("46500".equals(categoryId)) {
            // Tシャツ（半袖/長袖）
            return true;
        }
        return false;
    }
    
    public static boolean isCateogrySocks(String categoryId) {
        if ("97000".equals(categoryId) || "91000".equals(categoryId) || "93000".equals(categoryId)) {
            // ソックス
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryPack(String categoryId) {
        if ("261000".equals(categoryId) || "262000".equals(categoryId)
                || "263000".equals(categoryId)) {
            // 大型ザック
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryFeatherCloth(String categoryId) {
        if ("131000".equals(categoryId) || "137000".equals(categoryId)||"136000".equals(categoryId)) {
            // ダウンジャケット
            return true;
        }
        return false;
    }
    
    public static boolean isCateogrySnowShoes(String categoryId) {
        if ("241000".equals(categoryId)) {
            // 登山靴（アルパイン）
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryClimbShoes(String categoryId) {
        if ("241100".equals(categoryId) || "241200".equals(categoryId)) {
            // 登山靴（トレッキング + ハイキング）
            return true;
        }
        return false;
    }
    
    public static boolean isCateogryRuningShoes(String categoryId) {
        if ("241500".equals(categoryId) || "241600".equals(categoryId)) {
            // ウォーキング＆ランニング
            return true;
        }
        return false;
    }

    public static List<CategoryObject> readCategoryList() throws IOException {

        List<CategoryObject> categoryList = new ArrayList<CategoryObject>();
        BufferedReader br = null;
        String categoryFile = "C:/Users/niehp/Google ドライブ/taobao-niehtjp/montbell/category_edit.txt";
        CategoryObject rootCategory = null;
        CategoryObject p02Category = null;
        try {
            File file = new File(categoryFile);
            String str = null;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            while ((str = br.readLine()) != null) {
                if (!StringUtil.isBlank(str) && !str.startsWith("#")) {
                    if (str.startsWith("        ")) {
                        CategoryObject category = new CategoryObject();
                        category.rootCategory = rootCategory;
                        category.p02Category = p02Category;
                        String[] tmp = str.replace("        ", "").split(":");
                        category.categoryId = tmp[1];
                        category.categoryName = tmp[0];
                        categoryList.add(category);
                    } else if (str.startsWith("    ")) {
                        p02Category = new CategoryObject();
                        String[] tmp = str.replace("    ", "").split(":");
                        p02Category.categoryId = tmp[1];
                        p02Category.categoryName = tmp[0];
                    } else {
                        rootCategory = new CategoryObject();
                        String[] tmp = str.split(":");
                        rootCategory.categoryId = tmp[1];
                        rootCategory.categoryName = tmp[0];
                    }
                }
            }
        } finally {
            if (br != null)
                br.close();
        }
        return categoryList;
    }

    protected void scanCategory() throws ClientProtocolException, IOException {
        List<String> categoryList = new ArrayList<String>();
        String rootCategoryUrl = "http://webshop.montbell.jp/goods/?c=1";
        categoryList.add("ギア:1");
        scanCategory(categoryList, rootCategoryUrl);
        rootCategoryUrl = "http://webshop.montbell.jp/goods/?c=2";
        categoryList.add("クロージング:2");
        scanCategory(categoryList, rootCategoryUrl);
        for (String str : categoryList) {
            System.out.println(str);
        }
    }

    public static void scanCategory(List<String> categoryList, String parantCategoryUrl)
            throws ClientProtocolException, IOException {
        Document doc = TaobaoUtil.urlToDocumentByUTF8(parantCategoryUrl);
        Elements elements03 = doc.select("ul").select("li.level03Off");
        if (!elements03.isEmpty()) {
            for (Element elment : elements03) {
                Element a = elment.select("a").get(0);
                String href = a.attr("href");
                String cateName = a.attr("title");
                categoryList.add("        " + cateName + ":" + findCategoryId(href));
            }
            return;
        }
        Elements elements02 = doc.select("ul").select("li.level02Off");
        if (!elements02.isEmpty()) {
            for (Element elment : elements02) {
                Element a = elment.select("a").get(0);
                String href = a.attr("href");
                String cateName = a.attr("title");
                categoryList.add("    " + cateName + ":" + findCategoryId(href));
                scanCategory(categoryList, urlPrefix + href);
            }
        }
    }

    public static String findCategoryId(String href) {
        String queryS = href.substring(href.indexOf("?") + 1, href.length());
        return queryS.split("=")[1];
    }

    public static void composeBaobeiPictureStatus(GoodsObject item, BaobeiPublishObject obj,List<String> taobaoColors ) {
        String picStatus = "";
        // picture_status 图片状态：2;2;2;2;2;2;2;2;2;2;
        // 宝贝主图 main picture
        for(int i=0;i<item.pictureNameList.size();i++){
            if(i==5) break;
            picStatus +="2;";
        }
		int maxColorLen =  Math.min(item.colorList.size(), taobaoColors.size());
        // 销售属性图片
        for (int i = 0; i < maxColorLen; i++) {
            if(item.pictureNameList.size() == item.colorList.size()){
                // color picture
                picStatus +="2;";
            }
        }
        obj.picture_status ="\"" + picStatus + "\""  ;
    }

    public static void composeBaobeiPicture(GoodsObject item, BaobeiPublishObject obj,List<String> taobaoColors) {
        String picture = "";
        // picture　新图片：1128533_dkfs:1:0:|;1128533_mst:1:1:|;1128533_scl:1:2:|;1128533_tq:1:3:|;1128533_umr:1:4:|;1128533_dkfs:2:0:1627207:28320|;1128533_mst:2:0:1627207:28340|;1128533_scl:2:0:1627207:3232479|;1128533_tq:2:0:1627207:3232478|;1128533_umr:2:0:1627207:3232482|;
        // 宝贝主图 main picture
        for(int i=0;i<item.pictureNameList.size();i++){
            if(i==5) break;
            picture += item.pictureNameList.get(i) + ":1:" + i +":|;";
        }
		int maxColorLen =  Math.min(item.colorList.size(), taobaoColors.size());
        // 销售属性图片
        for (int i = 0; i < maxColorLen; i++) {
            if(item.pictureNameList.size() == item.colorList.size()){
                // color picture
                picture += item.pictureNameList.get(i) + ":2:0:1627207:" + taobaoColors.get(i) +"|;";
            }
        }

        obj.picture = "\"" + picture + "\"" ;
    }


    public static  void composeBaobeiTaobaoCategory(GoodsObject item,
            BaobeiPublishObject baobei) {
        String categoryId = baobei.cid;
        String prodCId = item.cateogryObj.categoryId;
        // TODO
        if (MontBellUtil.isCateogryDownClothes(prodCId) ) {
        	//羽绒衣|裤
            categoryId = "50014798";
        }
        if (MontBellUtil.isCateogryRainClothes(prodCId) ) {
            // レインウェア 户外雨衣
            categoryId = "124208012";
        }
        if (MontBellUtil.isCateogrySoftShell1(prodCId)
                || MontBellUtil.isCateogryHardShell1(prodCId)) {
            // ソフトシェルジャケット + ハードシェル>ジャケット 冲锋衣
            categoryId = "50014785";
        }
        if (MontBellUtil.isCateogrySoftShell2(prodCId)
                || MontBellUtil.isCateogryHardShell2(prodCId)) {
            // ソフトシェルパンツ + ハードシェル>パンツ 冲锋裤
            categoryId = "50014785";
        }
        if (MontBellUtil.isCateogryFreece1(prodCId) ) {
            // フリース 抓绒衣 抓绒衣
            categoryId = "50014787";
        }
        if (MontBellUtil.isCateogryFreece2(prodCId) ) {
            // フリースパンツ 抓绒裤 
            categoryId = "50014787";
        } 
        if (MontBellUtil.isCateogryTShirt(prodCId) ) {
            // Tシャツ（半袖/長袖）
            categoryId = "50013932";
        } 
        if (MontBellUtil.isCateogryPack(prodCId) ) {
            // 大型ザック
            categoryId = "";
        } 
        baobei.cid =  categoryId;
    }

    public static void composeBaobeiSubtitle(GoodsObject item,BaobeiPublishObject baobei) {
        baobei.subtitle =  "\"日本直邮！100%正品！真正的日本代购！" + item.titleOrg + "\"";
    }

    public static  void composeBaobeiTitle(GoodsObject item,
            BaobeiPublishObject baobei) {
        String title = "\"日本直邮";
        title += " MontBell";
        if(!StringUtil.isBlank(item.titleCN)){
            title += " " + item.titleEn ;
        }
        if(!StringUtil.isBlank(item.titleEn)){
            title += " " + item.titleEn ;
        }
        title += " " + item.productId;
        if(!StringUtil.isBlank(item.gender)){
            title += " " + item.gender;
        }
//        String suffix = "/包邮";
//        if (title.length() + suffix.length() < 60) {
//            title += suffix;
//        }
        baobei.title =  title + "\"";
    }
    public static  void composeBaobeiMyCategory(GoodsObject item,
            BaobeiPublishObject baobei) {

        String categoryId = baobei.seller_cids;
        String prodCId = item.cateogryObj.categoryId;
        // TODO
        if (MontBellUtil.isCateogryDownClothes(prodCId) ) {
        	//羽绒衣|裤
            categoryId = "1184361986";
        }
        
        if (MontBellUtil.isCateogryRainClothes(prodCId) ) {
            // レインウェア 户外雨衣
            categoryId = "1184361988";
        }
        if (MontBellUtil.isCateogrySoftShell1(prodCId)
                || MontBellUtil.isCateogryHardShell1(prodCId)) {
            // ソフトシェルジャケット + ハードシェル>ジャケット 冲锋衣
            categoryId = "1184361987";
        }
        if (MontBellUtil.isCateogrySoftShell2(prodCId)
                || MontBellUtil.isCateogryHardShell2(prodCId)) {
            // ソフトシェルパンツ + ハードシェル>パンツ 冲锋裤
            categoryId = "1184361987";
        }
        if (MontBellUtil.isCateogryFreece1(prodCId) ) {
            // フリース 抓绒衣 抓绒衣
            categoryId = "1289906242";
        }
        if (MontBellUtil.isCateogryFreece2(prodCId) ) {
            // フリースパンツ 抓绒裤 
            categoryId = "1289906242";
        } 
        if (MontBellUtil.isCateogryTShirt(prodCId) ) {
            // Tシャツ（半袖/長袖）
            categoryId = "1184361987";
        } 
        if (MontBellUtil.isCateogryPack(prodCId) ) {
            // 大型ザック
            categoryId = "";
        } 
        baobei.seller_cids =  categoryId;
    }

	public static String getStock(GoodsObject item, String colorName, String sizeName) {
		boolean isStock = false;
		for (StockObject stockObj : item.stockList) {
			if (stockObj.colorName.equals(colorName)
					&& stockObj.sizeName.equals(sizeName)) {
				isStock = stockObj.isStock;
				break;
			}
		}
		return isStock ? "999" : "0";
	}
}
