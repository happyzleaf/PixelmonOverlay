package com.happyzleaf.pixelbroadcaster.bridge;

import com.happyzleaf.pixelbroadcaster.PixelBroadcaster;
import org.spongepowered.api.Sponge;

import java.util.List;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class PlaceholderBridge {
	private static PlaceholderAPI papi = null;
	
	public static void setup(Object plugin) {
		if (Sponge.getPluginManager().isLoaded("placeholderapi")) {
			papi = new PlaceholderAPI(plugin);
		} else {
			PixelBroadcaster.LOGGER.error("PlaceholderAPI was not found, the placeholders won't be parsed.");
		}
	}
	
	public static String parse(String text, Object source) {
		if (papi == null) {
			return text;
		} else {
			return papi.parse(text, source);
		}
	}
	
	public static List<String> parse(List<String> texts, Object source) {
		if (papi == null) {
			return texts;
		} else {
			return papi.parse(texts, source);
		}
	}
}
