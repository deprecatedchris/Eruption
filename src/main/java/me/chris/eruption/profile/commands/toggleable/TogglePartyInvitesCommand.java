package me.chris.eruption.profile.commands.toggleable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.settings.SettingsInfo;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.UsageAlias;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TogglePartyInvitesCommand {
    @Command({"toggle party", "tpi"})
    @Description("Toggle whether you want to receive party invites.")
    public static void togglePartyInvites(@Sender Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        SettingsInfo settings = playerData.getSettings();
        settings.setDuelRequests(!settings.isPartyInvites());
    }
}
