package me.chris.eruption.match;

import me.chris.eruption.util.team.KillableTeam;

import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MatchTeam extends KillableTeam {

	private final int teamID;

	public MatchTeam(UUID leader, List<UUID> players, int teamID) {
		super(leader, players);
		this.teamID = teamID;
	}
}
