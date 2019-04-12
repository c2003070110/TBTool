<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ . '/MyMontb.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[ERROR]Parameter Of [action] is NULL";
	return;
}
if($actionStr == "saveOrder"){
	$my = new MyMontb();
	$rslt = $my->saveOrder();
	echo $rslt;
} else if($actionStr == "deleteOrder"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->deleteOrder($uid);
	echo $rslt;
} else if($actionStr == "updateOrder"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->updateOrder();
} else if($actionStr == "orderOrder"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->orderOrder($uid);
} else if($actionStr == "convertHanziToPY"){
	$hanzi = $_GET['hanzi'];
	if($hanzi === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->convertHanziToPY($hanzi);
	echo $rslt;
} else if($actionStr == "updateMBOrderNo"){
	if(empty($_GET['uid']) || empty($_GET['mbOrderNo'])){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->updateOrderByMBOrder($_GET['uid'],$_GET['mbOrderNo']);
} else if($actionStr == "updateTransferNo"){
	if(empty($_GET['uid'])){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->updateOrderByTranfserNo($_GET['uid'],$_GET['transferNoGuoji'],$_GET['transferNoGuonei']);
	
//***********service action**************
// listOrderByEmptyMBOrderOne input:NONE; ouput:OrderObject;
} else if($actionStr == "listOrderByEmptyMBOrderOne"){
	$my = new MyMontb();
	return $my->listOrderByEmptyMBOrderOne();
// updateMBOrderNo input:uid ; mbOrderNo; output:NONE;
	
	
} else {	
}
?>