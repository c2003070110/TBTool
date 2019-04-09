<?php

//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyYaBid.php';
require __DIR__ .'/../adminsupp/MyTransfee.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}

if($actionStr == "getYunfei"){
	$my = new MyTransfee();
	if(!isset($_GET["weigth"]) || !isset($_GET["guojiShoudan"])){
		return "0;0";
	}
	$transfeeGuojiJPY = $my->getGuojiYunfei($_GET["weigth"], $_GET["guojiShoudan"]);
	$transfeeGuonei = $my->getGuoneiYunfei($_GET["weigth"], $_GET["guojiShoudan"]);
	echo strval($transfeeGuojiJPY) . ':' . strval($transfeeGuonei);
} else if($actionStr == "addBuyer"){
	if(!isset($_GET["buyer"])){
		return "";
	} 
	$my = new MyYaBid();
	$rslt = $my->addBuyer($_GET["buyer"]);
	echo $rslt;
} else if($actionStr == "addMyBid"){
	if(!isset($_GET["buyer"]) || !isset($_GET["urllist"])){
		return "";
	} 
	$my = new MyYaBid();
	$rslt = $my->addMyBid($_GET["buyer"], $_GET["urllist"]);
	echo $rslt;
} else if($actionStr == "addMyBid"){
	if(!isset($_GET["buyer"]) || !isset($_GET["urllist"])){
		return "";
	} 
	$my = new MyYaBid();
	$rslt = $my->addMyBid($_GET["buyer"], $_GET["urllist"]);
	echo $rslt;
} else if($actionStr == "updateItemStatus"){
	if(!isset($_GET["uid"]) || !isset($_GET["buyer"]) || !isset($_GET["status"])){
		return "";
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemStatus($_GET["buyer"], $_GET["uid"], $_GET["status"]);
	//echo $rslt;
} else if($actionStr == "updateItemPrice"){
	if(!isset($_GET["uid"]) || !isset($_GET["buyer"])){
		return "[ERROR]PARAMETER";
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemPrice($_GET["buyer"], $_GET["uid"],
	                $_GET["priceJPY"],$_GET["transfeeDaoneiJPY"],$_GET["weight"]);
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