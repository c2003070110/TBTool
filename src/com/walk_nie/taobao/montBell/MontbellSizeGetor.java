package com.walk_nie.taobao.montBell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.walk_nie.taobao.util.TaobaoUtil;

public class MontbellSizeGetor  {
	//private static String urlPrefix = "http://webshop.montbell.jp";
	//private static String categoryUrlPrefix = "http://webshop.montbell.jp/goods/list.php?category=";
	//private static String productUrlPrefix = "http://webshop.montbell.jp/goods/disp.php?product_id=";
	//private static String productSizeUrlPrefix = "http://webshop.montbell.jp/goods/size/?product_id=";
	private static Map<String,String> sizePictureMap = Maps.newHashMap();
	private static List<String> sizePictureList = Lists.newArrayList();

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

//		MontbellProductParser parser = new MontbellProductParser();
//		List<CategoryObject> categoryList = parser.readCategoryList();
//		for (CategoryObject category : categoryList) {
//			List<GoodsObject> goodsList = new ArrayList<GoodsObject>();
//			scanItem(goodsList, category);
//			scanSize(goodsList);
//		}
//		downloadSizePicture();
		screenShot();
	}
	protected static void screenShot() throws ClientProtocolException, IOException {

		//FirefoxProfile profile = new FirefoxProfile(new File("C:/Users/niehp/AppData/Roaming/Mozilla/Firefox/Profiles/nu29zmti.default"));
		WebDriver driver = new FirefoxDriver();
		
		String url = "http://webshop.montbell.jp/goods/disp.php?product_id=1128531";
		driver.get(url);
		List<WebElement> ele = driver.findElements(By.className("ttlType02"));  
		   
		//Get entire page screenshot
		File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenshot, new File("c:\\temp\\GoogleLogo_screenshot_all.png"));
		
		BufferedImage  fullImg = ImageIO.read(screenshot);
		//Get the location of element on the page
		Point point1 = ele.get(0).getLocation();
		Point point2 = null;
		if (ele.size() > 3) {
			point2 = ele.get(2).getLocation();
		} else {
			point2 = ele.get(ele.size() - 1).getLocation();
		}
		
		//Get width and height of the element
		int eleWidth = ele.get(0).getSize().getWidth() ;
		int eleHeight = point2.getY() - point1.getY();
		
		//Crop the entire page screenshot to get only element screenshot
		BufferedImage eleScreenshot= fullImg.getSubimage(point1.getX(), point1.getY(), eleWidth,
		    eleHeight);
		ImageIO.write(eleScreenshot, "png", screenshot);
		//Copy the element screenshot to disk
		FileUtils.copyFile(screenshot, new File("c:\\temp\\GoogleLogo_screenshot.png"));
	}

	protected static void downloadSizePicture() throws ClientProtocolException, IOException {

		int i = 0;
		for (String picSrc : sizePictureList) {
			String picName = "MontBell_sizeTable_" + i;
			TaobaoUtil.downloadPicture("MontBell_sizeTable", MontBellUtil.urlPrefix + picSrc, picName);
			System.out.println("map.put(\"" + picSrc +"\"," + "\""+picName+"\");");
			i++;
		}
		
	}

	protected static void scanSize(List<GoodsObject> goodsList) throws ClientProtocolException, IOException {
		for (GoodsObject goods : goodsList) {
			String url = MontBellUtil.productUrlPrefix  + goods.productId;
			Document doc = TaobaoUtil.urlToDocumentByUTF8(url);
			Elements elesSize = doc.select("p.aboutSize").select("a");
			if(elesSize.size() !=0){
				String sizeUrl = MontBellUtil.productSizeUrlPrefix  + goods.productId;
				Document docSize = TaobaoUtil.urlToDocumentByUTF8(sizeUrl);
				Elements sizePics = docSize.select("div.innerCont").select("img");
				int i = 0;
				for (Element sizePic : sizePics) {
					String src = sizePic.attr("src");
					if(!sizePictureList.contains(src)){
						sizePictureList.add(src);
					}
					sizePictureMap.put(goods.productId +i, src);
					i++;
				}
			}
		}
	}

	protected static void scanItem(List<GoodsObject> goodsList,
			CategoryObject category) throws ClientProtocolException, IOException {
		String cateogryUrl = MontBellUtil.categoryUrlPrefix + category.categoryId;
		Document doc = TaobaoUtil.urlToDocumentByUTF8(cateogryUrl);
		Elements goods = doc.select("div.unit");
		for (Element goodsElement : goods) {
			GoodsObject goodsObj = new GoodsObject();
			goodsObj.cateogryObj = category;

			Elements desp = goodsElement.select(".description").select("p");
			goodsObj.productId = desp.get(1).text().replace("品番#", "");
			
			goodsList.add(goodsObj);
		}
	}

}
