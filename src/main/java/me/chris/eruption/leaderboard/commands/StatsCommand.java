package me.chris.eruption.leaderboard.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;

//TODO: Make this a menu & recode to blade.
public class StatsCommand extends Command {
    
        private final EruptionPlugin plugin = EruptionPlugin.getInstance();

        public StatsCommand() {
            super("stats");
            this.setAliases( Arrays.asList("elo", "statistics"));
            this.setUsage( ChatColor.RED + "Usage: /stats [player]");
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] args) {

            Player player = (Player)sender;
            if (args.length == 0) {
                PlayerData Sender = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
                sender.sendMessage(ChatColor.RED.toString() + "            " + ChatColor.WHITE + player.getName() + ChatColor.RED +  "'s Statistics");
                sender.sendMessage(ChatColor.GRAY + "");
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "General Statistics");
                sender.sendMessage(ChatColor.GRAY + "");
                sender.sendMessage(ChatColor.YELLOW + "Global: " + ChatColor.WHITE + Sender.getGlobalStats("ELO") + " ELO ");
                sender.sendMessage(ChatColor.GRAY + "");
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "ELO Statistics");
                sender.sendMessage(ChatColor.GRAY + "");
                for (Kit kit : this.plugin.getKitManager().getKits()) {
                    sender.sendMessage(ChatColor.RED + kit.getName() + ": " + ChatColor.WHITE + Sender.getElo(kit.getName()));
                }
                sender.sendMessage(ChatColor.GRAY + "");
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Event Statistics");
                sender.sendMessage(ChatColor.GRAY + "");
                sender.sendMessage(ChatColor.GREEN + "OITC Wins: " + ChatColor.WHITE + Sender.getOitcWins());
                sender.sendMessage(ChatColor.GREEN + "Runner Wins: " + ChatColor.WHITE + Sender.getRunnerWins());
                sender.sendMessage(ChatColor.GREEN + "Parkour Wins: " + ChatColor.WHITE + Sender.getParkourWins());
                sender.sendMessage(ChatColor.GREEN + "Sumo Wins: " + ChatColor.WHITE + Sender.getSumoWins());
                sender.sendMessage(ChatColor.GREEN + "Corners Wins: " + ChatColor.WHITE + Sender.getCornersWins());
                sender.sendMessage(ChatColor.GREEN + "LMS Wins: " + ChatColor.WHITE + Sender.getLmsWins());
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
                return true;
            }

            Player target = this.plugin.getServer().getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(String.format( StringUtil.PLAYER_NOT_FOUND, args[0]));
                return true;
            }

            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
            sender.sendMessage(ChatColor.RED.toString() + "            " + ChatColor.WHITE+ target.getName() + ChatColor.RED + "'s Statistics");
            sender.sendMessage(ChatColor.GRAY + "");
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "General Statistics");
            sender.sendMessage(ChatColor.GRAY + "");
            sender.sendMessage(ChatColor.YELLOW + "Global: " + ChatColor.WHITE + playerData.getGlobalStats("ELO") + " ELO ");
            sender.sendMessage(ChatColor.GRAY + "");
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "ELO Statistics");
            sender.sendMessage(ChatColor.GRAY + "");

            for (Kit kit : this.plugin.getKitManager().getKits()) {
                sender.sendMessage(ChatColor.RED + kit.getName() + ": " + ChatColor.WHITE + playerData.getElo(kit.getName()));
            }
            sender.sendMessage(ChatColor.GRAY + "");
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Event Statistics");
            sender.sendMessage(ChatColor.GRAY + "");
            sender.sendMessage(ChatColor.GREEN + "OITC Wins: " + ChatColor.WHITE + playerData.getOitcWins());
            sender.sendMessage(ChatColor.GREEN + "Runner Wins: " + ChatColor.WHITE + playerData.getRunnerWins());
            sender.sendMessage(ChatColor.GREEN + "Parkour Wins: " + ChatColor.WHITE + playerData.getParkourWins());
            sender.sendMessage(ChatColor.GREEN + "Sumo Wins: " + ChatColor.WHITE + playerData.getSumoWins());
            sender.sendMessage(ChatColor.GREEN + "Corners Wins: " + ChatColor.WHITE + playerData.getCornersWins());
            sender.sendMessage(ChatColor.GREEN + "LMS Wins: " + ChatColor.WHITE + playerData.getLmsWins());
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
            return true;
        }
    }
