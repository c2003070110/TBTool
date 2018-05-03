package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.arnx.jsonic.JSON;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class TaobaoPictureMain {

	protected BufferedReader stdReader = null;
	private String srcFile = "akb48/taobaoPictureSrc.txt";
	private String outFileName = "./akb48/ticketPictureUrl.txt";

	public static void main(String[] args) throws IOException {

		TaobaoPictureMain main = new TaobaoPictureMain();

		main.demon();
	}

	public void demon() throws IOException {
		System.out
				.println("[INFO] Parsing the picture url from taobao picture box!");
		System.out.println("[INFO] please put the html to the File(UTF-8) "
				+ new File(srcFile).getAbsolutePath());
		long updateTime = System.currentTimeMillis();
		String updateDateTimeStr = DateUtils.formatDate(new Date(updateTime),
				"yyyy-MM-dd HH:mm:ss");
		while (true) {
			File tempFile0 = new File(srcFile);
			long lastModified = tempFile0.lastModified();
			String lastModifiedStr = DateUtils.formatDate(
					new Date(lastModified), "yyyy-MM-dd HH:mm:ss");
			if (lastModifiedStr.compareTo(updateDateTimeStr) > 0) {
				updateTime = lastModified;
				updateDateTimeStr = DateUtils.formatDate(
						new Date(lastModified), "yyyy-MM-dd HH:mm:ss");
				generate();

				System.out
						.println("[INFO] please put the html to the File(UTF-8) "
								+ tempFile0.getAbsolutePath());
			}
		}
	}

	public void generate() throws IOException {

		File tempFile0 = new File(srcFile);
		System.out.println("[INFO] Parsing! File TimeStamp is "
				+ DateUtils.formatDate(new Date(tempFile0.lastModified()),
						"yyyy-MM-dd HH:mm:ss"));
		List<String> lines = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		StringBuffer sb = new StringBuffer();
		for (String line : lines) {
			sb.append(line);
		}
		String content = sb.toString();
		if (content == null || "".equals(content)) {
			System.err.println("[ERROR] The File is Empty! "
					+ tempFile0.getAbsolutePath());
			return;
		}
		Document doc = Jsoup.parse(content);
		Elements eles = doc.select("div.list-items-table");
		if (eles == null || eles.isEmpty()) {
			System.err.println("[ERROR] NOT validate HTML");
			return;
		}
		List<String> outList = Lists.newArrayList();
		String outputFmt = "%03d\t%s\t%s\t%s";
		int i = 1;
		for (Element ele : eles) {
			String dataPicJson = ele.attr("data-picture");
			if (dataPicJson == null || "".equals(dataPicJson)) {
				System.err.println("[ERROR] NOT validate Picture "
						+ ele.toString());
				continue;
			}
			Map<String, String> dataPicMap = JSON.decode(dataPicJson);
			// String pictureId = dataPicMap.get("pictureId");
			// String pictureCategoryId = dataPicMap.get("pictureCategoryId");
			// String picturePath = dataPicMap.get("picturePath");
			// String sellerId = dataPicMap.get("sellerId");
			String pictureCategoryName = dataPicMap.get("pictureCategoryName");
			String name = dataPicMap.get("name");
			String fullUrl = dataPicMap.get("fullUrl");
			if (name == null || "".equals(name)) {
				System.err.println("[ERROR] NOT validate data-picture "
						+ dataPicJson);
				continue;
			}
			outList.add(String.format(outputFmt, i++, pictureCategoryName,
					name, fullUrl));
		}
		Collections.sort(outList);
		File oFile = new File(outFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd HH:mm:ss");
		outList.add(0, "-------" + today + "-------");
		FileUtils.writeLines(oFile, "UTF-8", outList, "\n", true);
		oFile = null;
		System.out.println("[INFO] Parsing Finish! Count of Picture URL is  "
				+ outList.size());
	}
}
