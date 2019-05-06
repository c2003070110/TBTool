<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ . '/MyGiftCard.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}
if($actionStr == "save"){
	$paramStr = $_GET['paramstr'];
	if($paramStr === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->saveCode($paramStr);
	echo $rslt;
} else if($actionStr == "saveAmznOrder"){
	$uid = $_GET['uid'];
	$qtty = $_GET['qtty'];
	$amt = $_GET['amt'];
	$payway = $_GET['payway'];
	$mailAddress = $_GET['mailAddress'];
	if(empty($qtty) || empty($amt)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->saveAmznOrder($uid, $amt, $qtty, $payway, $mailAddress);
	echo $rslt;
} else if($actionStr == "updateAmznOrderStatus"){
	$uid = $_GET['uid'];
	$tostatus = $_GET['status'];
	if(empty($uid) || empty($tostatus)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->updateAmznOrderStatus($uid, $tostatus);
	echo $rslt;

// **** service action
} else if($actionStr == "addCode"){
	$orderNo = $_GET['orderNo'];
	$codeType = $_GET['codeType'];
	$codeCd = $_GET['codeCd'];
	if(empty($codeType) || empty($codeCd)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->addCode($orderNo, $codeType, $codeCd);
	echo $rslt;
} else if($actionStr == "bided"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	$codeType = $_GET['codeType'];
	if(empty($bidId) || empty($obidId)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->addBid($bidId, $obidId,$codeType);
} else if($actionStr == "get"){
	$codeType = $_GET['codeType'];
	if($codeType === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->getCodeV2($codeType, $bidId, $obidId);
	echo $rslt;
} else if($actionStr == "asset"){
	$codeCd = $_GET['codeCd'];
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	if($codeCd === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->assetCode($codeCd, $bidId, $obidId);
} else if($actionStr == "stockCheck"){
	$codeType = $_GET['codeType'];
	if(empty($codeType)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->stockCheck($codeType);
	echo $rslt; // true/false
} else if($actionStr == "fin"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	if(empty($bidId) || empty($obidId)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->finishCode($bidId, $obidId);
	
} else if($actionStr == "addBidMsg"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	$msg = $_GET['msg'];
	if(empty($bidId) || empty($obidId) || empty($msg)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->addBidMsg($bidId, $obidId, $msg);
} else if($actionStr == "getAplyBidMsgOne"){
	$myGiftCard = new MyGiftCard();
	$data = $myGiftCard->getAplyBidMsgOne();// BidObject
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($data);
	}
} else if($actionStr == "updateBidMsgStatus"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	$status = $_GET['status'];
	if(empty($bidId) || empty($obidId)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->updateBidMsgStatus($bidId, $obidId, $status);
} else if($actionStr == "updateBidMsgReply"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	$msg = $_GET['msg'];
	if(empty($bidId) || empty($obidId) || empty($msg)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->updateBidMsgReply($bidId, $obidId, $msg);
	
} else if($actionStr == "getAmazonNoticeForAddCode"){
	$myGiftCard = new MyGiftCard();
	$data = $myGiftCard->getAmznOrderUnorderedOne();
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($data);
	}
} else if($actionStr == "finishAmazonNoticeForAddCode"){
	$uid = $_GET['uid'];
	$mailAddress = $_GET['mailAddress'];
	if(empty($uid) || empty($mailAddress)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->finishAmazonNoticeForAddCode($uid, $mailAddress);
	
// **** service end
	
} else if($actionStr == "DelBid" || $actionStr == "paid" || $actionStr == "send" || $actionStr == "finishBid"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	if(empty($bidId) || empty($obidId)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	if($actionStr == "DelBid"){
		$rslt = $myGiftCard->deleteBid($bidId, $obidId);
	}else if ($actionStr == "paid"){
		$rslt = $myGiftCard->updateBidStatus($bidId, $obidId, 'paid');
	}else if ($actionStr == "send"){
		$rslt = $myGiftCard->updateBidStatus($bidId, $obidId, 'sent');
	}else if ($actionStr == "finishBid"){
		$rslt = $myGiftCard->updateBidStatus($bidId, $obidId, 'fin');
	}
	
} else if($actionStr == "DEL"){
	$codeCd = $_GET['codeCd'];
	if($codeCd === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->deleteCode($codeCd);
	echo $rslt;
} else if($actionStr == "US" || $actionStr == "RE" || $actionStr == "INV" || $actionStr == "FIN"){
	$codeCd = $_GET['codeCd'];
	if($codeCd === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	if($actionStr == "US"){
		$rslt = $myGiftCard->updateStatus($codeCd, 'used');
	}else if ($actionStr == "RE"){
		$rslt = $myGiftCard->updateStatus($codeCd, 'unused');
	}else if ($actionStr == "INV"){
		$rslt = $myGiftCard->updateStatus($codeCd, 'invalid');
	}else if ($actionStr == "FIN"){
		$rslt = $myGiftCard->updateStatus($codeCd, 'fin');
	}
	echo $rslt;
	
}
?>