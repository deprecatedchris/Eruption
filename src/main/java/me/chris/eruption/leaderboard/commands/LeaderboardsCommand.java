package me.chris.eruption.leaderboard.commands;

import me.chris.eruption.leaderboard.menus.LeaderboardsMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardsCommand {
    @Command({"leaderboard", "lb"})
    @Description("View leaderboards")
    public static void leaderboardCommand(@Sender Player player, CommandSender sender) {
        new LeaderboardsMenu().openMenu((Player) sender);
    }
}

