<?php
/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/
require __DIR__ .'/MyVideoTr.php';
?>
<html lang="ja">
<head>
<title>video list</title>
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
        updateStatus(thisBox, "ignore");
    });
    $(document).on("click", "#btnToUpload", function() {
		var thisBox = getMyBox(this);
        updateStatus(thisBox, "toul");
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
	  $dataArr = $my->listByAll();
  }else{
	  $dataArr = $my->listByStatus($status);
  }
  /*
  foreach ($dataArr as $data) {
	  if(strpos($data["url"], "twitter") === false)continue;
	  $url = $data["url"] . "/1";
	  $videoUrl   = $data["videoUrl"] . "/1";
	  var_dump($data["url"]);
	  $my->updateByTemo($data["uid"], $url ,$videoUrl);
  }
  */
  //var_dump($dataArr);
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  if(empty($status) || $status == "parsed" || $status == "todl" || $status == "dlfailure"){
		  $sort[$key] = $value['dtAdd'];
	  }else if($status === "toul" || $status == "ulfailure" || $status == "dled"){
		  $sort[$key] = $value['dtdled'];
	  }else if($status === "uled"){
		  $sort[$key] = $value['dtuled'];
	  }
  }
  //var_dump($sort);
  array_multisort($sort, SORT_DESC, $dataArr);
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>  
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">title</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">uper</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">ytSRslt</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
  </div>
<?php
  $counter = 0;
  foreach ($dataArr as $data) {
	  $counter++;
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="col-3 text-break themed-grid-col border border-primary">
<?php
		if($data["status"] == "uled") {
?>
       <?php  if(!empty($data["title"])) {echo $data["title"];}else{echo $data["url"];} ?>
<?php
		}else {
?>
	  <a class="form-control btn btn-success" href="/myphp/myvideotr/page_add.php?uid=<?php echo $data['uid'] ?>">
		<?php  if(!empty($data["title"])) {echo $data["title"];}else{echo $data["url"];} ?>
	  </a>
<?php
		}
?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary">
	  <?php echo $counter ?><?php echo $data["uper"] ?>
<?php
		if($data["status"] == "parsed" || $data["status"] == "dtdled") {
			echo substr($data["dtAdd"], 0, 8);
		}
?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary">
	  <?php  if(!empty($data["ytSearchRslt"])) {echo $data["ytSearchRslt"];}else{echo $data["status"];} ?>
	</div>
    <div class="col-3 text-break themed-grid-col border border-primary">
<?php
		if($data["status"] !== "ignore") {
?>
	  <button type="button" id="btnDel" class="btn btn-secondary actionBtn">DEL</button>
<?php
		}
		if($data["status"] == "parsed") {
?>
	  <button type="button" id="btnToDownload" class="btn btn-secondary actionBtn">toDL</button>
<?php
		}else if($data["status"] == "todl") {
?>
<?php
		}else if($data["status"] == "dled") {
?>
	  <button type="button" id="btnToUpload" class="btn btn-secondary actionBtn">toUL</button>
<?php
		}else if($data["status"] == "toul") {
?>
	  <button type="button" id="btnToDownload" class="btn btn-secondary actionBtn">STOP</button>
<?php
		}else if($data["status"] == "uled") {
?>
<?php
		}else if($data["status"] == "parsefailure") {
?>
<?php
		}else if($data["status"] == "dlfailure") {
?>
	  <button type="button" id="btnToDownload" class="btn btn-secondary actionBtn">toDL</button>
<?php
		}else if($data["status"] == "ulfailure") {
?>
	  <button type="button" id="btnToUpload" class="btn btn-secondary actionBtn">toUL</button>
<?php
		}
?>
	</div>
  </div>
<?php
  }
?>
</div>
</body>
</html>