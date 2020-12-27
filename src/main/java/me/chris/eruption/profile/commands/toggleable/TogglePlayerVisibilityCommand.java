package me.chris.eruption.profile.commands.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.settings.SettingsInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TogglePlayerVisibilityCommand extends Command {

    public TogglePlayerVisibilityCommand() {
        super("toggleplayervisibility");
    }

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "You are not allowed to change this settings in your current state.");
            return false;
        }
        SettingsInfo settings = playerData.getSettings();
        settings.setPlayerVisibility(!settings.isPlayerVisibility());
        if (!settings.isPlayerVisibility()) {
            this.plugin.getServer().getOnlinePlayers().forEach(player::hidePlayer);
        } else {
            this.plugin.getServer().getOnlinePlayers().forEach(player::showPlayer);
        }
        return true;
    }

}
