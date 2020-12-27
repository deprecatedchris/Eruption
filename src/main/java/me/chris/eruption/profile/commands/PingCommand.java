package me.chris.eruption.profile.commands;

import me.chris.eruption.util.random.PlayerUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends Command {

    public PingCommand() {
        super("ping");

        setDescription("Get ping of players.");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYour ping: &a" + PlayerUtil.getPing(player) + " &7ms"));
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cFailed to find that player."));
            return false;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',ChatColor.RED + target.getDisplayName() + "&7's ping: " + PlayerUtil.getPing(target) + " ms"));
        return false;
    }
}
