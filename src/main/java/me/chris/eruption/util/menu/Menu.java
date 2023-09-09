package me.chris.eruption.util.menu;

import com.google.common.base.Preconditions;
import lombok.Getter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Menu {
    @Getter
    private final ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap<>();
    @Getter private boolean autoUpdate = false;
    @Getter private boolean updateAfterClick = true;
    @Getter private boolean placeholder = false;
    @Getter private boolean noncancellingInventory = false;
    @Getter
    private String staticTitle = null;
    private boolean fill = false;
    public static Map<String, Menu> currentlyOpenedMenus;
    public static Map<String, BukkitRunnable> checkTasks;
    public static Button BLANK_BUTTON;
    public static ItemStack BLANK_BUTTON_ITEM;

    //todo: i need a way to make specific spots no filled and others filled; YAY IT DID IT
    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = this.getButtons(player);
        Inventory inv = Bukkit.createInventory(player, (useNormalSize() ? this.size(invButtons) : this.size(player)), this.getTitle(player));
        for (Map.Entry<Integer, Button> buttonEntry : invButtons.entrySet()) {
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inv.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }

        if (isFill(player, invButtons)) {
            //todo: have 0 clue why this doesn't work
            Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15);
            for (int index = 0; index < (useNormalSize() ? this.size(invButtons) : this.size(player)); ++index) {
                boolean skip = false;
                for (int index1 : noneFillButtons()) {
                    if (index1 == index) {
                        skip = true;
                        break; // No need to check further, we can skip this slot
                    }
                }
                if (skip || invButtons.get(index) != null) {
                    continue; // Skip this iteration if "skip" is true or there's a button
                }

                this.buttons.put(index, placeholder);
                inv.setItem(index, placeholder.getButtonItem(player));
            }
        }
        return inv;
    }

    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle = (String) Preconditions.checkNotNull((Object) staticTitle);
    }

    public void openMenu(Player player) {

        Inventory inv = this.createInventory(player);
        try {
            player.openInventory(inv);
            this.update(player);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void update(final Player player) {
        Menu.cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    currentlyOpenedMenus.remove(player.getName());
                }
                try {
                    if (Menu.this.isAutoUpdate()) {
                        player.getOpenInventory().getTopInventory().setContents(Menu.this.createInventory(player).getContents());
                    }
                } catch (IllegalArgumentException ignored) {
                    openMenu(player);
                }
            }
        };
        runnable.runTaskTimer(EruptionPlugin.getInstance(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public int[] noneFillButtons(){
        return new int[]{};
    }

    public static void cancelCheck(Player player) {
        if (checkTasks.containsKey(player.getName())) {
            checkTasks.remove(player.getName()).cancel();
        }
    }

    public int getSlot(int x, int y) {
        return 9 * y + x;
    }

    public String getTitle(Player player) {
        return this.staticTitle;
    }

    public abstract Map<Integer, Button> getButtons(Player player);

    public int size(Player player){
        return 9;
    }

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public boolean isFill(Player player, Map<Integer, Button> buttons) {
        return false;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public void setUpdateAfterClick(boolean updateAfterClick) {
        this.updateAfterClick = updateAfterClick;
    }

    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }

    public void setNoncancellingInventory(boolean noncancellingInventory) {
        this.noncancellingInventory = noncancellingInventory;
    }

    public boolean useNormalSize(){
        return true;
    }

    public boolean isFillFiltered(int slot) {
        return false;
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        for (int buttonValue : buttons.keySet()) {
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int) (Math.ceil((double) (highest + 1) / 9.0D) * 9.0D);
    }

    static {
        EruptionPlugin.getInstance().getServer().getPluginManager().registerEvents(new ButtonListener(), EruptionPlugin.getInstance());
        currentlyOpenedMenus = new HashMap<>();
        checkTasks = new HashMap<>();
        Button BLANK_BUTTON = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).data((short) 15).name(" ").build());
        ItemStack BLANK_BUTTON_ITEM = ItemBuilder.of(Material.STAINED_GLASS_PANE).data((short) 15).name(" ").build();
    }
}

