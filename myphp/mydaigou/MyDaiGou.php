<?php
require __DIR__ . '/../common.php';
require __DIR__ .'/BuyerObject.php';
require __DIR__ .'/ItemObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyDaiGou
{
	public function saveItem($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		if(!isset($obj->buyer) || $obj->buyer == ''){
			$obj->uid = null;
			$obj->status = 'unasign';
		}
		if(!isset($obj->uid) || $obj->uid == ''){
			$obj->uid = uniqid();
			$tbl->insert($obj);
		}
		$cnt = $tbl->select(['uid', '==', $obj->uid])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		} else{
			$tbl->select(['uid', '==', $obj->uid])
				->update(
				         ['buyer', $obj->buyer],
				         ['orderDate', $obj->orderDate],
				         ['orderItem', $obj->orderItem],
				         ['status', $obj->status],
						 ['priceJPY', $obj->priceJPY],
				         ['qtty', $obj->qtty],
						 ['priceCNY', $obj->priceCNY]);
		}
	}
	public function deleteItem($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		if(!isset($obj->uid) || $obj->uid == ''){
			return;
		}
		$cnt = $tbl->select(['uid', '==', $obj->uid])->delete();
		
	}
	public function updateItemStatus($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		
		if(!isset($obj->uid) || $obj->uid == ''){
			return;
		}
		
		$tbl->select(['uid', '==', $obj->uid])
			->update(['status', $obj->status]);
	}
	public function listAllItem(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function listItemByBuyer($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['buyer', '==', $buyer])->fetch();
		return $data;
	}
	public function listItemByBuyerAndStatus($buyer, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['buyer', '==', $buyer, 'and'],['status', '==', $status, 'and'])->fetch();
		return $data;
	}
	public function listItemByUnAsign(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['status', '==', 'unasign', 'and'])->fetch();
		return $data;
	}
	
	
	public function addBuyer($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		$cnt = $tbl->select(['buyer', '==', $obj->buyer, 'and'])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		}
	}
	public function listAllBuyer(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
}
?>