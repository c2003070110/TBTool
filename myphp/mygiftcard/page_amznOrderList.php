<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyGiftCard.php';
?>
<html lang="ja">
<head>
<title>amzn order list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYGIFTCARD") ?>";
$(function() {

});
</script>
</head>
<body class="py-4">
<?php
  $myGiftCard = new MyGiftCard();
  $dataArr = $myGiftCard->listAllAmznOrder();
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  $sort[$key] = $value['dtAdd'];
  }
  array_multisort($sort, SORT_DESC, $dataArr);
  
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>  
  <div class="row">
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">amt</div>
    <div class="col-2 text-break themed-grid-col border border-primary bg-info text-white">qtty</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">Status</div>
    <div class="col-5 text-break themed-grid-col border border-primary bg-info text-white">mailAddress</div>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data["uid"] ?>">
    <input type="hidden" id="bidId" value="<?php echo $data["bidId"] ?>">
    <input type="hidden" id="obidId" value="<?php echo $data["obidId"] ?>">
    <div class="col-2 text-break themed-grid-col border border-secondary">
		<a class="btn btn-primary" href="/myphp/mygiftcard/page_makeAmznOrder.php?uid=<?php echo $data['uid'] ?>">
		  <?php echo $data["amt"] ?>
		</a>
	</div>
    <div class="col-2 text-break themed-grid-col border border-secondary"><?php echo $data["qtty"] ?></div>
    <div class="col-3 text-break themed-grid-col border border-secondary"><?php echo $data["status"] ?></div>
    <div class="col-5 text-break themed-grid-col border border-secondary"><?php echo $data["mailAddress"] ?></div>
  </div>
<?php
  }
?>
</div>
</body>
</html>