<?php
class BidItemObject
{
    public $uid = '';
    public $buyer = '';
	
    public $itemUrl = '';
    public $itemId = '';
    public $itemName = '';
    public $priceJPY = '稍后录入';
    public $priceCNY = '';// priceJPY * huilv
	
    public $priceJPYEsti = '';
	
    public $hdaoDt = '';
    public $assetPlace = '';
	
    public $obiderId = '';
    public $obiderAddr = '';
    public $obiderMsg = '';
    public $bidFinishDt = '';
	
    public $transfeeDaoneiJPY = '稍后录入';
    public $transfeeDaoneiCNY = '稍后录入';// transfeeDaoneiJPY * huilv 
	
	public $weight = '稍后录入';
	
    public $status = '';//paiBf;paiing;depai;liupai;fuk;bdfh;bddao;rubao;daobao;zaitu;fin;cancel;
	
    public $daigoufeiCNY = '';// priceCNY + transfeeDaoneiCNY + daigoufei
    public $itemCNY = '';// priceCNY + transfeeDaoneiCNY + daigoufei
    //public $qtty = '';
	public $parcelUid ='';//parcelObject->uid
}
?>