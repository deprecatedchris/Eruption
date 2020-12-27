package me.chris.eruption.util.random;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public final class PlayerUtil {

	private PlayerUtil() {
	}

	public static void setFirstSlotOfType(Player player, Material type, ItemStack itemStack) {
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			ItemStack itemStack1 = player.getInventory().getContents()[i];
			if (itemStack1 == null || itemStack1.getType() == type || itemStack1.getType() == Material.AIR) {
				player.getInventory().setItem(i, itemStack);
				break;
			}
		}
	}

	public static void clearPlayer(Player player) {
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		player.setSaturation(12.8F);
		player.setMaximumNoDamageTicks(20);
		player.setFireTicks(0);
		player.setFallDistance(0.0F);
		player.setLevel(0);
		player.setExp(0.0F);
		player.setWalkSpeed(0.2F);
		player.getInventory().setHeldItemSlot(0);
		player.setAllowFlight(false);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.closeInventory();
		player.setGameMode(GameMode.SURVIVAL);
		player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
		player.updateInventory();
	}

	public static void sendMessage(String message, Player... players) {
		for (Player player : players) {
			player.sendMessage(message);
		}
	}

	public static void sendMessage(String message, Set<Player> players) {
		for (Player player : players) {
			player.sendMessage(message);
		}
	}

	public static int getPing(Player player) {
		int ping = ((CraftPlayer)player).getHandle().ping;

		return ping;
	}

	public static void sendFirework(FireworkEffect effect, Location location) {
		Firework f = location.getWorld().spawn(location, Firework.class);
		FireworkMeta fm = f.getFireworkMeta();
		fm.addEffect(effect);
		f.setFireworkMeta(fm);

		try {
			Class<?> entityFireworkClass = getClass("net.minecraft.server.", "EntityFireworks");
			Class<?> craftFireworkClass = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
			Object firework = craftFireworkClass.cast(f);
			Method handle = firework.getClass().getMethod("getHandle");
			Object entityFirework = handle.invoke(firework);
			Field expectedLifespan = entityFireworkClass.getDeclaredField("expectedLifespan");
			Field ticksFlown = entityFireworkClass.getDeclaredField("ticksFlown");
			ticksFlown.setAccessible(true);
			ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
			ticksFlown.setAccessible(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException {
		String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
		String name = prefix + version + nmsClassString;
		Class<?> nmsClass = Class.forName(name);
		return nmsClass;
	}
}
