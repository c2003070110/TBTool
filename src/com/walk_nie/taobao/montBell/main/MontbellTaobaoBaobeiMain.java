package com.walk_nie.taobao.montBell.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;



import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.BaobeiUtil;

public class MontbellTaobaoBaobeiMain {
	String publishedBaobeiFile = "C:/temp/montbell-down-20171112.csv";

	public static void main(String[] args) throws  IOException {
		new MontbellTaobaoBaobeiMain().process();
	}

	public void process() {
		while (true) {
			try {
				//
				int todoType = choiceTodo();
				if (todoType == 0) {
					listDup();
				}
				if (todoType == 1) {
					listShortStock();
				}
				if (todoType == 2) {
				}
				if (todoType == 3) {
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void listShortStock() {
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(file);
		for (BaobeiPublishObject baobeiObj : baobeiList) {
			String productId = getProductIdFromOuter_id(baobeiObj.outer_id);
			String[] skuProps = baobeiObj.skuProps.split(";");
			boolean isShortStock = false;
			for (String skuProp : skuProps) {
				if (skuProp.startsWith("20509")) {
					continue;
				}
				String skus[] = skuProp.split(":");
				if (skus.length > 1) {
					String num = skus[1];
					if (num.equals("0")) {
						isShortStock = true;
						break;
					}
				}
			}
			if (isShortStock) {
				System.out.println("[ShortStock][ProductId][" + productId + "]");
			}
		}
	}

	public void listDup() {
		File file = new File(publishedBaobeiFile);
		List<BaobeiPublishObject> baobeiList = BaobeiUtil.readInPublishedBaobei(file);

		Map<String, List<String>> map = Maps.newHashMap();
		for (BaobeiPublishObject baobeiObj : baobeiList) {
			String productId = getProductIdFromOuter_id(baobeiObj.outer_id);
			List<String> items = map.get(productId);
			if (items == null) {
				items = Lists.newArrayList();
				items.add(baobeiObj.title);
				map.put(productId, items);
			} else {
				items.add(baobeiObj.title);
			}
		}
		while (map.entrySet().iterator().hasNext()) {
			Map.Entry<String, List<String>> e = map.entrySet().iterator().next();
			List<String> dupList = e.getValue();
			if (dupList.size() == 1)
				continue;
			for (int i = 1; i < dupList.size(); i++) {
				System.out.println("[DUP][ProductId][" + e.getKey() + "][BaobeiId][" + dupList.get(i) + "]");
			}
		}
	}

	public String getProductIdFromOuter_id(String outer_id) {

		outer_id = outer_id.replace("\"", "");
		if (outer_id.startsWith("MTBL_")) {
			String[] split = outer_id.split("-");
			return split[split.length - 1];
		} else {
			return outer_id;
		}
	}

	protected BufferedReader stdReader = null;

	private int choiceTodo() {
		int type = 0;
		try {
			System.out.println("Type of todo : ");
			System.out.println("0:list dupcate item;\n" + "1:list baobei stock;\n" + "2:...;\n");

			stdReader = getStdReader();
			while (true) {
				String line = stdReader.readLine();
				if ("0".equals(line.trim())) {
					type = 0;
					break;
				} else if ("1".equals(line.trim())) {
					type = 1;
					break;
				} else if ("2".equals(line.trim())) {
					type = 2;
					break;
				} else if ("3".equals(line.trim())) {
					type = 3;
					break;
				} else if ("4".equals(line.trim())) {
					type = 4;
					break;
				} else {
					System.out.println("Listed number only!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return type;
	}

	public BufferedReader getStdReader() {
		if (stdReader == null) {
			stdReader = new BufferedReader(new InputStreamReader(System.in));
		}
		return stdReader;
	}
}
