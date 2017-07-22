package com.walk_nie.ya.auction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;

public class YaAutoReview {

	protected BufferedReader stdReader = null;

	public void execute(WebDriver driver) throws IOException {
		String aucUrlPrefix = "https://page.auctions.yahoo.co.jp/jp/auction/";
		List<String> yaAucIds = getYahooAuctionIds();
		for (String yaAucId : yaAucIds) {
			String aucUrl = aucUrlPrefix + yaAucId;
			driver.get(aucUrl);
			System.out.println("[INFO]ヤフーオクID＝" + yaAucId);
			// 取引ナビ
			if (driver.findElements(By.id("modTradingNaviStep")).isEmpty()) {
				System.out.println("[INFO]取引ナビ なし");
				continue;
			}
			WebElement el1 = driver.findElements(By.id("modTradingNaviStep"))
					.get(0);
			el1.findElement(By.className("libBtnBlueL")).click();
			// 受け取り連絡をする
			// 出品者を評価する
			List<WebElement> we = driver.findElements(By
					.className("acMdTradeBtn"));
			if (we.isEmpty()) {
				System.out.println("[INFO]「受け取り連絡」または「出品者を評価」なし");
				continue;
			}
			if (we.get(0).getText().equals("受け取り連絡をする")) {
				// receive
				receive(driver, we.get(0), yaAucId);
				continue;
			}
			if (we.get(0).getText().equals("出品者を評価する")) {
				// review
				review(driver, we.get(0), yaAucId);
				continue;
			}
		}
	}

	private void receive(WebDriver driver, WebElement we, String yaAucId) {
		if (we.findElements(By.tagName("a")).isEmpty()) {
			System.out.println("[INFO][受け取はできません。]ヤフーオクID＝" + yaAucId);
			return;
		}
		we.findElement(By.tagName("a")).click();
		driver.findElement(By.id("mBoxPay"))
				.findElement(By.cssSelector("input[class=\"libBtnRedL\"]"))
				.click();
		WebElement we2 = driver.findElements(By.className("acMdTradeBtn")).get(
				0);
		System.out.println("[INFO][受け取は完了しました。]ヤフーオクID＝" + yaAucId);
		// review
		review(driver, we2, yaAucId);

	}

	private void review(WebDriver driver, WebElement we, String yaAucId) {
		we.findElement(By.tagName("a")).click();
		List<WebElement> wes = driver.findElements(By.tagName("b"));
		boolean hasReviewed = false;
		for (WebElement weB : wes) {
			if ("前回、評価した内容".equals(weB.getText())) {
				hasReviewed = true;
				break;
			}
		}
		if (hasReviewed) {
			System.out.println("[INFO][評価すでに完了しています。]ヤフーオクID＝" + yaAucId);
			return;
		}
		driver.findElement(By.id("commonTextIn")).click();
		driver.findElement(By.id("decCheck")).click();
		driver.findElement(By.cssSelector("input[class=\"libBtnBlueL\"]"))
				.click();
		System.out.println("[INFO][評価は完了しました。]ヤフーオクID＝" + yaAucId);
	}

	protected List<String> getYahooAuctionIds() throws IOException {

		System.out.print("input yahoo auction id ? ");
		String str = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		List<String> ids = Lists.newArrayList();
		while ((str = in.readLine()) != null && str.length() != 0) {
			ids.add(str);
		}
		// while(true){
		// Scanner stdin1 = new Scanner(new BufferedInputStream(System.in));
		// while(stdin1.hasNext()){
		// String line = stdin1.next();
		// if("END".equalsIgnoreCase(line))break;
		// ids.add(line);
		// }
		// stdin1.close();
		// }
		// String[] spl = str.split("\r\n");
		// return Arrays.asList(spl);
		return ids;
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
