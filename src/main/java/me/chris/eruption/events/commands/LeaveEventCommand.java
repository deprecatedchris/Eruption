package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.tournament.Tournament;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveEventCommand extends Command {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public LeaveEventCommand() {
		super("leave");
		this.setDescription("Leave an commands or tournament.");
		this.setUsage(ChatColor.RED + "Usage: /leave");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		boolean inTournament = this.plugin.getTournamentManager().isInTournament(player.getUniqueId());
		boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;

		if (inEvent) {
			this.leaveEvent(player);
		} else if (inTournament) {
			this.leaveTournament(player);
		} else {
			player.sendMessage(ChatColor.RED + "There is nothing to leave.");
		}

		return true;
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
