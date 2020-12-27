package me.chris.eruption.setup.arena.commands;

import me.chris.eruption.util.random.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.util.random.Style;
import me.chris.eruption.util.runnable.ArenaCommandRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand extends Command {

    private static final String NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public ArenaCommand() {
        super("arena");
        this.setDescription("Arenas command.");
        this.setUsage(ChatColor.RED + "Usage: /arena <subcommand> [args]");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player)sender;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            sender.sendMessage(Style.translate( "&cEruption &7» Arena Help"));
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");
            sender.sendMessage(Style.translate("&c» &7/arena create - Create an Arena"));
            sender.sendMessage(Style.translate("&c» &7/arena delete - Delete an Arena"));
            sender.sendMessage(Style.translate("&c» &7/arena a - Setup first arena spawn point"));
            sender.sendMessage(Style.translate("&c» &7/arena b - Setup second arena spawn point"));
            sender.sendMessage(Style.translate("&c» &7/arena min - Setup min arena loc"));
            sender.sendMessage(Style.translate("&c» &7/arena max - Setup max arena loc"));
            sender.sendMessage(Style.translate("&c» &7/arena enable - Enable an Arena"));
            sender.sendMessage(Style.translate("&c» &7/arena disable - Disable an Arena"));
            sender.sendMessage(Style.translate("&c» &7/arena generate - Generate an Arena"));
            sender.sendMessage(Style.translate("&c» &7/arena save save - Generate an Arena"));
            sender.sendMessage(Style.translate("&c» &7/arena manage manage - Generate an Arena"));
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "---------------------------------------------------");

            return true;
        }

        if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(usageMessage);
            return true;
        }

        Arena arena = this.plugin.getArenaManager().getArena(args[1]);

        switch (args[0].toLowerCase()) {
            case "create":
                if (arena == null) {
                    this.plugin.getArenaManager().createArena(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully created arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "That arena already exists!");
                }
                break;
            case "delete":
                if (arena != null) {
                    this.plugin.getArenaManager().deleteArena(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                }
                break;
            case "a":
                if (arena != null) {
                    Location location = player.getLocation();

                    if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX(location.getBlockX() + 0.5D);
                        location.setY(location.getBlockY() + 3.0D);
                        location.setZ(location.getBlockZ() + 0.5D);
                    }
                    arena.setA(LocationUtil.fromBukkitLocation(location));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set position A for arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                }
                break;
            case "b":
                if (arena != null) {
                    Location location = player.getLocation();

                    if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX(location.getBlockX() + 0.5D);
                        location.setY(location.getBlockY() + 3.0D);
                        location.setZ(location.getBlockZ() + 0.5D);
                    }
                    arena.setB(LocationUtil.fromBukkitLocation(location));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set position B for arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                }
                break;
            case "min":
                if (arena != null) {
                    arena.setMin(LocationUtil.fromBukkitLocation(player.getLocation()));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                }
                break;
            case "max":
                if (arena != null) {
                    arena.setMax(LocationUtil.fromBukkitLocation(player.getLocation()));
                    sender.sendMessage(ChatColor.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                }
                break;
            case "disable":
            case "enable":
                if (arena != null) {
                    arena.setEnabled(!arena.isEnabled());
                    sender.sendMessage(arena.isEnabled() ? ChatColor.GREEN + "Successfully enabled arena " + args[1] + "." :
                            ChatColor.RED + "Successfully disabled arena " + args[1] + ".");
                } else {
                    sender.sendMessage(ArenaCommand.NO_ARENA);
                }
                break;
            case "save":
                this.plugin.getArenaManager().reloadArenas();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the arenas.");
                break;
            case "manage":
                this.plugin.getArenaManager().openArenaSystemUI(player);
                break;
            case "generate":
                if (args.length == 3) {
                    int arenas = Integer.parseInt(args[2]);
                    this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
                    this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /arena generate <arena> <arenas>");
                }
                break;
            default:
                sender.sendMessage(this.usageMessage);
                break;
        }

        return true;
    }
}
