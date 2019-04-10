<?php
require_once __DIR__ . '/../mycommon.php';
require_once __DIR__ . '/../mydefine.php';
require_once __DIR__ .'/HuilvObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyHuilv
{
	public function save($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		
		$obj = new HuilvObject();
		$obj->huilvDiv = $_GET['huilvDiv'];
		$obj->plusplus = $_GET['plusplus'];
		$obj->huilvVal = $_GET['myhuilv'];
		
		$cnt = $tbl->select(['huilvDiv', '==', $obj->huilvDiv])->count();
		if($cnt == 0){
			$tbl->insert($obj);
			return "insert!";
		} else{
			$tbl->select(['huilvDiv', '==', $obj->huilvDiv])
				->update(['huilvVal', $obj->myhuilv]);
			return "update!";
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
		return $dataA[0]["huilvVal"];
	}
}
?>