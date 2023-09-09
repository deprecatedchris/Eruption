package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.util.other.Clickable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO: Make this into a menu.
public class HostCommand extends Command {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public HostCommand() {
        super("host");
        this.setDescription("33Host an commands.");
        this.setUsage(ChatColor.RED + "Usage: /host <commands>");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return true;
        }

        String eventName = args[0];

        if (eventName == null) {
            return true;
        }

        if (plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage(ChatColor.RED + "That commands doesn't exist.");
            player.sendMessage(ChatColor.RED + "Available events: Sumo, OITC, Runner, Parkour, LMS, 4Corners");
            return true;
        }

        if (System.currentTimeMillis() < plugin.getEventManager().getCooldown()) {
            player.sendMessage(ChatColor.RED + "There is a cooldown. Event can't start at this moment.");
            return true;
        }

        PracticeEvent event = plugin.getEventManager().getByName(eventName);
        if (event.getState() != EventState.UNANNOUNCED) {
            player.sendMessage(ChatColor.RED + "There is currently an active commands.");
            return true;
        }

        boolean eventBeingHosted = plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
        if (eventBeingHosted) {
            player.sendMessage(ChatColor.RED + "There is currently an active commands.");
            return true;
        }


        String toSend = ChatColor.YELLOW.toString() + ChatColor.BOLD + "(Event) " + ChatColor.GREEN + "" + event.getName() + " is starting soon. " + ChatColor.GRAY + "[Click to Join]";
        String toSendDonor = ChatColor.GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "*" + ChatColor.GRAY + "] " + ChatColor.BOLD + player.getName() + ChatColor.WHITE + " is hosting a " + ChatColor.BOLD + event.getName() + " Event. " + ChatColor.GRAY + "[Click to Join]";


        Clickable message = new Clickable(player.hasPermission("practice." + this.getName().toLowerCase() + ".command") ? toSendDonor : toSend,
                ChatColor.GRAY + "Click to join this commands.",
                "/joinevent " + event.getName());
        plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);

        if (player.hasPermission("practice." + this.getName().toLowerCase() + ".command.admin")) {
            event.setLimit(event instanceof SumoEvent ? 100 : 50);
        } else if (player.hasPermission("practice." + this.getName().toLowerCase() + ".command.veryhigh")) {
            event.setLimit(event instanceof SumoEvent ? 50 : 30);
        } else if (player.hasPermission("practice." + this.getName().toLowerCase() + ".command.high")) {
            event.setLimit(event instanceof SumoEvent ? 40 : 25);
        } else if (player.hasPermission("practice." + this.getName().toLowerCase() + ".command.medium")) {
            event.setLimit(event instanceof SumoEvent ? 35 : 25);
        } else if (player.hasPermission("practice." + this.getName().toLowerCase() + ".command.low")) {
            event.setLimit(event instanceof SumoEvent ? 30 : 20);
        } else {
            event.setLimit(30);
        }

        EruptionPlugin.getInstance().getEventManager().hostEvent(event, player);

        return true;
    }
}
