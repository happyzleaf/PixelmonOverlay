package com.happyzleaf.pixelmonoverlay.api;

import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.client.gui.custom.overlays.OverlayGraphicType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

/**
 * @author happyzleaf
 * @since 25/02/2019
 */
public interface OverlayService {
	Overlay create(EnumOverlayLayout layout, OverlayGraphicType type, List<String> lines, @Nullable Long duration, @Nullable String spec, @Nullable ItemStack itemStack);

	void show(Overlay overlay);

	void hide();

	boolean reload();

	Overlay getCurrent();
}
