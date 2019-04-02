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
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYDAIGOU") ?>";
var autocompleteUrl = "<?php echo constant("URL_AUTOCOMPLETE_MYDAIGOU") ?>";
$(function() {
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent().parent().parent();
	}
    $("#buyer").autocomplete({
	    source: function(request, response) {
		    $.ajax({
			    url: autocompleteUrl,
			    dataType: "json",
			    type: "POST",
			    cache: false,
			    data: { "sugguestType":"buyer" , "termKey": request.term },
			    success: function(data) {
				    response(data);
			    },
			    error: function(XMLHttpRequest, textStatus, errorThrown) {
				    response(['']);
			    }
		    });
	    }	
    });
	var updateRecord = function(thisBox, action){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":action, 
					   "uid" : thisBox.find("#uid").val(),
					   "buyer" : thisBox.find("#buyer").val(),
					   "orderItem" : thisBox.find("#orderItem").val(),
					   "priceJPY" : thisBox.find("#priceJPY").val(),
					   "priceCNY" : thisBox.find("#priceCNY").val(),
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            //location.reload();
        });
	};
    $(document).on("click", "#btnCopyBox", function() {
        var thisBox = getMyBox(this);
        var cloneBox = thisBox.clone();
        cloneBox.find("#uid").val("");
        cloneBox.find("#buyer").val("");
        cloneBox.find("#status").val("");
        $("#container").append(cloneBox);
    });
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
		var buyer = thisBox.find("#buyer").val();
		if(buyer == ""){
			alert("please input buyer!!");
			return;
		}
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
  include __DIR__ .'/subpage_toplink.php';
?>
<?php
  $status = '';
  if(isset($_GET['status'])){
	$status = $_GET['status'];
  }
  $buyer = '';
  if(isset($_GET['buyer'])){
	$buyer = $_GET['buyer'];
  }
  if($status == 'unasign'){
	  $cssBgUnasign = "bg-success text-white";
  }else if($status == 'unGou'){
	  $cssBgUnGou = "bg-success text-white";
  }else if($status == 'gouru'){
	  $cssBgGouru = "bg-success text-white";
  }else if($status == 'zaitu'){
	  $cssBgZaitu = "bg-success text-white";
  }else if($status == 'fahuo'){
	  $cssBgFahuo = "bg-success text-white";
  }else if($status == 'compl'){
	  $cssBgCompl = "bg-success text-white";
  }else{
	  $cssBgAll = "bg-success text-white";
  }
  if(isset($buyer) && $buyer != ''){
?>
  <h3>买家:<span id="buyer"><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php">ALL X ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnasign ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=unasign">unasign</a></li>
    <li class="list-group-item <?php echo $cssBgUnGou ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=unGou">待采购</a></li>
    <li class="list-group-item <?php echo $cssBgGouru ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=gouru">已购入</a></li>
    <li class="list-group-item <?php echo $cssBgZaitu ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=zaitu">已在途</a></li>
    <li class="list-group-item <?php echo $cssBgFahuo ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=fahuo">已发货</a></li>
    <li class="list-group-item <?php echo $cssBgCompl ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>&status=compl">已完結</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $mydaigou = new MyDaiGou();
  if((isset($status) && $status != '') && (isset($buyer) && $buyer != '')){
	  $dataArr = $mydaigou->listItemByBuyerAndStatus($buyer, $status);
  }else if (isset($buyer) && $buyer != ''){
	  $dataArr = $mydaigou->listItemByBuyer($buyer);
  }else if (isset($status) && $status != ''){
	  $dataArr = $mydaigou->listItemByStatus($status);
  }else{
	  $dataArr = $mydaigou->listAllItem();
  }
  
?>
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">orderItem</div>
<?php
    if($status == ''){
?>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">status</div>
<?php
    }
?>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">JPY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">CNY</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">buyer</div>
  </div>
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
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <input type="hidden" id="orderItem" value="<?php echo $data['orderItem'] ?>">
    <input type="hidden" id="status" value="<?php echo $data['status'] ?>">
    <input type="hidden" id="priceJPY" value="<?php echo $data['priceJPY'] ?>">
    <input type="hidden" id="priceCNY" value="<?php echo $data['priceCNY'] ?>">
    <div class="col-4 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/mydaigou/modifyitem.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data['orderItem'] ?>
	  </a>
	</div>
<?php
    if($status == ''){
?>
    <div class="col-2 text-break themed-grid-col border border-secondary"><?php echo $data["status"] ?></div>
<?php
    }
?>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceJPY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceCNY"] ?></div>
    <div class="col-3 text-break themed-grid-col border">
		<div class="mb-1 input-group ui-front">
<?php
    if($data["status"] == 'unasign'){
?>
		  <input value="<?php echo $buyer ?>" type="text" class="form-control" id="buyer" aria-describedby="button-addon4">
<?php
    }else{
?>
		  <a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $data['buyer'] ?>&status=<?php echo $data['status'] ?>"><?php echo $data['buyer'] ?></a>
<?php
    }
?>
		  <div class="input-group-append" id="button-addon4">
<?php
    if($data["status"] == 'unasign'){
?>
			<button class="btn" id="btnAssign" type="button">assignにする</button>
<?php
    }else if($data["status"] == 'unGou'){
?>
			<button class="btn" id="btnGouru" type="button">已购にする</button>
<?php
    }else if($data["status"] == 'gouru'){
?>
			<button class="btn" id="btnZaitu" type="button">在途にする</button>
<?php
    }else if($data["status"] == 'zaitu'){
?>
			<button class="btn" id="btnFahuo" type="button">发货にする</button>
<?php
    }else if($data["status"] == 'fahuo'){
?>
			<button class="btn" id="btnCompl" type="button">完結にする</button>
<?php
    }
?>
          </div>
		</div>
    </div>
  </div>
<?php
  }
?>

</div>
</body>
</html>