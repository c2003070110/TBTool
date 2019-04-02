<?
require './MyDaiGou.php';
$sugguestType = $_POST['sugguestType'];
$termKey = $_POST['termKey'];
if ($sugguestType === 'buyer') {
	$my = new MyDaiGou();
	$dataArr = $my->listAllBuyer();
	$tags = array();
	foreach ($dataArr as $data) {
		if (stripos($data["buyer"], $termKey) !== false) {
			$tags[] = array("label" => $data["buyer"],"value" => $data["buyer"]);
		}
	}
	echo json_encode($tags);
}else if ($sugguestType === 'orderItem') {
	$my = new MyDaiGou();
	$dataArr = $my->listAllItem();
	$tags = array();
	foreach ($dataArr as $data) {
		if (stripos($data["orderItem"], $termKey) !== false) {
			$data = array(
			             "label" => $data["orderItem"].':'.$data["priceJPY"].':'.$data["priceCNY"],
			             "value" => $data["orderItem"]);
			if(!in_array($data, $tags)){
				$tags[] = $data;
			}
		}
	}
	echo json_encode($tags);
} else {
    echo json_encode(array());
}

?>