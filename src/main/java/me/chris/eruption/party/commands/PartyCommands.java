package me.chris.eruption.party.commands;


import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.party.managers.PartyManager;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.vaperion.blade.annotation.argument.Name;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.UsageAlias;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyCommands {
    @Command({"party create"})
    @UsageAlias("p create")
    @Description("Create a party")
    public static void partyCreate(@Sender Player player) {
        Party party = EruptionPlugin.getInstance().getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

        //todo: hopefully this works LOL
        if (party != null) {
            player.sendMessage(ChatColor.RED + "You are already in a party.");
            return;
        }

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return;
        }

        EruptionPlugin.getInstance().getPartyManager().createParty(player);

    }

    @Command({"party leave"})
    @UsageAlias("p leave")
    @Description("Leave your current party")
    public static void partyLeave(@Sender Player player) {
        Party party = EruptionPlugin.getInstance().getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        PartyManager partyManager = EruptionPlugin.getInstance().getPartyManager();
        if (party == null) {
            player.sendMessage(ChatColor.RED + "You are not in a party.");
            return;
        }

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return;
        }

        partyManager.leaveParty(player);
    }
}