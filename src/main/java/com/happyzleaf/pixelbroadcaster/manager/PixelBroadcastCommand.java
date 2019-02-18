package com.happyzleaf.pixelbroadcaster.manager;

import com.happyzleaf.pixelbroadcaster.Announcement;
import com.happyzleaf.pixelbroadcaster.Config;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.happyzleaf.pixelbroadcaster.manager.OverlayManager.*;

/**
 * @author happyzleaf
 * @since 18/02/2019
 */
public class PixelBroadcastCommand implements CommandExecutor {
	private static Object plugin;
	
	public static void register(Object plugin) {
		PixelBroadcastCommand.plugin = plugin;
		Sponge.getCommandManager().register(plugin, CommandSpec.builder()
				.permission("pixelbroadcaster.command.pixelbroadcast")
				.arguments(
						GenericArguments.firstParsing(
								GenericArguments.integer(Text.of("id")),
								GenericArguments.seq(
										GenericArguments.enumValue(Text.of("layout"), EnumOverlayLayout.class),
										GenericArguments.firstParsing(
												GenericArguments.enumValue(Text.of("species"), EnumSpecies.class),
												GenericArguments.seq(
														GenericArguments.enumValue(Text.of("species"), EnumSpecies.class),
														GenericArguments.doubleNum(Text.of("scale"))
												),
												GenericArguments.string(Text.of("item"))
										),
										GenericArguments.remainingJoinedStrings(Text.of("lines"))
								)),
						GenericArguments.optional(GenericArguments.longNum(Text.of("duration")))
				)
				.executor(new PixelBroadcastCommand())
				.build(), "pixelbroadcast");
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		Long duration = args.<Long>getOne("duration").orElse(null);
		
		if (args.hasAny("id")) {
			int id = args.<Integer>getOne("id").get() - 1;
			if (id < 0 || id >= Config.announcements.size()) {
				throw new CommandException(Text.of(TextColors.RED, String.format("[PixelBroadcaster] The id must be within 1 and %d.", Config.announcements.size())));
			}
			announcement = Config.announcements.get(id);
		} else {
			EnumOverlayLayout layout = args.<EnumOverlayLayout>getOne("layout").get();
			List<String> lines = new ArrayList<>(Arrays.asList(args.<String>getOne("lines").get().split("\n")));
			
			EnumSpecies species = args.<EnumSpecies>getOne("species").orElse(null);
			Float scale = (float) ((double) args.<Double>getOne("scale").orElse(-1d));
			if (scale < 0) scale = null;
			Item item = null;
			String itemName = args.<String>getOne("item").orElse(null);
			if (itemName != null) {
				item = Item.REGISTRY.getObject(new ResourceLocation(itemName));
				if (item == null) {
					throw new CommandException(Text.of(TextColors.RED, String.format("[PixelBroadcaster] The item '%s' was not found.", itemName)));
				}
				if (!item.getRegistryName().getNamespace().equals("minecraft")) { // TODO 7.0.2 => remove
					throw new CommandException(Text.of(TextColors.RED, String.format("[PixelBroadcaster] The item '%s' is forbidden! Only vanilla items are enabled.", itemName)));
				}
			}
			
			OverlayGraphicType type = species == null ? OverlayGraphicType.ItemSprite : scale == null ? OverlayGraphicType.PokemonSprite : OverlayGraphicType.Pokemon3D; // TODO currently this doesn't let you create Item3D announcements, but is it worth it? In 7.0.2 it's gonna be useless anyway.
			
			announcement = new Announcement(layout, type, lines, duration, species, scale, item);
		}
		
		stop();
		announcement.sendToAll();
		if (!Config.announcements.isEmpty()) {
			task = Task.builder().delay(duration == null ? announcement.getDuration() : duration, TimeUnit.SECONDS).execute(OverlayManager::forward).submit(plugin).getUniqueId();
		}
		return null;
	}
}
