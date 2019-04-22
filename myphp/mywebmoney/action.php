<?php
/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/
require_once __DIR__ .'/MyWebMoney.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}

//******helper********
if($actionStr == "getHuilv"){
	$my = new MyWebMoney();
	$data = $my->getHuilv();
	echo intval($data * 1000); // 1000JPY=xxCNY
	
	
//*****daichong******	
} else if($actionStr == "addDaiChong"){
	$uid = $_GET["uid"];
	$url = $_GET["url"];
	$amtJPY = $_GET["amtJPY"];
	$tbBuyer = $_GET["tbBuyer"];
	$payway = $_GET["payway"];
	
	if(empty($_GET["url"]) || empty($_GET["amtJPY"]) || empty($_GET["payway"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyWebMoney();
	$uid = $my->addDaiChong($uid, $url, $amtJPY, $tbBuyer, $payway);
	echo $uid;
} else if($actionStr == "updateDaichongStatus"){
	$uid = $_GET["uid"];
	$status = $_GET["status"];
	
	if(empty($_GET["uid"]) || empty($_GET["status"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyWebMoney();
	$my->updateDaichongStatus($uid, $status);
				   
//*****service action start******	
} else if($actionStr == "getLastestNoticeOne"){
	$my = new MyWebMoney();
	$data = $my->getLastestNoticeOne();//WebMoneyObject
	$data["realShopComment"] = "";
	$data["realItemName"] = "";
	$data["payResult"] = "";
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($data);
	}
} else if($actionStr == "updateCheckResult"){
	$uid = $_GET["uid"];
	$shopComment = $_GET["shopComment"];
	$itemInfo = $_GET["itemInfo"];//itemName:amtTtl;...
	
	if(empty($_GET["uid"]) || empty($_GET["itemInfo"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyWebMoney();
	$my->updateCheckResult($uid, $shopComment, $itemInfo);
} else if($actionStr == "getCheckedNoticeOne"){
	$my = new MyWebMoney();
	$data = $my->getCheckedNoticeOne();//WebMoneyObject
	$data["realShopComment"] = "";
	$data["realItemName"] = "";
	$data["payResult"] = "";
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($data);
	}
} else if($actionStr == "updatePayResult"){
	$uid = $_GET["uid"];
	$payResult = $_GET["payResult"];
	
	if(empty($uid) || empty($payResult)){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyWebMoney();
	$my->updatePayResult($uid, $payResult);
	
//*****service action end******	
	
} else{
	var_dump($actionStr);
	return "[ERROR]PARAMETER";
}
?>