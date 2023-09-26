package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class EventSpectateCommand {
    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();


    @Command({"event spectate", "eventspec", "specevent"})
    @Usage("/event spectate <player>")
    @Description("Spectate an event.")
    public static void eventSpectate(@Sender Player player, Player target, String[] args) throws BladeExitMessage {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        Party party = plugin.getPartyManager().getParty(playerData.getUniqueId());
        PracticeEvent event = plugin.getEventManager().getByName(args[0]);

        if (args.length < 1) {
            throw new BladeUsageMessage();
        }

        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            throw new BladeExitMessage(CC.translate("&cCannot issue this command in your current state."));
        }

        if (event == null) {
            throw new BladeExitMessage(CC.translate("&cThat player is not currently in an event."));
        }

        if (event.getState() == EventState.UNANNOUNCED) {
            throw new BladeExitMessage(CC.translate("&cThat event is not currently available."));
        }

        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            if (plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
                throw new BladeExitMessage(CC.translate("&cYou are already spectating this event."));
            }
            plugin.getEventManager().removeSpectator(player, event);
        }

        player.sendMessage(CC.translate("&eYou are now spectating &c" + event.getName() + " &eEvent."));

        plugin.getEventManager().addSpectator(player, playerData, event);
    }
}
