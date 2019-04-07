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
(function (original) {
  jQuery.fn.clone = function () {
    var result           = original.apply(this, arguments),
        my_textareas     = this.find('textarea').add(this.filter('textarea')),
        result_textareas = result.find('textarea').add(result.filter('textarea')),
        my_selects       = this.find('select').add(this.filter('select')),
        result_selects   = result.find('select').add(result.filter('select'));

    for (var i = 0, l = my_textareas.length; i < l; ++i) $(result_textareas[i]).val($(my_textareas[i]).val());
    for (var i = 0, l = my_selects.length;   i < l; ++i) result_selects[i].selectedIndex = my_selects[i].selectedIndex;

    return result;
  };
}) (jQuery.fn.clone);
</script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYMONTB") ?>";
$(function() {
    $(document).on("click", "#btnConvertHanziToPY", function() {
        var hanzi = $("#maijiadianzhiHanzi").val();
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":"convertHanziToPY", "hanzi" : hanzi},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			var adrArr = msg.split("\n");
			if(adrArr.length>2){
				var nmArr = adrArr[2].split(/(?=[A-Z])/);
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
			}
			if(adrArr.length>3){
				$("#tel").val(adrArr[3]);
			}
			if(adrArr.length>4){
				$("#statePY").val(adrArr[4]);
			}
			if(adrArr.length>5){
				$("#cityPY").val(adrArr[5]);
			}
			if(adrArr.length>6){
				$("#adr1PY").val(adrArr[6]);
			}
			if(adrArr.length>8){
				$("#adr2PY").val(adrArr[7] + " " + adrArr[8]);
			}
			if(adrArr.length>9){
				$("#adr2PY").val(adrArr[7] + " " + adrArr[8]);
				$("#postcode").val(adrArr[9]);
			}
            $("#maijiadianzhiPY").val(msg);
        });
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
	}
    $(document).on("click", "#btnOrder", function() {
        
	    var param = formParameter();
		param.action = "orderOrder";
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : param,
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert("请看后台执行MB官网下单情况！！");
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
  if(!isset($_GET['uid'])){
	  echo "ERROR!";
	exit('0');
  }
  $orderUid = $_GET['uid'];
  $my = new MyMontb();
  $orderObj = $my->listOrderInfoByUid($orderUid);
?>
  <input type="hidden" id="orderUid" value="<?php echo $orderUid ?>">
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-10">
		  <label for="maijia">淘宝买家ID</label>
		  <input type="text" class="form-control" id="maijia" value="<?php echo $orderObj['maijia'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10">
		  <label for="dingdanhao">淘宝订单号</label>
		  <input type="text" class="form-control" id="dingdanhao" value="<?php echo $orderObj['dingdanhao'] ?>"></div>
      </div>
<?php
  $prodSize = count($orderObj["productObjList"]);
  for($i = 0, $size = $prodSize; $i < $size; ++$i) {
	$p = $orderObj["productObjList"][$i];
	$lines = $p["productId"] . ' ' . $p["colorName"] . ' ' . $p["sizeName"];
?>
      <div class="row mb-4 form-group">
        <div class="col-12">
            <label for="maijiadianzhiHanzi">宝贝</label>
            <input type="text" class="form-control" id="baobailist" value="<?php echo $lines ?>">
        </div>
      </div>
<?php
  }
?>
      <div class="row mb-4 form-group">
        <div class="col-10">
            <label for="maijiadianzhiHanzi">买家地址</label>
            <input type="text" class="form-control" id="maijiadianzhiHanzi" value="<?php echo $orderObj['maijiadianzhiHanzi'] ?>">
        </div>
        <div class="col-2">
		  <button type="button" id="btnConvertHanziToPY" class="btn btn-secondary">TO PY</button>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10">
            <label for="maijiadianzhiPY">买家地址(拼音)</label>
            <textarea  cols="60" rows="6" id="maijiadianzhiPY"  ></textarea >
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-3">
		  <label for="maijiaName">F</label>
		  <input type="text" class="form-control" id="firstName" value="<?php echo $orderObj['firstName'] ?>">
        </div>
        <div class="col-3">
		  <label for="maijiaName">L</label>
		  <input type="text" class="form-control" id="lastName" value="<?php echo $orderObj['lastName'] ?>">
        </div>
        <div class="col-3">
		  <label for="tel">tel</label>
		  <input type="text" class="form-control" id="tel" value="<?php echo $orderObj['tel'] ?>">
        </div>
        <div class="col-3">
		  <label for="postcode">postcode</label>
		  <input type="text" class="form-control" id="postcode" value="<?php echo $orderObj['postcode'] ?>">
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-4">
		  <label for="state">省(拼音)</label>
		  <input type="text" class="form-control" id="statePY" value="<?php echo $orderObj['statePY'] ?>">
        </div>
        <div class="col-8">
		  <label for="city">市(拼音)</label>
		  <input type="text" class="form-control" id="cityPY" value="<?php echo $orderObj['cityPY'] ?>">
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="adr1">地址1(拼音)</label>
		  <input type="text" class="form-control" id="adr1PY" value="<?php echo $orderObj['adr1PY'] ?>">
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12">
		  <label for="adr2">地址2(拼音)</label>
		  <input type="text" class="form-control" id="adr2PY" value="<?php echo $orderObj['adr2PY'] ?>">
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-5">
		  <label for="fukuanWay">付款CR</label>
            <select class="custom-select d-block w-100" id="fukuanWay">
                <option value="1" selected>Line JCB</option>
                <option value="2">CCB Master</option>
            </select>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-5">
		  <button type="button" id="btnOrder" class="btn btn-secondary">MB官网下单</button>
        </div>
      </div>
      <hr class="mb-4">
  </div>
</div>
</body>
</html>