<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/OrderInfo.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;


class MyMontb
{
	public function saveOrder(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		$orderObj = new OrderObject();
		
		$orderObj->uid = $_GET['uid'];
		$orderObj->maijia = $_GET['maijia'];
		$orderObj->dingdanhao = $_GET['dingdanhao'];
		$orderObj->maijiadianzhiHanzi = $_GET['maijiadianzhiHanzi'];
		$orderObj->mbOrderNo = $_GET['mbOrderNo'];
		$prodliststr = $_GET['productList'];
		
		$prodlines = explode(";", $prodliststr);
		$prodArr = array();
		
		for($i = 0, $size = count($prodlines); $i < $size; ++$i) {
			list($productId,$colorName,$sizeName) = explode(",", $prodlines[$i]);
			if($productId == null)continue;
			$prodObj = new ProductObject();
			$prodObj->productId = $productId;
			$prodObj->colorName = $colorName;
			$prodObj->sizeName = $sizeName;
			$prodArr[] = $prodObj;
		}
		$orderObj->productObjList = $prodArr;
		//var_dump($orderObj->uid);
		if(!isset($orderObj->uid) || $orderObj->uid == ''){
			$orderObj->uid = uniqid();
			$orderObj->status = 'unorder';
			$tbl->insert($orderObj);
			$resultStr = "Insert!";
			return $resultStr;
		}
		$cnt = $tbl->select(['uid', '==', $orderObj->uid])->count();
		if($cnt == 0){
			$tbl->insert($orderObj);
			$resultStr = "Insert!";
			return $resultStr;
		} else{
			$tbl->select(['uid', '==', $orderObj->uid])
				->update(
				         ['maijia', $orderObj->maijia],
				         ['dingdanhao', $orderObj->dingdanhao],
				         ['maijiadianzhiHanzi', $orderObj->maijiadianzhiHanzi],
						 ['mbOrderNo', $orderObj->mbOrderNo],
						 ['productObjList', $orderObj->productObjList]);
			$resultStr = "Update!";
			return $resultStr;
		}
	}
	public function deleteOrder($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		$tbl->select(['uid', '==', $uid])->delete();
	}
	public function orderOrder(){
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
				         ['status', 'ordered'],
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