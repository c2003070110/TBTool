package com.walk_nie.mercari;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.beust.jcommander.internal.Lists;
import com.google.common.io.Files;
import com.walk_nie.util.NieUtil;

public class MercariAutoPublish {
	private String inFileName = "./mercari/publish-in.txt";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

		WebDriver driver = MercariUtil.logon();
		MercariAutoPublish main = new MercariAutoPublish();
		main.execute(driver);
	}

	public void execute(WebDriver driver) throws IOException, InterruptedException {

		File tempFile0 = new File(inFileName);
		List<String> lines = Files.readLines(tempFile0,
				Charset.forName("UTF-8"));
		int idx = 0;
		String publishCount = lines.get(idx++);
		String itemName = lines.get(idx++);
		String itemDesp = lines.get(idx++);
		String itemPrice = lines.get(idx++);

		List<String> pictures = Lists.newArrayList();
		
		String pic = lines.get(idx++);
		if(pic!=null && !"".equals(pic)){
			pictures.add(pic);
		}
		pic = lines.get(idx++);
		if(pic!=null && !"".equals(pic)){
			pictures.add(pic);
		}pic = lines.get(idx++);
		if(pic!=null && !"".equals(pic)){
			pictures.add(pic);
		}
		boolean reslt = false;
		String hint ="Execute is OK? ENTER For continue.N for Exit!\n";
		hint+=" [publish Time] "+ publishCount +"\n";
		hint+=" [Item Title] "+ itemName +"\n";
		hint+=" [Item Desp] "+ itemDesp +"\n";
		hint+=" [Item Price] "+ itemPrice +"\n";
		while (true) {
			System.out.print(hint);
			String line;
			try {
				line = MercariUtil.getStdReader().readLine().trim();
				if ("\r\n".equalsIgnoreCase(line)
						|| "\n".equalsIgnoreCase(line) || "".equals(line)) {
					reslt =  true;
					break;
				}else if ("n".equalsIgnoreCase(line)){
					reslt =  false;
					break;
				}
			} catch (IOException e) {
			}
		}
		if(!reslt){
			return;
		}
		/*
		String itemName = "送料無料 iPhone7 用 ケース No312";// 商品名
		// 商品の説明
		String itemDesp = "箱はありません、割安出品しております。OPP袋に入れて発送する予定です。\n"
				+ "ご購入は、コメント不要です。\n"
		        + "即購入は歓迎です。\n";// 商品の説明
		String itemPrice = "666";// 商品の説明
		
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo311-0.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo311-1.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo311-4.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo312-0.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo312-1.jpg");
		pictures.add("C:\\Users\\niehp\\Google ドライブ\\YaAuc\\iphonecase\\te\\CaseNo312-4.jpg");
		*/
		for (int i = 0; i < Integer.parseInt(publishCount); i++) {
			publish(driver, itemName, itemDesp, itemPrice, pictures);
		}

	}
	private void publish(WebDriver driver,String itemName,String itemDesp,String itemPrice,List<String> pictures) throws InterruptedException{
		driver.get("https://www.mercari.com/jp/sell/");
		WebElement containerWe = driver.findElement(By.id("sell-container"));
		
		List<WebElement> weList = containerWe.findElements(By.tagName("input"));
		WebElement selWe = null;
		for (WebElement we : weList) {
			if ("image1".equals(we.getAttribute("name"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [PICTURE] Element!");
			MercariUtil.mywait("[ERROR]Picture Upload ERROR!upload manually !ENTER when upload finish!");
		}else{
			selWe.sendKeys(pictures.get(0));
			NieUtil.mySleepBySecond(5);
			selWe.sendKeys(pictures.get(1));
			NieUtil.mySleepBySecond(5);
			selWe.sendKeys(pictures.get(2));
		}
		
//		List<WebElement> weList1 = containerWe.findElements(By.tagName("pre"));
//		WebElement selWe1 = null;
//		for (WebElement we : weList1) {
//			String txt = we.getText();
//			txt = txt.replaceAll("\n", "");
//			if ("ドラッグアンドドロップまたはクリックしてファイルをアップロード".equals(txt)) {
//				selWe1 = we;
//			}
//		}
//		if(selWe1 == null){
//			System.err.println("[ERROR]NOT FOUND [PICTURE] Element!");
//			MercariUtil.mywait("[ERROR]Picture Upload ERROR!upload manually !ENTER when upload finish!");
//		}
//		selWe1.click();
//		driver.switchTo().window("File Upload");
//		WebElement el=driver.findElement(By.name("fileName"));
//		el.sendKeys("C:\\temp\\test\\IMG_6519.PNG");
		
		
		// 商品名
		weList = containerWe.findElements(By.tagName("input"));
		selWe = null;
		for (WebElement we : weList) {
			if ("商品名（必須 40文字まで)".equalsIgnoreCase(we.getAttribute("placeholder"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [商品名] Element!");
			return;
		}
		selWe.sendKeys(itemName);
		// 商品の説明
		selWe = null;
		weList = containerWe.findElements(By.tagName("textarea"));
		for (WebElement we : weList) {
			if ("商品の説明（必須 1,000文字以内）（色、素材、重さ、定価、注意点など）例）2010年頃に1万円で購入したジャケットです。ライトグレーで傷はありません。あわせやすいのでおすすめです。".equalsIgnoreCase(we.getAttribute("placeholder"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [商品の説明] Element!");
			return;
		}
		selWe.sendKeys(itemDesp);
		
		// 商品の詳細
		// カテゴリー
		selWe = null;
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("レディース".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [カテゴリー1] Element!");
			return;
		}
		Select select = new Select(selWe);
		select.selectByValue("7");
		
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("スマートフォン/携帯電話".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [カテゴリー2] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("862");
		
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("Android用ケース".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [カテゴリー3] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("1208");
		
		//商品の状態
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("新品、未使用".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [商品の状態] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("1");
		
		//配送料の負担
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("送料込み(出品者負担)".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [配送料の負担] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("2");
		
		//配送の方法
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("未定".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [配送の方法] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("9");

		//発送元の地域
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("北海道".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [発送元の地域] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("13");
		
		//発送までの日数
		weList = driver.findElements(By.tagName("select"));
		for (WebElement we : weList) {
			Select dropDown = new Select(we);   
			WebElement dfd = dropDown.getOptions().get(1);
			if ("1~2日で発送".equalsIgnoreCase(dfd.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [発送までの日数] Element!");
			return;
		}
		select = new Select(selWe);
		select.selectByValue("2");
		
		//    価格
		weList = containerWe.findElements(By.tagName("input"));
		selWe = null;
		for (WebElement we : weList) {
			if ("例）300".equalsIgnoreCase(we.getAttribute("placeholder"))) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [価格] Element!");
			return;
		}
		selWe.sendKeys(itemPrice);

		//出品する
		weList = containerWe.findElements(By.tagName("button"));
		selWe = null;
		for (WebElement we : weList) {
			if ("出品する".equalsIgnoreCase(we.getText())) {
				selWe = we;
			}
		}
		if(selWe == null){
			System.err.println("[ERROR]NOT FOUND [出品Button] Element!");
			return;
		}
		selWe.click();
	}
	/*
	 url = https://www.mercari.com/jp/search/?sort_order=created_desc&keyword=akb48&category_root=&brand_name=&brand_id=&size_group=&price_min=&price_max=&status_on_sale=1
	 class="items-box-content clearfix"
	 <section class="items-box">
          <a href="https://item.mercari.com/jp/m48008252039/">
            <figure class="items-box-photo">
              <img data-src="https://static-mercari-jp-imgtr2.akamaized.net/thumb/photos/m48008252039_1.jpg?1541684983" class="lazyloaded is-higher-height" alt="AKB48 クリアファイル" src="https://static-mercari-jp-imgtr2.akamaized.net/thumb/photos/m48008252039_1.jpg?1541684983">
                          </figure>
            <div class="items-box-body">
              <h3 class="items-box-name font-2">AKB48 クリアファイル</h3>
              <div class="items-box-num clearfix">
                <div class="items-box-price font-5">¥ 300</div>
                                                  <p class="item-box-tax">(税込)</p>
                              </div>
            </div>
          </a>
        </section>
        ----detailpage-------https://item.mercari.com/jp/m49934451444/
        <section class="item-box-container l-single-container">
  <h1 class="item-name">ルイヴィトン ダミエ 長財布</h1>
  
  <div class="item-main-content clearfix">
    <div class="item-photo">
      <div class="owl-carousel owl-loaded owl-drag">
                              
                                        
                                        
                                        
                        <div class="owl-stage-outer"><div class="owl-stage" style="left: -300px; width: 1200px;"><div class="owl-item" style="width: 300px;"><div class="owl-item-inner is-higher-width">
              <img data-src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_1.jpg?1541429601" alt="ルイヴィトン ダミエ 長財布" class="owl-lazy" src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_1.jpg?1541429601" style="opacity: 1;">
            </div></div><div class="owl-item active" style="width: 300px;"><div class="owl-item-inner is-higher-height">
              <img data-src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_2.jpg?1541429601" alt="ルイヴィトン ダミエ 長財布" class="owl-lazy" src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_2.jpg?1541429601" style="opacity: 1;">
            </div></div><div class="owl-item" style="width: 300px;"><div class="owl-item-inner is-higher-height">
              <img data-src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_3.jpg?1541429601" alt="ルイヴィトン ダミエ 長財布" class="owl-lazy" src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_3.jpg?1541429601" style="opacity: 1;">
            </div></div><div class="owl-item" style="width: 300px;"><div class="owl-item-inner is-higher-height">
              <img data-src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_4.jpg?1541429601" alt="ルイヴィトン ダミエ 長財布" class="owl-lazy" src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_4.jpg?1541429601" style="opacity: 1;">
            </div></div></div></div><div class="owl-nav disabled"><div class="owl-prev">prev</div><div class="owl-next">next</div></div><div class="owl-dots"><div class="owl-dot"><span></span>
            <div class="owl-dot-inner">
              <img src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_1.jpg?1541429601" class="is-higher-width">
          </div></div><div class="owl-dot active"><span></span>
            <div class="owl-dot-inner">
              <img src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_2.jpg?1541429601" class="is-higher-height">
          </div></div><div class="owl-dot"><span></span>
            <div class="owl-dot-inner">
              <img src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_3.jpg?1541429601" class="is-higher-height">
          </div></div><div class="owl-dot"><span></span>
            <div class="owl-dot-inner">
              <img src="https://static-mercari-jp-imgtr2.akamaized.net/item/detail/orig/photos/m49934451444_4.jpg?1541429601" class="is-higher-height">
          </div></div></div></div>
              <div class="item-sold-out-badge"><div>SOLD</div></div>
          </div>

          <div class="item-price-box text-center visible-sp">
        <span class="item-price bold">¥ 19,000</span>
        <span class="item-tax"> (税込)</span>
                  <span class="item-shipping-fee">送料込み</span>
              </div>
            <section class="visible-sp">
                                                        <div class="item-buy-btn disabled f18-24">売り切れました</div>
            
                                  </section>
        <table class="item-detail-table">
      <tbody><tr>
        <th>出品者</th>
        <td>
          <a href="https://www.mercari.com/jp/u/577215247/">バナナ</a>
          <div>
                          <div class="item-user-ratings">
                <i class="icon-good"></i>
                <span>36</span>
              </div>
                          <div class="item-user-ratings">
                <i class="icon-normal"></i>
                <span>0</span>
              </div>
                          <div class="item-user-ratings">
                <i class="icon-bad"></i>
                <span>0</span>
              </div>
                      </div>
        </td>
      </tr>
      <tr>
        <th>カテゴリー</th>
        <td>
                    <a href="https://www.mercari.com/jp/category/1/"><div>レディース</div></a>
                                <a href="https://www.mercari.com/jp/category/23/">
              <div class="item-detail-table-sub-category"><i class="icon-arrow-right"></i> 小物</div>
            </a>
                    <a href="https://www.mercari.com/jp/category/241/">
            <div class="item-detail-table-sub-sub-category"><i class="icon-arrow-right"></i> 長財布</div>
          </a>
        </td>
      </tr>
      <tr>
        <th>ブランド</th>
          <td>
                          <a href="https://www.mercari.com/jp/brand/1326/">
                <div>
                  ルイ ヴィトン
                </div>
              </a>
                      </td>
      </tr>
            <tr>
        <th>商品の状態</th>
        <td>新品、未使用</td>
      </tr>
      <tr>
        <th>配送料の負担</th>
        <td>送料込み(出品者負担)</td>
      </tr>
      <tr>
        <th>配送の方法</th>
        <td>ゆうゆうメルカリ便</td>
      </tr>
              <tr>
          <th>配送元地域</th>
          <td><a href="https://www.mercari.com/jp/area/23/">愛知県</a></td>
        </tr>
      
              <tr>
          <th>発送日の目安</th>
          <td>1~2日で発送</td>
        </tr>
      
          </tbody></table>
  </div>

    <div class="item-price-box text-center">
    <span class="item-price bold">¥ 19,000</span>
    <span class="item-tax"> (税込)</span>
          <span class="item-shipping-fee">送料込み</span>
      </div>

                            <div class="item-buy-btn disabled f18-24">売り切れました</div>
      
          
    <div class="item-description f14">
    <p class="item-description-inner">ご覧頂きましてありがとうございます。
プレゼントで頂いた物です、使用する機会がなく、使っていません。

商品詳細
[状態]新品未使用
[素材] PVC
[備考] 「Made in France」 製造番号: SP1918
[サイズ] 11※17※2
[ランク] N
[付属品] 布保存袋
[購入元] プレゼント

※状態やランクにつきましては目安としてお考え頂きます様お願い致します。
※取り扱い商品のほとんどが中古品の為、全ての状態のお伝えが難しい場合が           ございます。神経質な方はご購入をお控え頂きます様お願い致します。
※本物の商品ですのでご安心してお買い物をお楽しみください。
※他にもブランド品を多数出品していますので、ご覧いただければ光栄です。

ランク参考
Nーー新品や新古品などの完璧な超美品。
NAーー未使用品でダメージ感が無い、美品の中古品
Aーーダメージをほとんど感じない、美品の中古品
ABーー目立たないダメージが僅かにある程度の、良品の中古品
Bーー若干の使用感があり若干のダメージがある、一般的な中古品といった印    象の商品
BCーー多少の使用感がありダメージ感のある、程度の低い中古品
Cーーかなりの使用感があり酷いダメージや欠陥があるが、使用は可能な中古品
CDーー酷いダメージや欠品欠損のある、使用に問題が出るような中古品
Dーー修理などが必要な使用困難な中古品(ジャンク品)


120</p>
  </div>

    <div class="item-button-container clearfix">
      <div class="item-button-left">
                            <button type="button" name="like!" data-toggle="like" data-id="m49934451444" data-ga="element" data-ga-category="LIKE" data-ga-label="like!" class="item-button item-button-like">
            <i class="icon-like-border"></i>
            <span>いいね!</span>
            <span data-num="like" class="fade-in-down">7</span>
          </button>

                    <a href="" data-modal="report-item" data-open="modal" class="item-button item-button-report clearfix">
            <i class="icon-flag"></i>
            <span>不適切な商品の報告</span>
          </a>

          <input name="like_add_url" value="https://www.mercari.com/jp/like/add/m49934451444/" type="hidden">
          <input name="like_del_url" value="https://www.mercari.com/jp/like/delete/m49934451444/" type="hidden">
          <input name="__csrf_value" value="8b694422410d6f020d3ed34432c060dd495bfaf18bc60677c850f56fbc79e6ae4c1b5f7ec70178396056daf587c4a0f1fb1b6965a8385131e0c766db028f42bf5" type="hidden">
              </div>
      <div class="item-button-right">
        <a href="https://www.mercari.com/jp/safe/description/" target="_blank">
          <i class="icon-lock"></i>
          <span>あんしん・あんぜんへの取り組み</span>
        </a>
      </div>
    </div>
</section>

<div class="item-detail-message">
  <div class="message-container">
      <div class="message-content">
      
      <ul class="message-items">
                  <li class="clearfix">
                          <a href="https://www.mercari.com/jp/u/766736075/" class="message-user">
                <figure>
                                    <div>
                    <img src="//static-mercari-jp-imgtr2.akamaized.net/thumb/members/766736075.jpg?1539354867" alt="">
                  </div>
                                    <figcaption class="bold">
                    山口
                  </figcaption>
                </figure>
                                              </a>
            
                        <div class="message-body">
              <div class="message-body-text">すみませんですが、これは12000円いいですか？</div>
              <div class="message-icons clearfix">
                                <time class="message-icon-left">
                  <i class="icon-time"></i>
                  <span>2 日前</span>
                </time>
                                  <div class="message-icon-right">
                                                              <form action="https://www.mercari.com/jp/comment/report/m49934451444/1834096255/" method="POST">
                        <button type="submit"><i class="icon-flag"></i></button>
                                                  <input name="redirect_url_key" value="item_detail" type="hidden">
                                              </form>
                                                                              </div>
                              </div>
              <i class="icon-balloon"></i>
            </div>
          </li>
                  <li class="clearfix">
                          <a href="https://www.mercari.com/jp/u/699952571/" class="message-user">
                <figure>
                                    <div>
                    <img src="//static-mercari-jp-imgtr2.akamaized.net/thumb/members/699952571.jpg?1540983530" alt="">
                  </div>
                                    <figcaption class="bold">
                    サチ
                  </figcaption>
                </figure>
                                              </a>
            
                        <div class="message-body">
              <div class="message-body-text">こんにちは、はじめまして。<br>
コメント失礼致します。<br>
即決、19000円で譲って頂けないでしょうか？<br>
よろしくお願い致します。</div>
              <div class="message-icons clearfix">
                                <time class="message-icon-left">
                  <i class="icon-time"></i>
                  <span>2 日前</span>
                </time>
                                  <div class="message-icon-right">
                                                              <form action="https://www.mercari.com/jp/comment/report/m49934451444/1834946323/" method="POST">
                        <button type="submit"><i class="icon-flag"></i></button>
                                                  <input name="redirect_url_key" value="item_detail" type="hidden">
                                              </form>
                                                                              </div>
                              </div>
              <i class="icon-balloon"></i>
            </div>
          </li>
                  <li class="clearfix">
                          <a href="https://www.mercari.com/jp/u/766736075/" class="message-user">
                <figure>
                                    <div>
                    <img src="//static-mercari-jp-imgtr2.akamaized.net/thumb/members/766736075.jpg?1539354867" alt="">
                  </div>
                                    <figcaption class="bold">
                    山口
                  </figcaption>
                </figure>
                                              </a>
            
                        <div class="message-body">
              <div class="message-body-text">15000円いいですか？</div>
              <div class="message-icons clearfix">
                                <time class="message-icon-left">
                  <i class="icon-time"></i>
                  <span>2 日前</span>
                </time>
                                  <div class="message-icon-right">
                                                              <form action="https://www.mercari.com/jp/comment/report/m49934451444/1834960116/" method="POST">
                        <button type="submit"><i class="icon-flag"></i></button>
                                                  <input name="redirect_url_key" value="item_detail" type="hidden">
                                              </form>
                                                                              </div>
                              </div>
              <i class="icon-balloon"></i>
            </div>
          </li>
                  <li class="clearfix">
                          <a href="https://www.mercari.com/jp/u/577215247/" class="message-user">
                <figure>
                                    <div>
                    <img src="//static-mercari-jp-imgtr2.akamaized.net/thumb/members/577215247.jpg?1537336302" alt="">
                  </div>
                                    <figcaption class="bold">
                    バナナ
                  </figcaption>
                </figure>
                                                  <div class="message-is-seller ">出品者</div>
                              </a>
            
                        <div class="message-body">
              <div class="message-body-text">金額を変更しました。よろしくお願いいたします。</div>
              <div class="message-icons clearfix">
                                <time class="message-icon-left">
                  <i class="icon-time"></i>
                  <span>2 日前</span>
                </time>
                                  <div class="message-icon-right">
                                                              <form action="https://www.mercari.com/jp/comment/report/m49934451444/1835431921/" method="POST">
                        <button type="submit"><i class="icon-flag"></i></button>
                                                  <input name="redirect_url_key" value="item_detail" type="hidden">
                                              </form>
                                                                              </div>
                              </div>
              <i class="icon-balloon"></i>
            </div>
          </li>
                  <li class="clearfix">
                          <a href="https://www.mercari.com/jp/u/577215247/" class="message-user">
                <figure>
                                    <div>
                    <img src="//static-mercari-jp-imgtr2.akamaized.net/thumb/members/577215247.jpg?1537336302" alt="">
                  </div>
                                    <figcaption class="bold">
                    バナナ
                  </figcaption>
                </figure>
                                                  <div class="message-is-seller ">出品者</div>
                              </a>
            
                        <div class="message-body">
              <div class="message-body-text">申し訳ありません，15000円は無理です。</div>
              <div class="message-icons clearfix">
                                <time class="message-icon-left">
                  <i class="icon-time"></i>
                  <span>2 日前</span>
                </time>
                                  <div class="message-icon-right">
                                                              <form action="https://www.mercari.com/jp/comment/report/m49934451444/1835433522/" method="POST">
                        <button type="submit"><i class="icon-flag"></i></button>
                                                  <input name="redirect_url_key" value="item_detail" type="hidden">
                                              </form>
                                                                              </div>
                              </div>
              <i class="icon-balloon"></i>
            </div>
          </li>
              </ul>
    </div>
            <div class="message-content">
      <form action="https://www.mercari.com/jp/comment/add/" method="POST" class="message-form">
        <p>相手のことを考え丁寧なコメントを心がけましょう。不快な言葉遣いなどは利用制限や退会処分となることがあります。</p>
        <input name="item_id" value="m49934451444" type="hidden">
                  <input name="redirect_url_key" value="item_detail" type="hidden">
                <textarea type="text" name="body" value="" class="textarea-default" disabled=""></textarea>
        <button type="submit" class="message-submit btn-default btn-gray" disabled="">
                      <span>売り切れのためコメントできません</span>
                  </button>
      </form>
    </div>
  <aside class="modal" data-modal="delete-comment" data-close="modal">
    <div class="modal-inner modal-banner">
      <div class="modal-body">
        <div class="modal-head bold">確認</div>
        <div data-comment="body"></div>
        <br>
        本当にこのコメントを削除してよろしいですか？
      </div>

      <form action="https://www.mercari.com/jp/comment/delete/" method="POST">
        <div class="clearfix">
          <div class="modal-btn modal-btn-cancel" data-close="modal">キャンセル</div>
          <button type="submit" class="modal-btn modal-btn-submit">
            削除する
          </button>
        </div>
        <input name="item_id" value="m49934451444" type="hidden">
        <input name="comment_id" value="" type="hidden">
                  <input name="redirect_url_key" value="item_detail" type="hidden">
                <input name="__csrf_value" value="8b694422410d6f020d3ed34432c060dd495bfaf18bc60677c850f56fbc79e6ae4c1b5f7ec70178396056daf587c4a0f1fb1b6965a8385131e0c766db028f42bf5" type="hidden">
      </form>
    </div>
  </aside>
  </div>
</div>
	 */
}
