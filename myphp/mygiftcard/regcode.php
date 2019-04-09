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
var actionUrl = "http://133.130.114.129/myphp/mygiftcard/action.php";
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
            thisBox.find("#codeCd").val("");
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
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php">stock list</a></li>
  </ul>   
  <hr class="mb-4">
  <textarea id="tempTxtArea" rows="6" cols="35"></textarea>
  <hr class="mb-4">
<?php
  $uid = '';
  if(isset($_GET['uid'])){
	$uid = $_GET['uid'];
  }
  if($uid !== ''){
	$my = new MyGiftCard();
	$obj = $my->listCodeByUid($uid);
  }
?>
  <input type="hidden" id="uid" value="<?php echo $uid ?>">
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
		  <label for="orderNo">OrderNo</label>
		  <input type="text" class="form-control" id="orderNo" value="<?php echo $obj['orderNo'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-8 themed-grid-col">
            <label for="codeType">CodeType</label>
            <select class="custom-select d-block w-100" id="codeType">
                <option value="" selected></option>
                <option value="PSNUSD10" <?php if($obj['codeType']=='PSNUSD10'){?> selected <?php } ?>>PSN 10美元</option>
                <option value="PSNUSD20" <?php if($obj['codeType']=='PSNUSD20'){?> selected <?php } ?>>PSN 20美元</option>
                <option value="PSNUSD50" <?php if($obj['codeType']=='PSNUSD50'){?> selected <?php } ?>>PSN 50美元</option>
                <option value="PSNUSD100" <?php if($obj['codeType']=='PSNUSD100'){?> selected <?php } ?>>PSN 100美元</option>
                <option value="XBOXUSD10" <?php if($obj['codeType']=='XBOXUSD10'){?> selected <?php } ?>>XBOX 10美元</option>
                <option value="XBOXUSD20" <?php if($obj['codeType']=='XBOXUSD10'){?> selected <?php } ?>>XBOX 20美元</option>
                <option value="XBOXUSD50" <?php if($obj['codeType']=='XBOXUSD50'){?> selected <?php } ?>>XBOX 50美元</option>
                <option value="XBOXUSD100" <?php if($obj['codeType']=='XBOXUSD100'){?> selected <?php } ?>>XBOX 100美元</option>
            </select>
        </div>
<?php
  if($uid == ''){
?>
        <div class="col-4 themed-grid-col">
          <button type="button" id="btnSave" class="btn btn-secondary actionBtn">save</button>
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
      <hr class="mb-4">
<?php
  if($uid !== ''){
?>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
<?php 
    if($data["status"] == 'unused') {
?>
          <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
          <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
          <button type="button" id="btnInlid" class="btn btn-secondary actionBtn">INV</button>
<?php 
    }else if($data["status"] == 'using') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
      <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
<?php 
    }else if($data["status"] == 'used') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
<?php 
    }else if($data["status"] == 'invalid') {
?>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
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