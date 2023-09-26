package me.chris.eruption.command.event;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.tournament.TournamentState;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinEventCommand {

	private static final EruptionPlugin plugin = EruptionPlugin.getInstance();


	@Command({"event join", "joinevent"})
	@Usage("/event join <id>")
	@Description("Join an event or tournament.")
	public static void eventJoin(@Sender Player player, Player target, String[] args) throws BladeExitMessage {
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Party party = plugin.getPartyManager().getParty(playerData.getUniqueId());

		//Events
		if (args.length < 1) {
			throw new BladeUsageMessage();
		}

		if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
			throw new BladeExitMessage(CC.translate("&cCannot issue this command in your current state."));
		}

		String eventId = args[0].toLowerCase();
		if(!NumberUtils.isNumber(eventId)) {

			PracticeEvent event = plugin.getEventManager().getByName(eventId);

			if(event == null) {
				throw new BladeExitMessage(CC.translate("&cThat event does not exist."));
			}

			if (event.getState() != EventState.WAITING) {
				throw new BladeExitMessage(CC.translate("&cThat event is currently not available."));
			}

			if (event.getPlayers().containsKey(player.getUniqueId())) {
				throw new BladeExitMessage(CC.translate("&cYou are already in this event."));
			}

			if(event.getPlayers().size() >= event.getLimit()) {
				player.sendMessage(ChatColor.RED + "That event is already full.");
			}

			event.join(player);
		}

		//Tournaments
		if (plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
			throw new BladeExitMessage(CC.translate("&cYou are currently in a tournament."));
		}

		int id = Integer.parseInt(eventId);
		Tournament tournament = plugin.getTournamentManager().getTournament(id);

		if (tournament != null) {


			if (party != null && party.getMembers().size() != tournament.getTeamSize()) {
				player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
				player.sendMessage(String.valueOf(party.getMembers().size()));
			}

			if (tournament.getSize() > tournament.getPlayers().size()) {
				if ((tournament.getTournamentState() == TournamentState.WAITING
						|| tournament.getTournamentState() == TournamentState.STARTING)
						&& tournament.getCurrentRound() == 1) {
					plugin.getTournamentManager().joinTournament(id, player);
				} else {
					player.sendMessage(ChatColor.RED + "The tournament has already started.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "The tournament is already full.");
			}
		} else {
			throw new BladeExitMessage(CC.translate("&cThat tournament doesn't exist."));
		}

	}
}
