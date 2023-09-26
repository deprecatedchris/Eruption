package me.chris.eruption.command.event;

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

	private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Command({"event leave", "leave"})
	@Usage("/event leave")
	@Description("Leave an event or tournament.")
	public void eventJoin(@Sender Player player, Player target, String[] args) throws BladeExitMessage {
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Party party = plugin.getPartyManager().getParty(playerData.getUniqueId());

		boolean inTournament = plugin.getTournamentManager().isInTournament(player.getUniqueId());
		boolean inEvent = plugin.getEventManager().getEventPlaying(player) != null;

		if (inEvent) {
			this.leaveEvent(player);
		} else if (inTournament) {
			this.leaveTournament(player);
		} else {
			throw new BladeExitMessage(CC.translate("&cThere is nothing to leave."));
		}
	}

	private void leaveTournament(Player player) {
		Tournament tournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId());

		if (tournament != null) {
			this.plugin.getTournamentManager().leaveTournament(player);
		}
	}

	private void leaveEvent(Player player) {
		PracticeEvent event = plugin.getEventManager().getEventPlaying(player);

		if(event == null) {
			player.sendMessage(ChatColor.RED + "That commands doesn't exist.");
			return;
		}

		if(!this.plugin.getEventManager().isPlaying(player, event)) {
			player.sendMessage(ChatColor.RED + "You are not in an commands.");
			return;
		}

		event.leave(player);
	}
}
