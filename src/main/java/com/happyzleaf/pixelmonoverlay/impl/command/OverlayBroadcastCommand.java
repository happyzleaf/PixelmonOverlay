package com.happyzleaf.pixelmonoverlay.impl.command;

import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * I'm gonna implement these through builders from the config
 * basically you will be able to make an announcement like in the config,
 * and also declare placeholders, like 'spec = %1$s' and 'lines = [%2$s, %3$s, %3$s]', and in the command
 * you will be able to parse those in the command, by typing
 * something like /pixelmonoverlay broadcast <id> "pikachu s" "First line" "Second line" "Third line".
 * Even thought I could make an exception for the lines, but the general idea is this.
 *
 * @author happyzleaf
 * @since 18/02/2019
 */
public class OverlayBroadcastCommand implements CommandExecutor {
	private static Object plugin;
	
	public static void register(Object plugin) {
		OverlayBroadcastCommand.plugin = plugin;
		Sponge.getCommandManager().register(plugin, CommandSpec.builder()
				.permission("pixeloverlaybroadcaster.command.pixelbroadcast")
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
				.executor(new OverlayBroadcastCommand())
				.build(), "pixelbroadcast");
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		src.sendMessage(Text.of(TextColors.RED, "Don't even try that."));
		return CommandResult.empty();
//		Long duration = args.<Long>getOne("duration").orElse(null);
//
//		if (args.hasAny("id")) {
//			int id = args.<Integer>getOne("id").get() - 1;
//			if (id < 0 || id >= Config.overlays.size()) {
//				throw new CommandException(Text.of(TextColors.RED, String.format("[PixelBroadcaster] The id must be within 1 and %d.", Config.overlays.size())));
//			}
//			announcement = Config.overlays.get(id);
//		} else {
//			EnumOverlayLayout layout = args.<EnumOverlayLayout>getOne("layout").get();
//			List<String> lines = new ArrayList<>(Arrays.asList(args.<String>getOne("lines").get().split("\n")));
//
//			EnumSpecies species = args.<EnumSpecies>getOne("species").orElse(null);
//			Float scale = (float) ((double) args.<Double>getOne("scale").orElse(-1d));
//			if (scale < 0) scale = null;
//			Item item = null;
//			String itemName = args.<String>getOne("item").orElse(null);
//			if (itemName != null) {
//				item = Item.REGISTRY.getObject(new ResourceLocation(itemName));
//				if (item == null) {
//					throw new CommandException(Text.of(TextColors.RED, String.format("[PixelBroadcaster] The item '%s' was not found.", itemName)));
//				}
//			}
//
//			OverlayGraphicType type = species == null ? OverlayGraphicType.ItemStack : scale == null ? OverlayGraphicType.PokemonSprite : OverlayGraphicType.Pokemon3D; // TODO currently this doesn't let you create Item3D overlays, but is it worth it? In 7.0.2 it's gonna be useless anyway.
//
//			announcement = new OverlayImpl(layout, type, lines, duration, species, scale, item);
//		}
//
//		stop();
//		announcement.sendToAll();
//		if (!Config.overlays.isEmpty()) {
//			task = Task.builder().delay(duration == null ? announcement.getDuration() : duration, TimeUnit.SECONDS).execute(OverlayBroadcaster::forward).submit(plugin).getUniqueId();
//		}
//		return null;
	}
}
