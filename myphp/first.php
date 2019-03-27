<?

//require './mygiftcard/MyGiftCard.php';
//$myGiftCard = new MyGiftCard();
//$rslt = $myGiftCard->saveCode("1,ca1,11;2,ca2,22;3,ca3,33;");
//$rslt = $myGiftCard->getCode("ca1");
//$rslt = $myGiftCard->assetCode("11", "a000","o0001");

require './mydaigou/MyDaigou.php';
require './mydaigou/DaiGouObject.php';

$my = new MyDaigou();
$obj = new MyDaigou();

$obj->buyer ='buyer3';
$obj->orderDate ='20190313';
$obj->orderItem ='it1004';
$obj->priceJPY ='3200';
$obj->qtty ='1';
$obj->priceCNY ='220';

$obj->status ='fahuo';

$rslt = $my->save($obj);
//$rslt = $my->updateStatus($obj);
//$rslt = $my->delete($obj);
var_dump($rslt);

?>