package me.chris.eruption.profile.commands.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.settings.SettingsInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleDuelRequestsCommand extends Command {

    public ToggleDuelRequestsCommand() {
        super("toggleduelrequests");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(((Player) sender).getUniqueId());
        SettingsInfo settings = playerData.getSettings();
        settings.setDuelRequests(!settings.isDuelRequests());
        return true;
    }
}
