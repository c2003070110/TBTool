<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyGiftCard.php';
?>
<html lang="ja">
<head>
<title>my msg list</title>
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
var actionUrl = "<?php echo constant("URL_ACTION_MYGIFTCARD") ?>";
$(function() {
	var getMyBox = function(thisElement){
		return $(thisElement).parent().parent().parent();
	}
	var updateStatus = function(thisBox, status){
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateTBOrderStatus", 
					   "uid" : thisBox.find("#uid").val(),
					   "status" : status
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
	};
    $(document).on("click", "#btnGetMsgTemplate1", function() {
		var thisBox = getMyBox(this);
		var msg = "ご連絡、ご評価ありがとうございます。また、機会があれば、その時もよろしくお願いします。";
		$(thisBox).find("#replymsg").val(msg);
    });
    $(document).on("click", "#btnGetMsgTemplate2", function() {
		var thisBox = getMyBox(this);
		var msg = "承知いたします。お待ちております。";
		$(thisBox).find("#replymsg").val(msg);
    });
    $(document).on("click", "#btnReply", function() {
		var thisBox = getMyBox(this);
		var bidId = $(thisBox).find("#bidId").val();
		var obidId = $(thisBox).find("#obidId").val();
		var replymsg = $(thisBox).find("#replymsg").val();
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateBidMsgReply", 
					   "bidId" : bidId,
					   "obidId" : obidId,
					   "msg" : replymsg
			   },
			   dataType : "html" 
			  }
		  );
        jqxhr.done(function( msg ) {
            location.reload();
        });
    });
    $(document).on("click", "#btnIgnore", function() {
		var thisBox = getMyBox(this);
		var bidId = $(thisBox).find("#bidId").val();
		var obidId = $(thisBox).find("#obidId").val();
        var jqxhr = $.ajax(actionUrl,
			 { type : "GET",
			   data : {"action":"updateBidMsgStatus", 
					   "bidId" : bidId,
					   "obidId" : obidId,
					   "status" : "ignore"
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
  include __DIR__ .'/subpage_toplink.php';
  $status = $_GET['status'];
  $my = new MyGiftCard();
  if($status == 'wait'){
	  $cssBgwait = "bg-warning text-white";
  }else if($status == 'ignore'){
	  $cssBgignore = "bg-warning text-white";
  }else if($status == 'aplied'){
	  $cssBgaplied= "bg-warning text-white";
  }else if($status == 'sent'){
	  $cssBgsent= "bg-warning text-white";
  }else{
	  $cssBgAll = "bg-warning text-white";
  }
?>
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item <?php echo $cssBgwait ?>"><a href="/myphp/mygiftcard/msglist.php?status=wait">待处理</a></li>
    <li class="list-group-item <?php echo $cssBgignore ?>"><a href="/myphp/mygiftcard/msglist.php?status=ignore">不理</a></li>
    <li class="list-group-item <?php echo $cssBgaplied ?>"><a href="/myphp/mygiftcard/msglist.php?status=aplied">回复</a></li>
    <li class="list-group-item <?php echo $cssBgsent ?>"><a href="/myphp/mygiftcard/msglist.php?status=sent">发送</a></li>
    <li class="list-group-item <?php echo $cssBgAll ?>"><a href="/myphp/mygiftcard/msglist.php">ALL</a></li>
  </ul>
  <hr class="mb-4">
<?php
  $dataArr = array();
  if (!empty($status)){
	  $dataArr = $my->listBidByMsgStatus($status);
  }else{
	  $dataArr = $my->listBidByMsgStatus("");
  }
  //var_dump($status);
  $sort = array();
  foreach ((array) $dataArr as $key => $value) {
	$sort[$key] = $value['dtMsg'];
  }
  array_multisort($sort, SORT_DESC, $dataArr);
?>
<?php
  foreach ($dataArr as $data) {
?>
  <div class="box border border-primary mb-1">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <input type="hidden" id="bidId" value="<?php echo $data['bidId'] ?>">
    <input type="hidden" id="obidId" value="<?php echo $data['obidId'] ?>">
    <div class="row mb-4 form-group">
      <div class="col-6">
	    <label for="maijia">bidId</label>
	    <a class="form-control btn btn-primary" href="https://contact.auctions.yahoo.co.jp/seller/top?aid=<?php echo $data['bidId'] ?>" target="blank">
	      <?php echo $data["bidId"] ?>
	    </a>
	  </div>
      <div class="col-6">
	    <label for="obidIdTxt">obidId</label>
	    <input type="text" class="form-control" id="obidIdTxt" value="<?php echo $data['obidId'] ?>" readOnly>
	  </div>
    </div>
    <div class="row mb-4 form-group">
      <div class="col-12">
	    <label for="msgBid">msgBid</label>
		<textarea class="form-control" cols="40" rows="4" id="msgBid"><?php echo $data['msg'] ?></textarea >
	  </div>
    </div>
<?php
    if($data["msgStatus"] == "wait" || $data["msgStatus"] == "ignore"){
?>
    <div class="row mb-4 form-group">
      <div class="col-3">
	    <button type="button" id="btnGetMsgTemplate1" class="btn btn-secondary">get tempate1</button>
	  </div>
      <div class="col-3">
	    <button type="button" id="btnGetMsgTemplate2" class="btn btn-secondary">get tempate2</button>
	  </div>
      <div class="col-3">
	    <button type="button" id="btnIgnore" class="btn btn-secondary">ignore</button>
	  </div>
    </div>
<?php
    }
?>
    <div class="row mb-4 form-group">
      <div class="col-12">
	    <label for="replymsg">reply msg</label>
		<textarea class="form-control" cols="40" rows="4" id="replymsg"><?php echo $data['replymsg'] ?></textarea >
	  </div>
    </div>
<?php
    if($data["msgStatus"] == "wait" || $data["msgStatus"] == "ignore"){
?>
    <div class="row mb-4 form-group">
      <div class="col-8">
	    <button type="button" id="btnReply" class="btn btn-secondary">reply</button>
	  </div>
    </div>
<?php
    }
?>
  </div>
<?php
  }
?>
</div>
</body>
</html>