package com.walk_nie.taobao.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class WebDriverUtil  {
    // TODO change it to fix your pc
    public final static String chromeDriverPath = "C:/Users/niehp/Google ドライブ/tool/chromedriver.exe";

    private static WebDriver driver = null;

    public static WebDriver getWebDriver(String url) {
        FirefoxProfile profile = new FirefoxProfile(new File(
                "C:/Users/niehp/AppData/Roaming/Mozilla/Firefox/Profiles/i5bwa3vd.default"));

        if (driver == null) {
            driver = new FirefoxDriver(profile);
        }
        if (!url.equals(driver.getCurrentUrl())) {
            driver.get(url);
            return driver;
        }
        return driver;
    }

    public static Document urlToDocumentByWebDriver(String url)  {

        System.out.println("[START]parse URL =" + url);
        // WebDriver chromeDriver = new ChromeDriver();

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        WebDriver chromeDriver = new ChromeDriver();
        chromeDriver.get(url);
        String src = chromeDriver.getPageSource();
        chromeDriver.close();
        // chromeDriver.close();
        Document doc = Jsoup.parse(src);

        // System.out.println("[END]parse URL =" + url);
        return doc;
    }

    public static void screenShot(WebDriver driver,List<WebElement> elements,String saveTo) throws ClientProtocolException, IOException {
           
        //Get entire page screenshot
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        
        BufferedImage  fullImg = ImageIO.read(screenshot);
        //Get the location of element on the page
        Point point1 = elements.get(0).getLocation();
        //Get width and height of the element
        int eleWidth = 0 ;
        int eleHeight = 0;
        if (elements.size() == 1) {
            eleHeight = elements.get(0).getRect().height;
            eleWidth = elements.get(0).getSize().getWidth() ;
        } else if (elements.size() == 3) {
            Point point2 = elements.get(2).getLocation();
            eleHeight = point2.getY() - point1.getY();
            eleWidth = elements.get(2).getSize().getWidth() ;
        } else {
            Point point2 = elements.get(elements.size() - 1).getLocation();
            eleHeight = point2.getY() - point1.getY();
            eleWidth = elements.get(elements.size() - 1).getSize().getWidth() ;
        }
        
        if(eleWidth != 0  &&  eleHeight!=0 ){
            //Crop the entire page screenshot to get only element screenshot
            BufferedImage eleScreenshot= fullImg.getSubimage(point1.getX(), point1.getY(), eleWidth,
                eleHeight);
            String picSuffix = "png";
            ImageIO.write(eleScreenshot, picSuffix, screenshot);
            //Copy the element screenshot to disk
            FileUtils.copyFile(screenshot, new File(saveTo));
        	
        }
        
    }
}
