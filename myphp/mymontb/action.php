<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ . '/MyMontb.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}
if($actionStr == "saveOrder"){
	$my = new MyMontb();
	$rslt = $myGiftCard->saveOrder();
	echo $rslt;
} else if($actionStr == "orderOrder"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->orderOrder();
	echo $rslt;
} else if($actionStr == "convertHanziToPY"){
	$hanzi = $_GET['hanzi'];
	if($hanzi === null){
		echo "[Fatal]Parameter is NULL";
		return;
	}
	$rslt = $my->convertHanziToPY($hanzi);
	echo $rslt;
} else {	
}
?>