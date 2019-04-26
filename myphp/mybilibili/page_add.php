<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
  require __DIR__ .'/MyBilibili.php';
?>
<html lang="ja">
<head>
<title>add bilibili</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYBILIBILI") ?>";
$(function() {
    $(document).on("click", "#btnAdd", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addMyBid", 
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
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  
<?php
  $uid = empty($_GET['uid'])? "" : $_GET['uid'];
  if(!empty($uid)){
	$my = new MyBilibili();
	$obj = $my->listByUid($uid);
  }
?>
  <div class="box itembox">
    <input type="hidden" id="uid" value="<?php echo $uid ?>">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="url">链接 URL</label>
	    <input type="text" class="form-control" id="url" value="<?php echo $obj['url'] ?>">
	  </div>
    </div>

<?php 
  if(!empty($uid)){
?>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="title">title</label>
	    <input type="text" class="form-control" id="title" value="<?php echo $obj['title'] ?>" readonly>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="title">uper</label>
	    <input type="text" class="form-control" id="uper" value="<?php echo $obj['uper'] ?>" readonly>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="ytSearchRslt">ytSearchRslt</label>
	    <pre><?php echo $obj['ytSearchRslt'] ?></pre>
	  </div>
    </div>
<?php 
  }
?>
    <div class="row mb-4 form-group">
      <div class="col-4"></div>
      <div class="col-8 ">
        <button class="btn btn-secondary actionBtn" id="btnAdd" type="button">a d d ！！</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>