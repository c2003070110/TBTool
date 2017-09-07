	package com.walk_nie.taobao;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;

import com.walk_nie.taobao.util.TaobaoUtil;

public class FileExtensionConverter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String path = "C:/Users/niehp/git/TBTool/out/montBell_down_baobei_2017_09_07_13_09_53";
		final String extensionFrom = "jpg";
		final String extensionTo = "tbi";
		final boolean doReversa = false;
		File fPath = new File(path);
		if(!fPath.exists() || !fPath.isDirectory()){
			System.out.println("path is not recorrect!!" + fPath.getCanonicalPath());
			return ;
		}
		File[] files = fPath.listFiles( new FileFilter(){
			@Override
			public boolean accept(File file) {
				String trueExt = doReversa?extensionTo:extensionFrom;
				if(file.getName().endsWith(trueExt)){
					return true;
				}
				return false;
			}
		});
		Arrays.sort(files);
		String prevName = "";
		int count = 0;
		for(File file:files){
            String fileName = file.getName();
            int idx = fileName.indexOf(TaobaoUtil.FILE_NAME_SEPERATOR);
            if (idx != -1) {
                String keyName = fileName.substring(0, idx);
                if (keyName.equals(prevName)) {
                    count++;
                    if (count > 4) {
                        continue;
                    }
                } else {
                    count = 0;
                    prevName = keyName;
                }
            }
			String trueFrom = doReversa?extensionTo:extensionFrom;
			String trueTo = doReversa?extensionFrom:extensionTo;
			
			String newFileName = file.getName().replace(trueFrom, "") + trueTo;
			File newFile = new File(file.getParent(),newFileName);
			file.renameTo(newFile);
		}
	}

}
