package com.walk_nie.douyin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.walk_nie.util.NieConfig;

public class DouYinUtil {
	
	public static void recordFailureURL(List<String> urls) {
		File file = new File(NieConfig.getConfig("douyin.root.folder") + NieConfig.getConfig("douyin.failure.url.filename"));
		try {
			String fmt = "[FAIL][%s]%s";
			String dt = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy/MM/dd HH:mm:ss");
			List<String> contents = Lists.newArrayList();
			for(String url:urls){
				String content = String.format(fmt, dt, url);
				contents.add(content);
			}
			Files.asCharSink(file, Charset.forName("UTF-8"), FileWriteMode.APPEND).writeLines(contents);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void recordFailureURL(String url) {
		File file = new File(NieConfig.getConfig("douyin.root.folder") + NieConfig.getConfig("douyin.failure.url.filename"));
		try {
			String content = String.format("[FAIL][%s]%s",
					DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy/MM/dd HH:mm:ss"), url);
			Files.asCharSink(file, Charset.forName("UTF-8"), FileWriteMode.APPEND).write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void recordFailureVideo(String videoFilePath) {
		File file = new File(NieConfig.getConfig("douyin.root.folder") + NieConfig.getConfig("douyin.failure.video.filename"));
		try {
			String content = String.format("[FAIL][%s]%s",
					DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy/MM/dd HH:mm:ss"), videoFilePath);
			Files.asCharSink(file, Charset.forName("UTF-8"), FileWriteMode.APPEND).write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
