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
		$uid = uniqid("bili", true);
		$obj->uid = $uid;
		$obj->url = $url;
		$obj->status = "added";
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function updateByVideoUper($uid, $title, $uper, $ytSearchRsltr, $urlTrue){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['title', $title],
					 ['uper', $uper],
					 ['ytSearchRslt', $ytSearchRslt],
					 ['urlTrue', $urlTrue],
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
	
	
	public function listByNewOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "added" ){
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
			if(empty($data["ytSearchRslt"]) ){
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
			if($data["status"] === "todl" ){
				return $data;
			}
		}
		return NULL;
	}
	public function getByTouploadOne($url){
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
	public function listByTodownload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYVIDEOTR_VIDEO_INFO"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "parsed"){
			//if($data["status"] === "added" || $data["status"] === "parsed" || $data["status"] === "todl" ){
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
			if($data["status"] === "dled"){
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
}
?>