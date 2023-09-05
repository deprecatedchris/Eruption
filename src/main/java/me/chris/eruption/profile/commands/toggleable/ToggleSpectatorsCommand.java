package me.chris.eruption.profile.commands.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.setting.SettingsInfo;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.bukkit.entity.Player;

public class ToggleSpectatorsCommand {
    @Command({"toggle spectators", "tspec"})
    @Description("Toggle whether you want to allow spectators or not.")
    public static void toggleSpectators(@Sender Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        SettingsInfo settings = playerData.getSettings();
        settings.setSpectatorsAllowed(!settings.isSpectatorsAllowed());
    }

}