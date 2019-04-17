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
        param.dingdanDt = $("#dingdanDt").val();
        param.status = $("#status").val();
        param.transferWay = $("#transferWay").val();
		
		var itemBoxes = $(".form-group_product");
		var productList = "";
		for(var i=0; i<itemBoxes.length; i++){
			var productId = $(itemBoxes[i]).find("#productId").val();
			if(productId == "" || productId == undefined) continue;
			var colorName = $(itemBoxes[i]).find("#colorName").val();
			var sizeName = $(itemBoxes[i]).find("#sizeName").val();
            productList = productList + productId.replace(/^\s+|\s+$/g,'') + "," + colorName.replace(/^\s+|\s+$/g,'') + "," + sizeName.replace(/^\s+|\s+$/g,'');
			if(i != itemBoxes.length){
				productList = productList +";";
			}
		}
		param.productList = productList;
		
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
					var arr2 = cdArr[j].split(/[:：]+/);
					if(arr2.length <2) continue;
					if(arr2[0].indexOf("商家编码") == 0){
						productId = arr2[1].replace(/^\s+|\s+$/g,'');
						var wr = productId.split(/[-,_]+/);
						if(wr.length == 3){
							productId = wr[2];
						}else{
							productId = wr[0];
						}
					}
					if(arr2[0].indexOf("颜色分类") == 0){
						colorName = arr2[1].replace(/^\s+|\s+$/g,'');
					}
					if(arr2[0].indexOf("尺码") == 0){
						sizeName = arr2[1].replace(/^\s+|\s+$/g,'');
					}
					if(arr2[0].indexOf("鞋码") == 0){
						sizeName = arr2[1].replace(/^\s+|\s+$/g,'');
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
			param.action = "saveTBOrder";
			param.productList = productList;
			var jqxhr = $.ajax(actionUrl,
							 { type : "GET",
							   data : param,
							   dataType : "html" 
							  }
						  );
			jqxhr.done(function( msg ) {
				var href = window.location.href;
				if(href.indexOf("uid") == -1){
					var url = href +"?uid="+msg;
					window.location.href = url;
				}else{
					location.reload();
				}
			});
		}
    });
    $(document).on("click", "#btnSave", function() {
	    var param = formParameter();
		param.action = "saveTBOrder";
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			var href = window.location.href;
			if(href.indexOf("uid") == -1){
				var url = href +"?uid="+msg;
				window.location.href = url;
			}else{
			location.reload();
			}
        });
    });
    $(document).on("click", "#btnDelete", function() {
	    var param = formParameter();
		param.action = "deleteTBOrder";
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			if(msg != ""){
				if(msg.indexOf("ERROR") == -1){
					// success
					$("#orderUid").val("");
				}
				alert(msg);
			}
			location.reload();
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
  $my = new MyMontb();
  if($orderUid !== ''){
	$orderObj = $my->listTBOrderInfoByUid($orderUid);
	$editFlag = !($my->isTBOrderForMBOrderedByTBUid($orderUid));
  }
  //var_dump($editFlag);
?>
  <input type="hidden" id="orderUid" value="<?php echo $orderUid ?>">
  <input type="hidden" id="status" value="<?php echo $orderObj["status"] ?>">
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
		  <label for="dingdanhao">下单时间</label>
		  <input type="text" class="form-control" id="dingdanDt" value="<?php echo $orderObj['dingdanDt'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
            <label for="maijiadianzhiHanzi">买家地址</label>
            <textarea class="form-control" cols="40" rows="2" id="maijiadianzhiHanzi" <?php if(!$editFlag){?> readOnly <?php } ?>><?php echo $orderObj['maijiadianzhiHanzi'] ?></textarea >
        </div>
      </div>
      <div class="row mb-4 form-group_product">
        <div class="col-12">
          <label for="transferWay">快递方式</label>
          <select class="custom-select form-control parcelAmt" id="transferWay" <?php if(!$editFlag){?> disabled <?php } ?>>
              <option value=""></option>
              <option value="mbzhiYou" <?php if($orderObj['transferWay']=='mbzhiYou'){?> selected <?php } ?>>MB直邮</option>
              <option value="wozhiYou" <?php if($orderObj['transferWay']=='wozhiYou'){?> selected <?php } ?>>WO直邮</option>
              <option value="pinYou" <?php if($orderObj['transferWay']=='pinYou'){?> selected <?php } ?>>拼邮</option>
          </select>
        </div>
      </div>
<?php
  if($editFlag){
?>
      <div class="row mb-4 form-group">
        <div class="col-4 themed-grid-col">
            <label for="tempTxtArea">product lines</label>
          <button type="button" id="btnConvert" class="btn btn-secondary">C o n v e r t !</button>
		</div>
        <div class="col-8 themed-grid-col">
		  <textarea class="form-control" id="tempTxtArea" rows="6" cols="35"></textarea>
		</div>
      </div>
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
  $prodArr = $my->listProductInfoByByTBUid($orderUid);
  foreach($prodArr as $prodObj){
?>
      <div class="row mb-1 form-group_product" id="productBox">
        <div class="col-4">
		  <label for="productId">productId</label>
		  <input type="text" class="form-control" id="productId" value="<?php echo $prodObj['productId'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
        <div class="col-3">
		  <label for="colorName">color</label>
		  <input type="text" class="form-control" id="colorName" value="<?php echo $prodObj['colorName'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>">
        </div>
        <div class="col-3">
		  <label for="sizeName">size</label>
		  <input type="text" class="form-control" id="sizeName" value="<?php echo $prodObj['sizeName'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>">
        </div>
<?php
    if($editFlag){
?>
        <div class="col-1">
		  <button type="button" id="btnAddProduct" class="btn btn-secondary">N</button>
		  <button type="button" id="btnDelProduct" class="btn btn-secondary">D</button>
        </div>
<?php
    }
?>
      </div>
<?php
  }
?>
      <div class="row mb-4 form-group">
<?php
    if($editFlag){
?>
        <div class="col-5">
		  <button type="button" id="btnSave" class="btn btn-secondary">SAVE</button>
        </div>
        <div class="col-5">
		  <button type="button" id="btnDelete" class="btn btn-secondary">删除</button>
        </div>
<?php
    }
?>
      </div>
  </div>
</div>
</body>
</html>