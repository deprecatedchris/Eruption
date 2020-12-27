package me.chris.eruption.event.match;

import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import lombok.Getter;

@Getter
public class MatchEndEvent extends MatchEvent {

	private final MatchTeam winningTeam;
	private final MatchTeam losingTeam;

	public MatchEndEvent(Match match, MatchTeam winningTeam, MatchTeam losingTeam) {
		super(match);

		this.winningTeam = winningTeam;
		this.losingTeam = losingTeam;
	}

}
