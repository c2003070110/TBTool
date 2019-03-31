<?php

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
//require './common.php';
require __DIR__ . '/mygiftcard/MyGiftCard.php';
$myGiftCard = new MyGiftCard();
$rslt = $myGiftCard->saveCode("12,ca1,11");
//$rslt = $myGiftCard->getCode("ca1");
//$rslt = $myGiftCard->assetCode("11", "a000","o0001");
var_dump($rslt);
/*
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
*/
?>
