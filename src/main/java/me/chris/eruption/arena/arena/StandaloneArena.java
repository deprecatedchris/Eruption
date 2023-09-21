package me.chris.eruption.arena.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chris.eruption.util.other.LocationUtil;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class StandaloneArena {

	private LocationUtil a;
	private LocationUtil b;

	private LocationUtil min;
	private LocationUtil max;

}
