package com.happyzleaf.pixelbroadcaster.bridge;

import me.rojo8399.placeholderapi.PlaceholderService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class PlaceholderAPI implements EventListener<ChangeServiceProviderEvent> {
	private PlaceholderService service;
	
	PlaceholderAPI(Object plugin) {
		service = Sponge.getServiceManager().provideUnchecked(PlaceholderService.class);
		Sponge.getEventManager().registerListener(plugin, ChangeServiceProviderEvent.class, this);
	}
	
	@Override
	public void handle(ChangeServiceProviderEvent event) {
		if (event.getService().equals(PlaceholderService.class)) {
			service = (PlaceholderService) event.getNewProviderRegistration().getProvider();
		}
	}
	
	String parse(String text, Object source) {
		return TextSerializers.FORMATTING_CODE.serialize(service.replacePlaceholders(text, source, null));
	}
	
	// TODO a bit heavy? Maybe papi can parse multiple lines directly
	List<String> parse(List<String> texts, Object source) {
		List<String> replacements = new ArrayList<>(texts.size());
		for (String text : texts) {
			replacements.add(parse(text, source));
		}
		return replacements;
	}
}
