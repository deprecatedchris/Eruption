package me.chris.eruption.command.admin;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.LocationUtil;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnCommand {

    @Command("setspawn")
    @Permission("eruption.setspawn")
    @Description("Setup spawns.")
    public void spawn(@Sender Player player)  {
        FileConfiguration config = EruptionPlugin.getInstance().getConfig();

        config.set("LOCATION", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
        player.sendMessage(CC.translate("&aSpawn location set."));
        EruptionPlugin.getInstance().saveConfig();

    }
}
