package me.chris.eruption.command.arena;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;

public class ArenaCommands {

    @Command(value = "arena create")
    @Permission("eruption.arena.create")
    @Description("Create an arena")
    public static void createArena(@Sender Player player, String name) throws BladeExitMessage {
        if (EruptionPlugin.getInstance().getArenaManager().getArena(name) != null) {
            throw new BladeExitMessage("An arena with that name already exists.");
        }

        EruptionPlugin.getInstance().getArenaManager().createArena(name);
        player.sendMessage(CC.translate("&aCreated arena &e" + name + "&a!"));
    }

    @Command(value = "arena delete")
    @Permission("eruption.arena.delete")
    @Description("Delete an arena")
    public static void deleteArena(@Sender Player player, String name) throws BladeExitMessage {
        if (EruptionPlugin.getInstance().getArenaManager().getArena(name) == null) {
            throw new BladeExitMessage("An arena with that name does not exist.");
        }

        EruptionPlugin.getInstance().getArenaManager().deleteArena(name);
        player.sendMessage(CC.translate("&aDeleted arena &e" + name + "&a!"));
    }
}
