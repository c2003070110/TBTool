<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/ObjectClass.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;


class MyMontb
{
	public function saveTBOrder(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		$orderObj = new TBOrderObject();
		$tbOrderUid = empty($_GET['uid']) ? uniqid("tb", true) : $_GET['uid'];
		$status = empty($_GET['status']) ? "st" : $_GET['status'];
		$orderObj->uid = $tbOrderUid;
		$orderObj->maijia = $_GET['maijia'];
		$orderObj->dingdanhao = $_GET['dingdanhao'];
		$orderObj->dingdanDt = $_GET['dingdanDt'];
		$orderObj->maijiadianzhiHanzi = $_GET['maijiadianzhiHanzi'];
		$orderObj->transferWay = $_GET['transferWay'];
		//var_dump($orderObj);
		$mbUid = "";
		$tbOrdData = $tbl->select(['uid', '==', $orderObj->uid])->fetch()[0];
		if(empty($tbOrdData)){
			if($orderObj->transferWay === "mbzhiYou" || $orderObj->transferWay === "wozhiYou"){
				$mbUid = uniqid("mb", true);
				$orderObj->mbUid = $mbUid;
			}
			$tbl->insert($orderObj);
			if($orderObj->transferWay === "mbzhiYou" || $orderObj->transferWay === "wozhiYou"){
				$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
				$mbObj = new MBOrderObject();
				$mbObj->uid = $mbUid;
				$mbObj->status = "unorder";
				if($orderObj->transferWay === "wozhiYou"){
					$mbObj->firstName = "tokyo";
					$mbObj->lastName = "tokyo";
					$mbObj->statePY = "tokyo";
					$mbObj->tel = "08042001314";
				}
				$tbl->insert($mbObj);
			}
		} else{
			$tbl->select(['uid', '==', $orderObj->uid])
				->update(
				         ['maijia', $orderObj->maijia],
				         ['dingdanhao', $orderObj->dingdanhao],
				         ['dingdanDt', $orderObj->dingdanDt],
				         ['maijiadianzhiHanzi', $orderObj->maijiadianzhiHanzi],
						 ['transferWay', $orderObj->transferWay]
						 );
			
			//var_dump($orderObj->transferWay);
			//var_dump($tbOrdData["transferWay"]);
			if($tbOrdData["transferWay"] === "mbzhiYou" || $tbOrdData["transferWay"] === "wozhiYou"){
				// TODO!!
				$productDataArr = $this->listProductInfoByByTBUid($tbOrdData["uid"]);
				$mbUidArr = array();
				foreach ($productDataArr as $dataProd) {
					if(!empty($dataProd["mbUid"])){
						$mbUidArr[] = $dataProd["mbUid"];
					}
				}
				foreach ($mbUidArr as $mbUid) {
					$this->deleteMBOrderByMBUid($mbUid);
				}
			}
			if(($orderObj->transferWay === "mbzhiYou" || $orderObj->transferWay === "wozhiYou")){
				// insert 
				$mbUid = uniqid("mb", true);
				$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
				$mbObj = new MBOrderObject();
				$mbObj->uid = $mbUid;
				$mbObj->status = "unorder";
				if($orderObj->transferWay === "wozhiYou"){
					$mbObj->firstName = "tokyo";
					$mbObj->lastName = "tokyo";
					$mbObj->statePY = "tokyo";
					$mbObj->tel = "08042001314";
				}
				$tbl->insert($mbObj);
			}else{
				$mbUid = "";
			}
		}
		
		$prodliststr = $_GET['productList'];
		$prodlines = explode(";", $prodliststr);
		$prodArr = array();
		for($i = 0, $size = count($prodlines); $i < $size; ++$i) {
			list($prodUid,$productId,$colorName,$sizeName) = explode(",", $prodlines[$i]);
			if($productId == null)continue;
			$prodObj = new ProductObject();
			$prodObj->uid = uniqid("p", true) . $i;
			$prodObj->productId = $productId;
			$prodObj->colorName = $colorName;
			$prodObj->sizeName = $sizeName;
			$prodObj->tbUid = $tbOrderUid;
			$prodObj->mbUid = $mbUid;
			$orderObj->status = $status;
			if(!empty($prodUid)){
				$prodInfoOld = $this->listProductInfoByByUid($prodUid);
				if(!empty($prodUid)){
					//$prodObj->mbUid = $prodInfoOld["mbUid"];
					$prodObj->priceOffTax = $prodInfoOld["priceOffTax"];
					$prodObj->stock = $prodInfoOld["stock"];
				}
			}
			$prodArr[] = $prodObj;
		}
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$tbl->select(['tbUid', '==', $orderObj->uid])->delete();
		foreach ($prodArr as $data) {
		    $tbl->insert($data);
		}
		return $orderObj->uid;
	}
	public function deleteTBOrderByTBUid($tbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		$tbData = $tbl->select(['uid', '==', $tbUid])
					  ->fetch()[0];
		if(empty($tbData)){
			return "[ERROR]TBOrder do NOT exist!uid = " . $tbUid;
		}
		$productDataArr = $this->listProductInfoByByTBUid($tbUid);
		foreach ($productDataArr as $dataProd) {
			if(!empty($dataProd["mbUid"])){
				return "[ERROR]MBOrder do exist!productUid = " . $dataProd["uid"];
			}
		}
		$tbl->select(['uid', '==', $tbUid])->delete();
		
		$this->deleteProductInfoByTBUid($tbData["tbUid"]);
	}
	public function isTBOrderForMBOrderedByTBUid($tbUid){
		$dataProdArr = $this->listProductInfoByByTBUid($tbUid);
		foreach ($dataProdArr as $dataProd) {
			if(empty($dataProd["mbUid"])){
				return false;
			}
			$dataMB = $this->listMBOrderInfoByUid($dataProd["mbUid"]);
			//var_dump($dataMB);
			if(!empty($dataMB) && $dataMB["status"] !== "unorder"){
				return true;
			}
		}
		return false;
	}
	public function listTBOrderInfoByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['uid', '==', $uid])->fetch()[0];
	}
	/*
	public function listTBOrderByMaijiaAndStatus($maijia, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['maijia', '==', $maijia],['status', '==', $status, 'and'])->fetch();
	}
	*/
	public function listTBOrderByMaijia($maijia){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['maijia', '==', $maijia])->fetch();
	}
	/*
	public function listTBOrderByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['status', '==', $status])->fetch();
	}
	*/
	public function listAllTBOrder(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select("*")->fetch();
	}
	public function listTBOrderInfoByMBUid($mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['mbUid', '==', $mbUid,'and'])->fetch()[0];
	}
	/*
	public function listTBOrderInfoByPinyou(){
		$rsltArr = array();
		$dataArr = $this->listAllTBOrder();
		foreach ($dataArr as $data) {
			if($data["transferWay"] === 'pinYou'){
				$rsltArr[] = $data;
			}
		}
		return $rsltArr;
	}
	*/
	public function listTBOrderByMbUnorder(){
		$rsltArr = array();
		$dataArr = $this->listAllTBOrder();
		foreach ($dataArr as $data) {
			$dataProdArr = $this->listProductInfoByByTBUid($data['uid']);
			foreach ($dataProdArr as $dataProd) {
				if(empty($dataProd["mbUid"])){
					$rsltArr[] = $data;
					break;
				}
				$mbData = $this->listMBOrderInfoByUid($dataProd["mbUid"]);
				if($mbData["status"] == "unorder" || $mbData["status"] == "ordering"){
					$rsltArr[] = $data;
					break;
				}
			}
		}
		return $rsltArr;
	}
	public function listTBOrderByMbOrdered(){
		$rsltArr = array();
		$dataArr = $this->listAllTBOrder();
		foreach ($dataArr as $data) {
			$dataProdArr = $this->listProductInfoByByTBUid($data['uid']);
			$mbOrderNo = "";
			foreach ($dataProdArr as $dataProd) {
				if(!empty($dataProd["mbUid"])){
					$mbData = $this->listMBOrderInfoByUid($dataProd["mbUid"]);
					if($mbData["status"] == "ordered"){
						$mbOrderNo = $mbData["mbOrderNo"];
					    break;
					}
				}
				
			}
			if(!empty($mbOrderNo)){
				$data["mbOrderNo"] = $mbOrderNo;
				$rsltArr[] = $data;
			}
		}
		return $rsltArr;
	}
	public function listTBOrderByMbFahuo(){
		$rsltArr = array();
		$dataArr = $this->listAllTBOrder();
		foreach ($dataArr as $data) {
			$dataProdArr = $this->listProductInfoByByTBUid($data['uid']);
			$mbOrderNo = "";
			$transferNoGuoji = "";
			foreach ($dataProdArr as $dataProd) {
				if(!empty($dataProd["mbUid"])){
					$mbUidFlag = false;
					$mbData = $this->listMBOrderInfoByUid($dataProd["mbUid"]);
					if($mbData["status"] == "mbfh"){
						$mbOrderNo = $mbData["mbOrderNo"];
						$transferNoGuoji = $mbData["transferNoGuoji"];
					    break;
					}
				}
			}
			if(!empty($mbOrderNo)){
				$data["mbOrderNo"] = $mbOrderNo;
				$data["transferNoGuoji"] = $transferNoGuoji;
				$rsltArr[] = $data;
			}
		}
		return $rsltArr;
	}
	
	
	//*********MB_ORDER ********
	public function deleteMBOrderByMBUid($mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $mbUid])->delete();
		
		$this->clearProductInfoForMBUidByMBUid($mbUid);
	}
	public function updateMBOrder(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		
		$orderObj = new MBOrderObject();
		
		$orderObj->uid = $_GET['uid'];
		$orderObj->firstName = $_GET['firstName'];
		$orderObj->lastName = $_GET['lastName'];
		$orderObj->tel = $_GET['tel'];
		$orderObj->postcode = $_GET['postcode'];
		$orderObj->statePY = $_GET['statePY'];
		$orderObj->cityPY = $_GET['cityPY'];
		$orderObj->adr1PY = $_GET['adr1PY'];
		$orderObj->adr2PY = $_GET['adr2PY'];
		$orderObj->fukuanWay = $_GET['fukuanWay'];
		$cnt = $tbl->select(['uid', '==', $orderObj->uid])->count();
		if($cnt == 0){
			$resultStr = "Not Exist!";
			return $resultStr;
		} else{
			$tbl->select(['uid', '==', $orderObj->uid])
				->update(
				         ['status', 'unorder'],
				         ['firstName', $orderObj->firstName],
				         ['lastName', $orderObj->lastName],
				         ['tel', $orderObj->tel],
				         ['postcode', $orderObj->postcode],
						 ['statePY', $orderObj->statePY],
						 ['cityPY', $orderObj->cityPY],
						 ['adr1PY', $orderObj->adr1PY],
						 ['adr2PY', $orderObj->adr2PY],
						 ['fukuanWay', $orderObj->fukuanWay]);
		}
		return;
	}
	public function updateMBOrderStatus($mbUid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		if($toStatus === "ordering"){
			$tbl->select(['uid', '==', $mbUid])
				->update(['status', $toStatus],
				         ['mbOrderNo', ""]);
			// TODO
			$this->updateProductInfoStatusByMBUid($mbUid, "mbordering");
		}else if($toStatus === "cancel"){
			$tbl->select(['uid', '==', $mbUid])
				->delete();
			$this->clearProductInfoForMBUidByMBUid($mbUid);
		}else if($toStatus === "ordered"){
			$tbl->select(['uid', '==', $mbUid])
				->update(['status', $toStatus]);
			$this->updateProductInfoStatusByMBUid($mbUid, "mbordered");
		}else if($toStatus === "mbfh"){
			$tbl->select(['uid', '==', $mbUid])
				->update(['status', $toStatus]);
			$this->updateProductInfoStatusByMBUid($mbUid, "mboff");
		}else if($toStatus === "fin"){
			$tbl->select(['uid', '==', $mbUid])
				->update(['status', $toStatus]);
			$this->updateProductInfoStatusByMBUid($mbUid, "fin");
				
		}else{
			$tbl->select(['uid', '==', $mbUid])
				->update(['status', $toStatus]);
		}
	}
	public function updateMBOrderByMBOrder($uid, $mbOrderNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['mbOrderNo', $mbOrderNo],
					 ['status', 'ordered']);
		$this->updateProductInfoStatusByMBUid($uid, "mbordered");
	}
	public function updateMBOrderByTranfserNo($uid, $transferNoGuoji, $transferNoGuonei){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['transferNoGuoji', $transferNoGuoji],
					 ['transferNoGuonei', $transferNoGuonei],
					 ['status', 'mbfh']);
	}
	public function orderMBOrder($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['status', 'ordering']);
	}
	public function listMBOrderInfoByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		
		return $tbl->select(['uid', '==', $uid])->fetch()[0];
	}
	public function listMBOrderInfoByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listMBOrderByEmptyMBOrderOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		
		$dataArr = $tbl->select(['status', '==', "ordering"])->fetch();
		foreach ($dataArr as $data) {
			if(empty($data["mbOrderNo"]) &&
				(!empty($data["firstName"]) && !empty($data["lastName"]) && !empty($data["tel"]))
			   /*
				(!empty($data["firstName"]) && !empty($data["lastName"]) && !empty($data["tel"])
				  && !empty($data["postcode"]) && !empty($data["statePY"]) && !empty($data["cityPY"])
				  && !empty($data["adr1PY"]) && !empty($data["adr2PY"]) && !empty($data["fukuanWay"])*/
				){
				return $data;
			}
		}
		return NULL;
	}
	public function listAllMBOrderInfo(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		
		return $tbl->select('*')->fetch();
	}
	
	
	
	
	//*********PRODUCT ********
	/*
	public function deleteProductInfoByDUSH(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$dataArr = $tbl->select("*")->fetch();
		foreach ($dataArr as $data) {
			$tb = $this->listTBOrderInfoByUid($data["tbUid"]);
			if(empty($tb)){
				var_dump($data["uid"]);
				$tbl->select(['uid', '==', $data["uid"]])->delete();
			}
		}
	}
	*/
	public function deleteProductInfoByTBUid($tbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$tbl->select(['tbUid', '==', $tbUid])->delete();
	}
	public function updateProductInfoForMBUidByUid($uid, $mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		//$data = $tbl->select(['uid', '==', $uid])->fetch()[0];
		$tbl->select(['uid', '==', $uid])
			->update(['mbUid', $mbUid]);
		// deprecated!!
		//$this->updateTBOrderForMBUidByUid($data["tbUid"], $mbUid);
	}
	public function updateProductInfoByStock($uid, $priceOffTax, $stock){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['priceOffTax', $priceOffTax],
					 ['stock', $stock]);
	}
	public function updateProductInfoStatus($uid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		if($toStatus === "mboff"){
			$tbl->select(['uid', '==', $uid])
				->update(['status', $toStatus]);
				
			$tbData = $tbl->select(['uid', '==', $uid])
						  ->fetch()[0];
			$this->deleteMBOrderByMBUid($tbData["mbUid"]);
		}else{
			$tbl->select(['uid', '==', $uid])
				->update(['status', $toStatus]);
		}
	}
	public function updateProductInfoStatusByMBUid($mbUid, $toStatus){
		$dataArr = $this->listProductInfoByMBUid($mbUid);
		foreach ($dataArr as $data) {
			$this->updateProductInfoStatus($data["uid"], $toStatus);
		}
	}
	public function clearProductInfoForMBUidByMBUid($mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$tbl->select(['mbUid', '==', $mbUid])
			->update(['mbUid', '']);
	}
	public function listProductInfoByByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		return $tbl->select(['uid', '==', $uid])->fetch()[0];
	}
	public function listProductInfoByMBUid($mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		return $tbl->select(['mbUid', '==', $mbUid])->fetch();
	}
	public function listProductInfoByByTBUid($tbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		return $tbl->select(['tbUid', '==', $tbUid])->fetch();
	}
	public function listProductInfoByEmptyPriceOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		$dataArr = $tbl->select("*")->fetch();
		//var_dump($dataArr);
		foreach ($dataArr as $data) {
			if(empty($data["priceOffTax"]) || $data["priceOffTax"] == 0){
				return $data;
			}
		}
		return NULL;
	}
	public function listProductInfoByPinyou(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$dataArr = $tbl->select("*")->fetch();
		$rsltArr = array();
		foreach ($dataArr as $dataProd) {
			if(!empty($dataProd["mbUid"])) continue;
			$tbInfo = $this->listTBOrderInfoByUid($dataProd["tbUid"]);
			//var_dump($tbInfo);
			$dataProd["maijia"] = $tbInfo["maijia"];
			$dataProd["dingdanhao"] = $tbInfo["dingdanhao"];
			$dataProd["dingdanDt"] = $tbInfo["dingdanDt"];
			//$dataProd["tbUid"] = $tbInfo["uid"];
			$dataProd["productUid"] = $dataProd["uid"];
			$rsltArr[] = $dataProd;
		}
		return $rsltArr;
	}

	// *********** Util ************
	public function makePinyou($productUidList, $pinyouType){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$mbUid = uniqid("mb", true);
		$mbObj = new MBOrderObject();
		$mbObj->uid = $mbUid;
		$mbObj->status = "unorder";
		if($pinyouType === "jp"){
			$mbObj->firstName = "tokyo";
			$mbObj->lastName = "tokyo";
			$mbObj->statePY = "tokyo";
			$mbObj->tel = "08042001314";
		}else if($pinyouType === "cnPX"){
			$mbObj->firstName = "Peng";
			$mbObj->lastName = "Juan";
			$mbObj->tel = "13879961238";
			$mbObj->postcode = "337000";
			$mbObj->statePY = "JiangXi";
			$mbObj->cityPY = "PingXiang Shi";
			$mbObj->adr1PY = "AnYuan Qu FengHuangJie JieDao";
			$mbObj->adr2PY = "GongYuan Lu ChengShi FengQing";
		}else{
		}
		$tbl->insert($mbObj);
		
		foreach ($productUidList as $uid) {
			$this->updateProductInfoForMBUidByUid($uid, $mbUid);
		}
	}
	
	public function convertHanziToPY($hanzhi){
		$fileIn = '/home/nie2019/TBTool/temp/pinyin_in';
		$fileOut = '/home/nie2019/TBTool/temp/pinyin_out';

		$wRslt = unlink(realpath($fileIn));
		$wRslt = file_put_contents($fileIn, $hanzhi);
		$wRslt = unlink(realpath($fileOut));
		$inter = 0;
		while($inter < 20){
			if (file_exists($fileOut)) {
				break;
			}
			sleep(2);
			$inter = $inter + 2;
		}
		
		$readRslt = file_get_contents($fileOut);
		return $readRslt;
	}
}
?>