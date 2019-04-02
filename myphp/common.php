<?php
require __DIR__ .'/crunchDB/crunchDB.php';
require __DIR__ .'/crunchDB/crunchTable.php';
require __DIR__ .'/crunchDB/crunchResource.php';

define("CRDB_PATH", __DIR__ . "/db"); 

define("TBL_MYGIFTCODE_CODE", "mygiftcode.code"); 

define("TBL_MYDAIGOU_ITEM_INFO", "mydaigou.item.info"); 

define("TBL_MYDAIGOU_BUYER_INFO", "mydaigou.buyer.info"); 

define("URL_ACTION_MYDAIGOU", "http://localhost:8080/myphp/mydaigou/action.php"); 

define("URL_AUTOCOMPLETE_MYDAIGOU", "http://localhost:8080/myphp/mydaigou/autocomplete.php"); 

?>