<?php
require_once __DIR__ . '/../mycommon.php';
require_once __DIR__ . '/../mydefine.php';
require_once __DIR__ .'/HuilvObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyHuilv
{
	public function save($huilvDiv, $daigoufei, $myhuilv){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		
		$obj = new HuilvObject();
		$obj->huilvDiv = $huilvDiv;
		$obj->daigoufei = $daigoufei;
		$obj->huilvVal = $myhuilv;
		
		$cnt = $tbl->select(['huilvDiv', '==', $obj->huilvDiv])->count();
		if($cnt == 0){
			$tbl->insert($obj);
			return "insert!";
		} else{
			$tbl->select(['huilvDiv', '==', $obj->huilvDiv])
				->update(['huilvVal', $obj->huilvVal],['daigoufei', $obj->daigoufei]);
			return "update!";
		}
	}
	public function updateHuilvByYinglian($huilvYL){
		$dataArr = $this->listAllItem();
		$huilv = doubleval($huilvYL);
		foreach ($dataArr as $data) {
			$pp = doubleval($data["plusplus"]);
			$huilvNew = $huilv + $pp;
			$this->save($data["huilvDiv"], $data["plusplus"], $huilvNew);
		}
	}
	public function listAllItem(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function getMyhuilvByHuilvDiv($huilvDiv){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		return $tbl->select(['huilvDiv', '==', $huilvDiv])->fetch()[0];
	}
	public function listByHuilvDiv($huilvDiv){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYHUILV_INFO"));
		$dataA = $tbl->select(['huilvDiv', '==', $huilvDiv])->fetch();
		return $dataA[0]["huilvVal"];
	}
}
?>