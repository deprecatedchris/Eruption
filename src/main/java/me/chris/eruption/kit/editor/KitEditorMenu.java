package me.chris.eruption.kit.editor;

import lombok.AllArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.ItemBuilder;
import me.chris.eruption.util.other.ItemUtil;
import me.chris.eruption.util.other.Style;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.menu.buttons.DisplayButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitEditorMenu extends Menu {

    private static int[] ITEM_POSITIONS = new int[]{
            20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 47, 48, 49, 50, 51, 52,
            53
    };
    private static int[] BORDER_POSITIONS = new int[]{1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 28, 37, 46};
    private static Button BORDER_BUTTON = Button.placeholder(Material.COAL_BLOCK, (byte) 0, " ");

    public KitEditorMenu() {
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        return Style.AQUA + "Editing " + Style.AQUA + playerData.getSelectedKit().getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        Map<Integer, Button> buttons = new HashMap<>();

        for (int border : BORDER_POSITIONS) {
            buttons.put(border, BORDER_BUTTON);
        }

        buttons.put(0, new CurrentKitButton());
        buttons.put(2, new SaveButton());
        buttons.put(6, new LoadDefaultKitButton());
        buttons.put(7, new ClearInventoryButton());
        buttons.put(8, new CancelButton());
        buttons.put(18, new ArmorDisplayButton(playerData.getSelectedLadder().getArmor()[3]));
        buttons.put(27, new ArmorDisplayButton(playerData.getSelectedLadder().getArmor()[2]));
        buttons.put(36, new ArmorDisplayButton(playerData.getSelectedLadder().getArmor()[1]));
        buttons.put(45, new ArmorDisplayButton(playerData.getSelectedLadder().getArmor()[0]));

        List<ItemStack> items = Arrays.asList(playerData.getSelectedLadder().getKitEditContents());

        for (int i = 20; i < (Arrays.asList(playerData.getSelectedLadder().getKitEditContents()).size()); i++) {
            buttons.put(ITEM_POSITIONS[i - 20], new InfiniteItemButton(items.get(i - 20)));
        }

        return buttons;
    }

    @Override
    public void onOpen(Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setActive(true);

        if (playerData.getSelectedKit() != null) {
            player.getInventory().setContents(playerData.getSelectedKit().getContents());
        }

        player.updateInventory();

    }

    @Override
    public void onClose(Player player) {
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setActive(false);

        if (!playerData.isInMatch()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(EruptionPlugin.getInstance(), () -> EruptionPlugin.getInstance().getPlayerManager().reset(player), 1L);
        }
    }

    @AllArgsConstructor
    private class ArmorDisplayButton extends Button {

        private ItemStack itemStack;

        @Override
        public ItemStack getButtonItem(Player player) {
            if (this.itemStack == null || this.itemStack.getType() == Material.AIR) {
                return new ItemStack(Material.AIR);
            }

            return new ItemBuilder(this.itemStack.clone())
                    .name(Style.AQUA + ItemUtil.getName(this.itemStack))
                    .lore(Arrays.asList(
                            "",
                            Style.GREEN + "This is automatically equipped."
                    ))
                    .build();
        }

    }

    @AllArgsConstructor
    private class CurrentKitButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            return new ItemBuilder(Material.NAME_TAG)
                    .name(Style.AQUA + Style.BOLD + "Editing: " + Style.GREEN +
                            playerData.getSelectedKit().getName())
                    .build();
        }

    }

    @AllArgsConstructor
    private class ClearInventoryButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.WOOL)
                    .durability(4)
                    .name(Style.YELLOW + Style.BOLD + "Clear Inventory")
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "This will clear your menus",
                            Style.YELLOW + "so you can start over."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);
            player.getInventory().clear();
            player.updateInventory();
        }


    }

    @AllArgsConstructor
    private class LoadDefaultKitButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.WOOL)
                    .durability(7)
                    .name(Style.YELLOW + Style.BOLD + "Load default kit")
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "Click this to load the default kit",
                            Style.YELLOW + "into the kit editing menu."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);

            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            player.getInventory()
                    .setContents(playerData.getSelectedLadder().getContents());
            player.updateInventory();
        }


    }

    @AllArgsConstructor
    private class SaveButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.WOOL)
                    .durability(5)
                    .name(Style.GREEN + Style.BOLD + "Save")
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "Click this to save your kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);
            player.closeInventory();

            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            if (playerData.getSelectedKit() != null) {
                playerData.getSelectedKit().setContents(player.getInventory().getContents());
            }

            EruptionPlugin.getInstance().getPlayerManager().giveLobbyItems(player);

            new KitManagementMenu(playerData.getSelectedLadder()).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class CancelButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.WOOL)
                    .durability(14)
                    .name(Style.RED + Style.BOLD + "Cancel")
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "Click this to abort editing your kit,",
                            Style.YELLOW + "and return to the kit menu."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Button.playNeutral(player);

            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            if (playerData.getSelectedLadder() != null) {
                new KitManagementMenu(playerData.getSelectedLadder()).openMenu(player);
            }
        }

    }

    private class InfiniteItemButton extends DisplayButton {

        InfiniteItemButton(ItemStack itemStack) {
            super(itemStack, false);
        }

        @Override
        public void clicked(Player player, int i, ClickType clickType, int hb) {
            Inventory inventory = player.getOpenInventory().getTopInventory();
            ItemStack itemStack = inventory.getItem(i);

            inventory.setItem(i, itemStack);

            player.setItemOnCursor(itemStack);
            player.updateInventory();
        }

    }

}
