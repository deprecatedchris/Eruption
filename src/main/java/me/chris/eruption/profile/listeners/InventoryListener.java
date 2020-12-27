package me.chris.eruption.profile.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;

public class InventoryListener implements Listener {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null){
			return;
		}

		if (event.getWhoClicked() instanceof Player) {
			final Player player = (Player) event.getWhoClicked();

			if (event.getClickedInventory() != null && event.getClickedInventory() instanceof CraftingInventory) {
				if (player.getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
					return;
				}
			}


			if(event.getClickedInventory().getName().contains("'s Stats")){
				event.setCancelled(true);
			}

			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			if (!playerData.isInMatch() && player.getGameMode() == GameMode.SURVIVAL) {
				final Inventory clicked = event.getClickedInventory();

				if (playerData.isActive()) {
					if (clicked == null) {
						event.setCancelled(true);
						event.setCursor(null);
						player.updateInventory();
					} else if (clicked.equals(player.getOpenInventory().getTopInventory())) {
						if (event.getCursor().getType() != Material.AIR &&
								event.getCurrentItem().getType() == Material.AIR ||
								event.getCursor().getType() != Material.AIR &&
										event.getCurrentItem().getType() != Material.AIR) {
							event.setCancelled(true);
							event.setCursor(null);
							player.updateInventory();
						}
					}
				} else {
					if (clicked != null && clicked.equals(player.getInventory())) {
						event.setCancelled(true);
					}
				}
			}

			if (playerData.getPlayerState() == PlayerState.SPECTATING) {
				event.setCancelled(true);
				return;
			}

		}
	}



}
