<?
require './mygiftcard/MyGiftCard.php';
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
	$rslt = $myGiftCard->getCode($codeType);
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
}
?>