package com.walk_nie.mercari;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.http.client.utils.DateUtils;
import org.jsoup.helper.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;

public class MercariScanNotify {

	protected BufferedReader stdReader = null;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		MercariScanNotify main = new MercariScanNotify();
		main.execute(driver);
		driver.close();
	}

	public void execute(WebDriver driver) throws IOException {
		List<MercariObject> objList = scan(driver);
		notify(objList);
	}

	private List<MercariObject> scan(WebDriver driver) {
		List<MercariObject> objList = parseSumPage(driver);
		parseDetail(driver, objList);
		return objList;
	}

	private List<MercariObject> parseSumPage(WebDriver driver) {
		// URL = keyword + saling + sort
		String url = "https://www.mercari.com/jp/search/?sort_order=created_desc&status_on_sale=1&keyword=";
		url += NieConfig.getConfig("mercari.notify.keyword");
		driver.get(url);
		List<MercariObject> objList = Lists.newArrayList();
		List<WebElement> boxs = driver.findElements(By.cssSelector("section[class=\"items-box\"]"));
		for (WebElement box : boxs) {
			MercariObject obj = new MercariObject();
			WebElement we = box.findElement(By.tagName("a"));
			String href = we.getAttribute("href");
			if (href.endsWith("/")) {
				href = href.substring(0, href.length() - 1);
			}
			String id = href.substring(href.lastIndexOf("/") + 1);
			obj.id = id;
			objList.add(obj);
		}
		return objList;
	}

	private void parseDetail(WebDriver driver, List<MercariObject> objList) {
		String urlPrefix = "https://item.mercari.com/jp/";
		for (MercariObject obj : objList) {
			String detailUrlPage = urlPrefix + obj.id;
			driver.get(detailUrlPage);
			WebElement box = driver.findElement(By.cssSelector("section[class=\"item-box-container\"]"));
			obj.title = box.findElement(By.cssSelector("h1[class=\"item-name\"]")).getText();
			WebElement priceBox = box.findElement(By.cssSelector("div[class=\"item-price-box\"]"));
			obj.price = toPrice(priceBox.findElement(By.cssSelector("span[class=\"item-price\"]")).getText());
			obj.shippingFee = priceBox.findElement(By.cssSelector("span[class=\"item-shipping-fee\"]")).getText();

			WebElement detailTable = box.findElement(By.cssSelector("table[class=\"item-detail-table\"]"));
			List<WebElement> trList = detailTable.findElements(By.tagName("tr"));
			for (WebElement tr : trList) {
				WebElement th = tr.findElement(By.tagName("th"));
				WebElement td = tr.findElement(By.tagName("td"));
				if ("出品者".equals(th.getText())) {
					obj.publisher = td.findElement(By.tagName("a")).getText();
					List<WebElement> wes = detailTable.findElements(By.cssSelector("div[class=\"item-user-ratings\"]"));
					obj.publisherRateGood = wes.get(0).getText();
					obj.publisherRateBads = wes.get(2).getText();
				}
				if ("配送元地域".equals(th.getText())) {
					obj.publisherPlace = td.findElement(By.tagName("a")).getText();
				}
			}
			WebElement desp = box.findElement(By.cssSelector("div[class=\"item-description\"]"));
			obj.desp = desp.getText().trim();

			List<WebElement> msgLiList = box.findElement(By.cssSelector("ul[class=\"message-items\"]"))
					.findElements(By.tagName("li"));

			for (WebElement li : msgLiList) {
				String msgUser = li.findElement(By.cssSelector("a[class=\"message-user\"]")).getText();
				String msgTxt = li.findElement(By.cssSelector("div[class=\"message-body-text\"]")).getText();
				String msgTime = li.findElement(By.tagName("time")).getText();
				obj.commentList.add(msgUser + ":" + msgTime + ":" + msgTxt);
			}
		}
	}

	private String toPrice(String text) {
		if (StringUtil.isBlank(text)) {
			return "";
		}
		text = text.replace("¥", "");
		text = text.replace(",", "");
		return text.trim();
	}

	private void notify(List<MercariObject> objList) {
		String to = NieConfig.getConfig("mercari.notify.email.to");
		String from = NieConfig.getConfig("mercari.notify.email.from");
		final String username = NieConfig.getConfig("mercari.notify.mail.user.id");
		final String password = NieConfig.getConfig("mercari.notify.mail.user.password");

		String charset = "UTF-8";
		String encoding = "base64";

		Properties props = new Properties();
		props.put("mail.smtp.host", NieConfig.getConfig("mercari.notify.mail.smtp.host"));
		props.put("mail.smtp.port", NieConfig.getConfig("mercari.notify.mail.smtp.port"));
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", true);
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.debug", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setHeader("Content-Transfer-Encoding", encoding);
			message.setFrom(new InternetAddress(from));
			message.setReplyTo(new Address[] { new InternetAddress(from) });
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			String dt = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy/MM/dd HH:mm");
			String subject = "[MERCARI]" + "[" + NieConfig.getConfig("mercari.notify.keyword") + "]" + dt;
			message.setSubject(subject, charset);
			
			message.setText(composeMailContent(objList), charset);

			Transport.send(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String composeMailContent(List<MercariObject> objList) {
		StringBuffer sb = new StringBuffer();
		for(MercariObject obj:objList){
			sb.append("[BID]<a  href=\"https://item.mercari.com/jp/" + obj.id+"\">GOTO BID</a>").append("\n");
			sb.append("[TITLE]" + obj.title).append("\n");
			sb.append("[price]" + obj.price+"(" +obj.shippingFee +")").append("\n");
			sb.append("[SELL]" + obj.publisher+"(" +obj.publisherRateGood +":"+obj.publisherRateBads +")").append("\n");
			sb.append("[DESP]" + obj.desp).append("\n");
			if (!obj.commentList.isEmpty()) {
				sb.append("[COMMENT]START").append("\n");
				for (String line : obj.commentList) {
					sb.append("[COMMENT]" + line).append("\n");
				}
				sb.append("[COMMENT]END--").append("\n");
			}
			sb.append("[BID]<a  href=\"https://item.mercari.com/jp/" + obj.id+"\">GOTO BID</a>").append("\n");
			sb.append("[------------------------]").append("\n");
		}
		return sb.toString();
	}
}
