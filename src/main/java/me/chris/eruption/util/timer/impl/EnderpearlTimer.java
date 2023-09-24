package me.chris.eruption.util.timer.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketCooldown;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.timer.PlayerTimer;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EnderpearlTimer extends PlayerTimer implements Listener {

	public EnderpearlTimer() {
		super("Enderpearl", TimeUnit.SECONDS.toMillis(15));
	}

	@Override
	protected void handleExpiry(Player player, UUID playerUUID) {
		super.handleExpiry(player, playerUUID);

		if (player == null) {
			return;
		}

		player.sendMessage(ChatColor.RED + "You can now use enderpearls again.");

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) || !event.hasItem()) {
			return;
		}
		Player player = event.getPlayer();

		if (event.getItem().getType() == Material.ENDER_PEARL) {
			long cooldown = this.getRemaining(player);
			if (cooldown > 0) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You must wait " + DurationFormatUtils.formatDurationWords(cooldown, true, true) + " for use enderpearls again.");
				player.updateInventory();
			}
		}
	}

	@EventHandler
	public void onPearlLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			if (event.getEntity() instanceof EnderPearl) {
				Player player = (Player) event.getEntity().getShooter();


				if(EruptionPlugin.getInstance().getServer().getPluginManager().isPluginEnabled("LunarClient-API")){
					LunarClientAPI.getInstance().sendPacket(player, new LCPacketCooldown("Enderpearl", TimeUnit.SECONDS.toMillis(15L), Material.ENDER_PEARL.getId()));
				}

				this.setCooldown(player, player.getUniqueId());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		}
		Player player = event.getPlayer();

		if (this.getRemaining(player) != 0) {
			// Was the commands cancelled?
			if (event.isCancelled()) {
				this.clearCooldown(player);
			}
		}
		event.getTo().setX((double) event.getTo().getBlockX() + 0.5D);
		event.getTo().setZ((double) event.getTo().getBlockZ() + 0.5D);
		if (event.getTo().getBlock().getType() != Material.AIR) {
			event.getTo().setY(event.getTo().getY() - (event.getTo().getY() - event.getTo().getBlockY()));
		}
	}
}
