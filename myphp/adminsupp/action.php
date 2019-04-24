<?php

//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyHuilv.php';
require __DIR__ .'/MyBackup.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}

if($actionStr == "saveMyhuilv"){
	$my = new MyHuilv();
	$rslt = $my->save($_GET['huilvDiv'],$_GET['plusplus'],$_GET['myhuilv']);
	echo $rslt;
} else if($actionStr == "updateYLHuilv"){
	if(empty($_GET["huilvYL"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyHuilv();
	$rslt = $my->updateHuilvByYinglian($_GET["huilvYL"]);
	echo $rslt;

if($actionStr == "getMyhuilvByHuilvDiv"){
	$huilvDiv = $_GET['huilvDiv'];
	if(empty($huilvDiv)) {
		return;
	}
	$my = new MyHuilv();
	$rslt = $my->getMyhuilvByHuilvDiv($huilvDiv);
	echo json_encode($rslt);

//******* backup ********
} else if($actionStr == "backupMyGiftCard"){
	$my = new MyBackup();
	$rslt = $my->backupMyGiftCard();
	echo $rslt;
} else if($actionStr == "backupMyMontbell"){
	$my = new MyBackup();
	$rslt = $my->backupMyMontbell();
	echo $rslt;
	
	
} else {
	echo "[ERROR]PARAMETER";
	return;
}
?>