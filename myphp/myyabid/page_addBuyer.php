<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

  require __DIR__ .'/MyYaBid.php';
  $admin = $_GET["admin"];
?>
<html lang="ja">
<head>
<title>add buyer</title>
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
    $(document).on("click", "#btnAdd", function() {
		
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"addBuyer", 
					   "buyer" : $("#buyer").val()
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
  $my = new MyYaBid();
  $isAdmin = $my->isAdmin($admin);
  include __DIR__ .'/subpage_toplink.php';
  if($admin != '' && !$isAdmin){
	  exit(0);
  }
?>
  <div class="box itembox">
    <div class="row mb-2 form-group">
      <div class="col-12 themed-grid-col">
        <label for="buyer">buyer</label>
        <input type="text" class="form-control" id="buyer" >
      </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-10 themed-grid-col">
        <button class="btn btn-secondary" id="btnAdd" type="button">add</button>
      </div>
    </div>
  </div>
<?php
  $dataArr = $my->listAllBuyer();
  foreach ($dataArr as $data) {
	  $host = (empty($_SERVER["HTTPS"]) ? "http://" : "https://") . $_SERVER["HTTP_HOST"];
	  $url = $host . "/myphp/myyabid/page_myBidList.php?buyer=".$data["buyer"];
	  $urlAdmin = $host . "/myphp/myyabid/page_myBidList-admin.php?buyer=".$data["buyer"]."&admin=".$my->getAdminIdentifier();
?>
  <div class="row">
    <div class="col-4 text-break themed-grid-col border border-primary"><?php echo $data["buyer"] ?></div>
    <div class="col-4 text-break themed-grid-col border border-primary">
	  <a href="<?php echo $url ?>">bidList(Buyer)</a>
	</div>
    <div class="col-4 text-break themed-grid-col border border-primary">
	  <a href="<?php echo $urlAdmin ?>">bidList(admin)</a>
	</div>
  </div>
<?php
  }
?>
</div>
</body>
</html>