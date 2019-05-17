<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);

require_once __DIR__ .'/MyVideoTr.php';
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
	$my = new MyVideoTr();
	$rslt = $my->addVideoByUrl($url);
	echo $rslt;
}else if($actionStr == "updateStatusWithTitle"){
	$uid = $_GET["uid"];
	$title = $_GET["title"];
	$uper = $_GET["uper"];
	$status = $_GET["status"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$rslt = $my->updateByTitle($uid, $title, $uper);
	if(!empty($uid)){
		$my->updateByStatus($uid, $status);
	}
	echo $rslt;

} else if($actionStr == "updateStatus"){
	$uid = $_GET["uid"];
	$status = $_GET["status"];
	if(empty($uid) || empty($status)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$rslt = $my->updateByStatus($uid, $status);


//*****service action******	
} else if($actionStr == "getLastestVideoOne"){
	$my = new MyVideoTr();
	$rslt = $my->listByNewOne();//MyVideoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "updateByVideoUper"){
	$uid = $_GET["uid"];
	$trid = $_GET["trid"];
	$title = $_GET["title"];
	$uper = $_GET["uper"];
	$ytSearchRslt = $_GET["ytSearchRslt"];
	$videoUrl = $_GET["videoUrl"];
	$toType = $_GET["toType"];
	$fromType = $_GET["fromType"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$rslt = $my->updateByVideoUper($uid, $trid, $title, $uper, $ytSearchRslt, $videoUrl, $toType, $fromType);
	echo $rslt;
} else if($actionStr == "getByTodownloadOne"){
	$my = new MyVideoTr();
	$rslt = $my->getByTodownloadOne();//MyVideoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "listByTodownload"){
	$my = new MyVideoTr();
	$rslt = $my->listByTodownload();//MyVideoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "getByTouploadOne"){
	$my = new MyVideoTr();
	$rslt = $my->getByTouploadOne();//MyVideoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "getByTomergeOne"){
	$my = new MyVideoTr();
	$rslt = $my->getByTomergeOne();//MyVideoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "listByGroupUid"){
	$groupUid = $_GET["groupUid"];
	if(empty($groupUid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$rslt = $my->listByGroupUid($groupUid);//MyVideoObject
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "listVideoStatusByUrl"){
	$url = $_GET["url"];
	if(empty($url)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$rslt = $my->listVideoStatusByUrl($url);
	if(empty($rslt)){
		echo "";
	}else{
		echo json_encode($rslt);
	}
} else if($actionStr == "insertVideo"){
	$url = $_GET["url"];
	$trid = $_GET["trid"];
	$title = $_GET["title"];
	$uper = $_GET["uper"];
	$ytSearchRslt = $_GET["ytSearchRslt"];
	$videoUrl = $_GET["videoUrl"];
	$toType = $_GET["toType"];
	$fromType = $_GET["fromType"];
	$groupUid = $_GET["groupUid"];
	if(empty($url) || empty($videoUrl)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$data = $my->listVideoStatusByUrl($url);
	if(!empty($data)) { 
		echo "[Fatal]Already inserted!"; 
		return;
	}
	$uid = $my->addVideoByUrl($url);
	$my->updateByVideoUper($uid, $trid, $title, $uper, $ytSearchRslt, $videoUrl, $toType, $fromType);
	if(!empty($groupUid)){
		$my->updateByGroupUid($uid, $groupUid);
	}
	echo $uid;
/*	
} else if($actionStr == "getByYTNewOne"){
	$my = new MyVideoTr();
	$rslt = $my->listByNewOne();//MyVideoObject
	if(empty($rslt)){
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
	$my = new MyVideoTr();
	$rslt = $my->updateByYTInfo($uid, $ytSearchRslt);
	echo $rslt;
} else if($actionStr == "updateByDownloadInfo"){
	$uid = $_GET["uid"];
	$dlVideoPath = $_GET["dlVideoPath"];
	if(empty($uid)){
		echo "[Fatal]Parameter is NULL";
		return ;
	}
	$my = new MyVideoTr();
	$rslt = $my->updateByDownloadInfo($uid, $dlVideoPath);
	echo $rslt;
} else if($actionStr == "getByTouploadOne"){
	$my = new MyVideoTr();
	$rslt = $my->getByTouploadOne();//MyVideoObject
	if(empty($rslt)){
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
	$my = new MyVideoTr();
	$rslt = $my->updateByUploadInfo($uid, $ytVideoUrl);
	echo $rslt;
*/

} else{
	return "[ERROR]PARAMETER";
}
?>