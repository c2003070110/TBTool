<?php
require __DIR__ .'/MyHuilv.php';
?>
<html lang="ja">
<head>
<title>my hui lv</title>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<!--
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.js"></script>
-->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

<script type="text/javascript">
var actionUrl = "<?php echo constant("URL_ACTION_ADMINSUPP") ?>";
$(function() {
    $(document).on("click", "#btnMyGiftCardBackup", function() {
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {
							   "action" : "backupMyGiftCard"},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
        });
    });
    $(document).on("click", "#btnMyMontbellBackup", function() {
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {
							   "action" : "backupMyMontbell"},
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
<div id="container" class="container">
<?php
  include __DIR__ .'/subpage_toplink.php';
?>
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-12">
          <button type="button" id="btnMyGiftCardBackup" class="btn btn-primary">MyGiftCard Backup</button>
        </div>
      </div>
      <hr class="mb-4">
      <div class="row mb-4 form-group">
        <div class="col-12">
          <button type="button" id="btnMyMontbellBackup" class="btn btn-primary">My MontBell Backup</button>
        </div>
      </div>
      <hr class="mb-4">
  </div>
</div>
</body>
</html>