<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
  require __DIR__ .'/MyYaBid.php';
  $buyer = $_GET["buyer"];
  $admin = $_GET["admin"];
  $uid = $_GET["uid"];
?>
<html lang="ja">
<head>
<title>My item</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYYABID") ?>";
$(function() {
    $(document).on("click", "#btnUpdateItem", function() {
		
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateItemEstimatePrice", 
					   "uid" : $("#uid").val(),
					   "buyer" : $("#buyer").val(),
					   "estimateJPY" : $("#estimateJPY").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("change", ".priceInput", function() {
		var thisBox = $(this).parent().parent();
		var jpy = parseInt($("#estimateJPY").val());
		var huilv = parseFloat($("#myhuilv").val());
		var mydaigoufei = parseInt($("#mydaigoufei").val());
		var cny = Math.ceil(jpy * huilv) + mydaigoufei;
		$("#estimateCNY").val(cny);
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
  if(empty($buyer) && empty($uid)){
	  exit(0);
  }
  $my = new MyYaBid();
  $data = $my->listItemByBuyerAndItemUid($buyer, $uid);
  if(empty($data)){
	  exit(0);
  }
  $huilv = $my->getHuilv();
  $mydaigoufei = $my->getDaigoufei();
  $estimateCNY = ceil(intval($data["estimateJPY"]) * $huilv) + $mydaigoufei;
  $editFlag = ($data["status"] == "paiBf") || ($data["status"] == "paiing");
  //var_dump($editFlag);
?>
  <div class="box itembox">
    <input type="hidden" id="buyer" value="<?php echo $buyer ?>">
    <input type="hidden" id="uid" value="<?php echo $uid ?>">
    <input type="hidden" id="myhuilv" value="<?php echo $huilv ?>">
    <input type="hidden" id="mydaigoufei" value="<?php echo $mydaigoufei ?>">
    <div class="row mb-4 form-group">
      <div class="col-12 text-break themed-grid-col">
	    <a href="<?php echo $data['itemUrl'] ?>" target="blank" id="itemName">
	      <?php echo $data['itemName'] != '' ? $data['itemName'] : $data['itemUrl'] ?>
	    </a>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-12 themed-grid-col">
        <label for="priceJPY">心里价 日元</label>
        <input type="text" class="form-control priceInput" id="estimateJPY" value="<?php echo $data['estimateJPY'] ?>" <?php if(!$editFlag){?> readonly <?php } ?>>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-12 themed-grid-col">
        <label for="priceJPY">人民币含代购费</label>
        <input type="text" class="form-control" id="estimateCNY" value="<?php echo $estimateCNY ?>" readonly >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnUpdateItem" class="btn btn-secondary actionBtn">更  新</button>
      </div>
    </div>
<?php
    if($isAdmin){
?>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <?php echo $data['obiderAddr'] ?>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <?php echo $data['obiderMsg'] ?>
      </div>
    </div>
<?php
    }
?>
  </div>
</div>
</body>
</html>