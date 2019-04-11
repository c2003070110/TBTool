<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyYaBid.php';

  $buyer = $_GET["buyer"];
  $admin = $_GET["admin"];
  $status = $_GET["status"];
?>
<html lang="ja">
<head>
<title>my bid</title>
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
  if($buyer != ''){
?>
  <h3>买家:<span><?php echo $buyer ?></span></h3>
  <hr class="mb-4">
<?php
  }
  if (!empty($buyer)){
	  $dataArr = $my->listParcelByBuyer($buyer);
  }else if($isAdmin && !empty($buyer) && !empty($status)){
	  $dataArr = $my->listParcelByBuyerAndStatus($buyer, $status);
  }else if($isAdmin){
	  $dataArr = $my->listParcelByAll();
  } else{
	  exit(0);
  }
?>
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">transnoGuoji</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">transnoGuonei</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">状态</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">action</div>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row <?php echo $boxCss ?>">
    <input type="hidden" id="buyer" value="<?php echo $data['buyer'] ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <a href="<?php echo $data['itemUrl'] ?>" target="blank">
	    <?php echo $data['transnoGuoji'] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <a href="<?php echo $data['transnoGuonei'] ?>" target="blank">
	    <?php echo $data['transnoGuonei'] ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <a href="/myphp/myyabid/page_myParcel.php?buyer=<?php echo $data['buyer'] ?>&uid=<?php echo $data['uid'] ?>&admin=<?php echo $admin ?>">
	    <?php echo $my->getParcelStatusName($data["status"]) ?>
	  </a>
	</div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
<?php
	if($data["status"] =='zaiTu' || $data["status"] =='guonneiFh' ) {
?>
      <button class="btn btn-secondary actionBtn" id="btnShouhuo" type="button">shouhuo</button>
<?php
		if($isAdmin && $data["guojiShoudan"] =="pinyou"){
?>
	  <a href="/myphp/myyabid/page_myParcel.php?buyer=<?php echo $data['buyer'] ?>&uid=<?php echo $data['uid'] ?>&admin=<?php echo $admin ?>">
	    guoneifahuo
	  </a>
<?php
		}
	}
?>
	</div>
  </div>
<?php
  }
?>
</div>
</body>
</html>