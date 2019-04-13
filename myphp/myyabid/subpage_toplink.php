<?php
  if($isAdmin){
?>
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/myyabid/page_addBuyer.php?admin=<?php echo $admin ?>">add buyer</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myBidList-admin.php?admin=<?php echo $admin ?>">bid list(ALL)</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myParcel.php?buyer=<?php echo $buyer ?>&admin=<?php echo $admin ?>">parcel</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myParcelList.php?buyer=<?php echo $buyer ?>&admin=<?php echo $admin ?>">parcel(fh)</a></li>
  </ul>
<?php
  }else{
?>
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/myyabid/page_myEstimation.php?buyer=<?php echo $buyer ?>">估算大概多少钱</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_addMyBid.php?buyer=<?php echo $buyer ?>">我要竞拍</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myBidList.php?buyer=<?php echo $buyer ?>">我的竞拍现在怎么样？</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myParcel.php?buyer=<?php echo $buyer ?>">我打包中的包裹都有哪些宝贝？</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myParcelList.php?buyer=<?php echo $buyer ?>">以前的包裹</a></li>
  </ul>
<?php 
  }
?>
  <hr class="mb-4">