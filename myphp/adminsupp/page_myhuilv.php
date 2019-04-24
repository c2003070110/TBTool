<?php
require __DIR__ .'/MyHuilv.php';
?>
<html lang="ja">
<head>
<title>my hui lv</title>
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
    $(document).on("click", "#btnGetYLHV", function() {
		//$("#huilvYL").val(0.0603);
		
        var url = "https://www.kuaiyilicai.com/uprate/jpy.html";
        var jqxhr = $.ajax("/myphp/redirect.php",
                         { type : "GET",
                           data : {realUrl : url},
                           dataType : "html" }
                      );
        jqxhr.done(function( msg ) {
			//console.log(msg);
			//var htmlEle = $(msg).find("body > div.container > form > div:nth-child(3) > div > div.panel-body > div:nth-child(2) > div.col-md-10.col-xs-8 > span:nth-child(1)");
			
			//console.log(htmlEle.html());
			
			var subRowList = $(msg).find(".sub_row").find("span");
            for(var i=0; i<subRowList.length; i++){
				var huilvTxt = $(subRowList[i]).html();
				if(huilvTxt.indexOf("0.0") == 0){
					$("#huilvYL").val(huilvTxt);
					
					var huilvYL = parseFloat($("#huilvYL").val());
                    $("#myhuilv").val(huilvYL + 0.005);
					break;
				}
			}
	    });
    });
    $(document).on("click", "#btnSave", function() {
        var myhuilv = parseFloat($("#myhuilv").val());
        var daigoufei = parseInt($("#daigoufei").val());
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {
							   "action" : "saveMyhuilv", 
							   "huilvDiv" : $("#huilvDiv").val(),
						       "myhuilv" : myhuilv,
							   "daigoufei" : daigoufei},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
        });
    });
    $(document).on("onchange", "#huilvDiv", function() {
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {
							   "action" : "getMyhuilvByHuilvDiv", 
							   "huilvDiv" : $("#huilvDiv").val()},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
        	var huilvObj = JSON.parse(msg);
            $("#daigoufei").val(huilvObj.daigoufei);
            $("#myhuilv").val(huilvObj.myhuilv);
        });
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
        <button type="button" id="btnGetYLHV" class="btn btn-secondary actionBtn">get YingLian huilv</button>
        <div class="col-6 themed-grid-col">
          <label for="huilv">YingLian</label>
          <input type="text" class="form-control" id="huilvYL">
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-6 themed-grid-col">
            <label for="huilvDiv">huilv Div</label>
            <select class="custom-select d-block w-100" id="huilvDiv">
                <option value="YA" selected>YA</option>
                <option value="WM">webmoney</option>
            </select>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-5 themed-grid-col">
          <label for="plusplus">daigoufei</label>
          <input type="text" class="form-control" id="daigoufei"?>">
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-6 themed-grid-col">
          <label for="myhuilv">My huilv</label>
          <input type="text" class="form-control" id="myhuilv">
        </div>
        <button type="button" id="btnSave" class="btn btn-secondary actionBtn">SAVE</button>
      </div>
      <hr class="mb-4">
  </div>
</div>
</body>
</html>