package com.walk_nie.douyin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieUtil;

public class DouYinToWeibo {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new DouYinToWeibo().execute();
	}
	protected void execute() throws Exception {
		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					downloadDouYinVideo();
				}
				if (todoType == 1) {
					mergeVideo();
				}
				if (todoType == 2) {
					publishToWeibo();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	private int choiceTodo() {
		int type = 0;
		try {
			System.out.println("Type of todo : ");
			System.out.println("0:日本抖音视频下载;\n" + "1:合并视频;\n2:发布视频到weibo;\n" + "3:...;\n");

			stdReader = getStdReader();
			while (true) {
				String line = stdReader.readLine();
				if ("0".equals(line.trim())) {
					type = 0;
					break;
				} else if ("1".equals(line.trim())) {
					type = 1;
					break;
				} else if ("2".equals(line.trim())) {
					type = 2;
					break;
				} else if ("3".equals(line.trim())) {
					type = 3;
					break;
				} else {
					System.out.println("Listed number only!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}
	protected void publishToWeibo() throws Exception {
		// TODO Logon weibo
		WebDriver driver = weiboLogon();

		String videoSrcPath = NieUtil
				.readLineFromSystemIn("Pleas input the video path");
		File videoSrcF = new File(videoSrcPath);
		File[] videoFiles = videoSrcF.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return isVideoFile(f);
			}
		});
		for (File f : videoFiles) {
			File descFile = new File(videoSrcF, getFileWithoutExtention(f)
					+ ".txt");
			String despTxt = "";
			if (descFile.exists()) {
				despTxt = FileUtils.readFileToString(descFile, "UTF-8");
			}
			if ("".equals(despTxt)) {
				despTxt = "日本抖音推荐"
						+ formatDate(Calendar.getInstance().getTime(),
								"yyyyMMdd");
			}
			uploadVideoAndPublish(driver, f, despTxt);
		}

	}

	private void uploadVideoAndPublish(WebDriver driver, File f, String despTxt) {
		List<WebElement> elInputs = driver.findElements(By.tagName("input"));
		for (WebElement el : elInputs) {
			if (el.getAttribute("id").startsWith("publisher_upvideo")) {
				el.sendKeys(f.getAbsolutePath());
				break;
			}
		}

//		List<WebElement> elDiv = driver.findElements(By.tagName("div"));
//		for (WebElement el : elDiv) {
//			if (el.getAttribute("id").startsWith("layer_")) {
//				List<WebElement> elA = el.findElements(By.tagName("a"));
//				for (WebElement el1 : elA) {
//					if (el1.getText().equals("知道了")) {
//						el1.click();
//						break;
//					}
//				}
//			}
//		}

		WebElement elMain = driver.findElement(By.id("plc_main"));
		while (true) {
				boolean uploaded = false;
				try {
					List<WebElement> elspans = elMain.findElements(By
							.tagName("dl"));
					for (WebElement el : elspans) {
						String attr = el.getAttribute("node-type");
						if (attr != null && attr.equals("uploading")) {
							String attr1 = el.getAttribute("style");
							if (attr1 != null && attr1.indexOf("block") == -1)
								uploaded = true;
							break;
						}
					}
				} catch (Exception ex) {

				}
				if (uploaded) {
					break;
				}
				NieUtil.mySleepBySecond(3);
		}

		List<WebElement> elInputs1 = elMain.findElements(By.tagName("input"));
		for (WebElement el : elInputs1) {
			String attr = el.getAttribute("action-type");
			if (attr != null && attr.equals("inputTitle")) {
				el.sendKeys(despTxt);
				break;
			}
		}
		List<WebElement> elA = elMain.findElements(By.tagName("a"));
		for (WebElement el : elA) {
			if (el.getText().equals("完成")) {
				el.click();
				break;
			}
		}

		for (WebElement el : elA) {
			String attr = el.getAttribute("title");
			if (attr != null && attr.equals("发布微博按钮")) {
				el.click();
				break;
			}
		}
	}

	private String formatDate(Date date, String format) {
		SimpleDateFormat s = new SimpleDateFormat(format);
		return s.format(date);
	}

	private WebDriver weiboLogon() {

		String rootUrl = "https://www.weibo.com/";
		// WebDriver driver = new ChromeDriver();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		driver.get(rootUrl);

		NieUtil.mySleepBySecond(10);
		
		WebElement elLogin = driver.findElement(By.id("pl_login_form"));
		List<WebElement> elInputs = elLogin.findElements(By.tagName("input"));
		for (WebElement el : elInputs) {
			if ("loginname".equals(el.getAttribute("id"))) {
				el.clear();
				el.sendKeys("nhp12@sina.com");
			}
			if ("password".equals(el.getAttribute("type"))) {
				el.clear();
				el.sendKeys("nhp12345");
			}
		}
		List<WebElement> elas = elLogin.findElements(By.tagName("a"));
		for (WebElement el : elas) {
			if ("登录".equals(el.getText())) {
				el.click();
			}
		}

		NieUtil.mySleepBySecond(2);
		return driver;
	}

	private boolean isVideoFile(File f) {
		String exd = getFileExtention(f);
		if ("mp4".equalsIgnoreCase(exd) || "flv".equalsIgnoreCase(exd)) {
			return true;
		}
		return false;
	}

	private String getFileExtention(File f) {
		String fileName = f.getName();
		int dotPox = fileName.lastIndexOf(".");
		return fileName.substring(dotPox + 1, fileName.length());
	}

	private String getFileWithoutExtention(File f) {
		String fileName = f.getName();
		int dotPox = fileName.lastIndexOf(".");
		return fileName.substring(0, dotPox);
	}

	protected void mergeVideo() throws Exception {
		String videoSrcPath = "";
		File videoSrcF = new File(videoSrcPath);
		File[] files = videoSrcF.listFiles();
		StringBuffer sb = new StringBuffer();
		for (File f : files) {
			sb.append(String.format("file %s", f.getAbsolutePath())).append(
					"\n");
		}
		File filelistF = new File(videoSrcF, "filelist.txt");
		File videoOutputF = new File(videoSrcF, "output.mp4");
		FileUtils.write(filelistF, sb, "UTF-8");
		String cmdMergeVideo = "C:\\ffmpeg-4.0.1-win64-static\\bin\\ffmpeg -f concat ";
		cmdMergeVideo = cmdMergeVideo + " -i " + filelistF.getAbsolutePath();
		cmdMergeVideo = cmdMergeVideo + "  " + videoOutputF.getAbsolutePath()
				+ "";
		executeCommand(cmdMergeVideo);
	}

	protected void downloadDouYinVideo() throws Exception {
		String adbPath = "C:\\android\\platform-tools\\adb ";
		String cmdAdbShare = adbPath + " shell input tap 668 852";
		String cmdAdbDownload = adbPath + " shell input tap 336 935";
		String cmdAdbNext = adbPath + " shell input swipe 550 700 550 100";

		int maxVideoCount = 200;
		for (int i = 0; i < maxVideoCount; i++) {
			// share
			String result = executeCommand(cmdAdbShare);
			// String input =
			// NieUtil.readLineFromSystemIn("want to download ? Y:yes;N:No");
			// if ("Y".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input))
			// {
			// download
			result = executeCommand(cmdAdbDownload);
			System.out.println(result);

			NieUtil.mySleepBySecond(3);
			// }
			// input =
			// NieUtil.readLineFromSystemIn("want to goto next ? Y:yes;N:No");
			// if ("Y".equalsIgnoreCase(input) || "yes".equalsIgnoreCase(input))
			// {
			// next
			result = executeCommand(cmdAdbNext);
			System.out.println(result);
			// }
		}
	}

	protected String executeCommand(String command) throws Exception {
		Process process = Runtime.getRuntime().exec(command);
		process.waitFor();

		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream(), "Shift-JIS"));
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}
	protected BufferedReader stdReader = null;
	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}

}
