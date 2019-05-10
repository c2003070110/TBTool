<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyMontb.php';
?>
<html lang="ja">
<head>
<title>my montbell order</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<!--
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYMONTB") ?>";
$(function() {
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent().parent();
	}
    $(document).on("click", "#btnRemove", function() {
		var thisBox = getMyBox(this);
        thisBox.remove();
		recalc();
    });
    $(document).on("click", "#btnPinyouJapan", function() {
		var itemBoxes = $(".productbox");
		var productUidList = [];
		for(var i=0; i<itemBoxes.length; i++){
			var urlVal = $(itemBoxes[i]).find("#productUid").val();
			productUidList[i] = urlVal;
		}
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"makePinyouJapan", 
					   "productUidList" : productUidList
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnPinyouChinaPX", function() {
		var itemBoxes = $(".productbox");
		var productUidList = [];
		for(var i=0; i<itemBoxes.length; i++){
			var urlVal = $(itemBoxes[i]).find("#productUid").val();
			productUidList[i] = urlVal;
		}
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"makePinyouChinaPX", 
					   "productUidList" : productUidList
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnPinyouChinaMJ", function() {
		var itemBoxes = $(".productbox");
		var productUidList = [];
		for(var i=0; i<itemBoxes.length; i++){
			var urlVal = $(itemBoxes[i]).find("#productUid").val();
			productUidList[i] = urlVal;
		}
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"makePinyouChinaMJ", 
					   "productUidList" : productUidList
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnMboff", function() {
		var thisBox = getMyBox(this);
		var productUid = $(thisBox).find("#productUid").val();
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateProductStatus", 
					   "uid" : productUid, 
					   "status" : "mboff"
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
	var recalc = function(){
		var priceList = $(".priceOffTax");
		var ttlPrice = 0;
		for(var i=0;i<priceList.length;i++){
			var price = parseInt($(priceList[i]).val());
			ttlPrice = ttlPrice + price;
		}
		$("#ttlJPY").val(ttlPrice);
    };
	recalc();
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
<?php
  $my = new MyMontb();
  $dataArr = $my->listProductInfoByPinyou();
?>
<?php
  foreach ($dataArr as $data) {
	  /*
	  if(empty($data['maijia'])){
		  $my->deleteProductInfoByUid($data['uid']);
		  continue;
	  }
	  */
	  $editFlag = false;
?>
  <div class="box productbox border border-primary mb-4 pl-2">
    <input type="hidden" id="productUid" value="<?php echo $data['productUid'] ?>">
    <input type="hidden" id="tbUid" value="<?php echo $data['tbUid'] ?>">
    <div class="row mb-1 pt-1">
      <div class="col-4">
		 <a class="btn btn-primary" href="/myphp/mymontb/page_regTBOrder.php?uid=<?php echo $data['tbUid'] ?>">
          <?php echo $data['maijia'] ?>
        </a>
	  </div>
      <div class="col-8">
	    <input type="text" class="form-control" id="dingdanDt" value="<?php echo $data['dingdanDt'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
	  </div>
    </div>
    <div class="row mb-1 form-group_product">
      <div class="col-4">
		<input type="text" class="form-control" id="productId" value="<?php echo $data['productId'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
      </div>
      <div class="col-4">
		<input type="text" class="form-control" id="colorName" value="<?php echo $data['colorName'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
      </div>
      <div class="col-3">
		<input type="text" class="form-control" id="sizeName" value="<?php echo $data['sizeName'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
      </div>
    </div>
    <div class="row mb-1 form-group_product">
      <div class="col-6">
		<input type="text" class="form-control priceOffTax" id="priceOffTax" value="<?php echo $data['priceOffTax'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
      </div>
      <div class="col-6">
		<input type="text" class="form-control" id="stock" value="<?php echo $data['stock'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
      </div>
    </div>
    <div class="row mb-1 form-group_product">
      <div class="col-4">
	    <button class="btn btn-secondary actionBtn" id="btnRemove" type="button">REM</button>
	  </div>
      <div class="col-4">
	    <button class="btn btn-secondary actionBtn" id="btnMboff" type="button">mboff</button>
	  </div>
    </div>
  </div>
<?php
  }
?>
  <div class="box mb-4">
    <div class="col-12">
	  <label for="stock">合计金额</label>
	  <input type="text" class="form-control" id="ttlJPY" readOnly>
    </div>
  </div>
  <div class="row mb-4 pl-3">
	<div class="col-4">
	  <button type="button" id="btnPinyouJapan" class="btn btn-secondary">WO 邮!</button>
	</div>
	<div class="col-4">
	  <button type="button" id="btnPinyouChinaMJ" class="btn btn-secondary">MB 邮MJ!</button>
	</div>
	<div class="col-4">
	  <button type="button" id="btnPinyouChinaPX" class="btn btn-secondary">MB 邮PX!</button>
	</div>
  </div>
</div>
</body>
</html>