package me.chris.eruption.command.party;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.party.managers.PartyManager;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.UsageAlias;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyCommands {

    @Command({"party create"})
    @UsageAlias("p create")
    @Description("Create a party")
    public static void partyCreate(@Sender Player player) throws BladeExitMessage {
        Party party = EruptionPlugin.getInstance().getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

        if (party != null) {
            throw new BladeExitMessage(ChatColor.RED + "You are already in a party.");
        }

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage(ChatColor.RED + "Cannot execute this command in your current state.");
        }

        EruptionPlugin.getInstance().getPartyManager().createParty(player);
    }

    @Command({"party leave"})
    @UsageAlias("p leave")
    @Description("Leave your current party")
    public static void partyLeave(@Sender Player player) throws BladeExitMessage {
        Party party = EruptionPlugin.getInstance().getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        PartyManager partyManager = EruptionPlugin.getInstance().getPartyManager();

        if (party == null) {
            throw new BladeExitMessage(ChatColor.RED + "You are not in a party.");
        }

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage(ChatColor.RED + "Cannot execute this command in your current state.");
        }

        partyManager.leaveParty(player);
    }
}