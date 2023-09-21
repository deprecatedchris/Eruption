package me.chris.eruption.command.party;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.party.managers.PartyManager;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.Clickable;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.annotation.command.UsageAlias;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PartyCommands {

    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"party create", "p create"})
    @UsageAlias("p create")
    @Description("Create a party")
    public static void partyCreate(@Sender Player player) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (party != null) {
            throw new BladeExitMessage(ChatColor.RED + "You are already in a party.");
        }

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage(ChatColor.RED + "Cannot execute this command in your current state.");
        }

        plugin.getPartyManager().createParty(player);
    }

    @Command({"party leave", "p leave"})
    @Description("Leave your current party")
    public static void partyLeave(@Sender Player player) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        PartyManager partyManager = plugin.getPartyManager();

        if (party == null) {
            throw new BladeExitMessage(ChatColor.RED + "You are not in a party.");
        }

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage(ChatColor.RED + "Cannot execute this command in your current state.");
        }

        partyManager.leaveParty(player);
    }

    @Command({"party invite", "party inv", "p invite", "p inv"})
    @Description("Invite a player to your party.")
    public static void partyInvite(@Sender Player player, CommandSender sender, Player target) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        PartyManager partyManager = plugin.getPartyManager();
        if (party == null) {
            throw new BladeExitMessage("You are not in a party.");
        }

        if (party.getLeader() != player.getUniqueId()) {
            throw new BladeExitMessage(ChatColor.RED + "You are not the leader of this party.");
        }

        if (target == null) {
            throw new BladeExitMessage("This player cannot be found.");
        }

        PlayerData targetData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (target.getUniqueId() == player.getUniqueId()) {
            throw new BladeExitMessage("You cannot invite yourself to a party.");
        } else if (plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
            throw new BladeExitMessage("That player is currently in another party.");
        } else if (targetData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage("That player is currently busy.");
        } else if (!targetData.getSettings().isPartyInvites()) {
            throw new BladeExitMessage("That player has the party invites disabled.");
        } else if (plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
            throw new BladeExitMessage("You have already sent a party invitation to that player, please wait.");
        } else if (!party.isAllInvite() && !player.getUniqueId().equals(party.getLeader())) {
            throw new BladeExitMessage("All invite isn't enabled, ask " + ChatColor.RED + ChatColor.BOLD.toString() + Bukkit.getPlayer(party.getLeader()).getName() + ChatColor.RED + " to invite " + target.getName() + ".");
        } else {
            plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());

            Clickable partyInvite = new Clickable(ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + " has invited you to their party! " + ChatColor.GRAY + "[Click to Accept]",
                    ChatColor.GRAY + "Click to accept",
                    "/party accept " + sender.getName());

            partyInvite.sendToPlayer(target);

            party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + target.getName() + ChatColor.YELLOW + " has been invited to the party.");
        }
    }

    @Command({"party accept", "party join", "p accept", "p join"})
    @Description("Accept a party invite.")
    public static void partyAccept(@Sender Player player, Player target) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());
        Party targetParty = plugin.getPartyManager().getParty(target.getUniqueId());
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (party != null) {
            throw new BladeExitMessage("You are already in a party.");
        } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage("Cannot execute this command in your current state.");
        } else if (target == null) {
            throw new BladeExitMessage("This player cannot be found.");
        }

        PartyManager partyManager = plugin.getPartyManager();

        if (targetParty == null) {
            throw new BladeExitMessage("That player is not in a party.");
        } else if (targetParty.getMembers().size() >= targetParty.getLimit()) {
            throw new BladeExitMessage("Party size has reached it's limit");
        } else if (!plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
            throw new BladeExitMessage("You do not have any pending requests.");
        } else {
            plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
        }
    }

    @Command({"party join", "p join"})
    @Description("Join a public party.")
    public static void partyJoin(@Sender Player player, CommandSender sender, Player target) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());
        Party targetParty = plugin.getPartyManager().getParty(target.getUniqueId());
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (party != null) {
            throw new BladeExitMessage("You are already in a party.");
        } else if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage("Cannot execute this command in your current state.");
        } else {
            if (target == null) {
                throw new BladeExitMessage("This player cannot be found.");
            }

            if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
                player.sendMessage(ChatColor.RED + "You can't join this party.");
            } else {
                plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
            }
        }
    }

    @Command({"party kick", "p kick"})
    @Description("Kick a player from party.")
    public static void partyKick(@Sender Player player, CommandSender sender, Player target) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());
        Party targetParty = plugin.getPartyManager().getParty(target.getUniqueId());

        if (party == null) {
            throw new BladeExitMessage("You are not in a party.");
        }

        if (party.getLeader() != player.getUniqueId()) {
            throw new BladeExitMessage(ChatColor.RED + "You are not the leader of this party.");
        }

        if (target == null) {
            throw new BladeExitMessage("This player cannot be found.");
        }

        if (targetParty == null || targetParty.getLeader() != party.getLeader()) {
            player.sendMessage(ChatColor.RED + "That player is not in your party.");
        } else {
            plugin.getPartyManager().leaveParty(target);
        }
    }

    @Command({"party setLimit", "p setLimit"})
    @Usage("/party limit <value>")
    @Description("Limit your party.")
    public static void partyLimit(@Sender Player player, Integer limit, Player target) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            throw new BladeExitMessage("You are not in a party.");
        } else {
            if (party.getLeader() != player.getUniqueId()) {
                throw new BladeExitMessage("You are not the leader of this party.");
            }
            try {
                if (limit < 2 || limit > 50) {
                    throw new BladeExitMessage("That is not a valid limit.");
                } else {
                    party.setLimit(limit);
                    throw new BladeExitMessage(CC.translate("&eYou have set the party profile limit to " + ChatColor.RED + limit + CC.YELLOW + " players."));
                }
            } catch (NumberFormatException e) {
                throw new BladeExitMessage("That is not a number.");
            }
        }
    }

    @Command({"party toggle", "p toggle"})
    @Usage("/party toggle <open|close>")
    @Description("Limit your party.")
    public static void partyToggle(@Sender Player player, CommandSender sender, Integer limit, Player target) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            throw new BladeExitMessage("You are not in a party.");
        } else {
            if (party.getLeader() != player.getUniqueId()) {
                throw new BladeExitMessage("You are not the leader of this party.");
            }
            party.setOpen(!party.isOpen());
            party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + "Your party is now " + ChatColor.BOLD + (party.isOpen() ? "OPEN" : "LOCKED"));
        }
    }

    @Command({"party info", "p info"})
    @Description("See your party information.")
    public static void partyInformation(@Sender Player player) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());

        if (party == null) {
            throw new BladeExitMessage("You are not in a party.");
        }

        List<UUID> members = new ArrayList<>(party.getMembers());
        members.remove(party.getLeader());

        StringBuilder builder = new StringBuilder(CC.translate("&eParty Members: (" + ((party.getMembers().size() - 1) + CC.translate(")&e: "))));
        members.stream().map(plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(ChatColor.WHITE).append(member.getName()).append(","));

        String[] information = new String[]{
                " ",
                CC.translate("&c&lParty Information"),
                " ",
                ChatColor.YELLOW + "Leader&7: " + ChatColor.RED + plugin.getServer().getPlayer(party.getLeader()).getName(),
                ChatColor.YELLOW + "Party State&7: " + ChatColor.RED + (party.isOpen() ? "Open" : "Locked"),
                ChatColor.YELLOW + "All Invite&7: " + ChatColor.RED + (party.isAllInvite() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"),
                " ",
                ChatColor.WHITE + builder.toString(),
        };

        player.sendMessage(information);
    }


    //hot
    @Command({"party allinvite", "p allinvite", "p allinv"})
    @Description("Allow all party members to invite.")
    public static void partAllInvite(@Sender Player player) throws BladeExitMessage {
        Party party = plugin.getPartyManager().getParty(player.getUniqueId());

        if(party == null){
            throw new BladeExitMessage("You are not in a party.");
        }

        if (!party.getLeader().equals(player.getUniqueId())) {
            throw new BladeExitMessage(ChatColor.RED + "You are not the leader of this party.");
        }

        party.setAllInvite(!party.isAllInvite());
        party.messageAllMembers(ChatColor.GREEN.toString() + ChatColor.BOLD + "[*]" + " &cYou're are " + (!party.isAllInvite() ? "no longer allowed" : "now allowed") + " to invite people to the party.");
    }
}