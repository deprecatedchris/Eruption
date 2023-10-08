package me.chris.eruption.command.admin;

import me.chris.eruption.EruptionPlugin;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.*;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;

public class WarpCommand {

    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"spawn"})
    @Description("Teleport to spawn.")
    @Permission("practice.admin")
    public static void spawnCommand(@Sender Player player, String[] args) throws BladeExitMessage {
        if (args.length == 0) {
            plugin.getPlayerManager().sendToSpawnAndReset(player);
        }
    }
}
