package me.chris.eruption.runnable;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.oitc.OITCPlayer;
import me.chris.eruption.util.timer.impl.EnderpearlTimer;

@RequiredArgsConstructor
public class ExpBarRunnable implements Runnable {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Override
	public void run() {

		EnderpearlTimer timer = EruptionPlugin.getInstance().getTimerManager().getTimer(EnderpearlTimer.class);

		for (UUID uuid : timer.getCooldowns().keySet()) {
			Player player = this.plugin.getServer().getPlayer(uuid);

			if (player != null) {
				long time = timer.getRemaining(player);
				int seconds = (int) Math.round((double) time / 1000.0D);

				player.setLevel(seconds);
				player.setExp((float) time / 15000.0F);
			}
		}

		for(Player player : this.plugin.getServer().getOnlinePlayers()) {
			PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);

			if(event != null && event instanceof OITCEvent) {
				OITCEvent oitcEvent = (OITCEvent) event;
				OITCPlayer oitcPlayer = oitcEvent.getPlayer(player.getUniqueId());

				if(oitcPlayer != null && oitcPlayer.getState() != OITCPlayer.OITCState.WAITING) {
					int seconds = oitcEvent.getGameTask().getTime();

					if(seconds >= 0) {
						player.setLevel(seconds);
					}
				}
			}
		}
	}
}
