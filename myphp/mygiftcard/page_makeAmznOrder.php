<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ . '/MyGiftCard.php';
?>
<html lang="ja">
<head>
<title>my gift card</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYGIFTCARD") ?>";
$(function() {
    $(document).on("click", "#btnSave", function() {
        var amt = $("#amt").val();
        var qtty = $("#qtty").val();
        if(amt == "" || qtty == ''){
			alert("amt or qtty is NULL!");
            return;
        }
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":"saveAmznOrder",
									"uid" : $("#uid").val(), 
									"payway" : $("#payway").val(),
									"amt" : amt, 
									"qtty" : qtty
									},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			if(msg.indexOf("ERROR") == -1){
				var href = window.location.href;
				if(href.indexOf("uid") == -1){
					var url = href +"?uid="+msg;
					window.location.href = url;
				}else{
					location.reload();
				}
			}else{
				alert(msg);
			}
        });
    });
	var updateStatus = function(status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateAmznOrderStatus", 
					   "uid" : $("#uid").val(),
					   "status" : status
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
	};
    $(document).on("click", "#btnDel", function() {
        updateStatus("del");
    });
    $(document).on("click", "#btnOrdered", function() {
        updateStatus("ordered");
    });
    $(document).on("click", "#btnUnorder", function() {
        updateStatus("unorder");
    });
    $(document).on("click", "#btnFin", function() {
        updateStatus("fin");
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
  $uid = empty($_GET['uid'])? "" : $_GET['uid'];
  if(!empty($uid)){
	$my = new MyGiftCard();
	$obj = $my->listAmznOrderByUid($uid);
  }
?>
  <input type="hidden" id="uid" value="<?php echo $uid ?>">
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
		  <label for="amt">amt</label>
		  <input type="text" class="form-control" id="amt" value="<?php echo $obj['amt'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
		  <label for="qtty">qtty</label>
		  <input type="text" class="form-control" id="qtty" value="<?php echo $obj['qtty'] ?>"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10 themed-grid-col">
            <label for="payway">payway</label>
            <select class="custom-select d-block w-100" id="payway">
                <option value="" selected></option>
                <option value="XXX" <?php if($obj['payway']=='XXX'){?> selected <?php } ?>>XXX</option>
            </select>
        </div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
		  <label for="mailAddress">mailAddress</label>
		  <input type="text" class="form-control" id="mailAddress" value="<?php echo $obj['mailAddress'] ?>" readonly></div>
      </div>
<?php 
    if(empty($uid) || $obj["status"] == 'unorder') {
?>
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
            <button type="button" id="btnSave" class="btn btn-primary">save</button>
        </div>
      </div>
<?php 
    }
?>
<?php
  if(!empty($uid)){
?>
      <hr class="mb-4">
      <div class="row mb-4 form-group">
        <div class="col-12 themed-grid-col">
<?php 
    if($obj["status"] == 'unorder') {
?>
          <button type="button" id="btnDel" class="btn btn-secondary">DEL</button>
          <button type="button" id="btnOrdered" class="btn btn-primary">ORDERED</button>
<?php 
    }else if($obj["status"] == 'ordered') {
?>
          <button type="button" id="btnUnorder" class="btn btn-primary">UNORDER</button>
          <button type="button" id="btnFin" class="btn btn-secondary">FIN</button>
<?php 
    }
?>
        </div>
      </div>
<?php 
  }
?>
  </div>
</div>
</body>
</html>