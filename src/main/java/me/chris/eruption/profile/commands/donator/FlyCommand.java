package me.chris.eruption.profile.commands.donator;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class FlyCommand extends Command {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public FlyCommand() {
		super("fly");
		this.setDescription("Toggles flight.");
		this.setUsage(ChatColor.RED + "Usage: /fly");
		this.setAliases(Arrays.asList("flight"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
			return true;
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
			return true;
		}

		player.setAllowFlight(!player.getAllowFlight());

		if (player.getAllowFlight()) {
			player.sendMessage(ChatColor.WHITE + "Your flight has been enabled.");
		} else {
			player.sendMessage(ChatColor.WHITE + "Your flight has been disabled.");
		}

		return true;
	}
}
