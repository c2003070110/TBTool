<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyGiftCard.php';
?>
<html lang="ja">
<head>
<title>bid list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_MYGIFTCARD") ?>";
$(function() {
    $(document).on("click", ".actionBtn", function() {
        var thisBox = $(this).parent().parent();
        var actionName = $(this).html();

        var bidId = thisBox.find("#bidId").val();
        var obidId = thisBox.find("#obidId").val();
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action" : actionName, 
                                   "bidId" : bidId, 
                                   "obidId" : obidId},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
			//alert(msg);
            location.reload();
        });
    });
});
</script>
</head>
<body class="py-4">
<?php
  $myGiftCard = new MyGiftCard();
  $status = $_GET['status'];
  if(!empty($status)){
      $dataArr = $myGiftCard->listBidByStatus($status);
  }else{
      $dataArr = $myGiftCard->listAllBid();
  }
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	  $sort[$key] = $value['dtAdd'];
  }
  array_multisort($sort, SORT_DESC, $dataArr);
  
  if($status == 'bided'){
	  $cssBgbided = "bg-warning text-white";
  }else if($status == 'paid'){
	  $cssBgpaid = "bg-warning text-white";
  }else if($status == 'sent'){
	  $cssBgsent= "bg-warning text-white";
  }else if($status == 'fin'){
	  $cssBgfin= "bg-warning text-white";
  }else{
	  $cssBgAll = "bg-warning text-white";
  }
?>
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?> 
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgbided ?>"><a href="/myphp/mygiftcard/bidlist.php?status=bided">未付</a></li>
    <li class="list-group-item <?php echo $cssBgpaid ?>"><a href="/myphp/mygiftcard/bidlist.php?status=paid">未发</a></li>
    <li class="list-group-item <?php echo $cssBgsent ?>"><a href="/myphp/mygiftcard/bidlist.php?status=sent">未收</a></li>
    <li class="list-group-item <?php echo $cssBgfin ?>"><a href="/myphp/mygiftcard/bidlist.php?status=fin">完结</a></li>
    <!--<li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mygiftcard/bidlist.php">ALL</a></li>-->
  </ul> 
  <hr class="mb-2">   
  <div class="row">
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">bidId</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">codeType</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">Status</div>
    <div class="col-3 text-break themed-grid-col border border-primary bg-info text-white">Action</div>
  </div>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="row">
    <input type="hidden" id="uid" value="<?php echo $data["uid"] ?>">
    <input type="hidden" id="bidId" value="<?php echo $data["bidId"] ?>">
    <input type="hidden" id="obidId" value="<?php echo $data["obidId"] ?>">
    <div class="col-3 text-break themed-grid-col border border-secondary">
	  <a href="https://contact.auctions.yahoo.co.jp/seller/top?aid=<?php echo $data['bidId'] ?>" target="blank">
	    <?php echo $data["bidId"] ?>
	  </a>
    </div>
    <div class="col-3 text-break themed-grid-col border border-secondary"><?php echo $data["codeType"] ?></div>
    <div class="col-3 text-break themed-grid-col border border-secondary"><?php echo $data["status"] ?></div>
    <div class="col-3 text-break themed-grid-col border border-secondary">
<?php 
  if($data["status"] == 'bided') {
?>
      <button type="button" id="btnDelBid" class="btn btn-secondary actionBtn">DelBid</button>
      <button type="button" id="btnPaid" class="btn btn-secondary actionBtn">paid</button>
<?php 
  }else if($data["status"] == 'paid') {
?>
      <button type="button" id="btnSend" class="btn btn-secondary actionBtn">send</button>
<?php 
  }else if($data["status"] == 'sent') {
?>
      <button type="button" id="btnFinish" class="btn btn-secondary actionBtn">finishBid</button>
<?php 
  }else if($data["status"] == 'fin') {
?>
      <div class="text-break themed-grid-col"><?php echo substr($data["dtAdd"],0,8) ?></div>
<?php 
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