package me.chris.eruption.profile.commands;

import me.chris.eruption.util.random.Style;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class PracticeCommand extends Command {

    public PracticeCommand() {
        super("help");
        this.setDescription("Practice Command.");
        this.setAliases(Collections.singletonList("?"));
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
            sender.sendMessage(Style.translate(" &cEruption &7» Information On Practice"));
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
            sender.sendMessage(Style.translate("&cDefault Commands:"));
            sender.sendMessage(Style.translate("&7/duel <Name> &8- &7Send a player a duel request"));
            sender.sendMessage(Style.translate("&7/spec <Name> &8- &7Spectate a player's ongoing match"));
            sender.sendMessage(Style.translate("&7/settings &8- &7Edit preferences on the server"));
            sender.sendMessage(Style.translate("&7/party &8- &7See all party related commands"));
            sender.sendMessage(" ");
            sender.sendMessage(Style.translate("&7To send a message to your party do &7'&d@&7' <message>:"));
            sender.sendMessage(" ");
            sender.sendMessage(Style.translate("&cServer Links:"));
            sender.sendMessage(Style.translate("&7Website &8- cbshinto.online"));
            sender.sendMessage(Style.translate("&7Discord &8- &chttps://discord.gg/5UnmMqGHs5"));
            sender.sendMessage(Style.translate("&7Store &8- &cstore.shinto.online"));
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------");
        }
        return true;
    }
}
