<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/../adminsupp/MyHuilv.php';
require __DIR__ .'/MyYaBid.php';

  $buyer = $_GET["buyer"];
  $admin = $_GET["admin"];
  $uid = $_GET["uid"];
  
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
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYYABID") ?>";
$(function() {
    $(document).on("click", "#btnAddTaobaoDingdan", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addTaobaoDingdan", 
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val(),
					   "taobaoDingdanhao" : $("#taobaoDingdanhao").val(),
					   "taobaoDingdanCNY" : $("#taobaoDingdanCNY").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
			if(msg != ""){
				alert(msg);
			}else{
				location.reload();
			}
        });
    });
    $(document).on("click", "#btnDeleteTaobaoDingdan", function() {
		var thisBox = $(this).parent().parent();
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"deleteTaobaoDingdan", 
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val(),
					   "taobaodingdanUid" : thisBox.find("#taobaodingdanUid").val(),
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
			if(msg != ""){
				alert(msg);
			}else{
				location.reload();
			}
        });
    });
    $(document).on("change", "#guojiShoudan", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"getYunfei", 
					   "weigth" : $("#transWeight").val(),
					   "guojiShoudan" : $("#guojiShoudan").val(),
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
		    var huilv = parseFloat($("#myhuilv").val());
            var vals = msg.split(":");
			var transfeeGuojiJPY = parseInt(vals[0]);
			var transfeeGuonei = parseInt(vals[1]);
			
			$("#transfeeGuojiJPY").val(transfeeGuojiJPY);
			
			var transfeeGuojiCNY = Math.ceil(transfeeGuojiJPY * huilv);
			$("#transfeeGuojiCNY").val(transfeeGuojiCNY);
			
			$("#transfeeGuonei").val(transfeeGuonei);
			
			var itemTtlJPY = parseInt($("#itemTtlJPY").val());
			var itemTransfeeDaoneiTtlJPY = parseInt($("#itemTransfeeDaoneiTtlJPY").val());
			var itemTtlCNY = parseInt($("#itemTtlCNY").val());
			var ttlCNY = itemTtlCNY + transfeeGuojiCNY + transfeeGuonei;
			$("#ttlJPY").val(itemTtlJPY + itemTransfeeDaoneiTtlJPY + transfeeGuojiJPY);
			$("#ttlCNY").val(ttlCNY);
			
			var paidTtlCNY = parseInt($("#paidTtlCNY").val());
			$("#bukuanCNY").val(ttlCNY - paidTtlCNY);
        });
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  $isAdmin = $my->isAdmin($admin);
  include __DIR__ .'/subpage_toplink.php';
  if($admin != '' && !$isAdmin){
	  exit(0);
  }
  if(empty($buyer)){
	  exit(0);
  }
?>
  <h3>买家:<span><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
<?php
  $myparcel = $my->listParcelByBuyerAndUnParcel($buyer);
  //var_dump($myparcel);
  $dataArr = $my->listItemByBuyerAndUnParcel($buyer, $myparcel["uid"]);
  
  $ttlCNY = $myparcel["itemTtlCNY"] + $myparcel["transfeeGuojiCNY"] + $myparcel["transfeeGuonei"];
  $bukuanCNY = $ttlCNY - $myparcel["paidTtlCNY"] ;
  
  $myhuilv = new MyHuilv();
  $huilv = $myhuilv->listByHuilvDiv("YA");
