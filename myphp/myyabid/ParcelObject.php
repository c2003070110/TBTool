<?php
class ParcelObject
{
    public $uid = '';
    public $buyer = '';
	
    public $guojiShoudan = '';//1:ems;2:air;3:sal;4:sea;5:pinyou
	
	//public $transWeight = 0;
	
	public $transfeeGuojiJPY = 0;
	public $transfeeGuojiCNY = 0;
	public $transnoGuoji = '';
	
    public $status = '';// 1:daBao;2:zaiTu;3:gnFh;4:fin;
	
	public $transfeeGuonei = 0;
	public $transnoGuonei = '';
	
	public $paidTtlCNY = 0;
	
    public $itemTtlPriceJPY = 0;
    public $itemTtlPriceCNY = 0;
	
    public $itemTtlWeight = 0;
	
    public $itemTransfeeDaoneiTtlJPY = 0;
    public $itemTransfeeDaoneiTtlCNY = 0;
	
    public $itemTtlCNY = 0;
    public $daigoufeiTtlCNY = 0;
}
?>