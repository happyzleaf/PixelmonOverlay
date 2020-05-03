package com.happyzleaf.pixelmonoverlay.impl;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public final class Utils {
	private Utils() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static <Key, Value> Map<Key, Value> populateMap(Map map, Object... args) {
		checkArgument(args.length % 2 == 0, "The args must be even.");

		for (int i = 0; i < args.length; i += 2) {
			map.put(args[i], args[i + 1]);
		}

		return map;
	}
}
