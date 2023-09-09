package me.chris.eruption.events.types.parkour;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.events.EventPlayer;
import me.chris.eruption.events.PracticeEvent;

import java.util.UUID;

@Setter
@Getter
public class ParkourPlayer extends EventPlayer {

	private ParkourState state = ParkourState.WAITING;
	private LocationUtil lastCheckpoint;
	private int checkpointId;

	public ParkourPlayer(UUID uuid, PracticeEvent event) {
		super(uuid, event);
	}

	public enum ParkourState {
		WAITING, INGAME
	}
}
