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
    $(document).on("click", "#btnMBoff", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "mboff");
    });
    $(document).on("click", "#btnCancel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "cancel");
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
  $maijia = $_GET['maijia'];
  if($status == 'st'){
	  $cssBgSt = "bg-success text-white";
  }else if($status == 'mbordered'){
	  $cssBgmbordered = "bg-success text-white";
  }else if($status == 'fin'){
	  $cssBgfin= "bg-success text-white";
  }else if($status == 'mbfh'){
	  $cssBgmbfh= "bg-success text-white";
  }else if($status == 'mboff'){
	  $cssBgmboff= "bg-success text-white";
  }else{
	  $cssBgAll = "bg-success text-white";
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgSt ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=st&maijia=<?php echo $maijia ?>">start</a></li>
    <li class="list-group-item <?php echo $cssBgmbordered ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=mbordered&maijia=<?php echo $maijia ?>">MB中</a></li>
    <li class="list-group-item <?php echo $cssBgmbfh ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=mbfh&maijia=<?php echo $maijia ?>">mbfh</a></li>
    <li class="list-group-item <?php echo $cssBgfin ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=fin&maijia=<?php echo $maijia ?>">fin</a></li>
    <li class="list-group-item <?php echo $cssBgmboff ?>"><a href="/myphp/mymontb/page_TBOrderlist.php?status=mboff&maijia=<?php echo $maijia ?>">mboff</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mymontb/page_TBOrderlist.php">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $my = new MyMontb();
  if(!empty($status) && !empty($maijia)){
	  $dataArr = $my->listTBOrderByMaijiaAndStatus($maijia, $status);
  }else if (!empty($maijia)){
	  $dataArr = $my->listTBOrderByMaijia($maijia);
  }else if (!empty($status)){
	  $dataArr = $my->listTBOrderByStatus($status);
  }else{
	  //var_dump($my);
	  $dataArr = $my->listAllTBOrder();
  }
  
?>
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">淘宝买家ID</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">淘宝订单号</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">下单日期</div>
<?php
    if($status == ''){
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">状态</div>
<?php
    }
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">Action</div>
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
    <div class="col-3 text-break border border-secondary">
	  <a href="/myphp/mymontb/page_TBOrderlist.php?maijia=<?php echo $data['maijia'] ?>">
	    <?php echo $data['maijia'] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mymontb/page_regTBOrder.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data['dingdanhao'] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary"><?php echo $data['dingdanDt'] ?></div>
<?php
    if($status == ''){
?>
    <div class="col-3 text-break themed-grid-col border border-primary"><?php echo $data['status'] ?></div>
<?php
    }
?>
    <div class="col-3 text-break themed-grid-col border border-primary">
<?php
		if($data["status"] == 'st'){
?>
		<button class="btn btn-secondary actionBtn" id="btnMBoff" type="button">mboff</button>
		<button class="btn btn-secondary actionBtn" id="btnCancel" type="button">删除</button>
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