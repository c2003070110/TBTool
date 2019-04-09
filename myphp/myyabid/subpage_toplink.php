<?php
  if($isAdmin){
?>
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/myyabid/page_addBuyer.php?admin=<?php echo $admin ?>">add buyer</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myBidList.php?buyer=<?php echo $buyer ?>&admin=<?php echo $admin ?>">bid list</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myParcel.php?buyer=<?php echo $buyer ?>&admin=<?php echo $admin ?>">parcel</a></li>
  </ul>
<?php
  }else{
?>
  <ul class="list-group list-group-horizontal">
   <li class="list-group-item"><a href="/myphp/myyabid/page_myEstimation.php?buyer=<?php echo $buyer ?>">Estimate</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_addMyBid.php?buyer=<?php echo $buyer ?>">add bid</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myBidList.php?buyer=<?php echo $buyer ?>">bid list</a></li>
   <li class="list-group-item"><a href="/myphp/myyabid/page_myParcel.php?buyer=<?php echo $buyer ?>">parcel</a></li>
  </ul>
<?php 
  }
?>
  <hr class="mb-4">