package me.chris.eruption.command.extra;

import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.bukkit.entity.Player;

public class EruptionCommand {
    @Command({"eruption"})
    @Description("Show core details")
    public static void eruptionCommand(@Sender Player player) {
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&eThis server is running the &cEruption &ePractice plugin on a "));
        player.sendMessage(CC.translate("&estable development build."));
        player.sendMessage(CC.CHAT_BAR);
    }
}