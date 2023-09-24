package me.chris.eruption.events.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.menu.EventManagerMenu;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventManagerCommand {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();


    @Command("eventmanager")
    @Permission("practice.eventmanager")
    @Description("Opens the event manager menu")
    @Usage("Wrong usage: /eventamanger <event>")
    private void eventManager(@Sender Player player, String eventName) throws BladeExitMessage {

        if (plugin.getEventManager().getByName(eventName) == null) {
            throw new BladeExitMessage(CC.RED + "Types: Sumo, LMS, OITC, Runner, 4Corners, Parkour.");
        }

        new EventManagerMenu(eventName).openMenu(player);

    }

}
