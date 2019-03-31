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
var httpPrefix = "http://133.130.114.129/";
$(function() {
    $(document).on("click", ".actionBtn", function() {
        var thisBox = $(this).parent().parent();
        var actionName = $(this).html();

		var orderNoVal = thisBox.find("#orderNo").text();
		var codeTypeVal = thisBox.find("#codeType").text();
		var codeCdVal = thisBox.find("#codeCd").text();
        
        var jqxhr = $.ajax(httpPrefix + "myphp/mygiftcard.php",
                         { type : "GET",
                           data : {"action" : actionName, 
						           "orderNo" : orderNoVal, 
						           "codeType" : codeTypeVal, 
						           "codeCd" : codeCdVal},
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
  $myGiftCard = new MyGiftCard();
  if(isset($_GET['status'])){
	  $dataArr = $myGiftCard->listStockByStatus($_GET['status']);
  }else{
	  $dataArr = $myGiftCard->listStock();
  }
?>
<ul class="list-group list-group-horizontal">
  <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php">ALL</a></li>
  <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php?status=unused">unused</a></li>
  <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php?status=using">using</a></li>
  <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php?status=used">used</a></li>
  <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php?status=invalid">invalid</a></li>
  <li class="list-group-item"><a href="/myphp/mygiftcard.html">REG</a></li>
</ul>
      <hr class="mb-4">
<div id="container" class="container">
  <div class="row">
	<div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">OrderNo</div>
	<div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">CodeType</div>
	<div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">CodeCd</div>
	<div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">Status</div>
	<div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">Action</div>
	<!--
	<div class="col-1 themed-grid-col border border-primary bg-info text-white">AucId</div>
	<div class="col-1 themed-grid-col border border-primary bg-info text-white">Obider</div>
	-->
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
	<div id="orderNo" class="col-2 text-break themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["orderNo"] ?></div>
	<div id="codeType" class="col-2 text-break themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["codeType"] ?></div>
	<div id="codeCd" class="col-3 text-break themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["codeCd"] ?></div>
	<div id="status" class="col-2 text-break themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["status"] ?></div>
	<div class="col-3 text-break themed-grid-col border border-secondary">
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	  <button type="button" id="btnUsded" class="btn btn-secondary actionBtn">US</button>
	  <button type="button" id="btnReuse" class="btn btn-secondary actionBtn">RE</button>
	  <button type="button" id="btnInlid" class="btn btn-secondary actionBtn">INV</button>
	</div>
	<!--
	<div class="col-1 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["aucId"] ?></div>
	<div class="col-1 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["obider"] ?></div>
	-->
  </div>
<?php
  }
?>
      <hr class="mb-4">

</div>
</body>
</html>