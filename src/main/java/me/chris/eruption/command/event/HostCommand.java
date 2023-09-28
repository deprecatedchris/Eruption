package me.chris.eruption.command.event;

import me.chris.eruption.events.menu.EventHostMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;

public class HostCommand  {
    @Command("host")
    @Description("Host an event")
    @Permission("practice.host")
    private void host(@Sender Player player) throws BladeExitMessage {

        new EventHostMenu().openMenu(player);
    }

}
