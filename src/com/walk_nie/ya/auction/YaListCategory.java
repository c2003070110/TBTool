package com.walk_nie.ya.auction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
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

	private void processMidCategory(String url) throws IOException {
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
			throws IOException {
	
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
	
	// https://auctions.yahoo.co.jp/jp/show/myaucinfo
	/*
	 <div id="modItemNewList">
<table cellpadding="0">
<tbody><tr class="decTr01">
<td class="decTd01">&nbsp;</td>
<td class="decTd02">通知項目：タイトル</td>
<td class="decTd03">日時</td>
</tr>



<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=rats"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v623934653?notice=rats">評価:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 21時 46分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=stldl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v623934653&amp;syid=yiyi2014jp&amp;bid=f162tmwe&amp;oid=60919575-0951046015-9205632&amp;read=stldl">商品受け取り完了:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 21時 45分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=q274616678&amp;type=rats"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/q274616678?notice=rats">評価:土日休も即時発送 PSNカード $50ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 21時 38分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=q274616678&amp;type=stldl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=q274616678&amp;syid=yiyi2014jp&amp;bid=yamato06083&amp;oid=61228703-8850850515-5053533&amp;read=stldl">商品受け取り完了:土日休も即時発送 PSNカード $50ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 21時 38分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=q274616678&amp;type=stl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=q274616678&amp;syid=yiyi2014jp&amp;bid=yamato06083&amp;oid=61228703-8850850515-5053533&amp;read=stl">支払い完了:土日休も即時発送 PSNカード $50ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 19時 52分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=rats"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v624254653?notice=rats">評価:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 46分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=stldl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v624254653&amp;syid=yiyi2014jp&amp;bid=deepair84&amp;oid=60911106-6951069315-8006158&amp;read=stldl">商品受け取り完了:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 46分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=stl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v624254653&amp;syid=yiyi2014jp&amp;bid=deepair84&amp;oid=60911106-6951069315-8006158&amp;read=stl">支払い完了:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 39分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=clsic"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v624254653&amp;syid=yiyi2014jp&amp;bid=deepair84&amp;oid=60911106-6951069315-8006158&amp;read=clsic">お届け先住所確定:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 37分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=autos"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v624254653?notice=autos">即決価格での落札:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 36分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=fbid"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v624254653?notice=fbid">初回入札:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 36分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v624254653&amp;type=clows"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v624254653?notice=clows">終了（落札者あり）:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 13時 36分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=stl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v623934653&amp;syid=yiyi2014jp&amp;bid=f162tmwe&amp;oid=60919575-0951046015-9205632&amp;read=stl">支払い完了:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 11時 32分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=o301697914&amp;type=rats"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/o301697914?notice=rats">評価:★【Amazon（アマゾン）ギフト券50000円　メッセージでのお伝え　送料無料】★</a></p></td>
<td class="decTd05">2019年 2月 25日 09時 11分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=o301697914&amp;type=paydl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/buyer/top?aid=o301697914&amp;syid=drdnw262&amp;bid=yiyi2014jp&amp;oid=61110547-5150931115-1942128&amp;read=paydl">発送連絡:★【Amazon（アマゾン）ギフト券50000円　メッセージでのお伝え　送料無料】★</a></p></td>
<td class="decTd05">2019年 2月 25日 08時 58分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=clsic"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v623934653&amp;syid=yiyi2014jp&amp;bid=f162tmwe&amp;oid=60919575-0951046015-9205632&amp;read=clsic">お届け先住所確定:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 07時 07分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=autos"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v623934653?notice=autos">即決価格での落札:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 07時 06分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=fbid"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v623934653?notice=fbid">初回入札:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 07時 06分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v623934653&amp;type=clows"></span></td>
<td class="decTd06"><p><a href="https://page.auctions.yahoo.co.jp/jp/auction/v623934653?notice=clows">終了（落札者あり）:土日休も即時発送 PSNカード $20ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 07時 06分</td>
</tr>
<tr>
<td class="decTd04"><span class="decTxt01"><input name="aidlist[]" type="checkbox" value="aid=v619946859&amp;type=stldl"></span></td>
<td class="decTd06"><p><a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=v619946859&amp;syid=yiyi2014jp&amp;bid=metal_gear_kingdom_1224&amp;oid=60919710-9351002115-4500165&amp;read=stldl">商品受け取り完了:土日休も即時発送 PSNカード $10ドル 北米版 米国 PlayStationStore PS4 PS3 PSVita</a></p></td>
<td class="decTd05">2019年 2月 25日 04時 03分</td>
</tr>




<tr class="decTr01">
<td class="decTd01">&nbsp;</td>
<td class="decTd02">通知項目：タイトル</td>
<td class="decTd03">日時</td>
</tr>
</tbody></table>
</div>

<div class="acMdMsgForm" id="messageComment">
<div class="libTitleH2TxtVr">
<h2>取引メッセージ</h2>
</div>
<div class="libLeadText">
<p>取引で困ったことなどがあったら、落札者に質問してみよう！</p>
</div>
<div class="decFormErr" id="messageTxt"></div>
<div class="untMsgForm" id="msgForm">
<div id="area1" class="decTxtArea">
<textarea id="textarea" placeholder="メッセージを入力してください" style="overflow: hidden; overflow-wrap: break-word; resize: horizontal; height: 44.0114px;"></textarea>
</div>
<div class="decSmtBtn">
<input type="hidden" id="oid" value="60911106-6951069315-8006158" data-rapid_p="8">
<input type="hidden" id="syid" value="yiyi2014jp" data-rapid_p="9">
<input type="hidden" id="aid" value="v624254653" data-rapid_p="10">
<input type="hidden" id="bid" value="deepair84" data-rapid_p="11">
<input type="hidden" id="crumb" value="jEzDkkdpavg" data-rapid_p="12">
<input id="submitButton" class="libBtnGrayM" type="submit" value="送信する" onclick="YAHOO.JP.auc.order.common.SubmitMessage.execute('/message/submit');" data-ylk="rsec:msg;slk:snd;pos:1" data-rapid_p="13">
</div>
</div>
</div>

	 */
}
