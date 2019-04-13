<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ . '/MyMontb.php';
?>
<html lang="ja">
<head>
<title>my montbell order</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYMONTB") ?>";
$(function(){
	var validateLen = function(vStr,vlen){
		var l = vStr.length;
		if(l > vlen){
			alert("too long!max len = " + vlen);
		}
	};
    $(document).on("click", "#btnConvertHanziToPY", function() {
        var hanzi = $("#maijiadianzhiHanzi").val();
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":"convertHanziToPY", "hanzi" : hanzi},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			/*
			*/
            $("#maijiadianzhiPY").val(msg);
        });
    });
    $(document).on("click", "#btnConvert", function() {
		var srcTxt = $("#maijiadianzhiPY").val();
		var adrArr = srcTxt.split("\n");
		if(adrArr.length < 10){
			alert("[ERROR]10 Lines!");
			return ;
		}
		var idx = 2;
		var nmArr = adrArr[idx++].split(/(?=[A-Z])/);
		if(nmArr.length>2){
			$("#firstName").val(nmArr[0]);
			$("#lastName").val(nmArr[1] + nmArr[2]);
		}else if(nmArr.length>1){
			$("#firstName").val(nmArr[0]);
			$("#lastName").val(nmArr[1]);
		}else{
			$("#firstName").val(nmArr[0]);
			$("#lastName").val(nmArr[0]);
		}
		$("#tel").val(adrArr[idx++]);
		$("#statePY").val(adrArr[idx++]);
		$("#cityPY").val(adrArr[idx++]);
		$("#adr1PY").val(adrArr[idx++]);
		$("#adr2PY").val(adrArr[idx++]);
		$("#postcode").val(adrArr[idx++]);
	});
	var formParameter = function(){
		param = {};
        param.uid = $("#orderUid").val();
        param.firstName = $("#firstName").val();
        param.lastName = $("#lastName").val();
        param.tel = $("#tel").val();
        param.postcode = $("#postcode").val();
        param.statePY = $("#statePY").val();
        param.cityPY = $("#cityPY").val();
        param.adr1PY = $("#adr1PY").val();
        param.adr2PY = $("#adr2PY").val();
        param.fukuanWay = $("#fukuanWay").val();
		return param;
	};
    $(document).on("click", "#btnUpdate", function() {
        
	    var param = formParameter();
		param.action = "updateMBOrder";
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnOrder", function() {
        
	    var param = formParameter();
		param.action = "orderMBOrder";
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnUpdateMBOrderNo", function() {
        
	    var param = formParameter();
		param.action = "updateMBOrderNo";
		param.mbOrderNo = $("#mbOrderNo").val();
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnUpdateTransferNo", function() {
        
	    var param = formParameter();
		param.action = "updateTransferNo";
		param.transferNoGuoji = $("#transferNoGuoji").val();
		param.transferNoGuonei = $("#transferNoGuonei").val();
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("change", "#cityPY", function() {
        var thisVal = $("#cityPY").val();
		validateLen(thisVal,20);
    }); 
    $(document).on("change", "#adr1PY", function() {
        var thisVal = $("#adr1PY").val();
		validateLen(thisVal,30);
    }); 
    $(document).on("change", "#adr2PY", function() {
        var thisVal = $("#adr2PY").val();
		validateLen(thisVal,30);
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
  if(!isset($_GET['uid'])){
	  echo "ERROR!";
	exit('0');
  }
  $orderUid = $_GET['uid'];
  $my = new MyMontb();
  $orderObj = $my->listMBOrderInfoByUid($orderUid);
  
  $tbOrderObj = $my->listTBOrderInfoByMBUid($orderUid);
  
  $productObjList = $my->listProductInfoByMBUid($orderUid);
  
  $editFlag = ($orderObj["status"] == 'unorder' || $orderObj["status"] == 'ordered');
?>
  <input type="hidden" id="orderUid" value="<?php echo $orderUid ?>">
<?php
  if($orderObj['status'] == 'unorder'){
?>
      <div class="row mb-1 form-group">
        <div class="col-12">
		  <label for="mbOrderNo">MB 官网订单号</label>
		  <input type="text" class="form-control" id="mbOrderNo" value="<?php echo $orderObj['mbOrderNo'] ?>">
        </div>
      </div>
      <div class="row mb-4">
        <div class="col-12">
		  <button type="button" id="btnUpdateMBOrderNo" class="btn btn-secondary">UPDATE</button>
        </div>
      </div>
<?php
  }else{
?>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="mbOrderNo">MB 官网订单号</label>
		  <input type="text" class="form-control" id="mbOrderNo" value="<?php echo $orderObj['mbOrderNo'] ?>" readonly>
        </div>
      </div>
<?php
  }
?>
<?php
  if($orderObj['status'] == 'ordered'||$orderObj['status'] == 'mbfh'){
?>
      <div class="row mb-1 form-group">
        <div class="col-2">
		  <label for="mbOrderNo">Transfer No</label>
        </div>
        <div class="col-5">
		  <input type="text" class="form-control" id="transferNoGuoji" value="<?php echo $orderObj['transferNoGuoji'] ?>">
        </div>
        <div class="col-5">
		  <input type="text" class="form-control" id="transferNoGuonei" value="<?php echo $orderObj['transferNoGuonei'] ?>">
        </div>
      </div>
      <div class="row mb-4">
        <div class="col-12">
		  <button type="button" id="btnUpdateTransferNo" class="btn btn-secondary">UPDATE</button>
        </div>
      </div>
<?php
  }
?>
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-10">
		  <label for="maijia">淘宝买家ID</label>
		  <input type="text" class="form-control" id="maijia" value="<?php echo $tbOrderObj['maijia'] ?>" readOnly ></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10">
		  <label for="dingdanhao">淘宝订单号</label>
		  <input type="text" class="form-control" id="dingdanhao" value="<?php echo $tbOrderObj['dingdanhao'] ?>" readOnly></div>
      </div>
<?php
  $prodSize = count($productObjList);
  $lines = $tbOrderObj['maijiadianzhiHanzi'] . "\n";
  for($i = 0, $size = $prodSize; $i < $size; ++$i) {
	$p = $productObjList[$i];
	$lines .= "商家编码:" . $p["productId"] . ' 颜色分类:' . $p["colorName"] . ';尺码:' . $p["sizeName"] . "\n";
  }
?>
      <div class="row mb-4 form-group">
        <div class="col-8 themed-grid-col">
		  <textarea id="tempTxtArea" rows="6" cols="35"><?php echo $lines ?></textarea>
		</div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10">
            <label for="maijiadianzhiHanzi">买家地址</label>
            <input type="text" class="form-control" id="maijiadianzhiHanzi" value="<?php echo $tbOrderObj['maijiadianzhiHanzi'] ?>" readOnly>
        </div>
<?php if($editFlag){?> 
        <div class="col-2">
		  <button type="button" id="btnConvertHanziToPY" class="btn btn-secondary" <?php if(!$editFlag){?> readOnly <?php } ?>>TO PY</button>
        </div>
<?php } ?>
      </div>
<?php if($editFlag){?> 
      <div class="row mb-4 form-group">
        <div class="col-12">
            <label for="maijiadianzhiPY">买家地址(拼音)</label>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
            <textarea  cols="40" rows="6" id="maijiadianzhiPY"  ></textarea >
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
          <button type="button" id="btnConvert" class="btn btn-secondary">C o n v e r t !</button>
		</div>
      </div>
<?php } ?>
      <div class="row mb-4 form-group">
        <div class="col-6">
		  <label for="maijiaName">First Name</label>
		  <input type="text" class="form-control" id="firstName" value="<?php echo $orderObj['firstName'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
        <div class="col-6">
		  <label for="maijiaName">Last Name</label>
		  <input type="text" class="form-control" id="lastName" value="<?php echo $orderObj['lastName'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-8">
		  <label for="tel">tel</label>
		  <input type="text" class="form-control" id="tel" value="<?php echo $orderObj['tel'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
        <div class="col-4">
		  <label for="postcode">postcode</label>
		  <input type="text" class="form-control" id="postcode" value="<?php echo $orderObj['postcode'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-5">
		  <label for="state">省(拼音)</label>
		  <input type="text" class="form-control" id="statePY" value="<?php echo $orderObj['statePY'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
        <div class="col-7">
		  <label for="city">市(拼音) Max 20</label>
		  <input type="text" class="form-control" id="cityPY" value="<?php echo $orderObj['cityPY'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="adr1">地址1(拼音) Max 30</label>
		  <input type="text" class="form-control valid-length" id="adr1PY" value="<?php echo $orderObj['adr1PY'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="adr2">地址2(拼音) Max 30</label>
		  <input type="text" class="form-control valid-length" id="adr2PY" value="<?php echo $orderObj['adr2PY'] ?>" <?php if(!$editFlag){?> readOnly <?php } ?>>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="fukuanWay">付款CR</label>
            <select class="custom-select d-block w-100" id="fukuanWay" <?php if(!$editFlag){?> disabled <?php } ?>>
                <option value="1" <?php if($orderObj['fukuanWay']=='1'){?> selected <?php } ?>>Line JCB</option>
                <option value="2" <?php if($orderObj['fukuanWay']=='2'){?> selected <?php } ?>>CCB Master</option>
            </select>
        </div>
      </div>
<?php if($editFlag){?> 
      <div class="row mb-4 form-group">
        <div class="col-5">
		  <button type="button" id="btnUpdate" class="btn btn-secondary">UPDATE</button>
        </div>
        <div class="col-5">
		  <button type="button" id="btnOrder" class="btn btn-secondary">to order list</button>
        </div>
      </div>
<?php } ?>
  </div>
</div>
</body>
</html>