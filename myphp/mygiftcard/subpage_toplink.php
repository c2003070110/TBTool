<?php
function endsWith($haystack, $needle) {
    return (strlen($haystack) > strlen($needle)) ? (substr($haystack, -strlen($needle)) == $needle) : false;
}
$selfName = $_SERVER["PHP_SELF"];
if (endsWith($selfName, "stocklist.php")) {
	$thisFile = "stocklist.php";
}else{
	$thisFile = "regcode.php";
}
?>
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNUSD&uid=<?php echo $_GET['uid'] ?>">PSN USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNHKD&uid=<?php echo $_GET['uid'] ?>">PSN HKD</a></li>
  </ul>
  <hr class="mb-2">
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=XBOXUSD&uid=<?php echo $_GET['uid'] ?>">XBOX USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=GOOGLUSD&uid=<?php echo $_GET['uid'] ?>">GOOGLE USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=AMZNUSD&uid=<?php echo $_GET['uid'] ?>">Amazon USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=STEAMUSD&uid=<?php echo $_GET['uid'] ?>">STEAM USD</a></li>
  </ul>
  <ul class="list-group list-group-horizontal">
  </ul>
  <hr class="mb-2">
