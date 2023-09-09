package me.chris.eruption.util.other;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.team.KillableTeam;
import me.chris.eruption.tournament.TournamentTeam;

import java.util.UUID;
import org.bukkit.entity.Player;

public class TeamUtil {

	public static String getNames(KillableTeam team) {
		StringBuilder names = new StringBuilder();

		for (int i = 0; i < team.getPlayers().size(); i++) {
			UUID teammateUUID = team.getPlayers().get(i);
			Player teammate = EruptionPlugin.getInstance().getServer().getPlayer(teammateUUID);

			String name = "";

			if (teammate == null) {
				if (team instanceof TournamentTeam) {
					name = ((TournamentTeam) team).getPlayerName(teammateUUID);
				}
			} else {
				name = teammate.getName();
			}

			int players = team.getPlayers().size();

			if (teammate != null) {
				names.append(name).append(((players - 1) == i) ? "" : ((players - 2) == i) ? (players > 2 ? "," : "") + " & " : ", ");
			}
		}

		return names.toString();
	}
}
