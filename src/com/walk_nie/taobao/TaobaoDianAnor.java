package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;
import com.walk_nie.taobao.util.WebDriverSingleton;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;


public class TaobaoDianAnor {

	private  String taobaoUrl = "https://shopsearch.taobao.com/search?imgfile=&js=1&stats_click=search_radio_all%3A1&initiative_id=staobaoz_20190220&ie=utf8&q=";

	public static void main(String[] args) throws IOException {
		TaobaoDianAnor anor = new TaobaoDianAnor();
		anor.anorForkeyword();
	}
	
	public void anorForkeyword() throws IOException {
		String outputPath = NieConfig.getConfig("taobao.work.folder") +  "/dianAnor";
		File root = new File(outputPath);
		if (!root.exists()) {
			root.mkdir();
		}
		File keywordFile = new File(NieConfig.getConfig("taobao.dianAnor.keyword.infile"));
		System.out.println("[waiting for keyword in ]"
				+ keywordFile.getAbsolutePath());
		long updateTime = System.currentTimeMillis();
		while (true) {
			if (updateTime < keywordFile.lastModified()) {
				updateTime = keywordFile.lastModified();
				try{
					doAnor(root,keywordFile);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				System.out.println("[waiting for keyword in ]"
						+ keywordFile.getAbsolutePath());
			}
		}
	}
	private void doAnor(File root, File keywordFile) throws IOException {

		List<String> keys = Files.readLines(keywordFile, Charset.forName("UTF-8"));
		
		if(keys == null || keys.isEmpty()){
			System.err.println("[anorForkeyword] NONE record in File(" + keywordFile.getAbsolutePath() +")");
			return;
		}

		String yyyyMMdd = DateUtils.formatDate(Calendar.getInstance().getTime(), "yyyyMMdd");
		String fileNameFmt = "%s-%s.png";
		for (String k : keys) {
			String searchUrl = taobaoUrl + k.replaceAll(" ", "+");
			WebDriver webDriver = WebDriverSingleton.getWebDriver(searchUrl);
			
			NieUtil.mySleepBySecond(4);
			
			File screenshot = ((TakesScreenshot)  webDriver)
					.getScreenshotAs(OutputType.FILE);
			
			String fileNm = String.format(fileNameFmt, yyyyMMdd, k);
			File saveTo = new File(root, fileNm);
			FileUtils.copyFile(screenshot, saveTo);
		}
	}

	// https://s.taobao.com/search?type=p&tmhkh5=&spm=a21wu.241046-jp.a2227oh.d100&from=sea_1_searchbutton&catId=100&q=SHIMANO
	/*
	 <div id="mainsrp-itemlist"><div class="m-itemlist" data-spm="14" data-spm-max-idx="332">
  <div class="grid g-clearfix">
    <div class="items">
      
        
          

<div class="item J_MouserOnverReq item-ad  " data-category="auctions" data-index="0">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_546980536306" class="pic-link J_ClickStat J_ItemPicA" data-nid="546980536306" data-recommend-nav="" href="https://click.simba.taobao.com/cc_im?spm=a230r.1.14.1&amp;p=SHIMANO&amp;s=1784493868&amp;k=589&amp;e=VX6T8TVji9X4gRoQ9EuHHonAUHJj3h%2FWwBwO6TmuFec%2B9CV6E7X8z2Z66hdqwc%2BIbo%2FUKJ8fwWDETlaZQ4aOyxsTFfoTvEJE1bqtsSssndyBYXFWQRIvtDThUWCNQnAM6hfZ9PHPlHGRZj5GgVNOn4rG8liulhwzfFNF%2F7j5HYd%2FiU2hL27%2FeNtzZkRmXHC%2B6V1KsEvoMxlLQBkELo17Auml0SbSMdUjx4aBRFtf0%2FZAeDoYkYT7MFQ2fWQCuQvCvJ%2Buw51a3QdwNpLsCYW6UNUjF3HmOr38aV9At9qhs1%2FhEVZMZRwXu5jcGxLGB6eEjQTYonhq0QKuf7ymJyuPh79D2WIv%2FA0OOdhO659MM5%2BxuK5gZ7Yf%2F4i9NHgrNvG1ddEbS40GFR2ta%2FwBauW96mBcsj0luhPZlhK97FpuE7XcxJM6Fh2gqsUvwbF4V5h94DHqadIBC5mGGQqfTjszsHyyQKeE7tTrwjRBv9q6W5UquyxMDfUn32rju9UP2Y69aSvkQ5BYnafizem9JUrTN5CmPxtxAcx17EBz1cKxdH5jEglNwKX6XY1BriTnbG4O90SWW9gFXjg%3D" data-href="https://click.simba.taobao.com/cc_im?p=SHIMANO&amp;s=1784493868&amp;k=589&amp;e=VX6T8TVji9X4gRoQ9EuHHonAUHJj3h%2FWwBwO6TmuFec%2B9CV6E7X8z2Z66hdqwc%2BIbo%2FUKJ8fwWDETlaZQ4aOyxsTFfoTvEJE1bqtsSssndyBYXFWQRIvtDThUWCNQnAM6hfZ9PHPlHGRZj5GgVNOn4rG8liulhwzfFNF%2F7j5HYd%2FiU2hL27%2FeNtzZkRmXHC%2B6V1KsEvoMxlLQBkELo17Auml0SbSMdUjx4aBRFtf0%2FZAeDoYkYT7MFQ2fWQCuQvCvJ%2Buw51a3QdwNpLsCYW6UNUjF3HmOr38aV9At9qhs1%2FhEVZMZRwXu5jcGxLGB6eEjQTYonhq0QKuf7ymJyuPh79D2WIv%2FA0OOdhO659MM5%2BxuK5gZ7Yf%2F4i9NHgrNvG1ddEbS40GFR2ta%2FwBauW96mBcsj0luhPZlhK97FpuE7XcxJM6Fh2gqsUvwbF4V5h94DHqadIBC5mGGQqfTjszsHyyQKeE7tTrwjRBv9q6W5UquyxMDfUn32rju9UP2Y69aSvkQ5BYnafizem9JUrTN5CmPxtxAcx17EBz1cKxdH5jEglNwKX6XY1BriTnbG4O90SWW9gFXjg%3D" target="_blank" trace="msrp_auction" traceidx="0" trace-index="0" trace-nid="546980536306" trace-num="48" trace-price="608.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.1" data-spm-act-id="a230r.1.14.1">
          
            <img id="J_Itemlist_Pic_546980536306" class="J_ItemPic img" src="//g-search1.alicdn.com/img/bao/uploaded/i4/imgextra/i1/46113337/O1CN01hMe2t41aWOAXtPohQ_!!0-saturn_solar.jpg_180x180.jpg" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/imgextra/i1/46113337/O1CN01hMe2t41aWOAXtPohQ_!!0-saturn_solar.jpg" alt="Shimano禧玛诺复古休闲背包TOKY" data-spm-anchor-id="a230r.1.14.i0.6f5477fcRxHSbK">
          
        </a>
      </div>

      
        


      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.2.6f5477fcRxHSbK&amp;itemId=546980536306" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.2">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>608.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_546980536306" class="J_ClickStat" data-nid="546980536306" href="https://click.simba.taobao.com/cc_im?p=SHIMANO&amp;s=1784493868&amp;k=589&amp;e=VX6T8TVji9X4gRoQ9EuHHonAUHJj3h%2FWwBwO6TmuFec%2B9CV6E7X8z2Z66hdqwc%2BIbo%2FUKJ8fwWDETlaZQ4aOyxsTFfoTvEJE1bqtsSssndyBYXFWQRIvtDThUWCNQnAM6hfZ9PHPlHGRZj5GgVNOn4rG8liulhwzfFNF%2F7j5HYd%2FiU2hL27%2FeNtzZkRmXHC%2B6V1KsEvoMxlLQBkELo17Auml0SbSMdUjx4aBRFtf0%2FZAeDoYkYT7MFQ2fWQCuQvCvJ%2Buw51a3QdwNpLsCYW6UNUjF3HmOr38aV9At9qhs1%2FhEVZMZRwXu5jcGxLGB6eEjQTYonhq0QKuf7ymJyuPh79D2WIv%2FA0OOdhO659MM5%2BxuK5gZ7Yf%2F4i9NHgrNvG1ddEbS40GFR2ta%2FwBauW96mBcsj0luhPZlhK97FpuE7XcxJM6Fh2gqsUvwbF4V5h94DHqadIBC5mGGQqfTjszsHyyQKeE7tTrwjRBv9q6W5UquyxMDfUn32rju9UP2Y69aSvkQ5BYnafizem9JUrTN5CmPxtxAcx17EBz1cKxdH5jEglNwKX6XY1BriTnbG4O90SWW9gFXjg%3D" target="_blank" trace="msrp_auction" traceidx="0" trace-index="0" trace-nid="546980536306" trace-num="48" trace-price="608.00" trace-pid="" data-spm-anchor-id="a230r.1.14.3">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        Shimano禧玛诺复古休闲背包TOKYO骑行背包 城市通勤背包 邮差包
      </a>
    </div>

    
      
      
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      
        <a class="shopname p4p-shopname J_MouseEneterLeave J_ShopInfo" data-userid="1771002457" data-nid="546980536306" href="javascript:;" data-spm-anchor-id="a230r.1.14.4">
      
        
          <span class="dsrs">
            
              <span class="dsr morethan"></span>
            
              <span class="dsr morethan"></span>
            
              <span class="dsr morethan"></span>
            
          </span>
        
        <span>九骑户外旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 杭州</div>
      </div>
      
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_546980536306">
        <ul class="icons">
          
          

          
            <li class="icon" data-index="0">
              
                
                  <a href="//re.taobao.com/search?spm=a230r.1.14.5.6f5477fcRxHSbK&amp;keyword=SHIMANO&amp;refpid=420432_1006&amp;frcatid=&amp;" target="_blank" title="掌柜热卖宝贝" trace="srpservice" traceidx="0" data-spm-anchor-id="a230r.1.14.5"><span class="icon-service-remai"></span></a>
                
              
            </li>
          
            <li class="icon" data-index="1">
              
                
                  <span class="icon-service-tianmao" title="尚天猫，就购了"></span>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="2">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="1">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_568531135224" class="pic-link J_ClickStat J_ItemPicA" data-nid="568531135224" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.6.6f5477fcRxHSbK&amp;id=568531135224&amp;ad_id=&amp;am_id=&amp;cm_id=140105335569ed55e27b&amp;pm_id=&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=568531135224&amp;ad_id=&amp;am_id=&amp;cm_id=140105335569ed55e27b&amp;pm_id=&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="1" trace-index="1" trace-nid="568531135224" trace-num="48" trace-price="174.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.6">
          
            <img id="J_Itemlist_Pic_568531135224" class="J_ItemPic img" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/3909853424/O1CN01dqkYiP1bAEcpRXXNY_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/3909853424/O1CN01dqkYiP1bAEcpRXXNY_!!0-item_pic.jpg" alt="SHIMANO禧玛诺纺车轮SIENNA渔轮鱼线轮路亚轮金属矶钓远投海钓轮">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.7.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=568531135224" data-spm-anchor-id="a230r.1.14.7">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.8.6f5477fcRxHSbK&amp;itemId=568531135224" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.8">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>174.00</strong>
      </div>
      <div class="deal-cnt">264人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_568531135224" class="J_ClickStat" data-nid="568531135224" href="//detail.tmall.com/item.htm?spm=a230r.1.14.9.6f5477fcRxHSbK&amp;id=568531135224&amp;ad_id=&amp;am_id=&amp;cm_id=140105335569ed55e27b&amp;pm_id=&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="1" trace-index="1" trace-nid="568531135224" trace-num="48" trace-price="174.00" trace-pid="" data-spm-anchor-id="a230r.1.14.9">
        
        <span class="H">SHIMANO</span>禧玛诺纺车轮SIENNA渔轮鱼线轮路亚轮金属矶钓远投海钓轮
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3909853424" data-nid="568531135224" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.10.6f5477fcRxHSbK&amp;user_number_id=3909853424" target="_blank" data-spm-anchor-id="a230r.1.14.10">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>shimano禧玛诺怡和专卖店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 宁波</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_568531135224">
        <ul class="icons">
          
          

          
            <li class="icon" data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.11.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="3" data-spm-anchor-id="a230r.1.14.11"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="shimano禧玛诺怡和专卖店" data-display="inline" data-item="568531135224" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.12.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=shimano%E7%A6%A7%E7%8E%9B%E8%AF%BA%E6%80%A1%E5%92%8C%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.12"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="2">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_568582067017" class="pic-link J_ClickStat J_ItemPicA" data-nid="568582067017" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.13.6f5477fcRxHSbK&amp;id=568582067017&amp;ad_id=&amp;am_id=&amp;cm_id=140105335569ed55e27b&amp;pm_id=&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=568582067017&amp;ad_id=&amp;am_id=&amp;cm_id=140105335569ed55e27b&amp;pm_id=&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="2" trace-index="2" trace-nid="568582067017" trace-num="48" trace-price="441.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.13">
          
            <img id="J_Itemlist_Pic_568582067017" class="J_ItemPic img" src="//g-search2.alicdn.com/img/bao/uploaded/i4/i2/3909853424/O1CN01IyMO8U1bAEcn8rpoB_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i2/3909853424/O1CN01IyMO8U1bAEcn8rpoB_!!0-item_pic.jpg" alt="SHIMANO禧玛诺纺车轮SAHARA FI路亚轮鱼线轮海钓轮渔轮金属远投轮">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.14.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=568582067017" data-spm-anchor-id="a230r.1.14.14">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.15.6f5477fcRxHSbK&amp;itemId=568582067017" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.15">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>441.00</strong>
      </div>
      <div class="deal-cnt">52人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_568582067017" class="J_ClickStat" data-nid="568582067017" href="//detail.tmall.com/item.htm?spm=a230r.1.14.16.6f5477fcRxHSbK&amp;id=568582067017&amp;ad_id=&amp;am_id=&amp;cm_id=140105335569ed55e27b&amp;pm_id=&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="2" trace-index="2" trace-nid="568582067017" trace-num="48" trace-price="441.00" trace-pid="" data-spm-anchor-id="a230r.1.14.16">
        
        <span class="H">SHIMANO</span>禧玛诺纺车轮SAHARA FI路亚轮鱼线轮海钓轮渔轮金属远投轮
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3909853424" data-nid="568582067017" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.17.6f5477fcRxHSbK&amp;user_number_id=3909853424" target="_blank" data-spm-anchor-id="a230r.1.14.17">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>shimano禧玛诺怡和专卖店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 宁波</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_568582067017">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.18.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="4" data-spm-anchor-id="a230r.1.14.18"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="shimano禧玛诺怡和专卖店" data-display="inline" data-item="568582067017" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.19.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=shimano%E7%A6%A7%E7%8E%9B%E8%AF%BA%E6%80%A1%E5%92%8C%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.19"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="3">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_580285448947" class="pic-link J_ClickStat J_ItemPicA" data-nid="580285448947" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.20.6f5477fcRxHSbK&amp;id=580285448947&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=580285448947&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="3" trace-index="3" trace-nid="580285448947" trace-num="48" trace-price="3350.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.20">
          
            <img id="J_Itemlist_Pic_580285448947" class="J_ItemPic img" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/699657113/O1CN0122PngY5ELSjnwuk_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/699657113/O1CN0122PngY5ELSjnwuk_!!0-item_pic.jpg" alt="SHIMANO/禧玛诺 矶钓竿 18 TWIN PULSER SZ2 海钓竿 钓竿 垂钓用">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.21.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=580285448947" data-spm-anchor-id="a230r.1.14.21">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.22.6f5477fcRxHSbK&amp;itemId=580285448947" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.22">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>3350.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_580285448947" class="J_ClickStat" data-nid="580285448947" href="//detail.tmall.com/item.htm?spm=a230r.1.14.23.6f5477fcRxHSbK&amp;id=580285448947&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="3" trace-index="3" trace-nid="580285448947" trace-num="48" trace-price="3350.00" trace-pid="" data-spm-anchor-id="a230r.1.14.23">
        
        <span class="H">SHIMANO</span>/禧玛诺 矶钓竿 18 TWIN PULSER SZ2 海钓竿 钓竿 垂钓用
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="699657113" data-nid="580285448947" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.24.6f5477fcRxHSbK&amp;user_number_id=699657113" target="_blank" data-spm-anchor-id="a230r.1.14.24">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>拿趣然户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">上海</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_580285448947">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.25.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="5" data-spm-anchor-id="a230r.1.14.25"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon " data-index="1">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="拿趣然户外专营店" data-display="inline" data-item="580285448947" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.26.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%8B%BF%E8%B6%A3%E7%84%B6%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.26"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="4">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_551758134735" class="pic-link J_ClickStat J_ItemPicA" data-nid="551758134735" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.27.6f5477fcRxHSbK&amp;id=551758134735&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=551758134735&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="4" trace-index="4" trace-nid="551758134735" trace-num="48" trace-price="1275.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.27">
          
            <img id="J_Itemlist_Pic_551758134735" class="J_ItemPic img" src="//g-search1.alicdn.com/img/bao/uploaded/i4/i1/699657113/O1CN0122PnfeRd8p6wgga_!!699657113.jpg_180x180.jpg" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/i1/699657113/O1CN0122PnfeRd8p6wgga_!!699657113.jpg" alt="SHIMANO/禧玛诺 鱼竿 庆春风 台钓竿 振出式鲤竿 渔具">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.28.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=551758134735" data-spm-anchor-id="a230r.1.14.28">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.29.6f5477fcRxHSbK&amp;itemId=551758134735" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.29">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1275.00</strong>
      </div>
      <div class="deal-cnt">7人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_551758134735" class="J_ClickStat" data-nid="551758134735" href="//detail.tmall.com/item.htm?spm=a230r.1.14.30.6f5477fcRxHSbK&amp;id=551758134735&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="4" trace-index="4" trace-nid="551758134735" trace-num="48" trace-price="1275.00" trace-pid="" data-spm-anchor-id="a230r.1.14.30">
        
        <span class="H">SHIMANO</span>/禧玛诺 鱼竿 庆春风 台钓竿 振出式鲤竿 渔具
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="699657113" data-nid="551758134735" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.31.6f5477fcRxHSbK&amp;user_number_id=699657113" target="_blank" data-spm-anchor-id="a230r.1.14.31">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>拿趣然户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">上海</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_551758134735">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.32.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="7" data-spm-anchor-id="a230r.1.14.32"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon " data-index="1">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="拿趣然户外专营店" data-display="inline" data-item="551758134735" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.33.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%8B%BF%E8%B6%A3%E7%84%B6%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.33"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="5">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_570596820491" class="pic-link J_ClickStat J_ItemPicA" data-nid="570596820491" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.34.6f5477fcRxHSbK&amp;id=570596820491&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=570596820491&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="5" trace-index="5" trace-nid="570596820491" trace-num="48" trace-price="88.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.34">
          
            <img id="J_Itemlist_Pic_570596820491" class="J_ItemPic img" src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/1771002457/O1CN01aT25Id1U1Ld3ZXGQj_!!1771002457.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/1771002457/O1CN01aT25Id1U1Ld3ZXGQj_!!1771002457.jpg" alt="Shimano禧玛诺Elite自行车水壶架CUSTOM RACE PLUS骑行水壶架">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.35.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=570596820491" data-spm-anchor-id="a230r.1.14.35">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.36.6f5477fcRxHSbK&amp;itemId=570596820491" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.36">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>88.00</strong>
      </div>
      <div class="deal-cnt">18人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_570596820491" class="J_ClickStat" data-nid="570596820491" href="//detail.tmall.com/item.htm?spm=a230r.1.14.37.6f5477fcRxHSbK&amp;id=570596820491&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="5" trace-index="5" trace-nid="570596820491" trace-num="48" trace-price="88.00" trace-pid="" data-spm-anchor-id="a230r.1.14.37">
        
        <span class="H">Shimano</span>禧玛诺Elite自行车水壶架CUSTOM RACE PLUS骑行水壶架
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1771002457" data-nid="570596820491" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.38.6f5477fcRxHSbK&amp;user_number_id=1771002457" target="_blank" data-spm-anchor-id="a230r.1.14.38">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>九骑户外旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 杭州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_570596820491">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.39.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="9" data-spm-anchor-id="a230r.1.14.39"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="九骑户外旗舰店" data-display="inline" data-item="570596820491" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.40.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E4%B9%9D%E9%AA%91%E6%88%B7%E5%A4%96%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.40"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="6">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_41093406248" class="pic-link J_ClickStat J_ItemPicA" data-nid="41093406248" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.41.6f5477fcRxHSbK&amp;id=41093406248&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=41093406248&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="6" trace-index="6" trace-nid="41093406248" trace-num="48" trace-price="488.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.41">
          
            <img id="J_Itemlist_Pic_41093406248" class="J_ItemPic img" src="//g-search1.alicdn.com/img/bao/uploaded/i4/i1/1771002457/O1CN01THOtKa1U1LdB7mgqA_!!1771002457.jpg_180x180.jpg" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/i1/1771002457/O1CN01THOtKa1U1LdB7mgqA_!!1771002457.jpg" alt="Shimano禧玛诺正品行货公路车新款105锁踏R7000公路车锁踏附扣片">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.42.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=41093406248" data-spm-anchor-id="a230r.1.14.42">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.43.6f5477fcRxHSbK&amp;itemId=41093406248" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.43">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>488.00</strong>
      </div>
      <div class="deal-cnt">9人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_41093406248" class="J_ClickStat" data-nid="41093406248" href="//detail.tmall.com/item.htm?spm=a230r.1.14.44.6f5477fcRxHSbK&amp;id=41093406248&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="6" trace-index="6" trace-nid="41093406248" trace-num="48" trace-price="488.00" trace-pid="" data-spm-anchor-id="a230r.1.14.44">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        <span class="H">Shimano</span>禧玛诺正品行货公路车新款105锁踏R7000公路车锁踏附扣片
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1771002457" data-nid="41093406248" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.45.6f5477fcRxHSbK&amp;user_number_id=1771002457" target="_blank" data-spm-anchor-id="a230r.1.14.45">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>九骑户外旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 杭州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_41093406248">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.46.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="11" data-spm-anchor-id="a230r.1.14.46"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="九骑户外旗舰店" data-display="inline" data-item="41093406248" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.47.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E4%B9%9D%E9%AA%91%E6%88%B7%E5%A4%96%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.47"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="7">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_568421281951" class="pic-link J_ClickStat J_ItemPicA" data-nid="568421281951" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.48.6f5477fcRxHSbK&amp;id=568421281951&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=568421281951&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="7" trace-index="7" trace-nid="568421281951" trace-num="48" trace-price="2289.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.48">
          
            <img id="J_Itemlist_Pic_568421281951" class="J_ItemPic img" src="//g-search1.alicdn.com/img/bao/uploaded/i4/i3/3909853424/O1CN01Sf9dpr1bAEcqpILYg_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/i3/3909853424/O1CN01Sf9dpr1bAEcqpILYg_!!0-item_pic.jpg" alt="SHIMANO禧玛诺海鲈竿 LUNAMIS 双节直柄枪柄路亚竿碳素鱼竿钛合金">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.49.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=568421281951" data-spm-anchor-id="a230r.1.14.49">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.50.6f5477fcRxHSbK&amp;itemId=568421281951" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.50">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2289.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_568421281951" class="J_ClickStat" data-nid="568421281951" href="//detail.tmall.com/item.htm?spm=a230r.1.14.51.6f5477fcRxHSbK&amp;id=568421281951&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="7" trace-index="7" trace-nid="568421281951" trace-num="48" trace-price="2289.00" trace-pid="" data-spm-anchor-id="a230r.1.14.51">
        
        <span class="H">SHIMANO</span>禧玛诺海鲈竿 LUNAMIS 双节直柄枪柄路亚竿碳素鱼竿钛合金
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3909853424" data-nid="568421281951" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.52.6f5477fcRxHSbK&amp;user_number_id=3909853424" target="_blank" data-spm-anchor-id="a230r.1.14.52">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>shimano禧玛诺怡和专卖店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 宁波</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_568421281951">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.53.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="13" data-spm-anchor-id="a230r.1.14.53"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="shimano禧玛诺怡和专卖店" data-display="inline" data-item="568421281951" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.54.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=shimano%E7%A6%A7%E7%8E%9B%E8%AF%BA%E6%80%A1%E5%92%8C%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.54"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="8">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_568296240073" class="pic-link J_ClickStat J_ItemPicA" data-nid="568296240073" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.55.6f5477fcRxHSbK&amp;id=568296240073&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=568296240073&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="8" trace-index="8" trace-nid="568296240073" trace-num="48" trace-price="1469.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.55">
          
            <img id="J_Itemlist_Pic_568296240073" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/3909853424/O1CN01dDQGh51bAEcnq5m7Z_!!0-item_pic.jpg" alt="SHIMANO 禧玛诺 路亚竿NEW EXPRIDE 枪柄直柄路亚竿 碳素轻量鱼竿" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/3909853424/O1CN01dDQGh51bAEcnq5m7Z_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.56.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=568296240073" data-spm-anchor-id="a230r.1.14.56">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.57.6f5477fcRxHSbK&amp;itemId=568296240073" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.57">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1469.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_568296240073" class="J_ClickStat" data-nid="568296240073" href="//detail.tmall.com/item.htm?spm=a230r.1.14.58.6f5477fcRxHSbK&amp;id=568296240073&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="8" trace-index="8" trace-nid="568296240073" trace-num="48" trace-price="1469.00" trace-pid="" data-spm-anchor-id="a230r.1.14.58">
        
        <span class="H">SHIMANO</span> 禧玛诺 路亚竿NEW EXPRIDE 枪柄直柄路亚竿 碳素轻量鱼竿
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3909853424" data-nid="568296240073" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.59.6f5477fcRxHSbK&amp;user_number_id=3909853424" target="_blank" data-spm-anchor-id="a230r.1.14.59">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>shimano禧玛诺怡和专卖店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 宁波</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_568296240073">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.60.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="14" data-spm-anchor-id="a230r.1.14.60"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="shimano禧玛诺怡和专卖店" data-display="inline" data-item="568296240073" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.61.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=shimano%E7%A6%A7%E7%8E%9B%E8%AF%BA%E6%80%A1%E5%92%8C%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.61"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="9">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_555889755359" class="pic-link J_ClickStat J_ItemPicA" data-nid="555889755359" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.62.6f5477fcRxHSbK&amp;id=555889755359&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=555889755359&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="9" trace-index="9" trace-nid="555889755359" trace-num="48" trace-price="357.84" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.62">
          
            <img id="J_Itemlist_Pic_555889755359" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/TB1ua9.SpXXXXbjXFXXXXXXXXXX_!!0-item_pic.jpg" alt="新款SHIMANO 禧玛诺R8000 8010公路自行车C夹器刹车装置UT直装式" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/TB1ua9.SpXXXXbjXFXXXXXXXXXX_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.63.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=555889755359" data-spm-anchor-id="a230r.1.14.63">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.64.6f5477fcRxHSbK&amp;itemId=555889755359" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.64">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>357.84</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_555889755359" class="J_ClickStat" data-nid="555889755359" href="//detail.tmall.com/item.htm?spm=a230r.1.14.65.6f5477fcRxHSbK&amp;id=555889755359&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="9" trace-index="9" trace-nid="555889755359" trace-num="48" trace-price="357.84" trace-pid="" data-spm-anchor-id="a230r.1.14.65">
        
        新款<span class="H">SHIMANO</span> 禧玛诺R8000 8010公路自行车C夹器刹车装置UT直装式
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2022055885" data-nid="555889755359" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.66.6f5477fcRxHSbK&amp;user_number_id=2022055885" target="_blank" data-spm-anchor-id="a230r.1.14.66">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>骑侠运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">广东 广州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_555889755359">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.67.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="15" data-spm-anchor-id="a230r.1.14.67"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="骑侠运动户外专营店" data-display="inline" data-item="555889755359" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.68.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E9%AA%91%E4%BE%A0%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-offline" data-spm-anchor-id="a230r.1.14.68"><span>旺旺离线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="10">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_555992654092" class="pic-link J_ClickStat J_ItemPicA" data-nid="555992654092" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.69.6f5477fcRxHSbK&amp;id=555992654092&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=555992654092&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="10" trace-index="10" trace-nid="555992654092" trace-num="48" trace-price="319.60" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.69">
          
            <img id="J_Itemlist_Pic_555992654092" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/TB162LUSpXXXXbFXpXXXXXXXXXX_!!0-item_pic.jpg" alt="SHIMANO 禧玛诺RT800 RT900公路自行车碟刹车碟片中锁UT DA套件" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/TB162LUSpXXXXbFXpXXXXXXXXXX_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.70.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=555992654092" data-spm-anchor-id="a230r.1.14.70">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.71.6f5477fcRxHSbK&amp;itemId=555992654092" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.71">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>319.60</strong>
      </div>
      <div class="deal-cnt">3人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_555992654092" class="J_ClickStat" data-nid="555992654092" href="//detail.tmall.com/item.htm?spm=a230r.1.14.72.6f5477fcRxHSbK&amp;id=555992654092&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="10" trace-index="10" trace-nid="555992654092" trace-num="48" trace-price="319.60" trace-pid="" data-spm-anchor-id="a230r.1.14.72">
        
        <span class="H">SHIMANO</span> 禧玛诺RT800 RT900公路自行车碟刹车碟片中锁UT DA套件
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2022055885" data-nid="555992654092" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.73.6f5477fcRxHSbK&amp;user_number_id=2022055885" target="_blank" data-spm-anchor-id="a230r.1.14.73">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>骑侠运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">广东 广州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_555992654092">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.74.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="16" data-spm-anchor-id="a230r.1.14.74"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="骑侠运动户外专营店" data-display="inline" data-item="555992654092" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.75.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E9%AA%91%E4%BE%A0%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-offline" data-spm-anchor-id="a230r.1.14.75"><span>旺旺离线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="11">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_537463374476" class="pic-link J_ClickStat J_ItemPicA" data-nid="537463374476" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.76.6f5477fcRxHSbK&amp;id=537463374476&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=537463374476&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="11" trace-index="11" trace-nid="537463374476" trace-num="48" trace-price="12999.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.76">
          
            <img id="J_Itemlist_Pic_537463374476" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/2933153576/TB2aDXgXCzqK1RjSZFHXXb3CpXa_!!2933153576-0-item_pic.jpg" alt="2017法国LOOK碳纤维公路自行车22速公路单车禧玛诺套件公路车765" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/2933153576/TB2aDXgXCzqK1RjSZFHXXb3CpXa_!!2933153576-0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.77.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=537463374476" data-spm-anchor-id="a230r.1.14.77">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.78.6f5477fcRxHSbK&amp;itemId=537463374476" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.78">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>12999.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_537463374476" class="J_ClickStat" data-nid="537463374476" href="//detail.tmall.com/item.htm?spm=a230r.1.14.79.6f5477fcRxHSbK&amp;id=537463374476&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="11" trace-index="11" trace-nid="537463374476" trace-num="48" trace-price="12999.00" trace-pid="" data-spm-anchor-id="a230r.1.14.79">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        2017法国LOOK碳纤维公路自行车22速公路单车禧玛诺套件公路车765
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2933153576" data-nid="537463374476" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.80.6f5477fcRxHSbK&amp;user_number_id=2933153576" target="_blank" data-spm-anchor-id="a230r.1.14.80">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr equalthan"></span>
          
        </span>
        <span>美骑旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 广州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_537463374476">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.81.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="17" data-spm-anchor-id="a230r.1.14.81"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="美骑旗舰店" data-display="inline" data-item="537463374476" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.82.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E7%BE%8E%E9%AA%91%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.82"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="12">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_41335327278" class="pic-link J_ClickStat J_ItemPicA" data-nid="41335327278" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.83.6f5477fcRxHSbK&amp;id=41335327278&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=41335327278&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="12" trace-index="12" trace-nid="41335327278" trace-num="48" trace-price="78.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.83">
          
            <img id="J_Itemlist_Pic_41335327278" class="J_ItemPic img" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/i3/TB1mbIFRVXXXXa5apXXXXXXXXXX_!!0-item_pic.jpg" alt="立富Icetoolz 中轴工具拆卸工具自行车工具山地车shimano花键11B1" src="//g-search1.alicdn.com/img/bao/uploaded/i4/i3/TB1mbIFRVXXXXa5apXXXXXXXXXX_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.84.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=41335327278" data-spm-anchor-id="a230r.1.14.84">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.85.6f5477fcRxHSbK&amp;itemId=41335327278" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.85">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>78.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_41335327278" class="J_ClickStat" data-nid="41335327278" href="//detail.tmall.com/item.htm?spm=a230r.1.14.86.6f5477fcRxHSbK&amp;id=41335327278&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="12" trace-index="12" trace-nid="41335327278" trace-num="48" trace-price="78.00" trace-pid="" data-spm-anchor-id="a230r.1.14.86">
        
        立富Icetoolz 中轴工具拆卸工具自行车工具山地车<span class="H">shimano</span>花键11B1
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2019966336" data-nid="41335327278" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.87.6f5477fcRxHSbK&amp;user_number_id=2019966336" target="_blank" data-spm-anchor-id="a230r.1.14.87">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>酷风运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">陕西 西安</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_41335327278">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.88.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="19" data-spm-anchor-id="a230r.1.14.88"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon " data-index="1">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="酷风运动户外专营店" data-display="inline" data-item="41335327278" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.89.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E9%85%B7%E9%A3%8E%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.89"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="13">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_581487036650" class="pic-link J_ClickStat J_ItemPicA" data-nid="581487036650" data-recommend-nav="" href="//item.taobao.com/item.htm?spm=a230r.1.14.90.6f5477fcRxHSbK&amp;id=581487036650&amp;ns=1&amp;abbucket=4#detail" data-href="//item.taobao.com/item.htm?id=581487036650&amp;ns=1&amp;abbucket=4#detail" target="_blank" trace="msrp_auction" traceidx="13" trace-index="13" trace-nid="581487036650" trace-num="48" trace-price="148.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.90">
          
            <img id="J_Itemlist_Pic_581487036650" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/74109925/O1CN012NBhQWeK73eTdV8_!!74109925.jpg" alt="西班牙AMS CANK  曲柄保护套适合SRAM Shimano" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/74109925/O1CN012NBhQWeK73eTdV8_!!74109925.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.91.6f5477fcRxHSbK&amp;itemId=581487036650" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.91">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>148.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_581487036650" class="J_ClickStat" data-nid="581487036650" href="//item.taobao.com/item.htm?spm=a230r.1.14.92.6f5477fcRxHSbK&amp;id=581487036650&amp;ns=1&amp;abbucket=4#detail" target="_blank" trace="msrp_auction" traceidx="13" trace-index="13" trace-nid="581487036650" trace-num="48" trace-price="148.00" trace-pid="" data-spm-anchor-id="a230r.1.14.92">
        
        西班牙AMS CANK  曲柄保护套适合SRAM <span class="H">Shimano</span>
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="74109925" data-nid="581487036650" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.93.6f5477fcRxHSbK&amp;user_number_id=74109925" target="_blank" data-spm-anchor-id="a230r.1.14.93">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>三鼎单车生活馆</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_581487036650">
        <ul class="icons">
          
          

          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="三鼎单车生活馆" data-display="inline" data-item="581487036650" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.94.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E4%B8%89%E9%BC%8E%E5%8D%95%E8%BD%A6%E7%94%9F%E6%B4%BB%E9%A6%86&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.94"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="14">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_555988234689" class="pic-link J_ClickStat J_ItemPicA" data-nid="555988234689" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.95.6f5477fcRxHSbK&amp;id=555988234689&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=555988234689&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="14" trace-index="14" trace-nid="555988234689" trace-num="48" trace-price="159.90" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.95">
          
            <img id="J_Itemlist_Pic_555988234689" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/TB1VEt1SpXXXXXcaXXXXXXXXXXX_!!0-item_pic.jpg" alt="迪卡侬 禧玛诺飞轮 自行车10速山地自行车零件飞轮11-36齿HBTWIN" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/TB1VEt1SpXXXXXcaXXXXXXXXXXX_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.96.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=555988234689" data-spm-anchor-id="a230r.1.14.96">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.97.6f5477fcRxHSbK&amp;itemId=555988234689" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.97">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>159.90</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_555988234689" class="J_ClickStat" data-nid="555988234689" href="//detail.tmall.com/item.htm?spm=a230r.1.14.98.6f5477fcRxHSbK&amp;id=555988234689&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="14" trace-index="14" trace-nid="555988234689" trace-num="48" trace-price="159.90" trace-pid="" data-spm-anchor-id="a230r.1.14.98">
        
        迪卡侬 禧玛诺飞轮 自行车10速山地自行车零件飞轮11-36齿HBTWIN
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="352469034" data-nid="555988234689" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.99.6f5477fcRxHSbK&amp;user_number_id=352469034" target="_blank" data-spm-anchor-id="a230r.1.14.99">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>迪卡侬旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">上海</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_555988234689">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.100.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="21" data-spm-anchor-id="a230r.1.14.100"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="迪卡侬旗舰店" data-display="inline" data-item="555988234689" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.101.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E8%BF%AA%E5%8D%A1%E4%BE%AC%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.101"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="15">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_529780814015" class="pic-link J_ClickStat J_ItemPicA" data-nid="529780814015" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.102.6f5477fcRxHSbK&amp;id=529780814015&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=529780814015&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="15" trace-index="15" trace-nid="529780814015" trace-num="48" trace-price="3399.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.102">
          
            <img id="J_Itemlist_Pic_529780814015" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/1086970088/O1CN01tfnBo91CWLLrEHZQx_!!0-item_pic.jpg" alt="SAVA萨瓦战风碳纤维公路车自行车18速禧玛诺变速超轻破风公路赛车" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/1086970088/O1CN01tfnBo91CWLLrEHZQx_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.103.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=529780814015" data-spm-anchor-id="a230r.1.14.103">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.104.6f5477fcRxHSbK&amp;itemId=529780814015" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.104">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>3399.00</strong>
      </div>
      <div class="deal-cnt">5人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_529780814015" class="J_ClickStat" data-nid="529780814015" href="//detail.tmall.com/item.htm?spm=a230r.1.14.105.6f5477fcRxHSbK&amp;id=529780814015&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="15" trace-index="15" trace-nid="529780814015" trace-num="48" trace-price="3399.00" trace-pid="" data-spm-anchor-id="a230r.1.14.105">
        
        SAVA萨瓦战风碳纤维公路车自行车18速禧玛诺变速超轻破风公路赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1086970088" data-nid="529780814015" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.106.6f5477fcRxHSbK&amp;user_number_id=1086970088" target="_blank" data-spm-anchor-id="a230r.1.14.106">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>sava旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 惠州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_529780814015">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.107.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="22" data-spm-anchor-id="a230r.1.14.107"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="2">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
            <li class="icon " data-index="3">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="sava旗舰店" data-display="inline" data-item="529780814015" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.108.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=sava%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.108"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="16">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_559318521328" class="pic-link J_ClickStat J_ItemPicA" data-nid="559318521328" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.109.6f5477fcRxHSbK&amp;id=559318521328&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=559318521328&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="16" trace-index="16" trace-nid="559318521328" trace-num="48" trace-price="1399.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.109">
          
            <img id="J_Itemlist_Pic_559318521328" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/2397525548/O1CN01I70Wrl1qr1rz1quPU_!!0-item_pic.jpg" alt="喜德盛公路车RX200公路自行车14速禧玛诺变速成人弯把公路赛车" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/2397525548/O1CN01I70Wrl1qr1rz1quPU_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.110.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=559318521328" data-spm-anchor-id="a230r.1.14.110">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.111.6f5477fcRxHSbK&amp;itemId=559318521328" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.111">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1399.00</strong>
      </div>
      <div class="deal-cnt">15人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_559318521328" class="J_ClickStat" data-nid="559318521328" href="//detail.tmall.com/item.htm?spm=a230r.1.14.112.6f5477fcRxHSbK&amp;id=559318521328&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="16" trace-index="16" trace-nid="559318521328" trace-num="48" trace-price="1399.00" trace-pid="" data-spm-anchor-id="a230r.1.14.112">
        
        喜德盛公路车RX200公路自行车14速禧玛诺变速成人弯把公路赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2397525548" data-nid="559318521328" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.113.6f5477fcRxHSbK&amp;user_number_id=2397525548" target="_blank" data-spm-anchor-id="a230r.1.14.113">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>xds喜德盛旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_559318521328">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.114.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="26" data-spm-anchor-id="a230r.1.14.114"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="2">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
            <li class="icon " data-index="3">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="xds喜德盛旗舰店" data-display="inline" data-item="559318521328" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.115.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=xds%E5%96%9C%E5%BE%B7%E7%9B%9B%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.115"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="17">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_577188144949" class="pic-link J_ClickStat J_ItemPicA" data-nid="577188144949" data-recommend-nav="" href="//item.taobao.com/item.htm?spm=a230r.1.14.116.6f5477fcRxHSbK&amp;id=577188144949&amp;ns=1&amp;abbucket=4#detail" data-href="//item.taobao.com/item.htm?id=577188144949&amp;ns=1&amp;abbucket=4#detail" target="_blank" trace="msrp_auction" traceidx="17" trace-index="17" trace-nid="577188144949" trace-num="48" trace-price="4600.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.116">
          
            <img id="J_Itemlist_Pic_577188144949" class="J_ItemPic img" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/i3/82861840/O1CN01G4K49Z1PSlG8kLd9i_!!0-item_pic.jpg" alt="【三平路亚】ZPI 官改 SHIMANO 蒙塔尼 ZPRIDE Metanium HG" src="//g-search1.alicdn.com/img/bao/uploaded/i4/i3/82861840/O1CN01G4K49Z1PSlG8kLd9i_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.117.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=577188144949" data-spm-anchor-id="a230r.1.14.117">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.118.6f5477fcRxHSbK&amp;itemId=577188144949" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.118">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>4600.00</strong>
      </div>
      <div class="deal-cnt">3人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_577188144949" class="J_ClickStat" data-nid="577188144949" href="//item.taobao.com/item.htm?spm=a230r.1.14.119.6f5477fcRxHSbK&amp;id=577188144949&amp;ns=1&amp;abbucket=4#detail" target="_blank" trace="msrp_auction" traceidx="17" trace-index="17" trace-nid="577188144949" trace-num="48" trace-price="4600.00" trace-pid="" data-spm-anchor-id="a230r.1.14.119">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        【三平路亚】ZPI 官改 <span class="H">SHIMANO</span> 蒙塔尼 ZPRIDE Metanium HG
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="82861840" data-nid="577188144949" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.120.6f5477fcRxHSbK&amp;user_number_id=82861840" target="_blank" data-spm-anchor-id="a230r.1.14.120">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>binbinz77</span>
      </a>
    
  


        </div>
        <div class="location">江苏 无锡</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_577188144949">
        <ul class="icons">
          
          

          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="0">
              
                
                  <a href="//www.taobao.com/go/act/jpmj.php?spm=a230r.1.14.121.6f5477fcRxHSbK" target="_blank" trace="srpservice" traceidx="30" data-spm-anchor-id="a230r.1.14.121"><span class="icon-service-jinpaimaijia"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="binbinz77" data-display="inline" data-item="577188144949" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.122.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=binbinz77&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.122"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="18">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_528503982523" class="pic-link J_ClickStat J_ItemPicA" data-nid="528503982523" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.123.6f5477fcRxHSbK&amp;id=528503982523&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=528503982523&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="18" trace-index="18" trace-nid="528503982523" trace-num="48" trace-price="2600.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.123">
          
            <img id="J_Itemlist_Pic_528503982523" class="J_ItemPic img" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/TB13yVOMXXXXXbTXFXXXXXXXXXX_!!0-item_pic.jpg" alt="别誂翼3.6/4.5/5.4米SHIMANO禧玛诺碳素并继插节杆台钓鲫鱼竿" src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/TB13yVOMXXXXXbTXFXXXXXXXXXX_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.124.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=528503982523" data-spm-anchor-id="a230r.1.14.124">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.125.6f5477fcRxHSbK&amp;itemId=528503982523" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.125">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2600.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_528503982523" class="J_ClickStat" data-nid="528503982523" href="//detail.tmall.com/item.htm?spm=a230r.1.14.126.6f5477fcRxHSbK&amp;id=528503982523&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="18" trace-index="18" trace-nid="528503982523" trace-num="48" trace-price="2600.00" trace-pid="" data-spm-anchor-id="a230r.1.14.126">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        别誂翼3.6/4.5/5.4米<span class="H">SHIMANO</span>禧玛诺碳素并继插节杆台钓鲫鱼竿
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2261871593" data-nid="528503982523" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.127.6f5477fcRxHSbK&amp;user_number_id=2261871593" target="_blank" data-spm-anchor-id="a230r.1.14.127">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>普天元户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 南京</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_528503982523">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.128.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="31" data-spm-anchor-id="a230r.1.14.128"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="普天元户外专营店" data-display="inline" data-item="528503982523" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.129.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%99%AE%E5%A4%A9%E5%85%83%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-offline" data-spm-anchor-id="a230r.1.14.129"><span>旺旺离线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="19">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_554816567434" class="pic-link J_ClickStat J_ItemPicA" data-nid="554816567434" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.130.6f5477fcRxHSbK&amp;id=554816567434&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=554816567434&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="19" trace-index="19" trace-nid="554816567434" trace-num="48" trace-price="8999.90" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.130">
          
            <img id="J_Itemlist_Pic_554816567434" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/352469034/O1CN012GbcWvbE2hxbSEm_!!0-item_pic.jpg" alt="迪卡侬自行车公路赛车禧玛诺105大套内走线ULTRA900碳纤维RBTWIN" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/352469034/O1CN012GbcWvbE2hxbSEm_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.131.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=554816567434" data-spm-anchor-id="a230r.1.14.131">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.132.6f5477fcRxHSbK&amp;itemId=554816567434" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.132">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>8999.90</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_554816567434" class="J_ClickStat" data-nid="554816567434" href="//detail.tmall.com/item.htm?spm=a230r.1.14.133.6f5477fcRxHSbK&amp;id=554816567434&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="19" trace-index="19" trace-nid="554816567434" trace-num="48" trace-price="8999.90" trace-pid="" data-spm-anchor-id="a230r.1.14.133">
        
        迪卡侬自行车公路赛车禧玛诺105大套内走线ULTRA900碳纤维RBTWIN
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="352469034" data-nid="554816567434" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.134.6f5477fcRxHSbK&amp;user_number_id=352469034" target="_blank" data-spm-anchor-id="a230r.1.14.134">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>迪卡侬旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 苏州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_554816567434">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.135.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="32" data-spm-anchor-id="a230r.1.14.135"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="迪卡侬旗舰店" data-display="inline" data-item="554816567434" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.136.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E8%BF%AA%E5%8D%A1%E4%BE%AC%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.136"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="20">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_573363860752" class="pic-link J_ClickStat J_ItemPicA" data-nid="573363860752" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.137.6f5477fcRxHSbK&amp;id=573363860752&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=573363860752&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="20" trace-index="20" trace-nid="573363860752" trace-num="48" trace-price="1880.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.137">
          
            <img id="J_Itemlist_Pic_573363860752" class="J_ItemPic img" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i1/707662361/O1CN01xVctxD1TJNbhkpTo5_!!0-item_pic.jpg" alt="凤凰自行车 700c铝合金公路车禧玛诺变速成人弯把男女学生赛车" src="//g-search2.alicdn.com/img/bao/uploaded/i4/i1/707662361/O1CN01xVctxD1TJNbhkpTo5_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.138.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=573363860752" data-spm-anchor-id="a230r.1.14.138">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.139.6f5477fcRxHSbK&amp;itemId=573363860752" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.139">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1880.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_573363860752" class="J_ClickStat" data-nid="573363860752" href="//detail.tmall.com/item.htm?spm=a230r.1.14.140.6f5477fcRxHSbK&amp;id=573363860752&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="20" trace-index="20" trace-nid="573363860752" trace-num="48" trace-price="1880.00" trace-pid="" data-spm-anchor-id="a230r.1.14.140">
        
        凤凰自行车 700c铝合金公路车禧玛诺变速成人弯把男女学生赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="707662361" data-nid="573363860752" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.141.6f5477fcRxHSbK&amp;user_number_id=707662361" target="_blank" data-spm-anchor-id="a230r.1.14.141">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr equalthan"></span>
          
        </span>
        <span>凤凰官方旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 镇江</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_573363860752">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.142.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="33" data-spm-anchor-id="a230r.1.14.142"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="凤凰官方旗舰店" data-display="inline" data-item="573363860752" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.143.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%87%A4%E5%87%B0%E5%AE%98%E6%96%B9%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.143"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="21">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_562783470606" class="pic-link J_ClickStat J_ItemPicA" data-nid="562783470606" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.144.6f5477fcRxHSbK&amp;id=562783470606&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=562783470606&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="21" trace-index="21" trace-nid="562783470606" trace-num="48" trace-price="1399.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.144">
          
            <img id="J_Itemlist_Pic_562783470606" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/2938641077/O1CN01HIUDYA1JpJ3ln5rJD_!!0-item_pic.jpg" alt="喜德盛2018款公路车RX200公路自行车禧玛诺变速新品" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/2938641077/O1CN01HIUDYA1JpJ3ln5rJD_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.145.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=562783470606" data-spm-anchor-id="a230r.1.14.145">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.146.6f5477fcRxHSbK&amp;itemId=562783470606" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.146">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1399.00</strong>
      </div>
      <div class="deal-cnt">13人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_562783470606" class="J_ClickStat" data-nid="562783470606" href="//detail.tmall.com/item.htm?spm=a230r.1.14.147.6f5477fcRxHSbK&amp;id=562783470606&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="21" trace-index="21" trace-nid="562783470606" trace-num="48" trace-price="1399.00" trace-pid="" data-spm-anchor-id="a230r.1.14.147">
        
        喜德盛2018款公路车RX200公路自行车禧玛诺变速新品
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2938641077" data-nid="562783470606" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.148.6f5477fcRxHSbK&amp;user_number_id=2938641077" target="_blank" data-spm-anchor-id="a230r.1.14.148">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>喜德盛惠州专卖店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_562783470606">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.149.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="35" data-spm-anchor-id="a230r.1.14.149"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="2">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="喜德盛惠州专卖店" data-display="inline" data-item="562783470606" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.150.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%96%9C%E5%BE%B7%E7%9B%9B%E6%83%A0%E5%B7%9E%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.150"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="22">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_559515031211" class="pic-link J_ClickStat J_ItemPicA" data-nid="559515031211" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.151.6f5477fcRxHSbK&amp;id=559515031211&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=559515031211&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="22" trace-index="22" trace-nid="559515031211" trace-num="48" trace-price="2390.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.151">
          
            <img id="J_Itemlist_Pic_559515031211" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/3021506430/TB2amnyqDmWBKNjSZFBXXXxUFXa_!!3021506430-0-item_pic.jpg" alt="骓特猎人公路车自行车18/20/22速破风禧玛诺公路自行车碳纤维前叉" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/3021506430/TB2amnyqDmWBKNjSZFBXXXxUFXa_!!3021506430-0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.152.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=559515031211" data-spm-anchor-id="a230r.1.14.152">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.153.6f5477fcRxHSbK&amp;itemId=559515031211" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.153">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2390.00</strong>
      </div>
      <div class="deal-cnt">5人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_559515031211" class="J_ClickStat" data-nid="559515031211" href="//detail.tmall.com/item.htm?spm=a230r.1.14.154.6f5477fcRxHSbK&amp;id=559515031211&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="22" trace-index="22" trace-nid="559515031211" trace-num="48" trace-price="2390.00" trace-pid="" data-spm-anchor-id="a230r.1.14.154">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        骓特猎人公路车自行车18/20/22速破风禧玛诺公路自行车碳纤维前叉
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3021506430" data-nid="559515031211" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.155.6f5477fcRxHSbK&amp;user_number_id=3021506430" target="_blank" data-spm-anchor-id="a230r.1.14.155">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>twitter骓特旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_559515031211">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.156.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="38" data-spm-anchor-id="a230r.1.14.156"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="twitter骓特旗舰店" data-display="inline" data-item="559515031211" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.157.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=twitter%E9%AA%93%E7%89%B9%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.157"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="23">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_581088424469" class="pic-link J_ClickStat J_ItemPicA" data-nid="581088424469" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.158.6f5477fcRxHSbK&amp;id=581088424469&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=581088424469&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="23" trace-index="23" trace-nid="581088424469" trace-num="48" trace-price="60.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.158">
          
            <img id="J_Itemlist_Pic_581088424469" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/545841904/TB1xJaMm4jaK1RjSZKzXXXVwXXa_!!0-item_pic.jpg" alt="禧玛诺达瓦Daiwa电绞轮电轮 电绞 电源线 电线 渔轮充电器连接线" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/545841904/TB1xJaMm4jaK1RjSZKzXXXVwXXa_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.159.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=581088424469" data-spm-anchor-id="a230r.1.14.159">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.160.6f5477fcRxHSbK&amp;itemId=581088424469" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.160">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>60.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_581088424469" class="J_ClickStat" data-nid="581088424469" href="//detail.tmall.com/item.htm?spm=a230r.1.14.161.6f5477fcRxHSbK&amp;id=581088424469&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="23" trace-index="23" trace-nid="581088424469" trace-num="48" trace-price="60.00" trace-pid="" data-spm-anchor-id="a230r.1.14.161">
        
        禧玛诺达瓦Daiwa电绞轮电轮 电绞 电源线 电线 渔轮充电器连接线
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="545841904" data-nid="581088424469" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.162.6f5477fcRxHSbK&amp;user_number_id=545841904" target="_blank" data-spm-anchor-id="a230r.1.14.162">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>mcobeam旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_581088424469">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.163.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="39" data-spm-anchor-id="a230r.1.14.163"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="mcobeam旗舰店" data-display="inline" data-item="581088424469" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.164.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=mcobeam%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.164"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="24">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_580652568716" class="pic-link J_ClickStat J_ItemPicA" data-nid="580652568716" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.165.6f5477fcRxHSbK&amp;id=580652568716&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=580652568716&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="24" trace-index="24" trace-nid="580652568716" trace-num="48" trace-price="2199.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.165">
          
            <img id="J_Itemlist_Pic_580652568716" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/2938641077/O1CN01QMzvst1JpJ3n9qLo3_!!0-item_pic.jpg" alt="喜德盛公路车RX200Pro公路自行车16速禧玛诺变速成人弯把公路赛车" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/2938641077/O1CN01QMzvst1JpJ3n9qLo3_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.166.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=580652568716" data-spm-anchor-id="a230r.1.14.166">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.167.6f5477fcRxHSbK&amp;itemId=580652568716" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.167">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2199.00</strong>
      </div>
      <div class="deal-cnt">5人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_580652568716" class="J_ClickStat" data-nid="580652568716" href="//detail.tmall.com/item.htm?spm=a230r.1.14.168.6f5477fcRxHSbK&amp;id=580652568716&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="24" trace-index="24" trace-nid="580652568716" trace-num="48" trace-price="2199.00" trace-pid="" data-spm-anchor-id="a230r.1.14.168">
        
        喜德盛公路车RX200Pro公路自行车16速禧玛诺变速成人弯把公路赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2938641077" data-nid="580652568716" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.169.6f5477fcRxHSbK&amp;user_number_id=2938641077" target="_blank" data-spm-anchor-id="a230r.1.14.169">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>喜德盛惠州专卖店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_580652568716">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.170.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="40" data-spm-anchor-id="a230r.1.14.170"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="喜德盛惠州专卖店" data-display="inline" data-item="580652568716" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.171.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%96%9C%E5%BE%B7%E7%9B%9B%E6%83%A0%E5%B7%9E%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.171"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="25">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_545862198281" class="pic-link J_ClickStat J_ItemPicA" data-nid="545862198281" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.172.6f5477fcRxHSbK&amp;id=545862198281&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=545862198281&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="25" trace-index="25" trace-nid="545862198281" trace-num="48" trace-price="3999.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.172">
          
            <img id="J_Itemlist_Pic_545862198281" class="J_ItemPic img" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/2933153576/TB2oXkNqRmWBuNkSndVXXcsApXa_!!2933153576-0-item_pic.jpg" alt="Missile米赛尔禧玛诺22速碳纤维前叉公路自行车 破风5000公路单车" src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/2933153576/TB2oXkNqRmWBuNkSndVXXcsApXa_!!2933153576-0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.173.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=545862198281" data-spm-anchor-id="a230r.1.14.173">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.174.6f5477fcRxHSbK&amp;itemId=545862198281" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.174">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>3999.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_545862198281" class="J_ClickStat" data-nid="545862198281" href="//detail.tmall.com/item.htm?spm=a230r.1.14.175.6f5477fcRxHSbK&amp;id=545862198281&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="25" trace-index="25" trace-nid="545862198281" trace-num="48" trace-price="3999.00" trace-pid="" data-spm-anchor-id="a230r.1.14.175">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        Missile米赛尔禧玛诺22速碳纤维前叉公路自行车 破风5000公路单车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2933153576" data-nid="545862198281" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.176.6f5477fcRxHSbK&amp;user_number_id=2933153576" target="_blank" data-spm-anchor-id="a230r.1.14.176">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr equalthan"></span>
          
        </span>
        <span>美骑旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 广州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_545862198281">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.177.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="42" data-spm-anchor-id="a230r.1.14.177"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="美骑旗舰店" data-display="inline" data-item="545862198281" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.178.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E7%BE%8E%E9%AA%91%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.178"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="26">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_548645476589" class="pic-link J_ClickStat J_ItemPicA" data-nid="548645476589" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.179.6f5477fcRxHSbK&amp;id=548645476589&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=548645476589&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="26" trace-index="26" trace-nid="548645476589" trace-num="48" trace-price="999.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.179">
          
            <img id="J_Itemlist_Pic_548645476589" class="J_ItemPic img" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/435869263/O1CN01mg0PZQ2IIVFRSXLdP_!!0-item_pic.jpg" alt="千里达入门级铝合金弯把公路赛自行车700C轮跑长途竞速禧玛诺变速" src="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/435869263/O1CN01mg0PZQ2IIVFRSXLdP_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.180.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=548645476589" data-spm-anchor-id="a230r.1.14.180">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.181.6f5477fcRxHSbK&amp;itemId=548645476589" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.181">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>999.00</strong>
      </div>
      <div class="deal-cnt">21人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_548645476589" class="J_ClickStat" data-nid="548645476589" href="//detail.tmall.com/item.htm?spm=a230r.1.14.182.6f5477fcRxHSbK&amp;id=548645476589&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="26" trace-index="26" trace-nid="548645476589" trace-num="48" trace-price="999.00" trace-pid="" data-spm-anchor-id="a230r.1.14.182">
        
        千里达入门级铝合金弯把公路赛自行车700C轮跑长途竞速禧玛诺变速
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="435869263" data-nid="548645476589" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.183.6f5477fcRxHSbK&amp;user_number_id=435869263" target="_blank" data-spm-anchor-id="a230r.1.14.183">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>川野车品专营店</span>
      </a>
    
  


        </div>
        <div class="location">广东 汕头</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_548645476589">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.184.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="44" data-spm-anchor-id="a230r.1.14.184"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="川野车品专营店" data-display="inline" data-item="548645476589" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.185.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%B7%9D%E9%87%8E%E8%BD%A6%E5%93%81%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.185"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="27">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_585681489195" class="pic-link J_ClickStat J_ItemPicA" data-nid="585681489195" data-recommend-nav="" href="//item.taobao.com/item.htm?spm=a230r.1.14.186.6f5477fcRxHSbK&amp;id=585681489195&amp;ns=1&amp;abbucket=4#detail" data-href="//item.taobao.com/item.htm?id=585681489195&amp;ns=1&amp;abbucket=4#detail" target="_blank" trace="msrp_auction" traceidx="27" trace-index="27" trace-nid="585681489195" trace-num="48" trace-price="200.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.186">
          
            <img id="J_Itemlist_Pic_585681489195" class="J_ItemPic img" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/396922860/O1CN01pxVMUu1WzvEz8YPzH_!!0-item_pic.jpg" alt="livre 10周年限定版改装 摇臂 steez款左右通用款 shimano 达瓦" src="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/396922860/O1CN01pxVMUu1WzvEz8YPzH_!!0-item_pic.jpg_180x180.jpg">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.187.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=585681489195" data-spm-anchor-id="a230r.1.14.187">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.188.6f5477fcRxHSbK&amp;itemId=585681489195" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.188">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>200.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_585681489195" class="J_ClickStat" data-nid="585681489195" href="//item.taobao.com/item.htm?spm=a230r.1.14.189.6f5477fcRxHSbK&amp;id=585681489195&amp;ns=1&amp;abbucket=4#detail" target="_blank" trace="msrp_auction" traceidx="27" trace-index="27" trace-nid="585681489195" trace-num="48" trace-price="200.00" trace-pid="" data-spm-anchor-id="a230r.1.14.189">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        livre 10周年限定版改装 摇臂 steez款左右通用款 <span class="H">shimano</span> 达瓦
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="396922860" data-nid="585681489195" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.190.6f5477fcRxHSbK&amp;user_number_id=396922860" target="_blank" data-spm-anchor-id="a230r.1.14.190">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>王嗨520123</span>
      </a>
    
  


        </div>
        <div class="location">湖南 衡阳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_585681489195">
        <ul class="icons">
          
          

          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="0">
              
                
                  <a href="//www.taobao.com/go/act/jpmj.php?spm=a230r.1.14.191.6f5477fcRxHSbK" target="_blank" trace="srpservice" traceidx="46" data-spm-anchor-id="a230r.1.14.191"><span class="icon-service-jinpaimaijia"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="王嗨520123" data-display="inline" data-item="585681489195" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.192.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E7%8E%8B%E5%97%A8520123&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.192"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="28">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_575409159418" class="pic-link J_ClickStat J_ItemPicA" data-nid="575409159418" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.193.6f5477fcRxHSbK&amp;id=575409159418&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=575409159418&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="28" trace-index="28" trace-nid="575409159418" trace-num="48" trace-price="60.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.193">
          
            <img id="J_Itemlist_Pic_575409159418" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i1/2159532624/TB2zOm2m7omBKNjSZFqXXXtqVXa_!!2159532624-0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i1/2159532624/TB2zOm2m7omBKNjSZFqXXXtqVXa_!!2159532624-0-item_pic.jpg" alt="佳威 Jagwire shimano 碟刹来令片 刹车片 全系列齐全">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.194.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=575409159418" data-spm-anchor-id="a230r.1.14.194">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.195.6f5477fcRxHSbK&amp;itemId=575409159418" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.195">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>60.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_575409159418" class="J_ClickStat" data-nid="575409159418" href="//detail.tmall.com/item.htm?spm=a230r.1.14.196.6f5477fcRxHSbK&amp;id=575409159418&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="28" trace-index="28" trace-nid="575409159418" trace-num="48" trace-price="60.00" trace-pid="" data-spm-anchor-id="a230r.1.14.196">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        佳威 Jagwire <span class="H">shimano</span> 碟刹来令片 刹车片 全系列齐全
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2159532624" data-nid="575409159418" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.197.6f5477fcRxHSbK&amp;user_number_id=2159532624" target="_blank" data-spm-anchor-id="a230r.1.14.197">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>syb运动旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_575409159418">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.198.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="47" data-spm-anchor-id="a230r.1.14.198"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="syb运动旗舰店" data-display="inline" data-item="575409159418" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.199.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=syb%E8%BF%90%E5%8A%A8%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.199"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="29">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_534215210324" class="pic-link J_ClickStat J_ItemPicA" data-nid="534215210324" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.200.6f5477fcRxHSbK&amp;id=534215210324&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=534215210324&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="29" trace-index="29" trace-nid="534215210324" trace-num="48" trace-price="1199.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.200">
          
            <img id="J_Itemlist_Pic_534215210324" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/1882656020/O1CN012TnWDF1uLCoIoMyrH_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/1882656020/O1CN012TnWDF1uLCoIoMyrH_!!0-item_pic.jpg" alt="TRINX千里达乐驰禧玛诺21速700C轮胎公路车弯把公路赛车2018款">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.201.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=534215210324" data-spm-anchor-id="a230r.1.14.201">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.202.6f5477fcRxHSbK&amp;itemId=534215210324" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.202">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1199.00</strong>
      </div>
      <div class="deal-cnt">4人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_534215210324" class="J_ClickStat" data-nid="534215210324" href="//detail.tmall.com/item.htm?spm=a230r.1.14.203.6f5477fcRxHSbK&amp;id=534215210324&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="29" trace-index="29" trace-nid="534215210324" trace-num="48" trace-price="1199.00" trace-pid="" data-spm-anchor-id="a230r.1.14.203">
        
        TRINX千里达乐驰禧玛诺21速700C轮胎公路车弯把公路赛车2018款
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1882656020" data-nid="534215210324" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.204.6f5477fcRxHSbK&amp;user_number_id=1882656020" target="_blank" data-spm-anchor-id="a230r.1.14.204">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>trinx旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 广州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_534215210324">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.205.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="48" data-spm-anchor-id="a230r.1.14.205"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="trinx旗舰店" data-display="inline" data-item="534215210324" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.206.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=trinx%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.206"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="30">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_530314926864" class="pic-link J_ClickStat J_ItemPicA" data-nid="530314926864" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.207.6f5477fcRxHSbK&amp;id=530314926864&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=530314926864&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="30" trace-index="30" trace-nid="530314926864" trace-num="48" trace-price="3599.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.207">
          
            <img id="J_Itemlist_Pic_530314926864" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/892864479/TB1BucMtTXYBeNkHFrdXXciuVXa_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/892864479/TB1BucMtTXYBeNkHFrdXXciuVXa_!!0-item_pic.jpg" alt="sava萨瓦碳纤维公路车 18/20/22速禧玛诺竞速碳纤公路车战风">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.208.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=530314926864" data-spm-anchor-id="a230r.1.14.208">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.209.6f5477fcRxHSbK&amp;itemId=530314926864" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.209">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>3599.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_530314926864" class="J_ClickStat" data-nid="530314926864" href="//detail.tmall.com/item.htm?spm=a230r.1.14.210.6f5477fcRxHSbK&amp;id=530314926864&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="30" trace-index="30" trace-nid="530314926864" trace-num="48" trace-price="3599.00" trace-pid="" data-spm-anchor-id="a230r.1.14.210">
        
        sava萨瓦碳纤维公路车 18/20/22速禧玛诺竞速碳纤公路车战风
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="892864479" data-nid="530314926864" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.211.6f5477fcRxHSbK&amp;user_number_id=892864479" target="_blank" data-spm-anchor-id="a230r.1.14.211">
        <span class="dsrs">
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>洛克运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">广东 惠州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_530314926864">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.212.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="50" data-spm-anchor-id="a230r.1.14.212"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon " data-index="1">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="洛克运动户外专营店" data-display="inline" data-item="530314926864" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.213.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%B4%9B%E5%85%8B%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.213"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="31">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_571617025270" class="pic-link J_ClickStat J_ItemPicA" data-nid="571617025270" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.214.6f5477fcRxHSbK&amp;id=571617025270&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=571617025270&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="31" trace-index="31" trace-nid="571617025270" trace-num="48" trace-price="798.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.214">
          
            <img id="J_Itemlist_Pic_571617025270" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/2214830602/O1CN01P75N3r1GJkvhqeKWy_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/2214830602/O1CN01P75N3r1GJkvhqeKWy_!!0-item_pic.jpg" alt="VIRES复古公路自行车双刹车平把公路车赛车14级禧玛诺变速自行车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.215.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=571617025270" data-spm-anchor-id="a230r.1.14.215">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.216.6f5477fcRxHSbK&amp;itemId=571617025270" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.216">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>798.00</strong>
      </div>
      <div class="deal-cnt">31人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_571617025270" class="J_ClickStat" data-nid="571617025270" href="//detail.tmall.com/item.htm?spm=a230r.1.14.217.6f5477fcRxHSbK&amp;id=571617025270&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="31" trace-index="31" trace-nid="571617025270" trace-num="48" trace-price="798.00" trace-pid="" data-spm-anchor-id="a230r.1.14.217">
        
        VIRES复古公路自行车双刹车平把公路车赛车14级禧玛诺变速自行车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2214830602" data-nid="571617025270" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.218.6f5477fcRxHSbK&amp;user_number_id=2214830602" target="_blank" data-spm-anchor-id="a230r.1.14.218">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>vires旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">北京</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_571617025270">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.219.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="52" data-spm-anchor-id="a230r.1.14.219"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="2">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
            <li class="icon " data-index="3">
              
                
                  <span class="icon-fest-gongyibaobei" title="公益宝贝"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="vires旗舰店" data-display="inline" data-item="571617025270" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.220.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=vires%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.220"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="32">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_571425914261" class="pic-link J_ClickStat J_ItemPicA" data-nid="571425914261" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.221.6f5477fcRxHSbK&amp;id=571425914261&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=571425914261&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="32" trace-index="32" trace-nid="571425914261" trace-num="48" trace-price="5380.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.221">
          
            <img id="J_Itemlist_Pic_571425914261" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/3021506430/TB16xDxwKySBuNjy1zdXXXPxFXa_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/3021506430/TB16xDxwKySBuNjy1zdXXXPxFXa_!!0-item_pic.jpg" alt="骓特碳纤维自行车公路车22速赛车跑车男女公路赛突袭禧玛诺R8000">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.222.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=571425914261" data-spm-anchor-id="a230r.1.14.222">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.223.6f5477fcRxHSbK&amp;itemId=571425914261" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.223">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>5380.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_571425914261" class="J_ClickStat" data-nid="571425914261" href="//detail.tmall.com/item.htm?spm=a230r.1.14.224.6f5477fcRxHSbK&amp;id=571425914261&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="32" trace-index="32" trace-nid="571425914261" trace-num="48" trace-price="5380.00" trace-pid="" data-spm-anchor-id="a230r.1.14.224">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        骓特碳纤维自行车公路车22速赛车跑车男女公路赛突袭禧玛诺R8000
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3021506430" data-nid="571425914261" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.225.6f5477fcRxHSbK&amp;user_number_id=3021506430" target="_blank" data-spm-anchor-id="a230r.1.14.225">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>twitter骓特旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_571425914261">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.226.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="56" data-spm-anchor-id="a230r.1.14.226"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="twitter骓特旗舰店" data-display="inline" data-item="571425914261" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.227.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=twitter%E9%AA%93%E7%89%B9%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.227"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="33">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_578813877608" class="pic-link J_ClickStat J_ItemPicA" data-nid="578813877608" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.228.6f5477fcRxHSbK&amp;id=578813877608&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=578813877608&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="33" trace-index="33" trace-nid="578813877608" trace-num="48" trace-price="1880.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.228">
          
            <img id="J_Itemlist_Pic_578813877608" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/707662361/O1CN01MAlLvt1TJNbprroCj_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/707662361/O1CN01MAlLvt1TJNbprroCj_!!0-item_pic.jpg" alt="凤凰自行车 700c铝合金公路车禧玛诺变速成人弯把男女学生赛车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.229.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=578813877608" data-spm-anchor-id="a230r.1.14.229">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.230.6f5477fcRxHSbK&amp;itemId=578813877608" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.230">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1880.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_578813877608" class="J_ClickStat" data-nid="578813877608" href="//detail.tmall.com/item.htm?spm=a230r.1.14.231.6f5477fcRxHSbK&amp;id=578813877608&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="33" trace-index="33" trace-nid="578813877608" trace-num="48" trace-price="1880.00" trace-pid="" data-spm-anchor-id="a230r.1.14.231">
        
        凤凰自行车 700c铝合金公路车禧玛诺变速成人弯把男女学生赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="707662361" data-nid="578813877608" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.232.6f5477fcRxHSbK&amp;user_number_id=707662361" target="_blank" data-spm-anchor-id="a230r.1.14.232">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr equalthan"></span>
          
        </span>
        <span>凤凰官方旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 镇江</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_578813877608">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.233.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="57" data-spm-anchor-id="a230r.1.14.233"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="凤凰官方旗舰店" data-display="inline" data-item="578813877608" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.234.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%87%A4%E5%87%B0%E5%AE%98%E6%96%B9%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.234"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="34">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_585267374076" class="pic-link J_ClickStat J_ItemPicA" data-nid="585267374076" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.235.6f5477fcRxHSbK&amp;id=585267374076&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=585267374076&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="34" trace-index="34" trace-nid="585267374076" trace-num="48" trace-price="1960.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.235">
          
            <img id="J_Itemlist_Pic_585267374076" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/2081116161/O1CN011sxh4y1vNmetRVfTi_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/2081116161/O1CN011sxh4y1vNmetRVfTi_!!0-item_pic.jpg" alt="凤凰公路自行车700c铝合金禧玛诺变速青少年成人弯把男女学生赛车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.236.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=585267374076" data-spm-anchor-id="a230r.1.14.236">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.237.6f5477fcRxHSbK&amp;itemId=585267374076" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.237">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1960.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_585267374076" class="J_ClickStat" data-nid="585267374076" href="//detail.tmall.com/item.htm?spm=a230r.1.14.238.6f5477fcRxHSbK&amp;id=585267374076&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="34" trace-index="34" trace-nid="585267374076" trace-num="48" trace-price="1960.00" trace-pid="" data-spm-anchor-id="a230r.1.14.238">
        
        凤凰公路自行车700c铝合金禧玛诺变速青少年成人弯把男女学生赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2081116161" data-nid="585267374076" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.239.6f5477fcRxHSbK&amp;user_number_id=2081116161" target="_blank" data-spm-anchor-id="a230r.1.14.239">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>凤凰登泰专卖店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 镇江</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_585267374076">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.240.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="58" data-spm-anchor-id="a230r.1.14.240"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="凤凰登泰专卖店" data-display="inline" data-item="585267374076" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.241.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%87%A4%E5%87%B0%E7%99%BB%E6%B3%B0%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.241"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="35">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_578622332629" class="pic-link J_ClickStat J_ItemPicA" data-nid="578622332629" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.242.6f5477fcRxHSbK&amp;id=578622332629&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=578622332629&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="35" trace-index="35" trace-nid="578622332629" trace-num="48" trace-price="2199.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.242">
          
            <img id="J_Itemlist_Pic_578622332629" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/2397525548/O1CN012sui7C1qr1s3jsAJn_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/2397525548/O1CN012sui7C1qr1s3jsAJn_!!0-item_pic.jpg" alt="喜德盛公路车RX200Pro公路自行车16速禧玛诺变速成人弯把公路赛车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.243.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=578622332629" data-spm-anchor-id="a230r.1.14.243">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.244.6f5477fcRxHSbK&amp;itemId=578622332629" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.244">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2199.00</strong>
      </div>
      <div class="deal-cnt">4人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_578622332629" class="J_ClickStat" data-nid="578622332629" href="//detail.tmall.com/item.htm?spm=a230r.1.14.245.6f5477fcRxHSbK&amp;id=578622332629&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="35" trace-index="35" trace-nid="578622332629" trace-num="48" trace-price="2199.00" trace-pid="" data-spm-anchor-id="a230r.1.14.245">
        
        喜德盛公路车RX200Pro公路自行车16速禧玛诺变速成人弯把公路赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2397525548" data-nid="578622332629" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.246.6f5477fcRxHSbK&amp;user_number_id=2397525548" target="_blank" data-spm-anchor-id="a230r.1.14.246">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>xds喜德盛旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_578622332629">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.247.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="60" data-spm-anchor-id="a230r.1.14.247"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="2">
              
                
                  <span class="icon-service-baoxian"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="xds喜德盛旗舰店" data-display="inline" data-item="578622332629" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.248.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=xds%E5%96%9C%E5%BE%B7%E7%9B%9B%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.248"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="36">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_576709322241" class="pic-link J_ClickStat J_ItemPicA" data-nid="576709322241" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.249.6f5477fcRxHSbK&amp;id=576709322241&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=576709322241&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="36" trace-index="36" trace-nid="576709322241" trace-num="48" trace-price="1199.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.249">
          
            <img id="J_Itemlist_Pic_576709322241" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i1/435869263/O1CN01MtvEax2IIVF6ANzoX_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i1/435869263/O1CN01MtvEax2IIVF6ANzoX_!!0-item_pic.jpg" alt="千里达自由客700C直把平把公路自行车禧玛诺变速成人超轻赛跑车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.250.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=576709322241" data-spm-anchor-id="a230r.1.14.250">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.251.6f5477fcRxHSbK&amp;itemId=576709322241" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.251">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1199.00</strong>
      </div>
      <div class="deal-cnt">4人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_576709322241" class="J_ClickStat" data-nid="576709322241" href="//detail.tmall.com/item.htm?spm=a230r.1.14.252.6f5477fcRxHSbK&amp;id=576709322241&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="36" trace-index="36" trace-nid="576709322241" trace-num="48" trace-price="1199.00" trace-pid="" data-spm-anchor-id="a230r.1.14.252">
        
        千里达自由客700C直把平把公路自行车禧玛诺变速成人超轻赛跑车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="435869263" data-nid="576709322241" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.253.6f5477fcRxHSbK&amp;user_number_id=435869263" target="_blank" data-spm-anchor-id="a230r.1.14.253">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>川野车品专营店</span>
      </a>
    
  


        </div>
        <div class="location">广东 汕头</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_576709322241">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.254.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="63" data-spm-anchor-id="a230r.1.14.254"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="川野车品专营店" data-display="inline" data-item="576709322241" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.255.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%B7%9D%E9%87%8E%E8%BD%A6%E5%93%81%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.255"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="37">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_559448590669" class="pic-link J_ClickStat J_ItemPicA" data-nid="559448590669" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.256.6f5477fcRxHSbK&amp;id=559448590669&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=559448590669&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="37" trace-index="37" trace-nid="559448590669" trace-num="48" trace-price="2390.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.256">
          
            <img id="J_Itemlist_Pic_559448590669" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/1800185873/TB2TRQPqcIrBKNjSZK9XXagoVXa_!!1800185873-0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i4/1800185873/TB2TRQPqcIrBKNjSZK9XXagoVXa_!!1800185873-0-item_pic.jpg" alt="骓特猎人公路车自行车18/20/22速破风禧玛诺公路自行车碳纤维前叉">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.257.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=559448590669" data-spm-anchor-id="a230r.1.14.257">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.258.6f5477fcRxHSbK&amp;itemId=559448590669" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.258">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2390.00</strong>
      </div>
      <div class="deal-cnt">3人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_559448590669" class="J_ClickStat" data-nid="559448590669" href="//detail.tmall.com/item.htm?spm=a230r.1.14.259.6f5477fcRxHSbK&amp;id=559448590669&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="37" trace-index="37" trace-nid="559448590669" trace-num="48" trace-price="2390.00" trace-pid="" data-spm-anchor-id="a230r.1.14.259">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        骓特猎人公路车自行车18/20/22速破风禧玛诺公路自行车碳纤维前叉
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1800185873" data-nid="559448590669" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.260.6f5477fcRxHSbK&amp;user_number_id=1800185873" target="_blank" data-spm-anchor-id="a230r.1.14.260">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>菲仕特运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">广东 深圳</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_559448590669">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.261.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="65" data-spm-anchor-id="a230r.1.14.261"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="菲仕特运动户外专营店" data-display="inline" data-item="559448590669" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.262.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E8%8F%B2%E4%BB%95%E7%89%B9%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.262"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="38">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_586201770886" class="pic-link J_ClickStat J_ItemPicA" data-nid="586201770886" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.263.6f5477fcRxHSbK&amp;id=586201770886&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=586201770886&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="38" trace-index="38" trace-nid="586201770886" trace-num="48" trace-price="1898.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.263">
          
            <img id="J_Itemlist_Pic_586201770886" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/1749001929/O1CN01aK2XpL1Q7WVdL0DhI_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/1749001929/O1CN01aK2XpL1Q7WVdL0DhI_!!0-item_pic.jpg" alt="凤凰自行车700C铝合金公路车禧玛诺变速成人弯把男女式学生赛车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.264.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=586201770886" data-spm-anchor-id="a230r.1.14.264">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.265.6f5477fcRxHSbK&amp;itemId=586201770886" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.265">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1898.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_586201770886" class="J_ClickStat" data-nid="586201770886" href="//detail.tmall.com/item.htm?spm=a230r.1.14.266.6f5477fcRxHSbK&amp;id=586201770886&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="38" trace-index="38" trace-nid="586201770886" trace-num="48" trace-price="1898.00" trace-pid="" data-spm-anchor-id="a230r.1.14.266">
        
        凤凰自行车700C铝合金公路车禧玛诺变速成人弯把男女式学生赛车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1749001929" data-nid="586201770886" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.267.6f5477fcRxHSbK&amp;user_number_id=1749001929" target="_blank" data-spm-anchor-id="a230r.1.14.267">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>凤凰百世恒通专卖店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 镇江</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_586201770886">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.268.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="66" data-spm-anchor-id="a230r.1.14.268"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="凤凰百世恒通专卖店" data-display="inline" data-item="586201770886" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.269.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E5%87%A4%E5%87%B0%E7%99%BE%E4%B8%96%E6%81%92%E9%80%9A%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.269"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="39">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_568878289438" class="pic-link J_ClickStat J_ItemPicA" data-nid="568878289438" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.270.6f5477fcRxHSbK&amp;id=568878289438&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=568878289438&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="39" trace-index="39" trace-nid="568878289438" trace-num="48" trace-price="1698.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.270">
          
            <img id="J_Itemlist_Pic_568878289438" class="J_ItemPic img" data-ks-lazyload="//g-search1.alicdn.com/img/bao/uploaded/i4/i1/3921210672/O1CN010adXpY1GpoecbrQe1_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search1.alicdn.com/img/bao/uploaded/i4/i1/3921210672/O1CN010adXpY1GpoecbrQe1_!!0-item_pic.jpg" alt="喜德盛新款RX280公路车 铝合金车架禧玛诺前后变速久裕铝合金花鼓">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.271.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=568878289438" data-spm-anchor-id="a230r.1.14.271">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.272.6f5477fcRxHSbK&amp;itemId=568878289438" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.272">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1698.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_568878289438" class="J_ClickStat" data-nid="568878289438" href="//detail.tmall.com/item.htm?spm=a230r.1.14.273.6f5477fcRxHSbK&amp;id=568878289438&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="39" trace-index="39" trace-nid="568878289438" trace-num="48" trace-price="1698.00" trace-pid="" data-spm-anchor-id="a230r.1.14.273">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        喜德盛新款RX280公路车 铝合金车架禧玛诺前后变速久裕铝合金花鼓
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3921210672" data-nid="568878289438" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.274.6f5477fcRxHSbK&amp;user_number_id=3921210672" target="_blank" data-spm-anchor-id="a230r.1.14.274">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>xds喜德盛门莱京美专卖店</span>
      </a>
    
  


        </div>
        <div class="location">北京</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_568878289438">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.275.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="68" data-spm-anchor-id="a230r.1.14.275"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="xds喜德盛门莱京美专卖店" data-display="inline" data-item="568878289438" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.276.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=xds%E5%96%9C%E5%BE%B7%E7%9B%9B%E9%97%A8%E8%8E%B1%E4%BA%AC%E7%BE%8E%E4%B8%93%E5%8D%96%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.276"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="40">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_559309237523" class="pic-link J_ClickStat J_ItemPicA" data-nid="559309237523" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.277.6f5477fcRxHSbK&amp;id=559309237523&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=559309237523&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="40" trace-index="40" trace-nid="559309237523" trace-num="48" trace-price="1059.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.277">
          
            <img id="J_Itemlist_Pic_559309237523" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/3066268629/O1CN01hRvT7W2Dc89KdyLIc_!!3066268629.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i2/3066268629/O1CN01hRvT7W2Dc89KdyLIc_!!3066268629.jpg" alt="邦德富士达平把公路车自行车27速禧玛诺变速车男女式学生车单车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.278.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=559309237523" data-spm-anchor-id="a230r.1.14.278">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.279.6f5477fcRxHSbK&amp;itemId=559309237523" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.279">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1059.00</strong>
      </div>
      <div class="deal-cnt">6人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_559309237523" class="J_ClickStat" data-nid="559309237523" href="//detail.tmall.com/item.htm?spm=a230r.1.14.280.6f5477fcRxHSbK&amp;id=559309237523&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="40" trace-index="40" trace-nid="559309237523" trace-num="48" trace-price="1059.00" trace-pid="" data-spm-anchor-id="a230r.1.14.280">
        
        邦德富士达平把公路车自行车27速禧玛诺变速车男女式学生车单车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3066268629" data-nid="559309237523" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.281.6f5477fcRxHSbK&amp;user_number_id=3066268629" target="_blank" data-spm-anchor-id="a230r.1.14.281">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>邦德富士达运动旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">上海</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_559309237523">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.282.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="69" data-spm-anchor-id="a230r.1.14.282"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="邦德富士达运动旗舰店" data-display="inline" data-item="559309237523" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.283.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E9%82%A6%E5%BE%B7%E5%AF%8C%E5%A3%AB%E8%BE%BE%E8%BF%90%E5%8A%A8%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.283"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="41">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_563997783052" class="pic-link J_ClickStat J_ItemPicA" data-nid="563997783052" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.284.6f5477fcRxHSbK&amp;id=563997783052&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=563997783052&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="41" trace-index="41" trace-nid="563997783052" trace-num="48" trace-price="4999.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.284">
          
            <img id="J_Itemlist_Pic_563997783052" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/2948452353/O1CN01s2gX8Z1TFiQ0Yz0WJ_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/2948452353/O1CN01s2gX8Z1TFiQ0Yz0WJ_!!0-item_pic.jpg" alt="ZGL碳纤维公路车弯把赛车20速禧玛诺4700套件专业入门级挑战G1">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.285.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=563997783052" data-spm-anchor-id="a230r.1.14.285">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.286.6f5477fcRxHSbK&amp;itemId=563997783052" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.286">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>4999.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_563997783052" class="J_ClickStat" data-nid="563997783052" href="//detail.tmall.com/item.htm?spm=a230r.1.14.287.6f5477fcRxHSbK&amp;id=563997783052&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="41" trace-index="41" trace-nid="563997783052" trace-num="48" trace-price="4999.00" trace-pid="" data-spm-anchor-id="a230r.1.14.287">
        
        ZGL碳纤维公路车弯把赛车20速禧玛诺4700套件专业入门级挑战G1
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2948452353" data-nid="563997783052" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.288.6f5477fcRxHSbK&amp;user_number_id=2948452353" target="_blank" data-spm-anchor-id="a230r.1.14.288">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>zgl运动旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 连云港</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_563997783052">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.289.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="71" data-spm-anchor-id="a230r.1.14.289"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="zgl运动旗舰店" data-display="inline" data-item="563997783052" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.290.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=zgl%E8%BF%90%E5%8A%A8%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.290"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="42">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_585267218036" class="pic-link J_ClickStat J_ItemPicA" data-nid="585267218036" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.291.6f5477fcRxHSbK&amp;id=585267218036&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=585267218036&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="42" trace-index="42" trace-nid="585267218036" trace-num="48" trace-price="1960.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.291">
          
            <img id="J_Itemlist_Pic_585267218036" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/1999821572/O1CN01e9IeHc1NU176Q55Nl_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/1999821572/O1CN01e9IeHc1NU176Q55Nl_!!0-item_pic.jpg" alt="凤凰自行车700c铝合金公路车禧玛诺变速成人弯把男女学生赛车单车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.292.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=585267218036" data-spm-anchor-id="a230r.1.14.292">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.293.6f5477fcRxHSbK&amp;itemId=585267218036" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.293">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>1960.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_585267218036" class="J_ClickStat" data-nid="585267218036" href="//detail.tmall.com/item.htm?spm=a230r.1.14.294.6f5477fcRxHSbK&amp;id=585267218036&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="42" trace-index="42" trace-nid="585267218036" trace-num="48" trace-price="1960.00" trace-pid="" data-spm-anchor-id="a230r.1.14.294">
        
        凤凰自行车700c铝合金公路车禧玛诺变速成人弯把男女学生赛车单车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1999821572" data-nid="585267218036" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.295.6f5477fcRxHSbK&amp;user_number_id=1999821572" target="_blank" data-spm-anchor-id="a230r.1.14.295">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>传世百旺运动专营店</span>
      </a>
    
  


        </div>
        <div class="location">江苏 镇江</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_585267218036">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.296.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="72" data-spm-anchor-id="a230r.1.14.296"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="传世百旺运动专营店" data-display="inline" data-item="585267218036" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.297.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E4%BC%A0%E4%B8%96%E7%99%BE%E6%97%BA%E8%BF%90%E5%8A%A8%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.297"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="43">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_563939553579" class="pic-link J_ClickStat J_ItemPicA" data-nid="563939553579" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.298.6f5477fcRxHSbK&amp;id=563939553579&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=563939553579&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="43" trace-index="43" trace-nid="563939553579" trace-num="48" trace-price="36.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.298">
          
            <img id="J_Itemlist_Pic_563939553579" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/1752148683/TB1ZBENmdfJ8KJjy0FeXXXKEXXa_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i3/1752148683/TB1ZBENmdfJ8KJjy0FeXXXKEXXa_!!0-item_pic.jpg" alt="GUB自行车山地车油刹橄榄头油针套装 接头油刹禧玛诺刹车油管套装">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.299.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=563939553579" data-spm-anchor-id="a230r.1.14.299">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.300.6f5477fcRxHSbK&amp;itemId=563939553579" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.300">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>36.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_563939553579" class="J_ClickStat" data-nid="563939553579" href="//detail.tmall.com/item.htm?spm=a230r.1.14.301.6f5477fcRxHSbK&amp;id=563939553579&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="43" trace-index="43" trace-nid="563939553579" trace-num="48" trace-price="36.00" trace-pid="" data-spm-anchor-id="a230r.1.14.301">
        
        GUB自行车山地车油刹橄榄头油针套装 接头油刹禧玛诺刹车油管套装
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1752148683" data-nid="563939553579" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.302.6f5477fcRxHSbK&amp;user_number_id=1752148683" target="_blank" data-spm-anchor-id="a230r.1.14.302">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr equalthan"></span>
          
        </span>
        <span>汉兰运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">浙江 杭州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_563939553579">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.303.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="73" data-spm-anchor-id="a230r.1.14.303"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="汉兰运动户外专营店" data-display="inline" data-item="563939553579" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.304.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%B1%89%E5%85%B0%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.304"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="44">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_44604880675" class="pic-link J_ClickStat J_ItemPicA" data-nid="44604880675" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.305.6f5477fcRxHSbK&amp;id=44604880675&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=44604880675&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="44" trace-index="44" trace-nid="44604880675" trace-num="48" trace-price="36.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.305">
          
            <img id="J_Itemlist_Pic_44604880675" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/1884894921/TB1W4.AcJLO8KJjSZPcXXaV0FXa_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/1884894921/TB1W4.AcJLO8KJjSZPcXXaV0FXa_!!0-item_pic.jpg" alt="台湾Bike hand一体中轴牙盘拆装工具 shimano SRAM曲柄盖螺丝工具">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.306.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=44604880675" data-spm-anchor-id="a230r.1.14.306">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.307.6f5477fcRxHSbK&amp;itemId=44604880675" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.307">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>36.00</strong>
      </div>
      <div class="deal-cnt">2人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_44604880675" class="J_ClickStat" data-nid="44604880675" href="//detail.tmall.com/item.htm?spm=a230r.1.14.308.6f5477fcRxHSbK&amp;id=44604880675&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="44" trace-index="44" trace-nid="44604880675" trace-num="48" trace-price="36.00" trace-pid="" data-spm-anchor-id="a230r.1.14.308">
        
        台湾Bike hand一体中轴牙盘拆装工具 <span class="H">shimano</span> SRAM曲柄盖螺丝工具
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="1884894921" data-nid="44604880675" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.309.6f5477fcRxHSbK&amp;user_number_id=1884894921" target="_blank" data-spm-anchor-id="a230r.1.14.309">
        <span class="dsrs">
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>果壳户外运动专营店</span>
      </a>
    
  


        </div>
        <div class="location">江西 景德镇</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_44604880675">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.310.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="74" data-spm-anchor-id="a230r.1.14.310"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="果壳户外运动专营店" data-display="inline" data-item="44604880675" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.311.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%9E%9C%E5%A3%B3%E6%88%B7%E5%A4%96%E8%BF%90%E5%8A%A8%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.311"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="45">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_42770726508" class="pic-link J_ClickStat J_ItemPicA" data-nid="42770726508" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.312.6f5477fcRxHSbK&amp;id=42770726508&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=42770726508&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="45" trace-index="45" trace-nid="42770726508" trace-num="48" trace-price="38.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.312">
          
            <img id="J_Itemlist_Pic_42770726508" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/TB1KRZFGpXXXXXPXVXXXXXXXXXX_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i4/TB1KRZFGpXXXXXPXVXXXXXXXXXX_!!0-item_pic.jpg" alt="台湾Bike hand一体中轴牙盘拆装工具 shimano SRAM曲柄盖螺丝工具">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.313.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=42770726508" data-spm-anchor-id="a230r.1.14.313">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.314.6f5477fcRxHSbK&amp;itemId=42770726508" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.314">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>38.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_42770726508" class="J_ClickStat" data-nid="42770726508" href="//detail.tmall.com/item.htm?spm=a230r.1.14.315.6f5477fcRxHSbK&amp;id=42770726508&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="45" trace-index="45" trace-nid="42770726508" trace-num="48" trace-price="38.00" trace-pid="" data-spm-anchor-id="a230r.1.14.315">
        
        台湾Bike hand一体中轴牙盘拆装工具 <span class="H">shimano</span> SRAM曲柄盖螺丝工具
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="2264254638" data-nid="42770726508" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.316.6f5477fcRxHSbK&amp;user_number_id=2264254638" target="_blank" data-spm-anchor-id="a230r.1.14.316">
        <span class="dsrs">
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>路凡户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">上海</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_42770726508">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.317.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="75" data-spm-anchor-id="a230r.1.14.317"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="路凡户外专营店" data-display="inline" data-item="42770726508" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.318.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E8%B7%AF%E5%87%A1%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.318"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="46">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_567334952675" class="pic-link J_ClickStat J_ItemPicA" data-nid="567334952675" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.319.6f5477fcRxHSbK&amp;id=567334952675&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=567334952675&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="46" trace-index="46" trace-nid="567334952675" trace-num="48" trace-price="2698.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.319">
          
            <img id="J_Itemlist_Pic_567334952675" class="J_ItemPic img" data-ks-lazyload="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/3683191503/O1CN01Bwc0xF1MyPmaHU0uR_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search2.alicdn.com/img/bao/uploaded/i4/i3/3683191503/O1CN01Bwc0xF1MyPmaHU0uR_!!0-item_pic.jpg" alt="店主推荐 UCC  索尼克1 禧玛诺16速 铝合金公路车架 休闲通勤车">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.320.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=567334952675" data-spm-anchor-id="a230r.1.14.320">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.321.6f5477fcRxHSbK&amp;itemId=567334952675" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.321">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>2698.00</strong>
      </div>
      <div class="deal-cnt">1人付款</div>
      
        <div class="ship icon-service-free"></div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_567334952675" class="J_ClickStat" data-nid="567334952675" href="//detail.tmall.com/item.htm?spm=a230r.1.14.322.6f5477fcRxHSbK&amp;id=567334952675&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="46" trace-index="46" trace-nid="567334952675" trace-num="48" trace-price="2698.00" trace-pid="" data-spm-anchor-id="a230r.1.14.322">
        
        <span class="baoyou-intitle icon-service-free"></span>
        
        店主推荐 UCC  索尼克1 禧玛诺16速 铝合金公路车架 休闲通勤车
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3683191503" data-nid="567334952675" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.323.6f5477fcRxHSbK&amp;user_number_id=3683191503" target="_blank" data-spm-anchor-id="a230r.1.14.323">
        <span class="dsrs">
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
            <span class="dsr morethan"></span>
          
        </span>
        <span>ucc运动旗舰店</span>
      </a>
    
  


        </div>
        <div class="location">广东 广州</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_567334952675">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.324.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="76" data-spm-anchor-id="a230r.1.14.324"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="ucc运动旗舰店" data-display="inline" data-item="567334952675" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.325.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=ucc%E8%BF%90%E5%8A%A8%E6%97%97%E8%88%B0%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.325"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
        
          

<div class="item J_MouserOnverReq  " data-category="auctions" data-index="47">
  <div class="pic-box J_MouseEneterLeave J_PicBox">
    <div class="pic-box-inner">
      <div class="pic">
        <a id="J_Itemlist_PLink_564167041730" class="pic-link J_ClickStat J_ItemPicA" data-nid="564167041730" data-recommend-nav="" href="//detail.tmall.com/item.htm?spm=a230r.1.14.326.6f5477fcRxHSbK&amp;id=564167041730&amp;ns=1&amp;abbucket=4" data-href="//detail.tmall.com/item.htm?id=564167041730&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="47" trace-index="47" trace-nid="564167041730" trace-num="48" trace-price="4098.00" trace-recommend-nav="" trace-risk="" trace-pid="" data-spm-anchor-id="a230r.1.14.326">
          
            <img id="J_Itemlist_Pic_564167041730" class="J_ItemPic img" data-ks-lazyload="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/3191947551/TB1MR5eoxHI8KJjy1zbXXaxdpXa_!!0-item_pic.jpg_180x180.jpg" data-src="//g-search3.alicdn.com/img/bao/uploaded/i4/i1/3191947551/TB1MR5eoxHI8KJjy1zbXXaxdpXa_!!0-item_pic.jpg" alt="永久弯把碳纤维超轻公路车18/22速自行车700C赛车禧玛诺单车G77-1">
          
        </a>
      </div>

      
        
  <div class="similars">
    <a class="btn disabled" target="_blank">
        <s class="shim"></s>
        <s class="bar"></s>
        <span class="text">找同款</span>
    </a>

    <a class="btn " target="_blank" href="/search?spm=a230r.1.14.327.6f5477fcRxHSbK&amp;type=similar&amp;app=i2i&amp;rec_type=1&amp;uniqpid=&amp;nid=564167041730" data-spm-anchor-id="a230r.1.14.327">
        <s class="shim"></s>
        <span class="text">找相似</span>
    </a>
  </div>



      

      <div class="report">
        <a href="//jubao.taobao.com/index.htm?spm=a230r.1.14.328.6f5477fcRxHSbK&amp;itemId=564167041730" target="_blank" title="举报该宝贝" data-spm-anchor-id="a230r.1.14.328">
          <span class="icon-btn-report"></span>
        </a>
      </div>

    </div>

    
  </div>
  

  <div class="ctx-box J_MouseEneterLeave J_IconMoreNew">

    <div class="row row-1 g-clearfix">
    
      
      <div class="price g_price g_price-highlight">
        <span>¥</span><strong>4098.00</strong>
      </div>
      <div class="deal-cnt">0人付款</div>
      
    
    </div>

    <div class="row row-2 title">
      <a id="J_Itemlist_TLink_564167041730" class="J_ClickStat" data-nid="564167041730" href="//detail.tmall.com/item.htm?spm=a230r.1.14.329.6f5477fcRxHSbK&amp;id=564167041730&amp;ns=1&amp;abbucket=4" target="_blank" trace="msrp_auction" traceidx="47" trace-index="47" trace-nid="564167041730" trace-num="48" trace-price="4098.00" trace-pid="" data-spm-anchor-id="a230r.1.14.329">
        
        永久弯把碳纤维超轻公路车18/22速自行车700C赛车禧玛诺单车G77-1
      </a>
    </div>

    
      <div class="row row-3 g-clearfix">
        <div class="shop">
          

  
    
      <a class="shopname J_MouseEneterLeave J_ShopInfo" data-userid="3191947551" data-nid="564167041730" href="//store.taobao.com/shop/view_shop.htm?spm=a230r.1.14.330.6f5477fcRxHSbK&amp;user_number_id=3191947551" target="_blank" data-spm-anchor-id="a230r.1.14.330">
        <span class="dsrs">
          
            <span class="dsr equalthan"></span>
          
            <span class="dsr lessthan"></span>
          
            <span class="dsr lessthan"></span>
          
        </span>
        <span>梢映运动户外专营店</span>
      </a>
    
  


        </div>
        <div class="location">上海</div>
      </div>
    

    <div class="row row-4 g-clearfix">
      
      <div class="feature-icons icon-has-more" id="J_Itemlist_Icons_564167041730">
        <ul class="icons">
          
          

          
            <li class="icon " data-index="0">
              
                
                  <a href="//www.tmall.com/?spm=a230r.1.14.331.6f5477fcRxHSbK" target="_blank" title="尚天猫，就购了" trace="srpservice" traceidx="77" data-spm-anchor-id="a230r.1.14.331"><span class="icon-service-tianmao"></span></a>
                
              
            </li>
          
            <li class="icon J_IconPopup J_MouseEneterLeave" data-index="1">
              
                
                  <span class="icon-service-fuwu"></span>
                
              
            </li>
          
        </ul>
      </div>

      
        <div class="wangwang">
          <span class="ww-light ww-small" data-nick="梢映运动户外专营店" data-display="inline" data-item="564167041730" data-icon="small" data-encode="true"><a href="https://amos.alicdn.com/getcid.aw?spm=a230r.1.14.332.6f5477fcRxHSbK&amp;v=3&amp;groupid=0&amp;s=1&amp;charset=utf-8&amp;uid=%E6%A2%A2%E6%98%A0%E8%BF%90%E5%8A%A8%E6%88%B7%E5%A4%96%E4%B8%93%E8%90%A5%E5%BA%97&amp;site=cntaobao&amp;fromid=cntaobao东京花酱" target="_blank" class="ww-inline ww-online" title="点此可以直接和卖家交流选好的宝贝，或相互交流网购体验，还支持语音视频噢。" data-spm-anchor-id="a230r.1.14.332"><span>旺旺在线</span></a></span>
        </div>
      
    </div>

  </div>

  
</div>

        
      
    </div>

    

    <div class="items" id="J_itemlistCont">
      
      

      
    </div>
  </div>
</div>
</div>
	 */
}
