
<?php
require 'MyGiftCard.php';
?>
<html>
<head>
<title>stock list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<!--
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script type="text/javascript">
$(function() {
jQuery("#list").jqGrid({
   	url:'/imart/myphp/mygiftcard.php?action=liststock',
	datatype: "json",
   	colNames:['orderNo','codeType', 'codeCd', 'isUsed','aucId','obider'],
   	colModel:[
   		{name:'orderNo',index:'orderNo', width:55, editable:false, editoptions:{readonly:true}, sorttype:'string'},
   		{name:'codeType',index:'codeType', width:90, editable:false, editoptions:{readonly:true}, sorttype:'string' },
   		{name:'codeCd',index:'codeCd', width:100,editable:false},
   		{name:'isUsed',index:'isUsed', width:80, editable:true,editrules:{boolean:true},sorttype:'string'},
   		{name:'aucId',index:'aucId', width:80, editable:false,sorttype:'string'},		
   		{name:'obider',index:'obider', width:80, editable:false,sorttype:'string'}
   	],
   	rowNum:10,
   	rowTotal: 50,
   	rowList:[10,20,30],
   	sortname: 'orderNo',
   	loadonce: true,
    viewrecords: true,
    sortorder: "desc",
    editurl: 'server.php', // this is dummy existing url
    caption:"CRUD on Local Data"
});
});
</script>
</head>
<body class="py-4">
<?php
  $myGiftCard = new MyGiftCard();
  $dataArr = $myGiftCard->listStock();
?>
<div id="container" class="container">
  <div class="row">
	<div class="col-2 themed-grid-col border border-primary bg-info text-white">OrderNo</div>
	<div class="col-2 themed-grid-col border border-primary bg-info text-white">CodeType</div>
	<div class="col-3 themed-grid-col border border-primary bg-info text-white">CodeCd</div>
	<div class="col-2 themed-grid-col border border-primary bg-info text-white">Status</div>
	<div class="col-1 themed-grid-col border border-primary bg-info text-white">AucId</div>
	<div class="col-1 themed-grid-col border border-primary bg-info text-white">Obider</div>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
	<div class="col-2 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["orderNo"] ?></div>
	<div class="col-2 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["codeType"] ?></div>
	<div class="col-3 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["codeCd"] ?></div>
	<div class="col-2 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["status"] ?></div>
	<div class="col-1 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["aucId"] ?></div>
	<div class="col-1 themed-grid-col border border-secondary bg-dark text-white"><?php echo $data["obider"] ?></div>
  </div>
<?php
  }
?>
</div>
</body>
</html>