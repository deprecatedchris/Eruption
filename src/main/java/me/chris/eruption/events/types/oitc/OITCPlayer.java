package me.chris.eruption.events.types.oitc;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.events.EventPlayer;
import me.chris.eruption.events.PracticeEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Setter
@Getter
public class OITCPlayer extends EventPlayer {

	private OITCState state = OITCState.WAITING;
	private int score = 0;
	private int lives = 5;
	private BukkitTask respawnTask;
	private OITCPlayer lastKiller;

	public OITCPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum OITCState {
		WAITING, PREPARING, FIGHTING, RESPAWNING, ELIMINATED
	}
}
