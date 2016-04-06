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

import com.walk_nie.taobao.util.TaobaoUtil;



public class MontBellUtil {
    public static String urlPrefix = "http://webshop.montbell.jp";
    
    public static String pictureUrlFmt = "http://webshop.montbell.jp/common/images/product/prod_k/m_k_%s_%s.jpg";
    public static String pictureUrlFmt1 = "http://webshop.montbell.jp/common/images/product/prod_k/k_%s_%s.jpg";

	
	public static void downloadPicture(GoodsObject goods,String outFilePathPrice) {
		
		for(String color : goods.colorList){
		    color = color.replaceAll("/", "-");
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
        if ("123000".equals(categoryId)) {
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
}
