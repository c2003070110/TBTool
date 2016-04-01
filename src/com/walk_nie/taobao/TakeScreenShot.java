package com.walk_nie.taobao;


import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class TakeScreenShot {
	private WebDriver driver;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	
		System.setProperty("webdriver.chrome.driver",args[0]);
		TakeScreenShot point = new TakeScreenShot();

		point.init();

		point.apply();
		
		point.stop();

	}
	public void init() throws Exception {
		driver = new ChromeDriver();
	    String baseUrl = "http://webshop.montbell.jp/goods/disp.php?product_id=1103239";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	    driver.get(baseUrl);
	    
	}
	public void apply() throws Exception {
		String src = driver.getPageSource();
		Document doc = Jsoup.parse(src);
		Elements elements = doc.select("div.type01");
		//String html = elements.get(0).outerHtml();
		String html = elements.outerHtml();
		
		drawByHtmlImageGenerator(html);
		
		drawByGraphics2D(html);
		
		//File newFile = ((TakesScreenshot)element).getScreenshotAs(OutputType.FILE);
		//File newFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		//FileUtils.copyFile(newFile, new File("c:/temp/screenshot.png"));
	    
	}

	protected void drawByGraphics2D(String html) throws Exception {
		int width = 780, height = 800;
		// Create a `BufferedImage` and create the its `Graphics`
		BufferedImage image = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(width, height);
		Graphics graphics = image.createGraphics();
		// Create an `JEditorPane` and invoke `print(Graphics)`
		JEditorPane jep = new JEditorPane("text/html", html);
		jep.setSize(width, height);
		jep.print(graphics);
		// Output the `BufferedImage` via `ImageIO`
		try {
			ImageIO.write(image, "png", new File("c:/temp/Graphics2D.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void drawByHtmlImageGenerator(String html) throws Exception {
//		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
//		imageGenerator.loadHtml(html);
//		imageGenerator.saveAsImage("c:/temp/html-image_generator.png");
	}

	public void stop() throws Exception {
		driver.quit();
		driver.close();
	}

}
