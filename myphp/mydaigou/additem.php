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
		return $(thisElement).parent().parent().parent();
	}
	var updateRecord = function(thisBox, action){
		var qtty = thisBox.find("#qtty").val();
		if(qtty == ""){
			qtty = "1";
		}
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":action, 
					   "uid" : thisBox.find("#uid").val(),
					   "buyer" : $("#buyer").val(),
					   "status" : thisBox.find("#status").val(),
					   "orderDate" : thisBox.find("#orderDate").val(),
					   "orderItem" : thisBox.find("#orderItem").val(),
					   "priceJPY" : thisBox.find("#priceJPY").val(),
					   "qtty" : qtty,
					   "priceCNY" : thisBox.find("#priceCNY").val(),
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
	};
    $("#orderItem").autocomplete({
	    source: function(request, response) {
		    $.ajax({
			    url: autocompleteUrl,
			    dataType: "json",
			    type: "POST",
			    cache: false,
			    data: { "sugguestType":"orderItem" , "termKey": request.term },
			    success: function(data) {
				    response(data);
			    },
			    error: function(XMLHttpRequest, textStatus, errorThrown) {
				    response(['']);
			    }
		    });
	    },
        select: function( event, ui ) {
		    var arr = ui.item.label.split(":");
            var thisBox = getMyBox(this);
            thisBox.find("#priceJPY").val(arr[1]);
            thisBox.find("#priceCNY").val(arr[2]);
		}		
    });
    $(document).on("click", "#btnSaveBox", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "saveItem");
    });
    $(document).on("click", "#btnDelBox", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "deleteItem");
    });
    $(document).on("click", "#btnAssign", function() {
		var thisBox = getMyBox(this);
        updateRecord(thisBox, "assign");
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
  $mydaigou = new MyDaiGou();
  $buyer = '';
  if(isset($_GET['buyer'])){
	$buyer = $_GET['buyer'];
  }
?>
  <ul class="list-group list-group-horizontal">
<?php
  if($buyer != ''){
?>
   <input type="hidden" id="buyer" value="<?php echo $buyer ?>">
   <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $buyer ?>">TA的剁手列表</a></li>
<?php
  }else{
?>
<?php 
  }
?>
  </ul>
  <hr class="mb-4">
<?php
  if($buyer != ''){
  // list unasign item
	  $subPageDiv ="unassign";
	  $boxCss = "";
	  $dataArr = $mydaigou->listItemByUnAsign();
	  if(count($dataArr) >0){
?>
	  <h2>无主列表</h2>
	  <hr class="mb-4">
<?php
	   foreach ($dataArr as $data) {
		   include __DIR__ .'/subpage_itembox.php';
	   }
	 }
  }
?>
<?php
   // blank box
?>
  <h2>新加宝贝</h2>
  <hr class="mb-4">
<?php
   $data = array('status'=>'');
   $subPageDiv ="createNew";
   $boxCss = "";
   include __DIR__ .'/subpage_itembox.php';

?>
</div>
</body>
</html>