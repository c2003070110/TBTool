<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ . '/MyMontb.php';
?>
<html lang="ja">
<head>
<title>Reg/Modify Order</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYMONTB") ?>";
$(function() {
    $(document).on("click", "#btnDelProduct", function() {
        var thisBox = $(this).parent().parent();
        thisBox.remove();
    });
    $(document).on("click", "#btnAddProduct", function() {
        var thisBox = $(this).parent().parent();
        var cloneBox = thisBox.clone();
        $(thisBox).after(cloneBox);
    });
	var formParameter = function(){
		param = {};
        param.uid = $("#orderUid").val();
        param.maijia = $("#maijia").val();
        param.dingdanhao = $("#dingdanhao").val();
        param.maijiadianzhiHanzi = $("#maijiadianzhiHanzi").val();
		
		var itemBoxes = $(".form-group_product");
		var productList = "";
		for(var i=0; i<itemBoxes.length; i++){
			var productId = $(itemBoxes[i]).find("#productId").val();
			var colorName = $(itemBoxes[i]).find("#colorName").val();
			var sizeName = $(itemBoxes[i]).find("#sizeName").val();
            productList = productList + productId + "," + colorName + "," + sizeName;
			if(i != itemBoxes.length){
				productList = productList +";";
			}
		}
		param.productList = productList;
		
        param.mbOrderNo = $("#mbOrderNo").val();
		return param;
	}
    $(document).on("click", "#btnConvert", function() {
        var srcTxt = $("#tempTxtArea").val();
		var arr1 = srcTxt.split(/(\r\n|\n|\r)/gm);
		var productList = "";
		for(var i=0; i<arr1.length; i++){
			var strLoop1 = arr1[i];
			var cdArr = strLoop1.split(/[\s,;]+/);
			if(cdArr){
				var productId="",colorName="",sizeName="";
				for(var j=0; j<cdArr.length; j++){
					var arr2 = cdArr[j].split(":");
					if(arr2.length <2) continue;
					if(arr2[0].indexOf("XXX") == 0){
						productId = arr2[1];
					}
					if(arr2[0].indexOf("YYY") == 0){
						colorName = arr2[1];
					}
					if(arr2[0].indexOf("ZZZ") == 0){
						sizeName = arr2[1];
					}
				}
				if(productId != ""){
					productList = productList + productId + "," + colorName + "," + sizeName +";";
				}
			}
		}
		if(productList != ""){
			productList = productList.substring(0,productList.length-1);
			var param = formParameter();
			param.action = "saveOrder";
			param.productList = productList;
			var jqxhr = $.ajax(actionUrl,
							 { type : "GET",
							   data : param,
							   dataType : "html" 
							  }
						  );
			jqxhr.done(function( msg ) {
				location.reload();
			});
		}
    });
    $(document).on("click", "#btnSave", function() {
	    var param = formParameter();
		param.action = "saveOrder";
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
				location.reload();
        });
    });
    $(document).on("click", "#btnOrder", function() {
        
	    var param = formParameter();
		param.action = "order";
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
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
<?php
  $orderUid = '';
  if(isset($_GET['uid'])){
	$orderUid = $_GET['uid'];
  }
  $editFlag = ($orderUid === '');
  if($orderUid !== ''){
	$my = new MyMontb();
	$orderObj = $my->listOrderInfoByUid($orderUid);
	$editFlag = ($orderObj["status"] == 'unorder' || $orderObj["status"] == 'ordered');
  }
?>
  <input type="hidden" id="orderUid" value="<?php echo $orderUid ?>">
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="maijia">淘宝买家ID</label>
		  <input type="text" class="form-control" id="maijia" value="<?php echo $orderObj['maijia'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="dingdanhao">淘宝订单号</label>
		  <input type="text" class="form-control" id="dingdanhao" value="<?php echo $orderObj['dingdanhao'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
            <label for="maijiadianzhiHanzi">买家地址</label>
            <input type="text" class="form-control" id="maijiadianzhiHanzi" value="<?php echo $orderObj['maijiadianzhiHanzi'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
      </div>
<?php
  if($orderObj["status"] == 'unorder'){
?>
      <div class="row mb-4 form-group">
        <div class="col-4 themed-grid-col">
            <label for="tempTxtArea">product lines</label>
          <button type="button" id="btnConvert" class="btn btn-secondary">C o n v e r t !</button>
		</div>
        <div class="col-8 themed-grid-col">
		  <textarea id="tempTxtArea" rows="6" cols="35"></textarea>
		</div>
      </div>
<?php
  }
?>
<?php
  if($orderUid === '' || $orderObj["status"] == 'unorder'){
?>
      <div class="row mb-4 form-group_product" id="productBox">
        <div class="col-4">
		  <label for="productId">productId</label>
		  <input type="text" class="form-control" id="productId" value="<?php echo $prodObj['productId'] ?>">
        </div>
        <div class="col-4">
		  <label for="colorName">color</label>
		  <input type="text" class="form-control" id="colorName" value="<?php echo $prodObj['colorName'] ?>">
        </div>
        <div class="col-3">
		  <label for="sizeName">size</label>
		  <input type="text" class="form-control" id="sizeName" value="<?php echo $prodObj['sizeName'] ?>">
        </div>
        <div class="col-1">
		  <button type="button" id="btnAddProduct" class="btn btn-secondary">N</button>
		  <button type="button" id="btnDelProduct" class="btn btn-secondary">D</button>
        </div>
      </div>
<?php
  }
?>
<?php
  foreach($orderObj['productObjList'] as $prodObj){
?>
      <div class="row mb-1 form-group_product" id="productBox">
        <div class="col-4">
		  <label for="productId">productId</label>
		  <input type="text" class="form-control" id="productId" value="<?php echo $prodObj['productId'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
        <div class="col-3">
		  <label for="colorName">color</label>
		  <input type="text" class="form-control" id="colorName" value="<?php echo $prodObj['colorName'] ?> <?php if(!$editFlag){?> readOnly <?php } ?>">
        </div>
        <div class="col-3">
		  <label for="sizeName">size</label>
		  <input type="text" class="form-control" id="sizeName" value="<?php echo $prodObj['sizeName'] ?> <?php if(!$editFlag){?> readOnly <?php } ?>">
        </div>
        <div class="col-1">
		  <button type="button" id="btnAddProduct" class="btn btn-secondary">N</button>
		  <button type="button" id="btnDelProduct" class="btn btn-secondary">D</button>
        </div>
      </div>
<?php
  }
?>
      <div class="row mb-1 form-group_product">
        <label for="transferWay">快递方式</label>
        <select class="custom-select form-control parcelAmt" id="transferWay" <?php if(!$editFlag){?> disabled <?php } ?>>
            <option value=""></option>
            <option value="zhiYou" <?php if($orderObj['transferWay']=='zhiYou'){?> selected <?php } ?>>zhiYou</option>
            <option value="pinYou" <?php if($orderObj['transferWay']=='pinYou'){?> selected <?php } ?>>拼邮</option>
        </select>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-5">
		  <button type="button" id="btnSave" class="btn btn-secondary">SAVE</button>
        </div>
      </div>
  </div>
</div>
</body>
</html>