package com.walk_nie.douyin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;

import com.walk_nie.util.NieUtil;

public class DouYinToWeibo {
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new DouYinToWeibo().execute();
	}
	public void execute() throws Exception {
		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					DouYinDownloader douyinDownload = new DouYinDownloader();
					File file = new File("douyin/douyin-in.txt");
					douyinDownload.downloadByFile(file);
				}
				if (todoType == 1) {
					mergeVideo();
				}
				if (todoType == 2) {
					WeiboPublisher publisher = new WeiboPublisher();
					String folder = NieUtil.readLineFromSystemIn("Please input the folder name");
					File file = new File("douyin/" + folder);
					publisher.publish(file);
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
	
	private DouYinToWeibo(){
		
	}
	private static DouYinToWeibo self = null;
	private DouYinDownloader downloader = new DouYinDownloader();
	private WeiboPublisher publisher = new WeiboPublisher();
	public static DouYinToWeibo getInstance() {
		if(self == null){
			self = new DouYinToWeibo();
		}
		return self;
	}
	public void downloadAndPublish(String douyinUrl) throws Exception {

		String outputFile = "douyin/v_%s";
		String outFilePath = String.format(outputFile,
				DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy_MM_dd_HH_mm_ss"));
		File outFolder = new File(outFilePath);
		downloader.downloadByURL(douyinUrl, outFolder);

		publisher.publish(outFolder);
	}

}
