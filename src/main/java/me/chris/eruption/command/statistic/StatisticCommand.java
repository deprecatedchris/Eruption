package me.chris.eruption.command.statistic;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

//Todo: recode this into leaderboards menu.
public class StatisticCommand {

    @Command({"statistic", "stats"})
    public static void statisticCommand(@Sender Player player, PlayerData target) throws BladeExitMessage {
        final PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(
                target.getUniqueId()
        );

        if (playerData == null) {
            throw new BladeExitMessage("That player does not exist!");
        }

        player.sendMessage(new String[] {
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------",
                ChatColor.RED + "            " + ChatColor.WHITE + player.getName() + ChatColor.RED +  "'s Statistics",
                ChatColor.GRAY + "",
                ChatColor.RED.toString() + ChatColor.BOLD + "General Statistics",
                ChatColor.GRAY + "",
                ChatColor.YELLOW + "Global: " + ChatColor.WHITE + target.getGlobalStats("ELO") + " ELO ",
                ChatColor.GRAY + "",
                ChatColor.RED.toString() + ChatColor.BOLD + "ELO Statistics",
                ChatColor.GRAY + "",
                ChatColor.RED + "OITC: " + ChatColor.WHITE + target.getElo("OITC"),
                ChatColor.RED + "Runner: " + ChatColor.WHITE + target.getElo("Runner"),
                ChatColor.RED + "Parkour: " + ChatColor.WHITE + target.getElo("Parkour"),
                ChatColor.RED + "Sumo: " + ChatColor.WHITE + target.getElo("Sumo"),
                ChatColor.RED + "Corners: " + ChatColor.WHITE + target.getElo("Corners"),
                ChatColor.RED + "LMS: " + ChatColor.WHITE + target.getElo("LMS"),
                ChatColor.GRAY + "",
                ChatColor.RED.toString() + ChatColor.BOLD + "Event Statistics",
                ChatColor.GRAY + "",
                ChatColor.GREEN + "OITC Wins: " + ChatColor.WHITE + target.getOitcWins(),
                ChatColor.GREEN + "Runner Wins: " + ChatColor.WHITE + target.getRunnerWins(),
                ChatColor.GREEN + "Parkour Wins: " + ChatColor.WHITE + target.getParkourWins(),
                ChatColor.GREEN + "Sumo Wins: " + ChatColor.WHITE + target.getSumoWins(),
                ChatColor.GREEN + "Corners Wins: " + ChatColor.WHITE + target.getCornersWins(),
                ChatColor.GREEN + "LMS Wins: " + ChatColor.WHITE + target.getLmsWins(),
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------"
        });
    }
}
