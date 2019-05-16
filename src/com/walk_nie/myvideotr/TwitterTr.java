package com.walk_nie.myvideotr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class TwitterTr {
	String favlog_url = "https://twitter.com/i/likes";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		TwitterTr main = new TwitterTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		main.scan(driver);
	}

	public List<MyVideoObject> scan(WebDriver driver) {

		List<MyVideoObject> videoObjs = parseTwitter(driver);

		return videoObjs;
	}

	public void removeFromTwitter(WebDriver driver,
			List<MyVideoObject> videoObjs) {
		if (videoObjs.isEmpty())
			return;
		driver.get(favlog_url);
		logonTwitter(driver);
		WebElement rootWe = driver.findElement(By
				.cssSelector("div[id=\"timeline\"]"));
		List<WebElement> eles = rootWe.findElements(By.tagName("li"));
		for (WebElement ele : eles) {
			removeFromTwitter(ele,videoObjs);
		}
	}
	public void removeFromTwitter(WebElement we,
			List<MyVideoObject> videoObjs) {

		String id = we.getAttribute("data-item-id");// tw1127569675770974208

		for (MyVideoObject tw : videoObjs) {
			if (id.equals(tw.trid)) {
				List<WebElement> list = findFavoriteAndUnfavoriteButton(we);
				if(list.get(0) != null){
					try{
					list.get(0).click();
					NieUtil.mySleepBySecond(2);
					}catch(Exception e) {}
				}
				if(list.get(1) != null){
					try{
					list.get(1).click();
					}catch(Exception e) {}
				}
			}
		}
	}

	private List<WebElement> findFavoriteAndUnfavoriteButton(WebElement we) {

		List<WebElement> eles1 = we.findElements(By
				.className("ProfileTweet-action--favorite"));
		WebElement fa = null;
		WebElement unf = null;
		for (WebElement ele1 : eles1) {
			List<WebElement> eles2 = we.findElements(By.tagName("button"));
			for (WebElement ele2 : eles2) {
				String claZZ = ele2.getAttribute("class");
				String describedby = ele2.getAttribute("aria-describedby");
				if (!StringUtil.isBlank(claZZ) && claZZ.indexOf("ProfileTweet-action--unfavorite") != -1) {
					//System.out.println("[unf]" + claZZ);
					unf = ele1;
				}
				if (!StringUtil.isBlank(describedby) && describedby.indexOf("profile-tweet-action-favorite-count") != -1) {
					//System.out.println("[fav]" + describedby);
					fa = ele1;
				}
			}
		}
		List<WebElement> r = Lists.newArrayList();
		r.add(fa);
		r.add(unf);
		return r;
	}
	public void removeFromTwitter(WebElement we,
			MyVideoObject videoObj) {
		List<MyVideoObject> videoObjs = Lists.newArrayList();
		videoObjs.add(videoObj);
		removeFromTwitter(we,videoObjs);
	}

	public void removeFromFav(WebDriver driver, List<MyVideoObject> videoObjs) {
		if (videoObjs.isEmpty())
			return;

		driver.get(favlog_url);
		logonFavolog(driver);

		WebElement rootWe = driver.findElement(By
				.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By
				.cssSelector("div[class=\"tl-tweets\"]"));
		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				List<WebElement> eles2 = ele1.findElements(By.tagName("a"));
				if (eles2.isEmpty())
					continue;
				boolean breakF = false;
				for (WebElement ele2 : eles2) {
					String txt = ele2.getText();
					String onclick = ele2.getAttribute("onclick");
					if (txt.indexOf("削除") == -1)
						continue;
					for (MyVideoObject tw : videoObjs) {
						if (onclick.indexOf(tw.trid) != -1) {
							ele2.click();
							NieUtil.mySleepBySecond(1);
							try {
								driver.switchTo().alert().accept();
								NieUtil.mySleepBySecond(1);
							} catch (Exception e) {
							}
							breakF = true;
							break;
						}
					}
					if (breakF)
						break;
				}
			}
		}
	}

	private List<MyVideoObject> parseTwitter(WebDriver driver) {
		driver.get(favlog_url);
		logonTwitter(driver);

		List<MyVideoObject> twList = Lists.newArrayList();
		WebElement rootWe = driver.findElement(By
				.cssSelector("div[id=\"timeline\"]"));
		List<WebElement> eles = rootWe.findElements(By.tagName("li"));
		for (WebElement ele : eles) {
			String itemType = ele.getAttribute("data-item-type");
			if (StringUtil.isBlank(itemType))
				continue;
			if (!itemType.equals("tweet"))
				continue;
			List<WebElement> eles1 = ele.findElements(By.tagName("div"));
			String permalink = "";
			boolean isVideo = false;
			String id = ele.getAttribute("data-item-id");// tw1127569675770974208
			String uper = "";
			String title = "";
			for (WebElement ele1 : eles1) {
				String clazz = ele1.getAttribute("class");
				if (clazz.indexOf("tweet ") != -1) {
					String permalinkTmp = ele1
							.getAttribute("data-permalink-path");
					if (!StringUtil.isBlank(permalinkTmp)
							&& permalinkTmp.startsWith("/")) {
						permalink = "https://twitter.com" + permalinkTmp;
					}
				}
				if (clazz.indexOf("is-video") != -1) {
					isVideo = true;
				}
			}
			eles1 = ele.findElements(By.tagName("p"));
			for (WebElement ele1 : eles1) {
				String clazz = ele1.getAttribute("class");
				if (clazz.indexOf("TweetTextSize ") != -1) {
					title = ele1.getText();
					break;
				}
			}
			if (isVideo) {
				permalink = permalink + "/video/1";
				title =  "[V]" + title;
			} else {
				permalink = permalink + "/photo/1";
				title =  "[P]" + title;
			}
			eles1 = ele.findElements(By.tagName("strong"));
			for (WebElement ele1 : eles1) {
				String clazz = ele1.getAttribute("class");
				if (clazz.indexOf("fullname ") != -1) {
					uper = ele1.getText();
					break;
				}
			}
			List<WebElement> list = findFavoriteAndUnfavoriteButton(ele);
			try{
				// click favorite button -> exception -> except   
				list.get(0).click();
			}catch(Exception ex){
				continue;
			}
			if (!StringUtil.isBlank(permalink)) {
				MyVideoObject obj = new MyVideoObject();
				obj.trid = id;
				obj.videoUrl = permalink;
				obj.url = permalink;
				obj.uper = uper;
				obj.title = title;
				obj.toType = "toWeibo";
				obj.fromType = "fromTwitter";
				MyVideoTrUtil.insertVideo(obj);
				removeFromTwitter(ele, obj);
				twList.add(obj);
			}
		}
		return twList;
	}

	public List<MyVideoObject> parseFavolog(WebDriver driver) {
		List<MyVideoObject> twList = Lists.newArrayList();
		driver.get(favlog_url);
		logonFavolog(driver);
		try {
			WebElement rootWe = driver.findElement(By
					.cssSelector("div[id=\"main\"]"));
			WebElement el1 = rootWe.findElement(By
					.cssSelector("div[id=\"update\"]"));
			el1.click();
			NieUtil.mySleepBySecond(2);
		} catch (Exception e) {
			return twList;
		}
		WebElement rootWe = driver.findElement(By
				.cssSelector("div[id=\"main\"]"));
		List<WebElement> eles = rootWe.findElements(By
				.cssSelector("div[class=\"tl-tweets\"]"));

		if (eles.isEmpty())
			return twList;

		for (WebElement ele : eles) {
			List<WebElement> eles1 = ele.findElements(By.className("tl-tweet"));
			for (WebElement ele1 : eles1) {
				String id = ele1.getAttribute("id");// tw1127569675770974208
				String href = "";
				String uper = "";
				String title = "";
				if (id.startsWith("tw")) {
					id = id.substring("tw".length());
				}
				List<WebElement> eles2 = ele1.findElements(By
						.className("tl-name"));
				if (eles2.isEmpty())
					continue;
				uper = eles2.get(0).getText();

				eles2 = ele1.findElements(By.className("tl-text"));
				if (eles2.isEmpty())
					continue;
				title = eles2.get(0).getText();
				List<WebElement> eles3 = eles2.get(0).findElements(
						By.tagName("a"));
				if (eles3.isEmpty())
					continue;
				for (WebElement ele3 : eles3) {
					String text = ele3.getText();
					if (text.toLowerCase().startsWith("pic.twitter")) {
						href = ele3.getAttribute("href");
						break;
					}
				}
				if (!StringUtil.isBlank(href)) {
					MyVideoObject obj = new MyVideoObject();
					obj.trid = id;
					obj.videoUrl = href;
					obj.url = href;
					obj.uper = uper;
					obj.title = title;
					obj.toType = "toWeibo";
					obj.fromType = "fromTwitter";
					twList.add(obj);
				}
			}
		}
		return twList;
	}
	private boolean needLogon (WebDriver driver) {
		WebElement rootWe = driver.findElement(By
				.cssSelector("div[id=\"page-container\"]"));
		List<WebElement> eles = rootWe.findElements(By.tagName("input"));
		if(eles.isEmpty()) return false;
		boolean needlogin = false;
		for (WebElement ele : eles) {
			try{
				String name = ele.getAttribute("name");
				if (name.indexOf("password") != -1) {
					needlogin = true;
				}
			}catch(Exception e){
				needlogin = false;
				break;
			}
		}
		return needlogin;
	}

	private void logonTwitter(WebDriver driver) {

		
		if(!needLogon(driver))return;

		WebElement rootWe = driver.findElement(By
				.cssSelector("div[id=\"page-container\"]"));
		List<WebElement> eles = rootWe.findElements(By.tagName("input"));
		for (WebElement ele : eles) {
			String name = ele.getAttribute("name");
			if (name.indexOf("username_or_email") != -1) {
				ele.clear();
				ele.sendKeys(NieConfig
						.getConfig("myvideotr.twitter.user.name"));
			}
			if (name.indexOf("password") != -1) {
				ele.clear();
				ele.sendKeys(NieConfig
						.getConfig("myvideotr.twitter.user.password"));
			}
		}
		eles = rootWe.findElements(By.tagName("button"));
		for (WebElement ele : eles) {
			String name = ele.getAttribute("type");
			if (name.indexOf("submit") != -1) {
				ele.click();
				break;
			}
		}
		NieUtil.mySleepBySecond(3);
		if(needLogon(driver)){
				NieUtil.readLineFromSystemIn("twitter need to login manually!!!login manually and press ANY KEY to continue");
		}

	}

	private void logonFavolog(WebDriver driver) {

		/*
		 * WebElement el1 =
		 * driver.findElement(By.cssSelector("div[id=\"header-r\"]")); String
		 * txt = el1.getText(); if(txt.indexOf("ログイン") == -1){ return ; }
		 * 
		 * List<WebElement> eles = el1.findElements(By.tagName("a")); for
		 * (WebElement ele : eles) { String txt1 = ele.getText(); if
		 * (txt1.indexOf("ログイン") != -1) { ele.click();break; } }
		 * NieUtil.mySleepBySecond(2);
		 * 
		 * el1 = driver.findElement(By.cssSelector("form[id=\"oauth_form\"]"));
		 * 
		 * WebElement el2 =
		 * el1.findElement(By.cssSelector("input[id=\"username_or_email\"]"));
		 * el2.clear();
		 * el2.sendKeys(NieConfig.getConfig("myvideotr.twitter.user.name"));
		 * 
		 * el2 = el1.findElement(By.cssSelector("input[id=\"password\"]"));
		 * el2.clear();
		 * el2.sendKeys(NieConfig.getConfig("myvideotr.twitter.user.password"));
		 * 
		 * el2 = el1.findElement(By.cssSelector("input[id=\"allow\"]"));
		 * el2.click();
		 */
		NieUtil.mySleepBySecond(2);
	}

	public boolean downloadVideo(WebDriver driver, MyVideoObject downloadObj)
			throws IOException {
		//driver.get(downloadObj.url);
		//NieUtil.mySleepBySecond(2);

		File outFolder = MyVideoTrUtil.getSaveFolder(downloadObj);

		String url = downloadObj.url;
		if (url.indexOf("video") != -1) {
			downloadObj.videoUrl = url;
			String videoDownloadUrl = MyVideoTrUtil
					.getVideoDownloadUrlByParsevideo(driver, downloadObj);
			if (StringUtil.isBlank(videoDownloadUrl)) {
				return false;
			}
			File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
			MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);

			return true;
		} else if (url.indexOf("photo") != -1) {
			driver.get(downloadObj.url);
			WebElement rootWe = driver.findElement(By
					.cssSelector("div[id=\"permalink-overlay\"]"));
			List<WebElement> eles = rootWe.findElements(By
					.className("permalink-tweet-container"));
			if (eles.isEmpty())
				return false;
			List<WebElement> eles1 = eles.get(0)
					.findElements(By.tagName("div"));
			if (eles1.isEmpty())
				return false;
			List<String> picUrlList = Lists.newArrayList();

			for (WebElement ele1 : eles1) {
				String dataUrl = ele1.getAttribute("data-image-url");
				if (StringUtil.isBlank(dataUrl)) {
					continue;
				}
				picUrlList.add(dataUrl);
			}
			for (int i = 0; i < picUrlList.size(); i++) {
				String urlpic = picUrlList.get(i);
				try (InputStream in = new URL(urlpic).openStream()) {
					File toFile = new File(outFolder, i + ".jpg");
					Files.copy(in, Paths.get(toFile.getCanonicalPath()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		return false;
	}

	public boolean publish(WebDriver driver, MyVideoObject uploadObj)
			throws IOException {
		return true;
	}
}
