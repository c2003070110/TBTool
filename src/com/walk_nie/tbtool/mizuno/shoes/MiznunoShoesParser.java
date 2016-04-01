package com.walk_nie.tbtool.mizuno.shoes;

import java.util.List;

import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beust.jcommander.internal.Lists;


public class MiznunoShoesParser {
    
    private String urlPrefix = "http://www.asics.com/";

    public List<MizunoShoesObject> parse(){
        List<MizunoShoesObject> prodList = Lists.newArrayList();
        String url = "";
        parseCategory(prodList,url);
        
        parseProducts(prodList);
        
        List<MizunoShoesObject> filteredProdList = filter(prodList);
        
        translate(filteredProdList);
        
        return prodList;
    }
    private void parseCategory(List<MizunoShoesObject> prodList, String url) {
        Document doc = getDocument(url);
        Elements prodEls = doc.select("div#main").select("div.compo_item-list_cmn _disp").select("div.list");
        for(Element rootEl :prodEls){
            MizunoShoesObject prodObj = new MizunoShoesObject();
            Element root = rootEl.select("div.list-cont").get(0);
            
            Elements genderEls = rootEl.select("p.ico").select("img");
            for(Element ele :genderEls){
                String alt = ele.attr("alt");
                if(alt.equals("レディース")){
                    prodObj.gender = 2;
                }else{
                    prodObj.gender = 3; 
                }
            }
            
            prodObj.prodUrl = root.select("p.img").select("a.productMainLink").attr("href");
            
            prodObj.titleOrg = root.select("p.name").text();
            
            String price = rootEl.select("p.price-wrap").text();
            price = price.replaceAll("¥", ""); 
            price = price.replaceAll(",", ""); 
            prodObj.priceOrg = price;
            
            prodObj.kataban = rootEl.select("p.item-number").text();
            prodList.add(prodObj);
        }
    }
    private void parseProducts(List<MizunoShoesObject> prodList) {
        for(MizunoShoesObject prod :prodList){
            parseProduct(prod);
        }
    }
    
    private void parseProduct(MizunoShoesObject prodObj) {
        Document doc = getDocument(urlPrefix + prodObj.prodUrl);
        Element headerEl = doc.select("div#main").select("div.item_detail").get(0);
        
        Elements picEls = headerEl.select("div.thumbnail-colors").select("ul").get(1).select("img");
        for(Element picEl:picEls){
            String picUrl = picEl.attr("data-zoom-image");
            if(StringUtil.isBlank(picUrl)){
                picUrl = picEl.attr("src");
            }
            prodObj.picUrlList.add(picUrl);
        }
        
        Elements sizeEls = headerEl.select("div.item_size").select("ul").get(1).select("a");
        for(Element sizeEl:sizeEls){
            MizunoShoesObject.SizeOption sizeOpt = prodObj.new SizeOption();
            if(sizeEl.hasClass("nostock")){
                sizeOpt.isStock = false;
            }else{
                sizeOpt.isStock = true;
            }
            sizeOpt.sizeLabel = sizeEl.text();
            prodObj.sizeOptList.add(sizeOpt);
        }
        
        Elements colorEls = headerEl.select("ul.list_colors").select("a");
        if(colorEls.size() > 1){
            for(Element colorEl:colorEls){
                if(colorEl.hasClass("select")){
                    continue;
                }
                String colorPic = colorEl.select("img").attr("src");
                prodObj.colorList.add(colorPic);
            }
        }
    }

    private List<MizunoShoesObject> filter(List<MizunoShoesObject> prodList) {
        List<MizunoShoesObject> filterdList = Lists.newArrayList();
        List<String> urls = Lists.newArrayList();
        for(MizunoShoesObject prod :prodList){
            if(!urls.contains(prod.prodUrl)){
                urls.add(prod.prodUrl);
                filterdList.add(prod);
            }
        }
        return filterdList;
    }

    private void translate(List<MizunoShoesObject> prodList) {
        for(MizunoShoesObject prod :prodList){
            translateTitle(prod);
        }
    }
    
    private void translateTitle(MizunoShoesObject prod) {
        String title = prod.titleOrg;
        // TODO 
        title = title.replaceAll("", "");
        prod.title = title;
    }
    private Document getDocument(String url){
        return null;
    }
    
}
