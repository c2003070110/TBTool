<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/ObjectClass.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyVideoTr
{
	public function addVideoByUrl($url, $toType, $fromType, $trid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$obj = new MyVideoObject();
		$uid = uniqid("tr", true);
		$obj->uid = $uid;
		$obj->url = $url;
		if(empty($toType)) $toType = "toYoutube";
		if(empty($fromType)) $fromType = "";
		if(empty($trid)) $trid = $uid;
		$obj->toType = $toType;
		$obj->fromType = $fromType;
		$obj->trid = $trid;
		$obj->status = "added";
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function updateByTitle($uid, $title){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['title', $title]);
	}
	public function updateByVideoUper($uid, $title, $uper, $ytSearchRsltr, $videoUrl){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['title', $title],
					 ['uper', $uper],
					 ['ytSearchRslt', $ytSearchRslt],
					 ['videoUrl', $videoUrl],
					 ['dtparsed', date("YmdGis")]);
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
			$tbl->select(['uid', '==', $uid])
				->update(['status', $toStatus]);
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
	public function getByTodownloadOne(){
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
	}
	public function getByTouploadOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "toul" ){
				return $data;
			}
		}
		return NULL;
	}
	public function getByTomergeOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select(['status', '==', "tomg"])->fetch();
		
		return $dataArr[0];
	}
	public function listByTodownload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "parsed" && empty($data["groupUid"])){
				$rslt[] = $data;
			}
		}
		//var_dump($rslt);
		return $rslt;
	}
	public function listByToupload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "dled" && empty($data["groupUid"])){
				$rslt[] = $data;
			}
		}
		return $rslt;
	}
	public function listByUploaded(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "uled"){
				$rslt[] = $data;
			}
		}
		return $rslt;
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
	    foreach ((array) $dataArr as $key => $value) {
			$sort[$key] = $value['dtAdd'];
	    }
	    array_multisort($sort, SORT_ASC, $rslt);
		return $rslt;
	}
}
?>