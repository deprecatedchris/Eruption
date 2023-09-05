package me.chris.eruption.setting.commands;

import me.chris.eruption.setting.menu.SettingsMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand {
	@Command({"settings", "options", "toggle"})
	@Description("Edit your settings")
	public static void settingsCommand(@Sender Player player, CommandSender sender) {
		new SettingsMenu().openMenu((Player) sender);
	}
}

