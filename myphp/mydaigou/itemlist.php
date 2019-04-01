<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyDaiGou.php';
?>
<html lang="ja">
<head>
<title>item list</title>
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
var actionUrl = "http://133.130.114.129/myphp/mydaigou/action.php";
$(function() {
    $(document).on("click", "#btnCopyBox", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        var cloneBox = thisBox.clone();
        cloneBox.find("#uid").val("");
        cloneBox.find("#status").val("");
        //cloneBox.find("#orderItem").val("");
        //cloneBox.find("#priceJPY").val("");
        //cloneBox.find("#qtty").val("");
        //cloneBox.find("#priceCNY").val("");
        $("#container").append(cloneBox);
    });
	var updateRecord = function(thisBox, action){
        var buyer = $("#buyer").text();
		/*
		if(buyer == ""){
			alert("TODO!");
			return;
		}
		*/
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":action, 
						           "uid" : thisBox.find("#uid").val(),
						           "buyer" : buyer,
						           "status" : thisBox.find("#status").val(),
								   "orderDate" : thisBox.find("#orderDate").val(),
								   "orderItem" : thisBox.find("#orderItem").val(),
								   "priceJPY" : thisBox.find("#priceJPY").val(),
								   "qtty" : thisBox.find("#qtty").val(),
								   "priceCNY" : thisBox.find("#priceCNY").val(),
						   },
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
        });
	};
    $(document).on("click", "#btnSaveBox", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        updateRecord(thisBox, "saveItem");
    });
    $(document).on("click", "#btnDelBox", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        updateRecord(thisBox, "deleteItem");
    });
    $(document).on("click", "#btnRmBox", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        thisBox.remove();
    });
    $(document).on("click", "#btnAssign", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        updateRecord(thisBox, "assign");
    });
    $(document).on("click", "#btnGouru", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        updateRecord(thisBox, "gouru");
    });
    $(document).on("click", "#btnFahuo", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        updateRecord(thisBox, "fahuo");
    });
    $(document).on("click", "#btnCompl", function() {
        var thisBox = $(this).parent().parent().parent().parent();
        updateRecord(thisBox, "compl");
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  if(isset($buyer)){
?>
  <h3>买家:<span id="buyer"><?php echo $buyer ?></span></h3>
<?php
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/buyerlist.php">BUYER</a></li>
<?php
  if(isset($buyer)){
?>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>">ALL</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=unGou">待采购</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=gouru">购入</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=zaitu">在途</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=fahuo">发货</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=compl">发货</a></li>
<?php
  }
?>
  </ul>
  <hr class="mb-4">
<?php
  $mydaigou = new MyDaiGou();
  $buyer = $_GET['buyer'];
  $status = $_GET['status'];
  $dataArr = array();
  if(isset($buyer)){
	  if(isset($status)){
		  $dataArr = $mydaigou->listItemByBuyerAndStatus($buyer, $status);
	  }else{
		  $dataArr = $mydaigou->listItemByBuyer($buyer);
	  }
  }
  foreach ($dataArr as $data) {
	  $boxCss = "bg-light";
	  if($data['status'] == 'unGou'){
		  $boxCss = "bg-danger text-white";
	  }else if($data['status'] == 'gouru'){
		  $boxCss = "bg-warning text-white";
	  }else if($data['status'] == 'zaitu'){
		  $boxCss = "bg-success text-white";
	  }else if($data['status'] == 'fahuo'){
		  $boxCss = "bg-success text-white"
	  }else if($data['status'] == 'compl'){
		  $boxCss = "bg-secondary text-white"
	  }
	  
      include __DIR__ .'/itembox_subpage.php';
  }
  // list unasign item
   if(isset($buyer) && isset($status) && ($status == 'unGou' || $status == 'gouru')){
	   $dataArr = $mydaigou->listItemByUnAsign();
	   foreach ($dataArr as $data) {
		   include __DIR__ .'/itembox_subpage.php';
	   }
   }
?>
<?php
   // blank box
   if(!isset($buyer) || (isset($status) && !($status == 'zaitu' || $status == 'fahuo' || $status == 'compl')){
       $data = array[];
       include __DIR__ .'/itembox_subpage.php';
   }
   
?>
<!--
  <div class="box">
      <div class="row mb-4">
		<div class="input-group">
          <input type="text" class="form-control" id="orderDate" placeholder="下单日期" aria-label="" aria-describedby="button-addon4">
		  <input type="hidden" name="uid">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnSaveBox" type="button">SAVE</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
		<div class="input-group ui-front">
          <input type="text" class="form-control" id="orderItem" placeholder="宝贝商品" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnCopyBox" type="button">COPY</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
		<div class="input-group">
		  <input type="text" class="form-control" placeholder="价格(JPY)" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnDelBox" type="button">DEL</button>
			<button class="btn btn-outline-secondary" id="btnRmBox" type="button">REMOVE</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
        <div class="input-group">
          <input type="text" class="form-control" id="qtty" placeholder="数量" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnGouru" type="button">已购</button>
			<button class="btn btn-outline-secondary" id="btnZaitu" type="button">在途</button>
		  </div>
        </div>
      </div>
      <div class="row mb-4">
        <div class="input-group">
          <input type="text" class="form-control" id="priceCNY" placeholder="价格(CNY)" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnFahuo" type="button">发货</button>
		  </div>
        </div>
      </div>
      <hr class="mb-4">
  </div>
  -->
</div>
</body>
</html>