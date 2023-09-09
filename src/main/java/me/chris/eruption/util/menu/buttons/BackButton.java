package me.chris.eruption.util.menu.buttons;

import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class BackButton extends Button {
    private Menu back;

    public Material getMaterial(Player player) {
        return Material.BARRIER;
    }

    public byte getDamageValue(Player player) {
        return 0;
    }

    public String getName(Player player) {
        return "§c§lGo back";
    }

    public List<String> getDescription(Player player) {
        return new ArrayList();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }

    @ConstructorProperties({"back"})
    public BackButton(Menu back) {
        this.back = back;
    }
}
