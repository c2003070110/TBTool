<?php
  include __DIR__ .'/subpage_toplink.php';
?>
<html lang="ja">
<head>
<title>My 見積り</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<!--
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_ADMINSUPP") ?>";
$(function() {
	var toInt = function(data){
		return !data ? 0 : parseInt(data, 10);
	}
	var parseDouble = function(data){
		return !data ? 0 : parseFloat(data, 10);
	}
	var getGuojiYunfei = function(weigth, guojiShoudan){
		if(guojiShoudan == "EMS"){
			int st1 = 1400,st2 = 2400,st3 = 3800,st4 = 8100;
			int incr1=140,incr2=300,incr3=500,incr4=800;
			if(weight < 500){
				return st;
			}else if(weight < 600){
				return st1 + incr1 * 1;
			}else if(weight < 700){
				return st1 + incr1 * 2;
			}else if(weight < 800){
				return st1 + incr1 * 3;
			}else if(weight < 900){
				return st1 + incr1 * 4;
			}else if(weight < 1000){
				return st1 + incr1 * 5;
			}else if(weight < 1250){
				return st2;
			}else if(weight < 1500){
				return st2 + incr2 * 1;
			}else if(weight < 1750){
				return st2 + incr2 * 2;
			}else if(weight < 2000){
				return st2 + incr2 * 3;
			}else if(weight < 2500){
				return st3;
			}else if(weight < 3000){
				return st3 + incr3 * 1;
			}else if(weight < 3500){
				return st3 + incr3 * 2;
			}else if(weight < 4000){
				return st3 + incr3 * 3;
			}else if(weight < 4500){
				return st3 + incr3 * 4;
			}else if(weight < 5000){
				return st3 + incr3 * 5;
			}else if(weight < 5500){
				return st3 + incr3 * 6;
			}else if(weight < 6000){
				return st3 + incr3 * 7;
			}else if(weight < 7000){
				return st4;
			}else {
				return st4 + incr3 * toInt((weight-7000)/1000);
			}
		}else if(guojiShoudan == "SAL"){
			int st1 = 1800,st2 = 4700,st3 = 7000;
			int incr1=600,incr2=500,incr3=300;
			if(weight < 1000){
				return st1;
			}else if(weight < 2000){
				return st1 + incr1 * 1;
			}else if(weight < 3000){
				return st1 + incr1 * 2;
			}else if(weight < 4000){
				return st1 + incr1 * 3;
			}else if(weight < 5000){
				return st1 + incr1 * 4;
			}else if(weight < 6000){
				return st2;
			}else if(weight < 7000){
				return st2 + incr2 * 1;
			}else if(weight < 8000){
				return st2 + incr2 * 2;
			}else if(weight < 8000){
				return st2 + incr2 * 3;
			}else if(weight < 10000){
				return st2 + incr2 * 4;
			}else if(weight < 11000){
				return st3;
			}else {
				return st3 + incr3 * toInt((weight-11000)/1000);
			}
		}else if(guojiShoudan == "SEA"){
			int st1 = 1600;
			int incr1=300;
			if(weight < 1000){
				return st1;
			}else {
				return st3 + incr1 * toInt((weight-1000)/1000);
			}
		}else if(guojiShoudan == "PINGYOU"){
			return (weigth / 100)*8;
		}
	}
    $(document).on("click", "#btnGetYLHV", function() {
        var url = "https://www.kuaiyilicai.com/uprate/jpy.html";
        var jqxhr = $.ajax("myphp/redirect.php",
                         { type : "GET",
                           data : {realUrl : url},
                           dataType : "html" }
                      );
        jqxhr.done(function( msg ) {
			var subRowList = $(msg).find("div[class=\"panel-body\"]").find("div[class=\"sub_row\"]");
            for(var i=0; i<subRowList.length; i++){
				var divlist = $(subRowList[i]).find("div");
				if(divlist.length <>2){
					continue;
				}
				if("汇率:" == $(divlist[0]).text()){
					var huilvTxt = $(divlist[1].find("span").text());
					$("#huilv").val(huilvTxt);
				}
			}
	    }
    }
    $(document).on("click", "#btnSave", function() {
        var myhuilv = parseFloat($("#myhuilv").val());
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {
							   "action" : "saveMyhuilv", 
							   "huilvDiv" : $("#huilvDiv").val(),
						       "myhuilv" : myhuilv},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
            thisBox.find("#codeCd").val("");
        });
    });
    $(document).on("change", ".form-control", function() {
		var itemBoxes = $(".itembox");
		var huilv = parseDouble($("#myhuilv").val());
		var mydaigoufei = toInt($("#mydaigoufei").val());
		var priceJPYHeji=0, yunfeiJPJPYHeji=0, shoufeiCNYHeji=0;
		for(var i=0; i<itemBoxes.length; i++){
			var priceJPY = toInt($(itemBoxes[i]).find("#priceJPY").val());
			priceJPYHeji = priceJPYHeji + priceJPY;
			var yunfeiJPJPY = toInt($(itemBoxes[i]).find("#yunfeiJPJPY").val());
			yunfeiJPJPYHeji = yunfeiJPJPYHeji + yunfeiJPJPY;
			var shoufeiCNY = (priceJPY + yunfeiJPJPY) * huilv + mydaigoufei
			shoufeiCNYHeji = shoufeiCNYHeji + shoufeiCNY;
			$(itemBoxes[i]).find("#shoufeiCNY").val(shoufeiCNY);
		}
        $("#priceJPYHeji").val(priceJPYHeji);
        $("#yunfeiJPJPYHeji").val(yunfeiJPJPYHeji);
        $("#shoufeiCNYHeji").val(shoufeiCNYHeji);
    });
    $(document).on("change", ".form-control1", function() {
        var weigth = $("#weigth").val();
        var guojiShoudan = $("#guojiShoudan").val();
		var guojiYunfeiJPY = getGuojiYunfei(weigth, guojiShoudan);
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <div class="box">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">XXXX</label>
        <input type="hidden" id="myhuilv" >
        <input type="hidden" id="mydaigoufei" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnAddRow" class="btn btn-secondary actionBtn">add another item</button>
      </div>
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnYunfei" class="btn btn-secondary actionBtn">add yunfei</button>
      </div>
    </div>
  </div>
  <hr class="mb-4">
  <div class="box itembox">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">riyuan</label>
        <input type="text" class="form-control" id="priceJPY" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">yunfeiJP</label>
        <input type="text" class="form-control" id="yunfeiJPJPY" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">shoufei</label>
        <input type="text" class="form-control" id="shoufeiCNY" readonly>
      </div>
    </div>
  </div>
  <hr class="mb-4">
  <div class="box" id="guojiYunfeiBox">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">zhongliang(g)</label>
        <input type="text" class="form-control1" id="weigth" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-6 themed-grid-col">
          <label for="guojiShoudan">shoudan</label>
          <select class="custom-select d-block w-100" id="guojiShoudan">
              <option value="EMS" selected>EMS</option>
              <option value="SAL">SAL</option>
              <option value="SEA">SEA</option>
              <option value="PINGYOU">PINGYOU</option>
          </select>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">yunfeiJP</label>
        <input type="text" class="form-control" id="guojiYunfeiJPY" readonly>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">shoufei</label>
        <input type="text" class="form-control" id="guojiYunfeiCNY" readonly>
      </div>
    </div>
  </div>
  <div class="box" id="hejibox">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">riyuan(heji)</label>
        <input type="text" class="form-control" id="priceJPYHeji" readonly >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">yunfeiJP(heji)</label>
        <input type="text" class="form-control" id="yunfeiJPJPYHeji" readonly >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">shoufei(heji)</label>
        <input type="text" class="form-control" id="shoufeiCNYHeji" readonly>
      </div>
    </div>
  </div>
</div>
</body>
</html>