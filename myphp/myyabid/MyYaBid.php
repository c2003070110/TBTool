<?php
require __DIR__ . '/../mycommon.php';
require __DIR__ . '/../mydefine.php';
require __DIR__ .'/ParcelObject.php';
require __DIR__ .'/BidItemObject.php';
require __DIR__ .'/BuyerObject.php';
require __DIR__ .'/../adminsupp/MyTransfee.php';
require __DIR__ .'/../adminsupp/MyHuilv.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyYaBid
{
	public function isAdmin($admin){
		return !empty($admin) && $admin === 'zzzZZZzzz';
	}
	public function getDaigoufei(){
		return 20;
	}
	public function getHuilv(){
		$myhuilv = new MyHuilv();
		return $myhuilv->listByHuilvDiv("YA");
	}
	public function getStatusName($status){
		return $status;
	}
	
	public function addBuyer($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BUYER_INFO"));
		$cnt = $tbl->select(['buyer', '==', $buyer])->count();
		if($cnt != 0){
			return "existed!";
		}
		$obj = new BuyerObject();
		$obj->uid = uniqid();
		$obj->buyer = $buyer;
		$tbl->insert($obj);
	}
	public function listAllBuyer(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BUYER_INFO"));
		return $tbl->select("*")->fetch();
	}
	
	
	//********parcel_info**********
	public function updateParcelByYunfei($buyer, $parcelUid, $guojiShoudan, $transfeeGuojiJPY, $transfeeGuonei){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		
		$transfeeGuojiCNY = intval($transfeeGuojiJPY * $huilv);
		
		$tbl->select(['uid', '==', $parcelUid])
			->update(['guojiShoudan', $guojiShoudan],
					 ['transfeeGuojiJPY', $transfeeGuojiJPY],
					 ['transfeeGuojiCNY', $transfeeGuojiCNY],
					 ['transfeeGuonei', $transfeeGuonei]);
	}
	public function updateParcelByPaidCNY($buyer, $parcelUid, $paidCNY){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		
		$nowData = $tbl->select(['uid', '==', $parcelUid])->fetch()[0];
		$paidTtlCNY = intval($nowData["paidTtlCNY"]) + intval($paidCNY);
		
		$tbl->select(['uid', '==', $parcelUid])
			->update(['paidTtlCNY', $paidTtlCNY]);
	}
	public function listParcelByBuyerAndUnParcel($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		
		$cnt = $tbl->select(['buyer', '==', $buyer,'and'],['status', '==', 'daBao','and'])->count();
		if($cnt == 0){
			$obj = new ParcelObject();
			$uid = uniqid();
			$obj->uid = $uid;
			$obj->buyer = $buyer;
			$obj->status = 'daBao';
			$tbl->insert($obj);
			$objA = $tbl->select(['uid', '==', $uid])->fetch();
			$obj = $objA[0];
		} else{
			$objA = $tbl->select(['buyer', '==', $buyer,'and'],['status', '==', 'daBao','and'])->fetch();
			$obj = $objA[0];
		}
		return $obj;
	}
	
	//*******item_info*******
	public function addMyBid($buyer, $urllist){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' .$buyer);
		$rsltstr = "";
		$idx = 0;
		$insertArr = array();
		foreach ($urllist as $url) {
			$cnt = $tbl->select(['itemUrl', '==', $url])->count();
			if($cnt != 0){
				$rsltstr .= "[ERROR][existed]" . $url . "\n";
				continue;
			}
			$obj = new BidItemObject();
			$obj->uid = uniqid(). strval(idx++);
			$obj->buyer = $buyer;
			$obj->status = 'paiBf';
			$obj->itemUrl = $url;
			$insertArr[] = $obj;
			$rsltstr .= "[SUCCESS]" . $url . "\n";
		}
		foreach ($insertArr as $a) {
			$tbl->insert($a);
		}
		return $rsltstr;
	}
	public function updateItemStatus($buyer, $itemUid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		if($toStatus === "cancel"){
			$tbl->select(['uid', '==', $itemUid])
				->delete();
		}else if($toStatus === "rubao"){
			$parcelObj = $this->listParcelByBuyerAndUnParcel($buyer);
			$itemObj = $tbl->select(['uid', '==', $itemUid])->fetch()[0];
			
			$itemTtlPriceJPY = intval($parcelObj["itemTtlJPY"]) + intval($itemObj["priceJPY"]);
			$itemTtlPriceCNY = intval($parcelObj["itemTtlCNY"]) + intval($itemObj["priceCNY"]);
			$itemTransfeeDaoneiTtlJPY = intval($parcelObj["itemTransfeeDaoneiTtlJPY"]) + intval($itemObj["transfeeDaoneiJPY"]);
			$itemTransfeeDaoneiTtlCNY = intval($parcelObj["itemTransfeeDaoneiTtlCNY"]) + intval($itemObj["transfeeDaoneiCNY"]);
			
			$daigoufeiTtlCNY = intval($parcelObj["daigoufeiTtlCNY"]) + intval($itemObj["daigoufeiCNY"]);
			$itemTtlCNY = intval($parcelObj["itemTtlCNY"]) + intval($itemObj["itemCNY"]);
			
			$itemTtlWeight = intval($parcelObj["itemTtlWeight"]) + intval($itemObj["weight"]);
			
			$tbl->select(['uid', '==', $itemUid])
				->update(['status', $toStatus],
				         ['parcelUid', $parcelObj["uid"]]);
						 
		    $tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
			$tbl->select(['uid', '==', $parcelObj["uid"]])
				->update(['itemTtlPriceJPY', $itemTtlPriceJPY],
				         ['itemTtlPriceCNY', $itemTtlPriceCNY],
				         ['itemTransfeeDaoneiTtlJPY', $itemTransfeeDaoneiTtlJPY],
				         ['itemTransfeeDaoneiTtlCNY', $itemTransfeeDaoneiTtlCNY],
				         ['daigoufeiTtlCNY', $daigoufeiTtlCNY],
				         ['itemTtlCNY', $itemTtlCNY],
				         ['itemTtlWeight', $itemTtlWeight]);
		}else{
			$tbl->select(['uid', '==', $itemUid])
				->update(['status', $toStatus]);
		}
	}
	public function updateItemPrice($buyer, $itemUid, $priceJPY, $transfeeDaoneiJPY, $weight){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		
		$huilv = $this->getHuilv();
		$daigoufei = $this->getDaigoufei();
		$priceCNY = intval($priceJPY * $huilv);
		$transfeeDaoneiCNY = intval($transfeeDaoneiJPY * $huilv);
		$itemCNY = $priceCNY + $transfeeDaoneiCNY + $daigoufei;
		$tbl->select(['uid', '==', $itemUid])
		    ->update(['priceJPY', $priceJPY],
			         ['priceCNY', $priceCNY],
			         ['transfeeDaoneiJPY', $transfeeDaoneiJPY],
			         ['transfeeDaoneiCNY', $transfeeDaoneiCNY],
			         ['daigoufeiCNY', $daigoufei],
			         ['itemCNY', $itemCNY],
					 ['weight', $weight]);
	}
	
	public function listItemByBuyerAndItemUid($buyer, $itemUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		$rsltArr = [];
		
		$objA = $tbl->select(['uid', '==', $itemUid,'and'])->fetch();
		//if(!isset($objA)){
		//	return NULL;
		//}
		return $objA[0];
	}
	public function listItemByBuyerAndUnParcel($buyer, $parcelUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		
		return $tbl->select(['buyer', '==', $buyer,'and'],
		                     ['parcelUid', '==', $parcelUid,'and'])
					->fetch();
	}
	public function listItemByBuyer($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' .$buyer);
		
		$objA = $tbl->select(['buyer', '==', $buyer])->fetch();
		return $objA;
	}
	public function listItemByBuyerAndStatus($buyer, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' .$buyer);
		$rsltArr = [];
		
		$objA = $tbl->select(['buyer', '==', $buyer, 'and'], ['status', '==', $status])->fetch();
		return $objA;
	}
	public function listItemByAll($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BUYER_INFO"));
		$rsltArr = [];
		$objA = $tbl->select("*")->fetch();
		foreach ($objA as $dataA) {
			$tblI = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' .$dataA["buyer"]);
			$objB = $tblI->select("*")->fetch();
			foreach ($objB as $dataB) {
				if(!isset($status) || $status == $dataB["status"]){
					$rsltArr[] = $dataB;
				}
			}
		}
		return $rsltArr;
	}
	
	//********taobao_dingdan_info*********
	public function listTaobaoDingdanByParcel($buyer, $parcelUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_TAOBAO_DINGDAN_INFO").'_'.$buyer);
		return $tbl->select("*")->fetch();
	}
	public function addTaobaoDingdan($buyer, $parcelUid, $taobaoDingdanhao, $taobaoDingdanCNY){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_TAOBAO_DINGDAN_INFO").'_'.$buyer);
		$obj = array();
		$obj["uid"] = uniqid();
		$obj["buyer"] = $buyer;
		$obj["parcelUid"] = $parcelUid;
		$obj["taobaoDingdanhao"] = $taobaoDingdanhao;
		$obj["taobaoDingdanCNY"] = $taobaoDingdanCNY;
		$tbl->insert($obj);
		
		$this->updateParcelByPaidCNY($buyer, $parcelUid, $taobaoDingdanCNY);
	}
	public function deleteTaobaoDingdan($buyer, $parcelUid, $taobaodingdanUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_TAOBAO_DINGDAN_INFO").'_'.$buyer);
		
		$nowData = $tbl->select(['uid', '==', $taobaodingdanUid])->fetch()[0];
		$paidCNY = intval($nowData["taobaoDingdanCNY"]) * -1;
		
		$tbl->select(['uid', '==', $taobaodingdanUid])->delete();
		
		$this->updateParcelByPaidCNY($buyer, $parcelUid, $paidCNY);
	}
	
	
	
	/*
	public function saveItem($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		if(!isset($obj->orderDate) || $obj->orderDate == ''){
			$obj->orderDate = date( "Ymd" );
		}
		if(!isset($obj->buyer) || $obj->buyer == ''){
			$obj->uid = null;
			$obj->status = 'unasign';
		}
		if(!isset($obj->uid) || $obj->uid == ''){
			if(isset($obj->qtty) && $obj->qtty != ''){
				$qtty = intval($obj->qtty);
				for ($i = 1; $i <= $qtty; $i++) {
					$insetObj = clone $obj;
					$insetObj->uid = uniqid() . strval($i);
					$insetObj->qtty = '1';
					//var_dump($insetObj);
					$rslt = $tbl->insert($insetObj);
                }
				return ;
			}
			$obj->uid = uniqid();
			$obj->qtty = '1';
			$tbl->insert($obj);
			return;
		}
		$cnt = $tbl->select(['uid', '==', $obj->uid])->count();
		if($cnt == 0){
			$tbl->insert($obj);
		} else{
			$tbl->select(['uid', '==', $obj->uid])
				->update(
				         ['buyer', $obj->buyer],
				         ['orderItem', $obj->orderItem],
				         ['status', $obj->status],
						 ['priceJPY', $obj->priceJPY],
						 ['priceCNY', $obj->priceCNY]);
		}
	}
	public function deleteItem($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		if(!isset($obj->uid) || $obj->uid == ''){
			return;
		}
		$dataArr = $tbl->select(['uid', '==', $obj->uid])->fetch();
		$data = $dataArr[0];
		if($data['status'] =='unasign'){
			$tbl->select(['uid', '==', $obj->uid])
				->delete();
		}else if($data['status'] =='unGou'){
			$tbl->select(['uid', '==', $obj->uid])
				->delete();
		}else if($data['status'] =='gouru' || $data['status'] =='zaitu' 
		          || $data['status'] =='fahuo' || $data['status'] =='compl'){
			$tbl->select(['uid', '==', $obj->uid])
				->update(['status', 'gouru'],
						 ['buyer', '']);
		}
		
	}
	public function updateItemStatus($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		
		if(!isset($obj->uid) || $obj->uid == ''){
			return;
		}
		
		$tbl->select(['uid', '==', $obj->uid])
			->update(['status', $obj->status]);
	}
	public function listAllItem(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function listItemByBuyer($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['buyer', '==', $buyer])->fetch();
		return $data;
	}
	public function listItemByStatus($status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['status', '==', $status])->fetch();
		return $data;
	}
	public function listItemByBuyerAndStatus($buyer, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['buyer', '==', $buyer, 'and'],['status', '==', $status, 'and'])->fetch();
		return $data;
	}
	public function listItemByUnAsign(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['status', '==', 'unasign', 'and'])->fetch();
		return $data;
	}
	public function listItemByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_ITEM_INFO"));
		$data = $tbl->select(['uid', '==', $uid])->fetch();
		return $data[0];
	}
	
	
	public function saveBuyer($obj){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		if(!isset($obj->uid) || $obj->uid == ''){
			$obj->uid = uniqid();
			$tbl->insert($obj);
			return "[success]insert!";
		}
		$tbl->select(['uid', '==', $obj->uid, 'and'])
		    ->update(
					 ['buyer', $obj->buyer],
					 ['address', $obj->address]);
		return "[success]update!";
	}
	public function listAllBuyer(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		$data = $tbl->select('*')->fetch();
		return $data;
	}
	public function listBuyerByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYDAIGOU_BUYER_INFO"));
		$data = $tbl->select(['uid', '==', $uid])->fetch();
		return $data[0];
	}
	*/
}
?>