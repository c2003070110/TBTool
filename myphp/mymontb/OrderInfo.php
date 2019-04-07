<?php
class OrderObject
{
    public $uid = '';
    public $maijia = '';
    public $dingdanhao = '';
    public $maijiadianzhiHanzi = '';
    public $mbOrderNo = '';
    public $status = ''; // unorder,ordered
	
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
	
    public $productObjList;
}
class ProductObject
{
    public $productId = '';
    public $colorName = '';
    public $sizeName = '';
}

?>
