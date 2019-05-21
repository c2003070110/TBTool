<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
  require __DIR__ .'/MyVideoTr.php';
?>
<html lang="ja">
<head>
<title>add video</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYVIDEOTR") ?>";
$(function() {
    $(document).on("click", "#btnAdd", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addByUrl", 
					   "url" : $("#url").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            alert(msg);
			if(msg.indexOf("ERROR") == -1){
				if(href.indexOf("uid") == -1){
					var url = href +"?uid="+msg;
					window.location.href = url;
				}else{
					location.reload();
				}
			}
        });
    });
    $(document).on("click", "#btnUpdate", function() {
        var status = $("#status").val()
		updateWithStatus(status);
    });
	var updateWithStatus = function(status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateStatusWithTitle", 
					   "uid" : $("#uid").val(), 
					   "title" : $("#title").val(), 
					   "uper" : $("#uper").val(),
					   "status":status
			   },
			   dataType : "html" 
			  }
		);
        jqxhr.done(function( msg ) {
            if(msg == "") return;
			if(msg.indexOf("ERROR") == -1){
				if(href.indexOf("uid") == -1){
					var url = href +"?uid="+msg;
					window.location.href = url;
				}else{
					location.reload();
				}
			}
        });
	};
    $(document).on("click", "#btnToDownload", function() {
        updateWithStatus(thisBox, "todl");
    });
    $(document).on("click", "#btnToUpload", function() {
		updateWithStatus("toul");
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
  $uid = $_GET['uid'];
  if(!empty($uid)){
	$my = new MyVideoTr();
	$obj = $my->listByUid($uid);
  }
  //var_dump($obj);
?>
  <div class="box itembox">
    <input type="hidden" id="uid" value="<?php echo $uid ?>">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="url">链接 URL</label>
		<textarea class="form-control" cols="40" rows="3" id="url" <?php if(!empty($uid)) echo "readOnly" ?>><?php echo $obj['url'] ?></textarea >
	  </div>
    </div>
<?php 
  if(!empty($uid)){
?>
    <input type="hidden" id="status" value="<?php echo $obj['status'] ?>">
<?php 
	if($obj['toType'] == "toYoutube"){
?>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="title">title(YT使用)</label>
		<textarea class="form-control" cols="40" rows="5" id="title"><?php echo $obj['title'] ?></textarea >
	  </div>
    </div>
<?php 
	}else if($obj['toType'] == "toWeibo"){
?>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="title">uper(Weibo使用)</label>
		<textarea class="form-control" cols="40" rows="2" id="uper"><?php echo $obj['uper'] ?></textarea >
	  </div>
    </div>
<?php 
	}
?>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="uid">uid</label>
	    <pre><?php echo $obj['uid'] ?></pre>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="uid">fromType||toType</label>
	    <pre><?php echo $obj['fromType'] ?>||<?php echo $obj['toType'] ?></pre>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="ytSearchRslt">ytSearchRslt</label>
	    <pre><?php echo $obj['ytSearchRslt'] ?></pre>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-8 ">
        <button class="btn btn-secondary actionBtn" id="btnUpdate" type="button">U P D A T E！！</button>
      </div>
      <div class="col-4">
<?php
    if($obj["status"] == "parsed"){
?>
	  <button type="button" id="btnToDownload" class="btn btn-secondary actionBtn">to DL</button>
<?php
    }else if($obj["status"] == "dled"){
?>
	  <button type="button" id="btnToUpload" class="btn btn-secondary actionBtn">to UL</button>
<?php
    }else if($obj["status"] == "dlfailure"){
?>
	  <button type="button" id="btnToDownload" class="btn btn-secondary actionBtn">to DL</button>
<?php
    }else if($obj["status"] == "ulfailure"){
?>
	  <button type="button" id="btnToUpload" class="btn btn-secondary actionBtn">to UL</button>
<?php
    }
?>
      </div>
    </div>
<?php 
  }else{
?>
    <div class="row mb-4 form-group">
      <div class="col-4"></div>
      <div class="col-8 ">
        <button class="btn btn-secondary actionBtn" id="btnAdd" type="button">S A V E ！！</button>
      </div>
    </div>
<?php 
  }
?>
  </div>
</div>
</body>
</html>