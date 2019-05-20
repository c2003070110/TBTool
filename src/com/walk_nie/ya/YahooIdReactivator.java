package com.walk_nie.ya;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.WebDriver;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.util.WebDriverUtil;

public class YahooIdReactivator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		new YahooIdReactivator().execute();
	}

	public void execute() throws IOException {

		WebDriver driver = WebDriverUtil.getFirefoxWebDriver();
		List<RegObjInfo> list = readin();
		for(RegObjInfo info:list){
			activate(driver,info);
		}
	}
	private void activate(WebDriver driver, RegObjInfo info) {
		
		YaUtil.login(driver, info.id, info.pswd);

		// TODO activate
		try {
			/*
<section class="main">
  <p>下記のオプションを選択し「利用再開」ボタンを押すと、停止されたYahoo!メールアドレスを再度利用できますが、再開しても、消去された情報・設定は復旧できません。</p>
  <ul class="list">
    <li>メール、フォルダー、添付ファイル、詳細設定はすべて消去されています。</li>
    <li>y7e8180205@yahoo.co.jp宛に送信されたメールはすべて送信者に返送されています。</li>
    <li>Yahoo! JAPANのトップページに新着メッセージ数が残っている場合がありますがメールの復旧はできません。</li>
  </ul>

  <ul class="radio">
    <li class="on">
      <label for="protectaccount"><input id="protectaccount" name="protect" checked="" value="1" type="radio">Yahoo!メールの利用を再開し、Yahoo!メール セキュリティーパックについて詳しくみる</label>
      <div class="detail">
        <p>以下のいずれかのサービスをご利用いただいている期間は、Yahoo!メールアドレスの利用停止は適用されません。</p>
        <ul class="list">
          <li>Yahoo!メール セキュリティーパック</li>
          <li>Yahoo!メール ウイルスチェックサービス</li>
          <li>マイネームアドレス</li>
          <li>追加メールアドレス</li>
          <li>Yahoo! BB</li>
          <li>Yahoo!プレミアム</li>
        </ul>
        <p>※これらのサービスを申し込まれても、すでに消去されたメールや詳細設定などは復旧されません。</p>
      </div>
    </li>
    <li class="">
      <label for="dontprotectaccount"><input id="dontprotectaccount" name="protect" value="0" type="radio">Yahoo!メールの利用を再開する</label>
      <div class="detail">
        <p>6カ月間以上Yahoo!メールのご利用がない場合は、保存されていたメールや詳細設定などは再び消去されてしまいますのでご注意ください。</p>
      </div>
    </li>
  </ul>

<ul class="link">
  <li>
    <form id="revive_form" action="/revive">
      <input class="functionBtn submit" value="利用再開" type="submit">
      <input name=".src" value="ym" type="hidden">
      <input name=".done" value="https://mail.yahoo.co.jp/" type="hidden">
      <input name="crumb" value="dD1NLnI0Y0Imc2s9OTdubVdJS3dneVFqUHhSWmFFRllrb0QyVXQ0LQ==" type="hidden">
      <input name="mail_revive" value="true" type="hidden">
      <input name="select_type" value="1" type="hidden">
    </form>
  </li>
</ul>
</section>
			 */
/*
<div id="pwdWrap" class="" data-ylk="sec:verify_pwd_input;">
<dl id="pwdBox" class="item">
<dt><label for="passwd" class="offLeft">パスワード</label></dt>
<dd id="pwd" class=" btnClearWrp"><input name="passwd" id="passwd" value="" class="" placeholder="パスワード" spellcheck="false" data-rapid_p="1" type="password"><span class="btnClear" id="btnClearPasswd" style="display: none;"></span></dd>
</dl><!--/.item-->
<div id="captchaBox" class="dispNone"></div>
</div>			
 */
			//driver.findElement(By.id("passwd"));
		} catch (Exception e) {
		}
		
	}

	private List<RegObjInfo> readin() {
		List<RegObjInfo> objList = Lists.newArrayList();
		// TODO which file??
		File file = new File("","");
		try {
			List<String> lines = FileUtils.readLines(file, "UTF-8");
			for(String line :lines){
				objList.add(RegObjInfo.parse(line));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return objList;
	}
}
