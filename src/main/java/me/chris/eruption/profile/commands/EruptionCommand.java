package me.chris.eruption.profile.commands;

import me.chris.eruption.util.CC;
import me.chris.eruption.util.random.Style;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EruptionCommand {
    @Command({"eruption"})
    @Description("Show core details")
    public static void eruptionCommand(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&7This server is running the &cEruption &7Practice plugin on a "));
        player.sendMessage(CC.translate("&7stable development build."));
        player.sendMessage(CC.CHAT_BAR);
    }
}