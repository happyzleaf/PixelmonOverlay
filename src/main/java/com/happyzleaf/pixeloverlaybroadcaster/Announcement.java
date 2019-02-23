package com.happyzleaf.pixeloverlaybroadcaster;

import com.google.common.reflect.TypeToken;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.happyzleaf.pixeloverlaybroadcaster.bridge.PlaceholderBridge.parse;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class Announcement {
	private EnumOverlayLayout layout;
	
	private List<String> lines;
	
	private Long duration;
	
	private OverlayGraphicType type;
	private EnumSpecies species;
	private Float scale;
	private Item item;
	
	private NoticeOverlay.Builder builder;
	
	public Announcement(EnumOverlayLayout layout, OverlayGraphicType type, List<String> lines, @Nullable Long duration, @Nullable EnumSpecies species, @Nullable Float scale, @Nullable Item item) throws IllegalArgumentException {
		this.layout = checkNotNull(layout, "layout");
		this.type = checkNotNull(type, "type");
		this.lines = checkNotNull(lines, "lines");
		this.duration = duration;
		
		builder = NoticeOverlay.builder(layout, "");
		
		switch (type) {
			case PokemonSprite:
				if (species != null) {
					builder.setIconToPokemonSprite(this.species = species);
				} else {
					throw new IllegalArgumentException("You didn't specified the species.");
				}
				break;
			case Pokemon3D:
				if (species != null && scale != null) {
					builder.setIconToPokemonModel(this.species = species, this.scale = scale);
				} else {
					throw new IllegalArgumentException("You didn't specified the species or the scale.");
				}
				break;
			case ItemSprite:
				if (item != null) {
					builder.setIconToItemSprite(this.item = item);
				} else {
					throw new IllegalArgumentException("You didn't specified the item.");
				}
				break;
			case Item3D:
				if (item != null) {
					if (!item.getRegistryName().getNamespace().equals("minecraft")) { // TODO 7.0.2 => remove
						throw new IllegalArgumentException(String.format("Due to limitations in pixelmon 7.0.1, you can only use minecraft items. '%s' is forbidden.", item.getRegistryName().toString()));
					}
					builder.setIconToItemModel(this.item = item);
				} else {
					throw new IllegalArgumentException("You didn't specified the item.");
				}
				break;
		}
	}
	
	public long getDuration() {
		return duration == null ? Config.broadcastInterval : duration;
	}
	
	// TODO 7.0.2 => build() it before, then use packet.setLines(parse(lines, player)).sendTo((EntityPlayerMP) player)
	public void sendTo(Player player) {
		builder.setLines(parse(lines, player)).sendTo((EntityPlayerMP) player);
	}
	
	public void sendToAll() {
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			sendTo(player);
		}
	}
	
	public static class Serializer implements TypeSerializer<Announcement> {
		@Override
		public void serialize(@NonNull TypeToken<?> t, @Nullable Announcement obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
			if (obj == null) {
				value.setValue("null");
				return;
			}
			
			value.getNode("layout").setValue(TypeToken.of(EnumOverlayLayout.class), obj.layout);
			value.getNode("type").setValue(TypeToken.of(OverlayGraphicType.class), obj.type);
			value.getNode("lines").setValue(new TypeToken<List<String>>() {}, obj.lines);
			if (obj.duration != null) {
				value.getNode("duration").setValue(obj.duration);
			}
			
			switch (obj.type) {
				case Pokemon3D:
					value.getNode("scale").setValue(obj.scale);
				case PokemonSprite:
					value.getNode("species").setValue(TypeToken.of(EnumSpecies.class), obj.species);
					break;
				case ItemSprite:
				case Item3D:
					value.getNode("item").setValue(obj.item.getRegistryName().toString());
					break;
			}
		}
		
		@Nullable
		@Override
		public Announcement deserialize(@NonNull TypeToken<?> t, @NonNull ConfigurationNode value) throws ObjectMappingException {
			if (value.getString("").equals("null")) {
				return null;
			}
			
			EnumOverlayLayout layout = value.getNode("layout").getValue(TypeToken.of(EnumOverlayLayout.class));
			OverlayGraphicType type = value.getNode("type").getValue(TypeToken.of(OverlayGraphicType.class));
			List<String> lines = value.getNode("lines").getList(TypeToken.of(String.class));
			for (int i = 0; i < lines.size(); i++) {
				lines.set(i, lines.get(i).replace('&', '\u00A7')); //if you're reading this: dratini sucks
			}
			Long duration = value.getNode("duration").getValue(Types::asLong);
			
			EnumSpecies species = null;
			ConfigurationNode speciesNode = value.getNode("species");
			if (!speciesNode.isVirtual()) {
				species = speciesNode.getValue(TypeToken.of(EnumSpecies.class));
			}
			
			Float scale = value.getNode("scale").getValue(Types::asFloat);
			
			Item item = null;
			String itemName = value.getNode("item").getString();
			if (itemName != null) {
				item = Item.REGISTRY.getObject(new ResourceLocation(itemName));
				if (item == null) {
					throw new ObjectMappingException(String.format("Cannot find any item with the id '%s'.", itemName));
				}
			}
			
			try {
				return new Announcement(layout, type, lines, duration, species, scale, item);
			} catch (IllegalArgumentException e) {
				throw new ObjectMappingException(e);
			}
		}
	}
}
