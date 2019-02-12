package com.walk_nie.taobao.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.beust.jcommander.internal.Lists;

public class WebDriverUtil  {
    // TODO change it to fix your pc
    public final static String chromeDriverPath = "C:/Users/niehp/Google ドライブ/tool/chromedriver.exe";
    public final static String firefoxDriverPath = "C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.21.0-win64/geckodriver.exe";
    public final static String ieDriverPath = "C:/Users/niehp/Google ドライブ/tool/IEDriverServer_x64_3.4.0/IEDriverServer.exe";

    private static WebDriver driver = null;
    public static String watermark_common = "in/watermark0.png";
    public static String watermark_montbell = "in/watermark.png";

	public static WebDriver getFirefoxWebDriver() {
		System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		System.setProperty("webdriver.ie.driver", ieDriverPath);

		return new FirefoxDriver();
	}

	public static WebDriver getIEWebDriver() {
		System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		System.setProperty("webdriver.ie.driver", ieDriverPath);
        DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
        cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

		return new InternetExplorerDriver(cap);
	}

    public static WebDriver getWebDriver(String url) {
        System.setProperty("webdriver.gecko.driver", firefoxDriverPath);
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        System.setProperty("webdriver.ie.driver", ieDriverPath);
        FirefoxProfile profile = new FirefoxProfile(new File(
                "C:/Users/niehp/AppData/Roaming/Mozilla/Firefox/Profiles/i5bwa3vd.default"));

        if (driver == null) {
            //driver = new FirefoxDriver(profile);
            //driver = new ChromeDriver();
            DesiredCapabilities cap = DesiredCapabilities.internetExplorer();
            cap.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            driver = new InternetExplorerDriver(cap);
        }
        if (!url.equals(driver.getCurrentUrl())) {
            driver.get(url);
            Dimension win_size = driver.manage().window().getSize();
            WebElement html = driver.findElement(By.tagName("html"));
            int inner_width = Integer.parseInt(html.getAttribute("clientWidth"));
            int inner_height = Integer.parseInt(html.getAttribute("clientHeight"));

            // set the inner size of the window to 400 x 400 (scrollbar excluded)
            driver.manage().window().setSize(new Dimension(
                win_size.width + (910 - inner_width),
                win_size.height + (800 - inner_height)
            ));
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

	public static void screenShotV2(WebDriver driver, WebElement element, String saveTo, String pathToWaterMarkPNG)
			throws IOException {
		List<WebElement> elements = Lists.newArrayList();
		elements.add(element);
		screenShotV2(driver, elements, saveTo, pathToWaterMarkPNG);
	}

	public static void screenShotV2(WebDriver driver, List<WebElement> elements,
			String saveTo,String pathToWaterMarkPNG) throws IOException {
		String picSuffix = "png";
		File screenshot = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		BufferedImage fullImg = ImageIO.read(screenshot);
		List<BufferedImage> childImages = Lists.newArrayList();
		int maxW=0,ttlHeight=0;
		for(WebElement element:elements){
			Point point1 = element.getLocation();
			Dimension dim = element.getSize();
			BufferedImage eleScreenshot = fullImg.getSubimage(point1.getX(),
					point1.getY(), dim.getWidth(), dim.getHeight());
			Graphics2D g = eleScreenshot.createGraphics();
			g.drawImage(eleScreenshot.getScaledInstance(dim.getWidth(), dim.getHeight(),
					Image.SCALE_AREA_AVERAGING), 0, 0, dim.getWidth(), dim.getHeight(), null);
			g.dispose();
			childImages.add(eleScreenshot);
			if(maxW <dim.getWidth()){
				maxW = dim.getWidth();
			}
			ttlHeight += dim.getHeight();
		}
		BufferedImage combined = new BufferedImage(maxW, ttlHeight, BufferedImage.TYPE_INT_ARGB);
		int heightPos = 0;
		for(BufferedImage image:childImages){
			Graphics2D g = combined.createGraphics();
			g.drawImage(image, 0, heightPos, null);
			g.dispose();
			heightPos += image.getHeight();
		}
		
		// add water mark
		if (!StringUtil.isBlank(pathToWaterMarkPNG)) {
			Graphics2D g = combined.createGraphics();
			ImageIcon imgIcon = new ImageIcon(pathToWaterMarkPNG);
			int interval = 0;
			Image img = imgIcon.getImage();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					0.05f/* 水印透明度 */));
			for (int height = interval + imgIcon.getIconHeight(); height < combined
					.getHeight(); height = height + interval
					+ imgIcon.getIconHeight()) {
				for (int weight = interval + imgIcon.getIconWidth(); weight < combined
						.getWidth() + imgIcon.getIconWidth(); weight = weight
						+ interval + imgIcon.getIconWidth()) {
					g.drawImage(img, weight - imgIcon.getIconWidth(), height
							- imgIcon.getIconHeight(), null);
				}
			}
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
			g.dispose();
		}
        ImageIO.write(combined, picSuffix, screenshot);
		FileUtils.copyFile(screenshot, new File(saveTo));
	}

    public static void screenShot(WebDriver driver,String saveTo) throws ClientProtocolException, IOException {
           
        //Get entire page screenshot
        File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(screenshot, new File(saveTo));
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
            eleHeight = point2.getY() - point1.getY() - elements.get(elements.size() - 1).getSize().getHeight();
            eleWidth = elements.get(elements.size() - 1).getSize().getWidth() ;
        }
        
        if(eleWidth != 0  &&  eleHeight!=0 ){
        	if(eleHeight > fullImg.getHeight()){
        		eleHeight = fullImg.getHeight();
        	}
        	if(eleWidth > fullImg.getWidth()){
        		eleWidth = fullImg.getWidth();
        	}
            //Crop the entire page screenshot to get only element screenshot
            BufferedImage eleScreenshot= fullImg.getSubimage(point1.getX(), point1.getY(), eleWidth,
                eleHeight);
            String picSuffix = "png";

    		int originalH = eleHeight;
    		int originalW = eleWidth;

    		Graphics2D g = eleScreenshot.createGraphics();
    		g.drawImage(eleScreenshot.getScaledInstance(originalW, originalH,
    				Image.SCALE_AREA_AVERAGING), 0, 0, originalW, originalH, null);
    		
    			ImageIcon imgIcon = new ImageIcon(watermark_common);
    			int interval = 0;
    			Image img = imgIcon.getImage();
    			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
    					0.07f/* 水印透明度 */));
    			for (int height = interval + imgIcon.getIconHeight(); height < eleScreenshot
    					.getHeight(); height = height + interval
    					+ imgIcon.getIconHeight()) {
    				for (int weight = interval + imgIcon.getIconWidth(); weight < eleScreenshot
    						.getWidth() + imgIcon.getIconWidth(); weight = weight + interval
    						+ imgIcon.getIconWidth()) {
    					g.drawImage(img, weight - imgIcon.getIconWidth(), height
    							- imgIcon.getIconHeight(), null);
    				}
    			}
    			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    			
    		g.dispose();

            ImageIO.write(eleScreenshot, picSuffix, screenshot);
    		
            //Copy the element screenshot to disk
            FileUtils.copyFile(screenshot, new File(saveTo));
        	
        }
        
    }
}
