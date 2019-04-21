<?php
/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/
require __DIR__ .'/MyWebMoney.php';
?>
<html lang="ja">
<head>
<title>bid list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYWEBMONEY") ?>";
$(function() {
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent().parent();
	}
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateDaichongStatus", 
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
    $(document).on("click", "#btnTopay", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "topay");
    });
    $(document).on("click", "#btnDel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "del");
    });
    $(document).on("click", "#btnPaid", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "paid");
    });
    $(document).on("click", "#btnFinish", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "finish");
    });
    $(document).on("click", "#btnRepay", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "topay");
    });
});
</script>
</head>
<body class="py-4">
<?php
  $my = new MyWebMoney();
  $status = $_GET['status'];
  if(!empty($status)){
      $dataArr = $my->listDaiChongByStatus($status);
  }else{
  //var_dump($status);
      $dataArr = $my->listDaiChongByAll();
  }
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  if(empty($status)){
		  $sort[$key] = $value['dtAdd'];
	  }else if($status === "checked"){
		  $sort[$key] = $value['dtAdd'];
	  }else if($status === "topay"){
		  $sort[$key] = $value['dtCheck'];
	  }else if($status === "paid"){
		  $sort[$key] = $value['dtPay'];
	  }else if($status === "fin"){
		  $sort[$key] = $value['dtFinish'];
	  }
  }
  array_multisort($sort, SORT_DESC, $dataArr);
  
  if($status == 'checked'){
	  $cssBgchecked= "bg-warning text-white";
  }else if($status == 'topay'){
	  $cssBgtopay = "bg-warning text-white";
  }else if($status == 'paid'){
	  $cssBgpaid= "bg-warning text-white";
  }else if($status == 'fin'){
	  $cssBgfin= "bg-warning text-white";
  }else{
	  $cssBgAll = "bg-warning text-white";
  }
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgchecked ?>"><a href="/myphp/mywebmoney/page_listDaiChong.php?status=checked">checked</a></li>
    <li class="list-group-item <?php echo $cssBgtopay ?>"><a href="/myphp/mywebmoney/page_listDaiChong.php?status=topay">topay</a></li>
    <li class="list-group-item <?php echo $cssBgpaid ?>"><a href="/myphp/mywebmoney/page_listDaiChong.php?status=paid">paid</a></li>
    <li class="list-group-item <?php echo $cssBgfin ?>"><a href="/myphp/mywebmoney/page_listDaiChong.php?status=fin">fin</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mywebmoney/page_listDaiChong.php">ALL</a></li>
  </ul> 
  <hr class="mb-2">   
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">JPY</div>
    <div class="col-6 text-break themed-grid-col border border-primary bg-info text-white">INFO</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">Action</div>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data["uid"] ?>">
    <div class="col-3 themed-grid-col border border-secondary">
	  <a href="/myphp/mywebmoney/page_addDaiChong.php?uid=<?php echo $data["uid"] ?>">
	    <?php echo $data["atmJPY"] ?>
	  </a>
	</div>
<?php 
  if($data["status"] == 'checked' || $data["status"] == 'topay') {
?>
    <div class="col-6 text-break themed-grid-col border border-secondary"><?php echo $data["realItemName"] ?></div>
<?php 
  }else if($data["status"] == 'paid') {
?>
    <div class="col-6 text-break themed-grid-col border border-secondary"><?php echo $data["payResult"] ?></div>
<?php 
  }else {
?>
    <div class="col-6 text-break themed-grid-col border border-secondary"><?php echo $data["payResult"] ?></div>
<?php 
  }
?>
    <div class="col-3 text-break themed-grid-col border border-secondary">
<?php 
  if($data["status"] == 'checked') {
?>
      <button type="button" id="btnTopay" class="btn btn-secondary actionBtn">topay</button>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">del</button>
<?php 
  }else if($data["status"] == 'checkwait') {
?>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">del</button>
<?php 
  }else if($data["status"] == 'topay') {
?>
      <button type="button" id="btnPaid" class="btn btn-secondary actionBtn">paid</button>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">del</button>
<?php 
  }else if($data["status"] == 'paid') {
?>
      <button type="button" id="btnRepay" class="btn btn-secondary actionBtn">repay</button>
      <button type="button" id="btnFinish" class="btn btn-secondary actionBtn">fin</button>
<?php 
  }else {
?>
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