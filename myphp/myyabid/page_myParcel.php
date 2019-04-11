<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

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
    $(document).on("click", "#btnReCalc", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"calcParcelPrice", 
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
			location.reload();
        });
    });
    $(document).on("click", "#btnGuojiFahuo", function() {
		var transferNo = $("#transnoGuoji").val();
		if(transferNo == ''){
			alert("please input 国际快递号码!");
			return;
		}
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"parcelGuojiFahuo", 
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val(),
					   "transferNo" : transferNo
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
			location.reload();
        });
    });
    $(document).on("click", "#btnGuoneiFahuo", function() {
		var transferNo = $("#transnoGuonei").val();
		if(transferNo == ''){
			alert("please input 国内快递号码!");
			return;
		}
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"parcelGuoneiFahuo", 
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val(),
					   "transferNo" : transferNo
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
			location.reload();
        });
    });
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
    $(document).on("change", ".parcelAmt", function() {
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateParcelAmt", 
					   "myparcelUid" : $("#myparcelUid").val(),
					   "buyer" : $("#buyer").val(),
					   "guojiShoudan" : $("#guojiShoudan").val(),
					   "itemTtlWeight" : $("#transWeight").val(),
					   "transfeeGuojiJPY" : $("#transfeeGuojiJPY").val(),
					   "transfeeGuojiCNY" : $("#transfeeGuojiCNY").val(),
					   "dabaofeiCNY" : $("#dabaofeiCNY").val(),
					   "transnoGuoji" : $("#transnoGuoji").val(),
					   "transfeeGuonei" : $("#transfeeGuonei").val(),
					   "transnoGuonei" : $("#transnoGuonei").val(),
					   "paidTtlCNY" : $("#paidTtlCNY").val()
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
  if(!empty($uid)){
	  $myparcel = $my->listParcelByBuyerAndUid($buyer, $uid);
  }else{
	  $myparcel = $my->listParcelByBuyerAndUnParcel($buyer);
  }
  $dataArr = $my->listItemByBuyerAndParcelUid($buyer, $myparcel["uid"]);
  //var_dump($myparcel);
  
  $bukuanCNY = $myparcel["itemTtlCNY"] - $myparcel["paidTtlCNY"] ;
  
  $huilv = $my->getHuilv();
  $ttlCNY = $myparcel["itemTtlCNY"]
            + $myparcel["transfeeGuojiCNY"] + $myparcel["transfeeGuonei"]
            + $myparcel["transfeeGuonei"] + $myparcel["dabaofeiCNY"]
            - $myparcel["barginCNY"];
  //var_dump($myparcel);
  $bukuanCNY = $ttlCNY - $myparcel["paidTtlCNY"] ;
  
  $guojiShoudanEditFlag = ($myparcel["status"] === "daBao");
  
  $amtEditFlag = ($myparcel["status"] === "daBao") && $isAdmin;
  $transnoGuoneiEditFlag = ($myparcel["status"] === "zaiTu") && $isAdmin;
?>
  <input type="hidden" id="myparcelUid" value="<?php echo $myparcel['uid'] ?>">
  <input type="hidden" id="myhuilv" value="<?php echo $huilv ?>">
  <input type="hidden" id="buyer" value="<?php echo $buyer ?>">
  <div class="row mb-4 form-group">
    <div class="col-6 themed-grid-col">
      <label for="transWeight">包裹重量(g)</label>
      <input type="text" class="form-control parcelAmt" id="transWeight" <?php if(!$amtEditFlag){?> readonly <?php } ?> value="<?php echo $myparcel['itemTtlWeight'] ?>">
    </div>
    <div class="col-6 themed-grid-col">
        <label for="guojiShoudan">快递方式</label>
        <select class="custom-select form-control parcelAmt" id="guojiShoudan" <?php if(!$guojiShoudanEditFlag){?> disabled <?php } ?>>
            <option value=""></option>
            <option value="EMS" <?php if($myparcel['guojiShoudan']=='EMS'){?> selected <?php } ?>>EMS</option>
            <!--<option value="AIR" <?php if($myparcel['guojiShoudan']=='AIR'){?> selected <?php } ?>>AIR</option>-->
            <option value="SAL" <?php if($myparcel['guojiShoudan']=='SAL'){?> selected <?php } ?>>SAL</option>
            <option value="SEA" <?php if($myparcel['guojiShoudan']=='SEA'){?> selected <?php } ?>>海运</option>
            <option value="PINGYOU" <?php if($myparcel['guojiShoudan']=='PINGYOU'){?> selected <?php } ?>>拼邮</option>
        </select>
    </div>
  </div>
  <div class="row mb-4 form-group">
    <input type="hidden" id="transfeeGuojiJPY" value="<?php echo $myparcel['transfeeGuojiJPY'] ?>">
    <div class="col-6 themed-grid-col">
      <label for="transfeeGuojiCNY">国际运费CNY</label>
      <input type="text" class="form-control parcelAmt" id="transfeeGuojiCNY" <?php if(!$amtEditFlag){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuojiCNY'] ?>">
    </div>
    <div class="col-6 themed-grid-col">
      <label for="dabaofeiCNY">daobao/cailiaofei CNY</label>
      <input type="text" class="form-control parcelAmt" id="dabaofeiCNY" <?php if(!$amtEditFlag){?> readonly <?php } ?> value="<?php echo $myparcel['dabaofeiCNY'] ?>">
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-8 themed-grid-col">
      <label for="transnoGuoji">国际快递号码</label>
      <input type="text" class="form-control" id="transnoGuoji" <?php if(!$amtEditFlag){?> readonly <?php } ?> value="<?php echo $myparcel['transnoGuoji'] ?>">
    </div>
<?php 
    if($amtEditFlag){
?>
    <div class="col">
	  <button class="btn btn-secondary actionBtn" id="btnGuojiFahuo" type="button">国际快递发货</button>
	</div>
<?php 
    }
?>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="transfeeGuonei">国内运费CNY</label>
      <input type="text" class="form-control parcelAmt" id="transfeeGuonei" <?php if(!$amtEditFlag){?> readonly <?php } ?> value="<?php echo $myparcel['transfeeGuonei'] ?>">
    </div>
    <div class="col-6 themed-grid-col">
      <label for="transnoGuonei">国内快递号码</label>
      <input type="text" class="form-control" id="transnoGuonei" <?php if(!$transnoGuoneiEditFlag){?> readonly <?php } ?> value="<?php echo $myparcel['transnoGuonei'] ?>">
    </div>
<?php 
  if($transnoGuoneiEditFlag){
?>
    <div class="col">
	  <button class="btn btn-secondary actionBtn" id="btnGuoneiFahuo" type="button">国际快递发货</button>
	</div>
<?php 
  }
?>
  </div>
  <hr class="mb-4">
  <div class="row mb-4 form-group">
    <div class="col-4 themed-grid-col">
      <label for="ttlCNY">总金额(CNY)</label>
      <input type="text" class="form-control" id="ttlCNY" readonly value="<?php echo $ttlCNY ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="bukuanCNY">已付款(CNY)</label>
      <input type="text" class="form-control" id="paidTtlCNY" readonly value="<?php echo $myparcel["paidTtlCNY"] ?>">
    </div>
    <div class="col-4 themed-grid-col">
      <label for="bukuanCNY">补款(CNY)</label>
      <input type="text" class="form-control" id="bukuanCNY" readonly value="<?php echo $bukuanCNY ?>">
    </div>
  </div>
  <hr class="mb-4">
  <div class="row">
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">宝贝名</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">货值JPY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">货值CNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">岛内运费JPY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">岛内运费CNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">代购费CNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">商品合计CNY</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">重量(g)</div>
  </div>
  <div class="row bg-success text-white">
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">
	合计
<?php 
  if($amtEditFlag){
?>
	<button class="btn btn-secondary actionBtn" id="btnReCalc" type="button">再计算</button>
<?php 
  }
?>
	</div>
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
    <div class="col-2 text-break themed-grid-col border border-secondary">
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
<?php 
	if($myparcel["status"] == "daBao"){
?>
  <hr class="mb-4">
  <div class="row mb-4 form-group">
    <div class="col-7 themed-grid-col">
      <label for="taobaoDingdanhao">淘宝订单号</label>
      <input type="text" class="form-control" id="taobaoDingdanhao" >
    </div>
    <div class="col-5 themed-grid-col">
      <label for="taobaoDingdanCNY">金额</label>
      <input type="text" class="form-control" id="taobaoDingdanCNY" >
    </div>
  </div>
  <div class="row mb-4 form-group">
    <div class="col-12 themed-grid-col">
	  <button class="btn btn-secondary actionBtn" id="btnAddTaobaoDingdan" type="button">添加淘宝订单</button>
    </div>
  </div>
<?php
	}
?>
  <hr class="mb-4">
  <div class="row">
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">淘宝订单号</div>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">金额</div>
<?php 
	if($myparcel["status"] == "daBao"){
?>
    <div class="col text-break themed-grid-col border border-primary bg-info text-white">action</div>
<?php
	}
?>
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
<?php 
	if($myparcel["status"] == "daBao"){
?>
    <div class="col text-break themed-grid-col border border-secondary">
	  <button class="btn btn-secondary actionBtn" id="btnDeleteTaobaoDingdan" type="button">delete</button>
	</div>
<?php
	}
?>
    </div> 
<?php
	}
  }
?>
  </div>
</div>
</body>
</html>