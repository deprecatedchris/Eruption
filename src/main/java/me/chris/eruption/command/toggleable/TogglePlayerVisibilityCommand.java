package me.chris.eruption.command.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.setting.SettingsInfo;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TogglePlayerVisibilityCommand {

    @Command({"TogglePlayerVisibility", "tpv"})
    @Permission("practice.donator")
    @Description("Toggle whether you want to see players or not.")
    public static void togglePlayerVisibility(@Sender Player player) throws BladeExitMessage {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage(ChatColor.RED + "You are not allowed to change this setting in your current state.");
        }

        SettingsInfo settings = playerData.getSettings();
        settings.setPlayerVisibility(!settings.isPlayerVisibility());
        if (!settings.isPlayerVisibility()) {
            EruptionPlugin.getInstance().getServer().getOnlinePlayers().forEach(player::hidePlayer);
        } else {
            EruptionPlugin.getInstance().getServer().getOnlinePlayers().forEach(player::showPlayer);
        }
    }
}



