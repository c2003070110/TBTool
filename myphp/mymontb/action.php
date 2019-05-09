<?php
/*
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
*/
require __DIR__ . '/MyMontb.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[ERROR]Parameter Of [action] is NULL";
	return;
}
if($actionStr == "saveTBOrder"){
	$dingdanhao = $_GET['dingdanhao'];
	if(empty($dingdanhao)){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->saveTBOrder();
	echo $rslt; // uid
	return;
} else if($actionStr == "deleteTBOrder"){
	if(empty($_GET["uid"])){
		return ;
	} 
	$my = new MyMontb();
	$rslt = $my->deleteTBOrderByTBUid($_GET["uid"]);
	echo $rslt; // success?failure?
	return;
} else if($actionStr == "updateProductStatus"){
	if(empty($_GET["uid"]) || empty($_GET["status"])){
		return ;
	} 
	$my = new MyMontb();
	$rslt = $my->updateProductInfoStatus($_GET["uid"], $_GET["status"]);
	//$rslt = $my->updateTBOrderStatus($_GET["uid"], $_GET["status"]);
	//echo $rslt; // uid
// ********** MB
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
} else if($actionStr == "updateMBOrderStatus"){
	if(empty($_GET["uid"]) || empty($_GET["status"])){
		return ;
	} 
	$my = new MyMontb();
	$rslt = $my->updateMBOrderStatus($_GET["uid"], $_GET["status"]);
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


} else if($actionStr == "makePinyouChinaMJ"){
	$productUidList = $_GET['productUidList'];
	if(empty($productUidList)){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->makePinyou($productUidList, "cnMJ");
	echo $rslt;
} else if($actionStr == "makePinyouChinaPX"){
	$productUidList = $_GET['productUidList'];
	if(empty($productUidList)){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->makePinyou($productUidList, "cnPX");
	echo $rslt;
} else if($actionStr == "makePinyouJapan"){
	$productUidList = $_GET['productUidList'];
	if(empty($productUidList)){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$rslt = $my->makePinyou($productUidList, "jp");
	echo $rslt;

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
// listMBOrderByEmptyMBOrderOne input:NONE; ouput:ProductObject;
} else if($actionStr == "listMBOrderByEmptyMBOrderOne"){
	$my = new MyMontb();
	$data = $my->listMBOrderByEmptyMBOrderOne();
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($data);
	}
	return;
// updateMBOrderNo input:uid ; mbOrderNo; output:NONE;
// see to updateMBOrderNo
// listProductInfoByMBUid input:NONE; ouput: List Of ProductObject;
} else if($actionStr == "listProductInfoByMBUid"){
	$mbUid = $_GET['mbUid'];
	if($mbUid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	echo json_encode($my->listProductInfoByMBUid($mbUid));
	return;
// listProductInfoByEmptyPriceOne input:NONE; ouput:MBOrderObject;
} else if($actionStr == "listProductInfoByEmptyPriceOne"){
	$my = new MyMontb();
	$data = $my->listProductInfoByEmptyPriceOne();
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($data);
	}
	return;
// updateProductInfoByStock input:productUid,priceOffTax,stock; ouput:NONE;
} else if($actionStr == "updateProductInfoByStock"){
	$uid = $_GET['uid'];
	if($uid === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$my->updateProductInfoByStock($_GET['uid'],$_GET['priceOffTax'],$_GET['stock']);
	return;
} else if($actionStr == "getMaijiadianzhiHanziOne"){
	$my = new MyMontb();
	$data = $my->getMaijiadianzhiHanziOne();
	echo $data;
	return;
} else if($actionStr == "updateMaijiadianzhiPY"){
	$uid = $_GET['uid'];
	$maijiadianzhiPY = $_GET['maijiadianzhiPY'];
	if($uid === null || $maijiadianzhiPY === null){
		echo "[ERROR]Parameter is NULL";
		return;
	}
	$my = new MyMontb();
	$my->updateMaijiadianzhiPY($uid, $maijiadianzhiPY);
	return;
	
	
} else {	
}
?>