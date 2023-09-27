//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.chris.eruption.util.menu;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Menu {
    @Getter
    private final ConcurrentHashMap<Integer, Button> buttons = new ConcurrentHashMap();
    @Getter
    private boolean autoUpdate = false;
    @Getter
    private boolean updateAfterClick = true;
    @Getter
    private boolean placeholder = false;
    @Getter
    private boolean noncancellingInventory = false;
    @Getter
    private String staticTitle = null;
    private final boolean fill = false;
    public static Map<String, Menu> currentlyOpenedMenus;
    public static Map<String, BukkitRunnable> checkTasks;
    public static Button BLANK_BUTTON;
    public static ItemStack BLANK_BUTTON_ITEM;

    private Inventory createInventory(Player player) {
        Map<Integer, Button> invButtons = this.getButtons(player);
        Inventory inv = Bukkit.createInventory(player, this.useNormalSize() ? this.size(invButtons) : this.size(player), this.getTitle(player));

        for (Map.Entry<Integer, Button> integerButtonEntry : invButtons.entrySet()) {
            Map.Entry<Integer, Button> buttonEntry = integerButtonEntry;
            this.buttons.put(buttonEntry.getKey(), buttonEntry.getValue());
            inv.setItem(buttonEntry.getKey(), buttonEntry.getValue().getButtonItem(player));
        }

        if (this.isFill(player, invButtons)) {
            Button placeholder = Button.placeholder(Material.STAINED_GLASS_PANE, (fillColor() == 0 ? 15 : fillColor()));

            for(int index = 0; index < (this.useNormalSize() ? this.size(invButtons) : this.size(player)); ++index) {
                boolean skip = false;
                int[] var7 = this.noneFillButtons();
                int var8 = var7.length;

                for (int index1 : var7) {
                    if (index1 == index) {
                        skip = true;
                        break;
                    }
                }

                if (!skip && invButtons.get(index) == null) {
                    this.buttons.put(index, placeholder);
                    inv.setItem(index, placeholder.getButtonItem(player));
                }
            }
        }

        return inv;
    }



    public Menu() {
    }

    public Menu(String staticTitle) {
        this.staticTitle = Preconditions.checkNotNull(staticTitle);
    }

    public void openMenu(Player player) {
        Inventory inv = this.createInventory(player);

        try {
            player.openInventory(inv);
            this.update(player);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public void update(final Player player) {
        cancelCheck(player);
        currentlyOpenedMenus.put(player.getName(), this);
        this.onOpen(player);
        BukkitRunnable runnable = new BukkitRunnable() {
            public void run() {
                if (!player.isOnline()) {
                    Menu.cancelCheck(player);
                    Menu.currentlyOpenedMenus.remove(player.getName());
                }

                try {
                    if (Menu.this.isAutoUpdate()) {
                        player.getOpenInventory().getTopInventory().setContents(Menu.this.createInventory(player).getContents());
                    }
                } catch (IllegalArgumentException var2) {
                    Menu.this.openMenu(player);
                }

            }
        };
        runnable.runTaskTimer(EruptionPlugin.getInstance(), 10L, 10L);
        checkTasks.put(player.getName(), runnable);
    }

    public int[] noneFillButtons() {
        return new int[0];
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

    public abstract Map<Integer, Button> getButtons(Player var1);

    public int size(Player player) {
        return 9;
    }

    public void onOpen(Player player) {
    }

    public void onClose(Player player) {
    }

    public boolean isFill(Player player, Map<Integer, Button> buttons) {
        return false;
    }

    public byte fillColor(){
        return (byte) 15;
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

    public boolean useNormalSize() {
        return true;
    }

    public boolean isFillFiltered(int slot) {
        return false;
    }

    public int size(Map<Integer, Button> buttons) {
        int highest = 0;
        Iterator var3 = buttons.keySet().iterator();

        while(var3.hasNext()) {
            int buttonValue = (Integer)var3.next();
            if (buttonValue > highest) {
                highest = buttonValue;
            }
        }

        return (int)(Math.ceil((double)(highest + 1) / 9.0) * 9.0);
    }

    static {
        EruptionPlugin.getInstance().getServer().getPluginManager().registerEvents(new ButtonListener(), qLib.getInstance());
        currentlyOpenedMenus = new HashMap();
        checkTasks = new HashMap();
        Button BLANK_BUTTON = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE).data((short)15).name(" ").build());
        ItemStack var1 = ItemBuilder.of(Material.STAINED_GLASS_PANE).data((short)15).name(" ").build();
    }
}
