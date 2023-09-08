package me.chris.eruption.util.menu;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import me.chris.eruption.util.random.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class Button {

    @Deprecated
    public static Button placeholder(Material material, byte data, String... title) {
        return placeholder(material, data, title != null && title.length != 0 ? Joiner.on(" ").join(title) : " ");
    }

    public static Button placeholder(Material material) {
        return placeholder(material, " ");
    }

    public static Button placeholder(Material material, String title) {
        return placeholder(material,(byte)0,title);
    }

    public String getSkullTexture(Player player) {
        return "";
    }

    public static Button placeholder(final Material material, final byte data, final String title) {
        return new Button(){

            @Override
            public String getName(Player player) {
                return title;
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of();
            }

            @Override
            public Material getMaterial(Player player) {
                return material;
            }

            @Override
            public byte getDamageValue(Player player) {
                return data;
            }
        };
    }

    public static Button fromItem(final ItemStack item) {
        return new Button(){

            @Override
            public ItemStack getButtonItem(Player player) {
                return item;
            }

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


        };
    }

    public abstract String getName(Player var1);

    public abstract List<String> getDescription(Player var1);

    public abstract Material getMaterial(Player var1);

    public byte getDamageValue(Player player) {
        return 0;
    }

    public void clicked(Player player, int slot, ClickType clickType) {
    }

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public int getAmount(Player player) {
        return 1;
    }

    public ItemStack getButtonItem(Player player) {
        ItemStack buttonItem = new ItemStack(this.getMaterial(player), this.getAmount(player), this.getDamageValue(player));
        ItemMeta meta = buttonItem.getItemMeta();
        meta.setDisplayName(this.getName(player) == null ? "fixing this" : this.getName(player));
        List<String> description = this.getDescription(player);
        if (description != null) {
            meta.setLore(description);
        }
        buttonItem.setItemMeta(meta);

        if (!this.getSkullTexture(player).equals("") && this.getMaterial(player).equals(Material.SKULL_ITEM)) {
            buttonItem = ItemBuilder.copyOf(buttonItem).texture(this.getSkullTexture(player)).build();
        }
        return buttonItem;
    }

    public static void playFail(Player player) {
        player.playSound(player.getLocation(), Sound.DIG_GRASS, 20.0f, 0.1f);
    }

    public static void playSuccess(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 15.0f);
    }

    public static void playNeutral(Player player) {
        player.playSound(player.getLocation(), Sound.CLICK, 20.0f, 1.0f);
    }

    public static Button createPlaceholder() {
        return createPlaceholder(" ", Material.STAINED_GLASS_PANE, (short) 15);
    }

    public static Button createPlaceholder(Material material, short subId) {
        return createPlaceholder(" ", material, subId);
    }

    public static Button createPlaceholder(String displayName, Material material, short subId) {
        return new Button() {
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
                return new ItemBuilder(material, subId)
                        .name(displayName)
                        .build();
            }
        };
    }

    public static Button createPlaceholder(ItemStack item) {
        return new Button() {
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
                return ItemBuilder.copyOf(item).build();
            }
        };
    }

    public static String styleAction(ChatColor color, String action, String text) {
        return color.toString() + ChatColor.BOLD + action + ChatColor.RESET + color + " " + text;
    }


}

