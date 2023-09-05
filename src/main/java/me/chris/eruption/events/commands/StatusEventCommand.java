package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.util.random.Clickable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

//TODO: Recode this command to blade.
public class StatusEventCommand extends Command {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public StatusEventCommand() {
        super("status");
        this.setDescription("Show an commands or tournament status.");
        this.setUsage(ChatColor.RED + "Usage: /status");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        if (playerData.getPlayerState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
            return true;
        }

        if (this.plugin.getTournamentManager().getTournaments().size() == 0) {
            player.sendMessage(ChatColor.RED + "There is no available tournaments.");
            return true;
        }

        for (Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {


            if (tournament == null) {
                player.sendMessage(ChatColor.RED + "This tournament doesn't exist.");
                return true;
            }


            player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.YELLOW.toString() + "Tournament (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") " + ChatColor.GOLD.toString() + tournament.getKitName());


            if (tournament.getMatches().size() == 0) {
                player.sendMessage(ChatColor.RED + "There is no available matches.");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                return true;
            }

            for (UUID matchUUID : tournament.getMatches()) {
                Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);

                MatchTeam teamA = match.getTeams().get(0);
                MatchTeam teamB = match.getTeams().get(1);

                boolean teamAParty = teamA.getAlivePlayers().size() > 1;
                boolean teamBParty = teamB.getAlivePlayers().size() > 1;
                String teamANames = (teamAParty ? teamA.getLeaderName() + "'s Party" : teamA.getLeaderName());
                String teamBNames = (teamBParty ? teamB.getLeaderName() + "'s Party" : teamB.getLeaderName());


                Clickable clickable = new Clickable(ChatColor.WHITE.toString() + ChatColor.BOLD + "* " + ChatColor.GOLD.toString() + teamANames + " vs " + teamBNames + ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "[Click to Spectate]", ChatColor.GRAY + "Click to spectate",
                        "/spectate " + teamA.getLeaderName());

                clickable.sendToPlayer(player);
            }

            player.sendMessage(" ");
            player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");

        }
        return true;

    }
}
