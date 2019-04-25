package com.walk_nie.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.HttpClientBuilder;

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
	

	public static String httpGet(String url, Map<String, String> param) throws UnsupportedOperationException, IOException {

		String p = urlEncodeUTF8(param);
		HttpClient client = HttpClientBuilder.create().build();
		String urlT = url + "?" + p;
		System.out.println(urlT);
		//log("[INFO][httpGet][URL]" + urlT);
		HttpGet request = new HttpGet(urlT);
		//try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			if(statusCode != 200){
				//log("[ERROR][URL]" + request.getRequestLine().getUri() + "[INFO]" + result.toString());
				return "";
			}else{
				return result.toString();
			}
			
		//} catch (Exception ex) {
			//log("[ERROR][URL]" + request.getRequestLine().getUri() + "[INFO]" + ex.getMessage());
		//	ex.printStackTrace();
		//}
		//return "";
	}
	private static String urlEncodeUTF8(Map<?,?> map) {
		if(map == null){
			return "";
		}
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?,?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                urlEncodeUTF8(entry.getKey().toString()),
                urlEncodeUTF8(entry.getValue().toString())
            ));
        }
        return sb.toString();       
    }
	
	private static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	e.printStackTrace();
            return "";
        }
    }
	
	public static String getNowDateTime(){
        //TimeZone tz2 = TimeZone.getTimeZone("Asia/Tokyo");
        //Calendar cal1 = Calendar.getInstance(tz2);
        Date dt = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dt);
		//return DateUtils.formatDate(dt, "yyyy-MM-dd HH:mm:ss");
	}

	public static void log(File logFile,String string) {

		String nowDateTimeStr = NieUtil.getNowDateTime();
		try {
			String str = "[" + nowDateTimeStr + "]" + string + "\n";
			System.out.print(str);
			FileUtils.write(logFile, str, Charset.forName("UTF-8"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(File logFile,Exception ex) {
		try {
			PrintStream ps = new PrintStream(new FileOutputStream(logFile,true));
			ex.printStackTrace(ps);
			ps.flush();
			ps.close();
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
