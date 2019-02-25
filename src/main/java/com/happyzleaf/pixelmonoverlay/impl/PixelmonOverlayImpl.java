package com.happyzleaf.pixelmonoverlay.impl;

import com.google.inject.Inject;
import com.happyzleaf.pixelmonoverlay.api.OverlayService;
import com.happyzleaf.pixelmonoverlay.impl.bridge.PlaceholderBridge;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(id = PixelmonOverlayImpl.PLUGIN_ID, name = PixelmonOverlayImpl.PLUGIN_NAME, version = PixelmonOverlayImpl.VERSION,
		description = "Gives plugin access to the pixelmon overlay, and provides some useful features like broadcasts and such.",
		url = "https://happyzleaf.com/", authors = {"happyzleaf"},
		dependencies = {@Dependency(id = "pixelmon", version = "7.0.3")})
public class PixelmonOverlayImpl {
	public static final String PLUGIN_ID = "pixelmonoverlay";
	public static final String PLUGIN_NAME = "PixelmonOverlay";
	public static final String VERSION = "1.1.0";
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);
	
	public static PixelmonOverlayImpl plugin;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	
	@Inject
	@DefaultConfig(sharedRoot = true)
	private File configFile;
	
	@Listener
	public void preInit(GamePreInitializationEvent event) {
		plugin = this;
		
		OverlayService impl = new OverlayServiceImpl();
		Sponge.getServiceManager().setProvider(this, OverlayService.class, impl);
		Sponge.getEventManager().registerListeners(this, impl);
	}
	
	@Listener
	public void init(GameInitializationEvent event) {
		Config.init(configLoader, configFile);
		
		LOGGER.info(String.format("%s v%s loaded! This plugin was made by happyzleaf and kindly offered by GT86.", PLUGIN_NAME, VERSION));
	}
	
	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		PlaceholderBridge.setup(this);
		
		load();
		
		// OverlayBroadcastCommand.register(this); TODO coming in the next version!
	}
	
	@Listener
	public void onReload(GameReloadEvent event) {
		Config.loadConfig();
		if (load()) {
			LOGGER.info("Reloaded.");
		}
	}
	
	private boolean load() {
		return Sponge.getServiceManager().provideUnchecked(OverlayService.class).reload();
	}
}
