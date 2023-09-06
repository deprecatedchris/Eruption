package me.chris.eruption.command.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.setting.SettingsInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleStyleCommand extends Command {
//TODO: implement Scoreboard States for different options on scoreboard.
    public ToggleStyleCommand() {
        super("togglestyle");
    }

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

        SettingsInfo settings = playerData.getSettings();

        if (!settings.isAltScoreboard()) {
            settings.setAltScoreboard(true);
        } else {
            settings.setAltScoreboard(false);
        }
        return true;
    }

}