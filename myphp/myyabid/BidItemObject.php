<?php
class BidItemObject
{
    public $uid = '';
	
    public $status = '';//paiBf;paiing;depai;liupai;fuk;bdfh;bddao;rubao;dabao;zaitu;fin;cancel;
    
    public $buyer = '';
    
    public $itemUrl = '';
    public $itemName = '';
    public $estimateJPY=0;
	
	public $weight = '稍后录入';
	
    public $hdaoDt = '';
    public $assetPlace = '';
    
    public $priceJPY = '稍后录入';
    public $priceCNY = '';// priceJPY * huilv
    
    public $transfeeDaoneiJPY = '稍后录入';
    public $transfeeDaoneiCNY = '稍后录入';// transfeeDaoneiJPY * huilv 
	
    public $daigoufeiCNY = '';
    public $itemCNY = ''; // priceCNY + transfeeDaoneiCNY + daigoufei
    
    //public $qtty = '';
    
	public $bidUid ='';//BidObject->uid
	
	public $parcelUid ='';//parcelObject->uid
}
?>