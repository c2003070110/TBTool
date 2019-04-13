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
		$orderObj->status = $status;
		$orderObj->transferWay = $_GET['transferWay'];
		var_dump($orderObj);
		$mbUid = "";
		$tbOrdData = $tbl->select(['uid', '==', $orderObj->uid])->fetch()[0];
		if(empty($tbOrdData)){
			if($orderObj->transferWay === "zhiYou"){
				$mbUid = uniqid("mb", true);
				$orderObj->mbUid = $mbUid;
			}
			$tbl->insert($orderObj);
			
			$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
			$mbObj = new MBOrderObject();
			$mbObj->uid = $mbUid;
			$mbObj->status = "unorder";
			$tbl->insert($mbObj);
		} else{
			
			$tbl->select(['uid', '==', $orderObj->uid])
				->update(
				         ['maijia', $orderObj->maijia],
				         ['dingdanhao', $orderObj->dingdanhao],
				         ['dingdanDt', $orderObj->dingdanDt],
				         ['maijiadianzhiHanzi', $orderObj->maijiadianzhiHanzi],
				         ['status', $orderObj->status],
				         ['transferWay', $orderObj->transferWay]);
			//$mbUid = $tbOrdData["mbUid"];
			
			if($orderObj->transferWay === "zhiYou" && $tbOrdData["transferWay"] !== "zhiYou"){
				// insert 
				$mbUid = uniqid("mb", true);
				$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
				$mbObj = new MBOrderObject();
				$mbObj->uid = $mbUid;
				$mbObj->status = "unorder";
				$tbl->insert($mbObj);
			}else if($orderObj->transferWay !== "zhiYou" && $tbOrdData["transferWay"] === "zhiYou"){
				// delete
				$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
				$tbl->select("uid","==",$tbOrdData["mbUid"])->delete();
			}else{
				$mbUid = $tbOrdData["mbUid"];
			}
		}
		
		$prodliststr = $_GET['productList'];
		$prodlines = explode(";", $prodliststr);
		$prodArr = array();
		for($i = 0, $size = count($prodlines); $i < $size; ++$i) {
			list($productId,$colorName,$sizeName) = explode(",", $prodlines[$i]);
			if($productId == null)continue;
			$prodObj = new ProductObject();
			$prodObj->uid = uniqid("p", true) . $i;
			$prodObj->productId = $productId;
			$prodObj->colorName = $colorName;
			$prodObj->sizeName = $sizeName;
			$prodObj->tbUid = $tbOrderUid;
			$prodObj->mbUid = $mbUid;
			
			$prodArr[] = $prodObj;
		}
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		$tbl->select(['tbUid', '==', $orderObj->uid])->delete();
		foreach ($prodArr as $data) {
		    $tbl->insert($data);
		}
	}
	
	public function updateTBOrderStatus($tbOrderUid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		if($toStatus === "mboff"){
			$tbl->select(['uid', '==', $tbOrderUid])
				->update(['status', $toStatus]);
				
			// TODO delete MBOrder
		}else{
			$tbl->select(['uid', '==', $tbOrderUid])
				->update(['status', $toStatus]);
		}
	}
	public function listTBOrderInfoByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['uid', '==', $uid])->fetch()[0];
	}
	public function listTBOrderByMaijiaAndStatus($maijia, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['maijia', '==', $maijia],['status', '==', $status, 'and'])->fetch();
	}
	public function listTBOrderByMaijia($maijia){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['maijia', '==', $maijia])->fetch();
	}
	public function listTBOrderByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listAllTBOrder(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select("*")->fetch();
	}
	public function listTBOrderInfoByMBUid($mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_TB_ORDER_INFO"));
		
		return $tbl->select(['mbUid', '==', $mbUid,'and'],['transferWay', '==', 'zhiYou','and'])->fetch()[0];
	}
	
	
	//*********MB_ORDER ********
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
	public function updateMBOrderByMBOrder($uid, $mbOrderNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['mbOrderNo', $mbOrderNo],
					 ['status', 'ordered']);
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
	public function listAllMBOrderInfo(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_MB_ORDER_INFO"));
		
		return $tbl->select('*')->fetch();
	}
	
	
	
	
	//*********PRODUCT ********
	public function listProductInfoByMBUid($mbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		return $tbl->select(['mbUid', '==', $mbUid])->fetch();
	}
	public function listProductInfoByByTBOrderUid($tbUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_PRODUCT_INFO"));
		
		return $tbl->select(['tbUid', '==', $tbUid])->fetch();
	}
	
	
	
	
	
	
	
	
	
	
	
	public function deleteOrder($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])->delete();
	}
	public function updateOrderByMBOrder($uid, $mbOrderNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['mbOrderNo', $mbOrderNo],
					 ['status', 'ordered']);
	}
	public function updateOrderByTranfserNo($uid, $transferNoGuoji, $transferNoGuonei){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['transferNoGuoji', $transferNoGuoji],
					 ['transferNoGuonei', $transferNoGuonei],
					 ['status', 'mbfh']);
	}
	public function orderOrder($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])
			->update(['status', 'ordering']);
	}
	public function updateOrder(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		$orderObj = new OrderObject();
		
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
		/*
		// write to order in file
		$dataNew = $tbl->select(['uid', '==', $orderObj->uid])->fetch();
		$dataNew0 = $dataNew[0];
		$lines = $dataNew0["maijia"] .PHP_EOL;
		$prodSize = count($dataNew0["productObjList"]);
		//var_dump($dataNew0["productObjList"]);
		for($i = 0, $size = $prodSize; $i < $size; ++$i) {
		    $p = $dataNew0["productObjList"][$i];
			$lines .= '商家编码：MTBL_XX-' . $p["productId"] .' ';
			if($p["colorName"] == ''){
				$lines .= '颜色分类:-' ;
			}else{
				$lines .= '颜色分类:' . $p["colorName"] ;
			}
			if($p["sizeName"] == ''){
				$lines .= ';尺码:-' ;
			}else{
				$lines .= ';尺码:' . $p["sizeName"] ;
			}
			if($i != $size -1){
				$lines .= ',';
			}
		}
		$lines .= PHP_EOL;
		$lines .= $dataNew0["firstName"].' ' .$dataNew0["lastName"] .PHP_EOL;
		$lines .= $dataNew0["tel"] .PHP_EOL;
		$lines .= $dataNew0["statePY"] .PHP_EOL;
		$lines .= $dataNew0["cityPY"] .PHP_EOL;
		$lines .= $dataNew0["adr2PY"] .PHP_EOL;
		$lines .= $dataNew0["adr1PY"] .PHP_EOL;
		if($dataNew0["postcode"] == ''){
			$lines .= "000000" .PHP_EOL;
		}else{
			$lines .= $dataNew0["postcode"] .PHP_EOL;
		}
		$lines .= $dataNew0["fukuanWay"] .PHP_EOL;
		
		$fileIn = '/home/nie2019/TBTool/temp/order_in';
		$wRslt = unlink(realpath($fileIn));
		$wRslt = file_put_contents($fileIn, $lines);
		return $wRslt;
		*/
	}
	public function listOrderInfoByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		return $tbl->select(['uid', '==', $uid])->fetch()[0];
	}
	public function listItemByMaijiaAndStatus($maijia, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		return $tbl->select(['maijia', '==', $maijia],['status', '==', $status, 'and'])->fetch();
	}
	public function listOrderInfoByMaijia($maijia){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		return $tbl->select(['maijia', '==', $maijia])->fetch();
	}
	public function listOrderInfoByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listOrderByEmptyMBOrderOne($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		$dataArr = $tbl->select(['status', '==', "ordering"])->fetch();
		foreach ($dataArr as $data) {
			if(empty($data["mbOrderNo"]) &&
				(!empty($data["firstName"]) && !empty($data["lastName"]) && !empty($data["tel"])
				  && !empty($data["postcode"]) && !empty($data["statePY"]) && !empty($data["cityPY"])
				  && !empty($data["adr1PY"]) && !empty($data["adr2PY"]) && !empty($data["fukuanWay"])
				)){
				return $data;
			}
		}
		return NULL;
	}
	
	public function listAllOrderInfo(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		return $tbl->select('*')->fetch();
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