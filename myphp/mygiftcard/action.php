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

} else if($actionStr == "get"){
	$codeType = $_GET['codeType'];
	if($codeType === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$rslt = $myGiftCard->getCodeV2($codeType);
	echo $rslt;
} else if($actionStr == "asset"){
	$codeCd = $_GET['codeCd'];
	$auctionId = $_GET['auctionId'];
	$obidId = $_GET['obidId'];
	if($codeCd === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->assetCode($codeCd, $auctionId, $obidId);
} else if($actionStr == "fin"){
	$auctionId = $_GET['auctionId'];
	$obidId = $_GET['obidId'];
	if(empty($auctionId) ||empty($obidId)){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$myGiftCard = new MyGiftCard();
	$myGiftCard->finishCode($auctionId, $obidId);
	
	
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