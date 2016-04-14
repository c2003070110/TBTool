package com.walk_nie.taobao.kakaku.explore.earphone;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.walk_nie.taobao.kakaku.KakakuBaobeiParser;
import com.walk_nie.taobao.kakaku.KakakuObject;

public class EarphoneBaobeiParser extends KakakuBaobeiParser {

    protected void parseItemSpec(Document doc, String itemUrl, KakakuObject obj) throws ClientProtocolException, IOException {
        obj.spec  = new SpecObject();
        
        super.parseItemBasicSpec(doc,  obj);
        
        Document docSpecDetail = EarphoneUtil.urlToDocumentKakaku(itemUrl + "/spec");
        Elements specDetail = docSpecDetail.select("div").select("#mainLeft")
                .select("table").select("tr");
        for (Element spec : specDetail) {
            if (StringUtil.isBlank(spec.attr("class"))
                    && spec.children().size() == 4) {
                EarphoneUtil.resolveSpecDetail(
                        spec.child(0).text(), spec.child(1).text(), obj);
                
                EarphoneUtil.resolveSpecDetail(
                        spec.child(2).text(), spec.child(3).text(), obj);
            }
        }
    }

    protected boolean isAllowToBaobei(KakakuObject obj) {
 
        if(obj.priceYodobashi == null){
            return false;
        }
        double d = (obj.priceYodobashi.price - obj.priceYodobashi.price * 0.1);
        if(d >  obj.priceMin.price){
            return false;
        }
//      if(obj.priceYodobashi.price < 1500){
//          return false;
//      }   
        if(obj.priceMin.price < 1500){
            return false;
        }
        return true;
        /*
        if(obj.priceYodobashi == null){
            if(obj.itemMaker.equals("SONY")){
                return false;
            }
            if(obj.itemMaker.equals("Audio Technica/铁三角")){
                return false;
            }
        }
        if(obj.priceMin != null){
            if(obj.priceMin.price < 1500){
                System.out.println("[WARN][price is Behind] " + 1500 + "[" + obj + "]");
                return false;
            }
            return true;
        }else{
            System.out.println("[WARN][price is ZERO] " + obj );
            return false;
        }
        */
    }
}
