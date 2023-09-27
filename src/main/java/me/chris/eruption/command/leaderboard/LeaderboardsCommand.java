package me.chris.eruption.command.leaderboard;

import me.chris.eruption.command.leaderboard.menu.LeaderboardMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.bukkit.entity.Player;

public class LeaderboardsCommand {

    @Command({"leaderboard", "lb"})
    @Description("View leaderboards")
    public static void leaderboardCommand(@Sender Player player) {
        new LeaderboardMenu().openMenu(player);
    }
}

