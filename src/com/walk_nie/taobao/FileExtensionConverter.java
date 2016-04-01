package com.walk_nie.taobao;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class FileExtensionConverter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String path = "C:/Users/niehp/Google ドライブ/git/TbTool/out/earphone_baobei_2016_02_27_00_31_13";
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
		for(File file:files){
			String trueFrom = doReversa?extensionTo:extensionFrom;
			String trueTo = doReversa?extensionFrom:extensionTo;
			String newFileName = file.getName().replace(trueFrom, "") + trueTo;
			File newFile = new File(file.getParent(),newFileName);
			file.renameTo(newFile);
		}
	}

}
