package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

//TODO: Recode this command to blade.
public class EventSpectateCommand extends Command {
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public EventSpectateCommand() {
        super("eventspectate");
        this.setDescription("Spectate an commands.");
        this.setUsage(ChatColor.RED + "Usage: /eventspectate <commands>");
        this.setAliases(Arrays.asList("eventspec", "specevent"));
    }


    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(usageMessage);
            return true;
        }

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());

        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }

        PracticeEvent event = this.plugin.getEventManager().getByName(args[0]);

        if (event == null) {
            player.sendMessage(ChatColor.RED + "That profile is currently not in an commands.");
            return true;
        }

        if (event.getState() == EventState.UNANNOUNCED) {
            player.sendMessage(ChatColor.RED + "That commands is not available right now.");
            return true;
        }

        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            if (this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You are already spectating this commands.");
                return true;
            }
            this.plugin.getEventManager().removeSpectator(player, event);
        }

        player.sendMessage(ChatColor.GREEN + "You are now spectating " + ChatColor.GRAY + event.getName() + " Event" + ChatColor.GREEN + ".");

        this.plugin.getEventManager().addSpectator(player, playerData, event);
        return true;
    }
}
