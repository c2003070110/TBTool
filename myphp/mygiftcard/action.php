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

// **** service action
} else if($actionStr == "bided"){
	$bidId = $_GET['bidId'];
	$obidId = $_GET['obidId'];
	if(empty($bidId) || empty($obidId)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->addBid($bidId, $obidId);
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
	$rslt = $myGiftCard->getAplyBidMsgOne();// BidObject
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
} else if($actionStr == "US" || $actionStr == "RE" || $actionStr == "INV"){
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
	}
	echo $rslt;
	
}
?>