package com.walk_nie.taobao.montBell;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class MontbellPinyinMain {
	private String inFileName = "./montbell/pinyin-in.txt";
	private String outFileName = "./montbell/pinyin-out.txt";

	public static void main(String[] args) throws Exception {
		new MontbellPinyinMain().process();
	}

	public void process() throws Exception {
		/*
		long updateTime = System.currentTimeMillis();
		while (true) {
			File tempFile0 = new File(inFileName);
			if (updateTime < tempFile0.lastModified()) {
				updateTime = tempFile0.lastModified();
				pinyin(tempFile0);
				System.out.println("[waiting for pinyin in ]"
						+ tempFile0.getAbsolutePath());
			}
		}
		*/
		File tempFile0 = new File(inFileName);
		pinyin(tempFile0);
	}

	protected void pinyin(File tempFile0) throws IOException, PinyinException {

		List<String> votes = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		List<String> outputList = Lists.newArrayList();
		for (String line : votes) {
			System.out.println("[processing]" + line);
			String pyStr = PinyinHelper.convertToPinyinString(line, " ",
					PinyinFormat.WITHOUT_TONE);
			String[] spl = pyStr.toLowerCase().split(" ");
			StringBuffer sb = new StringBuffer();
			for (String str : spl) {
				if ("".equals(str))
					continue;
				sb.append(str.substring(0, 1).toUpperCase() + str.substring(1));
			}
			
			StringBuffer nsb = new StringBuffer();
			String[] nsrs = sb.toString().split(",");
			for(String nsr:nsrs){
				nsb.append(nsr).append("\n");
			}
			outputList.add(line);
			outputList.add(nsb.toString());
		}

		// String fileName = String.format("pinyin-%d.txt",
		// System.currentTimeMillis());
		File oFile = new File(outFileName);
		String today = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy-MM-dd");
		FileUtils.write(oFile, "-------" + today + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : outputList) {
			FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		}
		oFile = null;
	}
}
