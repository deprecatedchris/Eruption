package me.chris.eruption.command.arena;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.arena.arena.type.ArenaType;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.runnable.ArenaCommandRunnable;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public final class ArenaCommands {

    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"arena create", "arena add"})
    @Usage("/arena create <name>")
    @Permission("eruption.arena.create")
    @Description("Create an arena")
    public static void createArena(@Sender Player player, String name) throws BladeExitMessage {
        if (plugin.getArenaManager().getArena(name) != null) {
            throw new BladeExitMessage("An arena with that name already exists.");
        }

        plugin.getArenaManager().createArena(name);
        player.sendMessage(CC.translate("&aCreated arena &e" + name + "&a!"));
    }

    @Command("arena event")
    @Usage("/arena event <name>")
    @Permission("eruption.arena.event")
    @Description("Toggle arena's event mode")
    public static void toggleEvent(@Sender Player player, Arena arena) {
        player.sendMessage(arena.isEvent() ? CC.RED + "Toggled event mode for arena " + arena.getName() : CC.GREEN + "Toggled event mode for arena" + arena.getName());
        arena.setEvent(!arena.isEvent());
    }

    @Command("arena type")
    @Usage("/arena type <arena> <event>")
    @Permission("eruption.arena.event")
    @Description("Toggle arena's event mode")
    public static void setType(@Sender Player player, Arena arena, String type) {
        switch (type.toLowerCase()) {
            case "sumo" -> {
                arena.setArenaType(ArenaType.SUMO);
                player.sendMessage(CC.GREEN + "Successfully changed the arena event type to " + type.toLowerCase());
            }
            case "lms" -> {
                arena.setArenaType(ArenaType.LMS);
                player.sendMessage(CC.GREEN + "Successfully changed the arena event type to " + type.toLowerCase());
            }
            case "oitc" -> {
                arena.setArenaType(ArenaType.OITC);
                player.sendMessage(CC.GREEN + "Successfully changed the arena event type to " + type.toLowerCase());
            }
            case "runner" -> {
                arena.setArenaType(ArenaType.RUNNER);
                player.sendMessage(CC.GREEN + "Successfully changed the arena event type to " + type.toLowerCase());
            }
            case "corners" -> {
                arena.setArenaType(ArenaType.CORNERS);
                player.sendMessage(CC.GREEN + "Successfully changed the arena event type to " + type.toLowerCase());
            }
            case "parkour" -> {
                arena.setArenaType(ArenaType.PARKOUR);
                player.sendMessage(CC.GREEN + "Successfully changed the arena event type to " + type.toLowerCase());
            }
            default -> player.sendMessage(CC.RED + "Types: sumo, lms, oitc, runner, corners, parkour.");
        }
    }


    @Command({"arena delete", "arena del", "arena remove", "arena rem"})
    @Usage("/arena delete <name>")
    @Permission("eruption.arena.delete")
    @Description("Delete an arena")
    public static void deleteArena(@Sender Player player, Arena arena) {
        plugin.getArenaManager().deleteArena(arena.getName());
        player.sendMessage(CC.translate("&aDeleted arena &e" + arena.getName() + "&a!"));
    }

    @Command({"arena toggle", "arena enable", "arena disable"})
    @Usage("/arena toggle <name>")
    @Permission("eruption.arena.toggle")
    @Description("Toggle an arena.")
    public static void toggleArena(@Sender Player player, Arena arena) throws BladeExitMessage {
        if (arena == null) {
            throw new BladeExitMessage("An arena with that name does not exist.");
        }

        arena.setEnabled(!arena.isEnabled());
        player.sendMessage((arena.isEnabled() ? GREEN : RED) + "Successfully " + (arena.isEnabled() ? "enabled" : "disabled") + " arena " + arena.getName() + ".");
    }

    @Command({"arena reload", "arena save", "arena rl"})
    @Usage("/arena reload")
    @Permission("eruption.arena.reload")
    @Description("Toggle an arena.")
    public static void reloadArena(@Sender Player player) {
        plugin.getArenaManager().reloadArenas();
        player.sendMessage(ChatColor.GREEN + "Successfully reloaded the arenas.");
    }

    @Command({"arena manage"})
    @Usage("/arena manage")
    @Permission("eruption.arena.manage")
    @Description("Open the management menu for an arena.")
    public static void manageArena(@Sender Player player) {
        plugin.getArenaManager().openArenaSystemUI(player);
    }

    @Command({"arena copy", "arena cp", "arena generate"})
    @Usage("/arena copy <name> <copies>")
    @Permission("eruption.arena.copy")
    @Description("Clone an arena.")
    public static void copyArena(@Sender Player player, Arena arena, int copies) throws BladeExitMessage {
        if (arena == null) {
            throw new BladeExitMessage("An arena with that name does not exist.");
        }

        plugin.getServer().getScheduler().runTask(plugin, new ArenaCommandRunnable(plugin, arena, copies));
        plugin.getArenaManager().setGeneratingArenaRunnables(plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
    }

    @Command({"arena position", "arena pos"})
    @Usage("/arena position <a|b|min|max> <name>")
    @Permission("eruption.arena.position")
    @Description("Set position for an arena.")
    public static void arenaPosition(@Sender Player player, String argument, Arena arena)  {
        if (arena == null) {
            throw new BladeExitMessage("An arena with that name does not exist.");
        }

        LocationUtil location = LocationUtil.fromBukkitLocation(transformLocation(player.getLocation()));

        switch (argument.toLowerCase()) {
            case "a" -> {
                arena.setA(location);
                player.sendMessage(GREEN + "Successfully set position A for arena " + arena.getName() + ".");
            }
            case "b" -> {
                arena.setB(location);
                player.sendMessage(GREEN + "Successfully set position B for arena " + arena.getName() + ".");
            }
            case "min" -> {
                arena.setMin(location);
                player.sendMessage(GREEN + "Successfully set minimum position for arena " + arena.getName() + ".");
            }
            case "max" -> {
                arena.setMax(location);
                player.sendMessage(GREEN + "Successfully set maximum position for arena " + arena.getName() + ".");
            }
            default -> throw new BladeUsageMessage();
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