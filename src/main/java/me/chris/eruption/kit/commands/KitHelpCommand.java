package me.chris.eruption.kit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitHelpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("kithelp")) {
            if (player.hasPermission("practice.staff")) {
                player.sendMessage("&7&m---------------------------");
                player.sendMessage("&cEruption &7» Kit Help&8 ");
                player.sendMessage("&7&m---------------------------");
                player.sendMessage(" &c* &7/kit create <namekit> ");
                player.sendMessage(" &c* &7/kit setinv <namekit>");
                player.sendMessage(" &c* &7/kit whitelistarena <namekit> <namearena>");
                player.sendMessage(" &c* &7/kit excludearena <namekit> <namearena>");
                player.sendMessage(" &c* &7/kit excludearenafromallkitsbut <namekit> <namearena>");
                player.sendMessage(" &c* &7/kit icon <namekit> <namearena>");
                player.sendMessage(" &c* &7/kit seteditinv <namekit> <namearena>");
                player.sendMessage(" &c* &7/kit flag <kit> <flag>");
                player.sendMessage("&7&m---------------------------");
                return true;
            }
        }
        return false;
    }
}
