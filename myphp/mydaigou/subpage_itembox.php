
  <div class='box <?php echo $boxCss ?>'>
    <input type="hidden" id="buyer" value="<?php echo $data['buyer'] ?>">
    <input type="hidden" id="uid" value="<?php echo $data['uid'] ?>">
    <input type="hidden" id="status" value="<?php echo $data['status'] ?>">
<?php
  if($subPageDiv == 'assigned'){
?>      
		<div class="mb-2 input-group">
          <input value="<?php echo $data['orderDate'] ?>" type="text" class="form-control" id="orderDate" placeholder="下单日期" aria-label="" aria-describedby="button-addon4">
		</div>
<?php
  }
?>
		<div class="mb-2 input-group ui-front">
          <input value="<?php echo $data['orderItem'] ?>" type="text" class="form-control" id="orderItem" placeholder="宝贝商品" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
<!--
			<button class="btn" id="btnCopyBox" type="button">COPY</button>
-->
		  </div>
		</div>
		<div class="mb-2 input-group">
		  <input value="<?php echo $data['priceJPY'] ?>" type="text" class="form-control" id="priceJPY" placeholder="价格(JPY)" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
<?php
  if($subPageDiv == 'createNew' || $subPageDiv == 'modify'){
?>
			<button class="btn" id="btnSaveBox" type="button">SAVE</button>
<?php
  }
?>
<?php
  if($data['status'] =='gouru' || $data['status'] =='zaitu' || $data['status'] =='fahuo' || $data['status'] =='compl'){
?>		
			<button class="btn" id="btnDelBox" type="button">RELEASE</button>
<?php
  }else{
?>
			<button class="btn" id="btnDelBox" type="button">DELETE</button>
<?php
  }
?>
<!--
			<button class="btn" id="btnRmBox" type="button">REMOVE</button>
-->
		  </div>
		</div>
<?php
  if($subPageDiv == 'createNew'){
?>	
        <div class="mb-2 input-group">
          <input type="text" class="form-control" id="qtty" placeholder="数量" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
		  </div>
        </div>
<?php
  }
?>
        <div class="mb-2 input-group">
          <input value="<?php echo $data['priceCNY'] ?>" type="text" class="form-control" id="priceCNY" placeholder="价格(CNY)" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
<?php
  if($subPageDiv == 'unassign'){
?>	
			<button class="btn" id="btnAssign" type="button">assign</button>
<?php
  }
?>
<?php
if($subPageDiv == 'assigned'){
  if($data['status'] =='unGou'){
?>			
            <button class="btn" id="btnGouru" type="button">已购にする</button>
<?php
  } else if($data['status'] =='gouru'){
?>			
			<button class="btn" id="btnZaitu" type="button">在途にする</button>
<?php
  } else if($data['status'] =='zaitu'){
?>	
			<button class="btn" id="btnFahuo" type="button">发货にする</button>
<?php
  } else if($data['status'] =='fahuo'){
?>	
			<button class="btn" id="btnCompl" type="button">完結にする</button>
<?php
  }
}
?>
		  </div>
      </div>
  <hr class="mb-4">
  </div>