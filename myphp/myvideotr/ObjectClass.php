<?php
class MyVideoObject
{
    public $uid = '';
    public $groupUid = '';// for ##
    
    public $url = '';
    public $videoUrl = '';
    
    public $uper = '';
    public $title = '';
	
	public $ytSearchRslt = "";
    
	public $dlVideoPath = "";
	
	public $ytVideoUrl = "";
	
	// addurl -> parse url -> youtube?merge? -> download video -> upload to youtubue
    public $status = ''; // added;parsed;todl;dled;tomg;toul;uled;parsefailure;dlfailure;mgfailure;;ulfailure
	
	public $dtAdd = '';// date time
	public $dtparsed = '';// date time
	public $dtdled = '';// date time
	public $dtuled = '';// date time
}

?>
