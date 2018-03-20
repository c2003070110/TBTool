package com.walk_nie.taobao.montBell.main;

import com.walk_nie.taobao.montBell.clothes.MontbellDownBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellHardShellBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellRainShellBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellSoftShellBaobeiCreator;
import com.walk_nie.taobao.montBell.clothes.MontbellWindShellBaobeiCreator;


public class Montbell18SpringSummer {


	public static void main(String[] args) throws Exception {
		new Montbell18SpringSummer().process();
	}

	public void process() throws Exception {

		new MontbellHardShellBaobeiCreator().process();
		new MontbellRainShellBaobeiCreator().process();
		new MontbellSoftShellBaobeiCreator().process();
		new MontbellWindShellBaobeiCreator().process();
		new MontbellDownBaobeiCreator().process();
		
/*
		new MontbellFreeceBaobeiCreator().process();
		new MontbellUnderWareBaobeiCreator().process();
		new MontbellCapHatBaobeiCreator().process();
		new MontbellFreeceBaobeiCreator().process();
		new MontbellDownBaobeiCreator().process();
		new MontbellHardShellBaobeiCreator().process();
		new MontbellRainShellBaobeiCreator().process();
		new MontbellSoftShellBaobeiCreator().process();
		new MontbellTShirtBaobeiCreator().process();
		new MontbellUnderWareBaobeiCreator().process();
		new MontbellWindShellBaobeiCreator().process();
		*/
		System.exit(0);
	}

}
