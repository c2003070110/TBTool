<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
  require __DIR__ .'/MyYaBid.php';
  require __DIR__ .'/../adminsupp/MyHuilv.php';
  $buyer = $_GET["buyer"];
  $admin = $_GET["admin"];
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

<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYYABID") ?>";
$(function() {
    $(document).on("change", ".form-control", function() {
		var huilv = parseFloat($("#myhuilv").val());
		var mydaigoufei = parseInt($("#mydaigoufei").val());
		
		var itemBoxes = $(".itembox");
		var itemTtlJPY=0, itemTransfeeDaoneiTtlJPY=0, itemTtlCNY=0;
		for(var i=0; i<itemBoxes.length; i++){
			var priceJPY = parseInt($(itemBoxes[i]).find("#priceJPY").val());
			itemTtlJPY = itemTtlJPY + priceJPY;
			
			var transfeeDaoneiJPY = parseInt($(itemBoxes[i]).find("#transfeeDaoneiJPY").val());
			itemTransfeeDaoneiTtlJPY = itemTransfeeDaoneiTtlJPY + transfeeDaoneiJPY;
			
			var priceCNY = Math.ceil((priceJPY + transfeeDaoneiJPY) * huilv) + mydaigoufei
			$(itemBoxes[i]).find("#priceCNY").val(priceCNY);
			itemTtlCNY = itemTtlCNY + priceCNY;
		}
		
        $("#itemTtlJPY").val(itemTtlJPY);
        $("#itemTransfeeDaoneiTtlJPY").val(itemTransfeeDaoneiTtlJPY);
        $("#itemTtlCNY").val(itemTtlCNY);
		
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"getYunfei", 
					   "weigth" : $("#weigth").val(),
					   "guojiShoudan" : $("#guojiShoudan").val()
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            var vals = msg.split(":");
			var transfeeGuojiJPY = parseInt(vals[0]);
			var transfeeGuonei = parseInt(vals[1]);
			
			$("#transfeeGuojiJPY").val(transfeeGuojiJPY);
			
			var transfeeGuojiCNY = Math.ceil(transfeeGuojiJPY * huilv);
			$("#transfeeGuojiCNY").val(transfeeGuojiCNY);
			
			$("#transfeeGuonei").val(transfeeGuonei);
			
			$("#ttlJPY").val(itemTtlJPY + itemTransfeeDaoneiTtlJPY + transfeeGuojiJPY);
			$("#ttlCNY").val(itemTtlCNY + transfeeGuojiCNY + transfeeGuonei);
        });
    });
    $(document).on("click", "#btnAddItem", function() {
        var thisBox = $("#itemRow");
        var cloneBox = thisBox.clone();
		$(cloneBox).find("input").val(0);
        $(thisBox).after(cloneBox);
    });
});
</script>
</head>
<body class="py-4">
<div id="container" class="container">
<?php
  $my = new MyYaBid();
  $isAdmin = $my->isAdmin($admin);
  include __DIR__ .'/subpage_toplink.php';
  if($buyer == ""){
	  exit(0);
  }
  $myhuilv = new MyHuilv();
  $huilv = $myhuilv->listByHuilvDiv("YA");
  
  $mydaigoufei = $my->getDaigoufei();
?>
  <input type="hidden" id="buyer" value="<?php echo $buyer ?>">
  <input type="hidden" id="myhuilv" value="<?php echo $huilv ?>">
  <input type="hidden" id="mydaigoufei" value="<?php echo $mydaigoufei ?>">
  <div class="box">
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <button type="button" id="btnAddItem" class="btn btn-secondary actionBtn">add another item</button>
      </div>
    </div>
  </div>
  <hr class="mb-4">
  <div class="box itembox" id="itemRow">
    <div class="row mb-4 form-group">
      <div class="col-4 themed-grid-col">
        <label for="priceJPY">priceJPY</label>
        <input type="text" class="form-control" id="priceJPY" >
      </div>
      <div class="col-4 themed-grid-col">
        <label for="transfeeDaoneiJPY">transfeeDaoneiJPY</label>
        <input type="text" class="form-control" id="transfeeDaoneiJPY" >
      </div>
      <div class="col-4 themed-grid-col">
        <label for="priceCNY">priceCNY</label>
        <input type="text" class="form-control" id="priceCNY" readonly>
      </div>
    </div>
  </div>
  <h3>item xiao ji</h3>
  <hr class="mb-1">
  <div class="box" id="itemhejibox">
    <div class="row mb-4 form-group">
      <div class="col-4 themed-grid-col">
        <label for="itemTtlJPY">itemTtlJPY</label>
        <input type="text" class="form-control" id="itemTtlJPY" readonly >
      </div>
      <div class="col-4 themed-grid-col">
        <label for="itemTransfeeDaoneiTtlJPY">itemTransfeeDaoneiTtlJPY</label>
        <input type="text" class="form-control" id="itemTransfeeDaoneiTtlJPY" readonly >
      </div>
      <div class="col-4 themed-grid-col">
        <label for="itemTtlCNY">itemTtlCNY</label>
        <input type="text" class="form-control" id="itemTtlCNY" readonly>
      </div>
    </div>
  </div>
  <h3>guoji yunfei</h3>
  <hr class="mb-1">
  <div class="box" id="guojiYunfeiBox">
    <div class="row mb-4 form-group">
      <div class="col-6 themed-grid-col">
        <label for="priceJPY">zhongliang(g)</label>
        <input type="text" class="form-control" id="weigth" >
      </div>
      <div class="col-6 themed-grid-col">
          <label for="guojiShoudan">shoudan</label>
          <select class="custom-select d-block w-100 form-control" id="guojiShoudan">
              <option value="EMS" selected>EMS</option>
              <!--<option value="AIR">AIR</option>-->
              <option value="SAL">SAL</option>
              <option value="SEA">SEA</option>
              <option value="PINGYOU">PINGYOU</option>
          </select>
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-4 themed-grid-col">
        <label for="transfeeGuojiJPY">transfeeGuojiJPY</label>
        <input type="text" class="form-control" id="transfeeGuojiJPY" readonly>
      </div>
      <div class="col-4 themed-grid-col">
        <label for="transfeeGuojiCNY">transfeeGuojiCNY</label>
        <input type="text" class="form-control" id="transfeeGuojiCNY" readonly>
      </div>
      <div class="col-4 themed-grid-col">
        <label for="transfeeGuonei">transfeeGuonei</label>
        <input type="text" class="form-control" id="transfeeGuonei" readonly>
      </div>
    </div>
  </div>
  <h3>zongji</h3>
  <hr class="mb-1">
  <div class="box" id="hejibox">
    <div class="row mb-4 form-group">
      <div class="col-6 themed-grid-col">
        <label for="ttlJPY">ttlJPY</label>
        <input type="text" class="form-control" id="ttlJPY" readonly >
      </div>
      <div class="col-6 themed-grid-col">
        <label for="ttlCNY">ttlCNY</label>
        <input type="text" class="form-control" id="ttlCNY" readonly>
      </div>
    </div>
  </div>
  <div class="box">
    <div class="row mb-4 form-group">
      <div class="col-12 themed-grid-col">
	    <a href="/myphp/myyabid/page_addMyBid.php?buyer=<?php echo $buyer ?>">
	      add bid
	    </a>
      </div>
    </div>
</div>
</body>
</html>