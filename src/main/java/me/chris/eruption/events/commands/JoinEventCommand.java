package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.tournament.TournamentState;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO: Recode this command to blade.
public class JoinEventCommand extends Command {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public JoinEventCommand() {
		super("joinevent");
		this.setDescription("Join an commands or tournament.");
		this.setUsage(ChatColor.RED + "Usage: /joinevent <id>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (this.plugin.getPartyManager().getParty(playerData.getUniqueId()) != null || playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
			return true;
		}


		String eventId = args[0].toLowerCase();

		if(!NumberUtils.isNumber(eventId)) {

			PracticeEvent event = this.plugin.getEventManager().getByName(eventId);

			if(event == null) {
				player.sendMessage(ChatColor.RED + "That commands doesn't exist.");
				return true;
			}

			if (event.getState() != EventState.WAITING) {
				player.sendMessage(ChatColor.RED + "That commands is currently not available.");
				return true;
			}

			if (event.getPlayers().containsKey(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You are already in this commands.");
				return true;
			}

			if(event.getPlayers().size() >= event.getLimit()) {
				player.sendMessage(ChatColor.RED + "Sorry! The commands is already full.");
			}

			event.join(player);

			return true;
		}


		if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
			return true;
		}

		int id = Integer.parseInt(eventId);
		Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

		if (tournament != null) {

			Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

			if (party != null && party.getMembers().size() != tournament.getTeamSize()) {
				player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
				player.sendMessage(String.valueOf(party.getMembers().size()));
				player.sendMessage("test");
				return true;
			}

			if (tournament.getSize() > tournament.getPlayers().size()) {
				if ((tournament.getTournamentState() == TournamentState.WAITING
						|| tournament.getTournamentState() == TournamentState.STARTING)
						&& tournament.getCurrentRound() == 1) {
					this.plugin.getTournamentManager().joinTournament(id, player);
				} else {
					player.sendMessage(ChatColor.RED + "Sorry! The tournament already started.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "That tournament doesn't exist.");
		}

		return true;

	}
}
