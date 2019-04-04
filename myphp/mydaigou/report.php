<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyDaiGou.php';
  if(!isset($_GET['buyer'])){
	exit(0);
  }
  $buyer = $_GET['buyer'];
  $reportDiv = '';
  if(isset($_GET['reportDiv'])){
	$reportDiv = $_GET['reportDiv'];
  }
  $reportForSelfFlag = ($reportDiv !== "buyer");
?>
<html lang="ja">
<head>
<title>report</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYDAIGOU") ?>";
var autocompleteUrl = "<?php echo constant("URL_AUTOCOMPLETE_MYDAIGOU") ?>";
$(function() {
	var toInt = function(data){
		return !data ? 0 : parseInt(data, 10);
	}
	var parseDouble = function(data){
		return !data ? 0 : parseFloat(data, 10);
	}
    $(document).on("change", ".form-control", function() {
        var xiaojiJPY = toInt($("#xiaojiJPY").val());
        var xiaojiCNY = toInt($("#xiaojiCNY").val());
        var yunfeiJPY = toInt($("#yunfeiJPY").val());
        var yunfeiCNY = toInt($("#yunfeiCNY").val());
        var currencyRate = parseDouble($("#currencyRate").val());
		
        $("#hejiCNY").val(xiaojiCNY + yunfeiCNY);
        $("#hejiJPY").val(xiaojiJPY + yunfeiJPY);
		var cny1 = toInt($("#hejiCNY").val());
		var cny2 = toInt(toInt($("#hejiJPY").val()) * currencyRate);
        $("#liyiCNY").val(cny1-cny2);
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <h3>买家:<span id="buyer"><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
<?php
  if($reportForSelfFlag == true){
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/report.php?reportDiv=buyer&buyer=<?php echo $buyer ?>">REPORT FOR BUYER</a></li>
  </ul>
  <hr class="mb-4">
<?php
  }
?>
<?php
  $mydaigou = new MyDaiGou();
  $dataArr = $mydaigou->listItemByBuyerAndStatus($buyer, 'gouru');  
?>
  <div class="row">
    <div class="col-6 text-break themed-grid-col border border-primary bg-info text-white">orderItem</div>
<?php
  if($reportForSelfFlag == true){
?>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">JPY</div>
<?php
  }
?>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">CNY</div>
  </div>
<?php
  $ttlJPY = 0;
  $ttlCNY = 0;
  foreach ($dataArr as $data) {
	  $ttlJPY = $ttlJPY + intval($data["priceJPY"]);
	  $ttlCNY = $ttlCNY + intval($data["priceCNY"]);
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <input type="hidden" id="orderItem" value="<?php echo $data['orderItem'] ?>">
    <input type="hidden" id="priceJPY" value="<?php echo $data['priceJPY'] ?>">
    <input type="hidden" id="priceCNY" value="<?php echo $data['priceCNY'] ?>">
    <div class="col-6 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mydaigou/modifyitem.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data['orderItem'] ?>
	  </a>
	</div>
<?php
     if($reportForSelfFlag == true){
?>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceJPY"] ?></div>
<?php
     }
?>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceCNY"] ?></div>
  </div>
<?php
  }
?>
  <hr class="mb-1">
  <div class="row">
<?php
  if($reportForSelfFlag == true){
?>
    <div class="col-2 text-break themed-grid-col border border-secondary">
	  <label for="xiaojiJPY">小计 JPY</label>
	  <input value="<?php echo $ttlJPY ?>" type="text" class="form-control" id="xiaojiJPY" >
	</div>
<?php
  }
?>
    <div class="col text-break themed-grid-col border border-secondary">
	  <label for="xiaojiCNY">小计</label>
	  <input value="<?php echo $ttlCNY ?>" type="text" class="form-control" id="xiaojiCNY" >
	</div>
  </div>
  <div class="row">
<?php
  if($reportForSelfFlag == true){
?>
    <div class="col-2 text-break themed-grid-col border border-secondary">
	  <label for="yunfeiJPY">运费 JPY</label>
	  <input type="text" class="form-control" id="yunfeiJPY" >
	</div>
<?php
  }
?>
    <div class="col text-break themed-grid-col border border-secondary">
	  <label for="yunfeiCNY">运费</label>
	  <input type="text" class="form-control" id="yunfeiCNY" >
	</div>
  </div>
  <hr class="mb-1">
  <div class="row">
<?php
  if($reportForSelfFlag == true){
?>
    <div class="col text-break themed-grid-col border border-secondary">
	  <label for="hejiJPY">合计 JPY</label>
	  <input type="text" class="form-control" id="hejiJPY" >
	</div>
<?php
  }
?>
    <div class="col-6 text-break themed-grid-col border border-secondary">
	  <label for="hejiCNY">合计</label>
	  <input type="text" class="form-control" id="hejiCNY" >
	</div>
  </div>
<?php
  if($reportForSelfFlag == true){
?>
  <hr class="mb-1">
  <div class="row">
    <div class="col text-break themed-grid-col border border-secondary">
	  <label for="currencyRate">汇率</label>
	  <input type="text" class="form-control" id="currencyRate">
	</div>
    <div class="col text-break themed-grid-col border border-secondary">
	  <label for="liyiCNY">利益</label>
	  <input type="text" class="form-control" id="liyiCNY" >
	</div>
  </div>
<?php
  }
?>
</div>
</body>
</html>