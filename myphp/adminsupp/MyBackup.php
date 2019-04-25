<?php
require_once __DIR__ . '/../mycommon.php';
require_once __DIR__ . '/../mydefine.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyBackup
{
	public function backupMyGiftCard(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$today = date("YmdGis");
		
		$dataFin = $tbl->select(['status', '==', "fin"])->fetch();
		$dataInvalid = $tbl->select(['status', '==', "invalid"])->fetch();
		$dataAll = array_merge($dataFin, $dataInvalid);
		if(empty($dataAll)){
			return "There are NONE Data to backup";
		}
		$tblBackup = $cdb->table(constant("TBL_MYGIFTCODE_CODE") . ".backup-" . $today);
		foreach ($dataAll as $data) {
			$tblBackup->insert($data);
			$tbl->select(['uid', '==', $data["uid"]])->delete();
		}
		
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		$dataAll = $tbl->select(['status', '==', "fin"])->fetch();
		if(empty($dataAll)){
			return "There are NONE Data to backup";
		}
		$tblBackup = $cdb->table(constant("TBL_MYGIFTCODE_BID") . ".backup-" . $today);
		foreach ($dataAll as $data) {
			$tblBackup->insert($data);
			$tbl->select(['uid', '==', $data["uid"]])->delete();
		}
		return "backup success!";
	}
	public function backupMyMontbell(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tblMB = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tblMBBackup = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO") . ".backup-" . $today);
		
		$tblTB = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		$tblTBBackup = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO") . ".backup-" . $today);
		$tblProd = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$tblProdBackup = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO") . ".backup-" . $today);
		
		$today = date("YmdGis");
		
		$dataFin = $tblMB->select(['status', '==', "fin"])->fetch();
		$dataInvalid = $tblMB->select(['status', '==', "cancel"])->fetch();
		$dataAll = array_merge($dataFin, $dataInvalid);
		if(empty($dataAll)){
			return "There are NONE Data to backup";
		}
		$tbUidArr = array();
		foreach ($dataAll as $data) {
			$tblMBBackup->insert($data);
			$tblMB->select(['uid', '==', $data["uid"]])->delete();
			
			$dataProdArr = $tblProd->select(['mbUid', '==', $data["uid"]])->fetch();
			if(empty($dataProdArr)) continue;
			foreach ($dataProdArr as $dataProd) {
				$tblProdBackup->insert($data);
				$tblProd->select(['uid', '==', $dataProd["uid"]])->delete();
				
				if(!in_array($dataProd["tbUid"], $tbUidArr)){
					$tbUidArr[] = $dataProd["tbUid"];
				}
			}
		}
		foreach ($tbUidArr as $uid) {
			$data = $tblTB->select(['uid', '==', $uid])->fetch()[0];
			if(empty($data)) continue;
			$tblTBBackup->insert($data);
			$tblTB->select(['uid', '==', $data["uid"]])->delete();
		}
		
		return "backup success!";
	}
}
?>