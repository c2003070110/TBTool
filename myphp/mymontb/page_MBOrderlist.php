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
    $(document).on("click", "#btnDelRow", function() {
        var thisBox = $(this).parent().parent();
		var param = {};
		param.action = "deleteOrder";
		param.uid = $(thisBox).find("#uid").val();
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            location.reload();
        });
        //thisBox.remove();
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
  $status = '';
  if(isset($_GET['status'])){
	$status = $_GET['status'];
  }
  if($status == 'unorder'){
	  $cssBgUnorder = "bg-success text-white";
  }else if($status == 'ordered'){
	  $cssBgOrdered= "bg-success text-white";
  }else if($status == 'fin'){
	  $cssBgfin= "bg-success text-white";
  }else if($status == 'mbfh'){
	  $cssBgmbfh= "bg-success text-white";
  }else{
	  $cssBgAll = "bg-success text-white";
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnorder ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=unorder">unorder</a></li>
    <li class="list-group-item <?php echo $cssBgOrdered ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=ordering">ordering</a></li>
    <li class="list-group-item <?php echo $cssBgOrdered ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=ordered">ordered</a></li>
    <li class="list-group-item <?php echo $cssBgmbfh ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=mbfh">MBFH</a></li>
    <li class="list-group-item <?php echo $cssBgfin ?>"><a href="/myphp/mymontb/page_MBOrderlist.php?status=fin">fin</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mymontb/page_MBOrderlist.php">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $my = new MyMontb();
  if ($status !== ''){
	  $dataArr = $my->listMBOrderInfoByStatus($status);
  }else{
	  $dataArr = $my->listAllMBOrderInfo();
  }
?>
  <div class="row">
    <div class="col-8 text-break themed-grid-col border border-primary bg-info text-white">收件人PY</div>
<?php
    if($status == ''){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">status</div>
<?php
    }
?>
<?php
    if($status == 'ordered'){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">MB官网订单号</div>
<?php
    }else if($status == 'mbfh'){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">快递号</div>
<?php
    }
?>
  </div>
<?php
  foreach ($dataArr as $data) {
	  $boxCss = "bg-light";
	  if($data['status'] == 'unorder'){
		  $boxCss = "bg-warning text-white";
	  }else if($data['status'] == 'ordered'){
		  $boxCss = "bg-success text-white";
	  }
	  $boxCss = "";
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="col-8 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mymontb/page_orderMBOrder.php?uid=<?php echo $data['uid'] ?>">
<?php
    if(empty($data['firstName'])){
		echo "Order!";
    }else{
        echo $data['firstName']. " " . $data['lastName'] . " " . $data['tel'];
    }
?>
	  </a>
	</div>
<?php
    if($status == ''){
?>
    <div class="col-4 text-break themed-grid-col border border-primary"><?php echo $data['status'] ?></div>
<?php
    }
?>
<?php
    if($status == 'ordered'){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white"><?php echo $data['mbOrderNo'] ?></div>
<?php
    }else if($status == 'mbfh'){
    	$transferNo = empty($data['transferNoGuonei']) ? $data['transferNoGuoji'] : $data['transferNoGuonei'];
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white"><?php echo $transferNo  ?></div>
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