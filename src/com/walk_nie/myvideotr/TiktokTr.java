package com.walk_nie.myvideotr;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.util.StringUtil;
import org.openqa.selenium.WebDriver;

import com.walk_nie.taobao.util.WebDriverUtil;

public class TiktokTr {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		TiktokTr main = new TiktokTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		 MyVideoObject downloadObj = new MyVideoObject();
		main.downloadVideo(driver,downloadObj);
	}

	public boolean downloadVideo(WebDriver driver, MyVideoObject downloadObj) throws IOException {
		String videoDownloadUrl = MyVideoTrUtil.getVideoDownloadUrlByVblogDownload(driver, downloadObj);
		if (StringUtil.isBlank(videoDownloadUrl)) {
			return false;
		}
		File saveFile = getVideoSaveFile(downloadObj);
		MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);
		return true;
	}
	
	private File getVideoSaveFile(MyVideoObject downloadObj) {
		File outFolder = MyVideoTrUtil.getSaveFolder(downloadObj);
		File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
		return saveFile;
	}
}
