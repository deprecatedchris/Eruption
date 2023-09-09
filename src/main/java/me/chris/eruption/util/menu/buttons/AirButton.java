package me.chris.eruption.util.menu.buttons;

import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AirButton extends Button {
    @Override
    public String getName(Player var1) {
        return null;
    }

    @Override
    public List<String> getDescription(Player var1) {
        return null;
    }

    @Override
    public Material getMaterial(Player var1) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.AIR, 1);
        return itemBuilder.build();
    }
}
