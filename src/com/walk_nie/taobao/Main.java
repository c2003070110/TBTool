package com.walk_nie.taobao;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        String url = "http://f.us.sinaimg.cn/0024Hahelx07tFAfOgFO01041200g4iV0E010.mp4?label=mp4_ld&template=480x360.28.0&Expires=1557549959&ssig=90W1ajRCpD&KID=unistore,video";

		Pattern p = Pattern.compile("\\d+{3,4}x{1}\\d+{3,4}");
		Matcher m = p.matcher(url); 
		String str0 = null;
		while (m.find()) {
			str0 = m.group();
		}
		System.out.println(url);
		System.out.println(str0);
		url = "https://video.twimg.com/ext_tw_video/1126834031234928642/pu/vid/1280x720/Y_Bw-I_xB_Td57NG.mp4?tag=10";
		 p = Pattern.compile("\\d+{3,4}x{1}\\d+{3,4}");
		 m = p.matcher(url); 
		 str0 = null;
		while (m.find()) {
			str0 = m.group();
		}
		System.out.println(url);
		System.out.println(str0);
    }

}
