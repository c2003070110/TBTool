package com.walk_nie.taobao.montBell;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import com.google.common.io.Files;


public class MontbellPinyinMain {
	private String voteFile = "./in/pinyin.txt";


	public static void main(String[] args) throws Exception {
		new MontbellPinyinMain().process();
	}

	public void process() throws Exception {
		long updateTime = System.currentTimeMillis();
		while (true) {
			File tempFile0 = new File(voteFile);
			if (updateTime < tempFile0.lastModified()) {
				updateTime = tempFile0.lastModified();
				pinyin(tempFile0);
				System.out.println("[waiting for pinyin in ]" + tempFile0.getAbsolutePath());
			}
		}
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
			for(String str:spl){
				if("".equals(str))continue;
				sb.append(str.substring(0, 1).toUpperCase() + str.substring(1));
			}
			outputList.add(line);
			outputList.add(sb.toString());
		}

		String fileName = String.format("pinyin-%d.txt", System.currentTimeMillis());
		File outFile = new File("./out/", fileName);
		for(String str :outputList){
			FileUtils.write(outFile, str +"\n", Charset.forName("UTF-8"),true);
		}
		outFile = null;
	}
}
