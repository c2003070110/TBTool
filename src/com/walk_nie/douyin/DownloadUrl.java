package com.walk_nie.douyin;
 
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;
 
/**
 * 根据提供的抖音的ID获取他的所有视频ID
 * 
 * @author lenovo
 *
 */
public class DownloadUrl {
	String URl_Id = null;
	int count = 0;// 计数
	// 根据主页获取每个视频的id
 
	public void DownHtml(String url) throws InterruptedException {
		ArrayList<String> alURl = new ArrayList<String>();//视频id集合
		ArrayList<String> alMP4 = new ArrayList<String>();//视频下载URL集合
		// 实例化一个浏览器对象
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(url);
		// 休眠等待页面加载
		NieUtil.mySleepBySecond(8);
		List<WebElement> elements = driver.findElements(By.cssSelector("li.item,goWork"));// 获取到每个视频的模块
		System.out.println(elements.size());
		//获取每个URl的ID
		for (WebElement we : elements) {
			String ids = we.getAttribute("data-id").toString();// 获取模块的data-id的属性值
			alURl.add("https://www.iesdouyin.com/share/video/" + ids);
		}
		driver.get("http://douyin.iiilab.com/");// 打开可以将每个视频链接转化成可以下载的链接的网页
		NieUtil.mySleepBySecond(8);// 休眠等待页面加载
		//获取可以下载的url
		for (int i = 0; i < alURl.size(); i++) {
			driver.findElement(By.cssSelector("input.form-control.link-input")).clear();// 清空这个输入框
			driver.findElement(By.cssSelector("input.form-control.link-input")).sendKeys(alURl.get(i));// 将需要转换的链接放入该输入框中
			driver.findElement(By.cssSelector("button.btn.btn-default")).click();// 点击解析
			NieUtil.mySleepBySecond(4);// 休眠等待页面加载
			alMP4.add( driver.findElement(By.cssSelector("a.btn.btn-success")).getAttribute("href").toString());// 获取解析后的链接
		}
		driver.close();
		//下载
		for (int i = 0; i < alMP4.size(); i++) {
			DownloadFile df = new DownloadFile();
			df.run(alMP4.get(i));
		}
	}
 
 
	/**
	 * 入口
	 * 
	 * @param id
	 */
	public static void main(String[] args) {
		DownloadUrl dl = new DownloadUrl();
		String ID = "80602533314";// 人物ID
		try {
			dl.DownHtml("https://www.douyin.com/share/user/" + ID + "/?share_type=link");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
 
 
 
}