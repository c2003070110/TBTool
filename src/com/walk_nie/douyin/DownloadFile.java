package com.walk_nie.douyin;
 
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
 
/**
 * 根据视频下载链接 下载视频 后缀为 .mp4 等
 * 
 * @author lenovo
 *
 */
public class DownloadFile {
	public  void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		InputStream inputStream = conn.getInputStream();
		byte[] getData = readInputStream(inputStream);
		java.io.File saveDir = new java.io.File(savePath);
		if (!saveDir.exists()) {
			saveDir.mkdir();
		}
		java.io.File file = new java.io.File(saveDir + java.io.File.separator + fileName);
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}
 
	public  byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}
 
 
	/**
	 * 程序入口
	 * @param urlStr
	 */
	public void run(String urlStr) {
		long imageTitile = System.currentTimeMillis();
		String fileName = imageTitile + "." + "mp4";
		String savePath = "G:\\VidioVidioVidioVidioVidioVidio";
		try {
			downLoadFromUrl(urlStr, fileName, savePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}