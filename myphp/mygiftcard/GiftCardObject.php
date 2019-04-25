<?php
class GiftCardObject
{
    public $uid = '';
    public $orderNo = '';
    public $codeType = '';
    public $codeCd = '';
	
    public $status = '';//unused,using,used,invalid,fin
	
    public $bidId = '';// aucId->bidId
    public $obidId = '';
	
	public $dtReg = '';// date time
	public $dtGot = '';// date time
	public $dtAsset = '';// date time
	public $dtFinish = '';// date time
}
class BidObject
{
    public $uid = '';
    public $bidId = '';
    public $obidId = '';
	
    public $codeType = '';
	
    public $status = '';//bided paid sent fin
	
	public $dtAdd = '';// date time
	//public $dtBid = '';// date time
	public $dtpaid = '';// date time
	public $dtsend = '';// date time
	public $dtfin = '';// date time
	
    public $msg = "";
    public $replymsg = "";
    public $msgStatus = '';//wait ignore aplied sent
	public $dtMsg = '';// date time
	
}
class AmznOrderObject
{
    public $uid = '';
    public $amt = '';
    public $qtty = '';
    public $payway = '';
	
    public $mailAddress = '';
	public $dtAdd = '';// date time
	public $dtOrdered = '';// date time
	public $dtFin = '';// date time
	
    public $status = '';//unorder ordered fin
	
	
}

?>
