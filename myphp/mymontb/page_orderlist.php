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
var actionUrl = "<?php echo constant("URL_ACTION_MYDAIGOU") ?>";
var autocompleteUrl = "<?php echo constant("URL_AUTOCOMPLETE_MYDAIGOU") ?>";
$(function() {
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
<?php
  $status = '';
  if(isset($_GET['status'])){
	$status = $_GET['status'];
  }
  $maijia = '';
  if(isset($_GET['maijia'])){
	$maijia = $_GET['maijia'];
  }
  if($status == 'unorder'){
	  $cssBgUnorder = "bg-success text-white";
  }else if($status == 'ordered'){
	  $cssBgOrdered= "bg-success text-white";
  }else{
	  $cssBgAll = "bg-success text-white";
  }
  if(isset($maijia) && $maijia != ''){
?>
  <h3>买家:<span id="buyer"><?php echo $maijia ?></span></h3>
  <hr class="mb-4">
<?php
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnorder ?>"><a href="/myphp/mymontb/page_orderlist.php?maijia=<?php echo $maijia ?>&status=unorder">unorder</a></li>
    <li class="list-group-item <?php echo $cssBgOrdered ?>"><a href="/myphp/mymontb/page_orderlist.php?maijia=<?php echo $maijia ?>&status=ordered">ordered</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mymontb/page_orderlist.php?maijia=<?php echo $maijia ?>">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $my = new MyMontb();
  if((isset($status) && $status != '') && (isset($maijia) && $maijia != '')){
	  $dataArr = $my->listItemByMaijiaAndStatus($maijia, $status);
  }else if (isset($maijia) && $maijia != ''){
	  $dataArr = $my->listOrderInfoByMaijia($maijia);
  }else if (isset($status) && $status != ''){
	  $dataArr = $my->listOrderInfoByStatus($status);
  }else{
	  $dataArr = $my->listAllItem();
  }
  
?>
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">maijia</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">dingdianhao</div>
<?php
    if($status == ''){
?>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">状态</div>
<?php
    }
?>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">mbOrderNo</div>
  </div>
<?php
  foreach ($dataArr as $data) {
	  $boxCss = "bg-light";
	  if($data['status'] == 'unorder'){
		  $boxCss = "bg-danger text-white";
	  }else if($data['status'] == 'ordered'){
		  $boxCss = "bg-warning text-white";
	  }
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="col-4 text-break themed-grid-col border border-secondary">
	    <?php echo $data['maijia'] ?>
	</div>
    <div class="col-4 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mymontb/page_regOrder.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data['dingdanhao'] ?>
	  </a>
	</div>
<?php
    if($status == ''){
?>
    <div class="col-2 text-break themed-grid-col border border-secondary"><?php echo $data["status"] ?></div>
<?php
    }
?>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceJPY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceCNY"] ?></div>
    <div class="col-3 text-break themed-grid-col border">
<?php
    if($data["mbOrderNo"] == ''){
      if($data["status"] == 'unorder'){
?>
	  <a href="/myphp/mymontb/page_orderOrder.php?uid=<?php echo $data['uid'] ?>">order!</a>
<?php
      }else{
?>
	  <a href="/myphp/mymontb/page_regOrder.php?uid=<?php echo $data['uid'] ?>">fill</a>
<?php
      }
    }else{
?>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["mbOrderNo"] ?></div>
<?php
    }
?>
    </div>
  </div>
<?php
  }
?>

</div>
</body>
</html>