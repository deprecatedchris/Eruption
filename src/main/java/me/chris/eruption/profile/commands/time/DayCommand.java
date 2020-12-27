package me.chris.eruption.profile.commands.time;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DayCommand extends Command {

    public DayCommand() {
        super("day");
        this.setDescription("Set profile time to day.");
        this.setUsage(ChatColor.RED + "Usage: /day");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        ((Player) sender).setPlayerTime(6000L, true);
        return true;
    }
}
