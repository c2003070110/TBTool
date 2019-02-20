package com.walk_nie.taobao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.walk_nie.util.NieConfig;

import net.arnx.jsonic.JSON;


public class TaobaoSuggestAnor {

	private  String taobaoUrl = "https://suggest.taobao.com/sug?area=c2c&code=utf-8&q=%s";

	public static void main(String[] args) throws 
			IOException {
		TaobaoSuggestAnor anor = new TaobaoSuggestAnor();
		anor.anorForkeyword();
		System.exit(0);
	}
	public void anorForkeyword() throws IOException {
		File keywordFile = new File(NieConfig.getConfig("taobao.suggestAnor.keyword.infile"));
		System.out.println("[waiting for keyword in ]"
				+ keywordFile.getAbsolutePath());
		long updateTime = System.currentTimeMillis();
		while (true) {
			if (updateTime < keywordFile.lastModified()) {
				updateTime = keywordFile.lastModified();
				try{
					doAnor(keywordFile);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				System.out.println("[waiting for keyword in ]"
						+ keywordFile.getAbsolutePath());
			}
		}
	}

	private void doAnor(File keywordFile) throws IOException {
		String outputPath = NieConfig.getConfig("taobao.work.folder") ;
		List<String> keys = Files.readLines(keywordFile, Charset.forName("UTF-8"));
		
		if(keys == null || keys.isEmpty()){
			System.err.println("[anorForkeyword] NONE record in File(" + keywordFile.getAbsolutePath() +")");
			return;
		}

		File outFile = new File(outputPath,"suggestResult.txt");
	    if (!outFile.exists()) {
	    	outFile.getParentFile().mkdirs();
		}
		//String yyyyMMdd = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMdd");
		//String fileNameFmt = "%s-%s.png";
		for (String k : keys) {
			String searchUrl = String.format(taobaoUrl, k.replaceAll(" ", "+"));
			
	        parseURL(searchUrl, outFile);
		}
	}
	public void parseURL(String url,  File outFile) {

		CharSink sink = Files.asCharSink(outFile, Charset.forName("UTF-8"), FileWriteMode.APPEND);
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest req = new HttpGet(url);
		HttpResponse res;
		try {
			res = client.execute(req);
			BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent(), "UTF-8"));
			String line = "";
			StringBuffer sbHtml = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				sbHtml.append(line);
				sbHtml.append("\n");
			}
			Map<String,List<List<String>>> o = new JSON().parse(sbHtml.toString());
			List<List<String>> resultList = o.get("result");
			for(List<String> result:resultList){
				sink.write(result.get(0) + "\n");
				System.out.println(result.get(0));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
