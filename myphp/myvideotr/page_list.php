<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyVideoTr.php';
?>
<html lang="ja">
<head>
<title>bilibili list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYVIDEOTR") ?>";
$(function() {
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
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent();
	}
    $(document).on("click", "#btnDel", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "del");
    });
    $(document).on("click", "#btnToUpload", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "toul");
    });
});
</script>
</head>
<body class="py-4">
<?php
  $my = new MyVideoTr();
  $status = $_GET['status'];
  if(empty($status) || $status == "toDL"){
	  $dataArr = $my->listByTodownload();
  }else if($status == "toUL"){
	  $dataArr = $my->listByToupload();
  }else if($status == "uploaded"){
	  $dataArr = $my->listByUploaded();
  }
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  if(empty($status) || $status == "toDL"){
		  $sort[$key] = $value['dtAdd'];
	  }else if($status === "toUL"){
		  $sort[$key] = $value['dtdled'];
	  }else if($status === "uploaded"){
		  $sort[$key] = $value['dtuled'];
	  }
  }
  array_multisort($sort, SORT_DESC, $dataArr);
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/mybilibili/page_list.php?status=toDL">to DL</a></li>
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/mybilibili/page_list.php?status=toUL">to UL</a></li>
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/mybilibili/page_list.php?status=uploaded">UPLOADED</a></li>
  </ul> 
  <hr class="mb-2">   
  <div class="row">
<?php
    if(empty($status) || $status == "toDL"){
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">title</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">uper</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">ytSearchRslt</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
<?php
    }else if($status == "toUL"){
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">title</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">uper</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">ytSearchRslt</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
<?php
    }else if($status == "toUL"){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">title</div>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">uper</div>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">YTURL</div>
<?php
    }
?>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
<?php
    if(empty($status) || $status == "toDL"){
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["title"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["uper"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["ytSearchRslt"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	  <button type="button" id="btnToUpload" class="btn btn-secondary actionBtn">to UL</button>
	</div>
<?php
    }else if($status == "toUL"){
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["title"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["uper"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["ytSearchRslt"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	</div>
<?php
    }else if($status == "toUL"){
?>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["title"] ?>
	</div>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["uper"] ?>
	</div>
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["ytVideoUrl"] ?>
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