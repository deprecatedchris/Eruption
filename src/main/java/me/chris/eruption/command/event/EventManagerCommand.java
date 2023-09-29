package me.chris.eruption.command.event;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.menu.EventManagerMenu;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;

public class EventManagerCommand {

    @Command("eventmanager")
    @Permission("practice.eventmanager")
    @Description("Opens the event manager menu")
    @Usage("Wrong usage: /eventamanger <event>")
    private static void eventManager(@Sender Player player, String eventName) throws BladeExitMessage {
        if (EruptionPlugin.getInstance().getEventManager().getByName(eventName) == null) {
            throw new BladeExitMessage(CC.RED + "Types: Sumo, LMS, OITC, Runner, 4Corners, Parkour.");
        }

        new EventManagerMenu(eventName).openMenu(player);
    }
}
