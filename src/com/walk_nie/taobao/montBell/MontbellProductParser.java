package com.walk_nie.taobao.montBell;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellProductParser {

    // change me!!
    double rate = 0.065;

    private final static double benefitRate = 0.1;


    String categoryUrlPrefix = "http://webshop.montbell.jp/goods/list.php?category=";

    String categoryUrlPrefix_fo = "http://webshop.montbell.jp/goods/list_fo.php?category=";

    String productUrlPrefix = "http://webshop.montbell.jp/goods/disp.php?product_id=";

    String productSizeUrlPrefix = "http://webshop.montbell.jp/goods/size/?product_id=";

    private List<String> sizePictureList = Lists.newArrayList();

    private Map<String, String> sizePictureMap = Maps.newHashMap();

    FirefoxProfile profile = new FirefoxProfile(new File(
            "C:/Users/niehp/AppData/Roaming/Mozilla/Firefox/Profiles/nu29zmti.default"));

    WebDriver driver = new FirefoxDriver(profile);

    public void scanSingleItem(GoodsObject goodsObj) throws IOException {
        String productId = goodsObj.productId;
        String url = productUrlPrefix + productId;
        goodsObj.productId = productId;
        Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
        if (doc.select("div.rightCont").size() == 0) {
            System.err.println("[ERRRO] " + url);
            return ;
        }
        Element mainRightEle = doc.select("div.rightCont").get(0);
        String priceOrg = mainRightEle.select("p.price").text();
        //goodsObj.weight = "900";
        goodsObj.priceOrg = priceOrg;
        scanItemForPriceJPY(goodsObj); 
        
        Elements sizes = mainRightEle.select("div.sizeChoice").select("select").select("option");
        for (int i = 1; i < sizes.size(); i++) {
            String zie = sizes.get(i).text();
            goodsObj.sizeList.add(zie);
        }

        Elements colors = mainRightEle.select("div#size_").select("table.dataTbl")
                .select("p.colorName");
        for (int i = 0; i < colors.size(); i++) {
            String color = colors.get(i).text().toLowerCase();
            goodsObj.colorList.add(color);
//            String picUrl = String.format(MontBellUtil.pictureUrlFmt, productId,color);
//            goodsObj.pictureUrlList.add(picUrl);
//            picUrl = String.format(MontBellUtil.pictureUrlFmt1, productId,color);
//            goodsObj.pictureUrlList.add(picUrl);
//            String picName = productId + "_" + color;
//            goodsObj.pictureLocalList.add(picName);
        }

        Elements eles = doc.select("div.type01");
        if (eles.size() > 2) {
            String disp0 = eles.get(0).outerHtml();
            String disp1 = eles.get(1).outerHtml();
            disp1 = disp1.replaceAll("カラー", "颜色");
            disp1 = disp1.replaceAll("サイズ", "鞋码/尺寸");
            goodsObj.detailDisp = disp0 + disp1;
        }
    }

    public List<GoodsObject> scanItem() throws IOException {
        List<CategoryObject> categoryList = MontBellUtil.readCategoryList();

        List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
        for (CategoryObject category : categoryList) {
            if (!this.isAllowCategory(category)) {
                continue;
            }
            if (this.isSkipCategory(category)) {
                continue;
            }
            scanItemByCategory(goodsList, category);
        }
        return goodsList;
    }

    public List<GoodsObject> scanItem(List<String> categoryIds) throws IOException {
        List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
        for (String categoryId : categoryIds) {
            CategoryObject categoryObj = new CategoryObject();
            categoryObj.categoryId = categoryId;
            scanItemByCategory(goodsList, categoryObj);
        }
        
        for(GoodsObject goodsObj :goodsList){
            scanSingleItem(goodsObj);
        }
        
        return goodsList;
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
            goodsObj.goodTitleOrg = ttl.text();
            goodsObj.goodTitle = translateTitle(goodsObj, ttl.text());

            String gender = ttl.select("img").attr("alt");
            goodsObj.genderOrg = gender;
            goodsObj.gender = translateGender(gender);

            Elements desp = goodsElement.select(".description").select("p");
            goodsObj.priceOrg = desp.get(0).text();
            // goodsObj.price = goodsObj.priceOrg.replace("価格 ¥", "")
            // .replace(" +税", "").replace("アウトレット", "").replaceAll(",", "");

            scanItemForPriceJPY(goodsObj);
            
            goodsObj.productId = desp.get(1).text().replace("品番#", "");

            goodsObj.brand = desp.get(2).text().replace("ブランド", "");
            if (!goodsObj.brand.equals("モンベル")) {
                continue;
            }

            String weight = desp.get(3).text();
            scanItemForWeight(goodsObj, weight);

            scanItemForSize(goodsObj, goodsElement);

            scanItemForColor(goodsObj, goodsElement);

            // (price + emsfee)*rate + benefit
            // goodsObj.price = convertToCNY(goodsObj);

            if (isScanReadyProduct(goodsList, goodsObj)) {
                appendProduct(goodsList, goodsObj);
            } else {
                // TODO
                // processItemForDetailDisp(goodsObj);
                addProduct(goodsList, goodsObj);
            }
        }
    }

    protected void processItemForDetailDisp(GoodsObject goodsObj) throws ClientProtocolException,
            IOException {
        String url = productUrlPrefix + goodsObj.productId;
        String picRootPath = "out/MontBell/detail_";
        File rootFile = new File("out/MontBell");
        String saveTo = picRootPath + goodsObj.productId + ".png";
        File despFile = new File(saveTo);
        // if (despFile.exists()) return;

        driver.get(url);

        List<WebElement> ele = driver.findElements(By.className("ttlType02"));
        if (!ele.isEmpty()) {
            TaobaoUtil.screenShot(driver, ele, saveTo);
            goodsObj.detailDisp = despFile.getAbsolutePath();
        }
        // Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
        // Elements eles = doc.select("div.type01");
        // if(eles.size() > 2){
        // String disp0 = eles.get(0).outerHtml();
        // String disp1 = eles.get(1).outerHtml();
        // disp1 = disp1.replaceAll("カラー", "颜色");
        // disp1 = disp1.replaceAll("サイズ", "鞋码/尺寸");
        // goodsObj.detailDisp = disp0 + disp1;
        // }

        try {
            WebElement aboutSize = driver.findElement(By.cssSelector("p.aboutSize"));
            WebElement sizeA = aboutSize.findElement(By.cssSelector("a"));
            if (sizeA != null) {
                String sizeUrl = productSizeUrlPrefix + goodsObj.productId;
                Document docSize = TaobaoUtil.urlToDocumentByUTF8(sizeUrl);
                Elements sizePics = docSize.select("div.innerCont").select("img");
                for (Element sizePic : sizePics) {
                    String src = sizePic.attr("src");
                    String picName = "sizeTable_" + goodsObj.productId + ".jpg";
                    if (!sizePictureList.contains(src)) {
                        sizePictureList.add(src);
                        TaobaoUtil.downloadPicture("MontBell", MontBellUtil.urlPrefix + src, picName);
                        sizePictureMap.put(src, picName);
                        goodsObj.sizeTips.add(new File(rootFile, picName).getAbsolutePath());
                    } else {
                        goodsObj.sizeTips.add(new File(rootFile, sizePictureMap.get(src))
                                .getAbsolutePath());
                    }
                }
            }
        } catch (Exception ex) {

        }
        // driver.close();
    }

    private String translateTitle(GoodsObject goodsObj, String goodTitle) {
        String categoryId = goodsObj.cateogryObj.categoryId;
        // newTitle = newTitle.replace("", "");
        // newTitle = newTitle.replace("", "");
        // newTitle = newTitle.replace("", "");
        // newTitle = newTitle.replace("", "");
        if ("241000".equals(categoryId)) {
            String title = "防水透气防滑户外雪地登山鞋";
            return title;
        }
        if ("241100".equals(categoryId) || "241200".equals(categoryId)) {
            String title = "防水透气防滑户外徒步鞋";
            return title;
        }
        if ("241500".equals(categoryId) || "241600".equals(categoryId)) {
            String title = "透气防滑休闲鞋";
            return title;
        }
        if ("261000".equals(categoryId) || "262000".equals(categoryId)
                || "263000".equals(categoryId)) {
            // 大型ザック
            String newTitle = goodTitle;
            newTitle = newTitle.replace("Women's", "");
            newTitle = newTitle.replace("トレッキングパック", "");
            newTitle = newTitle.replace("", "");
            newTitle = newTitle.replace("", "");
            newTitle = newTitle.replace(" ", "");
            return "户外背包 " + newTitle;
        }
        if ("131000".equals(categoryId) || "137000".equals(categoryId)) {
            // ダウンジャケット
            return "户外羽绒衣";
        }
        if ("136000".equals(categoryId)) {
            // コート（中綿入り）
            return "羽绒风衣";
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
            int emsFee = 0;
            int weight = item.weight;
            if (weight < 1000) {
                emsFee = 1800;
            } else if (weight < 2000) {
                emsFee = 3000;
            } else if (weight < 3000) {
                emsFee = 4000;
            } else if (weight < 4000) {
                emsFee = 5000;
            } else {
                emsFee = 6000;
            }
            double priceCNY = (price + emsFee) * rate;
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

    private void appendProduct(List<GoodsObject> goodsList, GoodsObject goodsObj) {
        GoodsObject oldObj = findGoodsObject(goodsList, goodsObj.productId);
        for (String color : goodsObj.colorList) {
            if (!oldObj.colorList.contains(color)) {
                oldObj.colorList.add(color);
            }
        }
        for (String size : goodsObj.sizeList) {
            if (!oldObj.sizeList.contains(size)) {
                oldObj.sizeList.add(size);
            }
        }
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
        goodsObj.weight = Integer.parseInt(weight) * doubleFl;
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

    protected boolean isSkipCategory(CategoryObject category) {
        if (category.p02Category != null) {
            if (skipP02Category.contains(category.p02Category.categoryId)) {
                return true;
            }
        }
        return skipCategory.contains(category.categoryId);
    }

    protected boolean isAllowCategory(CategoryObject category) {
        if (category.p02Category != null) {
            if (allowP02Category.contains(category.p02Category.categoryId)) {
                return true;
            }
        }
        return allowCategory.contains(category.categoryId);
    }

    private List<String> allowP02Category = new ArrayList<String>();
    {
        // allowP02Category.add("33");
    }

    private List<String> allowCategory = new ArrayList<String>();
    {
        // TODO
        // allowCategory.add("241000");// 登山靴（アルパイン）
        // allowCategory.add("241100");// 登山靴（トレッキング）
        // allowCategory.add("241200");// 登山靴（ハイキング）
        // allowCategory.add("241500");// ウォーキング＆ランニング
        // allowCategory.add("241600");// トラベル＆タウン

        // allowCategory.add("261000");// 大型ザック（50～120L）
        // allowCategory.add("262000");// 中型ザック（30～45L）
        // allowCategory.add("263000");// 小型ザック（5～25L）

        // allowCategory.add("131000"); // ダウンジャケット
        // allowCategory.add("137000"); // ダウンジャケット（軽量シリーズ）

        // allowCategory.add("136000"); // コート（中綿入り）

        allowCategory.add(""); //
        allowCategory.add(""); //
        allowCategory.add(""); //
    }

    private List<String> skipP02Category = new ArrayList<String>();
    {
        skipP02Category.add("37"); // バッグ
        skipP02Category.add("51"); // スノーギア
        skipP02Category.add("39"); // クッカー/バーナー/ボトル/食品
        skipP02Category.add("38"); // アクセサリー
        skipP02Category.add("46"); // 自転車
        skipP02Category.add("48"); // キャンプ・ファニチャー
        skipP02Category.add("53"); // 照明器具（ライト）/チャージャー
        skipP02Category.add("44"); // 防災用品
        skipP02Category.add("34"); // ストック
        skipP02Category.add("54"); // サングラス/ゴーグル
        skipP02Category.add("41"); // 犬用
        skipP02Category.add("50"); // ハイドレーション
        skipP02Category.add("49"); // ヘルメット
        skipP02Category.add("52"); // メンテナンス/リペア
        skipP02Category.add("35"); // カヌー・カヤック・SUP（本体）/パドル
        skipP02Category.add("47"); // 水遊び
        skipP02Category.add("42"); // エコプロダクツ
        skipP02Category.add("64"); // 書籍・CD・DVD
        skipP02Category.add("89"); // 社会貢献プロダクツ

        skipP02Category.add("1"); // 雨具
        skipP02Category.add("4"); // シャツ/ポロシャツ/スウェット
        skipP02Category.add("9"); // 手袋
        skipP02Category.add("10"); // ソックス
        skipP02Category.add("11"); // 帽子
        skipP02Category.add("21"); // マフラー/ネックゲーター
        skipP02Category.add("15"); // キッズ
        skipP02Category.add("16"); // ベビー
        skipP02Category.add("28"); // 特集一覧
        skipP02Category.add("62"); // USモデル（クロージング）
        skipP02Category.add("19"); // 水遊び
        skipP02Category.add("88"); // 社会貢献プロダクツ
        skipP02Category.add(""); //
    }

    private List<String> skipCategory = new ArrayList<String>();
    {
        skipCategory.add("242000"); // サンダル
        skipCategory.add("243000"); // スパッツ
        skipCategory.add("241400"); // アイゼン
        skipCategory.add("245000"); // メンテナンス/リペア
        skipCategory.add("246000"); // 靴ひも/その他

        skipCategory.add("263500"); // ザック（ポケッタブル）
        skipCategory.add("266500"); // 女性用ザック
        skipCategory.add("266000"); // 子供用ザック
        skipCategory.add("265000"); // キャメルバック
        skipCategory.add("269000"); // ベビーキャリア
        skipCategory.add("269500"); // その他ザック
        skipCategory.add("264000"); // ザックカバー
        skipCategory.add("268000"); // ザックアクセサリー

        skipCategory.add("206000"); // シェルター
        skipCategory.add("207000"); // ツェルト
        skipCategory.add("204000"); // ジュピタードーム
        skipCategory.add("205000"); // ヘリオスドーム
        skipCategory.add("208000"); // タープ
        skipCategory.add("208600"); // 常設用テント（グランピング）
        skipCategory.add("209000"); // テントリペア
        skipCategory.add("212000"); // テントオプション

        skipCategory.add("1802000"); // 自転車用手袋/小物
        skipCategory.add(""); //
        skipCategory.add(""); //
        skipCategory.add(""); //
        skipCategory.add(""); //
    }

}
