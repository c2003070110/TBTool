package com.walk_nie.taobao.akb48.sousenkyo;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Files;

public class CodeNotifyMain {

	protected BufferedReader stdReader = null;

	public static void main(String[] args) throws IOException {

		CodeNotifyMain main = new CodeNotifyMain();

		main.notifyA();
	}

	public void notifyA() throws IOException {
		File notifyFile = notifyFileReadin();
		List<String> allRecord = Files.readLines(notifyFile,
				Charset.forName("UTF-8"));

		Toolkit kit = Toolkit.getDefaultToolkit();
		Clipboard clip = kit.getSystemClipboard();

		for (String record : allRecord) {
			String[] split = record.split("\t");
			int idx = 0;
			String downUrl = split[idx++];
			String downPassword = split[idx++];
			String taobaoOrderNo = split[idx++];
			String taobaoBuyerName = split[idx++];

			System.out.println("Copy the Taobao Buyer to Clipboard : "
					+ taobaoBuyerName);
			StringSelection ss = new StringSelection(taobaoBuyerName);
			clip.setContents(ss, ss);

			myWait();

			String sendContxt = makeSendContext(downUrl, downPassword,
					taobaoOrderNo, taobaoBuyerName);
			System.out.println("Copy the Taobao send context to Clipboard : "
					+ sendContxt);
			ss = new StringSelection(sendContxt);
			clip.setContents(ss, ss);

			myWait();
		}
	}

	private String makeSendContext(String url, String password,
			String taobaoOrderNo, String taobaoBuyerName) {
		StringBuffer sb = new StringBuffer();
		sb.append("亲 你的票已经准备好了，请前往百度网盘提取。").append("\n");
		sb.append("下载地址：" + url).append("\n");
		sb.append("提取密码：" + password).append("\n");
		sb.append("请及时投票，确认收货，好评。谢谢！");
		return sb.toString();
	}

	protected void myWait() throws IOException {
		while (true) {
			System.out.print("ready for continue? ENTER");
			String line = getStdReader().readLine().trim();
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
			}
		}
	}

	protected File notifyFileReadin() throws IOException {
		while (true) {
			System.out.print("input the notify file:");
			String line = getStdReader().readLine().trim();
			File file = new File(line);
			if (!file.canWrite() || !file.exists() || !file.isFile()) {
				System.out.println("Folder is invalid!!input again.");
				continue;
			}
			return file;
		}
	}

	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
