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
		
		/*
		$lines = explode(";", $codeCdsToSave);
		var_dump($lines);
		$resultStr = "";
		$dataArr = array();
		
		for($i = 0, $size = count($lines); $i < $size; ++$i) {
			list($orderNo,$codeType,$codeCd) = explode(",", $lines[$i]);
			if($codeCd == null)continue;
			$cardObj = new GiftCardObject();
			$cardObj->orderNo = $orderNo;
			$cardObj->codeType = $codeType;
			$cardObj->codeCd = $codeCd;
			$cardObj->status = 'unused';
			$cardObj->aucId = "";
			$cardObj->obidId = "";
			$dataArr[] = $cardObj;
		}
		$continueFlag = true;
		foreach ($dataArr as $data) {
			$sameCnt = 0;
			foreach ($dataArr as $data2) {
				if($data->codeCd == $data2->codeCd){
					$sameCnt ++;
				}
			}
			if($sameCnt > 1){
				$resultStr .= "[DUP][codeType]" . $data->codeType . "[codeCd]" . $data->codeCd . "\n";
				$continueFlag = false;
			}
		}
		if($continueFlag == false){
			return $resultStr;
		}
		foreach ($dataArr as $data) {
			//$cnt = $tbl->select(['codeCd', '==', $data->codeCd],['codeType', '==', $data->codeType, 'and'])->count();
			$cnt = $tbl->select(['codeCd', '==', $data->codeCd])->count();
			if($cnt == 0){
				$tbl->insert($data);
			}else{
				$tbl->select(['codeCd', '==', $data->codeCd])
					->update(['orderNo', $data->orderNo], ['codeType', $data->codeType] );
				//$resultStr .= "[Exists][codeType]" . $data->codeType . "[codeCd]" . $data->codeCd . "\n";
				//$continueFlag = false;
			}
		}
		//if($continueFlag == false){
		//	return $resultStr;
		//}
		//foreach ($dataArr as $data) {
		//    $tbl->insert($data);
		//}
		$resultStr = "success";
		return $resultStr;
		*/
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
		
		$tbl->select(['codeCd', '==', $codeCd])->update(['status', 'used'],['aucId', $aucId],['obidId', $obidId]);
	}
	public function finishCode($aucId, $obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$data = $tbl->select(['aucId', '==', $aucId, "and"],
							 ['obidId', '==', $obidId, "and"])
					->fetch();
		if(empty($data)){
			echo "[ERROR]to be updated code was NOT Exists!"
			return;
		}
		if(count($data) != 1){
			echo "[ERROR]to be updated code was NOT ONE!"
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
		
		return $tbl->select(['status', '==', $status, 'and'],['codeType', '==', $codeType, 'and'])->fetch();
	}
	public function listStockByCodeType($codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		return $tbl->select(['codeType', '==', $codeType])->fetch();
	}
}
?>