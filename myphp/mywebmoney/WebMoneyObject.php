<?php
class WebMoneyObject
{
    public $uid = '';
    
    public $tbBuyer = '';
    public $tbOrderNo = '';
	
    public $url = '';
    public $amtJPY = '';
	public $payway ="";//prepaidNo,wallet,cardcase
    
	public $realShopComment ="";
	public $realItemName ="";
	
	public $payResult ="";
	
    public $status = '';//checkwait,checked,topay,paid,fin

	public $dtAdd = '';// date time
	public $dtCheck = '';// date time
	public $dtPay = '';// date time
	public $dtFinish = '';// date time
}

?>
