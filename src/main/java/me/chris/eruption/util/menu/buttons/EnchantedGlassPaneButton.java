package me.chris.eruption.util.menu.buttons;

import lombok.AllArgsConstructor;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class EnchantedGlassPaneButton extends Button {
    private short theShort;
    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder builder = new ItemBuilder(Material.STAINED_GLASS_PANE, 1).data(theShort).name(" ").glow();
        return builder.build();
    }
}
