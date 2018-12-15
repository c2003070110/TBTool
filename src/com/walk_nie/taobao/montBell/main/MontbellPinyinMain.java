package com.walk_nie.taobao.montBell.main;

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
import com.walk_nie.util.NieConfig;

public class MontbellPinyinMain {

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
		File tempFile0 = new File(NieConfig.getConfig("montbell.out.root.folder"),"pinyin-in.txt");
		pinyin(tempFile0);
	}

	protected void pinyin(File tempFile0) throws IOException, PinyinException {

		List<String> adrs = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		List<String> outputList = Lists.newArrayList();
		for (String line : adrs) {
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
				String idxKey = "Sheng";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey))).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
				}
				idxKey = "Shi";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey)+idxKey.length())).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
				}
				idxKey = "Qu";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey)+idxKey.length())).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
				}
				idxKey = "JieDao";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey)+idxKey.length())).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
				}
				idxKey = "Zhen";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey))).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey) + 1);
				}
				nsb.append(nsr).append("\n");
			}
			outputList.add(line);
			outputList.add(nsb.toString());
		}

		// String fileName = String.format("pinyin-%d.txt",
		// System.currentTimeMillis());
		File oFile = new File(NieConfig.getConfig("montbell.out.root.folder"),"pinyin-out.txt");
		String now = DateUtils.formatDate(Calendar.getInstance().getTime(),
				"yyyy_MM_dd_HH_mm_ss");
		FileUtils.write(oFile, "-------" + now + "-------\n",
				Charset.forName("UTF-8"), true);
		for (String str : outputList) {
			FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		}
		oFile = null;
	}
}
