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

    @Command("setlocation")
    @Permission("eruption.setlocation")
    @Description("Setup spawns.")
    public void execute(@Sender Player sender, String type) throws BladeExitMessage {
        FileConfiguration config = EruptionPlugin.getInstance().getConfig();
        if (!(sender instanceof Player)) {
            return;
        }

                if (type.equalsIgnoreCase("spawn")) {
                    config.set("LOCATIONS.SPAWN.LOCATION", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(sender.getLocation())));
                    sender.sendMessage(CC.translate("&aSpawn location set."));
                    EruptionPlugin.getInstance().saveConfig();
                } else if (type.equalsIgnoreCase("min")) {
                    config.set("LOCATIONS.SPAWN.MIN", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(sender.getLocation())));
                    sender.sendMessage(CC.translate("&aSpawn's min set."));
                    EruptionPlugin.getInstance().saveConfig();
                } else if (type.equalsIgnoreCase("max")) {
                    config.set("LOCATIONS.SPAWN.MAX", LocationUtil.locationToString(LocationUtil.fromBukkitLocation(sender.getLocation())));
                    sender.sendMessage(CC.translate("&aSpawn's max set."));
                    EruptionPlugin.getInstance().saveConfig();
                } else {
                    sender.sendMessage(CC.translate("&cWrong usage: /setlocation spawn [spawn|min|max]"));
                }
        }
    }

