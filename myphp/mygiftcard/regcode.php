<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ . '/MyGiftCard.php';
?>
<html lang="ja">
<head>
<title>my gift card</title>
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
(function (original) {
  jQuery.fn.clone = function () {
    var result           = original.apply(this, arguments),
        my_textareas     = this.find('textarea').add(this.filter('textarea')),
        result_textareas = result.find('textarea').add(result.filter('textarea')),
        my_selects       = this.find('select').add(this.filter('select')),
        result_selects   = result.find('select').add(result.filter('select'));

    for (var i = 0, l = my_textareas.length; i < l; ++i) $(result_textareas[i]).val($(my_textareas[i]).val());
    for (var i = 0, l = my_selects.length;   i < l; ++i) result_selects[i].selectedIndex = my_selects[i].selectedIndex;

    return result;
  };
}) (jQuery.fn.clone);
</script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYGIFTCARD") ?>";
$(function() {
    $(document).on("click", "#btnSave", function() {
        var orderNoVal = $("#orderNo").val();
        var codeTypeVal = $("#codeType").val();
        var codeCdVal = $("#codeCd").val();
        if(orderNoVal == "" || codeCdVal == ''){
            return;
        }
        var param = orderNoVal + "," + codeTypeVal + "," + codeCdVal;
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":"save", "paramstr" : param},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
            $("#codeCd").val("");
        });
    });
    $(document).on("click", ".actionBtn", function() {
        var actionName = $(this).html();

        var uid = $("#uid").val();
        var orderNoVal = $("#orderNo").val();
        var codeTypeVal = $("#codeType").val();
        var codeCdVal = $("#codeCd").val();
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action" : actionName, 
                                   "uid" : uid, 
                                   "orderNo" : orderNoVal, 
                                   "codeType" : codeTypeVal, 
                                   "codeCd" : codeCdVal},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			//alert(msg);
            location.reload();
        });
    });
    $(document).on("click", "#btnConvert", function() {
        var srcTxt = $("#tempTxtArea").val();
		var arr1 = srcTxt.split(/(\r\n|\n|\r)/gm);
		var hasOrderNo = false,hasCodeCd = false;
		for(var i=0; i<arr1.length; i++){
			var strLoop1 = arr1[i];
			if(hasOrderNo == false){
				var numArr = strLoop1.match(/\d+/g);
				if(numArr){
					for(var j=0; j<numArr.length; j++){
						if(numArr[j].length > 15){
							$("#orderNo").val(numArr[j]);
							hasOrderNo = true;
						}
					}
				}
			}
			if(hasCodeCd == false){
				var cdArr = strLoop1.split(/[\s,:：]+/);
				if(cdArr){
					for(var j=0; j<cdArr.length; j++){
						if(cdArr[j].indexOf("-") != -1){
							$("#codeCd").val(cdArr[j]);
							hasCodeCd = true;
						}
					}
				}
			}
		}
		if(hasOrderNo == false || hasCodeCd == false){
			alert("Convert do NOT completed successful!");
		}
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
<?php
  $uid = empty($_GET['uid'])? "" : $_GET['uid'];
  $codeType = empty($_GET['codeType']) ? "" : $_GET['codeType'];
  if(!empty($uid)){
	$my = new MyGiftCard();
	$obj = $my->listCodeByUid($uid);
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php">stock list</a></li>
  </ul>   
  <hr class="mb-4">
  <input type="hidden" id="uid" value="<?php echo $uid ?>">
  <div class="box">
<?php
  if(empty($uid)){
?>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
		  <textarea id="tempTxtArea" rows="6" cols="45"></textarea>
		</div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
          <button type="button" id="btnConvert" class="btn btn-secondary">C o n v e r t !</button>
		</div>
      </div>
<?php 
  }
?>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
		  <label for="orderNo">OrderNo</label>
		  <input type="text" class="form-control" id="orderNo" value="<?php echo $obj['orderNo'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-8 themed-grid-col">
            <label for="codeType">CodeType</label>
            <select class="custom-select d-block" id="codeType">
                <option value="" selected></option>
<?php
  if($codeType == "" || $codeType == "PSNUSD"){
?>
                <option value="PSNUSD10" <?php if($obj['codeType']=='PSNUSD10'){?> selected <?php } ?>>PSN 10美元</option>
                <option value="PSNUSD20" <?php if($obj['codeType']=='PSNUSD20'){?> selected <?php } ?>>PSN 20美元</option>
                <option value="PSNUSD30" <?php if($obj['codeType']=='PSNUSD30'){?> selected <?php } ?>>PSN 30美元</option>
                <option value="PSNUSD40" <?php if($obj['codeType']=='PSNUSD40'){?> selected <?php } ?>>PSN 40美元</option>
                <option value="PSNUSD50" <?php if($obj['codeType']=='PSNUSD50'){?> selected <?php } ?>>PSN 50美元</option>
                <option value="PSNUSD100" <?php if($obj['codeType']=='PSNUSD100'){?> selected <?php } ?>>PSN 100美元</option>
<?php 
  }
  if($codeType == "" || $codeType == "PSNHKD"){
?>
                <option value="PSNHKD80" <?php if($obj['codeType']=='PSNHKD80'){?> selected <?php } ?>>PSN 80HKD</option>
                <option value="PSNHKD160" <?php if($obj['codeType']=='PSNHKD160'){?> selected <?php } ?>>PSN 160HKD</option>
                <option value="PSNHKD200" <?php if($obj['codeType']=='PSNHKD200'){?> selected <?php } ?>>PSN 200HKD</option>
                <option value="PSNHKD300" <?php if($obj['codeType']=='PSNHKD300'){?> selected <?php } ?>>PSN 300HKD</option>
                <option value="PSNHKD400" <?php if($obj['codeType']=='PSNHKD400'){?> selected <?php } ?>>PSN 400HKD</option>
                <option value="PSNHKD500" <?php if($obj['codeType']=='PSNHKD500'){?> selected <?php } ?>>PSN 500HKD</option>
                <option value="PSNHKD1000" <?php if($obj['codeType']=='PSNHKD1000'){?> selected <?php } ?>>PSN 1000HKD</option>
<?php 
  }
  if($codeType == "" || $codeType == "XBOXUSD"){
?>
                <option value="XBOXUSD10" <?php if($obj['codeType']=='XBOXUSD10'){?> selected <?php } ?>>XBOX 10美元</option>
                <option value="XBOXUSD20" <?php if($obj['codeType']=='XBOXUSD20'){?> selected <?php } ?>>XBOX 20美元</option>
                <option value="XBOXUSD50" <?php if($obj['codeType']=='XBOXUSD50'){?> selected <?php } ?>>XBOX 50美元</option>
                <option value="XBOXUSD100" <?php if($obj['codeType']=='XBOXUSD100'){?> selected <?php } ?>>XBOX 100美元</option>
<?php 
  }
  if($codeType == "" || $codeType == "GOOGLUSD"){
?>
                <option value="GOOGLUSD10" <?php if($obj['codeType']=='GOOGLUSD10'){?> selected <?php } ?>>GOOGLE 10美元</option>
                <option value="GOOGLUSD20" <?php if($obj['codeType']=='GOOGLUSD20'){?> selected <?php } ?>>GOOGLE 20美元</option>
                <option value="GOOGLUSD50" <?php if($obj['codeType']=='GOOGLUSD50'){?> selected <?php } ?>>GOOGLE 50美元</option>
                <option value="GOOGLUSD100" <?php if($obj['codeType']=='GOOGLUSD100'){?> selected <?php } ?>>GOOGLE 100美元</option>
<?php 
  }
  if($codeType == "" || $codeType == "AMZNUSD"){
?>
                <option value="AMZNUSD20" <?php if($obj['codeType']=='AMZNUSD20'){?> selected <?php } ?>>AMZN 20美元</option>
                <option value="AMZNUSD50" <?php if($obj['codeType']=='AMZNUSD50'){?> selected <?php } ?>>AMZN 50美元</option>
                <option value="AMZNUSD100" <?php if($obj['codeType']=='AMZNUSD100'){?> selected <?php } ?>>AMZN 100美元</option>
<?php 
  }
  if($codeType == "" || $codeType == "STEAMUSD"){
?>
                <option value="STEAMUSD10" <?php if($obj['codeType']=='STEAMUSD10'){?> selected <?php } ?>>STEAM 10美元</option>
                <option value="STEAMUSD20" <?php if($obj['codeType']=='STEAMUSD20'){?> selected <?php } ?>>STEAM 20美元</option>
                <option value="STEAMUSD50" <?php if($obj['codeType']=='STEAMUSD50'){?> selected <?php } ?>>STEAM 50美元</option>
                <option value="STEAMUSD100" <?php if($obj['codeType']=='STEAMUSD100'){?> selected <?php } ?>>STEAM 100美元</option>
<?php 
  }
?>
            </select>
        </div>
<?php
  if(empty($uid)){
?>
        <div class="col-4 themed-grid-col">
          <button type="button" id="btnSave" class="btn btn-secondary">save</button>
		</div>
<?php 
  }
?>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
            <label for="codeCd">CodeCd</label>
            <input type="text" class="form-control" id="codeCd" value="<?php echo $obj['codeCd'] ?>">
        </div>
      </div>
<?php
  if(!empty($uid)){
?>
      <hr class="mb-4">
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
<?php 
    if($obj["status"] == 'unused') {
?>
          <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
          <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
          <button type="button" id="btnInlid" class="btn btn-secondary actionBtn">INV</button>
<?php 
    }else if($obj["status"] == 'using') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
      <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
<?php 
    }else if($obj["status"] == 'used') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
<?php 
    }else if($obj["status"] == 'invalid') {
?>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
<?php 
    }else if($obj["status"] == 'fin') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
<?php 
    }
?>
<?php 
  }
?>
        </div>
      </div>
  </div>
</div>
</body>
</html>