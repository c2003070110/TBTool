package com.walk_nie.taobao.montBell;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class MontbellProductParser {

    // change me!!
    double rate = 5.99 + 0.45;
    private final static double benefitRate = 0.086;
    
    String rootPathName = "out/MontBell/";

    String categoryUrlPrefix = "http://webshop.montbell.jp/goods/list.php?category=";

    String categoryUrlPrefix_fo = "http://webshop.montbell.jp/goods/list_fo.php?category=";

    String productUrlPrefix = "http://webshop.montbell.jp/goods/disp.php?product_id=";

    String productUrlPrefix_fo = "http://webshop.montbell.jp/goods/disp_fo.php?product_id=";

    String productSizeUrlPrefix = "http://webshop.montbell.jp/goods/size/?product_id=";
  
    public void scanSingleItem(GoodsObject goodsObj) throws IOException {

        String url = productUrlPrefix + goodsObj.productId;
        scanSingleItem(goodsObj,url);
         url = productUrlPrefix_fo + goodsObj.productId;
        scanSingleItem(goodsObj,url);
    }

    public void scanSingleItem(GoodsObject goodsObj,String url) throws IOException {
        Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
        if (doc.select("div.rightCont").size() == 0) {
            System.err.println("[ERROR] This is NOT product URL" + url);
            return ;
        }
        Element mainRightEle = doc.select("div.rightCont").get(0);

        Elements sizes = mainRightEle.select("div.sizeChoice").select("select").select("option");
        for (int i = 1; i < sizes.size(); i++) {
            String zie = sizes.get(i).text();
            if (!goodsObj.sizeList.contains(zie)) {
                goodsObj.sizeList.add(zie);
            }
        }

        Elements colors = mainRightEle.select("div#size_").select("table.dataTbl")
                .select("p.colorName");
        for (int i = 0; i < colors.size(); i++) {
            String color = colors.get(i).text().toLowerCase();
            if (!goodsObj.colorList.contains(color)) {
                goodsObj.colorList.add(color);
            }
        }

        // TODO
        //screenshotProductDetailDesp(goodsObj);
        //processProductSizeTable(goodsObj,mainRightEle);
    }

    protected void screenshotProductDetailDesp(GoodsObject goodsObj) throws ClientProtocolException,
            IOException {
        String fileNameFmt = "detail_%s.png";
        String fileName = String.format(fileNameFmt, goodsObj.productId);
        File despFile = new File(rootPathName, fileName);
        if (!despFile.exists()) {
            String url = productUrlPrefix + goodsObj.productId;
            WebDriver webDriver = WebDriverUtil.getWebDriver(url);
            List<WebElement> ele = webDriver.findElements(By.className("ttlType02"));
            if (!ele.isEmpty()) {
                WebDriverUtil.screenShot(webDriver, ele, despFile.getAbsolutePath());
            }
        }
        goodsObj.detailScreenShotPicFile = despFile.getAbsolutePath();
    }

    protected void processProductSizeTable(GoodsObject goodsObj, Element rootEl)
            throws ClientProtocolException, IOException {
        File rootPath = new File(rootPathName);
        String fileNameFmt = "sizeTable_%s_%d.jpg";
        try {
            Elements aboutSize = rootEl.select("p.aboutSize").select("select").select("option");
            Elements sizeA = aboutSize.select("a");
            if (sizeA == null || sizeA.isEmpty()) {
                return ;
            }
            String sizeUrl = productSizeUrlPrefix + goodsObj.productId;
            Document docSize = TaobaoUtil.urlToDocumentByUTF8(sizeUrl);
            Elements sizePics = docSize.select("div.innerCont").select("img");
            int i= 0;
            for (Element sizePic : sizePics) {
                String src = sizePic.attr("src");
                String picName = String.format(fileNameFmt, goodsObj.productId, i++);
                
                File picFile = new File(rootPath, picName);
                if (!picFile.exists()) {
                    TaobaoUtil.downloadPicture(rootPath.getAbsolutePath(),
                            MontBellUtil.urlPrefix + src, picName);
                }
                goodsObj.sizeTipPics.add(picFile.getAbsolutePath());
            }
        } catch (Exception ex) {

        }
    }

    public List<GoodsObject> scanItem(List<String> categoryIds) throws IOException {
        List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
        for (String categoryId : categoryIds) {
            if(StringUtil.isBlank(categoryId)) continue;
            CategoryObject categoryObj = new CategoryObject();
            categoryObj.categoryId = categoryId;
            scanItemByCategory(goodsList, categoryObj);
        }
        
        for (GoodsObject goodsObj : goodsList) {
            scanSingleItem(goodsObj);
        }

        List<GoodsObject> filteredProdList = filter(goodsList);
        
        translate(filteredProdList);
        
        return filteredProdList;
    }
    
    private List<GoodsObject> filter(List<GoodsObject> prodList) {
        List<GoodsObject> filterdList = Lists.newArrayList();
        List<String> productId = Lists.newArrayList();
        for(GoodsObject prod :prodList){
            if(!productId.contains(prod.productId)){
                productId.add(prod.productId);
                filterdList.add(prod);
            }
        }
        return filterdList;
    }

    private void translate(List<GoodsObject> prodList) {
        for(GoodsObject prod :prodList){
            prod.titleCN = translateTitle(prod);

            // (price + emsfee)*rate + benefit
            prod.priceCNY = convertToCNY(prod);
            System.out.println(""+prod.titleCN +":" + prod.titleOrg);
            
        }
    }

    public void scanItemByCategory(List<GoodsObject> goodsList, CategoryObject category) throws IOException {
        String cateogryUrl = categoryUrlPrefix + category.categoryId;
        scanItemByCategory(goodsList, category, cateogryUrl);
        // outlet
        cateogryUrl = categoryUrlPrefix_fo + category.categoryId;
        scanItemByCategory(goodsList, category, cateogryUrl);
    }

    protected void scanItemByCategory(List<GoodsObject> goodsList, CategoryObject category, String cateogryUrl)
            throws IOException {
        Document doc = TaobaoUtil.urlToDocumentByUTF8(cateogryUrl);
        scanItemByCategory(doc, goodsList, category);
        Elements pages = doc.select("div.resultArea").select("div.leftArea").select("p");
        if (pages.size() > 6) {
            for (int i = 3; i < pages.size() - 6; i++) {
                String pagedCategoryUrl = cateogryUrl + "&page=" + (i - 1);
                Document docPage = TaobaoUtil.urlToDocumentByUTF8(pagedCategoryUrl);
                scanItemByCategory(docPage, goodsList, category);
            }
        }
    }

    protected void scanItemByCategory(Document doc, List<GoodsObject> goodsList, CategoryObject category)
            throws IOException {
        Elements goods = doc.select("div.unit");
        for (Element goodsElement : goods) {
            GoodsObject goodsObj = new GoodsObject();
            goodsObj.cateogryObj = category;

            Elements ttl = goodsElement.select(".ttlType03");
            goodsObj.titleOrg = ttl.text();
            //goodsObj.goodTitleCN = translateTitle(goodsObj, ttl.text());

            String gender = ttl.select("img").attr("alt");
            goodsObj.genderOrg = gender;
            goodsObj.gender = translateGender(gender);

            Elements desp = goodsElement.select(".description").select("p");
            goodsObj.priceOrg = desp.get(0).text();
            scanItemForPriceJPY(goodsObj);
            
            String productId = desp.get(1).text().trim().replace("品番", "");
            goodsObj.productId = productId.replace("#", "").trim();

            goodsObj.brand = desp.get(2).text().replace("ブランド", "").trim();
            if (!goodsObj.brand.equals("モンベル")) {
                continue;
            }

            String weight = desp.get(3).text();
            scanItemForWeight(goodsObj, weight);

            //scanItemForSize(goodsObj, goodsElement);

            //scanItemForColor(goodsObj, goodsElement);

            if (!isScanReadyProduct(goodsList, goodsObj)) {
                addProduct(goodsList, goodsObj);
            }
        }
    }

    private String translateTitle(GoodsObject goodsObj) {
        String categoryId = goodsObj.cateogryObj.categoryId;
        String goodTitle = goodsObj.titleOrg;
        goodTitle = goodTitle.replace("Women's", "");
        goodTitle = goodTitle.replace("Men's", "");
        if (MontBellUtil.isCateogrySnowShoes(categoryId)) {
            String title = "防水透气防滑户外雪地登山鞋";
            goodsObj.weightExtra = 250;
            return title;
        }
        if (MontBellUtil.isCateogryClimbShoes(categoryId)) {
            String title = "防水透气防滑户外徒步鞋";
            goodsObj.weightExtra = 250;
            return title;
        }
        if (MontBellUtil.isCateogryRuningShoes(categoryId)) {
            String title = "透气防滑休闲鞋";
            goodsObj.weightExtra = 250;
            return title;
        }
        if (MontBellUtil.isCateogryPack(categoryId)) {
            // 大型ザック
            String newTitle = goodTitle;
            newTitle = newTitle.replace("Women's", "");
            newTitle = newTitle.replace("トレッキングパック", "");
            newTitle = newTitle.replace(" ", "");
            goodsObj.weightExtra = 250;
            return "户外背包 " + newTitle;
        }
        if (MontBellUtil.isCateogryRainClothes(categoryId)) {
            // ダウンジャケット
            goodsObj.weightExtra = 150;
            String title = "";
            if(goodTitle.indexOf("サイクル")!=-1){
                title +="自行车用!";
            };
            title += "速干!户外雨衣";
            return title;
        }
        if (MontBellUtil.isCateogryFeatherCloth(categoryId)) {
            // ダウンジャケット
            goodsObj.weightExtra = 150;
            return "户外羽绒衣";
        }
        if (MontBellUtil.isCateogrySoftShell1(categoryId)) {
            // ソフトシェルジャケット
            return "超轻!软壳冲锋衣";
        }
        if (MontBellUtil.isCateogrySoftShell2(categoryId)) {
            // ソフトシェルパンツ
            return "超轻!软壳冲锋裤";
        }
        if (MontBellUtil.isCateogryWindBreaker(categoryId)) {
            // ウインドブレーカー
            return "超轻!防水防风衣";
        }
        if (MontBellUtil.isCateogryHardShell1(categoryId)) {
            // ハードシェル>ジャケット
            return "冲锋衣";
        }
        if (MontBellUtil.isCateogryHardShell2(categoryId)) {
            // ハードシェル>パンツ
            return "冲锋裤";
        }
        if (MontBellUtil.isCateogryTShirt(categoryId)) {
            // Tシャツ（半袖/長袖）
            return "Wickron防晒速干t恤";
        }
        if (MontBellUtil.isCateogryWoolTShirt(categoryId)) {
            // Tシャツ（天然素材<メリノウールプラス>）
            return "保温通气速干羊毛衫";
        }
        if (MontBellUtil.isCateogrySocks(categoryId)) {
            // ソックス
            String title = "";
            if(goodTitle.indexOf("ウール")!=-1){
                title +="羊毛袜!";
            };
            title += "户外袜";
            return title;
        }
        if (MontBellUtil.isCateogryFreece1(categoryId)) {
            // フリース
            return "抓绒衣";
        }
        if (MontBellUtil.isCateogryFreece2(categoryId)) {
            // フリースパンツ
            return "抓绒裤";
        }
        return "";
    }

    private String translateGender(String gender) {
        if ("Unisex".equals(gender)) {
            return "";
            // return "男女通用";
        } else if ("Men".equals(gender)) {
            // return "男士";
            return "男款";
        } else if ("Women".equals(gender)) {
            // return "女士";
            return "女款";
        } else if ("Child".equals(gender)) {
            return "小孩";
        } else {
            return "";
        }
    }

    private String convertToCNY(GoodsObject item) {
        String priceStr = item.priceJPY;
        try {
            int price = Integer.parseInt(priceStr);
            long priceTax  = Math.round(price*1.08);
            int emsFee = TaobaoUtil.getEmsFee(item.weight + item.weightExtra);
            double priceCNY = ((priceTax + emsFee) * rate) / 100;
            priceCNY = priceCNY + priceCNY * benefitRate;
            return String.valueOf(Math.round(priceCNY));
        } catch (Exception ex) {
            ex.printStackTrace();
            return "XXXXXX";
        }
    }

    private void addProduct(List<GoodsObject> goodsList, GoodsObject goodsObj) {
        goodsList.add(goodsObj);
    }

    private boolean isScanReadyProduct(List<GoodsObject> goodsList, GoodsObject goodsObj) {
        return findGoodsObject(goodsList, goodsObj.productId) == null ? false : true;
    }

    protected GoodsObject findGoodsObject(List<GoodsObject> goodsList, String productId) {
        for (GoodsObject goods : goodsList) {
            if (goods.productId.equals(productId)) {
                return goods;
            }
        }
        return null;
    }

    private void scanItemForWeight(GoodsObject goodsObj, String weight) {
        goodsObj.weightOrg = weight;
        int doubleFl = 1;
        if (weight.indexOf("片足") != -1) {
            doubleFl = 2;
        }
        if (weight.indexOf("（") != -1) {
            weight = weight.substring(0, weight.indexOf("（"));
        }

        weight = weight.replace("【重量】", "");
        weight = weight.replace("【平均重量】", "");
        weight = weight.replace("【総重量】", "");
        weight = weight.replace(",", "");
        if (weight.indexOf("kg") > 0) {
            weight = weight.substring(0, weight.indexOf("kg"));
            weight = weight.replace(",", "");
            BigDecimal b = new BigDecimal(weight);
            weight = String.valueOf(b.multiply(new BigDecimal(1000)).intValue());
        }
        if (weight.indexOf("g") > 0) {
            weight = weight.substring(0, weight.indexOf("g"));
            weight = weight.replace(",", "");
        }
        try {
            goodsObj.weight = Integer.parseInt(weight) * doubleFl;
        } catch (Exception ex) {
            System.err.println("[ERROR]parse weight!category=" + goodsObj.cateogryObj.categoryId
                    + " product=" + goodsObj.productId);
            goodsObj.weight = 500;
        }
        goodsObj.weightExtra = 100;
    }

    private void scanItemForPriceJPY(GoodsObject goodsObj) {
        String price = goodsObj.priceOrg.replace("価格 ¥", "");
        price = price.replace(" +税", "");
        price = price.replace("アウトレット", "");
        price = price.replace(",", "");
        goodsObj.priceJPY = price;
    }

    private void scanItemForSize(GoodsObject goodsObj, Element good) {

        List<String> rslt = new ArrayList<String>();
        Elements size = good.select(".spec").select(".size").select("p");
        if (size.isEmpty() || size.size() == 1) {
            rslt.add("-");
            goodsObj.sizeList = rslt;
            return;
        }
        String sizeS = size.get(1).text();
        String tmp[] = sizeS.split(" ");
        for (String str : tmp) {
            str = str.replace("/", "");
            str = str.replace(" ", "");
            rslt.add(str);
        }
        goodsObj.sizeList = rslt;
    }

    private void scanItemForColor(GoodsObject goodsObj, Element good) {

        List<String> rslt = new ArrayList<String>();
        Elements color = good.select(".spec").select(".color").select("p");
        if (color.isEmpty() || color.size() == 1) {
            rslt.add("-");
            goodsObj.colorList = rslt;
            return;
        }

        String itemId = goodsObj.productId;
        for (int i = 1; i < color.size(); i++) {
            String tcolorS = color.get(i).select("a").attr("onclick");
            if (tcolorS.indexOf("s_" + itemId) > 0) {
                String tc = tcolorS.substring(tcolorS.indexOf("s_" + itemId)
                        + ("s_" + itemId).length() + 1);
                String trueS = tc.substring(0, tc.indexOf("."));
                rslt.add(trueS);
            } else {
                rslt.add("-");
            }
        }
        goodsObj.colorList = rslt;
    }

}
