package com.walk_nie.myvideotr;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.seleniumhq.jetty9.util.StringUtil;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;
import com.walk_nie.util.NieConfig;
import com.walk_nie.util.NieUtil;

public class WeiboTr {
	String myfavURL = "https://www.weibo.com/like/outbox?leftnav=1";
	public static void main(String[] args) throws IOException {
		WeiboTr weibo = new WeiboTr();
		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		weibo.scan(driver);
	}
	public void publish(WebDriver driver, MyVideoObject uploadObj) {
		driver.get(myfavURL);
		logonWeibo(driver);

		List<File> multimediaContext = getToPublishFile(uploadObj);
		for(File f:multimediaContext){
			if(isVideoFile(f)){
				uploadVideo(driver, uploadObj, f);
			}else if(isVideoFile(f)){
				uploadPhoto(driver, uploadObj, f);
			}
		}

		WebElement elMain = driver.findElement(By.id("plc_main"));
		
		List<WebElement> elA = elMain.findElements(By.tagName("a"));
		for (WebElement el : elA) {
			if (el.getText().equals("完成")) {
				el.click();
				break;
			}
		}

		for (WebElement el : elA) {
			String attr = el.getAttribute("title");
			if (attr != null && attr.equals("发布微博按钮")) {
				el.click();
				break;
			}
		}
		
	}
	private void uploadPhoto(WebDriver driver, MyVideoObject uploadObj, File f) {
		// TODO
	}
	private void uploadVideo(WebDriver driver, MyVideoObject uploadObj, File f) {

		List<WebElement> elInputs = driver.findElements(By.tagName("input"));
		for (WebElement el : elInputs) {
			if (el.getAttribute("id").startsWith("publisher_upvideo")) {
				el.sendKeys(f.getAbsolutePath());
				break;
			}
		}

		WebDriverWait wait1 = new WebDriverWait(driver,120);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					List<WebElement> elspans = driver.findElements(By.tagName("dl"));
					for (WebElement el : elspans) {
						String attr = el.getAttribute("node-type");
						if (attr != null && attr.equals("uploading")) {
							String attr1 = el.getAttribute("style");
							if (attr1 != null && attr1.indexOf("block") == -1)
							break;
						}
					}
					return Boolean.TRUE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
/*
<div class="gn_header clearfix">
        <div class="gn_logo" node-type="logo" data-logotype="logo" data-logourl="//weibo.com?topnav=1&amp;mod=logo">
       	 <a bpfilter="main" href="//weibo.com/u/1449729883/home?topnav=1&amp;wvr=6" class="box" title="" node-type="logolink" suda-uatrack="key=topnav_tab&amp;value=weibologo">
                       	<span class="logo"></span>
                    	</a>
        </div>
                           <div class=" gn_search_v2">
                    	<span class=" placeholder">大家正在搜：变美是最好的复仇</span><input node-type="searchInput" autocomplete="nope" value="" class="W_input" name="15577635968248" type="text">
        	<a href="javascript:void(0);" title="搜索" node-type="searchSubmit" class="W_ficon ficon_search S_ficon" suda-uatrack="key=topnav_tab&amp;value=search">f</a> 
        	<!--搜索热词下拉-->
	        <div class="gn_topmenulist_search" node-type="searchSuggest" style="display:none;">
		        <div class="gn_topmenulist">
			        <div node-type="basic"></div>
			        <div node-type="plus"></div>
		        </div>
	        </div>
          <!--/搜索热词下拉-->
        </div>       
        <div class="gn_position">
	        <div class="gn_nav">
	          <ul class="gn_nav_list">
	          	<li><a bpfilter="main" href="//weibo.com/u/1449729883/home?topnav=1&amp;wvr=6" nm="home" class="S_txt1 home" suda-uatrack="key=topnav_tab&amp;value=homepage"><em class="W_ficon ficon_home S_ficon">E</em><em class="S_txt1">首页</em></a></li>
                                        <li><a href="https://krcom.cn" nm="tv" class="S_txt1" suda-uatrack="key=topnav_tab&amp;value=video"><em class="W_ficon ficon_video_v2 S_ficon"></em><em class="S_txt1">视频</em><em class="W_new"></em></a></li>
                                        <li><a dot="pos55b9e1ad88ae4" href="//d.weibo.com/?topnav=1&amp;mod=logo&amp;wvr=6" nm="find" class="S_txt1" suda-uatrack="key=topnav_tab&amp;value=discover"><em class="W_ficon ficon_found S_ficon">F</em><em class="S_txt1">发现</em></a></li>
                  	                 <li><a href="http://game.weibo.com?topnav=1&amp;mod=logo&amp;wvr=6" nm="game" class="S_txt1" suda-uatrack="key=topnav_tab&amp;value=game" target="_blank"><em class="W_ficon ficon_game S_ficon">G</em><em class="S_txt1">游戏</em></a></li>
                                    <li><a dot="pos55b9e0848171d" bpfilter="page_frame" href="//weibo.com/1449729883/profile?topnav=1&amp;wvr=6" nm="name" class="gn_name" suda-uatrack="key=topnav_tab&amp;value=profile"><em class="W_ficon ficon_user S_ficon">H</em><em class="S_txt1">次郎花子</em></a></li>
	          </ul>
	        </div>
	        <div class="gn_set S_line1">
	          <div class="gn_set_list"><a href="javascript:void(0);" node-type="msg"><em class="W_ficon ficon_mail S_ficon">I</em></a>
	            <div class="gn_topmenulist gn_topmenulist_notice" node-type="msgLayer" style="display:none">
	            </div>
	            <div class="gn_topmenulist_tips" style="display:none" node-type="tips"></div>
	          </div>
	          <div class="gn_set_list"><a dot="pos55baf61a68b21" href="javascript:void(0);" node-type="account" nm="account" class=" "><em class="W_ficon ficon_set S_ficon">*</em> </a>
	            <div class="gn_topmenulist gn_topmenulist_set " node-type="accountLayer" style="display: none;"><!--data start--><ul><li><a dot="pos55b99b65482fe" href="//account.weibo.com/set/index?topnav=1&amp;wvr=6" suda-data="key=account_setup&amp;value=account_setup">帐号设置</a></li><li><a dot="pos55b99bf4bba8f" target="_top" href="http://vip.weibo.com?topnav=1&amp;wvr=6" suda-data="top_account&amp;value=member_center">会员中心</a></li><li><a dot="pos55b9df2cc0557" target="_top" href="http://verified.weibo.com/verify?topnav=1&amp;wvr=6" suda-data="key=pc_apply_entry&amp;value=home_top_entrance">V认证</a></li> <li><a dot="pos55b9df5d53f10" target="_top" href="//security.weibo.com/account/security?topnav=1&amp;wvr=6" suda-data="key=account_setup&amp;value=account_safe">帐号安全</a></li> <li><a dot="pos55b9df9b80cae" target="_top" href="//account.weibo.com/set/privacy?topnav=1&amp;wvr=6" suda-data="key=account_setup&amp;value=privacy_setup">隐私设置</a></li><li><a target="_top" href="//account.weibo.com/set/shield?topnav=1&amp;wvr=6">屏蔽设置</a></li><li><a dot="pos55b9dfb6dd2f7" href="//account.weibo.com/set/message?topnav=1&amp;wvr=6" suda-data="key=account_setup&amp;value=notice_setup">消息设置</a></li><li><a dot="pos55b9dfda375ef" href="http://help.weibo.com/?topnav=1&amp;wvr=6" suda-data="key=account_setup&amp;value=helpcenter">帮助中心</a></li><li class="line S_line1"></li><li class="gn_func"><a target="_top" suda-data="key=account_setup&amp;value=quit" href="//weibo.com/logout.php?backurl=%2F">退出</a></li></ul><div class="W_layer_arrow"><span class="W_arrow_bor W_arrow_bor_t"><i class="S_line3"></i><em class="S_bg2_br"></em></span></div></div>
	          </div>
	          <div class="gn_set_list"><a href="javascript:void(0);" node-type="publish" action-type="widget_publish" action-data="title=有什么新鲜事想告诉大家?" suda-data="key=quick_pubblog&amp;value=quickpub_entrance" data-widget-publish="[object Object]"><em class="W_ficon ficon_send S_ficon" title="试一下键盘N键？">ß</em></a>
	          </div>
	          <!--下拉层--> 
	          
	          <!--/下拉层--> 
	        </div>
        </div>       
      </div>
      
<div class="W_layer " id="layer_15577635968241" style="top: 322.5px; left: 373.5px;" stk-mask-key="1557763596824108"><div tabindex="0"></div>
   <div class="content" node-type="autoHeight">
      <div node-type="title" class="W_layer_title" style="cursor: move;">有什么新鲜事想告诉大家?</div>
       <div class="W_layer_close"><a node-type="close" href="javascript:void(0);" class="W_ficon ficon_close S_ficon">X</a></div>
        <div node-type="inner">
            <div node-type="inner"><div class="detail" node-type="outer">
    <div class="send_weibo clearfix send_weibo_long" node-type="wrap">
        <div class="title_area clearfix"><div class="title" node-type="info"></div>
            <div class="num S_txt2" node-type="num" style="">已输入<span>1</span>字</div><div class="key S_textb"></div>
        </div>
                <div class="input ">
                    <textarea pic_split="1" placeholder="" class="W_input" name="" node-type="textEl" style="overflow: hidden; margin: 0px; padding: 0px; border-style: none; border-width: 0px; font-size: 14px; overflow-wrap: break-word; line-height: 18px; outline: medium none currentcolor; height: 68px;" range="2&amp;0" data-video-status="ready" extra="video_fid=1034:4371639916496034&amp;video_titles=11&amp;video_covers=https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg|960|540&amp;video_monitor=0&amp;album_ids="></textarea>
<div class="send_succpic" style="display:none" node-type="successTip"><span class="W_icon icon_succB"></span><span class="txt">发布成功</span></div>
<form style="display:none;" node-type="extradata">
            <input name="pub_source" value="page_2">
                                                    <input name="topic_id" value="1022:">
                </form>
 
        </div>
        <div class="func_area clearfix" layout-shell="true">
    <div class="func">
                                    <div class="limits">
                    <!--                        新微博编辑框-->
                    <a href="javascript:void(0);" class="S_txt1" node-type="showPublishTo" action-type="showPublishTo" action-data="rank=0"><span node-type="publishTotext" class="W_autocut">公开</span><em class="W_ficon ficon_arrow_down S_ficon" node-type="publish_to_arrow">c</em></a>
                                        
                </div>
                            <a href="javascript:void(0)" class="W_btn_a btn_30px " node-type="submit">发布</a>
    </div>
    <div class="kind" node-type="widget" style="position: relative;">
                                    <a href="javascript:void(0);" class="S_txt1" action-type="face" action-data="type=500&amp;action=1&amp;log=face&amp;cate=1" title="表情" node-type="smileyBtn" suda-uatrack="key=tblog_home_edit&amp;value=phiz_button"><em class="W_ficon ficon_face">o</em></a>
                                                <a href="javascript:void(0);" class="S_txt1" action-type="multiimage" action-data="type=508&amp;action=1&amp;log=image&amp;cate=1" title="图片" node-type="multiimage" suda-uatrack="key=tblog_new_image_upload&amp;value=image_button"><em class="W_ficon ficon_image">p</em></a>
                                                <a href="javascript:void(0);" class="S_txt1" action-type="video" action-data="type=502&amp;action=1&amp;log=video&amp;cate=1" title="视频" suda-uatrack="key=tblog_home_edit&amp;value=video_button" style="position: relative;"><em class="W_ficon ficon_video">q</em><div style="position: absolute; left: 0px; top: 0px; display: block; overflow: hidden; background-color: rgb(0, 0, 0); opacity: 0; width: 25px; height: 24px;"><form node-type="form" action-type="form" name="uploadForm" enctype="multipart/form-data" method="post" style="overflow:hidden;opacity:0;filter:alpha(opacity=0);"><input node-type="fileInput" name="video" hidefoucs="true" title="视频" accept=".mpg,.m4v,.mp4,.flv,.3gp,.mov,.avi,.rmvb,.mkv,.wmv" style="cursor: pointer; width: 1000px; height: 1000px; position: absolute; bottom: 0px; right: 0px; font-size: 200px; display: none;" id="publisher_upvideo_1557763596824107" type="file"></form></div></a>
                                                <a href="javascript:void(0);" class="S_txt1" action-type="topic" action-data="type=504&amp;action=1&amp;log=topic&amp;cate=1" title="话题" suda-uatrack="key=tblog_home_edit&amp;value=topic_button"><em class="W_ficon ficon_swtopic">"</em></a>
                                </div>
<div class="W_layer W_layer_pop " node-type="outer" id="layer_15577635968241" style="left: 4px; top: 40px;"><div class="content"><div class="W_layer_close"><a href="javascript:void(0);" class="W_ficon ficon_close S_ficon" node-type="outerClose">X</a></div><div class="layer_send_video_v3 clearfix"><div class="W_layer_con_tit"><h1 class="W_f14 W_fb">上传普通视频：</h1><h2 class="S_txt2">请上传4GB以下的视频，请勿上传色情、反动等违法视频</h2></div><div class="video_upbox" node-type="upload" action-data="video_fid=1034:4371639916496034"><dl node-type="uploading" style="display: none;"><dt class="clearfix"><span class="loading_bar" node-type="loadingBar"><em style="width: 100%;"></em></span><span class="W_fr"><a href="javascript:void(0);" class="S_txt1" node-type="cancelUpload">取消上传</a></span></dt><dd><p class="S_txt2"><span node-type="status">视频上传成功</span><span class="sp1" node-type="uploadDetail" style="display: inline;"></span><a href="javascript:void(0);" node-type="reupload" style="display:none;">重新上传</a></p></dd></dl><dl node-type="uploadVideo" style="height:0px;overflow:hidden;position:relative;"><dd><p class="btn"><a href="javascript:void(0);" class="W_btn_a btn_30px" node-type="uploadBtn">上传视频</a></p></dd></dl><dl node-type="uploaded" style="height: 0px; overflow: hidden; position: relative;"><dt class="clearfix"><span class="W_fr"><a href="javascript:void(0);" class="a_ipt S_txt1" node-type="changeBtn" style="position: relative;"><em class="W_ficon ficon_reload S_ficon">ù</em>重新上传<div style="position: absolute; left: 0px; top: 0px; display: block; overflow: hidden; background-color: rgb(0, 0, 0); opacity: 0; width: 63px; height: 20px;"><form node-type="form" action-type="form" name="uploadForm" enctype="multipart/form-data" method="post" style="overflow:hidden;opacity:0;filter:alpha(opacity=0);"><input node-type="fileInput" name="video" hidefoucs="true" title="视频" accept=".mpg,.m4v,.mp4,.flv,.3gp,.mov,.avi,.rmvb,.mkv,.wmv" style="cursor: pointer; width: 1000px; height: 1000px; position: absolute; bottom: 0px; right: 0px; font-size: 200px; display: block;" id="change_upvideo_1557763596824107" type="file"></form></div></a></span></dt><dd><div class="dd_succ"><p class="ico"><span class="W_icon icon_succ"></span><span class="W_f14">视频上传成功！</span></p><p class="W_autocut S_txt2" node-type="fileName">tr5cd98a70ad...233918.mp4</p></div></dd></dl><dl node-type="editCompleted" style="display: block;"><dt class="clearfix"><span class="W_fr"><a href="javascript:void(0);" class="a_ipt S_txt1" node-type="editBtn"><em class="W_ficon ficon_longwb S_ficon">s</em>编辑</a></span></dt><dd><div class="dd_succ"><p class="pic_v2 W_piccut_h"><img src="https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg" alt=""><em class="W_ficon ficon_video">q</em></p></div></dd></dl></div><div class="video_iptbox" node-type="editBox" style="display: none;"><div class="form_table_s"><dl class="f_normal clearfix" node-type="title" action-data="video_titles=11"><dt class="f_tit"><span class="S_spetxt" node-type="star" style="visibility:hidden;">*</span>标题</dt><dd class="f_con"><div class="input_outer input_outer_default input_outer_only"><input class="W_input" placeholder="简明扼要的标题可吸引更多观看" action-type="inputTitle" type="text"><span class="sp1"><a href="javascript:void(0);" node-type="defaultTitle" action-type="setDefault">默认标题</a></span><span class="sp2"><a href="javascript:void(0);" action-type="delTitle"><em class="W_ficon ficon_close S_ficon">X</em></a></span></div><div class="tips clearfix"><span class="W_fl"><a href="javascript:void(0);" class="S_txt1" action-type="addTitle" style="display: none;"><em class="W_ficon ficon_add S_ficon">+</em>添加标题</a><em class="S_txt2" node-type="titleCount" style="display: none;">（还可以添加2个）</em></span><span class="W_fr S_txt2" node-type="countLimit">1/30</span></div></dd></dl><dl class="f_normal clearfix" node-type="program"> <dt class="f_tit">专辑</dt> <dd class="f_con">  <!--/所选专辑--><div class="input_outer4 input_outer2_disabled" node-type="content_div" style="border-color: rgb(204, 204, 204);"><input node-type="album_input" disabled="disabled" placeholder="优质的专辑会获得更多的曝光和推荐" class="W_input" readonly="readonly" style="position: absolute;" type="text"><div class="tagbox " style="min-height: 24px;" action-type="showProgram" node-type="scroll_program">   <div style="max-height: 80px">      <div node-type="add_Album" style="padding-right: 8px">      </div>   </div></div><span class="sp1" node-type="album_arrow_icon" action-type="showProgram"><a href="javascript:void(0);"><em class="W_ficon ficon_arrow_down_lite S_ficon">g</em></a></span><span class="sp2"><a href="javascript:void(0);" action-type="program_tip"><i class="W_icon icon_askS"></i></a></span><div class="layer_menu_list" node-type="album_layer" style="display: none">  <!--/专辑所有选项-->  <div class="list_a " node-type="scroll_ablum"><div style="max-height:150px">   <ul node-type="albumTemp">   </ul></div>  </div>  <!--/新建专辑-->  <div class="opt_box S_line1">    <ul node-type="newAblbum">      <li><a href="javascript:void(0);"><em class="W_ficon ficon_add S_ficon">+</em>新建专辑</a></li>    </ul>    <div class="opt_inner" node-type="add_Newalbum" style="display:none;">      <div class="tit W_fb">新建专辑</div>      <div class="ipt"><input class="W_input" node-type="albumInput" placeholder="1-15个字" type="text"><span><em node-type="limitAlbumName">0</em>/<em>15</em></span></div><em>      <div class="btn W_tc"><a href="javascript:void(0);" class="W_btn_a W_btn_a_disable" node-type="savaNewAlbum">确认</a> <a href="javascript:void(0);" node-type="closeNewAlbum" class="W_btn_b">取消</a></div>    </em></div><em>  </em></div></div><em>   </em></div><em>  </em></dd></dl><dl class="f_normal clearfix" node-type="sort"><dt class="f_tit"><em><span class="S_spetxt"></span>分类</em></dt><dd class="f_con"><div class="input_outer2"><em><input class="W_input" placeholder="请选择分类" readonly="readonly" type="text"><span class="sp1"><a href="javascript:void(0);" action-type="showSort"><em class="W_ficon ficon_arrow_down_lite S_ficon">g</em></a></span><div class="layer_sort_select" style="display: none;" node-type="sortLayer"><ul class="clearfix"><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">搞笑</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">时尚美妆</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">电视剧</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">娱乐综艺</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">电影</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">财经</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">情感两性</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">旅行</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">汽车</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">美食</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">教育</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">体育</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">运动健身</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">育儿</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">数码</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">动漫</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">军事</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">社会时政</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">音乐</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">舞蹈</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">游戏</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">动物宠物</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">科技</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">医疗健康</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">历史</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">美女</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">帅哥</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">其他</a></li></ul></div></em></div></dd></dl><dl class="f_normal clearfix" node-type="tags"><dt class="f_tit"><em><span class="S_spetxt" style="visibility:hidden;">*</span>标签</em></dt><dd class="f_con"><div class="input_outer3" style="overflow: visible;"><em><input class="W_input" placeholder="输入相关标签，多个标签空格分隔" node-type="inputDesc" type="text"><span class="sp1"><a href="javascript:void(0);" node-type="tagDesc"><i class="W_icon icon_askS"></i></a></span></em></div><div class="tag">历史标签：无</div></dd></dl><dl class="f_normal clearfix"><dt class="f_tit"><em>封面</em></dt><dd class="f_con"><div class="vd_input S_bg1"><em><a href="javascript:void(0);" class="upbtn_v3 W_piccut_h upbtn_v3_img" node-type="cover" action-data="video_covers=https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg|960|540"><span>+</span><img style="display: block;" src="https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg"><em class=" em2">设置视频封面</em></a></em></div></dd></dl></div></div></div><div class="W_layer_btn" node-type="btnBox" style="display: none;"><em><a href="javascript:void(0);" class="W_btn_a " node-type="completeBtn">完成</a></em></div><div class="W_layer_arrow"><em><span class="W_arrow_bor W_arrow_bor_t" node-type="arrow" style="left: 94px;"><i class="S_line3"></i><em class="S_bg2_br"></em></span></em></div></div></div><div class="W_layer W_layer_pop" node-type="outer" style="visibility: visible; display: none; left: 4px; top: 40px;" id="layer_15577635968241"><div class="content"><div class="W_layer_close"><a href="javascript:void(0);" class="W_ficon ficon_close S_ficon" action-type="hide">X</a></div><div class="layer_send_video_v3 clearfix"><div class="WB_minitab"><ul class="minitb_ul S_line1 S_bg1 clearfix tab3up"><li class="minitb_item current S_line1"><a href="javascript:void(0);" class="minitb_lk S_txt1 S_bg2" action-type="showUpload">上传封面</a><span class="cur_block"></span></li><li class="minitb_item S_line1"><a href="javascript:void(0);" class="minitb_lk S_txt1" action-type="showScreenshot">系统封面</a><span class="cur_block"></span></li></ul></div><div class="video_upbox" node-type="upload" style="display:none;"><div class="vd_input S_bg1"><a href="javascript:void(0);" class="upbtn" node-type="imgUpload" style="position:relative;">+<form node-type="form" action-type="form" id="pic_upload" name="pic_upload" target="upload_target" enctype="multipart/form-data" method="POST"><input hidefoucs="true" node-type="fileBtn" name="pic1" accept=".png,.jpg,.gif,.jpeg" type="file"></form></a></div><div class="W_tc S_txt2">支持5MB以内的png/gif/jpg图片，图片比例建议16：9</div></div><div class="video_upbox" node-type="uploading" style="display:none;"><div class="vd_input S_bg1"><span class="upinfo" node-type="uploadingDesc">正在上传封面...</span></div><div class="W_tc S_txt2">支持5MB以内的png/gif/jpg图片，图片比例建议16：9</div></div><div class="video_upbox" node-type="uploaded" style="display:none;"><div class="vd_input S_bg1"><span class="pic"><img alt="" node-type="uploadImg"></span><span class="bg" node-type="imgWrapper"></span></div><div class="W_tc S_txt2">支持5MB以内的png/gif/jpg图片，图片比例建议16：9</div></div><div class="video_upbox" node-type="processing" style="display:none;"><div class="vd_pics_info"><span class="upinfo">处理中，请稍候</span></div></div><div class="video_upbox" node-type="screenshot" style="display:none;" action-data="width=960&amp;height=540"><ul class="vd_pics_v3 clearfix"><li action-type="selectScreenshot" class=" curr"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx2.sinaimg.cn/large/56691f5bly1g3050ym554j20qo0f0q3w.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx2.sinaimg.cn/large/56691f5bly1g3050ylun4j20qo0f03zh.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx4.sinaimg.cn/large/56691f5bly1g3050ylxdjj20qo0f0t9p.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx3.sinaimg.cn/large/56691f5bly1g3050ylyysj20qo0f0ab1.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx4.sinaimg.cn/large/56691f5bly1g3050ym340j20qo0f0jsd.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx1.sinaimg.cn/large/56691f5bly1g3050yslvwj20qo0f0dgw.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx1.sinaimg.cn/large/56691f5bly1g3050ymo2uj20qo0f0dgr.jpg"></a></li><li action-type="selectScreenshot"><a href="javascript:void(0);" class=" W_piccut_h"><img alt="" src="https://wx1.sinaimg.cn/large/56691f5bly1g3050ym7p6j20qo0f0wfe.jpg"></a></li></ul><div class="W_tc S_txt2">请选取任意一张作为封面图</div></div></div><div class="W_layer_btn" node-type="button" style="display:none;"><a href="javascript:void(0);" class="W_btn_b" node-type="changeCover" style="position:relative;">更换封面</a><a href="javascript:void(0);" class="W_btn_a" node-type="setUploadCover" action-type="setUploadCover" style="display:none;">确定</a><a href="javascript:void(0);" class="W_btn_a " node-type="setScreenshotCover" action-type="setScreenshotCover" style="display:none;">确定</a></div><div class="W_layer_arrow"><span class="W_arrow_bor W_arrow_bor_t" node-type="arrow" style="left: 94px;"><i class="S_line3"></i><em class="S_bg2_br"></em></span></div></div></div></div>    </div>
</div></div>        </div>
 </div>
</div>

<div class="W_layer W_layer_pop " node-type="outer" id="layer_15577635968241" style="left: 4px; top: 40px;"><div class="content"><div class="W_layer_close"><a href="javascript:void(0);" class="W_ficon ficon_close S_ficon" node-type="outerClose">X</a></div><div class="layer_send_video_v3 clearfix"><div class="W_layer_con_tit"><h1 class="W_f14 W_fb">上传普通视频：</h1><h2 class="S_txt2">请上传4GB以下的视频，请勿上传色情、反动等违法视频</h2></div><div class="video_upbox" node-type="upload" action-data="video_fid=1034:4371639916496034"><dl node-type="uploading" style="display: none;"><dt class="clearfix"><span class="loading_bar" node-type="loadingBar"><em style="width: 100%;"></em></span><span class="W_fr"><a href="javascript:void(0);" class="S_txt1" node-type="cancelUpload">取消上传</a></span></dt><dd><p class="S_txt2"><span node-type="status">视频上传成功</span><span class="sp1" node-type="uploadDetail" style="display: inline;"></span><a href="javascript:void(0);" node-type="reupload" style="display:none;">重新上传</a></p></dd></dl><dl node-type="uploadVideo" style="height:0px;overflow:hidden;position:relative;"><dd><p class="btn"><a href="javascript:void(0);" class="W_btn_a btn_30px" node-type="uploadBtn">上传视频</a></p></dd></dl><dl node-type="uploaded" style="height: auto; overflow: hidden; position: relative;"><dt class="clearfix"><span class="W_fr"><a href="javascript:void(0);" class="a_ipt S_txt1" node-type="changeBtn" style="position: relative;"><em class="W_ficon ficon_reload S_ficon">ù</em>重新上传<div style="position: absolute; left: 0px; top: 0px; display: block; overflow: hidden; background-color: rgb(0, 0, 0); opacity: 0; width: 63px; height: 20px;"><form node-type="form" action-type="form" name="uploadForm" enctype="multipart/form-data" method="post" style="overflow:hidden;opacity:0;filter:alpha(opacity=0);"><input node-type="fileInput" name="video" hidefoucs="true" title="视频" accept=".mpg,.m4v,.mp4,.flv,.3gp,.mov,.avi,.rmvb,.mkv,.wmv" style="cursor: pointer; width: 1000px; height: 1000px; position: absolute; bottom: 0px; right: 0px; font-size: 200px; display: block;" id="change_upvideo_1557763596824107" type="file"></form></div></a></span></dt><dd><div class="dd_succ"><p class="ico"><span class="W_icon icon_succ"></span><span class="W_f14">视频上传成功！</span></p><p class="W_autocut S_txt2" node-type="fileName">tr5cd98a70ad...233918.mp4</p></div></dd></dl><dl node-type="editCompleted" style="display:none;"><dt class="clearfix"><span class="W_fr"><a href="javascript:void(0);" class="a_ipt S_txt1" node-type="editBtn"><em class="W_ficon ficon_longwb S_ficon">s</em>编辑</a></span></dt><dd><div class="dd_succ"><p class="pic_v2 pic_default"><img src="" alt=""><em class="W_ficon ficon_video">q</em></p></div></dd></dl></div><div class="video_iptbox" node-type="editBox"><div class="form_table_s"><dl class="f_normal clearfix" node-type="title" action-data=""><dt class="f_tit"><span class="S_spetxt" node-type="star" style="visibility:hidden;">*</span>标题</dt><dd class="f_con"><div class="input_outer input_outer_default input_outer_only"><input class="W_input" placeholder="简明扼要的标题可吸引更多观看" action-type="inputTitle" type="text"><span class="sp1"><a href="javascript:void(0);" node-type="defaultTitle" action-type="setDefault">默认标题</a></span><span class="sp2"><a href="javascript:void(0);" action-type="delTitle"><em class="W_ficon ficon_close S_ficon">X</em></a></span></div><div class="tips clearfix"><span class="W_fl"><a href="javascript:void(0);" class="S_txt1" action-type="addTitle" style="display: none;"><em class="W_ficon ficon_add S_ficon">+</em>添加标题</a><em class="S_txt2" node-type="titleCount" style="display: none;">（还可以添加2个）</em></span><span class="W_fr S_txt2" node-type="countLimit">0/30</span></div></dd></dl><dl class="f_normal clearfix" node-type="program"> <dt class="f_tit">专辑</dt> <dd class="f_con">  <!--/所选专辑--><div class="input_outer4 input_outer2_disabled" node-type="content_div" style="border-color: rgb(204, 204, 204);"><input node-type="album_input" disabled="disabled" placeholder="优质的专辑会获得更多的曝光和推荐" class="W_input" readonly="readonly" style="position: absolute;" type="text"><div class="tagbox " style="min-height: 24px;" action-type="showProgram" node-type="scroll_program">   <div style="max-height: 80px">      <div node-type="add_Album" style="padding-right: 8px">      </div>   </div></div><span class="sp1" node-type="album_arrow_icon" action-type="showProgram"><a href="javascript:void(0);"><em class="W_ficon ficon_arrow_down_lite S_ficon">g</em></a></span><span class="sp2"><a href="javascript:void(0);" action-type="program_tip"><i class="W_icon icon_askS"></i></a></span><div class="layer_menu_list" node-type="album_layer" style="display: none">  <!--/专辑所有选项-->  <div class="list_a " node-type="scroll_ablum"><div style="max-height:150px">   <ul node-type="albumTemp">   </ul></div>  </div>  <!--/新建专辑-->  <div class="opt_box S_line1">    <ul node-type="newAblbum">      <li><a href="javascript:void(0);"><em class="W_ficon ficon_add S_ficon">+</em>新建专辑</a></li>    </ul>    <div class="opt_inner" node-type="add_Newalbum" style="display:none;">      <div class="tit W_fb">新建专辑</div>      <div class="ipt"><input class="W_input" node-type="albumInput" placeholder="1-15个字" type="text"><span><em node-type="limitAlbumName">0</em>/<em>15</em></span></div><em>      <div class="btn W_tc"><a href="javascript:void(0);" class="W_btn_a W_btn_a_disable" node-type="savaNewAlbum">确认</a> <a href="javascript:void(0);" node-type="closeNewAlbum" class="W_btn_b">取消</a></div>    </em></div><em>  </em></div></div><em>   </em></div><em>  </em></dd></dl><dl class="f_normal clearfix" node-type="sort"><dt class="f_tit"><em><span class="S_spetxt"></span>分类</em></dt><dd class="f_con"><div class="input_outer2"><em><input class="W_input" placeholder="请选择分类" readonly="readonly" type="text"><span class="sp1"><a href="javascript:void(0);" action-type="showSort"><em class="W_ficon ficon_arrow_down_lite S_ficon">g</em></a></span><div class="layer_sort_select" style="display:none;" node-type="sortLayer"><ul class="clearfix"><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">搞笑</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">时尚美妆</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">电视剧</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">娱乐综艺</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">电影</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">财经</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">情感两性</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">旅行</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">汽车</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">美食</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">教育</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">体育</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">运动健身</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">育儿</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">数码</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">动漫</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">军事</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">社会时政</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">音乐</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">舞蹈</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">游戏</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">动物宠物</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">科技</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">医疗健康</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">历史</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">美女</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">帅哥</a></li><li><a class="sortItem" href="javascript:void(0);" action-type="selectSort">其他</a></li></ul></div></em></div></dd></dl><dl class="f_normal clearfix" node-type="tags"><dt class="f_tit"><em><span class="S_spetxt" style="visibility:hidden;">*</span>标签</em></dt><dd class="f_con"><div class="input_outer3" style="overflow: visible;"><em><input class="W_input" placeholder="输入相关标签，多个标签空格分隔" node-type="inputDesc" type="text"><span class="sp1"><a href="javascript:void(0);" node-type="tagDesc"><i class="W_icon icon_askS"></i></a></span></em></div><div class="tag">历史标签：无</div></dd></dl><dl class="f_normal clearfix"><dt class="f_tit"><em>封面</em></dt><dd class="f_con"><div class="vd_input S_bg1"><em><a href="javascript:void(0);" class="upbtn_v3 W_piccut_h upbtn_v3_img" node-type="cover" action-data="video_covers=https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg|960|540"><span>+</span><img style="display: block;" src="https://wx4.sinaimg.cn/large/56691f5bly1g3050ym3b7j20qo0f0gmj.jpg"><em class=" em2">设置视频封面</em></a></em></div></dd></dl></div></div></div><div class="W_layer_btn" node-type="btnBox"><em><a href="javascript:void(0);" class="W_btn_a " node-type="completeBtn">完成</a></em></div><div class="W_layer_arrow"><em><span class="W_arrow_bor W_arrow_bor_t" node-type="arrow" style="left: 94px;"><i class="S_line3"></i><em class="S_bg2_br"></em></span></em></div></div></div>

<div class="dd_succ"><p class="ico"><span class="W_icon icon_succ"></span><span class="W_f14">视频上传成功！</span></p><p class="W_autocut S_txt2" node-type="fileName">tr5cd98a70ad...233918.mp4</p></div>


<div class="W_layer W_layer_pop " id="layer_15577635968241" style="left: 4px; top: 40px;"><div class="content"><div class="W_layer_title"><div class="W_layer_close"><a href="javascript:void(0);" node-type="close" class="W_ficon ficon_close S_ficon">X</a></div></div><div class="layer_send_pic" node-type="inner"><div class="layer_send_btn clearfix"><ul class="clearfix"><li class="S_line2" suda-data="key=tblog_new_image_upload&amp;value=fast_upload"><a href="javascript:void(0);" class="W_btn_l" node-type="mUpload" style="position: relative;"><span class="btn_45px"><em class="W_ficon ficon_add_pic S_ficon">È</em>单图/多图</span><div style="position: absolute; left: 0px; top: 0px; display: block; overflow: hidden; background-color: rgb(0, 0, 0); opacity: 0; width: 194px; height: 45px;"><form node-type="form" action-type="form" id="pic_upload" name="pic_upload" target="upload_target" enctype="multipart/form-data" method="POST" style="overflow: hidden; opacity: 0; height: 45px; width: 194px;"><input accept="image/jpg,image/jpeg,image/png,image/gif" hidefoucs="true" node-type="fileBtn" name="pic1" style="cursor:pointer;width:1000px;height:1000px;position:absolute;bottom:0;right:0;font-size:200px;" id="swf_upbtn_1557763596824223" multiple="multiple" type="file"></form></div></a></li><li class="S_line2"><a href="javascript:void(0);" class="W_btn_l W_btn_l_dis" node-type="inputCover" suda-data="key=tblog_new_image_upload&amp;value=pin_upload"><span class="btn_45px"><em class="W_ficon ficon_puzzle S_ficon">Æ</em>拼图</span></a></li><li class="S_line2"><a href="javascript:void(0);" class="W_btn_l" action-type="capture" suda-data="key=tblog_new_image_upload&amp;value=screenshot_upload"><span class="btn_45px"><em class="W_ficon ficon_screenshot S_ficon">Ô</em>截屏</span></a></li><li class="S_line2"><a href="javascript:void(0);" class="W_btn_l" action-type="uploadAblum" suda-data="key=tblog_new_image_upload&amp;value=upload_albums"><span class="btn_45px"><em class="W_ficon ficon_upload_album S_ficon">Ë</em>传至相册</span></a></li></ul></div></div><div class="W_layer_arrow"><span class="W_arrow_bor W_arrow_bor_t" node-type="arrow" style="left: 45px;"><i class="S_line3"></i><em class="S_bg2_br"></em></span></div></div></div>


 */
		
		NieUtil.mySleepBySecond(3);
	}
	private List<File> getToPublishFile(MyVideoObject uploadObj) {
		File uploadFoldFolder = MyVideoTrUtil.getVideoSaveFolder(uploadObj);
		File[] files = uploadFoldFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				File f = new File(dir,name);
				if(isVideoFile(f))return true;
				if(isPhotoFile(f))return true;
				return false;
			}});
		List<File> multimediaContext = Lists.newArrayList();
		for(File file:files){
			multimediaContext.add(file);
		}
		return multimediaContext;
	}
	private boolean isVideoFile(File f) {
		String exd = MyVideoTrUtil.getFileExtention(f);
		if(exd.equalsIgnoreCase("mp4"))return true;
		if(exd.equalsIgnoreCase("flv"))return true;
		// FIXME more type...
		return false;
	}
	private boolean isPhotoFile(File f) {
		String exd = MyVideoTrUtil.getFileExtention(f);
		if(exd.equalsIgnoreCase("png"))return true;
		if(exd.equalsIgnoreCase("jpg"))return true;
		if(exd.equalsIgnoreCase("jpeg"))return true;
		// FIXME more type...
		return false;
	}

	public List<MyVideoObject> scan(WebDriver driver) {
		String visitUrl = "https://www.weibo.com/like/outbox?leftnav=1";
		driver.get(visitUrl);
		logonWeibo(driver);

		List<MyVideoObject> videoObjs = Lists.newArrayList();
		parseWeibo(driver, videoObjs);

//		((JavascriptExecutor) driver)
//				.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//		NieUtil.mySleepBySecond(4);

//		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
//			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
//			parseWeibo(driver, videoObjs);
//		} catch (AWTException e) {
//		}
//		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
//			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
//			parseWeibo(driver, videoObjs);
//		} catch (AWTException e) {
//		}
//		try {
//			Robot robot = new Robot();
//			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
//			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
//			parseWeibo(driver, videoObjs);
//		} catch (AWTException e) {
//		}
			

		return videoObjs;
	}

	private void parseWeibo(WebDriver driver, List<MyVideoObject> videoObjs) {
		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.tagName("video"));
			if(wes1.isEmpty())continue;
			String mid = we.getAttribute("mid");
			MyVideoObject videoObj = new MyVideoObject();
			wes1 = we.findElements(By.cssSelector("div[node-type=\"feed_list_content\"]"));
			if(!wes1.isEmpty()){
				videoObj.title = wes1.get(0).getText();
			}

			String videoUrl = findVideoUrl(we);
			if(StringUtil.isBlank(videoUrl))continue;

			videoObj.trid = mid;
			videoObj.url = videoUrl;
			videoObj.toType = "toYoutube";
			videoObj.fromType = "fromWeibo";
			videoObj.videoUrl = videoUrl;
		
			wes1 = we.findElements(By.cssSelector("div[class=\"WB_info\"]"));
			if(!wes1.isEmpty()){
				List<WebElement> wes2 = wes1.get(0).findElements(By.tagName("a"));
				for(WebElement we2:wes2){
					String nickName = we2.getAttribute("nick-name");
					if(!StringUtil.isBlank(nickName)){
						videoObj.uper = nickName.trim();break;
					}
				}
			}

			wes1 = we.findElements(By.cssSelector("div[class=\"W_fl\"]"));
			if(!wes1.isEmpty()){
				videoObj.fl = wes1.get(0).getText();
			}
			wes1 = we.findElements(By.cssSelector("div[class=\"W_fr\"]"));
			if(!wes1.isEmpty()){
				videoObj.fr = wes1.get(0).getText();
			}
			videoObjs.add(videoObj);
			//NieUtil.mySleepBySecond(1);
		}
	}
	
	private String findVideoUrl(WebElement we) {

		String videoUrl = "";
		List<WebElement> wes1 = we.findElements(By.cssSelector("li[action-type=\"feed_list_third_rend\"]"));
		if(!wes1.isEmpty()){
			for(WebElement we1 : wes1){
				String dataVal = we1.getAttribute("action-data");
				if(StringUtil.isBlank(dataVal)) continue;
				String[] sp = dataVal.split("&");
//				for(String s:sp){
//					try {
//						System.out.println(NieUtil.decode(s));
//					} catch (DecoderException e) {
//					}
//				}
				for(String s:sp){
					if(s.startsWith("short_url=")){
						videoUrl = NieUtil.decode(s.substring("short_url=".length()));
						return videoUrl;
					}
				}
			}
		}
		wes1 = we.findElements(By.cssSelector("li[action-type=\"feed_list_media_img\"]"));
		if(!wes1.isEmpty()){
			for(WebElement we1 : wes1){
				String dataVal = we1.getAttribute("action-data");
				if(StringUtil.isBlank(dataVal)) continue;
				String[] sp = dataVal.split("&");
//				for(String s:sp){
//					try {
//						System.out.println(NieUtil.decode(s));
//					} catch (DecoderException e) {
//					}
//				}
				for(String s:sp){
					if(s.startsWith("short_url=")){
						videoUrl = NieUtil.decode(s.substring("short_url=".length()));
						return videoUrl;
					}
				}
			}
		}
		return videoUrl;
	}
	public void logonWeibo(WebDriver driver) {

		WebDriverWait wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By
							.cssSelector("ul[class=\"gn_nav_list\"]"));
					return Boolean.TRUE;
				} catch (Exception ex) {
				}
				return Boolean.FALSE;
			}
		});
		WebElement el1 = driver.findElement(By
				.cssSelector("ul[class=\"gn_nav_list\"]"));
		List<WebElement> eles = el1.findElements(By
				.cssSelector("em[class=\"S_txt1\"]"));
		for (WebElement ele : eles) {
			String txt = ele.getText();
			if (txt.indexOf("次郎花子") != -1) {
				return;
			}
		}

		wait1.until(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver driver) {
				try {
					driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
					return Boolean.TRUE;
				} catch (Exception ex) {
				}
				return Boolean.FALSE;
			}
		});
		el1 = driver.findElement(By.cssSelector("div[id=\"pl_unlogin_home_login\"]"));
		WebElement el2 = el1.findElement(By.cssSelector("a[node-type=\"normal_tab\"]"));
		el2.click();
		
		el2 = el1.findElement(By.cssSelector("input[id=\"loginname\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("myvideotr.weibo.user.name"));
		
		el2 = el1.findElement(By.cssSelector("input[name=\"password\"]"));
		el2.clear();
		el2.sendKeys(NieConfig.getConfig("myvideotr.weibo.user.password"));
		
		List<WebElement> wes = el1.findElements(By.tagName("span"));
		for(WebElement we :wes){
			if("登录".equals(we.getText())){
				we.click();
				break;
			}
		}

		NieUtil.mySleepBySecond(2);
		
		wait1 = new WebDriverWait(driver,60);
		wait1.until(new ExpectedCondition<Boolean>(){
			@Override
			public Boolean apply(WebDriver driver) {
				try {

					WebElement el1 = driver.findElement(By.cssSelector("div[id=\"plc_top\"]"));
					List<WebElement> eles = el1.findElements(By.cssSelector("em[class=\"S_txt1\"]"));
					for(WebElement ele:eles){
						String txt = ele.getText();
						if(txt.indexOf("次郎花子") != -1){
							return Boolean.TRUE;
						}
					}
					return Boolean.FALSE;
				} catch (Exception e) {
				}
				return Boolean.FALSE;
			}
		});
		
		//NieUtil.readLineFromSystemIn("Weibo login is finished? ANY KEY For already");
	}

	public boolean downloadVideo(WebDriver driver, MyVideoObject downloadObj) throws IOException  {

		String videoDownloadUrl = MyVideoTrUtil.getVideoDownloadUrl(driver, downloadObj);
		if (StringUtil.isBlank(videoDownloadUrl)) {
			return false;
		}
		File saveFile = getVideoSaveFile(downloadObj);
		MyVideoTrUtil.downLoadVideoFromUrl(videoDownloadUrl, saveFile);
		return true;
	}
	
	private File getVideoSaveFile(MyVideoObject downloadObj) {
		File outFolder = MyVideoTrUtil.getVideoSaveFolder(downloadObj);
		File saveFile = new File(outFolder, downloadObj.uid + ".mp4");
		return saveFile;
	}

	public void removeFromFav(WebDriver driver, MyVideoObject videoObj) {
		//driver.get(myfavURL);
		//logonWeibo(driver);

		List<WebElement> wes = driver.findElements(By.cssSelector("div[action-type=\"feed_list_item\"]"));
		for(WebElement we :wes){
			List<WebElement> wes1 = we.findElements(By.cssSelector("div[class=\"WB_feed_handle\"]"));
			if(wes1.isEmpty())continue;
			List<WebElement> wes2 = wes1.get(0).findElements(By.tagName("a"));
			if(wes2.isEmpty())continue;
			boolean breakFlag = false;
			for(WebElement we2:wes2){
				String title = we2.getAttribute("title");
				String actionData = we2.getAttribute("action-data");
				if(title.equals("取消赞") && actionData.indexOf(videoObj.trid) != -1){
					we2.click();
					breakFlag = true;
					break;
				}
			}
			if(breakFlag)break;
		}
		
	}
}
