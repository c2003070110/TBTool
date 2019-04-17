<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/GiftCardObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;


class MyGiftCard
{
	public function saveCode($codeCdsToSave){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		list($orderNo,$codeType,$codeCd) = explode(",", $codeCdsToSave);
		if($codeCd == null){
			$resultStr = "[DUP][codeCd IS NULL]";
			return $resultStr;
		}
		$cardObj = new GiftCardObject();
		$cardObj->uid = uniqid();
		$cardObj->orderNo = $orderNo;
		$cardObj->codeType = $codeType;
		$cardObj->codeCd = $codeCd;
		$cardObj->status = 'unused';
		$cardObj->aucId = "";
		$cardObj->obidId = "";
		$q = $tbl->select(['codeCd', '==', $cardObj->codeCd], 'and');
		$cnt = $q->count();
		if($cnt == 0){
			$tbl->insert($cardObj);
		}else{
			
			$tbl->select(['codeCd', '==', $cardObj->codeCd])
				->update(['orderNo', $cardObj->orderNo], 
				         ['codeType', $cardObj->codeType]);
		}
		$resultStr = "success";
		return $resultStr;
	}
	public function listCodeByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$dataArr = $tbl->select(['uid', '==', $uid])->fetch();
		if(empty($dataArr)){
			return NULL;
		}
		return $dataArr[0];
	}
	
	public function getCodeV2($codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$dataArr = $tbl->select(['codeType', '==', $codeType, 'and'],['status', '==', 'unused', 'and'])->fetch();
		if(!empty($dataArr)){
			$rsltCd = $dataArr[0]["codeCd"];
			$tbl->select(['codeCd', '==', $rsltCd])->update(['status', 'using']);
			return $codeType . ":" . $rsltCd;
		}
		// compose!
		$rslt = "";
		$codeCdArr = array();
		if($codeType == "PSNUSD20"){
			$dataArr = $tbl->select(['codeType', '==', 'PSNUSD10', 'and'],['status', '==', 'unused', 'and'])->fetch();
			if(count($dataArr) < 2){
				return "";
			}
			$codeCdArr[] = $dataArr[0]["codeCd"];
			$codeCdArr[] = $dataArr[1]["codeCd"];
			$rslt = "PSNUSD10:" . $codeCdArr[0]  . ";PSNUSD10:" . $codeCdArr[1];
		}else if($codeType == "PSNUSD30"){
			$dataArr1 = $tbl->select(['codeType', '==', 'PSNUSD10', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr2 = $tbl->select(['codeType', '==', 'PSNUSD20', 'and'],['status', '==', 'unused', 'and'])->fetch();
			if(count($dataArr2) > 0 &&  count($dataArr1) > 0){
				// 30 = 10+20
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr2[0]["codeCd"];
				$rslt = "PSNUSD10:" . $codeCdArr[0]  . ";PSNUSD20:" . $codeCdArr[0];
			}else if(count($dataArr1) > 2){
				// 30 = 10+10+10
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$codeCdArr[] = $dataArr1[2]["codeCd"];
				$rslt = "PSNUSD10:" . $codeCdArr[0] . ";PSNUSD10:" . $codeCdArr[1] . ";PSNUSD10:" . $codeCdArr[2];
			}
		}else if($codeType == "PSNUSD40"){
			$dataArr1 = $tbl->select(['codeType', '==', 'PSNUSD10', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr2 = $tbl->select(['codeType', '==', 'PSNUSD20', 'and'],['status', '==', 'unused', 'and'])->fetch();
			var_dump(count($dataArr1));
			var_dump(count($dataArr2));
			if(count($dataArr2) > 1){
				// 40 = 20+20
				$codeCdArr[] = $dataArr2[0]["codeCd"];
				$codeCdArr[] = $dataArr2[1]["codeCd"];
				$rslt = "PSNUSD20:" . $codeCdArr[0] . ";PSNUSD20:" . $codeCdArr[1];
			}else if(count($dataArr2) > 0 &&  count($dataArr1) > 1){
				// 40 = 10+10+20
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$codeCdArr[] = $dataArr2[0]["codeCd"];
				$rslt = "PSNUSD10:" . $codeCdArr[0] . ";PSNUSD10:" . $codeCdArr[1] . ";PSNUSD20:" . $codeCdArr[2];
			}else if(count($dataArr1) > 3){
				// 40 = 10+10+10+10
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$codeCdArr[] = $dataArr1[2]["codeCd"];
				$codeCdArr[] = $dataArr1[3]["codeCd"];
				$rslt = "PSNUSD10:" . $codeCdArr[0] . ";PSNUSD10:" . $codeCdArr[1]
    	     		 . ";PSNUSD10:" . $codeCdArr[2] . ";PSNUSD10:" . $codeCdArr[3];
			}
		}else if($codeType == "PSNUSD50"){
			$dataArr1 = $tbl->select(['codeType', '==', 'PSNUSD10', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr2 = $tbl->select(['codeType', '==', 'PSNUSD20', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr3 = $tbl->select(['codeType', '==', 'PSNUSD25', 'and'],['status', '==', 'unused', 'and'])->fetch();
			if(count($dataArr3) > 1){
				$codeCdArr[] = $dataArr3[0]["codeCd"];
				$codeCdArr[] = $dataArr3[1]["codeCd"];
				$rslt = "PSNUSD25:" . $codeCdArr[0] . ";PSNUSD25:" . $codeCdArr[1];
			}else if(count($dataArr2) > 1 && count($dataArr1)>0){
				// 50 = 20 + 20 + 10
				$codeCdArr[] = $dataArr2[0]["codeCd"];
				$codeCdArr[] = $dataArr2[1]["codeCd"];
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$rslt = "PSNUSD20:" . $codeCdArr[0] . ";PSNUSD20:" . $codeCdArr[1] . ";PSNUSD10:" . $codeCdArr[2];
			}else if(count($dataArr2) > 0 && count($dataArr1) > 2){
				// 50 = 20 + 10 + 10 + 10
				$codeCdArr[] = $dataArr2[0]["codeCd"];
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$codeCdArr[] = $dataArr1[2]["codeCd"];
				$rslt = "PSNUSD20:" . $codeCdArr[0] . ";PSNUSD10:" . $codeCdArr[1] . ";PSNUSD10:" . $codeCdArr[2] . ";PSNUSD10:" . $codeCdArr[3];
			}else if(count($dataArr2) == 0 && count($dataArr1) > 4){
				// 50 = 10 + 10 + 10 + 10 + 10
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$codeCdArr[] = $dataArr1[2]["codeCd"];
				$codeCdArr[] = $dataArr1[3]["codeCd"];
				$codeCdArr[] = $dataArr1[4]["codeCd"];
				$rslt = "PSNUSD10:" . $codeCdArr[0] . ";PSNUSD10:" . $codeCdArr[1] . ";PSNUSD10:" . $codeCdArr[2]
						. ";PSNUSD10:" . $codeCdArr[3] . ";PSNUSD10:" . $codeCdArr[4];
			}
		}else if($codeType == "PSNUSD100"){
			$dataArr1 = $tbl->select(['codeType', '==', 'PSNUSD50', 'and'],['status', '==', 'unused', 'and'])->fetch();
			if(count($dataArr1) > 1){
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$rslt = "PSNUSD50:" . $codeCdArr[0] . ";PSNUSD50:" . $codeCdArr[1];
			}
		}
		foreach ($codeCdArr as $codeCd) {
			$tbl->select(['codeCd', '==', $codeCd])->update(['status', 'using']);
		}
		//var_dump($codeCdArr);
		//var_dump($rslt);
		return $rslt;
	}
	
	public function getCode($codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$dataArr = $tbl->select(['codeType', '==', $codeType])->fetch();
		if(empty($dataArr)){
			return "";
		}
		foreach ($dataArr as $data) {
			if($data["status"] == 'unused'){
				$rsltCd = $data["codeCd"];
				$tbl->select(['codeCd', '==', $rsltCd])->update(['status', 'using']);
				return $rsltCd;
			}
		}
		return "";
	}
	public function assetCode($codeCd,$aucId,$obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		$codeCds = explode(";", $codeCd);
		if(empty($codeCds)){
			return;
		}
		foreach ($codeCds as $data) {
			$tbl->select(['codeCd', '==', $data])->update(['status', 'used'],['aucId', $aucId],['obidId', $obidId]);
		}
	}
	public function finishCode($aucId, $obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$data = $tbl->select(['aucId', '==', $aucId, "and"],
							 ['obidId', '==', $obidId, "and"])
					->fetch();
		if(empty($data)){
			echo "[ERROR]to be updated code was NOT Exists!";
			return;
		}
		if(count($data) != 1){
			echo "[ERROR]to be updated code was NOT ONE!";
			return;
		}
		$tbl->select(['aucId',  '==', $aucId,  "and"],
					 ['obidId', '==', $obidId, "and"])
			->update(['status', "fin"]);
	}
	
	public function updateStatus($codeCd, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$tbl->select(['codeCd', '==', $codeCd])->update(['status', $status]);
		$resultStr = "success";
		return $resultStr;
	}
	public function deleteCode($codeCd){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$tbl->select(['codeCd', '==', $codeCd])->delete();
		$resultStr = "success";
		return $resultStr;
	}
	public function listStock(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		return $tbl->select('*')->fetch();
	}
	public function listStockByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listStockByStatusAndCodeType($status, $codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		$rsltArr = array();
		$dataArr = $tbl->select(['status', '==', $status, 'and'],['codeType', '==', $codeType, 'and'])->fetch();
		foreach ($dataArr as $data) {
			if(strpos($data["codeType"], $codeType) !== false){
				$rsltArr[] = $data;
			}
		}
		
		return $rsltArr;
	}
	public function listStockByCodeType($codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$rsltArr = array();
		$dataArr =  $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			//var_dump(strpos($data["codeType"], $codeType));
			if(strpos($data["codeType"], $codeType) !== false){
				$rsltArr[] = $data;
			}
		}
		return $rsltArr;
	}
}
?>