<?php
class MyVideoObject
{
    public $uid = '';
    
    public $url = '';
    
    public $uper = '';
    public $title = '';
	
	public $ytSearchRslt = "";
    
	public $dlVideoPath = "";
	
	public $ytVideoUrl = "";
	
	// addurl -> parse url -> youtube? -> download video -> upload to youtubue
    public $status = ''; // added;parsed;todl;dled;toul;uled;
	
	public $dtAdd = '';// date time
	public $dtparsed = '';// date time
	public $dtdled = '';// date time
	public $dtuled = '';// date time
}

?>
