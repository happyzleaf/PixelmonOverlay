package com.happyzleaf.pixelbroadcaster.manager;

import com.happyzleaf.pixelbroadcaster.Announcement;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.comm.packetHandlers.customOverlays.CustomNoticePacket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.happyzleaf.pixelbroadcaster.Config.*;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class OverlayManager {
	private static Object plugin;
	
	static UUID task = null;
	
	private static int index;
	static Announcement announcement = null;
	
	public static void init(Object plugin) {
		OverlayManager.plugin = plugin;
		stop();
		load(0);
		start();
	}
	
	private static void start() {
		task = Task.builder().delay(silenceInterval, TimeUnit.SECONDS).execute(OverlayManager::forward).submit(plugin).getUniqueId();
	}
	
	static void stop() {
		if (task != null) {
			Sponge.getScheduler().getTaskById(task).ifPresent(Task::cancel);
		}
	}
	
	static void forward() {
		if (index == announcements.size() - 1) {
			load(0);
		} else {
			load(index + 1);
		}
		task = Task.builder().delay(announcement.getDuration(), TimeUnit.SECONDS).execute(OverlayManager::silence).submit(plugin).getUniqueId();
	}
	
	private static void silence() {
		announcement = null;
		
		// TODO 7.0.2 => Pixelmon.network.sendToAll(new CustomNoticePacket().setEnabled(false))
		CustomNoticePacket packet = new CustomNoticePacket();
		packet.setEnabled(false);
		Pixelmon.network.sendToAll(packet);
		//
		
		start();
	}
	
	private static void load(int i) {
		index = i;
		announcement = announcements.get(index);
		announcement.sendToAll();
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
		if (announcement != null) {
			announcement.sendTo(player);
		}
	}
}
