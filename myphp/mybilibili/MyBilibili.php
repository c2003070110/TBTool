<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/ObjectClass.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;


class MyBilibili
{
	public function addBilibiliByUrl($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		$obj = new BilibiliObject();
		$uid = uniqid("bili", true);
		$obj->uid = $uid;
		$obj->url = $url;
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function updateByBilibiliInfo($uid, $title, $uper){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['title', $title],
					 ['uper', $uper],
					 ['dtparsed', date("YmdGis")]);
	}
	public function updateByYTInfo($uid, $ytSearchRslt){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['ytSearchRslt', $ytSearchRslt],
					 ['dtparsed', date("YmdGis")]);
	}
	public function updateByDownloadInfo($uid, $dlVideoPath){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['dlVideoPath', $dlVideoPath],
			         ['status', 'dled'],
					 ['dtdled', date("YmdGis")]);
	}
	public function updateByUploadInfo($uid, $ytVideoUrl){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['dlVideoPath', $ytVideoUrl],
			         ['status', 'uled'],
					 ['dtuled', date("YmdGis")]);
	}
	public function updateByStatus($uid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_AMZN_ORDER"));
		
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
	
	
	public function getByBiliNewOne($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if(empty($data["uper"]) )
				return $data;
			}
		}
		return NULL;
	}
	public function getByYTNewOne($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if(empty($data["ytSearchRslt"]) )
				return $data;
			}
		}
		return NULL;
	}
	public function getByTodownloadOne($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "todl" )
				return $data;
			}
		}
		return NULL;
	}
	public function getByTouploadOne($url){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "dled" || $data["status"] === "toul" )
				return $data;
			}
		}
		return NULL;
	}
	public function listByTodownload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "added" || $data["status"] === "parsed" || $data["status"] === "todl" )
				$rslt[] = $data;
			}
		}
		return rslt;
	}
	public function listByToupload(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "dled")
				$rslt[] = $data;
			}
		}
		return rslt;
	}
	public function listByUploaded(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYBILIBILI_BILI"));
		$rslt = array();
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			if($data["status"] === "uled")
				$rslt[] = $data;
			}
		}
		return rslt;
	}
}
?>