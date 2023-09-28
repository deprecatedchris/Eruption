package me.chris.eruption.kit.editor.button;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.util.other.ItemBuilder;
import me.chris.eruption.util.other.Style;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;

import java.util.Arrays;
import java.util.List;


@AllArgsConstructor
public class BackButton extends Button {

    private Menu back;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.REDSTONE).name(Style.RED + Style.BOLD + "Back").lore(Arrays
                .asList(Style.RED + "Click here to return to", Style.RED + "the previous menu.")).build();
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType, int hb) {

        this.back.openMenu(player);
    }

}
