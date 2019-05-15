<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyTaobao.php';
?>
<html lang="ja">
<head>
<title>taobao order list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYTAOBAO") ?>";
$(function() {
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateOrderStatus", 
					   "uid" : thisBox.find("#uid").val(),
					   "toStatus" : status
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
	};
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent();
	}
    $(document).on("click", "#btnDel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "del");
    });
    $(document).on("click", "#btnFahuo", function() {
		var thisBox = getMyBox(this);
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addTaobaoFahuo", 
					   "orderNo" : thisBox.find("#orderNo").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnLoadOrder", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addLoadOrderCommand"
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
<?php
  $my = new MyTaobao();
  $dataArr = $my->listTaobaoOrderByAll();
  //var_dump($dataArr);
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  $sort[$key] = $value['orderCreatedTime'];
  }
  array_multisort($sort, SORT_DESC, $dataArr);
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>  
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><button type="button" id="btnLoadOrder" class="btn btn-primary actionBtn">LOAD ORDER</button></li>
  </ul>
  <hr class="mb-4">
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">orderNo</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">buyerName</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">status</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
  </div>
<?php
  $counter = 0;
  foreach ($dataArr as $data) {
		$orderDtl = $my->listTaobaoOrderDetailByOrderNo($data['orderNo']);
		$baobeiTitle = $orderDtl[0]["baobeiTitle"];
	  $counter++;
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <input type="hidden" id="orderNo" value="<?php echo $data['orderNo'] ?>">
    <div class="col-3 text-break themed-grid-col border border-secondary "><?php echo $counter ?>
	  <a class="form-control btn btn-success" href="/myphp/mytaobao/page_addOrder.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data["buyerName"] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <?php echo $baobeiTitle ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <?php echo $data["status"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
<?php
	if($data["status"] == "added"){
?>
	  <button type="button" id="btnFahuo" class="btn btn-primary actionBtn">FH</button>
<?php
	}
?>
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	</div>
  </div>
<?php
    }
?>
</div>
</body>
</html>