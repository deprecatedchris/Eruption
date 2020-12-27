package me.chris.eruption.tournament.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.tournament.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TournamentCommand extends Command {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	private final static String[] HELP_ADMIN_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.RED + "Tournament Commands:",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.GRAY + "» /tournament start " + ChatColor.GRAY + "- Start a Tournament",
			ChatColor.GRAY + "» /tournament stop " + ChatColor.GRAY + "- Stop a Tournament",
			ChatColor.GRAY + "» /tournament alert " + ChatColor.GRAY + "- Alert a Tournament",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};
	private final static String[] HELP_REGULAR_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.RED + "Tournament Commands:",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.GRAY + "» /join <id> " + ChatColor.GRAY + "- Join a Tournament",
			ChatColor.GRAY + "» /leave " + ChatColor.GRAY + "- Leave a Tournament",
			ChatColor.GRAY + "» /status " + ChatColor.GRAY + "- Status of a Tournament",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};

	public TournamentCommand() {
		super("tournament");
		this.setUsage(ChatColor.RED + "Usage: /tournament [args]");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {

		if (!(commandSender instanceof Player)) {
			return true;
		}

		Player player = (Player) commandSender;

		if (args.length == 0) {
			commandSender.sendMessage(player.hasPermission("practice.admin") ? TournamentCommand.HELP_ADMIN_MESSAGE : HELP_REGULAR_MESSAGE);
			return true;
		}
		if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
			return true;
		}


		switch (args[0].toLowerCase()) {
			case "start":

				if (args.length == 5) {
					try {
						int id = Integer.parseInt(args[1]);
						int teamSize = Integer.parseInt(args[3]);
						int size = Integer.parseInt(args[4]);
						String kitName = args[2];

						if (size % teamSize != 0) {
							commandSender.sendMessage(ChatColor.RED + "Tournament size & team sizes are invalid. Please try again.");
							return true;
						}

						if (this.plugin.getTournamentManager().getTournament(id) != null) {
							commandSender.sendMessage(ChatColor.RED + "This tournament already exists.");
							return true;
						}

						Kit kit = this.plugin.getKitManager().getKit(kitName);

						if (kit == null) {
							commandSender.sendMessage(ChatColor.RED + "That kit does not exist.");
							return true;
						}

						this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);

					} catch (NumberFormatException e) {
						commandSender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size>");
					}
				} else {
					commandSender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size> ");
				}
				break;
			case "stop":

				if (args.length == 2) {
					int id = Integer.parseInt(args[1]);
					Tournament tournament = this.plugin.getTournamentManager().getTournament(id);

					if (tournament != null) {
						this.plugin.getTournamentManager().removeTournament(id);
						commandSender.sendMessage(ChatColor.RED + "Successfully removed tournament " + id + ".");
					} else {
						commandSender.sendMessage(ChatColor.RED + "This tournament does not exist.");
					}
				} else {
					commandSender.sendMessage(ChatColor.RED + "Usage: /tournament stop <id>");
				}
				break;
			case "alert":

				for (Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(ChatColor.GREEN + "A Tournament has been created!" +
												"\nTeam Size: " + plugin.getTournamentManager().getTournaments().get(0).getTeamSize() + "\n" +
												"Ladder: " + plugin.getTournamentManager().getTournaments().get(0).getKitName() + "\n" +
												"Required Players: " + plugin.getTournamentManager().getTournaments().get(0).getSize());
				}
		}
		return false;
	}
}
