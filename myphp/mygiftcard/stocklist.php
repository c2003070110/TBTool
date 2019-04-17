<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyGiftCard.php';
?>
<html lang="ja">
<head>
<title>stock list</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYGIFTCARD") ?>";
$(function() {
    $(document).on("click", ".actionBtn", function() {
        var thisBox = $(this).parent().parent();
        var actionName = $(this).html();

        var orderNoVal = thisBox.find("#orderNo").text();
        var codeTypeVal = thisBox.find("#codeType").text();
        var codeCdVal = thisBox.find("#codeCd").text();
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action" : actionName, 
                                   "orderNo" : orderNoVal, 
                                   "codeType" : codeTypeVal, 
                                   "codeCd" : codeCdVal},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			//alert(msg);
            location.reload();
        });
    });
});
</script>
</head>
<body class="py-4">
<?php
  $myGiftCard = new MyGiftCard();
  $status = $_GET['status'];
  $codeType = empty($_GET['codeType']) ? "" : $_GET['codeType'];
  if(!empty($status) && !empty($codeType)){
      $dataArr = $myGiftCard->listStockByStatusAndCodeType($status, $codeType);
  }else if(!empty($status)){
      $dataArr = $myGiftCard->listStockByStatus($status);
  }else if(!empty($codeType)){
      $dataArr = $myGiftCard->listStockByCodeType($codeType);
  }else{
      $dataArr = $myGiftCard->listStock();
  }
  if($status == 'unused'){
	  $cssBgUnused = "bg-warning text-white";
  }else if($status == 'using'){
	  $cssBgUsing = "bg-warning text-white";
  }else if($status == 'used'){
	  $cssBgUsed = "bg-warning text-white";
  }else if($status == 'invalid'){
	  $cssBgInvalid = "bg-warning text-white";
  }else{
	  $cssBgAll = "bg-warning text-white";
  }
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mygiftcard/regcode.php">REG</a></li>
  </ul>   
  <hr class="mb-2"> 
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mygiftcard/stocklist.php?codeType=<?php echo $_GET['uid'] ?>">ALL</a></li>
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/mygiftcard/stocklist.php?status=unused&codeType=<?php echo $_GET['uid'] ?>">unused</a></li>
    <li class="list-group-item <?php echo $cssBgUsing ?>"><a href="/myphp/mygiftcard/stocklist.php?status=using&codeType=<?php echo $_GET['uid'] ?>">using</a></li>
    <li class="list-group-item <?php echo $cssBgUsed ?>"><a href="/myphp/mygiftcard/stocklist.php?status=used&codeType=<?php echo $_GET['uid'] ?>">used</a></li>
  </ul> 
  <hr class="mb-2">   
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">CodeCd</div>
<?php
    if($status == ''){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">Status</div>
<?php
    }
?>
<?php
    if($status == 'unused' || $status == 'using' || $status == 'used'){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">Action</div>
<?php
    }
?>
<?php
    if($status == 'used'){
?>
    <div class="col-4 themed-grid-col border border-primary bg-info text-white">AucId</div>
<?php
    }else{
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">CodeType</div>
<?php
    }
?>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data["uid"] ?>">
    <div id="codeCd" class="col-4 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mygiftcard/regcode.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data["codeCd"] ?>
	  </a>
	</div>
<?php
    if($status == ''){
?>
    <div id="status" class="col-4 text-break themed-grid-col border border-secondary"><?php echo $data["status"] ?></div>
<?php
    }
?>
<?php
    if($status == 'unused' || $status == 'using' || $status == 'used'){
?>
    <div class="col-4 text-break themed-grid-col border border-secondary">
<?php 
  if($data["status"] == 'unused') {
?>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
      <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
      <button type="button" id="btnInlid" class="btn btn-secondary actionBtn">INV</button>
<?php 
  }else if($data["status"] == 'using') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
      <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
<?php 
  }else if($data["status"] == 'used') {
?>
      <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
<?php 
  }else if($data["status"] == 'invalid') {
?>
      <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
<?php 
  }
?>
    </div>
<?php
    }
?>
<?php
    if($status == 'used'){
?>
    <div class="col-4 themed-grid-col border border-secondary">
	  <a href="https://page.auctions.yahoo.co.jp/jp/auction/<?php echo $data['aucId'] ?>" target="blank">
	    <?php echo $data["aucId"] ?>
	  </a>
    </div>
<?php
    }else{
?>
    <div id="codeType" class="col-4 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mygiftcard/stocklist.php?status=<?php echo $status ?>&codeType=<?php echo $data["codeType"] ?>">
	    <?php echo $data["codeType"] ?>
	  </a>
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