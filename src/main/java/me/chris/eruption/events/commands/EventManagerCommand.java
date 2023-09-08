package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO: Recode this into menu (or commands)
public class EventManagerCommand extends Command {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public EventManagerCommand() {
        super("eventmanager");
        this.setDescription("Manage an commands.");
        this.setUsage(ChatColor.RED + "Usage: /eventmanager <start/end/status/cooldown> <commands>");
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

        if (args.length < 2) {
            player.sendMessage(usageMessage);
            return true;
        }

        String action = args[0];
        String eventName = args[1];

        if (plugin.getEventManager().getByName(eventName) == null) {
            player.sendMessage(ChatColor.RED + "That commands doesn't exist.");
            return true;
        }

        PracticeEvent event = plugin.getEventManager().getByName(eventName);

        if (action.toUpperCase().equalsIgnoreCase("START") && event.getState() == EventState.WAITING) {
            event.getCountdownTask().setTimeUntilStart(5);
            player.sendMessage(ChatColor.RED + "Event was force started.");

        } else if (action.toUpperCase().equalsIgnoreCase("END") && event.getState() == EventState.STARTED) {
            event.end();
            player.sendMessage(ChatColor.RED + "Event was cancelled.");

        } else if (action.toUpperCase().equalsIgnoreCase("STATUS")) {

            String[] message = new String[]{
                    ChatColor.RED + "Event: " + ChatColor.WHITE + event.getName(),
                    ChatColor.RED + "Host: " + ChatColor.WHITE + (event.getHost() == null ? "Player Left" : event.getHost().getName()),
                    ChatColor.RED + "Players: " + ChatColor.WHITE + event.getPlayers().size() + "/" + event.getLimit(),
                    ChatColor.RED + "State: " + ChatColor.WHITE + event.getState().name(),
            };

            player.sendMessage(message);

        } else if (action.toUpperCase().equalsIgnoreCase("COOLDOWN")) {

            this.plugin.getEventManager().setCooldown(0L);
            player.sendMessage(ChatColor.RED + "Event cooldown was cancelled.");
        } else {
            player.sendMessage(this.usageMessage);
        }


        return true;
    }
}
