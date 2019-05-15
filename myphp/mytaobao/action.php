<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require_once __DIR__ .'/MyTaobao.php';
$actionStr = $_GET['action'];

if(empty($actionStr)){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}
if($actionStr == "addTaobaoFahuo"){
	$uid = $_GET["uid"];
	$orderNo = $_GET["orderNo"];
	$trackTraceNo = $_GET["trackTraceNo"];
	$tranferProviderName = $_GET["tranferProviderName"];
	if(empty($orderNo)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyTaobao();
	$rslt = $my->addTaobaoFahuo($uid, $orderNo, $trackTraceNo, $tranferProviderName);
	echo $rslt;
} else if($actionStr == "updateFahuoStatus"){
	$uid = $_GET["uid"];
	$status = $_GET["toStatus"];
	if(empty($uid) || empty($status)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyTaobao();
	$rslt = $my->updateFahuoStatus($uid, $status);
} else if($actionStr == "updateOrderStatus"){
	$uid = $_GET["uid"];
	$status = $_GET["toStatus"];
	if(empty($uid) || empty($status)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyTaobao();
	$rslt = $my->updateTaobaoOrderStatus($uid, $status);


//*****service action******	
} else if($actionStr == "listFahuoOne"){
	$my = new MyTaobao();
	$rslt = $my->listFahuoOne();//TaobaoFahuoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "isRegistedOrderNo"){
	$orderNo = $_GET["orderNo"];
	if(empty($orderNo)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyTaobao();
	$rslt = $my->listTaobaoOrderByOrderNo($orderNo);
	if(empty($rslt)){
		echo "false";
	}else{
		echo "true";
	}
	
} else if($actionStr == "addTaobaoOrder"){
	$orderNo = $_GET["orderNo"];
	$orderCreatedTime = $_GET["orderCreatedTime"];
	$buyerName = $_GET["buyerName"];
	$buyerNote = $_GET["buyerNote"];
	$addressFull = $_GET["addressFull"];
	if(empty($orderNo)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyTaobao();
	$rslt = $my->addTaobaoOrder($orderNo, $orderCreatedTime, $buyerName, $buyerNote, $addressFull);
	echo $rslt;
} else if($actionStr == "addTaobaoOrderDetail"){
	$orderNo = $_GET["orderNo"];
	$baobeiTitle = $_GET["baobeiTitle"];
	$sku = $_GET["sku"];
	if(empty($orderNo)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyTaobao();
	$rslt = $my->addTaobaoOrderDetail($orderNo, $baobeiTitle, $sku);
	echo $rslt;
} else if($actionStr == "addLoadOrderCommand"){
	$my = new MyTaobao();
	$rslt = $my->addLoadOrderCommand();
} else if($actionStr == "getLoadOrderCommand"){
	$my = new MyTaobao();
	$rslt = $my->getLoadOrderCommand();
} else if($actionStr == "removeLoadOrderCommand"){
	$my = new MyTaobao();
	$rslt = $my->removeLoadOrderCommand();
	
	
} else{
	return "[ERROR]PARAMETER";
}
?>