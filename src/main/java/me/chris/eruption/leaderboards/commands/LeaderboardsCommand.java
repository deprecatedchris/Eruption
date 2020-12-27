package me.chris.eruption.leaderboards.commands;

import me.chris.eruption.leaderboards.menus.LeaderboardsMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaderboardsCommand extends Command {

    public LeaderboardsCommand() {
        super("leaderboards");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        new LeaderboardsMenu().openMenu((Player) commandSender);
        return false;
    }
}
