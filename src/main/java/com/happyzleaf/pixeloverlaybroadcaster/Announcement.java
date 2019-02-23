package com.happyzleaf.pixeloverlaybroadcaster;

import com.google.common.reflect.TypeToken;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

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
	
	private String spec;
	
	private ItemStack itemStack;
	
	private CustomNoticePacket packet;
	
	public Announcement(EnumOverlayLayout layout, OverlayGraphicType type, List<String> lines, @Nullable Long duration, @Nullable String spec, @Nullable ItemStack itemStack) throws IllegalArgumentException {
		this.layout = checkNotNull(layout, "layout");
		this.type = checkNotNull(type, "type");
		this.lines = checkNotNull(lines, "lines");
		this.duration = duration;
		
		NoticeOverlay.Builder builder = NoticeOverlay.builder();
		
		switch (type) {
			case PokemonSprite:
				if (spec != null) {
					builder.setPokemonSprite(new PokemonSpec(this.spec = spec));
				} else {
					throw new IllegalArgumentException("You didn't specified the species.");
				}
				break;
			case Pokemon3D:
				if (spec != null) {
					builder.setPokemon3D(new PokemonSpec(this.spec = spec));
				} else {
					throw new IllegalArgumentException("You didn't specified the species.");
				}
				break;
			case ItemStack:
				if (itemStack != null) {
					builder.setItemStack(net.minecraft.item.ItemStack.class.cast(this.itemStack = itemStack));
				} else {
					throw new IllegalArgumentException("You didn't specified the item.");
				}
				break;
		}
		
		packet = builder.build();
	}
	
	public long getDuration() {
		return duration == null ? Config.broadcastInterval : duration;
	}
	
	public void sendTo(Player player) {
		Pixelmon.network.sendTo(packet.setLines(parse(lines, player).toArray(new String[0])), (EntityPlayerMP) player);
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
				case PokemonSprite:
					value.getNode("spec").setValue(obj.spec);
					break;
				case ItemStack:
					value.getNode("itemStack").setValue(TypeToken.of(ItemStack.class), obj.itemStack);
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
			
			String specString = value.getNode("species").getString();
			
			ConfigurationNode itemStackNode = value.getNode("item");
			ItemStack itemStack = itemStackNode.isVirtual() ? null : itemStackNode.getValue(TypeToken.of(ItemStack.class));
			
			try {
				return new Announcement(layout, type, lines, duration, specString, itemStack);
			} catch (IllegalArgumentException e) {
				throw new ObjectMappingException(e);
			}
		}
	}
}
