package me.chris.eruption.event.match;

import me.chris.eruption.match.Match;

public class MatchStartEvent extends MatchEvent {
	public MatchStartEvent(Match match) {
		super(match);
	}
}
