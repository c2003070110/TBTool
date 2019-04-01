<?php
//ini_set('display_errors', 1);
//ini_set('display_startup_errors', 1);
//error_reporting(E_ALL);
require __DIR__ .'/MyDaiGou.php';
?>
<html lang="ja">
<head>
<title>buyer list</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<!--
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
var httpPrefix = "http://133.130.114.129/";
$(function() {
    $(document).on("click", "#btnAddBuyer", function() {
        var thisBox = $(this).parent().parent();
		var buyer = thisBox.find("#buyer").val();
        
        var jqxhr = $.ajax(httpPrefix + "myphp/mydaigou/action.php",
                         { type : "GET",
                           data : {"action" : "addBuyer", 
						           "buyer" : buyer},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
        });
    });
});
</script>
</head>
<body class="py-4">
<?php
  $myDaiGou = new MyDaiGou();
  $dataArr = $myDaiGou->listAllBuyer();  
?>
<div id="container" class="container">
	<ul class="list-group list-group-horizontal">
	<?php
	  foreach ($dataArr as $data) {
	?>
	  <li class="list-group-item"><a href="/myphp/mydaigou/itemlist.php?buyer=<?php echo $data["aucId"] ?>"><?php echo $data["aucId"] ?></a></li>
	<?php
	  }
	?>
	</ul>
      <hr class="mb-4">
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-6 themed-grid-col">
		    <div class="col-10 themed-grid-col"><label for="buyer">Buyer</label><input type="text" class="form-control" id="buyer"></div>
        </div>
		<button type="button" id="btnAddBuyer" class="btn btn-secondary actionBtn">ADD</button>
      </div>
      <hr class="mb-4">
  </div>
</div>
</body>
</html>