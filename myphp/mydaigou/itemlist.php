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
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent().parent();
	}
    $(document).on("click", "#btnCopyBox", function() {
        var thisBox = getMyBox(this);
        var cloneBox = thisBox.clone();
        cloneBox.find("#uid").val("");
        cloneBox.find("#buyer").val("");
        cloneBox.find("#status").val("");
        //cloneBox.find("#orderItem").val("");
        //cloneBox.find("#priceJPY").val("");
        //cloneBox.find("#qtty").val("");
        //cloneBox.find("#priceCNY").val("");
        $("#container").append(cloneBox);
    });
	var updateRecord = function(thisBox, action){
        
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
						           "buyer" : thisBox.find("#buyer").val(),
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
            location.reload();
        });
	};
    $(document).on("click", "#btnSaveBox", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "saveItem");
    });
    $(document).on("click", "#btnDelBox", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "deleteItem");
    });
    $(document).on("click", "#btnRmBox", function() {
		var thisBox = getMyBox(this);
        thisBox.remove();
    });
    $(document).on("click", "#btnAssign", function() {
		var thisBox = getMyBox(this);
		var buyer = $("#buyer").text();
		thisBox.find("#buyer").val(buyer)
        updateRecord(thisBox, "assign");
    });
    $(document).on("click", "#btnGouru", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "gouru");
    });
    $(document).on("click", "#btnZaitu", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "zaitu");
    });
    $(document).on("click", "#btnFahuo", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "fahuo");
    });
    $(document).on("click", "#btnCompl", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "compl");
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  if(isset($_GET['buyer'])){
	$buyer = $_GET['buyer'];
  }
  if(isset($_GET['status'])){
	$status = $_GET['status'];
  }
  if(isset($buyer)){
?>
  <h3>买家:<span id="buyer"><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/buyerlist.php">看看别的买家？</a></li>
  </ul>
  <hr class="mb-4">
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/report.php?buyer=<?php echo $buyer ?>">报价</a></li>
  </ul>
  <hr class="mb-4">
<?php
  }
?>
<?php
  if(!isset($buyer)){
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/buyerlist.php">结算 谁买了些什么？</a></li>
  </ul>
<?php
  }
?>
<?php
  if(isset($buyer)){
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>">ALL</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=unGou">待采购</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=gouru">购入</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=zaitu">在途</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=fahuo">发货</a></li>
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=compl">已完</a></li>
  </ul>
<?php
  }
?>
<?php
  $mydaigou = new MyDaiGou();
  $dataArr = array();
  if(isset($buyer)){
	  if(isset($status)){
		  $dataArr = $mydaigou->listItemByBuyerAndStatus($buyer, $status);
	  }else{
		  $dataArr = $mydaigou->listItemByBuyer($buyer);
	  }
  }
?>
	  <h2>购入列表</h2>
	  <hr class="mb-4">
<?php
  foreach ($dataArr as $data) {
	  $boxCss = "bg-light";
	  if($data['status'] == 'unGou'){
		  $boxCss = "bg-danger text-white";
	  }else if($data['status'] == 'gouru'){
		  $boxCss = "bg-warning text-white";
	  }else if($data['status'] == 'zaitu'){
		  $boxCss = "bg-success text-white";
	  }else if($data['status'] == 'fahuo'){
		  $boxCss = "bg-success text-white";
	  }else if($data['status'] == 'compl'){
		  $boxCss = "bg-secondary text-white";
	  }
	  $subPageDiv ="assigned";
      include __DIR__ .'/itembox_subpage.php';
  }
  // list unasign item
  if(isset($buyer) && isset($status) && ($status == 'unGou' || $status == 'gouru')){
?>
	  <h2>无主列表</h2>
	  <hr class="mb-4">
<?php
	  $subPageDiv ="unassign";
	  $boxCss = "";
	   $dataArr = $mydaigou->listItemByUnAsign();
	   foreach ($dataArr as $data) {
		   include __DIR__ .'/itembox_subpage.php';
	   }
  }
?>
<?php
   // blank box
   if(!isset($buyer) || (isset($status) && !($status == 'zaitu' || $status == 'fahuo' || $status == 'compl'))){
?>
	  <h2>新加宝贝</h2>
	  <hr class="mb-4">
<?php
       $data = array('status'=>'');
	   $subPageDiv ="createNew";
	   $boxCss = "";
       include __DIR__ .'/itembox_subpage.php';
   }
?>
</div>
</body>
</html>