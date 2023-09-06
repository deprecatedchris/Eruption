package me.chris.eruption.command.arena;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.random.LocationUtil;
import me.chris.eruption.util.runnable.ArenaCommandRunnable;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.chris.eruption.util.random.LocationUtil.fromBukkitLocation;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class ArenaCommands {

    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"arena create", "arena add"})
    @Permission("eruption.arena.create")
    @Description("Create an arena")
    public static void createArena(@Sender Player player, String name) throws BladeExitMessage {
        if (plugin.getArenaManager().getArena(name) != null) {
            throw new BladeExitMessage("An arena with that name already exists.");
        }

        plugin.getArenaManager().createArena(name);
        player.sendMessage(CC.translate("&aCreated arena &e" + name + "&a!"));
    }

    @Command({"arena delete", "arena del", "arena remove", "arena rem"})
    @Permission("eruption.arena.delete")
    @Description("Delete an arena")
    public static void deleteArena(@Sender Player player, String name) throws BladeExitMessage {
        if (plugin.getArenaManager().getArena(name) == null) {
            throw new BladeExitMessage("An arena with that name does not exist.");
        }

        plugin.getArenaManager().deleteArena(name);
        player.sendMessage(CC.translate("&aDeleted arena &e" + name + "&a!"));
    }

    @Command({"arena toggle", "arena enable", "arena disable"})
    @Permission("eruption.arena.toggle")
    @Description("Toggle an arena.")
    public static void toggleArena(@Sender Player player, String name) throws BladeExitMessage {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) throw new BladeExitMessage("An arena with that name does not exist.");

        arena.setEnabled(!arena.isEnabled());
        player.sendMessage((arena.isEnabled() ? GREEN : RED) + "Successfully " + (arena.isEnabled() ? "enabled" : "disabled") + " arena " + name + ".");
    }

    @Command({"arena reload", "arena save", "arena rl"})
    @Permission("eruption.arena.reload")
    @Description("Toggle an arena.")
    public static void reloadArena(@Sender Player player) throws BladeExitMessage {
        plugin.getArenaManager().reloadArenas();
        player.sendMessage(ChatColor.GREEN + "Successfully reloaded the arenas.");
    }

    @Command({"arena manage"})
    @Permission("eruption.arena.manage")
    @Description("Open the management menu for an arena.")
    public static void manageArena(@Sender Player player) throws BladeExitMessage {
        plugin.getArenaManager().openArenaSystemUI(player);
    }

    @Command({"arena copy", "arena cp", "arena generate"})
    @Permission("eruption.arena.copy")
    @Description("Clone an arena.")
    public static void copyArena(@Sender Player player, String name, int copies) throws BladeExitMessage {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) throw new BladeExitMessage("An arena with that name does not exist.");

        plugin.getServer().getScheduler().runTask(plugin, new ArenaCommandRunnable(plugin, arena, copies));
        plugin.getArenaManager().setGeneratingArenaRunnables(plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
    }

    @Command({"arena position", "arena pos"})
    @Usage("/arena position <a|b|min|max> <name>")
    @Permission("eruption.arena.position")
    @Description("Set position for an arena.")
    public static void arenaPosition(@Sender Player player, String argument, String name) throws BladeExitMessage {
        Arena arena = plugin.getArenaManager().getArena(name);
        if (arena == null) throw new BladeExitMessage("An arena with that name does not exist.");
        LocationUtil location = LocationUtil.fromBukkitLocation(transformLocation(player.getLocation()));

        switch (argument.toLowerCase()) {
            case "a":
                arena.setA(location);
                player.sendMessage(GREEN + "Successfully set position A for arena " + name + ".");
                return;
            case "b":
                arena.setB(location);
                player.sendMessage(GREEN + "Successfully set position B for arena " + name + ".");
                return;
            case "min":
                arena.setMin(location);
                player.sendMessage(GREEN + "Successfully set minimum position for arena " + name + ".");
                return;
            case "max":
                arena.setMax(location);
                player.sendMessage(GREEN + "Successfully set maximum position for arena " + name + ".");
                return;
            default:
                throw new BladeUsageMessage();
        }
    }

    // It was needed lol
    private static Location transformLocation(Location location) {
        location.setX(location.getBlockX() + 0.5D);
        location.setY(location.getBlockY() + 3.0D);
        location.setZ(location.getBlockZ() + 0.5D);
        return location;
    }
}