<?php

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

require __DIR__ .'/MyDaiGou.php';
//require __DIR__ .'/ItemObject.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}

if($actionStr == "saveBuyer"){
	$my = new MyDaiGou();
	$obj = new BuyerObject();
	$obj->uid = $_GET['uid'];
	$obj->buyer = $_GET['buyer'];
	$obj->address = $_GET['address'];
	$rslt = $my->saveBuyer($obj);
	echo $rslt;
} else if($actionStr == "listByBuyer"){
	$my = new MyDaiGou();
	$rslt = $my->listByBuyer($obj->buyer);
	echo $rslt;
} else if($actionStr == "saveItem"){
	$my = new MyDaiGou();
	$obj = new ItemObject();
	$obj->uid = $_GET['uid'];
	$obj->buyer = $_GET['buyer'];
	$obj->orderDate = $_GET['orderDate'];
	$obj->orderItem = $_GET['orderItem'];
	$obj->priceJPY = $_GET['priceJPY'];
	$obj->qtty = $_GET['qtty'];
	$obj->priceCNY = $_GET['priceCNY'];
	$obj->status = 'unGou';
	$rslt = $my->saveItem($obj);
	echo $rslt;
} else if($actionStr == "deleteItem"){
	$my = new MyDaiGou();
	$obj = new ItemObject();
	$obj->uid = $_GET['uid'];
	$rslt = $my->deleteItem($obj);
	echo $rslt;
} else if($actionStr == "assign"){
	$my = new MyDaiGou();
	$obj = new ItemObject();
	$obj->uid = $_GET['uid'];
	$obj->buyer = $_GET['buyer'];
	$obj->orderDate = $_GET['orderDate'];
	$obj->orderItem = $_GET['orderItem'];
	$obj->priceJPY = $_GET['priceJPY'];
	$obj->qtty = $_GET['qtty'];
	$obj->priceCNY = $_GET['priceCNY'];
	$obj->status = 'unGou';
	$rslt = $my->saveItem($obj);
	echo $rslt;
} else if($actionStr == "gouru"){
	$my = new MyDaiGou();
	$obj = new ItemObject();
	$obj->uid = $_GET['uid'];
	$obj->buyer = $_GET['buyer'];
	$obj->orderDate = $_GET['orderDate'];
	$obj->orderItem = $_GET['orderItem'];
	$obj->priceJPY = $_GET['priceJPY'];
	$obj->qtty = $_GET['qtty'];
	$obj->priceCNY = $_GET['priceCNY'];
	$obj->status = 'gouru';
	$rslt = $my->saveItem($obj);
	echo $rslt;
} else if($actionStr == "zaitu" || $actionStr == "fahuo" || $actionStr == "compl" ){
	$my = new MyDaiGou();
	$obj = new ItemObject();
	$obj->uid = $_GET['uid'];
	$obj->status = $actionStr;
	$rslt = $my->updateItemStatus($obj);
	echo $rslt;
}
?>