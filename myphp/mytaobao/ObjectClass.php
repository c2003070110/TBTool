<?php
class TaobaoFahuoObject
{
    public $uid = '';
    
    public $orderNo = '';
    
    public $trackTraceNo = '';
    public $tranferProviderName = '';
	
    public $status = ''; // added;fin;fhFailure
	
	public $dtAdd = '';// date time
	public $dtFahuo = '';// date time
}

class TaobaoOrderObject
{
    public $uid = '';
    
    public $orderNo = '';
    
    public $orderCreatedTime = '';
    public $buyerName = '';
    public $buyerNote = '';
    public $addressFull = '';
	
    public $status = ''; // added;fahuo;
	
	public $dtAdd = '';// date time
	public $dtFahuo = '';// date time
}

class TaobaoOrderDetailObject
{
    public $uid = '';
    
    public $orderNo = '';
    
    public $baobeiTitle = '';
    
    public $sku = '';
	
    public $status = ''; // added;fahuo;
	
	public $dtAdd = '';// date time
	public $dtFahuo = '';// date time
}

?>
