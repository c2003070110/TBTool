package com.walk_nie.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;

public class NieUtil {

    public static String readLineFromSystemIn(String hint) {
        System.out.println(hint);
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        try {
			while ((line = rd.readLine()) == null) {
			    System.out.println("Please type the valid value again!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return line;
    }

	public static void mySleepBySecond(int i) {
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
		}
	}
	
	public static void appendToFile(File oFile, List<String> outputList) throws IOException {
		String now = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss");
		FileUtils.write(oFile, "-------" + now + "-------\n", Charset.forName("UTF-8"), true);
		for (String str : outputList) {
			FileUtils.write(oFile, str + "\n", Charset.forName("UTF-8"), true);
		}
		oFile = null;
	}

}
