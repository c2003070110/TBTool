<?php
class ItemObject
{
    public $uid = '';
    public $buyer = '';
	
    public $itemUrl = '';
    public $itemId = '';
    public $itemName = '';
    public $priceJPY = '';
    public $priceJPYEsti = '';
	
    public $bidFinishDt = '';
	
    public $obiderId = '';
    public $obiderAddr = '';
    public $obiderMsg = '';
	
    public $transfeeJPY = 'weizhi';
	
	public $weight = 'weizhi';
	
    public $status = '';//paiBf;paiing;depai;liupai;fuk;bdfh;bddao;rubao;zaitu;fin;cancel;
	
    public $priceCNY = '';
    //public $qtty = '';
	public $parcelUid //parcelObject->uid
}
?>