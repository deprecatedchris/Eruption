package me.chris.eruption.command.admin;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.entity.Player;

public class SaveCommand {
    @Command({"save"})
    @Description("Saves all data within practice")
    @Permission("practice.admin")
    public static void saveCommand(@Sender Player player) {
        for (PlayerData playerData : EruptionPlugin.getInstance().getPlayerManager().getAllData())
            EruptionPlugin.getInstance().getPlayerManager().saveData(playerData);
        EruptionPlugin.getInstance().getArenaManager().saveArenas();
        EruptionPlugin.getInstance().getKitManager().saveKits();
        EruptionPlugin.getInstance().saveConfig();
    }
}
