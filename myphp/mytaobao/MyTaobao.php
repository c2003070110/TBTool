<?php
require_once __DIR__ . '/../mycommon.php';
require_once __DIR__ . '/../mydefine.php';
require_once __DIR__ . '/ObjectClass.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyTaobao
{
	public function addTaobaoFahuo($uid, $orderNo, $trackTraceNo, $tranferProviderName){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_FAHUO_INFO"));
		if(!empty($uid)){
			$tbl->select(['uid', '==', $uid])
				->update(['orderNo', $orderNo],
					 ['trackTraceNo', $trackTraceNo],
					 ['tranferProviderName', $tranferProviderName],
					 ['dtAdd', date("YmdGis")]);
			return $uid;
		}
		$obj = new TaobaoFahuoObject();
		$uid = uniqid("fh", true);
		$obj->uid = $uid;
		$obj->orderNo = $orderNo;
		$obj->trackTraceNo = $trackTraceNo;
		$obj->tranferProviderName = $tranferProviderName;
		$obj->status = "added";
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function updateFahuoStatus($uid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_FAHUO_INFO"));
		
		if(empty($uid) || empty($toStatus)){
			return;
		}
		if($toStatus == "del"){
			$tbl->select(['uid', '==', $uid])
				->delete();
			return;
		}else{
			$tbl->select(['uid', '==', $uid])
				->update(['status', $toStatus]);
		}
		$data = $this->listFahuoByUid($uid);
		if(empty($data)) return;
		
		$orderData = $this->listTaobaoOrderByOrderNo($data["orderNo"]);
		if(empty($orderData)) return;
		
		$this->updateTaobaoOrderStatus($orderData["uid"], $toStatus);
	}
	
	public function listFahuoByAll(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_FAHUO_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		
		return $dataArr;
	}
	public function listFahuoByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_FAHUO_INFO"));
		
		$dataArr = $tbl->select(['status', '==', $status])->fetch();
		
		return $dataArr;
	}
	public function listFahuoOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_FAHUO_INFO"));
		
		$dataArr = $tbl->select(['status', '==', "added"])->fetch();
		
		return $dataArr[0];
	}
	public function listFahuoByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_FAHUO_INFO"));
		
		$dataArr = $tbl->select(['uid', '==', $uid])->fetch();
		
		return $dataArr[0];
	}
	
	//------order
	public function addTaobaoOrder($orderNo, $orderCreatedTime, $buyerName, $buyerNote, $addressFull){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_INFO"));
		$obj = new TaobaoOrderObject();
		$uid = uniqid("tb", true);
		$obj->uid = $uid;
		$obj->orderNo = $orderNo;
		$obj->orderCreatedTime = $orderCreatedTime;
		$obj->buyerName = $buyerName;
		$obj->buyerNote = $buyerNote;
		$obj->addressFull = $addressFull;
		$obj->status = "added";
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function addTaobaoOrderDetail($orderNo, $baobeiTitle, $sku){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_DETAIL_INFO"));
		$obj = new TaobaoOrderObject();
		$uid = uniqid("tbd", true);
		$obj->uid = $uid;
		$obj->orderNo = $orderNo;
		$obj->baobeiTitle = $baobeiTitle;
		$obj->sku = $sku;
		$obj->status = "added";
		$obj->dtAdd = date("YmdGis");
		$tbl->insert($obj);
		return $uid;
	}
	public function updateTaobaoOrderStatus($uid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_INFO"));
		
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
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_DETAIL_INFO"));
		if($toStatus == "del"){
			$tbl->select(['uid', '==', $uid])
				->delete();
		}else{
			$tbl->select(['uid', '==', $uid])
				->update(['status', $toStatus]);
		}
	}
	public function listTaobaoOrderByAll(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		
		return $dataArr;
	}
	public function listTaobaoOrderByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_INFO"));
		
		$dataArr = $tbl->select(['uid', '==', $uid])->fetch();
		
		return $dataArr[0];
	}
	
	public function listTaobaoOrderByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_INFO"));
		
		$dataArr = $tbl->select(['status', '==', $status])->fetch();
		
		return $dataArr;
	}
	public function listTaobaoOrderByOrderNo($orderNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_INFO"));
		
		$dataArr = $tbl->select(['orderNo', '==', $orderNo])->fetch();
		
		return $dataArr[0];
	}
	public function listTaobaoOrderDetailByOrderNo($orderNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYTAOBAO_ORDER_DETAIL_INFO"));
		
		$dataArr = $tbl->select(['orderNo', '==', $orderNo])->fetch();
		
		return $dataArr;
	}
}
?>