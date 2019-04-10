<?php

class MyTransfee
{
	public function getGuojiYunfei($weight, $guojiShoudan){
		if($guojiShoudan == "EMS"){
			$st1 = 1400;$st2 = 2400;$st3 = 3800;$st4 = 8100;
			$incr1=140;$incr2=300;$incr3=500;$incr4=800;
			if($weight < 500){
				return $st1;
			}else if($weight < 600){
				return $st1 + $incr1 * 1;
			}else if($weight < 700){
				return $st1 + $incr1 * 2;
			}else if($weight < 800){
				return $st1 + $incr1 * 3;
			}else if($weight < 900){
				return $st1 + $incr1 * 4;
			}else if($weight < 1000){
				return $st1 + $incr1 * 5;
			}else if($weight < 1250){
				return $st2;
			}else if($weight < 1500){
				return $st2 + $incr2 * 1;
			}else if($weight < 1750){
				return $st2 + $incr2 * 2;
			}else if($weight < 2000){
				return $st2 + $incr2 * 3;
			}else if($weight < 2500){
				return $st3;
			}else if($weight < 3000){
				return $st3 + $incr3 * 1;
			}else if($weight < 3500){
				return $st3 + $incr3 * 2;
			}else if($weight < 4000){
				return $st3 + $incr3 * 3;
			}else if($weight < 4500){
				return $st3 + $incr3 * 4;
			}else if($weight < 5000){
				return $st3 + $incr3 * 5;
			}else if($weight < 5500){
				return $st3 + $incr3 * 6;
			}else if($weight < 6000){
				return $st3 + $incr3 * 7;
			}else if($weight < 7000){
				return $st4;
			}else {
				return $st4 + $incr3 * ceil (($weight-7000)/1000);
			}
		}else if($guojiShoudan == "SAL"){
			$st1 = 1800;$st2 = 4700;$st3 = 7000;
			$incr1=600;$incr2=500;$incr3=300;
			if($weight < 1000){
				return $st1;
			}else if($weight < 2000){
				return $st1 + $incr1 * 1;
			}else if($weight < 3000){
				return $st1 + $incr1 * 2;
			}else if($weight < 4000){
				return $st1 + $incr1 * 3;
			}else if($weight < 5000){
				return $st1 + $incr1 * 4;
			}else if($weight < 6000){
				return $st2;
			}else if($weight < 7000){
				return $st2 + $incr2 * 1;
			}else if($weight < 8000){
				return $st2 + $incr2 * 2;
			}else if($weight < 8000){
				return $st2 + $incr2 * 3;
			}else if($weight < 10000){
				return $st2 + $incr2 * 4;
			}else if($weight < 11000){
				return $st3;
			}else {
				return $st3 + $incr3 * ceil (($weight-11000)/1000);
			}
		}else if($guojiShoudan == "SEA"){
			$st1 = 1600;
			$incr1=300;
			if($weight < 1000){
				return $st1;
			}else {
				return $st1 + $incr1 * ceil (($weight-1000)/1000);
			}
		}else if($guojiShoudan == "PINGYOU"){
			return ceil (($weight / 100) * 120);
		}else{
			return 0;
		}
	}
	public function getGuoneiYunfei($weight, $guojiShoudan){
		if($guojiShoudan === ""){
			return 0;
		}
		if($guojiShoudan !== "PINGYOU"){
			return 0;
		}
		if($weight < 1000){
			return 10;
		}else{
			return 10 + ceil ($weight / 1000) * 5;
		}
	}
}
?>