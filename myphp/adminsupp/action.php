<?php

//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require __DIR__ .'/MyHuilv.php';
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
	
	
} else {
	echo "[ERROR]PARAMETER";
	return;
}
?>