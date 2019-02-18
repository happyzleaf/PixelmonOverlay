package com.happyzleaf.pixeloverlaybroadcaster;

import com.google.inject.Inject;
import com.happyzleaf.pixeloverlaybroadcaster.bridge.PlaceholderBridge;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(id = PixelOverlayBroadcaster.PLUGIN_ID, name = PixelOverlayBroadcaster.PLUGIN_NAME, version = PixelOverlayBroadcaster.VERSION,
		description = "Lets you broadcast a set of messages through the pixelmon's topbar overlay.",
		url = "https://happyzleaf.com/", authors = {"happyzleaf"},
		dependencies = {@Dependency(id = "pixelmon")})
public class PixelOverlayBroadcaster {
	public static final String PLUGIN_ID = "pixeloverlaybroadcaster";
	public static final String PLUGIN_NAME = "PixelOverlayBroadcaster";
	public static final String VERSION = "1.0.0";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private File configFile;
	
	@Listener
	public void init(GameInitializationEvent event) {
		Config.init(configLoader, configFile);
		
		LOGGER.info(String.format("%s v%s loaded! This plugin was made by happyzleaf and kindly offered by GT86.", PLUGIN_NAME, VERSION));
	}
	
	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		PlaceholderBridge.setup(this);
		
		Sponge.getEventManager().registerListeners(this, new EventListener());
		load();
	}
	
	@Listener
	public void onReload(GameReloadEvent event) {
		Config.loadConfig();
		if (load()) {
			LOGGER.info("Reloaded.");
		}
	}
	
	private boolean load() {
		if (Config.announcements.isEmpty()) {
			LOGGER.warn("The plugin will be disabled until you provide at least one announcement.");
			return false;
		} else {
			EventListener.init(this);
			return true;
		}
	}
}
