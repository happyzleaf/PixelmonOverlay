package com.happyzleaf.pixelmonoverlay.impl;

import com.google.common.reflect.TypeToken;
import com.happyzleaf.pixelmonoverlay.api.Overlay;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.happyzleaf.pixelmonoverlay.impl.bridge.PlaceholderBridge.parse;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class OverlayImpl implements Overlay {
	private EnumOverlayLayout layout;
	
	private List<String> originalLines; // I was wasting too much time on this, I'll just fix it quickly, maybe I'll come back later.
	private List<String> lines;
	
	private Long duration;
	
	private OverlayGraphicType type;
	
	private String spec;
	
	private ItemStack itemStack;
	
	private CustomNoticePacket packet;
	
	public OverlayImpl(EnumOverlayLayout layout, OverlayGraphicType type, List<String> lines, @Nullable Long duration, @Nullable String spec, @Nullable ItemStack itemStack) throws IllegalArgumentException {
		this.layout = checkNotNull(layout, "layout");
		this.type = checkNotNull(type, "type");
		
		this.originalLines = checkNotNull(lines, "lines");
		this.lines = this.originalLines.stream().map(s -> s.replaceAll("(?<!\\\\)&", "\u00A7")).collect(Collectors.toList());
		checkArgument(duration == null || duration >= 0, "The duration must be null, 0 or positive.");
		
		this.duration = duration;
		
		NoticeOverlay.Builder builder = NoticeOverlay.builder()
				.setLayout(layout)
				.setLines(); // TODO 7.0.4 => remove
		
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
	
	@Override
	public long getDuration() {
		return duration == null ? Config.broadcastInterval : duration;
	}
	
	@Override
	public CustomNoticePacket build(Player player) {
		return packet.setLines(parse(lines, player).toArray(new String[0]));
	}
	
	public static class Serializer implements TypeSerializer<OverlayImpl> {
		@Override
		public void serialize(@NonNull TypeToken<?> t, @Nullable OverlayImpl obj, @NonNull ConfigurationNode value) throws ObjectMappingException {
			if (obj == null) {
				value.setValue("null");
				return;
			}
			
			value.getNode("layout").setValue(TypeToken.of(EnumOverlayLayout.class), obj.layout);
			value.getNode("type").setValue(TypeToken.of(OverlayGraphicType.class), obj.type);
			
			if (!obj.originalLines.isEmpty()) {
				value.getNode("lines").setValue(obj.originalLines);
			}
			
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
		public OverlayImpl deserialize(@NonNull TypeToken<?> t, @NonNull ConfigurationNode value) throws ObjectMappingException {
			if (value.getString("").equals("null")) {
				return null;
			}
			
			EnumOverlayLayout layout = value.getNode("layout").getValue(TypeToken.of(EnumOverlayLayout.class));
			OverlayGraphicType type = value.getNode("type").getValue(TypeToken.of(OverlayGraphicType.class));
			
			List<String> lines = new ArrayList<>();
			ConfigurationNode linesNode = value.getNode("lines");
			if (!linesNode.isVirtual()) {
				lines.addAll(linesNode.getList(TypeToken.of(String.class)));
			}
			
			Long duration = value.getNode("duration").getValue(Types::asLong);
			
			String spec = value.getNode("spec").getString();
			
			ConfigurationNode itemStackNode = value.getNode("itemStack");
			ItemStack itemStack = itemStackNode.isVirtual() ? null : itemStackNode.getValue(TypeToken.of(ItemStack.class));
			
			try {
				return new OverlayImpl(layout, type, lines, duration, spec, itemStack);
			} catch (IllegalArgumentException e) {
				throw new ObjectMappingException(e);
			}
		}
	}
}
