package me.chris.eruption.settings.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.settings.menu.SettingsMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.UsageAlias;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SettingsCommand {
	@Command({"settings", "options", "toggle"})
	@Description("Edit your settings")
	public static void settingsCommand(@Sender Player player, CommandSender sender) {
		new SettingsMenu().openMenu((Player) sender);
	}
}

