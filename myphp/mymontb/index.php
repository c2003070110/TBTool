<html lang="ja">
<head>
<title>my montbell</title>
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

<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
<script type="text/javascript">
var rootUrl = "http://webshop.montbell.jp";
function toString(obj){
  var ss = "";
  for(pro in obj){
      ss += pro + ":" + obj[pro] + "\n";
  }
  return ss;
}
function pad (str, max) {
  str = str.toString();
  return str.length < max ? pad(str + " ", max) : str;
}
format = function(fmt){
  // "%1$s"
  for(var i=1;i< arguments.length; i++){
    fmt = fmt.replace(new RegExp('%' + i + '\\$s','g'), arguments[i]);
  }
  return fmt;
}
function sizeReget (prodId) {
    var sizeUrl = "http://webshop.montbell.jp/goods/size/?product_id=" + prodId;
    var jqxhr = $.ajax("/myphp/redirect.php",
                         { type : "GET",
                           data : {realUrl : sizeUrl},
                           async: false,
                           dataType : "html" }
                      );
    jqxhr.done(function( msg ) {
       var imglist = $(msg).find("#contents").find("img");
       var imgString = "";
       for(var i=0; i<imglist.length; i++){
           var href = $(imglist[i]).attr("src");
           if(href.startsWith("/")) href = rootUrl + href;
           if(href.endsWith(".jpg")){
               imgString += "<img src=\"" + href +"\">";
           }
       }
       var prodDivKey = "sizeFrame-"+ prodId;
       $("#" + prodDivKey).append(imgString);
    });
}
$(function() {
  $("#btnSend").click(function() {
     var prodId = $("#productId").val();
     var prodUrl = "http://webshop.montbell.jp/goods/disp.php?product_id=" + prodId;
     var jqxhr = $.ajax("/myphp/redirect.php",
                         { type : "GET",
                           data : {realUrl : prodUrl},
                           dataType : "html" }
                      );
     jqxhr.done(function( msg ) {
         var prodDivKey = "result-"+ prodId;
         var divFmt = "<div id=\"result-%1$s\">"
                      +  "<h1 id=\"head-%1$s\">%1$s</h1>"
                      +  "<div id=\"stock-%1$s\">%2$s\</div>"
                      +  "<div id=\"button-%1$s\">%3$s\</div>"
                      +  "<div id=\"dtlDisp-%1$s\" style=\"display:none;\">%4$s\</div>"
                      +  "<div id=\"imgList-%1$s\" style=\"display:none;\">%5$s\</div>"
                      +  "<div id=\"sizeFrame-%1$s\" style=\"display:none;\"></div>"
                      +"</div>";
         
         var sizeOpts = $(msg).find("select[name=\"sel_size\"]").find("option");
         var stockString = "";
         for(var i=0; i<sizeOpts.length; i++){
             var sizeName = $(sizeOpts[i]).attr("value");
             if(sizeName == "")continue;
             var sizeDiv = $(msg).find("div[id=\"size_"+sizeName+"\"]");
             var colorNames = $(sizeDiv).find("p[class=\"colorName\"]");
             var stockNames = $(sizeDiv).find("p[class=\"sell\"]");
             stockString += pad(sizeName, 3) +"| " ;
             for(var j=0; j<colorNames.length; j++){
                 stockString += $(colorNames[j]).text() +":" + pad($(stockNames[j]).text(),7) +";";
             }
             stockString += "\n";
         }
         stockString = "<textarea name=\"textArea1\" rows=\"7\" cols=\"60\">" + stockString + "</textarea>";
         
         var dtlDisp = $(msg).find("div.type01");
         var dtlDispString = "";
         for(var i=0; i<dtlDisp.length; i++){
             dtlDispString += $(dtlDisp[i]).text().trim() + "<br>";
         }
         
         var imgString = "";
         var imglist = $(msg).find("a.fancy_largelink");
         for(var i=0; i<imglist.length; i++){
             var href = $(imglist[i]).attr("href");
             if(href.startsWith("/")) href = rootUrl + href;
             var title = $(imglist[i]).attr("title");
             imgString += "<img src=\"" + href +"\" alt=\"" + title +"\">";
         }
         
         var buttonString = "<button class=\"dtlSwitch" +"\"  data=\""+prodId+"\">" + "詳細:"+prodId + "</button>";
         buttonString += "<button class=\"imgSwitch" +"\"  data=\""+prodId+"\">" + "画像:"+prodId + "</button>";
         buttonString += "<button class=\"sizeSwitch" +"\"  data=\""+prodId+"\">" + "サイズ:"+prodId + "</button>";
         
         divFmt = format(divFmt, prodId, stockString, buttonString, dtlDispString, imgString)
         $("#result").prepend(divFmt);
         
         $("#" + prodDivKey).find(".dtlSwitch").bind("click", function() {
            var key = "#dtlDisp-"+ $(this).attr("data");
            if($(key).is(":visible")){
                $(key).hide();
            }else{
                $(key).show();
            }
         });
         $("#" + prodDivKey).find(".imgSwitch").bind("click", function() {
            var key = "#imgList-"+ $(this).attr("data");
            if($(key).is(":visible")){
                $(key).hide();
            }else{
                $(key).show();
            }
         });
         $("#" + prodDivKey).find(".sizeSwitch").bind("click", function() {
            var key = "#sizeFrame-"+ $(this).attr("data");
            if(!$(key).find("img").length || $(key).find("img").length ==0){
                sizeReget($(this).attr("data"));
            }
		    if($(key).is(":visible")){
		        $(key).hide();
		    }else{
		        $(key).show();
		    }
            
         });
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
        <div class="col-5">
		  <label for="productId">MontBell ID:</label>
		  <input type="text" class="form-control" id="productId" >
        </div>
        <div class="col-3">
		  <button type="button" id="btnSend" class="btn btn-secondary">send</button>
        </div>
      </div>
  </div>
  <div id ="result"></div>
</div>
</body>
</html>