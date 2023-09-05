package me.chris.eruption.profile.commands.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.settings.SettingsInfo;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.UsageAlias;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleScoreboardCommand {
    @Command({"toggle scoreboard","tsb"})
    @Description("Toggle whether you want to see the scoreboard or not.")
    public static void toggleScoreboard(@Sender Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        SettingsInfo settings = playerData.getSettings();
        settings.setScoreboardToggled(!settings.isScoreboardToggled());
    }
}

