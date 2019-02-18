package com.happyzleaf.pixelbroadcaster;

import com.google.common.reflect.TypeToken;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.init.Items;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class Config {
	private static ConfigurationLoader<CommentedConfigurationNode> loader;
	private static CommentedConfigurationNode node;
	private static File file;
	
	/** This has to be an arraylist so the positions are preserved. */
	public static ArrayList<Announcement> announcements = new ArrayList<>(Arrays.asList(
			new Announcement(EnumOverlayLayout.LEFT_AND_RIGHT, OverlayGraphicType.PokemonSprite, new ArrayList<>(Arrays.asList("Hi %player%", "Currently there are %server_online% players connected", "X: %player_x% Y: %player_y% Z: %player_z%")), null, EnumSpecies.Pikachu, null, null),
			new Announcement(EnumOverlayLayout.LEFT, OverlayGraphicType.Pokemon3D, new ArrayList<>(Arrays.asList("Player: %player_displayname%", "Health: %player_health%/%player_max_health%", "Ping: %player_ping%")), null, EnumSpecies.Solgaleo, 5f, null),
			new Announcement(EnumOverlayLayout.RIGHT, OverlayGraphicType.ItemSprite, new ArrayList<>(Arrays.asList("Joined in: %player_first_join%", "Played for: %player_time_played%")), 10L, null, null, Items.APPLE),
			new Announcement(EnumOverlayLayout.LEFT_AND_RIGHT, OverlayGraphicType.Item3D, new ArrayList<>(Arrays.asList("Server: %server_online%/%server_max_players%", "%server_motd%", "TPS: %server_tps%")), 25L, null, null, Items.APPLE)
	));
	
	public static long broadcastInterval = 300; // in seconds
	public static long silenceInterval = 300; // in seconds
	
	public static void init(ConfigurationLoader<CommentedConfigurationNode> loader, File file) {
		Config.loader = loader;
		Config.file = file;
		
		TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Announcement.class), new Announcement.Serializer());
		
		loadConfig();
	}
	
	public static void loadConfig() {
		if (!file.exists()) {
			saveConfig();
		}
		
		load();
		
		try {
			announcements.clear();
			announcements.addAll(node.getNode("announcements").getList(TypeToken.of(Announcement.class)));
			
			broadcastInterval = node.getNode("broadcastInterval").getLong(broadcastInterval);
			if (broadcastInterval <= 0) {
				throw new ObjectMappingException(String.format("broadcastInterval must be positive, right now it's %d.", broadcastInterval));
			}
			silenceInterval = node.getNode("silenceInterval").getLong(silenceInterval);
			if (silenceInterval < 0) {
				throw new ObjectMappingException(String.format("silenceInterval must be 0 or higher, right now it's %d.", silenceInterval));
			}
		} catch (ObjectMappingException e) {
			PixelBroadcaster.LOGGER.error("There was a problem while loading the config.", e);
		}
		
		saveConfig();
	}
	
	public static void saveConfig() {
		load();
		
		try {
			node.getNode("announcements").setValue(new TypeToken<List<Announcement>>() {}, announcements);
			
			node.getNode("broadcastInterval").setComment("The interval (in seconds) after which the broadcast will change.").setValue(broadcastInterval);
			node.getNode("silenceInterval").setComment("The seconds of silence between two broadcasts. Set to 0 to disable.").setValue(silenceInterval);
		} catch (ObjectMappingException e) {
			PixelBroadcaster.LOGGER.error("There was a problem while saving the config.", e);
		}
		
		save();
	}
	
	private static void load() {
		try {
			node = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void save() {
		try {
			loader.save(node);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
