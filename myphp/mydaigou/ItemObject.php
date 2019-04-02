<?php
class ItemObject
{
    public $uid = '';
    public $buyer = '';
	
    public $gouruDate = '';// caigou
    public $orderDate = '';// xiadan
    public $shipmentDate1 = '';//riben fahuo
    public $shipmentDate2 = '';//zhongguo fahuo
	
    public $orderItem = '';
	
    public $status = '';//unasign,unGou,gouru,zaitu,fahuo,compl
	
    public $priceJPY = '';
    public $qtty = '';
    public $priceCNY = '';
}
?>