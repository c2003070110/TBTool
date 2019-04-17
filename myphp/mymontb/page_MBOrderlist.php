<?php
/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/
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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
<!--
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
-->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYMONTB") ?>";
$(function() {
	$( "#accordion" ).accordion({
      collapsible: true
    });
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent();
	}
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateMBOrderStatus", 
					   "uid" : thisBox.find("#uid").val(),
					   "status" : status
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
	};
    $(document).on("click", "#btnReOrding", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "ordering");
    });
    $(document).on("click", "#btnCancel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "cancel");
    });
    $(document).on("click", "#btnReUnOrder", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "unorder");
    });
    $(document).on("click", "#btnfin", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "fin");
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
  $status = $_GET['status'];
  if($status == 'unorder'){
	  $cssBgUnorder = "bg-warning text-white";
  }else if($status == 'ordering'){
	  $cssBgOrdering= "bg-warning text-white";
  }else if($status == 'ordered'){
	  $cssBgOrdered= "bg-warning text-white";
  }else if($status == 'fin'){
	  $cssBgfin= "bg-warning text-white";
  }else if($status == 'mbfh'){
	  $cssBgmbfh= "bg-warning text-white";
  }else{
	  $cssBgAll = "bg-warning text-white";
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnorder ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=unorder">unorder</a></li>
    <li class="list-group-item <?php echo $cssBgOrdering ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=ordering">ordering</a></li>
    <li class="list-group-item <?php echo $cssBgOrdered ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=ordered">ordered</a></li>
    <li class="list-group-item <?php echo $cssBgmbfh ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=mbfh">MBFH</a></li>
    <li class="list-group-item <?php echo $cssBgfin ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=fin">FIN</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mymontb/page_MBOrderlist.php">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $my = new MyMontb();
  if (!empty($status)){
	  $dataArr = $my->listMBOrderInfoByStatus($status);
  }else{
	  $dataArr = $my->listAllMBOrderInfo();
  }
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	$sort[$key] = $value['mbOrderNo'];
  }
  array_multisort($sort, SORT_DESC, $dataArr);
  //var_dump($dataArr);
?>
  <div id="accordion">
<?php
  foreach ($dataArr as $data) {
	  $h3Txt = "";
	    if(!empty($data['mbOrderNo'])){
		  $h3Txt = $data['mbOrderNo'];
	    }else{
		  $h3Txt = "MB未";
	  }
	  $orderDtllinkTxt = "";
	  if(!empty($data['firstName'])){
	      $orderDtllinkTxt = $data['firstName']. " " . $data['lastName'];
	  }else{
		  $orderDtllinkTxt = "Adr未";
		}
?>  
    <h3 class="bg-success"><?php echo $h3Txt ?></h3>
      <div class="box border border-primary mb-4 pl-2">
        <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
        <div class="row mb-1 p-2">
    	    <a class="btn btn-primary" href="/myphp/mymontb/page_orderMBOrder.php?uid=<?php echo $data['uid'] ?>">
              <?php echo $orderDtllinkTxt ?>
    	    </a>
	    </div>
        <div class="row mb-1 p-2">
	        <?php if(!empty($data['status'])){?><span class="border p-2"><?php echo $data['status']; } ?></span>
	        <?php if(!empty($data['mbOrderNo'])){?><span class="border p-2"><?php echo $data['mbOrderNo']; } ?></span>
	        <?php if(!empty($data['transferNoGuoji'])){?><span class="border p-2"><?php echo $data['transferNoGuoji']; } ?></span>
	        <?php if(!empty($data['transferNoGuonei'])){?><span class="border p-2"><?php echo $data['transferNoGuonei']; } ?></span>
	    </div>
<?php
		$dproductInfoArr = $my->listProductInfoByMBUid($data["uid"]);
		$tbUidArr = array();
		foreach ($dproductInfoArr as $productInfo) {	
			if(!in_array($productInfo["tbUid"], $tbUidArr)){
				$tbUidArr[] = $productInfo["tbUid"];
			}
		}
		$lines = "";
?>
<?php
		//var_dump( $tbUidArr);
		foreach ($tbUidArr as $tbUid) {
			$tbObj = $my->listTBOrderInfoByUid($tbUid);
			$lines .= $tbObj['maijiadianzhiHanzi'] . "\n";
?>
        <div class="row mb-1 p-2 col-12">
    	    <a class="btn btn-primary" href="/myphp/mymontb/page_regTBOrder.php?uid=<?php echo $tbObj['uid'] ?>">
              <span><?php echo $tbObj["maijia"] ?></span>
    	    </a>
		    <span class="border p-2"><?php echo $tbObj["dingdanhao"] ?></span>
		    <span class="border p-2"><?php echo $tbObj["dingdanDt"] ?></span>
		    <span class="border p-2"><?php echo $tbObj["transferWay"] ?></span>
		    <span class="border p-2"><?php echo $tbObj["maijiadianzhiHanzi"] ?></span>
	    </div>
<?php
			$tbObj = $my->listTBOrderInfoByUid($tbUid);
			$dproductInfoArr = $my->listProductInfoByByTBUid($tbUid);
			foreach ($dproductInfoArr as $productInfo) {
				$lines .= $productInfo["productId"]." ".$productInfo["colorName"]." ".$productInfo["sizeName"] . "\n";
?>
        <div class="row mb-1 p-2">
    	    <a class="btn btn-primary" href="https://webshop.montbell.jp/goods/disp.php?product_id=<?php echo $productInfo['productId'] ?>" target="blank">
              <span>P<?php echo $productInfo["productId"] ?></span>
    	    </a>
		    <span class="border p-2"><?php echo $productInfo["colorName"] ?></span>
		    <span class="border p-2"><?php echo $productInfo["sizeName"] ?></span>
		    <span class="border p-2"><?php echo $productInfo["priceOffTax"] ?></span>
	    </div>
<?php
			}
        }
?>
        <div class="row mb-1 p-4">
<?php
		if($data["status"] == 'unorder'){
?>
	        <a href="/myphp/mymontb/page_orderMBOrder.php?uid=<?php echo $data['uid'] ?>">
	    	  MB下单!
	        </a> 
		    <button class="btn btn-secondary actionBtn" id="btnCancel" type="button">删 除</button>
<?php
		}else if($data["status"] == 'ordering'){
?>
	    	<button class="btn btn-secondary actionBtn" id="btnReUnOrder" type="button">RE-UNOR</button>
<?php
		}else if($data["status"] == 'ordered'){
?>
	    	<button class="btn btn-secondary actionBtn" id="btnReOrding" type="button">RE-OR</button>
<?php
		}else if($data["status"] == 'mbfh'){
?>
	    	<button class="btn btn-secondary actionBtn" id="btnfin" type="button">fin</button>
<?php
		}
?>
        </div>
<?php
		if($data["status"] == 'mbfh'){
?>
        <div class="row mb-4 form-group">
          <div class="col-12">
	        <textarea id="tempTxtArea" class="form-control" rows="3" cols="40"><?php echo $lines ?></textarea>
	      </div>
        </div>
      </div>
<?php
		}
?>
<?php
  }
?>
  </div>
</div>
</body>
</html>