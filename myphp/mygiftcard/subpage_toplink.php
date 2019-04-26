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
  <ul class="nav">
      <li class="nav-item active">
        <a class="nav-link" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNUSD&status=<?php echo $_GET['status'] ?>" role="button" >
          PSN USD
        </a>
      </li>
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNHKD&status=<?php echo $_GET['status'] ?>" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          PSN HKD
        </a>
        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
          <a class="dropdown-item" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=PSNHKD&status=<?php echo $_GET['status'] ?>">PSN HKD</a>
          <a class="dropdown-item" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=XBOXUSD&status=<?php echo $_GET['status'] ?>">XBOX USD</a>
          <a class="dropdown-item" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=GOOGLUSD&status=<?php echo $_GET['status'] ?>">GOOGLE USD</a>
          <a class="dropdown-item" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=AMZNUSD&status=<?php echo $_GET['status'] ?>">Amazon USD</a>
          <a class="dropdown-item" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=STEAMUSD&status=<?php echo $_GET['status'] ?>">STEAM USD</a>
		  <div class="dropdown-divider"></div>
          <a class="dropdown-item" href="/myphp/mygiftcard/<?php echo $thisFile ?>?codeType=AMZNJPY&status=<?php echo $_GET['status'] ?>">Amazon JPY</a>
        </div>
      </li>
      <li class="nav-item dropdown">
        <a class="nav-link dropdown-toggle" href="/myphp/mygiftcard/regcode.php" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          录入
        </a>
        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
          <a class="dropdown-item" href="/myphp/mygiftcard/stocklist.php?status=unused">库存</a>
          <a class="dropdown-item" href="/myphp/mygiftcard/bidlist.php?status=paid">得拍</a>
          <a class="dropdown-item" href="/myphp/mygiftcard/msglist.php?status=wait">消息</a>
		  <div class="dropdown-divider"></div>
          <a class="dropdown-item" href="/myphp/mygiftcard/page_makeAmznOrder.php">amzn</a>
		  <a class="dropdown-item" href="/myphp/mygiftcard/page_amznOrderList.php">amznlist</a>
        </div>
      </li>
      <li class="nav-item active">
        <a class="nav-link" href="/myphp/adminsupp/page_backup.php">backup</a>
      </li>
  </ul>   
  <hr class="mb-2"> 
