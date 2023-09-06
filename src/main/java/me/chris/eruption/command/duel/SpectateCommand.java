package me.chris.eruption.command.duel;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.annotation.command.UsageAlias;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectateCommand {
    @Command({"spectate", "spec"})
    @Usage("/spectate <player>")
    @UsageAlias("/spec <player>")
    @Description("Spectate a duel.")
    public static void spectate(@Sender Player player, Player target, String[] args) throws BladeExitMessage {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

        if (args.length < 1) {
            throw new BladeUsageMessage();
        }

        if (target == null) {
            throw new BladeExitMessage(CC.translate("&cCannot find this player"));
        }
        PlayerData targetData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(target.getUniqueId());

        if (targetData.getPlayerState() != PlayerState.FIGHTING) {
            throw new BladeExitMessage(CC.translate("&cThis player is not in a match."));
        }

        if (targetData.getPlayerState() == PlayerState.EVENT) {

            PracticeEvent event = EruptionPlugin.getInstance().getEventManager().getEventPlaying(target);

            if (event != null && event instanceof SumoEvent) {
                player.performCommand("event.spectate");
            }
        }

        Party party = EruptionPlugin.getInstance().getPartyManager().getParty(playerData.getUniqueId());

        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
            throw new BladeExitMessage(CC.translate("&cCannot issue this command in your current state."));
        }

        Match targetMatch = EruptionPlugin.getInstance().getMatchManager().getMatch(targetData);

        if (!targetMatch.isParty()) {
            if (!targetData.getSettings().isSpectatorsAllowed() && !player.hasPermission("practice.staff")) {
                throw new BladeExitMessage(CC.translate("&cThat player has spectators disabled."));
            }

            MatchTeam team = targetMatch.getTeams().get(0);
            MatchTeam team2 = targetMatch.getTeams().get(1);
            PlayerData otherPlayerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(team.getPlayers().get(0) == target.getUniqueId() ? team2.getPlayers().get(0) : team.getPlayers().get(0));
            if (otherPlayerData != null && !otherPlayerData.getSettings().isSpectatorsAllowed() && !player.hasPermission("practice.staff")) {
                throw new BladeExitMessage(CC.translate("&cThat player has spectators disabled."));
            }
        }

        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
            Match match = EruptionPlugin.getInstance().getMatchManager().getSpectatingMatch(player.getUniqueId());

            if (match.equals(targetMatch)) {
                throw new BladeExitMessage(CC.translate("&cYou are already spectating this player."));
            }
            match.removeSpectator(player.getUniqueId());
        }
        player.sendMessage(ChatColor.GREEN + "You are now spectating " + ChatColor.GRAY + target.getName() + ChatColor.GREEN + ".");
        EruptionPlugin.getInstance().getMatchManager().addSpectator(player, playerData, target, targetMatch);
    }

}


//    private final EruptionPlugin plugin = EruptionPlugin.getInstance();
//
//    public SpectateCommand() {
//        super("spectate");
//        this.setDescription("Spectate a profile's match.");
//        this.setUsage(ChatColor.RED + "Usage: /spectate <profile>");
//        this.setAliases(Arrays.asList("spec"));
//    }
//
//    @Override
//    public boolean execute(CommandSender sender, String alias, String[] args) {
//        if (!(sender instanceof Player)) {
//            return true;
//        }
//        Player player = (Player) sender;
//
//        if (args.length < 1) {
//            player.sendMessage(usageMessage);
//            return true;
//        }
//        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
//        Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
//
//        if (party != null || (playerData.getPlayerState() != PlayerState.SPAWN && playerData.getPlayerState() != PlayerState.SPECTATING)) {
//            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
//            return true;
//        }
//
//        Player target = this.plugin.getServer().getPlayer(args[0]);
//
//        if (target == null) {
//            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
//            return true;
//        }
//        PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
//
//        if (targetData.getPlayerState() == PlayerState.EVENT) {
//
//            PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);
//
//            if (event != null && event instanceof SumoEvent) {
//                player.performCommand("event.spectate");
//                return true;
//            }
//        }
//
//        if (targetData.getPlayerState() != PlayerState.FIGHTING) {
//            player.sendMessage(ChatColor.RED + "That profile is currently not in a fight.");
//            return true;
//        }
//
//
//        Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
//
//        if (!targetMatch.isParty()) {
//            if (!targetData.getSettings().isSpectatorsAllowed() && !player.hasPermission("practice.staff")) {
//                player.sendMessage(ChatColor.RED + "That profile has ignored spectators.");
//                return true;
//            }
//
//            MatchTeam team = targetMatch.getTeams().get(0);
//            MatchTeam team2 = targetMatch.getTeams().get(1);
//            PlayerData otherPlayerData = this.plugin.getPlayerManager().getPlayerData(team.getPlayers().get(0) == target.getUniqueId() ? team2.getPlayers().get(0) : team.getPlayers().get(0));
//            if (otherPlayerData != null && !otherPlayerData.getSettings().isSpectatorsAllowed() && !player.hasPermission("practice.staff")) {
//                player.sendMessage(ChatColor.RED + "That profile has ignored spectators.");
//                return true;
//            }
//        }
//        if (playerData.getPlayerState() == PlayerState.SPECTATING) {
//            Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
//
//            if (match.equals(targetMatch)) {
//                player.sendMessage(ChatColor.RED + "You are already spectating this profile.");
//                return true;
//            }
//            match.removeSpectator(player.getUniqueId());
//        }
//        player.sendMessage(ChatColor.GREEN + "You are now spectating " + ChatColor.GRAY + target.getName() + ChatColor.GREEN + ".");
//        this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
//        return true;
//    }
//}
