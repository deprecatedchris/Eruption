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
    public void spawn(@Sender Player player, String type) throws BladeExitMessage {
        FileConfiguration config = EruptionPlugin.getInstance().getConfig();

        // How is this going to pass anything other than player to the player param?
        // Not even needed lol?

//        if (!(sender instanceof Player)) {
//            return;
//        }

        switch (type) {
            case "spawn":
                config.set("LOCATIONS.SPAWN.LOCATION", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
                player.sendMessage(CC.translate("&aSpawn location set."));
                EruptionPlugin.getInstance().saveConfig();
                break;
            case "min":
                config.set("LOCATIONS.SPAWN.MIN", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
                player.sendMessage(CC.translate("&aSpawn's min set."));
                EruptionPlugin.getInstance().saveConfig();
                break;
            case "max":
                config.set("LOCATIONS.SPAWN.MAX", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
                player.sendMessage(CC.translate("&aSpawn's max set."));
                EruptionPlugin.getInstance().saveConfig();
                break;
            default:
                player.sendMessage(CC.translate("&cWrong usage: /setspawn [spawn|min|max]"));
                break;
        }
    }
}
