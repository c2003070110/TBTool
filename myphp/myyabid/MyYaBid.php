<?php
require_once __DIR__ . '/../mycommon.php';
require_once __DIR__ . '/../mydefine.php';
require_once __DIR__ .'/ParcelObject.php';
require_once __DIR__ .'/BidItemObject.php';
require_once __DIR__ .'/BuyerObject.php';
require_once __DIR__ .'/TaobaoDingDanObject.php';
require_once __DIR__ .'/../adminsupp/MyTransfee.php';
require_once __DIR__ .'/../adminsupp/MyHuilv.php';

use cybrox\crunchdb\CrunchDB as CrunchDB;

class MyYaBid
{
	public function getAdminIdentifier(){
		return 'zzzZZZzzz';
	}
	public function isAdmin($admin){
		return !empty($admin) && $admin === $this->getAdminIdentifier();
	}
	public function getDaigoufei(){
		return 20;
	}
	public function getGuojiYunfei($weigth, $guojiShoudan){
		$my = new MyTransfee();
		return $my->getGuojiYunfei($weigth, $guojiShoudan);
	}
	public function getGuoneiYunfei($weigth, $guojiShoudan){
		$my = new MyTransfee();
		return $my->getGuoneiYunfei($weigth, $guojiShoudan);
	}
	public function getHuilv(){
		$myhuilv = new MyHuilv();
		return $myhuilv->listByHuilvDiv("YA");
	}
	public function getStatusName($status){
		if($status == "paiBf"){
			return "paiBf";
		}else if($status == "paiing"){
			return "paiing";
		}else if($status == "depai"){
			return "depai";
		}else if($status == "liupai"){
			return "liupai";
		}else if($status == "fuk"){
			return "fuk";
		}else if($status == "bdfh"){
			return "bdfh";
		}else if($status == "bddao"){
			return "bddao";
		}else if($status == "rubao"){
			return "rubao";
		}else if($status == "dabao"){
			return "dabao";
		}else if($status == "zaitu"){
			return "zaitu";
		}else if($status == "fin"){
			return "fin";
		}else if($status == "cancel"){
			return "cancel";
		}
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
	public function updateParcelByGuojiFahuo($buyer, $parcelUid, $transferNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
				
		$tbl->select(['uid', '==', $parcelUid])
			->update(['transnoGuoji', $transferNo],
			         ['status', 'zaiTu']);
			
		$this->updateItemStatusByBuyerAndParcel($buyer, $parcelUid,"zaitu");
		
		$this->updateTaobaoDingdanStatusByBuyerAndParcel($buyer, $parcelUid,"used");
	}
	public function updateParcelByGuoneiFahuo($buyer, $parcelUid, $transferNo){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
				
		$tbl->select(['uid', '==', $parcelUid])
			->update(['transnoGuonei', $transferNo],
			         ['status', 'guonneiFh']);
	}
	public function updateParcelByPaidCNY($buyer, $parcelUid, $paidCNY){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		
		$nowData = $tbl->select(['uid', '==', $parcelUid])->fetch()[0];
		$paidTtlCNY = intval($nowData["paidTtlCNY"]) + intval($paidCNY);
		
		$tbl->select(['uid', '==', $parcelUid])
			->update(['paidTtlCNY', $paidTtlCNY]);
	}
	public function calcParcelPrice($buyer, $parcelUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		
		$parcelData = $tbl->select(['uid', '==', $parcelUid])->fetch()[0];
		
		$itemTtlPriceJPY = 0;$itemTtlPriceCNY = 0;		
		$itemTtlWeight = 0;		
		$itemTransfeeDaoneiTtlJPY = 0;$itemTransfeeDaoneiTtlCNY = 0;		
		$itemTtlCNY = 0;$daigoufeiTtlCNY = 0;
		
		$itemDataArr = $this->listItemByBuyerAndParcelUid($buyer, $parcelUid);
		foreach ($itemDataArr as $itemObj) {
			$itemTtlPriceJPY = $itemTtlPriceJPY + intval($itemObj["priceJPY"]);
			$itemTtlPriceCNY = intval($itemTtlPriceCNY) + intval($itemObj["priceCNY"]);
			$itemTransfeeDaoneiTtlJPY = intval($itemTransfeeDaoneiTtlJPY) + intval($itemObj["transfeeDaoneiJPY"]);
			$itemTransfeeDaoneiTtlCNY = intval($itemTransfeeDaoneiTtlCNY) + intval($itemObj["transfeeDaoneiCNY"]);
			
			$daigoufeiTtlCNY = intval($daigoufeiTtlCNY) + intval($itemObj["daigoufeiCNY"]);
			$itemTtlCNY = intval($itemTtlCNY) + intval($itemObj["itemCNY"]);
			
			$itemTtlWeight = intval($itemTtlWeight) + intval($itemObj["weight"]);
		}
		
		$paidTtlCNY = 0;
		$tbDataArr = $this->listTaobaoDingdanByParcel($buyer, $parcelUid);
		foreach ($tbDataArr as $tbObj) {
			$paidTtlCNY = intval($paidTtlCNY) + intval($tbObj["taobaoDingdanCNY"]);
		}
		
		$tbl->select(['uid', '==', $parcelUid])
			->update(['itemTtlPriceJPY', $itemTtlPriceJPY],
			         ['itemTtlPriceCNY', $itemTtlPriceCNY],
			         ['itemTtlWeight', $itemTtlWeight],
			         ['itemTransfeeDaoneiTtlJPY', $itemTransfeeDaoneiTtlJPY],
			         ['itemTransfeeDaoneiTtlCNY', $itemTransfeeDaoneiTtlCNY],
			         ['daigoufeiTtlCNY', $daigoufeiTtlCNY],
			         ['itemTtlCNY', $itemTtlCNY],
			         ['paidTtlCNY', $paidTtlCNY]);
	}
	public function updateParcelByAmt($buyer, $parcelUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		
		$parcelData = $tbl->select(['uid', '==', $parcelUid])->fetch()[0];
		$huilv = $this->getHuilv();
		
		$guojiShoudanNew = $_GET["guojiShoudan"];
		$itemTtlWeightNew = $_GET["itemTtlWeight"];
		//var_dump($guojiShoudanNew);
		$transfeeGuojiJPY = $_GET["transfeeGuojiJPY"];
		$transfeeGuojiCNY = $_GET["transfeeGuojiCNY"];
		$transfeeGuonei = $_GET["transfeeGuonei"];
		if(($guojiShoudanNew !== $parcelData["guojiShoudan"])
			|| ($itemTtlWeightNew !== $parcelData["itemTtlWeight"])){
			$transfeeGuojiJPY = $this->getGuojiYunfei($itemTtlWeightNew, $guojiShoudanNew);
			$transfeeGuojiCNY = intval($transfeeGuojiJPY * $huilv);
			$transfeeGuonei = $this->getGuoneiYunfei($itemTtlWeightNew, $guojiShoudanNew);
		}
		
		$tbl->select(['uid', '==', $parcelUid])
			->update(
			         ['guojiShoudan', $guojiShoudanNew],
			         ['itemTtlWeight', $itemTtlWeightNew],
			         ['dabaofeiCNY', $_GET["dabaofeiCNY"]],
			         ['transfeeGuojiJPY', $transfeeGuojiJPY],
			         ['transfeeGuojiCNY', $transfeeGuojiCNY],
			         ['transfeeGuonei', $transfeeGuonei],
			         ['barginCNY', $_GET["barginCNY"]]
					 );
	}
	public function listParcelByBuyer($buyer){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		return $tbl->select('*')->fetch();
	}
	public function listParcelByBuyerAndStatus($buyer, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listParcelByAll($buyer, $status){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		return $tbl->select(['status', '==', $status])->fetch();
	}
	public function listParcelByBuyerAndUid($buyer, $parcelUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_PARCEL_INFO") . '_' .$buyer);
		return $tbl->select(['uid', '==', $parcelUid])->fetch()[0];
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
			$obj->uid = uniqid() . strval($idx++);
			
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
			
			$tbl->select(['uid', '==', $itemUid])
				->update(['status', $toStatus],
				         ['parcelUid', $parcelObj["uid"]]);
			
			$this->calcParcelPrice($buyer, $parcelObj["uid"]);
		}else{
			$tbl->select(['uid', '==', $itemUid])
				->update(['status', $toStatus]);
		}
	}
	public function updateItemStatusByBuyerAndParcel($buyer, $parcelUid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		return $tbl->select(['buyer', '==', $buyer, 'and'],
		                    ['parcelUid', '==', $parcelUid])
					->update(['status', $toStatus]);
	}
	public function updateItemPrice($buyer, $itemUid, $priceJPY, $transfeeDaoneiJPY, $weight){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		
		$oldData = $tbl->select(['uid', '==', $itemUid])->fetch()[0];
		//var_dump($oldData);
		$huilv = $this->getHuilv();
		$daigoufei = $this->getDaigoufei();
		
		$priceJPYNew = $oldData["priceJPY"];
		$priceCNYNew = $oldData["priceCNY"];
		if(!empty($priceJPY) && $priceJPY !=='稍后录入'){
			$priceJPYNew = $priceJPY;
			$priceCNYNew = intval($priceJPYNew * $huilv);
		}
		$transfeeDaoneiJPYNew = $oldData["transfeeDaoneiJPY"];
		//var_dump($transfeeDaoneiJPYNew);
		$transfeeDaoneiCNYNew = $oldData["transfeeDaoneiCNY"];
		if(!empty($transfeeDaoneiJPY) && $transfeeDaoneiJPY !=='稍后录入'){
			$transfeeDaoneiJPYNew = $transfeeDaoneiJPY;
			$transfeeDaoneiCNYNew = intval($transfeeDaoneiJPYNew * $huilv);
		}
		//var_dump($transfeeDaoneiJPYNew);
		$weightNew = $oldData["weight"];
		if(!empty($weight) && $weight !=='稍后录入'){
			$weightNew = $weight;
		}
		
		$itemCNYNew = $oldData["itemCNY"];
		if(!empty($priceJPY) && $priceJPY !=='稍后录入' 
			&& !empty($transfeeDaoneiJPY) && $transfeeDaoneiJPY !=='稍后录入'){
			$itemCNYNew = $priceCNYNew + $transfeeDaoneiCNYNew + $daigoufei;
		}
		
		$tbl->select(['uid', '==', $itemUid])
		    ->update(['priceJPY', $priceJPYNew],
			         ['priceCNY', $priceCNYNew],
			         ['transfeeDaoneiJPY', $transfeeDaoneiJPYNew],
			         ['transfeeDaoneiCNY', $transfeeDaoneiCNYNew],
			         ['daigoufeiCNY', $daigoufei],
			         ['itemCNY', $itemCNYNew],
					 ['weight', $weightNew]);
	}
	public function updateItemByEstimatePrice($buyer, $itemUid, $estimateJPY){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		
		$tbl->select(['bidUid', '==', $bidUid])
			->update(['estimateJPY', $estimateJPY]);
	}
	public function updateItemStatusByBidId($bidId, $toStatus){
		$bidObj = $this->listBidByBidId($bidId);
		if(empty($bidObj)){
			return;
		}
		$itemObj = $this->listItemByBidUid($bidObj["uid"]);
		if(empty($itemObj)){
			return;
		}
		$this->updateItemStatus($itemObj["buyer"], $itemObj["uid"], $toStatus)
	}
	public function updateItemPriceByBidId($bidId){
		$bidObj = $this->listBidByBidId($bidId);
		if(empty($bidObj)){
			return;
		}
		$itemObj = $this->listItemByBidUid($bidObj["uid"]);
		if(empty($itemObj)){
			return;
		}
		$this->updateItemStatus($itemObj["buyer"], $itemObj["uid"], $_GET["priceJPY"], $_GET["transfeeDaoneiJPY"], null);
	}
	public function updateItemNameByBuyerUid($buyer, $itemUid, $itemName){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		
		$tbl->select(['uid', '==', $itemUid])
			->update(['itemName', $itemName]);
	}
	
	public function listItemByBidUid($bidUid){
		$buyerAll = $this->listAllBuyer();
		foreach ($buyerAll as $buyerObj) {
			$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyerObj["buyer"]);
			$objA = $tbl->select(['*'])->fetch();
			foreach ($objA as $itemObj) {
				if(!empty(itemObj["bidUid"]) && itemObj["bidUid"] == $bidUid){
					return itemObj;
				}
			}
		}
		return array();
	}
	public function listItemByEmptyBidUidOne(){
		$buyerAll = $this->listAllBuyer();
		foreach ($buyerAll as $buyerObj) {
			$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyerObj["buyer"]);
			$objA = $tbl->select(['*'])->fetch();
			foreach ($objA as $itemObj) {
				if(empty(itemObj["bidUid"])){
					return itemObj;
				}
			}
		}
		return array();
	}
	public function listItemByEmptyPriceOne(){
		$buyerAll = $this->listAllBuyer();
		foreach ($buyerAll as $buyerObj) {
			$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyerObj["buyer"]);
			$objA = $tbl->select(['*'])->fetch();
			foreach ($objA as $itemObj) {
				if(itemObj["priceJPY"] == '稍后录入'){
					return itemObj["itemUrl"];
				}
				if(itemObj["transfeeDaoneiJPY"] == '稍后录入'){
					return itemObj["itemUrl"];
				}
				if(itemObj["weight"] == '稍后录入'){
					return itemObj["itemUrl"];
				}
			}
		}
		return "";
	}
	public function listItemByBuyerAndItemUid($buyer, $itemUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' . $buyer);
		$rsltArr = [];
		
		$objA = $tbl->select(['uid', '==', $itemUid,'and'])->fetch();
	
		return $objA[0];
	}
	public function listItemByBuyerAndParcelUid($buyer, $parcelUid){
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
	public function listItemByItemUrl($itemUrl){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_ITEM_INFO") . '_' .$buyer);
		
		$objA = $tbl->select(['itemUrl', '==', $itemUrl])->fetch();
		return $objA[0];
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
	
	//********bid info*********
	public function insertBidObject($itemUrl, $bidId){
		// TODO
		$itemObj = $this->listItemByItemUrl($itemUrl);
		if(empty($itemObj)) return;
		
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BID_INFO"));
		
		$obj = new BidObject();
		$obj->uid = uniqid();
		
		$obj->buyer = $itemObj["buyer"];
		$obj->bidId = $bidId;
		$obj->bidUrl = $itemUrl;
		$obj->bidName = $_GET("bidName");
		
		$obj->obiderId = $_GET("obiderId");
		$obj->bidFinishDt = $_GET("bidFinishDt");
		
		$tbl->insert($obj);
		
		$this->updateItemNameByBuyerUid($itemObj["buyer"],$itemObj["uid"],$obj->bidName);
	}
	public function updateBidByObiderAdr($bidId, $obiderAdr){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BID_INFO"));
		
		return $tbl->select(['bidId', '==', $bidId])
		           ->update(["obiderAddr", $obiderAdr]);
	}
	public function updateBidByObiderMsg($bidId, $obiderMsg){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BID_INFO"));
		
		return $tbl->select(['bidId', '==', $bidId])
		           ->update(["obiderMsg", $obiderMsg]);
	}
	public function listBidByUid($uid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BID_INFO"));
		return $tbl->select(['uid', '==', $uid])->fetch()[0];
	}
	public function listBidByBidId($bidId){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BID_INFO"));
		return $tbl->select(['bidId', '==', $bidId])->fetch()[0];
	}
	public function listBidByEmptyObiderAdrOne(){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_BID_INFO"));
		$objArr = $tbl->select('*')->fetch();
		foreach ($objArr as $obj) {
			if(empty($obj["obiderAddr"]) || $obj["obiderAddr"] == ""){
				return obj;
			}
		}
		return array();
	}
	
	//********taobao_dingdan_info*********
	public function listTaobaoDingdanByParcel($buyer, $parcelUid){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_TAOBAO_DINGDAN_INFO").'_'.$buyer);
		//var_dump($buyer);
		//var_dump($parcelUid);
	    return $tbl->select(['buyer', '==', $buyer,'and'],['parcelUid', '==', $parcelUid,'and'])->fetch();
	}
	public function updateTaobaoDingdanStatusByBuyerAndParcel($buyer, $parcelUid, $toStatus){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_TAOBAO_DINGDAN_INFO").'_'.$buyer);
		return $tbl->select(['buyer', '==', $buyer, 'and'],
		                    ['parcelUid', '==', $parcelUid])
					->update(['status', $toStatus]);
	}
	public function addTaobaoDingdan($buyer, $parcelUid, $taobaoDingdanhao, $taobaoDingdanCNY){
		$cdb = new CrunchDB(constant("CRDB_PATH"));
		$tbl = $cdb->table(constant("TBL_MYYABID_TAOBAO_DINGDAN_INFO").'_'.$buyer);
		
		$obj = new TaobaoDingDanObject();
		$obj->uid = uniqid();
		$obj->buyer = $buyer;
		$obj->status = 'using';
		$obj->parcelUid = $parcelUid;
		$obj->taobaoDingdanhao = $taobaoDingdanhao;
		$obj->taobaoDingdanCNY = $taobaoDingdanCNY;
		
		$tbl->insert($obj);
		/*
		$insertArr = array();
		$insertArr[] = $obj;
		
		foreach ($insertArr as $a) {
			$tbl->insert($a);
		}
		*/
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
}
?>