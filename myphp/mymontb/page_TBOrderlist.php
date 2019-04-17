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
		return $(thisElement).parent().parent();
	}
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateTBOrderStatus", 
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
    $(document).on("click", "#btnfin", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "fin");
    });
	/*
    $(document).on("click", "#btnMBoff", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "mboff");
    });
    $(document).on("click", "#btnCancel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "cancel");
    });
	*/
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
  $status = $_GET['status'];
  $my = new MyMontb();
  if($status == 'mbUnorder'){
	  $cssBgUnorder = "bg-warning text-white";
  }else if($status == 'mbOrdered'){
	  $cssBgMBOrdered= "bg-warning text-white";
  }else if($status == 'mbfh'){
	  $cssBgmbfh= "bg-warning text-white";
  }else{
	  $cssBgAll = "bg-warning text-white";
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnorder ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=mbUnorder">mbUnorder</a></li>
    <li class="list-group-item <?php echo $cssBgMBOrdered ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=mbOrdered">mbOrdered</a></li>
    <li class="list-group-item <?php echo $cssBgmbfh ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=mbfh">MBFH</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mymontb/page_TBOrderlist.php">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $sort = array();
  if (!empty($status) && $status == "mbUnorder"){
	  $dataArr = $my->listTBOrderByMbUnorder();
	  foreach ((array) $dataArr as $key => $value) {
		$sort[$key] = $value['dingdanDt'];
	  }
	  array_multisort($sort, SORT_DESC, $dataArr);
	
  }else if (!empty($status) && $status == "mbOrdered"){
	  $dataArr = $my->listTBOrderByMbOrdered();
	  foreach ((array) $dataArr as $key => $value) {
		$sort[$key] = $value['mbOrderNo'];
	  }
	  array_multisort($sort, SORT_DESC, $dataArr);
	
  }else if (!empty($status) && $status == "mbfh"){
	  $dataArr = $my->listTBOrderByMbFahuo();
	  foreach ((array) $dataArr as $key => $value) {
		$sort[$key] = $value['mbOrderNo'];
	  }
	  array_multisort($sort, SORT_DESC, $dataArr);
  }else{
	  $dataArr = $my->listAllTBOrder();
	  foreach ((array) $dataArr as $key => $value) {
		$sort[$key] = $value['dingdanDt'];
	  }
	  array_multisort($sort, SORT_DESC, $dataArr);
  }
?>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="box border border-primary mb-1">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="row mb-4 form-group">
      <div class="col-7">
	    <label for="maijia">淘宝买家ID</label>
	    <a class="form-control btn btn-primary" href="/myphp/mymontb/page_regTBOrder.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data['maijia'] ?>
	  </a>
	  </div>
      <div class="col-5">
	    <label for="transferWay">快递方式</label>
	    <input type="text" class="form-control" id="transferWay" value="<?php echo $data['transferWay'] ?>" readOnly>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-12">
	    <label for="dingdanDt">TB下单日期</label>
	    <input type="text" class="form-control" id="dingdanDt" value="<?php echo $data['dingdanDt'] ?>" readOnly>
	  </div>
    </div>
<?php
    $dataProdArr = $my->listProductInfoByByTBUid($data['uid']);
	$mbUidArr = array();
	$lines = $data['maijiadianzhiHanzi'] . "\n";
    foreach ($dataProdArr as $productInfo) {
		$lines .= $productInfo["productId"]." ".$productInfo["colorName"]." ".$productInfo["sizeName"] . "\n";
		if(empty($productInfo["mbUid"]))continue;
		if(!in_array($productInfo["mbUid"], $mbUidArr)){
			$mbUidArr[] = $productInfo["mbUid"];
		}
	}
?>
    <div class="row mb-4 form-group">
      <div class="col-12">
	    <textarea id="tempTxtArea" class="form-control" rows="3" cols="40"><?php echo $lines ?></textarea>
	  </div>
    </div>
<?php
    foreach ($mbUidArr as $mbUid) {
		$mbOrderData = $my->listMBOrderInfoByUid($mbUid);  
?>
    <div class="row mb-4 form-group">
      <div class="col-5">
	    <label for="mbOrderNo">MB 官网订单号</label>
	    <a class="form-control btn btn-success" href="/myphp/mymontb/page_orderMBOrder.php?uid=<?php echo $mbOrderData['uid'] ?>">
	      <?php echo (!empty($mbOrderData['mbOrderNo']) ? $mbOrderData['mbOrderNo'] : "MB未")?>
	    </a>
	  </div>
      <div class="col-7">
		  <label for="transferNoGuoji">Transfer No</label>
		<input type="text" class="form-control" id="transferNoGuoji" value="<?php echo $mbOrderData['transferNoGuoji'] ?>">
      </div>
    </div>
<?php
	}
?>
  </div>
<?php
  }
?>
</div>
</body>
</html>