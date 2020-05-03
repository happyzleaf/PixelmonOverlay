package com.happyzleaf.pixelmonoverlay.impl;

import com.google.common.reflect.TypeToken;
import com.happyzleaf.pixelmonoverlay.impl.overlay.OverlayImpl;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class Config {
	/**
	 * This has to be an arraylist so the positions are preserved.
	 */
	public static ArrayList<OverlayImpl> overlays = new ArrayList<>(Arrays.asList(
			new OverlayImpl(EnumOverlayLayout.LEFT_AND_RIGHT, OverlayGraphicType.PokemonSprite, new ArrayList<>(Arrays.asList("Hi %player%", "Currently there are %server_online% players connected", "X: %player_x% Y: %player_y% Z: %player_z%")), null, "pikachu s", null),
			new OverlayImpl(EnumOverlayLayout.LEFT, OverlayGraphicType.Pokemon3D, new ArrayList<>(Arrays.asList("Player: %player_displayname%", "Health: %player_health%/%player_max_health%", "Ping: %player_ping%")), null, "solgaleo gr:giant", null),
			new OverlayImpl(EnumOverlayLayout.RIGHT, OverlayGraphicType.ItemStack, new ArrayList<>(Arrays.asList("Joined in: %player_first_join%", "Played for: %player_time_played%")), 10L, null, ItemStack.of(ItemTypes.GRASS, 1)),
			new OverlayImpl(EnumOverlayLayout.LEFT_AND_RIGHT, OverlayGraphicType.ItemStack, new ArrayList<>(Arrays.asList("Server: %server_online%/%server_max_players%", "%server_motd%", "TPS: %server_tps%")), 25L, null, ItemStack.of(Sponge.getRegistry().getType(ItemType.class, "pixelmon:poke_ball").get(), 1))));
	public static long broadcastInterval = 300; // in seconds
	public static long silenceInterval = 300; // in seconds

	private static ConfigurationLoader<CommentedConfigurationNode> loader;
	private static Path path;

	public static void init(Path path) {
		Config.path = path;
		Config.loader = HoconConfigurationLoader.builder().setPath(path).build();

		TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(OverlayImpl.class), new OverlayImpl.Serializer());

		loadConfig();
	}

	public static void loadConfig() {
		if (Files.notExists(path)) {
			saveConfig();
		}

		try {
			ConfigurationNode node = loader.load();

			overlays.clear();
			overlays.addAll(node.getNode("overlays").getList(TypeToken.of(OverlayImpl.class)));

			broadcastInterval = node.getNode("broadcastInterval").getLong(broadcastInterval);
			if (broadcastInterval <= 0) {
				PixelmonOverlayImpl.LOGGER.error("There was a problem while loading the config. 'broadcastInterval' must be positive, right now it's {}.", broadcastInterval);
				return;
			}

			silenceInterval = node.getNode("silenceInterval").getLong(silenceInterval);
			if (silenceInterval < 0) {
				PixelmonOverlayImpl.LOGGER.error("There was a problem while loading the config. 'silenceInterval' must be 0 or higher, right now it's {}.", silenceInterval);
				return;
			}

			loader.save(node);
		} catch (Exception e) {
			PixelmonOverlayImpl.LOGGER.error("There was a problem while loading the config.", e);
		}
	}

	public static void saveConfig() {
		try {
			CommentedConfigurationNode node = loader.load();

			node.getNode("overlays").setValue(new TypeToken<List<OverlayImpl>>() {}, overlays);

			node.getNode("broadcastInterval").setComment("The interval (in seconds) after which the broadcast will change.").setValue(broadcastInterval);

			node.getNode("silenceInterval").setComment("The seconds of silence between two broadcasts. Set to 0 to disable.").setValue(silenceInterval);

			loader.save(node);
		} catch (Exception e) {
			PixelmonOverlayImpl.LOGGER.error("There was a problem while saving the config.", e);
		}
	}
}
