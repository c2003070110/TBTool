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
		return $(thisElement).parent().parent();
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
    $(document).on("click", "#btnPaiing", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "paiing");
    });
    $(document).on("click", "#btnDepai", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "depai");
    });
    $(document).on("click", "#btnLiupai", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "liupai");
    });
    $(document).on("click", "#btnfuk", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "fuk");
    });
    $(document).on("click", "#btnbdfh", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "bdfh");
    });
    $(document).on("click", "#btnBdDao", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "bddao");
    });
    $(document).on("click", "#btnRubao", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "rubao");
    });
    $(document).on("change", ".form-control", function() {
		var thisBox = getMyBox(this);
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateItemPrice", 
					   "uid" : thisBox.find("#uid").val(),
					   "buyer" : thisBox.find("#buyer").val(),
					   "priceJPY" : thisBox.find("#priceJPY").val(),
					   "transfeeDaoneiJPY" : thisBox.find("#transfeeDaoneiJPY").val(),
					   "weight" : thisBox.find("#weight").val(),
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            //location.reload();
        });
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
  
  if(empty($admin)){
	  exit(0);
  }
  if($buyer != ''){
?>
  <h3>买家:<span><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
<?php
  }
  $amtInputFlag = !empty($status) && ($status == "depai" || $status == "fuk");
  $weightInputFlag = !empty($status) && ($status == "bdfh");
  
  if (!empty($buyer)){
	  $dataArr = $my->listItemByBuyer($buyer);
  }else if(!empty($buyer) && !empty($status)){
	  $dataArr = $my->listItemByBuyerAndStatus($buyer,$status);
  } else{
	  $dataArr = $my->listItemByAll($status);
  }
?>
  <div class="row">
    <div class="col-2 border border-primary bg-info text-white">宝贝名</div>
<?php
    if(!$weightInputFlag && !$amtInputFlag){
?>
    <div class="col-2 border border-primary bg-info text-white">买家</div>
<?php
    }
?>
<?php
    if(empty($status)){
?>
    <div class="col-2 border border-primary bg-info text-white">心里价</div>
    <div class="col-2 border border-primary bg-info text-white">状态</div>
<?php
    }
?>
<?php
    if($amtInputFlag){
?>
    <div class="col-3 text-break border border-primary bg-info text-white">日元</div>
    <div class="col-3 text-break border border-primary bg-info text-white">岛内运费</div>
<?php
    }
?>
<?php
    if($weightInputFlag){
?>
    <div class="col-2 text-break border border-primary bg-info text-white">重量(g)</div>
    <div class="col-6 text-break border border-primary bg-info text-white">YA卖家地址</div>
<?php
    }
?>
<?php
    if($amtInputFlag ){
?>
    <div class="col-3 border border-primary bg-info text-white">Action</div>
<?php
    }else if($weightInputFlag){
?>
    <div class="col-2 border border-primary bg-info text-white">Action</div>
<?php
    }else{
?>
    <div class="col-4 border border-primary bg-info text-white">Action</div>
<?php
    }
?>
  </div>
<?php
  foreach ($dataArr as $data) {
	  //$boxCss = "bg-warning text-white";
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="buyer" value="<?php echo $data['buyer'] ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="col-2 text-truncate border border-secondary">
	  <a href="<?php echo $data['itemUrl'] ?>" target="blank">
	    <?php echo $data['itemName'] != '' ? $data['itemName'] : $data['itemUrl'] ?>
	  </a>
	</div>
<?php
    if(!$weightInputFlag && !$amtInputFlag){
?>
    <div class="col-2 border border-secondary">
	  <a href="/myphp/myyabid/page_myBidList-admin.php?buyer=<?php echo $data['buyer'] ?>&admin=<?php echo $admin ?>&status=<?php echo $data['status'] ?>">
	    <?php echo $data['buyer'] ?>
	  </a>
	</div>
<?php
    }
?>	
<?php
    if(empty($status)){
?>
    <div class="col-2 border border-secondary">
	  <a href="/myphp/myyabid/page_myItem.php?uid=<?php echo $data['uid'] ?>&buyer=<?php echo $data['buyer'] ?>&admin=<?php echo $admin ?>">
	    <?php echo empty($data["estimateJPY"]) ? "wsd" : $data["estimateJPY"] ?>
	  </a>
	</div>
    <div class="col-2 border border-secondary">
	  <a href="/myphp/myyabid/page_myBidList-admin.php?status=<?php echo $data['status'] ?>&admin=<?php echo $admin ?>">
	    <?php echo $my->getStatusName($data["status"]) ?>
	  </a>
	</div>
<?php
    }
?>	
<?php
    if($amtInputFlag){
?>
    <div class="col-3 text-break border border-secondary">
	  <input type="text" class="form-control" id="priceJPY" value="<?php echo $data['priceJPY'] ?>">
	</div>
    <div class="col-3 text-break border border-secondary">
	  <input type="text" class="form-control" id="transfeeDaoneiJPY" value="<?php echo $data['transfeeDaoneiJPY'] ?>">
	</div>	
    <input type="hidden" id="weight" value="<?php echo $data['weight'] ?>">
<?php
    }
?>	
<?php
    if($weightInputFlag){
?>
    <div class="col-2 text-break border border-secondary">
	  <input type="text" class="form-control" id="weight" value="<?php echo $data['weight'] ?>">
	</div>
    <input type="hidden" id="priceJPY" value="<?php echo $data['priceJPY'] ?>">
    <input type="hidden" id="transfeeDaoneiJPY" value="<?php echo $data['transfeeDaoneiJPY'] ?>">
    <div class="col-6 text-break border border-secondary"><?php echo $data["obiderAddr"] ?></div>
<?php
    }
?>
<?php
    if($amtInputFlag){
?>
    <div class="col-3 text-break border">
<?php
    }else if($weightInputFlag){
?>
    <div class="col-2 text-break border">
<?php
    }else{
?>
    <div class="col-4 text-break border">
<?php
    }
?>
<?php
    if($buyer == $data['buyer'] && ($data["status"] == 'paiBf')){
?>
	  <div class="mb-1 input-group">
		<button class="btn btn-secondary actionBtn" id="btnCancel" type="button">cancel</button>
	  </div>
<?php
    }
?>
<?php
		if($data["status"] == 'paiBf'){
?>
		<button class="btn btn-secondary actionBtn" id="btnPaiing" type="button">paiing</button>
		<button class="btn btn-secondary actionBtn" id="btnDepai" type="button">depai</button>
		<button class="btn btn-secondary actionBtn" id="btnLiupai" type="button">liupai</button>
<?php
		}else if($data["status"] == 'paiing'){
?>
		<button class="btn btn-secondary actionBtn" id="btnDepai" type="button">depai</button>
		<button class="btn btn-secondary actionBtn" id="btnLiupai" type="button">liupai</button>
<?php
		}else if($data["status"] == 'depai' && $amtInputFlag){
?>
		<button class="btn btn-secondary actionBtn" id="btnfuk" type="button">fuk</button>
<?php
		}else if($data["status"] == 'fuk'){
?>
		<button class="btn btn-secondary actionBtn" id="btnbdfh" type="button">bdfh</button>
<?php
		}else if($data["status"] == 'bdfh' && $weightInputFlag){
?>
		<button class="btn btn-secondary actionBtn" id="btnBdDao" type="button">bddao</button>
<?php
		}else if($data["status"] == 'bddao'){
?>
		<button class="btn btn-secondary actionBtn" id="btnRubao" type="button">rubao</button>
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