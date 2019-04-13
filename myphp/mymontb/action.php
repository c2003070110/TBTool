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
if($actionStr == "saveTBOrder"){
	$my = new MyMontb();
	$rslt = $my->saveTBOrder();
	echo $rslt;
} else if($actionStr == "updateTBOrderStatus"){
	if(empty($_GET["uid"]) || empty($_GET["buyer"]) || empty($_GET["status"])){
		return ;
	} 
	$my = new MyYaBid();
	$rslt = $my->updateTBOrderStatus($_GET["uid"], $_GET["status"]);
} else if($actionStr == "updateMBOrder"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->updateMBOrder();
} else if($actionStr == "orderMBOrder"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->orderMBOrder($uid);
} else if($actionStr == "updateMBOrderNo"){
	if(empty($_GET['uid']) || empty($_GET['mbOrderNo'])){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->updateMBOrderByMBOrder($_GET['uid'],$_GET['mbOrderNo']);
} else if($actionStr == "updateTransferNo"){
	if(empty($_GET['uid'])){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->updateMBOrderByTranfserNo($_GET['uid'],$_GET['transferNoGuoji'],$_GET['transferNoGuonei']);



} else if($actionStr == "convertHanziToPY"){
	$hanzi = $_GET['hanzi'];
	if($hanzi === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->convertHanziToPY($hanzi);
	echo $rslt;
	
//***********service action**************
// listOrderByEmptyMBOrderOne input:NONE; ouput:OrderObject;
} else if($actionStr == "listOrderByEmptyMBOrderOne"){
	$my = new MyMontb();
	return $my->listOrderByEmptyMBOrderOne();
// updateMBOrderNo input:uid ; mbOrderNo; output:NONE;
	
	
} else {	
}
?>