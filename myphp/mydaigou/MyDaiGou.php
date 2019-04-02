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
		if(!isset($obj->orderDate) || $obj->orderDate == ''){
			$obj->orderDate = date( "Ymd" );
		}
		if(!isset($obj->buyer) || $obj->buyer == ''){
			$obj->uid = null;
			$obj->status = 'unasign';
		}
		if(!isset($obj->uid) || $obj->uid == ''){
			if(isset($obj->qtty) && $obj->qtty != ''){
				$qtty = intval($obj->qtty);
				for ($i = 1; $i <= $qtty; $i++) {
					$insetObj = clone $obj;
					$insetObj->uid = uniqid() . strval($i);
					$insetObj->qtty = '1';
					//var_dump($insetObj);
					$rslt = $tbl->insert($insetObj);
                }
				return ;
			}
			$obj->uid = uniqid();
			$obj->qtty = '1';
			$tbl->insert($obj);
			return;
		}
		$cnt = $tbl->select(['uid', '==', $obj->uid])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		} else{
			$tbl->select(['uid', '==', $obj->uid])
				->update(
				         ['buyer', $obj->buyer],
				         ['orderItem', $obj->orderItem],
				         ['status', $obj->status],
						 ['priceJPY', $obj->priceJPY],
						 ['priceCNY', $obj->priceCNY]);
		}
	}
	public function deleteItem($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		if(!isset($obj->uid) || $obj->uid == ''){
			return;
		}
		$dataArr = $tbl->select(['uid', '==', $obj->uid])->fetch();
		$data = $dataArr[0];
		if($data['status'] =='unasign'){
			$tbl->select(['uid', '==', $obj->uid])
				->delete();
		}else if($data['status'] =='unGou'){
			$tbl->select(['uid', '==', $obj->uid])
				->delete();
		}else if($data['status'] =='gouru' || $data['status'] =='zaitu' 
		          || $data['status'] =='fahuo' || $data['status'] =='compl'){
			$tbl->select(['uid', '==', $obj->uid])
				->update(['status', 'gouru'],
						 ['buyer', '']);
		}
		
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
	public function listItemByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['status', '==', $status])->fetch();
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
	public function listItemByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['uid', '==', $uid])->fetch();
		return $data[0];
	}
	
	
	public function saveBuyer($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		if(!isset($obj->uid) || $obj->uid == ''){
			$obj->uid = uniqid();
			$tbl->insert($obj);
			return "[success]insert!";
		}
		$tbl->select(['uid', '==', $obj->uid, 'and'])
		    ->update(
					 ['buyer', $obj->buyer],
					 ['address', $obj->address]);
		return "[success]update!";
	}
	public function listAllBuyer(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function listBuyerByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		$data = $tbl->select(['uid', '==', $uid])->fetch();
		return $data[0];
	}
}
?>