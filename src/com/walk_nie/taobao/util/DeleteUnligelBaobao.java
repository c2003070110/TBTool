package com.walk_nie.taobao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DeleteUnligelBaobao {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		DeleteUnligelBaobao main = new DeleteUnligelBaobao();
		main.execute();

	}

	public void execute() throws IOException {
		WebDriver driver = logon();
		mywait();

		while (true) {

			try {

				List<WebElement> trs = driver
						.findElement(By.cssSelector("div.ant-tabs-content"))
						.findElement(By.cssSelector("div.ant-table-body"))
						.findElements(By.cssSelector("tr.ant-table-row"));
				for (WebElement tr : trs) {
					List<WebElement> as = tr.findElements(By.tagName("a"));
					for (WebElement a : as) {
						if ("查看详情".equals(a.getText())) {
							a.click();
							break;
						}
					}
				}
				List<WebElement> delAs = driver
						.findElement(By.cssSelector("div.ant-tabs-content"))
						.findElement(By.cssSelector("div.ant-table-body"))
						.findElements(By.cssSelector("tr.ant-table-row"));
				for (WebElement tr : delAs) {
					List<WebElement> as = tr.findElements(By.tagName("a"));
					for (WebElement a : as) {
						if ("删除商品".equals(a.getText())) {
							a.click();
							mywait();
						
						}
					}

				}
				mywait();
			} catch (Exception ex) {
				ex.printStackTrace();
				mywait();
			}
		}

	}

	private WebDriver logon() {

		String rootUrl = "https://healthcenter.taobao.com/home/punish_history.htm?spm=5144.7994333.0.0.5efed55w269fU";
		
		// WebDriver driver = new ChromeDriver();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(rootUrl);

		return driver;
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

	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
