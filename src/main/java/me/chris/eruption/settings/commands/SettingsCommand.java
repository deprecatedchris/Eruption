package me.chris.eruption.settings.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.settings.menu.SettingsMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SettingsCommand extends Command {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public SettingsCommand() {
		super("settings");
		this.setDescription("Toggles multiple settings.");
		this.setUsage(ChatColor.RED + "Usage: /settings");
		this.setAliases(Arrays.asList("options", "toggle"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		new SettingsMenu().openMenu((Player) sender);
		return true;
	}
}
