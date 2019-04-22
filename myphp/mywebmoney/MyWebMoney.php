<?php
require_once __DIR__ . '/../mycommon.php';
require_once __DIR__ . '/../mydefine.php';
require_once __DIR__ .'/../adminsupp/MyHuilv.php';
require_once __DIR__ . '/WebMoneyObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;


class MyWebMoney
{
	public function getHuilv(){
		$myhuilv = new MyHuilv();
		return $myhuilv->listByHuilvDiv("YA");
	}
	
	public function addDaiChong($uid, $url, $amtJPY, $tbBuyer, $payway){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		
		if(empty($uid)){
			$data = $tbl->select(['url', '==', $url, 'and'])->fetch();
			if(!empty($data)){
				return "[ERROR]This URL had added!!";
			}
			
			$data = new WebMoneyObject();
			$data->uid = uniqid();
			$data->tbBuyer = $tbBuyer;
			$data->url = $url;
			$data->amtJPY = $amtJPY;
			$data->payway = $payway;
			$data->status = 'checkwait';
			$data->dtAdd = date("YmdGis");
			
			$tbl->insert($data);
			return $data->uid;
		}else{
			$tbl->select(['uid', '==', $uid])
				->update(['tbBuyer', $tbBuyer],
						 ['url', $url],
						 ['amtJPY', $amtJPY],
						 ['payway', $payway]);
			return $uid;
		}
	}
	
	
	public function getLastestNoticeOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		
		$data = $tbl->select(['status', '==', "checkwait"])->fetch()[0];
		if(empty($data)){
			return "";
		}
		return $data;
	}
	public function getCheckedNoticeOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		
		$data = $tbl->select(['status', '==', "topay"])->fetch()[0];
		if(empty($data)){
			return "";
		}
		return $data;
	}
	public function updateCheckResult($uid, $shopComment, $itemInfo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		if(empty($uid) || empty($itemInfo)){
			return;
		}
		$tbl->select(['uid', '==', $uid])
			->update(['realShopComment', $shopComment],
			         ['realItemName', $itemInfo],
			         ['dtCheck', date("YmdGis")],
			         ['status', "checked"]);
	}
	public function updatePayResult($uid, $payResult){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		if(empty($uid) || empty($payResult)){
			return;
		}
		$tbl->select(['uid', '==', $uid])
			->update(['payResult', $payResult],
			         ['dtPay', date("YmdGis")],
			         ['status', "paid"]);
	}
	public function updateDaichongStatus($uid, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		if(empty($uid) || empty($status)){
			return;
		}
		if($status === "del"){
			$tbl->select(['uid', '==', $uid])
				->delete();
		}else{
			$tbl->select(['uid', '==', $uid])
				->update(['status', $status]);
		}
	}
	public function listDaiChongByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		if(empty($uid)){
			return;
		}
		return $tbl->select(['uid', '==', $uid])
				   ->fetch()[0];
	}
	public function listDaiChongByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		if(empty($uid) || empty($status)){
			return;
		}
		return $tbl->select(['status', '==', $status])
			->fetch();
	}
	public function listDaiChongByAll(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYWEBMONEY_DAICHONG"));
		$dataArr = $tbl->select("*")->fetch();
		return $dataArr;
	}
}
?>