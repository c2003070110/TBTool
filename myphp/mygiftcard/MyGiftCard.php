<?
require '../common.php';
require './GiftCardObject.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyGiftCard
{
	public function saveCode($codeCdsToSave){
		$lines = explode(";", $codeCdsToSave);
		$resultStr = "";
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
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
			if($cnt != 0){
				$resultStr .= "[Exists][codeType]" . $data->codeType . "[codeCd]" . $data->codeCd . "\n";
				$continueFlag = false;
			}
		}
		if($continueFlag == false){
			return $resultStr;
		}
		foreach ($dataArr as $data) {
		    $tbl->insert($data);
		}
		$resultStr = "success";
		return $resultStr;
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
		$cdb = new CrunchDB('./db/');
		$tbl = $cdb->table('mygiftcode.code');
		
		$tbl->select(['codeCd', '==', $codeCd])->update(['status', 'used'],['aucId', $aucId],['obidId', $obidId]);
	}
	public function listStock(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		return $tbl->select('*')->fetch();
	}
}
?>