package com.walk_nie.taobao.akb48.sousenkyo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Files;

public class VerifyStock {

	public static BufferedReader stdReader = null;

	public static void main(String[] args) throws IOException {

		VerifyStock main = new VerifyStock();

		// main.getCandidateList();
		main.vertify();
	}

	protected void vertify() throws IOException {
		

		File agisoStocked = new File("C:\\temp\\akb48\\stock\\卡密使用记录-20170612.txt");
		List<String> agisoOutStockedLines = Files.readLines(agisoStocked,
				Charset.forName("UTF-8"));
		File agisoInUnStocked = new File("C:\\temp\\akb48\\stock\\卡密-20170612.txt");
		List<String> agisoInStockedLines = Files.readLines(agisoInUnStocked,
				Charset.forName("UTF-8"));
		File myF = new File("C:\\temp\\akb48\\stock\\all-20170612.txt");
		List<String> myLines = Files.readLines(myF,
				Charset.forName("UTF-8"));
		
		for(String myLine:myLines){
			String[] myStock = myLine.split("\t");
			String myPanUrl = myStock[4];
			String myPanPswd = myStock[5];
			String myStatus = myStock[9];
			if("".equals(myPanUrl) || "".equals(myPanPswd) || "○".equals(myStatus)){
				//System.out.println("[NOT FOUND]" + myLine);
				continue;
			}
			boolean found = false;
			for(String agisoStockLine:agisoOutStockedLines){
				String[] myAgisoStockLine = agisoStockLine.split("\t");
				int i=1;
				String agisoPanUrl = myAgisoStockLine[i++];
				String agisoPanPswd = myAgisoStockLine[i++];
				//String agisoOrderNo = myAgisoStockLine[i++];
				//String agisoTaobaoId = myAgisoStockLine[i++];
				if(myPanUrl.equals(agisoPanUrl) && myPanPswd.equals(agisoPanPswd)){
					found = true;
					break;
				}
			}
			if(found) continue;
			for(String agisoStockLine:agisoInStockedLines){
				String[] myAgisoLine = agisoStockLine.split("\t");
				int i=0;
				String agisoPanUrl = myAgisoLine[i++];
				String agisoPanPswd = myAgisoLine[i++];
				//String agisoOrderNo = myAgisoLine[i++];
				//String agisoTaobaoId = myAgisoLine[i++];
				if(myPanUrl.equals(agisoPanUrl) && myPanPswd.equals(agisoPanPswd)){
					found = true;
					break;
				}
			}
			if(!found) {
				System.out.println("[NOT FOUND]" + myLine);
			}
		}
	}
 
 
}
