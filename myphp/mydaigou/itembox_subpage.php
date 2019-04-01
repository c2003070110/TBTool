<?php

  <div class="box border border-primary <?php echo $boxCss ?>">
      <div class="row mb-4">
		<div class="input-group">
<?php
  if(!isset($buyer)){
?>
          <label>买家:<?php echo $data['buyer'] ?></label>
<?php
  }
?>
          <label>status:<?php echo $data['status'] ?></label>
		  <input type="hidden" name="uid" value="<?php echo $data['uid'] ?>">
		  <input type="hidden" name="status" value="<?php echo $data['status'] ?>">
		</div>
      </div>
          
		<div class="input-group">
          <input value="<?php echo $data['orderDate'] ?>" type="text" class="form-control" id="orderDate" placeholder="下单日期" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnSaveBox" type="button">SAVE</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
		<div class="input-group ui-front">
          <input value="<?php echo $data['orderItem'] ?>" type="text" class="form-control" id="orderItem" placeholder="宝贝商品" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnCopyBox" type="button">COPY</button>
		  </div>
		</div>
      </div>
      <div class="row mb-4">
		<div class="input-group">
		  <input value="<?php echo $data['priceJPY'] ?>" type="text" class="form-control" placeholder="价格(JPY)" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
			<button class="btn btn-outline-secondary" id="btnDelBox" type="button">DEL</button>
			<button class="btn btn-outline-secondary" id="btnRmBox" type="button">REMOVE</button>
		  </div>
		</div>
      </div>
<!--
      <div class="row mb-4">
        <div class="input-group">
          <input value="<?php echo $data['qtty'] ?>" type="text" class="form-control" id="qtty" placeholder="数量" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
		  </div>
        </div>
      </div>
-->
      <div class="row mb-4">
        <div class="input-group">
          <input value="<?php echo $data['priceCNY'] ?>" type="text" class="form-control" id="priceCNY" placeholder="价格(CNY)" aria-label="" aria-describedby="button-addon4">
		  <div class="input-group-append" id="button-addon4">
<?php
  if(!isset($data['status']) || $data['status'] ==''){
?>	
			<button class="btn btn-outline-secondary" id="btnAssign" type="button">assign</button>
<?php
  }
?>
<?php
  if($data['status'] =='unGou'){
?>			
            <button class="btn btn-outline-secondary" id="btnGouru" type="button">已购</button>
<?php
  } else if($data['status'] =='gouru'){
?>			
			<button class="btn btn-outline-secondary" id="btnZaitu" type="button">在途</button>
<?php
  } else if($data['status'] =='zaitu'){
?>	
			<button class="btn btn-outline-secondary" id="btnFahuo" type="button">发货</button>
<?php
  } else if($data['status'] =='fahuo'){
?>	
			<button class="btn btn-outline-secondary" id="btnCompl" type="button">compl</button>
<?php
  }
?>
		  </div>
        </div>
      </div>
      <hr class="mb-4">
  </div>
<?php
  }
?>
</div>
</body>
</html>