?>
  <input type="hidden" id="myparcelUid" value="<?php echo $myparcel['uid'] ?>">
  <input type="hidden" id="myhuilv" value="<?php echo $huilv ?>">
  <input type="hidden" id="buyer" value="<?php echo $buyer ?>">
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="transWeight">transWeight</label>
      <input type="text" class="form-control" id="transWeight" <?php if(!$isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['itemTtlWeight'] ?>">
    </div>
    <div class="col-4 themed-grid-col">
        <label for="guojiShoudan">shoudan</label>
        <select class="custom-select d-block" id="guojiShoudan" class="form-control" >
            <option value=""></option>
            <option value="EMS" <?php if($myparcel['guojiShoudan']=='EMS'){?> selected <?php } ?>>EMS</option>
            <!--<option value="AIR" <?php if($myparcel['guojiShoudan']=='AIR'){?> selected <?php } ?>>AIR</option>-->
            <option value="SAL" <?php if($myparcel['guojiShoudan']=='SAL'){?> selected <?php } ?>>SAL</option>
            <option value="SEA" <?php if($myparcel['guojiShoudan']=='SEA'){?> selected <?php } ?>>SEA</option>
            <option value="PINGYOU" <?php if($myparcel['guojiShoudan']=='PINGYOU'){?> selected <?php } ?>>PINGYOU</option>
        </select>
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="transfeeGuojiJPY">transfeeGuojiJPY</label>
      <input type="text" class="form-control" id="transfeeGuojiJPY" <?php if(!$isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuojiJPY'] ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="transfeeGuojiCNY">transfeeGuojiCNY</label>
      <input type="text" class="form-control" id="transfeeGuojiCNY" <?php if(!$isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuojiCNY'] ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="transnoGuoji">transnoGuoji</label>
      <input type="text" class="form-control" id="transnoGuoji" <?php if(!$isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transnoGuoji'] ?>">
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="transfeeGuonei">transfeeGuonei</label>
      <input type="text" class="form-control" id="transfeeGuonei" <?php if(!$isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuonei'] ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="transnoGuonei">transnoGuonei</label>
      <input type="text" class="form-control" id="transnoGuonei" <?php if(!$isAdmin){?> readonly <?php } ?> value="<?php echo $myparcel['transnoGuonei'] ?>">
    </div>
  </div>
  <hr class="mb-4">
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="ttlCNY">total(CNY)</label>
      <input type="text" class="form-control" id="ttlCNY" readonly value="<?php echo $ttlCNY ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="bukuanCNY">paidTtl(CNY)</label>
      <input type="text" class="form-control" id="paidTtlCNY" readonly value="<?php echo $myparcel["paidTtlCNY"] ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="bukuanCNY">bukuan(CNY)</label>
      <input type="text" class="form-control" id="bukuanCNY" readonly value="<?php echo $bukuanCNY ?>">
    </div>
  </div>
  <hr class="mb-4">
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">宝贝名</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">日元</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">priceCNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">transfeeDaoneiJPY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">transfeeDaoneiCNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">daigoufeiCNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">itemCNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">zhongliang(g)</div>
  </div>
  <div class="row bg-success text-white">
    <div class="col-4 text-break themed-grid-col border border-primary bg-info text-white">heji</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["itemTtlPriceJPY"] ?>
	  <input type="hidden" class="form-control" id="itemTtlPriceJPY"  value="<?php echo $myparcel["itemTtlPriceJPY"] ?>">
	</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["itemTtlPriceCNY"] ?>
	  <input type="hidden" class="form-control" id="itemTtlPriceCNY"  value="<?php echo $myparcel["itemTtlPriceCNY"] ?>">
	</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["itemTransfeeDaoneiTtlJPY"] ?>
	  <input type="hidden" class="form-control" id="itemTransfeeDaoneiTtlJPY" readonly value="<?php echo $myparcel["itemTransfeeDaoneiTtlJPY"] ?>">
	</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["itemTransfeeDaoneiTtlCNY"] ?>
	  <input type="hidden" class="form-control" id="itemTransfeeDaoneiTtlCNY" readonly value="<?php echo $myparcel["itemTransfeeDaoneiTtlCNY"] ?>">
	</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["daigoufeiTtlCNY"] ?>
	  <input type="hidden" class="form-control" id="daigoufeiTtlCNY" readonly value="<?php echo $myparcel["daigoufeiTtlCNY"] ?>">
	</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["itemTtlCNY"] ?>
	  <input type="hidden" class="form-control" id="itemTtlCNY" readonly value="<?php echo $myparcel["itemTtlCNY"] ?>">
	</div>
    <div class="col text-break themed-grid-col border border-primary">
	  <?php echo $myparcel["itemTtlWeight"] ?>
	  <input type="hidden" class="form-control" id="itemTtlWeight" readonly value="<?php echo $myparcel["itemTtlWeight"] ?>">
	</div>
  </div>
<?php
  foreach ($dataArr as $data) {
	  $boxCss = "bg-warning text-white";
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="biduid" value="<?php echo $data['uid'] ?>">
    <div class="col-4 text-break themed-grid-col border border-secondary">
	  <a href="<?php echo $data['itemUrl'] ?>" target="blank">
	    <?php echo $data['itemName'] != '' ? $data['itemName'] : $data['itemUrl'] ?>
	  </a>
	</div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceJPY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["priceCNY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["transfeeDaoneiJPY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["transfeeDaoneiCNY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["daigoufeiCNY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["itemCNY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $data["weight"] ?></div>
  </div>
<?php
  }
?>
  <hr class="mb-4">
  <div class="row mb-4 form-group">
    <div class="col-6 themed-grid-col">
      <label for="taobaoDingdanhao">taobaoDingdanhao</label>
      <input type="text" class="form-control" id="taobaoDingdanhao" >
    </div>
    <div class="col-3 themed-grid-col">
      <label for="taobaoDingdanCNY">cny</label>
      <input type="text" class="form-control" id="taobaoDingdanCNY" >
    </div>
    <div class="col-2 themed-grid-col">
		<button class="btn btn-secondary actionBtn" id="btnAddTaobaoDingdan" type="button">add</button>
    </div>
  </div>
  <hr class="mb-4">
  <div class="row">
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">taobaoDingdanhao</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">cny</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">action</div>
  </div>
<?php
  $dataTbArr = $my->listTaobaoDingdanByParcel($buyer, $myparcel["uid"]);
  foreach ($dataTbArr as $dataTb) {
	  $boxCss = "bg-warning text-white";
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="taobaodingdanUid" value="<?php echo $dataTb['uid'] ?>">
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $dataTb["taobaoDingdanhao"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary"><?php echo $dataTb["taobaoDingdanCNY"] ?></div>
    <div class="col text-break themed-grid-col border border-secondary">
	  <button class="btn btn-secondary actionBtn" id="btnDeleteTaobaoDingdan" type="button">delete</button>
	</div>
    </div>
<?php
  }
?>
  </div>
</div>
</body>
</html>