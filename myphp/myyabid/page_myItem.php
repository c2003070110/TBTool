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
			   data : {"action":"updateItemPrice", 
					   "uid" : $("#uid").val(),
					   "buyer" : $("#buyer").val(),
					   "priceJPY" : $("#priceJPY").val(),
					   "transfeeDaoneiJPY" : $("#transfeeDaoneiJPY").val(),
					   "weight" : $("#weight").val(),
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
  if($admin != '' && !$isAdmin){
	  exit(0);
  }
  if($buyer == '' && !$uid == ''){
	  exit(0);
  }
  $my = new MyYaBid();
  $data = $my->listItemByBuyerAndItemUid($buyer, $uid);
  if(!isset($data)){
	  exit(0);
  }
?>
  <hr class="mb-4">
  <div class="box itembox">
    <input type="hidden" id="buyer" value="<?php echo $buyer ?>">
    <input type="hidden" id="uid" value="<?php echo $uid ?>" >
    <div class="row mb-4 form-group">
      <div class="col-12 text-break themed-grid-col border border-secondary">
	    <a href="<?php echo $data['itemUrl'] ?>" target="blank">
	      <?php echo $data['itemName'] ?>
	    </a>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-12 themed-grid-col">
        <label for="priceJPY">priceJPY</label>
        <input type="text" class="form-control" id="priceJPY" value="<?php echo $data['priceJPY'] ?>">
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="transfeeDaoneiJPY">transfeeDaoneiJPY</label>
        <input type="text" class="form-control" id="transfeeDaoneiJPY" value="<?php echo $data['transfeeDaoneiJPY'] ?>" <?php if(!$isAdmin){?> readonly <?php } ?>>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="weight">weight</label>
        <input type="text" class="form-control" id="weight" value="<?php echo $data['weight'] ?>" <?php if(!$isAdmin){?> readonly <?php } ?>>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnUpdateItem" class="btn btn-secondary actionBtn">update item</button>
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