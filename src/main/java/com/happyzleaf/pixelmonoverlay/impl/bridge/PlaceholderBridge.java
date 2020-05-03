package com.happyzleaf.pixelmonoverlay.impl.bridge;

import com.happyzleaf.pixelmonoverlay.impl.PixelmonOverlayImpl;
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
			PixelmonOverlayImpl.LOGGER.error("PlaceholderAPI was not found, the placeholders won't be parsed.");
		}
	}

	public static String parse(String text, Object source) {
		if (papi == null) {
			return text;
		}

		return papi.parse(text, source);
	}

	public static List<String> parse(List<String> texts, Object source) {
		if (papi == null) {
			return texts;
		}

		return papi.parse(texts, source);
	}
}
