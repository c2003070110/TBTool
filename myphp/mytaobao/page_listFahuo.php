<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyTaobao.php';
?>
<html lang="ja">
<head>
<title>fahuo list</title>
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
			   data : {"action":"updateFahuoStatus", 
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
        updateStatus(thisBox, "added");
    });
});
</script>
</head>
<body class="py-4">
<?php
  $my = new MyTaobao();
  $dataArr = $my->listFahuoByAll();
  //var_dump($dataArr);
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  $sort[$key] = $value['dtAdd'];
  }
  array_multisort($sort, SORT_DESC, $dataArr);
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">orderNo</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">trackTraceNo</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">status</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="col-3 text-break themed-grid-col border border-primary">
	  <a class="form-control btn btn-success" href="/myphp/mytaobao/page_addFahuo.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data["orderNo"] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary">
	  <?php echo $data["trackTraceNo"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary">
	  <?php echo $data["status"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary">
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	  <button type="button" id="btnFahuo" class="btn btn-secondary actionBtn">RE-FH</button>
	</div>
  </div>
<?php
  }
?>
</div>
</body>
</html>