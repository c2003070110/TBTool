<?php

//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require_once __DIR__ .'/MyYaBid.php';
require_once __DIR__ .'/../adminsupp/MyTransfee.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}

if($actionStr == "getYunfei"){
	$my = new MyTransfee();
	if(empty($_GET["weigth"]) || empty($_GET["guojiShoudan"])){
		return "0;0";
	}
	$transfeeGuojiJPY = $my->getGuojiYunfei($_GET["weigth"], $_GET["guojiShoudan"]);
	$transfeeGuonei = $my->getGuoneiYunfei($_GET["weigth"], $_GET["guojiShoudan"]);
	if(!empty($_GET["buyer"]) && !empty($_GET["myparcelUid"]) ){
		$my = new MyYaBid();
		$my->updateParcelByYunfei($_GET["buyer"], $_GET["myparcelUid"], $_GET["guojiShoudan"],$transfeeGuojiJPY, $transfeeGuonei);
	}
	echo strval($transfeeGuojiJPY) . ':' . strval($transfeeGuonei);
} else if($actionStr == "addBuyer"){
	if(empty($_GET["buyer"])){
		return ;
	} 
	$my = new MyYaBid();
	$rslt = $my->addBuyer($_GET["buyer"]);
	echo $rslt;
} else if($actionStr == "addMyBid"){
	if(empty($_GET["buyer"]) || empty($_GET["urllist"])){
		return ;
	} 
	$my = new MyYaBid();
	$rslt = $my->addMyBid($_GET["buyer"], $_GET["urllist"]);
	echo $rslt;
} else if($actionStr == "addMyBid"){
	if(empty($_GET["buyer"]) || empty($_GET["urllist"])){
		return ;
	} 
	$my = new MyYaBid();
	$rslt = $my->addMyBid($_GET["buyer"], $_GET["urllist"]);
	echo $rslt;
} else if($actionStr == "updateItemStatus"){
	if(empty($_GET["uid"]) || empty($_GET["buyer"]) || empty($_GET["status"])){
		return ;
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemStatus($_GET["buyer"], $_GET["uid"], $_GET["status"]);
	//echo $rslt;
} else if($actionStr == "updateItemPrice"){
	if(empty($_GET["uid"]) || empty($_GET["buyer"])){
		return "[ERROR]PARAMETER";
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemPrice($_GET["buyer"], $_GET["uid"],
	                $_GET["priceJPY"],$_GET["transfeeDaoneiJPY"],$_GET["weight"]);
} else if($actionStr == "addTaobaoDingdan"){
	if(empty($_GET["myparcelUid"]) || empty($_GET["buyer"])
		 || empty($_GET["taobaoDingdanCNY"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$rslt = $my->addTaobaoDingdan($_GET["buyer"], $_GET["myparcelUid"],
	                $_GET["taobaoDingdanhao"],$_GET["taobaoDingdanCNY"]);
} else if($actionStr == "deleteTaobaoDingdan"){
	if(empty($_GET["myparcelUid"]) || empty($_GET["buyer"])
		 || empty($_GET["taobaodingdanUid"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$rslt = $my->deleteTaobaoDingdan($_GET["buyer"], $_GET["myparcelUid"],$_GET["taobaodingdanUid"]);
	//echo $rslt;
	
	
	
	
	
} else if($actionStr == "listByBuyer"){
	$my = new MyDaiGou();
	$rslt = $my->listByBuyer($obj->buyer);
	echo $rslt;
} else{
	var_dump($actionStr);
	return "[ERROR]PARAMETER";
}
?>