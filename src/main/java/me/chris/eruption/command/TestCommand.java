package me.chris.eruption.command;

import me.chris.eruption.command.menu.TestMenu;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Permission;
import org.bukkit.entity.Player;

public class TestCommand {

    @Command(value = "test")
    @Permission("eruption.test")
    public static void test(@Sender Player player) {
        new TestMenu(player).updateMenu();
    }
}
