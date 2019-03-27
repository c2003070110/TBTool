<?
require '../suggestObject.php';

class mydaigouSuggest
{
	private $suggestList = array();

	private function init(){
		$obj = new suggestObject();
		$obj->label = "ActionScript:1:2";
		$obj->value = "ActionScript";
		array_push($this->suggestList, $obj);
		$obj = new suggestObject();
		$obj->label = "Asp:1:2";
		$obj->value = "Asp";
		array_push($this->suggestList, $obj);
	}
	
	public function sugguest($termKey){
		$this->init();
		
		$tags = array();
		foreach ($this->suggestList as $tag) {
			if (stripos($tag->value, $termKey) !== false) {
				$tags[] = $tag;
			}
		}
		echo json_encode($tags);
	}
}
?>