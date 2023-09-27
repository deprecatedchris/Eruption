package me.chris.eruption.command.event.user;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.Clickable;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatusEventCommand {
    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();


    @Command({"event status", "status"})
    @Usage("/event status")
    @Description("View the status of an event or tournament.")
    public static void eventJoin(@Sender Player player) throws BladeExitMessage {
        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            throw new BladeExitMessage(CC.translate("&cCannot issue this command in your current state."));
        }

        if (plugin.getTournamentManager().getTournaments().isEmpty()) {
            throw new BladeExitMessage(CC.translate("&cThere are no available tournaments."));
        }

        for (Tournament tournament : plugin.getTournamentManager().getTournaments().values()) {


            if (tournament == null) {
                throw new BladeExitMessage(CC.translate("&cThis tournament doesn't exist."));
            }


            player.sendMessage(" ");
            player.sendMessage(CC.translate("&e&lTournament (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") &67l" + tournament.getKitName()));


            if (tournament.getMatches().isEmpty()) {
                player.sendMessage(ChatColor.RED + "There is no available matches.");
                player.sendMessage(" ");
            }

            for (UUID matchUUID : tournament.getMatches()) {
                Match match = plugin.getMatchManager().getMatchFromUUID(matchUUID);

                MatchTeam teamA = match.getTeams().get(0);
                MatchTeam teamB = match.getTeams().get(1);

                boolean teamAParty = teamA.getAlivePlayers().size() > 1;
                boolean teamBParty = teamB.getAlivePlayers().size() > 1;
                String teamANames = (teamAParty ? teamA.getLeaderName() + "'s Party" : teamA.getLeaderName());
                String teamBNames = (teamBParty ? teamB.getLeaderName() + "'s Party" : teamB.getLeaderName());


                Clickable clickable = new Clickable(ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.GOLD.toString() + teamANames + " vs " + teamBNames + ChatColor.DARK_GRAY + " | " + CC.GREEN + "[Click to Spectate]",  CC.GREEN + "Click to spectate",
                        "/spectate " + teamA.getLeaderName());

                clickable.sendToPlayer(player);
            }

            player.sendMessage(" ");
        }
    }
}
