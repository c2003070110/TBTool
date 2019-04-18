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
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNUSD&status=<?php echo $_GET['status'] ?>">PSN USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNHKD&status=<?php echo $_GET['status'] ?>">PSN HKD</a></li>
  </ul>
  <hr class="mb-2">
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=XBOXUSD&status=<?php echo $_GET['status'] ?>">XBOX USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=GOOGLUSD&status=<?php echo $_GET['status'] ?>">GOOGLE USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=AMZNUSD&status=<?php echo $_GET['status'] ?>">Amazon USD</a></li>
   <li class="list-group-item"><a href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=STEAMUSD&status=<?php echo $_GET['status'] ?>">STEAM USD</a></li>
  </ul>
  <hr class="mb-2"> 
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mygiftcard/regcode.php">REG</a></li>
    <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php?status=using">stocklist</a></li>
    <li class="list-group-item"><a href="/myphp/mygiftcard/bidlist.php?status=paid">bidlist</a></li>
    <li class="list-group-item"><a href="/myphp/mygiftcard/msglist.php?status=wait">msglist</a></li>
  </ul>   
  <hr class="mb-2"> 
