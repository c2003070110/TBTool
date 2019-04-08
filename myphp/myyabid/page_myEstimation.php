<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
  require __DIR__ .'/MyYaBid.php';
  $buyer = '';
  $admin = '';
  if(isset($_GET("buyer"))){
	  $buyer = $_GET("buyer");
  }
  if(isset($_GET("admin"))){
	  $admin = $_GET("admin");
  }
  if($buyer == '' && $admin !== 'zzzZZZzzz'){
	  exit(0);
  }
?>
<html lang="ja">
<head>
<title>My estimate</title>
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

<script src="myphp/myjavascript.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_ADMINSUPP") ?>";
$(function() {
    $(document).on("change", ".form-control", function() {
		var itemBoxes = $(".itembox");
		var huilv = parseDouble($("#myhuilv").val());
		var mydaigoufei = toInt($("#mydaigoufei").val());
		var priceJPYHeji=0, yunfeiJPJPYHeji=0, shoufeiCNYHeji=0;
		for(var i=0; i<itemBoxes.length; i++){
			var priceJPY = toInt($(itemBoxes[i]).find("#priceJPY").val());
			priceJPYHeji = priceJPYHeji + priceJPY;
			var yunfeiJPJPY = toInt($(itemBoxes[i]).find("#yunfeiJPJPY").val());
			yunfeiJPJPYHeji = yunfeiJPJPYHeji + yunfeiJPJPY;
			var shoufeiCNY = (priceJPY + yunfeiJPJPY) * huilv + mydaigoufei
			shoufeiCNYHeji = shoufeiCNYHeji + shoufeiCNY;
			$(itemBoxes[i]).find("#shoufeiCNY").val(shoufeiCNY);
		}
        $("#priceJPYHeji").val(priceJPYHeji);
        $("#yunfeiJPJPYHeji").val(yunfeiJPJPYHeji);
        $("#shoufeiCNYHeji").val(shoufeiCNYHeji);
    });
    $(document).on("change", ".form-control1", function() {
        var weigth = $("#weigth").val();
        var guojiShoudan = $("#guojiShoudan").val();
		var guojiYunfeiJPY = getGuojiYunfei(weigth, guojiShoudan);
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <div class="box">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">XXXX</label>
        <input type="hidden" id="myhuilv" >
        <input type="hidden" id="mydaigoufei" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnAddRow" class="btn btn-secondary actionBtn">add another item</button>
      </div>
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnYunfei" class="btn btn-secondary actionBtn">add yunfei</button>
      </div>
    </div>
  </div>
  <hr class="mb-4">
  <div class="box itembox">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">riyuan</label>
        <input type="text" class="form-control" id="priceJPY" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">yunfeiJP</label>
        <input type="text" class="form-control" id="yunfeiJPJPY" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">shoufei</label>
        <input type="text" class="form-control" id="shoufeiCNY" readonly>
      </div>
    </div>
  </div>
  <hr class="mb-4">
  <div class="box" id="guojiYunfeiBox">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">zhongliang(g)</label>
        <input type="text" class="form-control1" id="weigth" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-6 themed-grid-col">
          <label for="guojiShoudan">shoudan</label>
          <select class="custom-select d-block w-100" id="guojiShoudan">
              <option value="EMS" selected>EMS</option>
              <!--<option value="AIR">AIR</option>-->
              <option value="SAL">SAL</option>
              <option value="SEA">SEA</option>
              <option value="PINGYOU">PINGYOU</option>
          </select>
      </div>
      <div class="col-6 themed-grid-col">
	    <ul>
		  <li></li>
		</ul>
      </div
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">yunfeiJP</label>
        <input type="text" class="form-control" id="guojiYunfeiJPY" readonly>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">shoufei</label>
        <input type="text" class="form-control" id="guojiYunfeiCNY" readonly>
      </div>
    </div>
  </div>
  <div class="box" id="hejibox">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="priceJPY">riyuan(heji)</label>
        <input type="text" class="form-control" id="priceJPYHeji" readonly >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">yunfeiJP(heji)</label>
        <input type="text" class="form-control" id="yunfeiJPJPYHeji" readonly >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <label for="huilv">shoufei(heji)</label>
        <input type="text" class="form-control" id="shoufeiCNYHeji" readonly>
      </div>
    </div>
  </div>
</div>
</body>
</html>