package com.happyzleaf.pixelmonoverlay.api;

import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import org.spongepowered.api.entity.living.player.Player;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public interface Overlay {
	long getDuration();

	CustomNoticePacket build(Player player);
}
