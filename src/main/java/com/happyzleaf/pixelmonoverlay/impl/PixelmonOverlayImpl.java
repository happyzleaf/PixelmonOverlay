package com.happyzleaf.pixelmonoverlay.impl;

import com.happyzleaf.pixelmonoverlay.api.OverlayService;
import com.happyzleaf.pixelmonoverlay.impl.bridge.PlaceholderBridge;
import com.happyzleaf.pixelmonoverlay.impl.command.OverlayBroadcastCommand;
import com.happyzleaf.pixelmonoverlay.impl.overlay.OverlayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = PixelmonOverlayImpl.PLUGIN_ID, name = PixelmonOverlayImpl.PLUGIN_NAME, version = PixelmonOverlayImpl.VERSION,
		description = "Gives plugins access to the pixelmon overlay, and provides some useful features like broadcasts and such.",
		url = "https://happyzleaf.com/", authors = {"happyzleaf"},
		dependencies = {@Dependency(id = "pixelmon", version = "7.2.2")})
public class PixelmonOverlayImpl {
	public static final String PLUGIN_ID = "pixelmonoverlay";
	public static final String PLUGIN_NAME = "PixelmonOverlay";
	public static final String VERSION = "1.1.0";

	public static final Logger LOGGER = LoggerFactory.getLogger(PLUGIN_NAME);

	public static PixelmonOverlayImpl plugin;
	public static OverlayService service;

	@Listener
	public void preInit(GamePreInitializationEvent event) {
		plugin = this;

		Sponge.getServiceManager().setProvider(this, OverlayService.class, new OverlayServiceImpl());
	}

	@Listener
	public void init(GameInitializationEvent event) {
		Config.init(Sponge.getConfigManager().getSharedConfig(this).getConfigPath());

		LOGGER.info(String.format("%s v%s loaded! This plugin was made by happyzleaf and kindly offered by GT86.", PLUGIN_NAME, VERSION));
	}

	@Listener
	public void serverStarted(GameStartedServerEvent event) {
		PlaceholderBridge.setup(this);

		service.reload();

		OverlayBroadcastCommand.register(this);
	}

	@Listener
	public void reload(GameReloadEvent event) {
		Config.loadConfig();

		if (service.reload()) {
			LOGGER.info("Reloaded.");
		}
	}

	@Listener
	public void onServiceProvider(ChangeServiceProviderEvent event) {
		if (event.getService().equals(OverlayService.class)) {
			if (service != null) {
				Sponge.getEventManager().unregisterListeners(service);
			}

			service = (OverlayService) event.getNewProvider();
			Sponge.getEventManager().registerListeners(this, service);
		}
	}
}
