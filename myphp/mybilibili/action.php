<?php

//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require_once __DIR__ .'/MyBilibili.php';
$actionStr = $_GET['action'];

if(empty($actionStr)){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}

if($actionStr == "addByUrl"){
	$url = $_GET["url"];
	if(empty($url)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyBilibili();
	$rslt = $my->addBilibiliByUrl($url);
	echo $rslt;
} else if($actionStr == "updateStatus"){
	$uid = $_GET["uid"];
	$status = $_GET["status"];
	if(empty($uid) || empty($status)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyBilibili();
	$rslt = $my->updateByStatus($uid, $status);


//*****service action******	
} else if($actionStr == "getByBiliNewOne"){
	$my = new MyBilibili();
	$rslt = $my->listByNewOne();
	echo $rslt["url"];
} elseuidif($actionStr == "updateByBilibiliInfo"){
	$uid = $_GET["uid"];
	$title = $_GET["title"];
	$uper = $_GET["uper"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyBilibili();
	$rslt = $my->updateByBilibiliInfo($uid, $title, $uper);
	echo $rslt;
} else if($actionStr == "getByYTNewOne"){
	$my = new MyBilibili();
	$rslt = $my->listByNewOne();//BilibiliObject
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "updateByYTInfo"){
	$uid = $_GET["uid"];
	$ytSearchRslt = $_GET["ytSearchRslt"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyBilibili();
	$rslt = $my->updateByYTInfo($uid, $ytSearchRslt);
	echo $rslt;
} else if($actionStr == "getByTodownloadOne"){
	$my = new MyBilibili();
	$rslt = $my->getByTodownloadOne();//BilibiliObject
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "updateByDownloadInfo"){
	$uid = $_GET["uid"];
	$dlVideoPath = $_GET["dlVideoPath"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyBilibili();
	$rslt = $my->updateByDownloadInfo($uid, $dlVideoPath);
	echo $rslt;
} else if($actionStr == "getByTouploadOne"){
	$my = new MyBilibili();
	$rslt = $my->getByTouploadOne();//BilibiliObject
	if(empty($data)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "updateByUploadInfo"){
	$uid = $_GET["uid"];
	$ytVideoUrl = $_GET["ytVideoUrl"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyBilibili();
	$rslt = $my->updateByUploadInfo($uid, $ytVideoUrl);
	echo $rslt;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
	$rslt = $my->addMyBid($_GET["buyer"], $_GET["urllist"], $_GET["jpylist"]);
	echo $rslt;

//******parcel info ********
} else if($actionStr == "updateParcelAmt"){
	if(empty($_GET["buyer"]) || empty($_GET["myparcelUid"])){
		return ;
	} 
	$my = new MyYaBid();
	$my->updateParcelByAmt($_GET["buyer"], $_GET["myparcelUid"]);
} else if($actionStr == "calcParcelPrice"){
	if(empty($_GET["buyer"]) || empty($_GET["myparcelUid"])){
		return ;
	} 
	$my = new MyYaBid();
	$my->calcParcelPrice($_GET["buyer"], $_GET["myparcelUid"]);
} else if($actionStr == "parcelGuojiFahuo"){
	if(empty($_GET["buyer"]) || empty($_GET["myparcelUid"])){
		return ;
	} 
	$my = new MyYaBid();
	$my->updateParcelByGuojiFahuo($_GET["buyer"], $_GET["myparcelUid"], $_GET["transferNo"]);
} else if($actionStr == "parcelGuoneiFahuo"){
	if(empty($_GET["buyer"]) || empty($_GET["myparcelUid"])){
		return ;
	} 
	$my = new MyYaBid();
	$my->updateParcelByGuoneiFahuo($_GET["buyer"], $_GET["myparcelUid"], $_GET["transferNo"]);

//******item info ********
} else if($actionStr == "updateItemStatus"){
	if(empty($_GET["uid"]) || empty($_GET["buyer"]) || empty($_GET["status"])){
		return ;
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemStatus($_GET["buyer"], $_GET["uid"], $_GET["status"]);
} else if($actionStr == "updateItemPrice"){
	if(empty($_GET["uid"]) || empty($_GET["buyer"])){
		return "[ERROR]PARAMETER";
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemPrice($_GET["buyer"], $_GET["uid"],
	                $_GET["priceJPY"],$_GET["transfeeDaoneiJPY"],$_GET["weight"]);
} else if($actionStr == "updateItemEstimatePrice"){
	if(empty($_GET["uid"]) || empty($_GET["buyer"])){
		return "[ERROR]PARAMETER";
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemByEstimatePrice($_GET["buyer"], $_GET["uid"],
	                $_GET["estimateJPY"]);

//******taobao dingdan info ********
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
	$rslt = $my->deleteTaobaoDingdan($_GET["buyer"], $_GET["myparcelUid"],
	               $_GET["taobaodingdanUid"]);
				   
//*****service action******	
} else if($actionStr == "listItemByEmptyBidUidOne"){
	$my = new MyYaBid();
	$rslt = $my->listItemByEmptyBidUidOne();
	echo $rslt["itemUrl"];//itemObject->itemUrl
} else if($actionStr == "insertBidObject"){
	if(empty($_GET["itemUrl"]) || empty($_GET["bidId"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$my->insertBidObject($_GET["itemUrl"], $_GET["bidId"]);
} else if($actionStr == "listBidByEmptyObiderAdrOne"){
	$my = new MyYaBid();
	$rslt = $my->listBidByEmptyObiderAdrOne();
	echo $rslt["bidId"];//bidObject->bidId
} else if($actionStr == "updateBidByObiderAdr"){
	if(empty($_GET["bidId"]) || empty($_GET["obiderAdr"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$my->updateBidByObiderAdr($_GET["bidId"], $_GET["obiderAdr"]);
} else if($actionStr == "updateBidByObiderMsg"){
	if(empty($_GET["bidId"]) || empty($_GET["obiderMsg"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$my->updateBidByObiderMsg($_GET["bidId"], $_GET["obiderMsg"]);

} else if($actionStr == "listItemByEmptyPriceOne"){
	$my = new MyYaBid();
	$rslt = $my->listItemByEmptyPriceOne();
	echo $rslt["itemUrl"];//itemObject->itemUrl
} else if($actionStr == "updateItemPriceByBidId"){
	if(empty($_GET["itemUrl"]) || empty($_GET["bidId"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$my->updateItemPriceByBidId($_GET["bidId"]);
} else if($actionStr == "updateItemStatusDepaiByBidId"){
	if(empty($_GET["bidId"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemStatusByBidId($_GET["bidId"], "depai");
} else if($actionStr == "updateItemStatusBdfhByBidId"){
	if(empty($_GET["bidId"])){
		echo "[ERROR]PARAMETER";
		return;
	} 
	$my = new MyYaBid();
	$rslt = $my->updateItemStatusByBidId($_GET["bidId"], "bdfh");
	
	
	
} else{
	var_dump($actionStr);
	return "[ERROR]PARAMETER";
}
?>