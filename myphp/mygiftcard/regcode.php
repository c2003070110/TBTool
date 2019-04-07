<html lang="ja">
<head>
<title>my gift card</title>
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
(function (original) {
  jQuery.fn.clone = function () {
    var result           = original.apply(this, arguments),
        my_textareas     = this.find('textarea').add(this.filter('textarea')),
        result_textareas = result.find('textarea').add(result.filter('textarea')),
        my_selects       = this.find('select').add(this.filter('select')),
        result_selects   = result.find('select').add(result.filter('select'));

    for (var i = 0, l = my_textareas.length; i < l; ++i) $(result_textareas[i]).val($(my_textareas[i]).val());
    for (var i = 0, l = my_selects.length;   i < l; ++i) result_selects[i].selectedIndex = my_selects[i].selectedIndex;

    return result;
  };
}) (jQuery.fn.clone);
</script>
<script type="text/javascript">
var actionUrl = "http://133.130.114.129/myphp/mygiftcard/action.php";
$(function() {
    $(document).on("click", "#btnSave", function() {
        var thisBox = $(this).parent().parent().parent();
        var orderNoVal = thisBox.find("#orderNo").val();
        var codeTypeVal = thisBox.find("#codeType").val();
        var codeCdVal = thisBox.find("#codeCd").val();
        if(orderNoVal == "" || codeCdVal == ''){
            return;
        }
        var param = orderNoVal + "," + codeTypeVal + "," + codeCdVal;
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":"save", "paramstr" : param},
                           dataType : "html" 
                          }
                      );
        jqxhr.done(function( msg ) {
            alert(msg);
            thisBox.find("#codeCd").val("");
        });
    });
    $(document).on("change", ".form-control11", function() {
        var thisBox = $(this).parent().parent().parent();
        var orderNoVal = thisBox.find("#orderNo").val();
        var codeTypeVal = thisBox.find("#codeType").val();
        var codeCdVal = thisBox.find("#codeCd").val();
        if(orderNoVal == "" || codeCdVal == ''){
            return;
        }
        var param = orderNoVal + "," + codeTypeVal + "," + codeCdVal;
        
        var jqxhr = $.ajax(actionUrl,
                         { type : "GET",
                           data : {"action":"save", "paramstr" : param},
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
  <ul class="list-group list-group-horizontal">
    <li class="list-group-item"><a href="/myphp/mygiftcard/stocklist.php">stock list</a></li>
  </ul>   
  <hr class="mb-4">
  <textarea id="tempTxtArea" rows="6" cols="35"></textarea>
  <hr class="mb-4">
  <div class="box">
      <div class="row mb-4 form-group">
        <div class="col-10 themed-grid-col"><label for="orderNo">OrderNo</label><input type="text" class="form-control" id="orderNo"></div>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-6 themed-grid-col">
            <label for="codeType">CodeType</label>
            <select class="custom-select d-block w-100" id="codeType">
                <option value="PSNUSD10" selected>PSN 10美元</option>
                <option value="PSNUSD20">PSN 20美元</option>
                <option value="PSNUSD50">PSN 50美元</option>
                <option value="PSNUSD100">PSN 100美元</option>
                <option value="XBOXUSD10">XBOX 10美元</option>
                <option value="XBOXUSD20">XBOX 20美元</option>
                <option value="XBOXUSD50">XBOX 50美元</option>
                <option value="XBOXUSD100">XBOX 100美元</option>
            </select>
        </div>
        <button type="button" id="btnSave" class="btn btn-secondary actionBtn">SAVE</button>
      </div>
      <div class="row mb-4 form-group">
        <div class="col-10 themed-grid-col">
            <label for="codeCd">CodeCd</label>
            <input type="text" class="form-control" id="codeCd">
        </div>
        <div class="col-2 themed-grid-col"></div>
      </div>
      <hr class="mb-4">
  </div>
</div>
</body>
</html>