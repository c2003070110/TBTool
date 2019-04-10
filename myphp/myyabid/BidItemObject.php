<?php
class BidItemObject
{
    public $uid = '';
    public $buyer = '';
	
    public $itemUrl = '';
    public $itemId = '';
    public $itemName = '';
    public $priceJPY = 'shaohouluru';
    public $priceCNY = '';// priceJPY * huilv
    public $priceJPYEsti = '';
	
    public $bidFinishDt = '';
	
    public $obiderId = '';
    public $obiderAddr = '';
    public $obiderMsg = '';
	
    public $transfeeDaoneiJPY = 'shaohouluru';
    public $transfeeDaoneiCNY = 'shaohouluru';// transfeeDaoneiJPY * huilv 
	
	public $weight = 'shaohouluru';
	
    public $status = '';//paiBf;paiing;depai;liupai;fuk;bdfh;bddao;rubao;daobao;zaitu;fin;cancel;
	
    public $daigoufeiCNY = '';// priceCNY + transfeeDaoneiCNY + daigoufei
    public $itemCNY = '';// priceCNY + transfeeDaoneiCNY + daigoufei
    //public $qtty = '';
	public $parcelUid ='';//parcelObject->uid
}
?>