package me.chris.eruption.setup;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.random.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetStatsCommand extends Command {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public ResetStatsCommand() {
		super("reset");
		this.setUsage(ChatColor.RED + "Usage: /reset [profile]");
	}

	@Override
	public boolean execute(CommandSender commandSender, String s, String[] args) {

		if (commandSender instanceof Player) {

			Player player = (Player) commandSender;
			if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
				player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
				return true;
			}
		}

		if (args.length == 0) {
			commandSender.sendMessage(ChatColor.RED + "Usage: /reset <profile>");
			return true;
		}
		Player target = this.plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			commandSender.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
			return true;
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
		for (Kit kit : this.plugin.getKitManager().getKits()) {
			playerData.setElo(kit.getName(), PlayerData.DEFAULT_ELO);
			playerData.setLosses(kit.getName(), 0);
			playerData.setWins(kit.getName(), 0);
		}

		commandSender.sendMessage(ChatColor.GREEN + target.getName() + "'s stats have been wiped.");
		return true;
	}

}
