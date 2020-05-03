package com.happyzleaf.pixelmonoverlay.impl.overlay;

import com.happyzleaf.pixelmonoverlay.api.OverlayService;
import com.happyzleaf.pixelmonoverlay.impl.PixelmonOverlayImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.happyzleaf.pixelmonoverlay.impl.Config.overlays;
import static com.happyzleaf.pixelmonoverlay.impl.Config.silenceInterval;
import static com.happyzleaf.pixelmonoverlay.impl.PixelmonOverlayImpl.plugin;

/**
 * @author happyzleaf
 * @since 25/02/2019
 */
public class OverlayBroadcaster {
	private OverlayService overlay;

	private UUID silenceTask;
	private int index;

	public OverlayBroadcaster(OverlayService overlay) {
		this.overlay = checkNotNull(overlay, "overlay");
	}

	public boolean reload() {
		if (overlays.isEmpty()) {
			PixelmonOverlayImpl.LOGGER.warn("The plugin will be disabled until you provide at least one announcement.");

			return false;
		}

		if (silenceTask != null) {
			Sponge.getScheduler().getTaskById(silenceTask).ifPresent(Task::cancel);
		}

		load(0);

		return true;
	}

	private void forward() {
		load(index == overlays.size() - 1 ? 0 : index + 1);
	}

	public void hide() {
		silenceTask = Task.builder()
				.delay(silenceInterval, TimeUnit.SECONDS)
				.execute(this::forward)
				.submit(plugin).getUniqueId();
	}

	private void load(int i) {
		index = i;
		overlay.show(overlays.get(index));
	}
}
