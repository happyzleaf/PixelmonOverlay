package com.happyzleaf.pixelmonoverlay.impl.command;

import com.happyzleaf.pixelmonoverlay.api.Overlay;
import com.happyzleaf.pixelmonoverlay.impl.Config;
import com.happyzleaf.pixelmonoverlay.impl.PixelmonOverlayImpl;
import com.happyzleaf.pixelmonoverlay.impl.Utils;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author happyzleaf
 * @since 03/05/2020
 */
public class OverlayBroadcastCommand implements CommandExecutor {
	public static void register(Object plugin) {
		Sponge.getCommandManager().register(plugin, CommandSpec.builder()
				.permission("pixelmonoverlay.command.pixelbroadcast")
				.arguments(
						GenericArguments.integer(Text.of("id")) // TODO remove
						/*GenericArguments.firstParsing(
								GenericArguments.integer(Text.of("id")),
								GenericArguments.seq(
										GenericArguments.enumValue(Text.of("layout"), EnumOverlayLayout.class),
										GenericArguments.firstParsing(
												GenericArguments.enumValue(Text.of("species"), EnumSpecies.class),
												GenericArguments.seq(
														GenericArguments.enumValue(Text.of("species"), EnumSpecies.class),
														GenericArguments.choicesInsensitive(Text.of("type"), Utils.populateMap(new LinkedHashMap<>(), "sprite", OverlayGraphicType.PokemonSprite, "3d", OverlayGraphicType.Pokemon3D))
												),
												GenericArguments.catalogedElement(Text.of("item"), ItemType.class)
										),
										GenericArguments.remainingJoinedStrings(Text.of("lines"))
								)),
						GenericArguments.optional(GenericArguments.longNum(Text.of("duration")))*/
				)
				.executor(new OverlayBroadcastCommand())
				.build(), "pixelbroadcast");
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Long duration = args.<Long>getOne("duration").orElse(null);

		Overlay announcement;
		if (args.hasAny("id")) {
			int id = args.<Integer>getOne("id").get() - 1;
			if (id < 0 || id >= Config.overlays.size()) {
				throw new CommandException(Text.of(TextColors.RED, String.format("[PixelBroadcaster] The id must be within 1 and %d.", Config.overlays.size())));
			}

			announcement = Config.overlays.get(id);
		} else {
			EnumOverlayLayout layout = args.<EnumOverlayLayout>getOne("layout").get();
			List<String> lines = new ArrayList<>(Arrays.asList(args.<String>getOne("lines").get().split("<br>")));

			String species = args.<EnumSpecies>getOne("species").map(EnumSpecies::getPokemonName).orElse(null);
			OverlayGraphicType type = args.<OverlayGraphicType>getOne("type").orElse(OverlayGraphicType.ItemStack);
			ItemType item = args.<ItemType>getOne("item").orElse(null);

			announcement = PixelmonOverlayImpl.service.create(layout, type, lines, duration, species, ItemStack.of(item));
		}

		PixelmonOverlayImpl.service.show(announcement);

		src.sendMessage(Text.of(TextColors.GREEN, "[PixelBroadcaster] Successfully broadcasted the message."));

		return CommandResult.success();
	}
}
