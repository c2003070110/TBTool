package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

public class PanBaidu  {

    protected  BufferedReader stdReader = null;
    protected  boolean loopFlag = true;
    
    protected boolean fetchBreakFlag = false;

	public static void main(String[] args) throws IOException {

		PanBaidu main = new PanBaidu();
		String user ="manga2014yy@126.com";
		String pass ="panbaidu2015";
		if(args.length==2){
			user = args[0];
			pass = args[1];
		}
		main.execute(user,pass);
	}
    
    protected  void execute(String user,String pass) throws IOException {
    	WebDriver driver = logon(user,pass);
    	while(true){
            System.out.println("Goto the FOLDER to generate link!");
            mywait1();
        	if(!loopFlag){
        		break;
        	}
			try {
				generateHref(driver);
				System.out.println("Finished on this FOLDER!");
			} catch (Exception ignore) {
				ignore.printStackTrace();
			}
    	}
    	driver.close();
    }

    private WebDriver logon(String user, String pass) throws IOException {
    	String panBaiduRootUrl = "http://pan.baidu.com/";
        System.setProperty("webdriver.chrome.driver", "C:/Users/niehp/Google ドライブ/tool/chromedriver.exe");
        System.setProperty("webdriver.gecko.driver", "C:/Users/niehp/Google ドライブ/tool/geckodriver-v0.16.1.exe");
        //WebDriver driver =  new ChromeDriver();
        WebDriver driver =  new FirefoxDriver();
        driver.get(panBaiduRootUrl);
        System.out.println("Swtich to USER/PASSWORD");
        mywait();

        driver.findElement(By.id("TANGRAM__PSP_4__userName")).sendKeys(user);
        driver.findElement(By.id("TANGRAM__PSP_4__password")).sendKeys(pass);
   
		return driver;
	}

