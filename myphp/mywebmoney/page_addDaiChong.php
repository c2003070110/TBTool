<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

  require __DIR__ .'/MyWebMoney.php';
?>
<html lang="ja">
<head>
<title>add url</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYWEBMONEY") ?>";
$(function() {
    $(document).on("click", "#btnAdd", function() {
		
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addDaiChong", 
					   "uid" : $("#uid").val(),
					   "url" : $("#url").val(), 
					   "amtJPY" : $("#amtJPY").val(), 
					   "tbBuyer" : $("#tbBuyer").val(), 
					   "payway" : $("#payway").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
			if(msg.indexOf("ERROR") != -1){
				alert(msg);
				return;
			}
			var href = window.location.href;
			if(msg.indexOf("uid") != -1){
				window.location.reload();
			}else{
				var url = href +"?uid="+msg;
				window.location.href = url;
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
  $my = new MyWebMoney();
  $uid = empty($_GET['uid'])? "" : $_GET['uid'];
  if(!empty($uid)){
	$obj = $my->listDaiChongByUid($uid);
  }
  //var_dump($obj);
?>
    <input type="hidden" id="uid" value="<?php echo $uid ?>">
    <div class="row mb-2 form-group">
      <div class="col-10">
        <label for="url">代充的链接 URL</label>
        <input type="text" class="form-control urlInput" id="url" value="<?php echo $obj['url'] ?>">
      </div>
    </div>
    <div class="row mb-2 form-group data-row">
      <div class="col-10">
        <label for="amtJPY">代充日元</label>
        <input type="text" class="form-control" id="amtJPY" value="<?php echo $obj['amtJPY'] ?>">
      </div>
    </div>
    <div class="row mb-2 form-group data-row">
      <div class="col-10">
        <label for="tbBuyer">TB买家ID</label>
        <input type="text" class="form-control" id="tbBuyer" value="<?php echo $obj['tbBuyer'] ?>">
      </div>
    </div>
    <div class="row mb-2 form-group data-row">
      <div class="col-10">
        <label for="payway">PayWay</label>
        <select class="custom-select d-block form-control" id="payway">
          <option value="wallet" <?php if($obj['payway']=='wallet'){?> selected <?php } ?>>wallet</option>
          <option value="prepaidNo" <?php if($obj['payway']=='prepaidNo'){?> selected <?php } ?>>prepaidNo</option>
          <option value="cardcase" <?php if($obj['payway']=='cardcase'){?> selected <?php } ?>>cardcase</option>
        </select>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-4"></div>
      <div class="col-8 ">
        <button class="btn btn-secondary" id="btnAdd" type="button">a d d ！！</button>
      </div>
    </div>
</div>
</body>
</html>