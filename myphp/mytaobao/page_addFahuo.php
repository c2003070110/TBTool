<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
  require __DIR__ .'/MyTaobao.php';
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
var actionUrl = "<?php echo constant("URL_ACTION_MYTAOBAO") ?>";
$(function() {
    $(document).on("click", "#btnAdd", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addTaobaoFahuo", 
					   "uid" : $("#uid").val(), 
					   "orderNo" : $("#orderNo").val(), 
					   "trackTraceNo" : $("#trackTraceNo").val(), 
					   "tranferProviderName" : $("#tranferProviderName").val()
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
  $uid = $_GET['uid'];
  if(!empty($uid)){
	$my = new MyTaobao();
	$obj = $my->listFahuoByUid($uid);
  }
?>
  <div class="box itembox">
    <input type="hidden" id="uid" value="<?php echo $uid ?>">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="orderNo">orderNo</label>
	    <input type="text" class="form-control" id="orderNo" value="<?php echo $obj['orderNo'] ?>" <?php if(!empty($orderNo)) echo "readOnly" ?> >
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="trackTraceNo">trackTraceNo</label>
	    <input type="text" class="form-control" id="trackTraceNo" value="<?php echo $obj['trackTraceNo'] ?>" >
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
	    <label for="tranferProviderName">tranferProviderName</label>
		<select class="custom-select d-block" id="tranferProviderName">
		  <option value="" selected></option>
		  <option value="JPEMS" <?php if($obj['tranferProviderName']=='JPEMS'){?> selected <?php } ?>>JPEMS</option>
		  <option value="ZHONGTONG" <?php if($obj['tranferProviderName']=='ZHONGTONG'){?> selected <?php } ?>>ZHONGTONG</option>
		</select>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-4"></div>
      <div class="col-8 ">
        <button class="btn btn-secondary actionBtn" id="btnAdd" type="button">S A V E ！！</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>