<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyYaBid.php';

  $buyer = $_GET["buyer"];
  $admin = $_GET["admin"];
  $status = $_GET["status"];
?>
<html lang="ja">
<head>
<title>my bid</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYYABID") ?>";
$(function() {
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent().parent();
	}
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateItemStatus", 
					   "uid" : thisBox.find("#uid").val(),
					   "buyer" : thisBox.find("#buyer").val(),
					   "status" : status
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
	};
    $(document).on("click", "#btnCancel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "cancel");
    });
    $(document).on("click", "#btnRubao", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "rubao");
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  $my = new MyYaBid();
  $isAdmin = $my->isAdmin($admin);
  
  include __DIR__ .'/subpage_toplink.php';
  
  if($admin != '' && !$isAdmin){
	  exit(0);
  }
  if($buyer != ''){
?>
  <h3>买家:<span><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
<?php
  }
  if (!empty($buyer)){
	  $dataArr = $my->listItemByBuyer($buyer);
  }else if($isAdmin){
	  $dataArr = $my->listItemByAll($status);
  } else{
	  exit(0);
  }
?>
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">宝贝名</div>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">状态</div>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">日元</div>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">daoneiyunfei</div>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">zhongliang(g)</div>
  </div>
<?php
  foreach ($dataArr as $data) {
	  //$boxCss = "bg-warning text-white";
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="buyer" value="<?php echo $data['buyer'] ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
<?php
    if($buyer == $data['buyer'] && ($data["status"] == 'bddao')){
?>
    <div class="col-2 text-break themed-grid-col border border-secondary">
<?php
    }else{
?>
    <div class="col-4 text-break themed-grid-col border border-secondary">
<?php
    }
?>
	  <a href="<?php echo $data['itemUrl'] ?>" target="blank">
	    <?php echo $data['itemName'] != '' ? $data['itemName'] : $data['itemUrl'] ?>
	  </a>
	</div>
<?php
    if($buyer == $data['buyer'] && ($data["status"] == 'bddao')){
?>
    <div class="col-2 border border-secondary">
	  <div class="mt-1 mb-1">
		<button class="btn btn-secondary actionBtn" id="btnRubao" type="button">rubao</button>
	  </div>
	</div>
<?php
    }
?>
    <div class="col-2 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/myyabid/page_myItem.php?buyer=<?php echo $data['buyer'] ?>&uid=<?php echo $data['uid'] ?>&admin=<?php echo $admin ?>">
	    <?php echo $my->getStatusName($data["status"]) ?>
	  </a>
	</div>
<?php
    if($buyer == $data['buyer'] && ($data["status"] == 'paiBf')){
?>
    <div class="col-2 border border-secondary">
	  <div class="mt-1 mb-1">
		<button class="btn btn-secondary actionBtn" id="btnCancel" type="button">cancel</button>
	  </div>
	</div>
    <div class="col-2 border border-secondary">
	  <div class="mt-1 mb-1">
		<button class="btn btn-secondary actionBtn" id="btnCancel" type="button">cancel</button>
	  </div>
	</div>
    <div class="col-2 border border-secondary">
	  <div class="mt-1 mb-1">
		<button class="btn btn-secondary actionBtn" id="btnCancel" type="button">cancel</button>
	  </div>
	</div>
<?php
    }else{
?>
    <div class="col-2 text-break themed-grid-col border border-secondary">
	  <?php echo $data["priceJPY"] ?>
	</div>
    <div class="col-2 text-break themed-grid-col border border-secondary">
	  <?php echo $data["transfeeDaoneiJPY"] ?>
	</div>
    <div class="col-2 text-break themed-grid-col border border-secondary">
	  <?php echo $data["weight"] ?>
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