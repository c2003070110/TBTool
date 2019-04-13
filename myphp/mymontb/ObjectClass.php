<?php
class MBOrderObject
{
    public $uid = '';
    public $mbOrderNo = '';
    public $status = ''; // unorder,ordering,ordered,mbfh,fin,cancel
    
	public $transferNoGuoji = "";
	public $transferNoGuonei = "";
	
    //public $addressObj;
    public $firstName = '';
    public $lastName = '';
    public $tel = '';
    public $postcode = '';
	
    public $statePY = '';
    public $cityPY = '';
    public $adr1PY = '';
    public $adr2PY = '';
	
    public $fukuanWay = ''; // 1:Line JCB 2:CCB Master
}

class TBOrderObject
{
    public $uid = '';
    public $maijia = '';
    public $dingdanhao = '';
    public $dingdanDt = '';
    
    public $maijiadianzhiHanzi = '';
	public $transferWay = ""; // mbzhiYou;wozhiYou,pinYou
    
    public $status = ''; // deprecated! see to ProductObject#status
	
	public $mbUid = ""; // deprecated! see to ProductObject#mbUid
	
    //public $productObjList;
}
class ProductObject
{
    public $uid = '';
    
    public $tbUid = '';
	public $mbUid = "";
    
    public $productId = '';
    public $colorName = '';
    public $sizeName = '';
    
    public $priceOffTax = 0;
    public $stock = "";
	
    public $status = ''; // st,mbordering,mbordered,mbfh,mboff,fin
}

?>
