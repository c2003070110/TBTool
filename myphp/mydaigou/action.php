<?
require './MyDaiGou.php';
require './DaiGouObject.php';
$actionStr = $_GET['action'];

if($actionStr === null){
	echo "[Fatal]Parameter Of [action] is NULL";
	return;
}
$obj = new DaiGouObject();
$obj->buyer = $_GET['buyer'];
$obj->orderDate = $_GET['orderDate'];
$obj->orderItem = $_GET['orderItem'];
$obj->priceJPY = $_GET['priceJPY'];
$obj->qtty = $_GET['qtty'];
$obj->priceCNY = $_GET['priceCNY'];
if($actionStr == "save"){
	$my = new MyDaiGou();
	$rslt = $my->save($obj);
	echo $rslt;
} else if($actionStr == "listByBuyer"){
	$my = new MyDaiGou();
	$rslt = $my->listByBuyer($buyer);
	echo $rslt;
} else if($actionStr == "delete"){
	$my = new MyDaiGou();
	$rslt = $my->delete($obj);
	echo $rslt;
} else if($actionStr == "gouru"){
	$my = new MyDaiGou();
	$obj->status = 'gouru';
	$rslt = $my->updateStatus($obj);
	echo $rslt;
} else if($actionStr == "fahuo"){
	$my = new MyDaiGou();
	$obj->status = 'fahuo';
	$rslt = $my->updateStatus($obj);
	echo $rslt;
}
?>