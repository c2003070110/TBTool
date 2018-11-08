package com.walk_nie.ya.auction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.util.NieUtil;

public class YaListCategory {

	protected BufferedReader stdReader = null;
	private List<String> midCategoryIdList = Lists.newArrayList();
	private List<String> endCategoryIdList = Lists.newArrayList();
	String rootUrl = "https://auctions.yahoo.co.jp/";
	private List<String> scannedCategoryIdList = Lists.newArrayList();
	

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		YaListCategory main = new YaListCategory();
		main.execute();
	}

	public void execute() throws IOException {
		//WebDriver driver = getWebDriver();
		Document doc = urlToDocument(rootUrl);
		File f = new File("./out","scanedCategoryId.txt");
		scannedCategoryIdList = FileUtils.readLines(f, "UTF-8");

		List<String> rootCategoryUrls = getRootCategory(doc);
		for (String url : rootCategoryUrls) {
			processMidCategory(url);
		}
		File outFile = new File("./out", "YaAucCategoryIdList.txt");
		FileUtils.writeLines(outFile, "UTF-8", midCategoryIdList, true);
		outFile = new File("./out", "YaAucCategoryIdList(END).txt");
		FileUtils.writeLines(outFile, "UTF-8", endCategoryIdList, true);
	}

	private void processMidCategory(String url) throws ClientProtocolException, IOException {
		String cateId =getCategoryIdFromUrl(url);
		if(StringUtil.isBlank(cateId)){
			return;
		}
		if(scannedCategoryIdList.contains(cateId)){
			return;
		}
		if(midCategoryIdList.contains(cateId) || endCategoryIdList.contains(cateId) ){
			return;
		}
		System.out.println("processing url :" + url);
		Document doc = urlToDocument(url);
		boolean isEndCategory = isEndCategory(doc);
		if(isEndCategory){
			processEndCategory(url);
			return;
		}
		midCategoryIdList.add(cateId);
		List<String> categoryUrls = getMidCategory(doc);
		for(String url1 : categoryUrls){
			processMidCategory(url1);
		}
	}

	private boolean isEndCategory(Document doc) {
		Element we = null;
		try {
			 we = doc.getElementById("S_Category");
		} catch (Exception ignore) {
		}
		if (we == null) {
			return false;
		}
		Elements wes = null;
		try {
			wes = doc.getElementsByClass("child");
		} catch (Exception ignore) {
		}
		if (wes == null || wes.isEmpty()) {
			return false;
		}
		for (int i = 0; i < wes.size() ; i++) {
			Elements wes1 = null;
			try {
				wes1 = wes.get(i).getElementsByTag("a");
			} catch (Exception ignore) {
			}
			if (wes1 == null || wes1.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void processEndCategory(String  url) {
		String cateId =getCategoryIdFromUrl(url);
		if(!endCategoryIdList.contains(cateId)){
			endCategoryIdList.add(cateId);
		}
	}

	private String getCategoryIdFromUrl(String url) {
		if(url.endsWith("-category.html")){
			int firt = url.lastIndexOf("/");
			int end = url.indexOf("-category.html");
			return url.substring(firt + 1, end);
		}
		if(url.indexOf("category/list") >0){
			int i1 = url.indexOf("?");
			String url1 = url.substring(0, i1);
			if(url1.endsWith("/")){
				url1 = url1.substring(0,url1.length()-1);
			}
			int i2 = url1.lastIndexOf("/");
			return url1.substring(i2 + 1);
		}
		return null;
	}

	private List<String> getMidCategory(Document doc) {
		List<String> categoryUrlList = Lists.newArrayList();
		Elements links = null;
		try{
			links = doc.getElementsByClass("decMainLi");
		}catch(Exception ignore){}
		if (links == null || links.isEmpty()) {
			return categoryUrlList;
		}
		for (int i = 0; i < links.size() ; i++) {
			Elements links1 = links.get(i).getElementsByTag("a");
			for (int j = 0; j < links1.size() ; j++) {
				categoryUrlList.add(links1.get(j).attr("href"));
			}
		}
		return categoryUrlList;
	}

	private List<String> getRootCategory(Document doc) {
		List<String> categoryUrlList = Lists.newArrayList();
		Elements elms = doc.getElementsByClass("acMdCateLinks");
		for (int i = 0; i < elms.size() ; i++) {
			Elements elms1 = elms.get(i).getElementsByTag("a");
			for (int j = 0; j < elms1.size() ; j++) {
				String url = elms1.get(j).attr("href");
				if (!url.endsWith("category.html")) {
					continue;
				}
				if (!categoryUrlList.contains(url)) {
					categoryUrlList.add(url);
				}
			}
		}

		return categoryUrlList;
	}

	protected void mywait() throws IOException {
		while (true) {
			String line = NieUtil.readLineFromSystemIn("ready for continue? ENTER;N for exit ");
			if ("\r\n".equalsIgnoreCase(line) || "\n".equalsIgnoreCase(line)
					|| "".equals(line)) {
				break;
			}
		}
	}

	private  Document urlToDocument(String url)
			throws ClientProtocolException, IOException {

	
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest req = new HttpGet(url);
		HttpResponse res = client.execute(req);
		BufferedReader rd = new BufferedReader(new InputStreamReader(res
				.getEntity().getContent(), "UTF-8"));
		String line = "";
		StringBuffer sbHtml = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			sbHtml.append(line);
			sbHtml.append("\n");
		}
		
		//client.close();
		Document doc = Jsoup.parse(sbHtml.toString());
		// System.out.println("[END]parse URL =" + url);
		return doc;
	}
}
