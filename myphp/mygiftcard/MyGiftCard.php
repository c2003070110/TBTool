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
		$cardObj->dtReg = date("YmdGis");
		$cardObj->bidId = "";
		$cardObj->obidId = "";
		$q = $tbl->select(['codeCd', '==', $cardObj->codeCd], 'and');
		$cnt = $q->count();
		if($cnt == 0){
			$tbl->insert($cardObj);
			$resultStr = "[INSERT]success";
		}else{
			$tbl->select(['codeCd', '==', $cardObj->codeCd])
				->update(['orderNo', $cardObj->orderNo], 
				         ['codeType', $cardObj->codeType]);
			$resultStr = "[UPDATE]success";
		}
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
	
	public function addBid($bidId, $obidId, $codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		$data = $tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])->fetch();
		if(!empty($data)){
			return;
		}
		
		$data = new BidObject();
		$data->uid = uniqid();
		$data->bidId = $bidId;
		$data->obidId = $obidId;
		$data->codeType = $codeType;
		$data->status = 'bided';
		$data->dtAdd = date("YmdGis");
		
		$tbl->insert($data);
	}
	public function addBidMsg($bidId, $obidId, $msg){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		if(empty($bidId) || empty($obidId) || empty($msg)){
			return;
		}
		$data = $tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])->fetch()[0];
		if(empty($data)){
			return;
		}
		$dtMsg = date("YmdGis");
		$tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])
			->update(['msg', $msg],['msgStatus', "wait"],['dtMsg', $dtMsg]);
	}
	public function deleteBid($bidId, $obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		$tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])
			->delete();
	}
	public function updateBidStatus($bidId, $obidId,$toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		if(empty($bidId) || empty($obidId)){
			return;
		}
		$tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])
			->update(['status', $toStatus]);
	}
	public function updateBidMsgReply($bidId, $obidId, $replyMsg){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		if(empty($bidId) || empty($obidId) || empty($replyMsg)){
			return;
		}
		$tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])
			->update(['replymsg', $replyMsg],['msgStatus', "aplied"]);
	}
	public function updateBidMsgStatus($bidId, $obidId, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		if(empty($bidId) || empty($obidId) || empty($toStatus)){
			return;
		}
		if($toStatus == "sent" || $toStatus == "ignore"){
			$tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])
				->update(['msgStatus', $toStatus],['replymsg', ""]);
		}else{
			$tbl->select(['bidId', '==', $bidId, 'and'],['obidId', '==', $obidId, 'and'])
				->update(['msgStatus', $toStatus]);
		}
	}
	public function getAplyBidMsgOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		$data = $tbl->select(['msgStatus', '==', "aplied"])->fetch()[0];
		if(empty($data)){
			return "";
		}
		return $data;
	}
	public function listAllBid(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		return $tbl->select('*')->fetch();
	}
	public function listBidByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listBidByMsgStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_BID"));
		
		if(!empty($status)){
			return $tbl->select(['msgStatus', '==', $status,'and'],['msg', '!==', ""])->fetch();
		}else{
			return $tbl->select(['msg','!==', ""])->fetch();
		}
	}
	
	
	
	public function getCodeV2($codeType,$bidId, $obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$this->updateBidStatus($bidId, $obidId, 'paid');
		$dtGot = date("YmdGis");
		$dataArr = $tbl->select(['codeType', '==', $codeType, 'and'],['status', '==', 'unused', 'and'])->fetch();
		if(!empty($dataArr)){
			$rsltCd = $dataArr[0]["codeCd"];
			$tbl->select(['codeCd', '==', $rsltCd])
			    ->update(['status', 'using'],['dtGot', $dtGot],
				         ['bidId', $bidId],['obidId', $obidId]);
			$this->updateBidStatus($bidId, $obidId, 'sent');
			
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
			
		/*}else if($codeType == "PSNUSD30"){
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
			}else{
			}
		*/}else if($codeType == "PSNUSD40"){
			$dataArr1 = $tbl->select(['codeType', '==', 'PSNUSD10', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr2 = $tbl->select(['codeType', '==', 'PSNUSD20', 'and'],['status', '==', 'unused', 'and'])->fetch();
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
			/*}else if(count($dataArr1) > 3){
				// 40 = 10+10+10+10
				$codeCdArr[] = $dataArr1[0]["codeCd"];
				$codeCdArr[] = $dataArr1[1]["codeCd"];
				$codeCdArr[] = $dataArr1[2]["codeCd"];
				$codeCdArr[] = $dataArr1[3]["codeCd"];
				$rslt = "PSNUSD10:" . $codeCdArr[0] . ";PSNUSD10:" . $codeCdArr[1]
    	     		 . ";PSNUSD10:" . $codeCdArr[2] . ";PSNUSD10:" . $codeCdArr[3];
			*/}else{
			}
		}else if($codeType == "PSNUSD50"){
			$dataArr1 = $tbl->select(['codeType', '==', 'PSNUSD10', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr2 = $tbl->select(['codeType', '==', 'PSNUSD20', 'and'],['status', '==', 'unused', 'and'])->fetch();
			$dataArr3 = $tbl->select(['codeType', '==', 'PSNUSD25', 'and'],['status', '==', 'unused', 'and'])->fetch();
			if(count($dataArr3) > 1){
				// 50 = 25 + 25
				$codeCdArr[] = $dataArr3[0]["codeCd"];
				$codeCdArr[] = $dataArr3[1]["codeCd"];
				$rslt = "PSNUSD25:" . $codeCdArr[0] . ";PSNUSD25:" . $codeCdArr[1];
			/*}else if(count($dataArr2) > 1 && count($dataArr1)>0){
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
			*/
			}else{
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
			$tbl->select(['codeCd', '==', $codeCd])
			    ->update(['status', 'using'],['dtGot', $dtGot],
				         ['bidId', $bidId],['obidId', $obidId]);
		}
		if(!empty($codeCdArr)){
			$this->updateBidStatus($bidId, $obidId, 'sent');
		}
		//var_dump($codeCdArr);
		//var_dump($rslt);
		return $rslt;
	}
	
	public function stockCheck($codeType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$dataArr = $tbl->select(['codeType', '==', $codeType])->fetch();
		if(empty($dataArr)){
			return "false";
		}
		return "true";
	}
	
	//***deprecated
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
	public function assetCode($codeCd,$bidId,$obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		$codeCds = explode(";", $codeCd);
		if(empty($codeCds)){
			return;
		}
		$dtAsset = date("YmdGis");
		foreach ($codeCds as $data) {
			$tbl->select(['codeCd', '==', $data])
			    ->update(['status', 'used'],
				         ['bidId', $bidId],
						 ['obidId', $obidId],
						 ['dtAsset', $dtAsset]);
		}
		$this->updateBidStatus($bidId, $obidId, 'sent');
	}
	public function finishCode($bidId, $obidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYGIFTCODE_CODE"));
		
		$data = $tbl->select(['bidId', '==', $bidId, "and"],
							 ['obidId', '==', $obidId, "and"])
					->fetch();
		if(empty($data)){
			echo "[ERROR]to be updated code was NOT Exists!";
			return;
		}
		$dtFinish = date("YmdGis");
		$tbl->select(['bidId',  '==', $bidId,  "and"],
					 ['obidId', '==', $obidId, "and"])
			->update(['status', "fin"],['dtFinish', $dtFinish]);
		$this->updateBidStatus($bidId, $obidId, 'fin');
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
		//$dataArr = $tbl->select(['status', '==', $status, 'and'],['codeType', '==', $codeType, 'and'])->fetch();
		$dataArr = $tbl->select(['status', '==', $status, 'and'])->fetch();
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