	protected void generateHref(WebDriver driver) throws IOException {

	       StringBuffer links = new StringBuffer();
		WebElement listRoot = driver.findElement(By.className("vdAfKMb"));
		List<WebElement> list = listRoot.findElements(By.tagName("dd"));
		
		for (int i = 0; i < list.size(); i++) {
			WebElement element = list.get(i);
			// 启动分享Dialog
//			while(true){
//				try{
//					Actions builder = new Actions(driver);
//					builder.moveToElement(element).build().perform();
//					break;
//				}catch(MoveTargetOutOfBoundsException e){
//					element.sendKeys(Keys.PAGE_DOWN);
//				}
//			}
			
			element.findElement(By.cssSelector("a[data-button-id=\"b51\"]"))
					.click();
			
			// 等待分享Dialog是否打开
//			WebDriverWait wait1 = new WebDriverWait(driver,10);
//			wait1.until(new ExpectedCondition<Boolean>(){
//				@Override
//				public Boolean apply(WebDriver driver) {
//					while (true){
//						try {
//							driver.findElement(By
//									.cssSelector("div[id=\"share\"]"));
//							return true;
//						} catch (Exception e) {
//
//						}
//					}
//				}
//			});
//			mysleep(1);
			// System.out.println(driver.getPageSource());
			mywait();
			//if(fetchBreakFlag)break;
			// System.out.println(driver.getPageSource());
			
			WebElement dialogRoot = driver.findElement(By
					.cssSelector("div[id=\"share\"]"));

			// 取得分享的文件名
			WebElement dialogHeader = dialogRoot.findElement(By
					.className("dialog-header"));
			String fileName = dialogHeader.findElement(By.tagName("h3"))
					.getText();
			System.out.println("Generating Link for" + fileName);
			fileName = fileName.replace("分享文件(夹):", "");

			WebElement closeBtn = dialogHeader.findElement(By
					.cssSelector("div[class=\"dialog-control\"]"));

			WebElement dialogBody = dialogRoot.findElement(By
					.cssSelector("div[class=\"dialog-body\"]"));

			WebElement shareLink = dialogBody.findElement(
					By.cssSelector("ul[class=\"tab\"]")).findElements(
					By.cssSelector("li")).get(0);
			shareLink.click();
			//mysleep(1.5);

//			wait1.until(new ExpectedCondition<Boolean>() {
//				@Override
//				public Boolean apply(WebDriver driver) {
//					while (true) {
//						try {
//							WebElement dialogRoot = driver.findElement(By
//									.cssSelector("div[id=\"share\"]"));
//							WebElement createBtn = dialogRoot.findElement(
//									By.cssSelector("div[class=\"footer\"]"))
//									.findElement(By.className("create"));
//							if(!createBtn.isDisplayed()){
//								WebElement dialogBody = dialogRoot.findElement(By
//										.cssSelector("div[class=\"dialog-body\"]"));
//								List<WebElement> shareLinks = dialogBody.findElement(
//										By.cssSelector("ul[class=\"tab\"]")).findElements(
//										By.cssSelector("li"));
//								shareLinks.get(1).click();
//								mysleep(1);
//								shareLinks.get(0).click();
//							}
//							return true;
//						} catch (Exception e) {
//
//						}
//					}
//				}
//			});
			WebElement dialogFoot = dialogRoot.findElement(By
					.cssSelector("div[class=\"footer\"]"));
			dialogFoot.findElement(By.className("create")).click();
			//mysleep(1.5);

			mywait();
			//if(fetchBreakFlag)break;
			// System.out.println(driver.getPageSource());

//			wait1.until(new ExpectedCondition<Boolean>() {
//
//				@Override
//				public Boolean apply(WebDriver driver) {
//					while (true) {
//						try {
//							WebElement dialogRoot = driver.findElement(By
//									.cssSelector("div[id=\"share\"]"));
//							WebElement dialogBody = dialogRoot.findElement(By
//									.cssSelector("div[class=\"dialog-body\"]"));
//							WebElement linkInfo = dialogBody
//									.findElement(
//											By.cssSelector("ul[class=\"content\"]"))
//									.findElement(
//											By.cssSelector("li[class=\"share-link\"]"))
//									.findElement(
//											By.cssSelector("div[class=\"link-info\"]"));
//							WebElement url = linkInfo.findElement(
//									By.cssSelector("div[class=\"url\"]"))
//									.findElement(By.tagName("input"));
//							String urlTxt = url.getAttribute("value");
//							return !"".equals(urlTxt);
//						} catch (Exception e) {
//
//						}
//					}
//				}
//			});
//			mysleep(4);

			dialogRoot = driver
					.findElement(By.cssSelector("div[id=\"share\"]"));
			dialogBody = dialogRoot.findElement(By
					.cssSelector("div[class=\"dialog-body\"]"));

			WebElement linkInfo = dialogBody
					.findElement(By.cssSelector("ul[class=\"content\"]"))
					.findElement(By.cssSelector("li[class=\"share-link\"]"))
					.findElement(By.cssSelector("div[class=\"link-info\"]"));
			WebElement url = linkInfo.findElement(
					By.cssSelector("div[class=\"url\"]")).findElement(
					By.tagName("input"));
			String urlTxt = url.getAttribute("value");

			WebElement password = linkInfo.findElement(
					By.cssSelector("div[class=\"password\"]")).findElement(
					By.tagName("input"));
			String passwordTxt = password.getAttribute("value");
			links.append(fileName + "\t" + urlTxt + "\t" + passwordTxt).append(
					"\n");

			Actions builder = new Actions(driver);
			builder.moveToElement(closeBtn).click().build().perform();
			//if (i % 8 == 7) {
				mywait2();
				//if (fetchBreakFlag)break;
			//}
		}
    	System.out.println(links);
	}
    
	protected void mywait2() throws IOException {
		while (true) {
			System.out.print("ready for change screnn ? ENTER;N for exit ");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
			}
			if ("n".equalsIgnoreCase(line)) {
				fetchBreakFlag = true;
				break;
			}
		}
	}
    
	protected void mywait1() throws IOException {
		while (true) {
			System.out.print("ready for continue?N for exit ENTER/N");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
			}
			if ("n".equalsIgnoreCase(line)) {
				loopFlag = false;
				break;
			}
		}
	}
    
	protected void mywait() throws IOException {
		while (true) {
			System.out.print("ready for continue? ENTER;N for exit ");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
			}
		}
	}
	protected void mysleep(double second) throws IOException {
		
		long now = System.currentTimeMillis();
		long millis = (long)second * 1000 + now;
		while(true){
			if(millis < System.currentTimeMillis())break;
		}
		
	}

    public  BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
