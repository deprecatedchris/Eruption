package me.chris.eruption.command.event.user;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveEventCommand {

	@Command({"event leave", "leave"})
	@Usage("/event leave")
	@Description("Leave an event or tournament.")
	public static void eventJoin(@Sender Player player) throws BladeExitMessage {
		PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
		Party party = EruptionPlugin.getInstance().getPartyManager().getParty(playerData.getUniqueId());

		boolean inTournament = EruptionPlugin.getInstance().getTournamentManager().isInTournament(player.getUniqueId());
		boolean inEvent = EruptionPlugin.getInstance().getEventManager().getEventPlaying(player) != null;

		if (inEvent) {
			leaveEvent(player);
		} else if (inTournament) {
			leaveTournament(player);
		} else {
			throw new BladeExitMessage(CC.translate("&cThere is nothing to leave."));
		}
	}

	private static void leaveTournament(Player player) {
		Tournament tournament = EruptionPlugin.getInstance().getTournamentManager().getTournament(player.getUniqueId());

		if (tournament != null) {
			EruptionPlugin.getInstance().getTournamentManager().leaveTournament(player);
		}
	}

	private static void leaveEvent(Player player) {
		PracticeEvent event = EruptionPlugin.getInstance().getEventManager().getEventPlaying(player);

		if(event == null) {
			player.sendMessage(ChatColor.RED + "That commands doesn't exist.");
			return;
		}

		if(!EruptionPlugin.getInstance().getEventManager().isPlaying(player, event)) {
			player.sendMessage(ChatColor.RED + "You are not in an commands.");
			return;
		}

		event.leave(player);
	}
}
