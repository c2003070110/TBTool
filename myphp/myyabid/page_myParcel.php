<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyYaBid.php';

  $buyer = '';
  $admin = '';
  $uid = '';
  if(isset($_GET("buyer"))){
	  $buyer = $_GET("buyer");
  }
  if(isset($_GET("admin"))){
	  $admin = $_GET("admin");
  }
  if(isset($_GET("uid"))){
	  $buyer = $_GET("uid");
  }
  $my = new MyYaBid();
?>
<html lang="ja">
<head>
<title>my parcel</title>
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
<script src="myphp/myjavascript.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYYABID") ?>";
$(function() {
    $(document).on("click", "#btnAddTaobaoDingdan", function() {
		
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addTaobaoDingdan", 
					   "uid" : thisBox.find("#uid").val(),
					   "taobaoDingdanhao" : thisBox.find("#taobaoDingdanhao").val(),
					   "taobaoDingdanCNY" : thisBox.find("#taobaoDingdanCNY").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("change", "#guojiShoudan", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateTransfee", 
					   "uid" : thisBox.find("#uid").val(),
					   "guojiShoudan" : guojiShoudan
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  $isAdmin = $my.isAdmin($admin);
  include __DIR__ .'/subpage_toplink.php';
  if($admin != '' && !$isAdmin){
	  exit(0);
  }
  if($buyer == ""){
	  exit(0);
  }
?>
  <h3>买家:<span id="buyer"><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
<?php
  $myparcel = array();
  $dataArr = array();
  $myparcel = $my->listParcelByBuyerAndUnParcel($buyer);
  $dataArr = $my->listItemByBuyerAndUnParcel($buyer, $myparcel["uid"]);

  //$itemTtlJPY = 0,$ttlWeight = 0;
  //$itemTtlCNY = 0;
  //foreach ($dataArr as $data) {
//	  $itemTtlJPY += intVal($data["priceJPY"]);
//	  //$ttlWeight += intVal($data["weight"]);
//	  $itemTtlCNY += intVal($data["priceCNY"]);
//  }
  
  // add to myparcel
  //$transfeeGuojiJPY = myTransfee->getGuojiYunfei($ttlWeight, $myparcel["guojiShoudan"]);
  //$transfeeGuonei = myTransfee->getGuoneiYunfei($ttlWeight, $myparcel["guojiShoudan"]);
  
  $dataTbArr = $my->listTaobaoDingdanByParcel($myparcel["uid"]);
  //$paidTtlCNY = 0;
  //foreach ($dataTbArr as $dataTb) {
	//  $paidTtlCNY += intVal($dataTb["taobaoDingdanCNY"]);
  //}
  
  //$ttlJPY = $myparcel["itemTtlJPY"] + $myparcel["transfeeGuojiJPY"];
  $ttlCNY = $myparcel["itemTtlCNY"] + $myparcel["transfeeGuojiCNY"] + $myparcel["transfeeGuonei"];
  $bukuanCNY = $ttlCNY - $myparcel["paidTtlCNY"] ;
?>
  <input type="hidden" id="uid" value="<?php echo $myparcel['uid'] ?>">
  <div class="row mb-4 form-group">
    <div class="col-10 themed-grid-col">
      <label for="itemTtlJPY">item total(JPY)</label>
      <input type="text" class="form-control" id="itemTtlJPY" readonly value="<?php echo $itemTtlJPY ?>">
    </div>
    <div class="col-10 themed-grid-col">
      <label for="itemTtlCNY">item total(CNY)</label>
      <input type="text" class="form-control" id="itemTtlCNY" readonly value="<?php echo $itemTtlCNY ?>">
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-6 themed-grid-col">
        <label for="guojiShoudan">shoudan</label>
        <select class="custom-select d-block w-100" id="guojiShoudan" class="form-control" >
            <option value=""></option>
            <option value="EMS" <?php if($myparcel['guojiShoudan']=='EMS'){?> selected <?php } ?>>EMS</option>
            <!--<option value="AIR" <?php if($myparcel['guojiShoudan']=='AIR'){?> selected <?php } ?>>AIR</option>-->
            <option value="SAL" <?php if($myparcel['guojiShoudan']=='SAL'){?> selected <?php } ?>>SAL</option>
            <option value="SEA" <?php if($myparcel['guojiShoudan']=='SEA'){?> selected <?php } ?>>SEA</option>
            <option value="PINGYOU" <?php if($myparcel['guojiShoudan']=='PINGYOU'){?> selected <?php } ?>>PINGYOU</option>
        </select>
    </div>
    <div class="col-6 themed-grid-col">
	  <ul>
	   <li></li>
	 </ul>
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-10 themed-grid-col">
      <label for="transWeight">transWeight</label>
      <input type="text" class="form-control" id="transWeight" <?php if($isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['itemTtlWeight'] ?>">
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-10 themed-grid-col">
      <label for="transfeeGuojiJPY">transfeeGuojiJPY</label>
      <input type="text" class="form-control" id="transfeeGuojiJPY" <?php if($isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuojiJPY'] ?>">
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-10 themed-grid-col">
      <label for="transnoGuoji">transnoGuoji</label>
      <input type="text" class="form-control" id="transnoGuoji" <?php if($isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transnoGuoji'] ?>">
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-10 themed-grid-col">
      <label for="transfeeGuonei">transfeeGuonei</label>
      <input type="text" class="form-control" id="transfeeGuonei" <?php if($isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuonei'] ?>">
    </div>
    <div class="col-10 themed-grid-col">
      <label for="transnoGuonei">transnoGuonei</label>
      <input type="text" class="form-control" id="transnoGuonei" <?php if($isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transnoGuonei'] ?>">
    </div>
  </div>
  <hr class="mb-4">
  <div class="row mb-4 form-group">
    <div class="col-10 themed-grid-col">
      <label for="ttlCNY">total(CNY)</label>
      <input type="text" class="form-control" id="ttlCNY" readonly value="<?php echo $ttlCNY ?>">
    </div>
    <div class="col-10 themed-grid-col">
      <label for="bukuanCNY">paidTtl(CNY)</label>
      <input type="text" class="form-control" id="paidTtlCNY" readonly value="<?php echo $paidTtlCNY ?>">
    </div>
    <div class="col-10 themed-grid-col">
      <label for="bukuanCNY">bukuan(CNY)</label>
      <input type="text" class="form-control" id="bukuanCNY" readonly value="<?php echo $bukuanCNY ?>">
    </div>
  </div>
  <hr class="mb-4">
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="taobaoDingdanhao">taobaoDingdanhao</label>
      <input type="text" class="form-control" id="taobaoDingdanhao" readonly>
    </div>
    <div class="col-4 themed-grid-col">
      <label for="taobaoDingdanCNY">cny</label>
      <input type="text" class="form-control" id="taobaoDingdanCNY" readonly>
    </div>
    <div class="col-2 themed-grid-col">
		<button class="btn" id="btnAddTaobaoDingdan" type="button">add</button>
    </div>
  </div>
  <hr class="mb-4">
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">宝贝名</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">日元</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">daoneiyunfei</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">priceCNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">zhongliang(g)</div>
  </div>
<?php
  foreach ($dataArr as $data) {
	  $boxCss = "bg-warning text-white";
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="biduid" value="<?php echo $data['uid'] ?>">
    <div class="col-4 text-break themed-grid-col border border-secondary">
	  <a href="<?php echo $data['itemUrl'] ?>" target="blank">
	    <?php echo $data['itemName'] ?>
	  </a>
	</div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceJPY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["transfeeJPY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceCNY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["weight"] ?></div>
    </div>
  </div>
<?php
  }
?>
  <hr class="mb-4">
  <div class="row">
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">taobaoDingdanhao</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">cny</div>
  </div>
<?php
  $dataTbArr = $my->listTaobaoDingdanByParcel($myparcel["uid"]);
  foreach ($dataTbArr as $dataTb) {
	  $boxCss = "bg-warning text-white";
?>
  <div class="row <?php echo $boxCss ?>">
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $dataTb["taobaoDingdanhao"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $dataTb["taobaoDingdanCNY"] ?></div>
    </div>
  </div>
<?php
  }
?>
</div>
</body>
</html>