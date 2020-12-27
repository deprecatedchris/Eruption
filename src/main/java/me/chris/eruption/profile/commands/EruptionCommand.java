package me.chris.eruption.profile.commands;

import me.chris.eruption.util.random.Style;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EruptionCommand extends Command {

    public EruptionCommand() {
        super("eruption");
        this.setDescription("Eruption Command.");
        this.setAliases(Collections.singletonList("praccore"));
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
            player.sendMessage(Style.translate("&7This server is running the &cEruption &7Practice plugin on a "));
            player.sendMessage(Style.translate("&7stable development build."));
            player.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        }
        return true;
    }
}
