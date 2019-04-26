<?php
/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/
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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYMONTB") ?>";
$(function() {
	$( "#accordion" ).accordion({
      collapsible: true,
	  heightStyle: "content",
	  icons: {
			header: "ui-icon-circle-arrow-e",
			activeHeader: "ui-icon-circle-arrow-s"
		}
    });
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent();
	}
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateMBOrderStatus", 
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
    $(document).on("click", "#btnReOrding", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "ordering");
    });
    $(document).on("click", "#btnCancel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "cancel");
    });
    $(document).on("click", "#btnReUnOrder", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "unorder");
    });
    $(document).on("click", "#btnfin", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "fin");
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
  $my = new MyMontb();
  $tbdataArr = $my->listAllTBOrder();
  $prodArr = array();
  foreach ($tbdataArr as $tbdata) {
	  if($tbdata['transferWay'] === "mbzhiYou" || $tbdata['transferWay'] === "wozhiYou") {continue;}
	  $prodData = $my->listProductInfoByByTBUid($tbdata['uid']);
	  foreach ($prodData as $prod) {
		  if($prod["status"] ==="mbordered" || $prod["status"] ==="mbfh"){
			  $prod["maijia"] = $tbdata["maijia"];
			  $prod["maijiadianzhiHanzi"] = $tbdata["maijiadianzhiHanzi"];
			  $prod["dingdanDt"] = $tbdata["dingdanDt"];
			  $prodArr[] = $prod;
		  }
	  }
  }
  $sort = array();
  foreach ((array) $prodArr as $key => $value) {
	$sort[$key] = $value['maijia'];
  }
  array_multisort($sort, SORT_DESC, $prodArr);
  //var_dump($prodArr);
?>
  <h3>guonei fahuo</h3>
  <hr class="mb-4">
<?php
  $tbUid = "";
  $lineArr = array();
  $line = "";
  foreach ($prodArr as $data) {
	  if(empty($tbUid)){
		  $line = $data["maijiadianzhiHanzi"] . "\r\n"; 
	  }else if($tbUid !== $data["tbUid"]){
		  $lineArr[] = $line;
		  $line = $data["maijiadianzhiHanzi"] . "\r\n";  
	  }
	  $line .= $data["productId"] . " " . $data["colorName"] . " " . $data["sizeName"] . "\r\n"; 
	  $tbUid = $data["tbUid"];
  }
  $lineArr[] = $line;
?>  
<?php
  foreach ($lineArr as $line) {
?>  
    <div class="row form-group">
      <div class="col-12">
		<textarea class="form-control" cols="40" rows="5" ><?php echo $line ?></textarea >
	  </div> 
	</div> 
    <hr class="mb-4">
<?php
  }
?>  
</div>
</body>
</html>