package com.walk_nie.taobao.montBell.main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.seleniumhq.jetty9.util.StringUtil;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class MontbellPinyinMain {
	private long lastestTime = System.currentTimeMillis();

	public static void main(String[] args) throws Exception {
		new MontbellPinyinMain().process();
	}

	public void process() throws Exception {
		String inFileName = NieConfig.getConfig("montbell.pinyin.file.in");
		File tempFile0 = new File(inFileName);
//		System.out.println("[waiting for pinyin in ]"
//				+ tempFile0.getAbsolutePath());
		long updateTime = tempFile0.lastModified();
		if (lastestTime < updateTime) {
			lastestTime = updateTime;
			pinyin(tempFile0);
		}
		
		//File tempFile0 = new File(MontBellUtil.rootPathName, "pinyin-in.txt");
		//pinyin(tempFile0);
	}

	protected void pinyin(File tempFile0) throws IOException, PinyinException {

		List<String> adrs = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		List<String> outputList = Lists.newArrayList();
		for (String line : adrs) {
			System.out.println("[processing]" + line);
			line = line.replaceAll(" ", ",");
			String pyStr = PinyinHelper.convertToPinyinString(line, ":",
					PinyinFormat.WITHOUT_TONE);
			String[] spl = pyStr.toLowerCase().split(":");
			StringBuffer sb = new StringBuffer();
			for (String str : spl) {
				if ("".equals(str))
					continue;
				sb.append(str.substring(0, 1).toUpperCase() + str.substring(1));
			}
			
			StringBuffer nsb = new StringBuffer();
			String[] nsrs = sb.toString().split(",");
			for(String nsr:nsrs){
				if(StringUtil.isBlank(nsr))continue;
				String idxKey = "Sheng";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey))).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
					if(StringUtil.isBlank(nsr))continue;
				}
				idxKey = "Shi";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey)+idxKey.length())).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
					if(StringUtil.isBlank(nsr))continue;
				}
				idxKey = "Qu";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey)+idxKey.length())).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
					if(StringUtil.isBlank(nsr))continue;
				}
				idxKey = "JieDao";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey)+idxKey.length())).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey)+idxKey.length());
					if(StringUtil.isBlank(nsr))continue;
				}
				idxKey = "Zhen";
				if(nsr.indexOf(idxKey) >0 ){
					nsb.append(nsr.substring(0,nsr.indexOf(idxKey))).append("\n");
					nsr = nsr.substring(nsr.indexOf(idxKey) + 1);
					if(StringUtil.isBlank(nsr))continue;
				}
				nsb.append(nsr).append("\n");
			}
			outputList.add(line);
			outputList.add(nsb.toString());
		}

		// String fileName = String.format("pinyin-%d.txt",
		// System.currentTimeMillis());
		File oFile = new File(NieConfig.getConfig("montbell.pinyin.file.out"));
		NieUtil.appendToFile(oFile, outputList);
	}
}
