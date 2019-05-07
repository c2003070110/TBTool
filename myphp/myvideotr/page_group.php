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
			   data : {"action":"updateStatus", 
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
    $(document).on("click", "#btnToMerge", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "tomg");
    });
    $(document).on("click", "#btnToDownload", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "todl");
    });
});
</script>
</head>
<body class="py-4">
<?php
  $my = new MyVideoTr();
  $status = $_GET['status'];
  if(empty($status)){
	  $dataArr = $my->listFromGroupByAll();
  //
  }else if ($status == "toDL"){
	  $dataArr = $my->listGroupByTodownload();
  }else if($status == "toMG"){
	  $dataArr = $my->listGroupByToupload();
  };
  //var_dump($dataArr);
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/myvideotr/page_list.php?status=toDL">to DL</a></li>
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/myvideotr/page_list.php?status=toMG">to MG</a></li>
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/myvideotr/page_list.php?status=uploaded">UPLOADED</a></li>
    <li class="list-group-item <?php echo $cssBgUnused ?>"><a href="/myphp/myvideotr/page_list.php">ALL</a></li>
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
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">groupId</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">title</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">ytSearchRslt</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
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
	  <?php echo $data["groupId"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["title"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["ytSearchRslt"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	  <button type="button" id="btnToDownload" class="btn btn-secondary actionBtn">to DL</button>
	</div>
<?php
    }else if($status == "toMG"){
?>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["groupId"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <a class="form-control btn btn-success" href="/myphp/myvideotr/page_add.php?uid=<?php echo $data['uid'] ?>">
	    <?php echo $data["title"] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <?php echo $data["ytSearchRslt"] ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
	  <button type="button" id="btnToMerge" class="btn btn-secondary actionBtn">to MG</button>
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