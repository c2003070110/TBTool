<?php
require '../common.php';
require './DaiGouObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyDaiGou
{
	public function save($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_SALE_INFO"));
		$cnt = $tbl->select(['buyer', '==', $obj->buyer, 'and'],
		                    ['orderDate', '==', $obj->orderDate, 'and'],
							['orderItem', '==', $obj->orderItem, 'and'])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		} else{
			$tbl->select(['buyer', '==', $obj->buyer, 'and'],
		                    ['orderDate', '==', $obj->orderDate, 'and'],
							['orderItem', '==', $obj->orderItem, 'and'])
				->update(['priceJPY', $obj->priceJPY],
				         ['qtty', $obj->qtty],
						 ['priceCNY', $obj->priceCNY]);
		}
	}
	public function listAll(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_SALE_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function listByBuyer($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_SALE_INFO"));
		$data = $tbl->select(['buyer', '==', $buyer])->fetch();
		return $data;
	}
	public function addBuyer($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_SALE_INFO"));
		$cnt = $tbl->select(['buyer', '==', $obj->buyer, 'and'])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		}
		//return $data;
	}
	public function delete($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_SALE_INFO"));
		$cnt = $tbl->select(['buyer', '==', $obj->buyer, 'and'],
		                    ['orderDate', '==', $obj->orderDate, 'and'],
							['orderItem', '==', $obj->orderItem, 'and'])->delete();
		
	}
	public function updateStatus($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_SALE_INFO"));
		
		$tbl->select(['buyer', '==', $obj->buyer, 'and'],
						['orderDate', '==', $obj->orderDate, 'and'],
						['orderItem', '==', $obj->orderItem, 'and'])
			->update(['status', $obj->status]);
		
	}
}
?>