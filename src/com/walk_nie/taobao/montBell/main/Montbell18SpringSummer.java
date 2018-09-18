package com.walk_nie.taobao.montBell.main;

import com.walk_nie.taobao.montBell.clothes.MontbellCapHatBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellDownBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellFreeceBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellGlovesBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellHardShellBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellRainShellBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellSoftShellBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellUnderWareBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellWindShellBaobeiCreator;



public class Montbell18SpringSummer {


	public static void main(String[] args) throws Exception {
		new Montbell18SpringSummer().process();
	}

	public void process() throws Exception {
		/*
		new MontbellTShirtBaobeiCreator().process();
		new MontbellSandalsBaobeiCreator().process();
		new MontbellCapHatBaobeiCreator().process();
		new MontbellHardShellBaobeiCreator().process();
		new MontbellRainShellBaobeiCreator().process();
		new MontbellSoftShellBaobeiCreator().process();
		new MontbellWindShellBaobeiCreator().process();
	*/

		new MontbellCapHatBaobeiCreator().process();
		new MontbellDownBaobeiCreator().process();
		new MontbellFreeceBaobeiCreator().process();
		new MontbellGlovesBaobeiCreator().process();
		new MontbellHardShellBaobeiCreator().process();
		new MontbellRainShellBaobeiCreator().process();
		new MontbellSoftShellBaobeiCreator().process();
		new MontbellUnderWareBaobeiCreator().process();
		new MontbellWindShellBaobeiCreator().process();
		
		System.exit(0);
	}

}
