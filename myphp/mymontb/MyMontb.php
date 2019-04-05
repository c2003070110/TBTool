<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ . '/OrderInfo.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;


class MyMontb
{
	public function saveOrder($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		
		list($orderNo,$codeType,$codeCd) = explode(",", $codeCdsToSave);
		if($codeCd == null){
			$resultStr = "[DUP][codeCd IS NULL]";
			return $resultStr;
		}
		$orderObj = new OrderObject();
		
		$orderObj->uid = $_GET['uid'];
		$orderObj->maijia = $_GET['maijia'];
		$orderObj->dingdanhao = $$_GET['dingdanhao'];
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
		$orderObj->productObjList = prodArr;
		
		if(!isset($orderObj->uid) || $orderObj->uid == ''){
			$orderObj->uid = uniqid();
			$orderObj->status = 'unorder';
			$tbl->insert($orderObj);
			$resultStr = "Insert!";
			return $resultStr;
		}
		$cnt = $tbl->select(['uid', '==', $obj->uid])->count();
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
	public function orderOrder($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		$orderObj = new OrderObject();
		
		$orderObj->uid = $_GET['uid'];
		$orderObj->maijiaNamePY = $_GET['maijiaNamePY'];
		$orderObj->tel = $$_GET['tel'];
		$orderObj->postcode = $_GET['postcode'];
		$orderObj->statePY = $_GET['statePY'];
		$orderObj->cityPY = $_GET['cityPY'];
		$orderObj->adr1PY = $_GET['adr1PY'];
		$orderObj->adr2PY = $_GET['adr2PY'];
		$orderObj->fukuanWay = $_GET['fukuanWay'];
		$cnt = $tbl->select(['uid', '==', $obj->uid])->count();
		if($cnt == 0){
			$resultStr = "Not Exist!";
			return $resultStr;
		} else{
			$tbl->select(['uid', '==', $orderObj->uid])
				->update(
				         ['status', 'ordered'],
				         ['maijiaNamePY', $orderObj->maijiaNamePY],
				         ['tel', $orderObj->tel],
				         ['postcode', $orderObj->postcode],
						 ['statePY', $orderObj->statePY],
						 ['cityPY', $orderObj->cityPY],
						 ['adr1PY', $orderObj->adr1PY],
						 ['adr2PY', $orderObj->adr2PY],
						 ['fukuanWay', $orderObj->fukuanWay]);
		}
		// write to order in file
		$dataNew = $tbl->select(['uid', '==', $obj->uid])->fetch();
		$dataNew0 = $dataNew[0];
		$lines = $dataNew0["maijia"] .'\n';
		$prodSize = count($dataNew0["productObjList"]);
		for($i = 0, $size = $prodSize; $i < $size; ++$i) {
			$lines .= $dataNew0[$i]["productId"] .' ' . $dataNew0[$i]["colorName"] . ':' . $dataNew0[$i]["sizeName"];
			if($i != $size -1){
				$lines .= ',';
			}
		}
		$lines .= $dataNew0["maijiaNamePY"] .'\n';
		$lines .= $dataNew0["tel"] .'\n';
		$lines .= $dataNew0["statePY"] .'\n';
		$lines .= $dataNew0["cityPY"] .'\n';
		$lines .= $dataNew0["adr2PY"] .'\n';
		$lines .= $dataNew0["adr1PY"] .'\n';
		$lines .= $dataNew0["postcode"] .'\n';
		$lines .= $dataNew0["fukuanWay"] .'\n';
		
		$file = 'order-in.txt';
		$wRslt = file_put_contents($file, $lines);
		return $wRslt;
	}
	public function listOrderInfoByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYMONTB_ORDER_INFO"));
		
		return $tbl->select(['uid', '==', $uid])->fetch();
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
	
	public function convertHanziToPY($hanzhi){
		$fileIn = 'pinyin-in.txt';
		$fileOut = 'pinyin-out.txt';
		$wRslt = file_put_contents($fileIn, $lines);
		$wRslt = unlink(realpath($fileOut));
		
		while(true){
			if (file_exists($fileOut)) {
				break;
			}
			sleep(2);
		}
		
		$readRslt = file_get_contents($fileOut);
		return $readRslt;
	}
	
}
?>