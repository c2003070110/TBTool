<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ .'/HuilvObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyHuilv
{
	public function save($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		$cnt = $tbl->select(['huilvDiv', '==', $obj->huilvDiv])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		} else{
			$tbl->select(['huilvDiv', '==', $obj->uidhuilvDiv
				->update(
				         ['huilvVal', $obj->huilvVal]);
		}
	}
	public function listAllItem(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function listByHuilvDiv($huilvDiv){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		$dataA = $tbl->select(['huilvDiv', '==', $huilvDiv])->fetch();
		return $dataA[0];
	}
}
?>