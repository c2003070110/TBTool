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
            $("#maijiadianzhiPY").val(msg);
        });
    });
	var formParameter = function(){
        param.uid = $("#orderUid").val();
        param.maijiaNamePY = $("#maijiaNamePY").val();
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
            alert(msg);
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
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-10">
		  <label for="maijia">maijia</label>
		  <input type="text" class="form-control" id="maijia" value="<?php echo $orderObj['maijia'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10">
		  <label for="dingdanhao">dingdanhao</label>
		  <input type="text" class="form-control" id="dingdanhao" value="<?php echo $orderObj['dingdanhao'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-8">
            <label for="maijiadianzhiHanzi">maijiadianzhiHanzi</label>
            <input type="text" class="form-control" id="maijiadianzhiHanzi" value="<?php echo $orderObj['maijiadianzhiHanzi'] ?>">
        </div>
        <div class="col-2">
		  <button type="button" id="btnConvertHanziToPY" class="btn btn-secondary">convert hanzi to PY</button>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10">
            <label for="maijiadianzhiPY">maijiadianzhiPY</label>
            <input type="textarea" col="10" row="6" id="maijiadianzhiPY" readonly>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-4">
		  <label for="maijiaName">maijiaNamePY</label>
		  <input type="text" class="form-control" id="maijiaName"></div>
        </div>
        <div class="col-4">
		  <label for="tel">tel</label>
		  <input type="text" class="form-control" id="tel"></div>
        </div>
        <div class="col-4">
		  <label for="postcode">postcode</label>
		  <input type="text" class="form-control" id="postcode"></div>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-4">
		  <label for="state">state</label>
		  <input type="text" class="form-control" id="statePY"></div>
        </div>
        <div class="col-4">
		  <label for="city">city</label>
		  <input type="text" class="form-control" id="cityPY"></div>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-4">
		  <label for="adr1">adr1</label>
		  <input type="text" class="form-control" id="adr1PY"></div>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-4">
		  <label for="adr2">adr2</label>
		  <input type="text" class="form-control" id="adr2PY"></div>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-5">
		  <label for="fukuanWay">fukuanWay</label>
            <select class="custom-select d-block w-100" id="fukuanWay">
                <option value="1" selected>Line JCB</option>
                <option value="2">CCB Master</option>
            </select>
        </div>
        <div class="col-5">
		  <button type="button" id="btnOrder" class="btn btn-secondary">ORDER</button>
        </div>
      </div>
      <hr class="mb-4">
  </div>
</div>
</body>
</html>