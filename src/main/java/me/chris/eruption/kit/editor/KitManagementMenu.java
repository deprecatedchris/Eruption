package me.chris.eruption.kit.editor;

import lombok.AllArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.kit.editor.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.ItemBuilder;
import me.chris.eruption.util.other.Style;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


//what menus?
public class KitManagementMenu extends Menu {

    private static Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");

    private Kit ladder;

    public KitManagementMenu(Kit ladder) {
        this.ladder = ladder;

        this.setPlaceholder(true);
        this.setUpdateAfterClick(false);
    }

    @Override
    public String getTitle(Player player) {
        return Style.DARK_GRAY + "Viewing " + this.ladder.getName() + " kits";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        PlayerKit[] kits = playerData.getKits(this.ladder);

        if (kits == null) {
            return buttons;
        }

        int startPos = -1;

        for (int i = 0; i < 4; i++) {
            PlayerKit kit = kits[i];
            startPos += 2;

            buttons.put(startPos, kit == null ? new CreateKitButton(i) : new KitDisplayButton(kit));
            buttons.put(startPos + 18, new LoadKitButton(i));
            buttons.put(startPos + 27, kit == null ? PLACEHOLDER : new RenameKitButton(kit));
            buttons.put(startPos + 36, kit == null ? PLACEHOLDER : new DeleteKitButton(kit));
        }

        buttons.put(36, new BackButton(new SelectLadderKitMenu()));

        return buttons;
    }

    @Override
    public void onClose(Player player) {
        if (!this.isClosedByMenu()) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            playerData.setSelectedLadder(null);
        }
    }

    @AllArgsConstructor
    private class DeleteKitButton extends Button {

        private PlayerKit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.WOOL)
                    .name(Style.AQUA + Style.BOLD + "Delete")
                    .durability(14)
                    .lore(Arrays.asList(
                            "",
                            Style.RED + "Click to delete this kit.",
                            Style.RED + "You will " + Style.BOLD + "NOT" + Style.RED + " be able to",
                            Style.RED + "recover this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            playerData.deleteKit(playerData.getSelectedLadder(), this.kit);

            new KitManagementMenu(playerData.getSelectedLadder()).openMenu(player);
        }

    }

    @AllArgsConstructor
    private class CreateKitButton extends Button {

        private int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.IRON_SWORD)
                    .name(Style.AQUA + Style.BOLD + "Create Kit")
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            Kit ladder = playerData.getSelectedLadder();

            // TODO: this shouldn't be null but sometimes it is?
            if (ladder == null) {
                player.closeInventory();
                return;
            }

            PlayerKit kit = new PlayerKit("Kit " + (this.index + 1), this.index, ladder.getContents(), "Kit " + (this.index + 1));
            kit.setContents(playerData.getSelectedLadder().getContents());

            playerData.addPlayerKit(this.index, kit);
            playerData.replaceKit(playerData.getSelectedLadder(), this.index, kit);
            playerData.setSelectedKit(kit);

            new KitEditorMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class RenameKitButton extends Button {

        private PlayerKit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.SIGN)
                    .name(Style.AQUA + Style.BOLD + "Rename")
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "Click to rename this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            playerData.setActive(true);
            playerData.setRename(true);
            playerData.setSelectedKit(this.kit);

            player.closeInventory();
            player.sendMessage(
                    Style.YELLOW + "Renaming " + Style.BOLD + this.kit.getName() + Style.YELLOW + "... " + Style.GREEN +
                            "Enter the new name now.");
        }

    }


    @AllArgsConstructor
    private class LoadKitButton extends Button {

        private int index;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name(Style.AQUA + Style.BOLD + "Load/Edit")
                    .lore(Arrays.asList(
                            "",
                            Style.YELLOW + "Click to edit this kit."
                    ))
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

            // TODO: this shouldn't be null but sometimes it is?
            if (playerData.getSelectedLadder() == null) {
                player.closeInventory();
                return;
            }

            PlayerKit kit = playerData.getKit(playerData.getSelectedLadder(), this.index);

            if (kit == null) {
                kit = new PlayerKit("Kit " + (this.index + 1), this.index, ladder.getContents().clone(), "Kit " + (this.index + 1));
                kit.setContents(playerData.getSelectedLadder().getContents());

                for (ItemStack is : kit.getContents()) {
                    if(is.getType() == Material.GOLDEN_APPLE) {

                        int goldenapples = is.getAmount();

                        Arrays.stream(playerData.getSelectedLadder().getContents()).filter(itemStack -> {

                            if(itemStack.getType() == Material.GOLDEN_APPLE) {
                                int normalgoldenapples = itemStack.getAmount();

                                if(goldenapples > normalgoldenapples) {
                                    player.closeInventory();
                                    return true;
                                }

                            }

                            return false;
                        });

                    }
                }

                playerData.addPlayerKit(this.index, kit);
                playerData.replaceKit(playerData.getSelectedLadder(), this.index, kit);
            }

            playerData.setSelectedKit(kit);

            new KitEditorMenu().openMenu(player);
        }

    }

    @AllArgsConstructor
    private class KitDisplayButton extends Button {

        private PlayerKit kit;


        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.BOOK)
                    .name(Style.GREEN + Style.BOLD + this.kit.getName())
                    .build();
        }

    }

}
