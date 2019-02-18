package com.happyzleaf.pixelmonoverlaybroadcaster;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.happyzleaf.pixelmonoverlaybroadcaster.Config.announcements;

/**
 * @author happyzleaf
 * @since 17/02/2019
 */
public class EventListener {
	private static UUID task = null;
	
	private static int index;
	private static Announcement announcement = null;
	
	public static void init(Object plugin) {
		index = -1;
		if (task != null) {
			Sponge.getScheduler().getTaskById(task).ifPresent(Task::cancel);
		}
		task = Task.builder().interval(Config.broadcastInterval, TimeUnit.SECONDS).execute(EventListener::forward).submit(plugin).getUniqueId();
	}
	
	private static void forward() {
		if (index == announcements.size() - 1) {
			load(0);
		} else {
			load(index + 1);
		}
	}
	
	private static void load(int i) {
		index = i;
		announcement = announcements.get(index);
		for (Player player : Sponge.getServer().getOnlinePlayers()) {
			announcement.sendTo(player);
		}
	}
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {
		if (announcement != null) {
			announcement.sendTo(player);
		}
	}
}
