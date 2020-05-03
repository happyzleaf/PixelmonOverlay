package com.happyzleaf.pixelmonoverlay.impl.overlay;

import com.happyzleaf.pixelmonoverlay.api.Overlay;
import com.happyzleaf.pixelmonoverlay.api.OverlayService;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.happyzleaf.pixelmonoverlay.impl.PixelmonOverlayImpl.plugin;

/**
 * @author happyzleaf
 * @since 25/02/2019
 */
public class OverlayServiceImpl implements OverlayService {
	private static final CustomNoticePacket SILENCE_PACKET = new CustomNoticePacket().setEnabled(false);

	private OverlayBroadcaster broadcaster = new OverlayBroadcaster(this);

	private Overlay current = null;
	private UUID task = null;

	@Override
	public Overlay create(EnumOverlayLayout layout, OverlayGraphicType type, List<String> lines, @Nullable Long duration, @Nullable String spec, @Nullable ItemStack itemStack) {
		return new OverlayImpl(layout, type, lines, duration, spec, itemStack);
	}

	@Override
	public void show(Overlay overlay) {
		current = checkNotNull(overlay, "overlay");
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			showTo(player);
		}

		task = Task.builder()
				.delay(current.getDuration(), TimeUnit.SECONDS)
				.execute(this::hide)
				.submit(plugin).getUniqueId();
	}

	private void showTo(Player player) {
		Pixelmon.network.sendTo(checkNotNull(current, "overlay").build(player), (EntityPlayerMP) checkNotNull(player, "player"));
	}

	@Override
	public void hide() {
		task = null;
		current = null;

		Pixelmon.network.sendToAll(SILENCE_PACKET);

		broadcaster.hide();
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
		if (current != null) {
			showTo(player);
		}
	}

	@Override
	public boolean reload() {
		if (task != null) {
			Sponge.getScheduler().getTaskById(task).ifPresent(t -> t.getConsumer().accept(t));
		}

		return broadcaster.reload();
	}

	@Override
	public Overlay getCurrent() {
		return current;
	}
}
