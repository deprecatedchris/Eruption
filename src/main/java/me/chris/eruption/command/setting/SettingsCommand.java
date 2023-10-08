package me.chris.eruption.command.setting;

import me.chris.eruption.command.setting.menu.SettingsMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.bukkit.entity.Player;

public class SettingsCommand {

	@Command({"settings", "options"})
	@Description("Edit your settings")
	public static void settingsCommand(@Sender Player player) {
		new SettingsMenu().openMenu(player);
	}
}

