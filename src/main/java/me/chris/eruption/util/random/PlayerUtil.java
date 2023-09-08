package me.chris.eruption.util.random;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

@UtilityClass
public class PlayerUtil {

	public void clearPlayer(Player player) {
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
		//todo: wtf
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
		player.updateInventory();
	}

	public void sendMessage(String message, Player... players) {
		for (Player player : players) {
			player.sendMessage(message);
		}
	}

	public void sendMessage(String message, Set<Player> players) {
		for (Player player : players) {
			player.sendMessage(message);
		}
	}

	public int getPing(Player player) {
		try {
			final Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
			return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
}
