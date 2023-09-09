package me.chris.eruption.util.other;


import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ItemBuilder implements Listener {

    private final ItemStack is;

    public ItemBuilder(final Material mat) {
        is = new ItemStack(mat);
    }

    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }

    public ItemBuilder(final Material material, final int amount) {
        this(material, amount, (byte)0);
    }

    public ItemBuilder(final Material material, final int amount, final byte data) {
        Preconditions.checkNotNull((Object)material, "Material cannot be null");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");
        this.is = new ItemStack(material, amount, data);
    }

    public ItemBuilder(final Material material, final int amount, final int data) {
        Preconditions.checkNotNull((Object)material, "Material cannot be null");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");
        this.is = new ItemStack(material, amount, (short)data);
    }

    public ItemBuilder amount(final int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(final String name) {
        final ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final String name) {
        final ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(name);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = is.getItemMeta();

        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }

        meta.setLore(toSet);
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(final int durability) {
        is.setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        is.setData(new MaterialData(is.getType(), (byte) data));
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment) {
        is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(final Material material) {
        is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        final ItemMeta meta = is.getItemMeta();
        meta.setLore(new ArrayList<String>());
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (final Enchantment e : is.getEnchantments().keySet()) {
            is.removeEnchantment(e);
        }
        return this;
    }

    public ItemBuilder texture(String value) {
        if (!(this.is.getItemMeta() instanceof SkullMeta)) {
            return this;
        }

        final SkullMeta meta = (SkullMeta) this.is.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));

        Field profileField;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        this.is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder glow() {
        ItemMeta meta = this.is.getItemMeta();

        meta.addEnchant(new Glow(), 1, true);
        this.is.setItemMeta(meta);

        return this;
    }

    public static void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Glow glow = new Glow();
            Enchantment.registerEnchantment(glow);
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Glow extends Enchantment {

        public Glow() {
            super(25);
        }

        @Override
        public boolean canEnchantItem(ItemStack arg0) {
            return false;
        }

        @Override
        public boolean conflictsWith(Enchantment arg0) {
            return false;
        }

        @Override
        public EnchantmentTarget getItemTarget() {
            return null;
        }

        @Override
        public int getMaxLevel() {
            return 2;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public int getStartLevel() {
            return 1;
        }
    }

    public static ItemBuilder copyOf(ItemBuilder builder) {
        return new ItemBuilder(builder.build());
    }

    public static ItemBuilder copyOf(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public ItemBuilder color(Color color) {
        if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET
                || is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return is;
    }

}