package me.chris.eruption.kit.editor;

import lombok.AllArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.util.other.ItemBuilder;
import me.chris.eruption.util.other.Style;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.profile.PlayerData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SelectLadderKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return Style.BLUE + Style.BOLD + "Select a kit...";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()) {
            if (kit.getKitEditContents()[0] != null) {
                buttons.put(buttons.size(), new LadderKitDisplayButton(kit));
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class LadderKitDisplayButton extends Button {

        private Kit ladder;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(this.ladder.getIcon().getType())
                    .name(Style.GREEN + Style.BOLD + this.ladder.getName())
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "Click to select " + Style.YELLOW + Style.BOLD + this.ladder.getName() +
                                    Style.YELLOW + "."
                    ))
                    .durability(this.ladder.getIcon().getDurability())
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            player.closeInventory();

            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            playerData.setSelectedLadder(this.ladder);
            new KitManagementMenu(this.ladder).openMenu(player);
        }

    }
}
