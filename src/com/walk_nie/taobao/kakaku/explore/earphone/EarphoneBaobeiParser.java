package com.walk_nie.taobao.kakaku.explore.earphone;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.kakaku.KakakuBaobeiParser;
import com.walk_nie.taobao.kakaku.KakakuObject;
import com.walk_nie.taobao.kakaku.KakakuUtil;
import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.taobao.util.WebDriverUtil;

public class EarphoneBaobeiParser extends KakakuBaobeiParser {
    String rootPathName = "out/kakaku/";

    protected void parseItemSpec(Document doc, String itemUrl, KakakuObject obj) throws ClientProtocolException, IOException {
        obj.spec  = new SpecObject();
        
        super.parseItemBasicSpec(doc,  obj);
        
        Document docSpecDetail = KakakuUtil.urlToDocumentKakaku(itemUrl + "/spec");
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

    @Override
    protected void parseMakerSite(KakakuObject kakakuObj) throws ClientProtocolException, IOException {
        
        if (StringUtil.isBlank(kakakuObj.spec.productInfoUrl)
                && StringUtil.isBlank(kakakuObj.spec.specInfoUrl)) {
            return;
        }
        String specUrl = !StringUtil.isBlank(kakakuObj.spec.productInfoUrl) ? kakakuObj.spec.productInfoUrl
                : kakakuObj.spec.specInfoUrl;
        if (specUrl.indexOf("audio-technica.co.jp") > 1) {
            parseMakerForAudioTechnica(kakakuObj, specUrl);
        } else if (specUrl.indexOf("jvckenwood.com") > 1) {
            parseMakerForJVC(kakakuObj, specUrl);
        } else if (specUrl.indexOf("pioneer-headphones.com") > 1) {
            parseMakerForPioneer(kakakuObj, specUrl);
        } else if (specUrl.indexOf("yamaha.com") > 1) {
            parseMakerForYamaha(kakakuObj, specUrl);
        } else if (specUrl.indexOf("denon.jp") > 1) {
            parseMakerForDenon(kakakuObj, specUrl);
        }
    }

    private void parseMakerForDenon(KakakuObject kakakuObj, String specUrl) throws ClientProtocolException, IOException {
       String fileNameFmt = "detail_%s.png";
        String fileName = String.format(fileNameFmt, kakakuObj.id);
        File despFile = new File(rootPathName, fileName);
        if (!despFile.exists()) {
            WebDriver webDriver = WebDriverUtil.getWebDriver(specUrl);
            List<WebElement> ele1 = webDriver.findElements(By.id("features"));
            List<WebElement> ele2 = webDriver.findElements(By.id("specs"));
            ele1.addAll(ele2);
            if (!ele1.isEmpty()) {
                WebDriverUtil.screenShot(webDriver, ele1, despFile.getAbsolutePath());
            }
        }
		if (despFile.exists()) {
			kakakuObj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
		}
    }

	private void parseMakerForYamaha(KakakuObject kakakuObj, String specUrl)
			throws ClientProtocolException, IOException {
		String fileNameFmt = "detail_%s_%d.png";
		int i = 0;
		String fileName = String.format(fileNameFmt, kakakuObj.id, i);
		File despFile = new File(rootPathName, fileName);
		WebDriver webDriver = WebDriverUtil.getWebDriver(specUrl);
		if (!despFile.exists()) {
			try {
				webDriver.findElement(By.name("feature")).click();
				List<WebElement> ele1 = webDriver
						.findElements(By.id("feature"));
				WebDriverUtil.screenShot(webDriver, ele1,
						despFile.getAbsolutePath());
				kakakuObj.detailScreenShotPicFile.add(despFile
						.getAbsolutePath());
			} catch (NoSuchElementException ex) {

			}
		}
		i++;
		fileName = String.format(fileNameFmt, kakakuObj.id, i);
		despFile = new File(rootPathName, fileName);
		if (!despFile.exists()) {
			try {
				webDriver.findElement(By.name("specs")).click();
				List<WebElement> ele1 = webDriver.findElements(By.id("specs"));
				WebDriverUtil.screenShot(webDriver, ele1,
						despFile.getAbsolutePath());
				kakakuObj.detailScreenShotPicFile.add(despFile
						.getAbsolutePath());
			} catch (NoSuchElementException ex) {

			}
		}
	}

    private void parseMakerForPioneer(KakakuObject kakakuObj, String specUrl) throws ClientProtocolException, IOException {
        String fileNameFmt = "detail_%s.png";
        String fileName = String.format(fileNameFmt, kakakuObj.id);
        File despFile = new File(rootPathName, fileName);
        if (!despFile.exists()) {
            WebDriver webDriver = WebDriverUtil.getWebDriver(specUrl);
            webDriver.findElement(By.cssSelector("acBtn")).click();
            List<WebElement> ele = webDriver.findElements(By.id("productArea"));
            if(ele.isEmpty()){
                ele = webDriver.findElements(By.id("specArea"));
            }
            if (!ele.isEmpty()) {
                WebDriverUtil.screenShot(webDriver, ele, despFile.getAbsolutePath());
            }
        }
		if (despFile.exists()) {
			kakakuObj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
		}
    }

    private void parseMakerForJVC(KakakuObject kakakuObj, String specUrl) throws ClientProtocolException, IOException {
        String fileNameFmt = "detail_%s.png";
        String fileName = String.format(fileNameFmt, kakakuObj.id);
        File despFile = new File(rootPathName, fileName);
        if (!despFile.exists()) {
            WebDriver webDriver = WebDriverUtil.getWebDriver(specUrl);
            List<WebElement> ele = webDriver.findElements(By.cssSelector("division"));
            if (!ele.isEmpty()) {
                WebDriverUtil.screenShot(webDriver, ele, despFile.getAbsolutePath());
            }
        }
		if (despFile.exists()) {
			kakakuObj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
		}
    }

    private void parseMakerForAudioTechnica(KakakuObject kakakuObj, String specUrl) throws ClientProtocolException, IOException {

        Document doc = TaobaoUtil.urlToDocumentByUTF8(specUrl);
        Elements picSelect = doc.select("table#photo_selecter").select("img");
        int idx = kakakuObj.pictureUrlList.size();
        for(Element element:picSelect){
            String picUrl = element.attr("src");
            picUrl = "http://www.audio-technica.co.jp/" + picUrl;
            kakakuObj.pictureUrlList.add(picUrl);
            kakakuObj.pictureNameList.add(kakakuObj.id + "-"  + idx);
            idx++;
        }
        String fileNameFmt = "detail_%s.png";
        String fileName = String.format(fileNameFmt, kakakuObj.id);
        File despFile = new File(rootPathName, fileName);
        if (!despFile.exists()) {
            WebDriver webDriver = WebDriverUtil.getWebDriver(specUrl);
            List<WebElement> ele = webDriver.findElements(By.id("spec"));
            if (!ele.isEmpty()) {
                WebDriverUtil.screenShot(webDriver, ele, despFile.getAbsolutePath());
            }
        }
		if (despFile.exists()) {
			kakakuObj.detailScreenShotPicFile.add(despFile.getAbsolutePath());
		}
    }

    protected boolean isAllowToBaobei(KakakuObject obj) {

        if(obj.priceMin == null){
            return false;
        }
        if(obj.priceYodobashi == null){
            return false;
        }
//        double d = (obj.priceYodobashi.price - obj.priceYodobashi.price * 0.1);
//        if(d >  obj.priceMin.price){
//            return false;
//        }
        if (obj.priceYodobashi.price < 1500) {
            return false;
        } 
//        if(obj.priceMin.price < 1500){
//            return false;
//        }
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
