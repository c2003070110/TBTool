package com.walk_nie.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;

import com.beust.jcommander.internal.Lists;

public class NieConfig {
	private static Properties propertiesMap = new Properties();;

	private static void init() {
		String configFolder = NieConstants.folder_config_file;
		File[] files = new File(configFolder).listFiles();
		for (File f : files) {
			if (!f.canRead())
				continue;
			if (!f.isFile())
				continue;
			try {
				propertiesMap.load(new InputStreamReader(new FileInputStream(f), "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getConfigByPrefix(final String keyPrefix) {
		if (propertiesMap.isEmpty()) {
			init();
		}
		final List<String> list = Lists.newArrayList();
		propertiesMap.forEach(new BiConsumer<Object, Object>(){
			@Override
			public void accept(Object key, Object val) {
				if(((String)key).startsWith(keyPrefix)){
					list.add((String)val);
				}
			}
		});
		return list;
	}

	public static String getConfig(String key) {
		if (propertiesMap.isEmpty()) {
			init();
		}
		return propertiesMap.getProperty(key);
	}

	public static String getConfig(String key, String defaultValue) {
		if (propertiesMap.isEmpty()) {
			init();
		}
		return propertiesMap.getProperty(key, defaultValue);
	}

}
