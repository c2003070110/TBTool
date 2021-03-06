<?php

require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/ObjectClass.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyVideoTr
{
	public function addVideoByUrl($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$obj = new MyVideoObject();
		$prefix = "tr";
		// #在抖音，记录美好生活#偶遇抖音小姐姐，表情一绝#抖音小姐姐@抖音小助手 http://v.douyin.com/MdUNMu/ //复制此链接，打开【抖音短视频】，直接观看视频！
		if(strpos($url, "sina") !== false){$prefix = "wb";}
		if(strpos($url, "twi") !== false){$prefix = "tw";}
		if(strpos($url, "bili") !== false){$prefix = "bi";}
		if(strpos($url, "tiktok") !== false){$prefix = "tkJP";}
		if(strpos($url, "tiktok") !== false){$prefix = "tkCN";}
		$uid = uniqid($prefix, true);
		$obj->uid = $uid;
		$obj->url = $url;
		$obj->status = "added";
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function updateByTitle($uid, $title, $uper){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['title', $title],
			         ['uper', $uper]);
	}
	public function updateByVideoUper($uid, $trid, $title, $uper, $ytSearchRslt, $videoUrl, $toType, $fromType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$data = $tbl->select(['uid', '==', $uid])->fetch()[0];
		if(empty($trid)) $trid = $data["trid"];
		if(empty($trid)) $trid = $uid;
		if(empty($toType)) $toType = $data["toType"];
		if(empty($fromType)) $fromType = $data["fromType"];
		$tbl->select(['uid', '==', $uid])
			->update(['title', $title],
					 ['uper', $uper],
					 ['ytSearchRslt', $ytSearchRslt],
					 ['videoUrl', $videoUrl],
					 ['dtparsed', date("YmdGis")],
			         ['toType', $toType],
			         ['fromType', $fromType],
			         ['trid', $trid]);
		$this->updateByStatus($uid, "parsed");
	}
	public function updateByYTInfo($uid, $ytSearchRslt){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['ytSearchRslt', $ytSearchRslt],
					 ['dtparsed', date("YmdGis")]);
	}
	public function updateByDownloadInfo($uid, $dlVideoPath){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['dlVideoPath', $dlVideoPath],
			         ['status', 'dled'],
					 ['dtdled', date("YmdGis")]);
	}
	public function updateByUploadInfo($uid, $ytVideoUrl){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['dlVideoPath', $ytVideoUrl],
			         ['status', 'uled'],
					 ['dtuled', date("YmdGis")]);
	}
	public function updateByStatus($uid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		if(empty($uid) || empty($toStatus)){
			return;
		}
		if($toStatus == "del"){
			$tbl->select(['uid', '==', $uid])
				->delete();
		}else{
			$nowS = date("YmdGis");
			$dtKey = "";
			if($toStatus == "parsed"){
				$dtKey = "dtparsed";
			}else if($toStatus == "dled"){
				$dtKey = "dtdled";
			}else if($toStatus == "uled"){
				$dtKey = "dtuled";
			}
			if(empty($dtKey)){
				$tbl->select(['uid', '==', $uid])
					->update(['status', $toStatus]);
			}else{
				$tbl->select(['uid', '==', $uid])
					->update(['status', $toStatus], [$dtKey, $nowS]);
			}
		}
	}
	public function updateByGroupUid($uid, $groupUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		if(empty($uid) || empty($groupUid)){
			return;
		}
		$tbl->select(['uid', '==', $uid])
			->update(['groupUid', $groupUid]);
	}
	
	
	public function listByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select(['uid', '==', $uid])->fetch();
		return $dataArr[0];
	}
	public function listVideoStatusByUrl($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select(['url', '==', $url])->fetch();
		return $dataArr;
	}
	public function listByNewOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "added" && empty($data["groupUid"]) ){
				return $data;
			}
		}
		return NULL;
	}
	public function getByYTNewOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if(empty($data["ytSearchRslt"]) && empty($data["groupUid"])){
				return $data;
			}
		}
		return NULL;
	}
	public function listByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function getByTodownload(){
		return $this->listByStatus("todl")[0];
		/*
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			//var_dump($data);
			if($data["status"] === "todl" ){
				return $data;
			}
		}
		return NULL;
		*/
	}
	public function getByTouploadOne(){
		return $this->listByStatus("toul")[0];
		/*
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "toul" ){
				return $data;
			}
		}
		return NULL;
		*/
	}
	public function getByTomergeOne(){
		return $this->listByStatus("tomg")[0];
		/*
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select(['status', '==', "tomg"])->fetch();
		
		return $dataArr[0];
		*/
	}
	public function listByTodownload(){
		return $this->listByStatus("todl");
	}
	public function listByToupload(){
		return $this->listByStatus("dled");
	}
	public function listByUploaded(){
		return $this->listByStatus("uled");
	}
	public function listByAll(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$dataArr = $tbl->select("*")->fetch();
		return $dataArr;
	}
	public function listFromGroupByAll(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$dataArr = $tbl->select("*")->fetch();
		$rslt = array();
		foreach ($dataArr as $data) {
			if(!empty($data["groupUid"])){
				$rslt[] = $data;
			}
		}
	    $sort = array();
	    foreach ((array) $dataArr as $key => $value) {
			$sort[$key] = $value['groupUid'];
	    }
	    array_multisort($sort, SORT_ASC, $rslt);
		return $rslt;
	}
	public function listByGroupUid($groupUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select(['groupUid', '==', $groupUid])->fetch();
	    $sort = array();
	    foreach ((array) $dataArr as $key => $value) {
			$sort[$key] = $value['dtAdd'];
	    }
	    array_multisort($sort, SORT_ASC, $dataArr);
		return $dataArr;
	}
	public function listGroupByTodownload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "parsed" && !empty($data["groupUid"])){
				$rslt[] = $data;
			}
		}
		return $rslt;
	}
	public function listGroupByToupload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "dled" && !empty($data["groupUid"])){
				$rslt[] = $data;
			}
		}
		return $rslt;
	}
	public function listByRedo(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		$rslt = array();
		foreach ($dataArr as $data) {
			if($data["status"] === "parsefailure" || $data["status"] === "dlfailure"
    			|| $data["status"] === "mgfailure" || $data["status"] === "ulfailure"){
				$rslt[] = $data;
			}
		}
	    $sort = array();
	    foreach ((array) $rslt as $key => $value) {
			$sort[$key] = $value['dtAdd'];
	    }
	    array_multisort($sort, SORT_DESC, $rslt);
		return $rslt;
	}
}
?>