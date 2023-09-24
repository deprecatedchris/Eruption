package me.chris.eruption.arena.arena;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.type.ArenaType;
import me.chris.eruption.util.other.LocationUtil;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Arena {

	private final String name;

	private List<StandaloneArena> standaloneArenas;
	private List<StandaloneArena> availableArenas;
	private LocationUtil a;
	private LocationUtil b;

	private LocationUtil min;
	private LocationUtil max;

	private LocationUtil eventJoinLocation;

	private boolean enabled;
	private boolean isEvent = false;

	private ArenaType arenaType = ArenaType.NONE;

	public StandaloneArena getAvailableArena() {
		StandaloneArena arena = this.availableArenas.get(0);

		this.availableArenas.remove(0);

		return arena;
	}

	public void addStandaloneArena(StandaloneArena arena) {
		this.standaloneArenas.add(arena);
	}

	public void addAvailableArena(StandaloneArena arena) {
		this.availableArenas.add(arena);
	}
}
