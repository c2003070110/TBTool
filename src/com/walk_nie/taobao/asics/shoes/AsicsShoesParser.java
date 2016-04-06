package com.walk_nie.taobao.asics.shoes;

import java.io.File;
import java.io.IOException;
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

public class AsicsShoesParser {
    
    private String urlPrefix = "http://www.asics.com/";
    
    private String rootPathName = "out/asicsShoes/";

    public List<AsicsShoesObject> parse() throws ClientProtocolException, IOException{
        List<AsicsShoesObject> prodList = Lists.newArrayList();
        String url = "";
        parseCategory(prodList,url);
        
        parseProducts(prodList);
        
        List<AsicsShoesObject> filteredProdList = filter(prodList);
        
        translate(filteredProdList);
        
        return prodList;
    }
    protected void parseCategory(List<AsicsShoesObject> prodList, String url) throws ClientProtocolException, IOException {
        Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
        Elements prodEls = doc.select("div.container").select("div.product-list").select("div.gridProduct");
        for(Element rootEl :prodEls){
            AsicsShoesObject prodObj = new AsicsShoesObject();
            prodObj.prodUrl = rootEl.select("a.productMainLink").attr("href");
            prodObj.titleOrg = rootEl.select("p.prod-name").text();
            prodObj.subTitleOrg = rootEl.select("p.prod-classification-reference").text();
            String price = rootEl.select("p.price").text();
            price = price.replaceAll("¥", ""); 
            price = price.replaceAll(",", ""); 
            prodObj.priceOrg = price;
            
            prodList.add(prodObj);
        }
    }
    protected void parseProducts(List<AsicsShoesObject> prodList) throws ClientProtocolException, IOException {
        for(AsicsShoesObject prod :prodList){
            parseProduct(prod);
        }
    }
    
    protected void parseProduct(AsicsShoesObject prodObj) throws ClientProtocolException, IOException {
        Document doc = TaobaoUtil.urlToDocumentByUTF8(urlPrefix + prodObj.prodUrl);
        Element headerEl = doc.select("div.container").select("div.nm").get(0);
        
        Elements picEls = headerEl.select("div.rsContainer").select("div.rsSlide").select("img");
        for(Element picEl:picEls){
            String picUrl = picEl.attr("data-big");
            if(StringUtil.isBlank(picUrl)){
                picUrl = picEl.attr("src");
            }
            prodObj.picUrlList.add(picUrl);
        }
        
        String prodMeta = headerEl.select("p.single-prod-meta").text();
        int idx = prodMeta.indexOf("_");
        prodObj.kataban = prodMeta.substring(0,idx);
        prodObj.id = prodMeta.substring(idx+1);
        
        Elements sizeEls = headerEl.select("ul#SelectSizeDropDown").select("li");
        for(Element sizeEl:sizeEls){
            AsicsShoesObject.SizeOption sizeOpt = prodObj.new SizeOption();
            if(sizeEl.hasClass("inStock")){
                sizeOpt.isStock = true;
            }
            if(sizeEl.hasClass("disabled")){
                sizeOpt.isStock = false;
            }
            sizeOpt.sizeLabel = sizeEl.text();
            prodObj.sizeOptList.add(sizeOpt);
        }
        
        Elements colorEls = headerEl.select("div.prod-sizes").select("div");
        if(colorEls.size() > 1){
            for(Element colorEl:colorEls){
                if(colorEl.hasClass("active")){
                    continue;
                }
                AsicsShoesObject colorObj = new AsicsShoesObject();
                colorObj.prodUrl = colorEl.select("a").attr("href");
                parseProduct(colorObj);
                prodObj.colorList.add(colorObj);
            }
        }
        
        screenshotProductDetailDesp(prodObj);
    }

    protected void screenshotProductDetailDesp(AsicsShoesObject prodObj) throws ClientProtocolException, IOException {
        String fileNameFmt = "detail_%s.png";
        String fileName = String.format(fileNameFmt, prodObj.kataban);
        File despFile = new File(rootPathName, fileName);
        if (!despFile.exists()) {
            String url = urlPrefix + prodObj.prodUrl;
            WebDriver webDriver = WebDriverUtil.getWebDriver(url);
            
            List<WebElement> ele = webDriver.findElements(By.className("tabInfoChildContent"));
            
            if (!ele.isEmpty()) {
                WebDriverUtil.screenShot(webDriver, ele, despFile.getAbsolutePath());
            }
        }
        prodObj.detailScreenShotPicFile = despFile.getAbsolutePath();
    }

    protected List<AsicsShoesObject> filter(List<AsicsShoesObject> prodList) {
        List<AsicsShoesObject> filterdList = Lists.newArrayList();
        List<String> urls = Lists.newArrayList();
        for(AsicsShoesObject prod :prodList){
            if(!urls.contains(prod.prodUrl)){
                urls.add(prod.prodUrl);
                filterdList.add(prod);
            }
        }
        return filterdList;
    }

    protected void translate(List<AsicsShoesObject> prodList) {
        for(AsicsShoesObject prod :prodList){
            translateTitle(prod);
        }
    }
    
    protected void translateTitle(AsicsShoesObject prod) {
        String title = prod.titleOrg;
        // TODO 
        title = title.replaceAll("", "");
        prod.titleCN = title;
    }
}
