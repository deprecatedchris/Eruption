package me.chris.eruption.command.time;

import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import org.bukkit.entity.Player;

public class TimeCommand {

    @Command({"day"})
    @Description("Set time to day.")
    public void onDay(@Sender Player player) {
        player.setPlayerTime(6000L, true);
        player.sendMessage(CC.YELLOW + "You've set your in-game time to " + CC.GREEN + "Day" + CC.YELLOW + ".");
    }

    @Command({"night"})
    @Description("Set time to night.")
    public void onNight(@Sender Player player) {
        player.setPlayerTime(18000L, true);
        player.sendMessage(CC.YELLOW + "You've set your in-game time to " + CC.BLUE + "Night" + CC.YELLOW + ".");
    }

    @Command({"sunset"})
    @Description("Set time to sunset.")
    public void onSunset(@Sender Player player) {
        player.setPlayerTime(12000L, true);
        player.sendMessage(CC.YELLOW + "You've set your in-game time to " + CC.AQUA + "Sunset" + CC.YELLOW + ".");
    }
}

