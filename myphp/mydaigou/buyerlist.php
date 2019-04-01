<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyDaiGou.php';
?>
<html lang="ja">
<head>
<title>buyer list</title>
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
<script type="text/javascript">
var httpPrefix = "http://133.130.114.129/";
$(function() {
    $(document).on("click", "#btnSaveBuyer", function() {
        var thisBox = $(this).parent().parent().parent().parent().parent();
		var uid = thisBox.find("#uid").val();
		var buyer = thisBox.find("#buyer").val();
		var address = thisBox.find("#address").val();
        
        var jqxhr = $.ajax(httpPrefix + "myphp/mydaigou/action.php",
                         { type : "GET",
                           data : {"action" : "saveBuyer", 
						           "uid" : uid, 
						           "buyer" : buyer, 
						           "address" : address},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
        });
    });
});
</script>
</head>
<body class="py-4">
<?php
  $myDaiGou = new MyDaiGou();
  $dataArr = $myDaiGou->listAllBuyer();  
?>
<div id="container" class="container">
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php">记账 每天都买了些啥？</a></li>
  </ul>
<?php
  foreach ($dataArr as $data) {
?>
	<ul class="list-group list-group-horizontal">
	  <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $data["buyer"] ?>"><?php echo $data["buyer"] ?>が买了些啥？</a></li>
	  <li class="list-group-item"><a href="/myphp/mydaigou/buyerlist.php?buyer=<?php echo $data["uid"] ?>"><?php echo $data["buyer"] ?>を修改地址？</a></li>

	</ul>
	<hr class="mb-4">
<?php
  }
  $uid = $_GET['buyer'];
  if(isset($uid)){
	  $dataMod = $myDaiGou->listBuyerByUid($uid);
  }
?>
<?php
  if(isset($uid)){
?>
	<ul class="list-group list-group-horizontal">
	  <li class="list-group-item"><a href="/myphp/mydaigou/buyerlist.php">需要新规买家？</a></li>
	</ul>
	<hr class="mb-4">
<?php
  }
?>
  <div class="box">
<?php
  if(isset($uid)){
?>
      <h3>修改买家地址</h3>
<?php
  }else{
?>
      <h3>新买家</h3>
<?php
  }
?>
      <div class="row mb-4 form-group">
		<div class="input-group">
          <input type="text" value="<?php echo $dataMod['buyer'] ?>" class="form-control" id="buyer" placeholder="买家" aria-label="" aria-describedby="button-addon4">
		  <input type="hidden" value="<?php echo $dataMod['uid'] ?>" id="uid">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnSaveBuyer" type="button">ADD</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
		<div class="input-group ui-front">
          <input type="text" value="<?php echo $dataMod['address'] ?>" class="form-control" id="address" placeholder="地址" aria-label="" aria-describedby="button-addon4">
		</div>
      </div>
  </div>
  <!--
  <hr class="mb-4">
  <div class="box">
      <h3>新买家</h3>
      <div class="row mb-4 form-group">
		<div class="input-group">
          <input type="text" class="form-control" id="buyer" placeholder="买家" aria-label="" aria-describedby="button-addon4">
		  <input type="hidden" name="uid">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnSaveBuyer" type="button">ADD</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
		<div class="input-group ui-front">
          <input type="text" class="form-control" id="address" placeholder="地址" aria-label="" aria-describedby="button-addon4">
		</div>
      </div>
  </div>
  -->
</div>
</body>
</html>