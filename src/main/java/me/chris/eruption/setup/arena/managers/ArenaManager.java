package me.chris.eruption.setup.arena.managers;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.setup.arena.type.ArenaType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.setup.arena.StandaloneArena;
import me.chris.eruption.util.config.Config;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.util.other.ItemUtil;
import me.chris.eruption.util.inventory.InventoryUI;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	private final Config config = new Config("arenas", this.plugin);

	@Getter
	private final Map<String, Arena> arenas = new HashMap<>();

	@Getter
	private final Map<StandaloneArena, UUID> arenaMatchUUIDs = new HashMap<>();

	@Getter
	@Setter
	private int generatingArenaRunnables;

	public ArenaManager() {
		this.loadArenas();
	}

	private void loadArenas() {
		FileConfiguration fileConfig = config.getConfig();
		ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");

		if (arenaSection == null) {
			return;
		}

		arenaSection.getKeys(false).forEach(name -> {
			String a = arenaSection.getString(name + ".a");
			String b = arenaSection.getString(name + ".b");
			String min = arenaSection.getString(name + ".min");
			String max = arenaSection.getString(name + ".max");

			LocationUtil locA = LocationUtil.stringToLocation(a);
			LocationUtil locB = LocationUtil.stringToLocation(b);
			LocationUtil locMin = LocationUtil.stringToLocation(min);
			LocationUtil locMax = LocationUtil.stringToLocation(max);

			List<StandaloneArena> standaloneArenas = new ArrayList<>();

			ConfigurationSection saSection = arenaSection.getConfigurationSection(name + ".standaloneArenas");

			if (saSection != null) {
				saSection.getKeys(false).forEach(id -> {
					String saA = saSection.getString(id + ".a");
					String saB = saSection.getString(id + ".b");
					String saMin = saSection.getString(id + ".min");
					String saMax = saSection.getString(id + ".max");

					LocationUtil locSaA = LocationUtil.stringToLocation(saA);
					LocationUtil locSaB = LocationUtil.stringToLocation(saB);
					LocationUtil locSaMin = LocationUtil.stringToLocation(saMin);
					LocationUtil locSaMax = LocationUtil.stringToLocation(saMax);

					standaloneArenas.add(new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax));
				});
			}

			boolean enabled = arenaSection.getBoolean(name + ".enabled", false);
			boolean isEvent = arenaSection.getBoolean(name + ".event", false);
			ArenaType arenaType = ArenaType.valueOf(arenaSection.getString(name + ".type"));

			Arena arena = new Arena(name, standaloneArenas, new ArrayList<>(standaloneArenas), locA, locB, locMin, locMax, enabled, isEvent, arenaType);

			this.arenas.put(name, arena);
		});
	}

	public void reloadArenas() {
		this.saveArenas();
		this.arenas.clear();
		this.loadArenas();
	}

	public void saveArenas() {
		FileConfiguration fileConfig = this.config.getConfig();

		fileConfig.set("arenas", null);
		arenas.forEach((arenaName, arena) -> {
			String a = LocationUtil.locationToString(arena.getA());
			String b = LocationUtil.locationToString(arena.getB());
			String min = LocationUtil.locationToString(arena.getMin());
			String max = LocationUtil.locationToString(arena.getMax());

			String arenaRoot = "arenas." + arenaName;

			fileConfig.set(arenaRoot + ".a", a);
			fileConfig.set(arenaRoot + ".b", b);
			fileConfig.set(arenaRoot + ".min", min);
			fileConfig.set(arenaRoot + ".max", max);
			fileConfig.set(arenaRoot + ".enabled", arena.isEnabled());
			fileConfig.set(arenaRoot + ".standaloneArenas", null);
			fileConfig.set(arenaRoot + ".event", arena.isEvent());
			fileConfig.set(arenaRoot + ".type", arena.getArenaType().getNiceName());
			int i = 0;
			if (arena.getStandaloneArenas() != null) {
				for (StandaloneArena saArena : arena.getStandaloneArenas()) {
					String saA = LocationUtil.locationToString(saArena.getA());
					String saB = LocationUtil.locationToString(saArena.getB());
					String saMin = LocationUtil.locationToString(saArena.getMin());
					String saMax = LocationUtil.locationToString(saArena.getMax());

					String standAloneRoot = arenaRoot + ".standaloneArenas." + i;

					fileConfig.set(standAloneRoot + ".a", saA);
					fileConfig.set(standAloneRoot + ".b", saB);
					fileConfig.set(standAloneRoot + ".min", saMin);
					fileConfig.set(standAloneRoot + ".max", saMax);

					i++;
				}
			}
		});

		this.config.save();
	}

	public void openArenaSystemUI(Player player) {

		if(this.arenas.size() == 0) {
			player.sendMessage(ChatColor.RED + "There's no arenas.");
			return;
		}

		InventoryUI inventory = new InventoryUI("Arena System", true, 6);

		for(Arena arena : this.arenas.values()) {

			ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RED + arena.getName() + ChatColor.GRAY + " (" + (arena.isEnabled() ? ChatColor.GREEN.toString() + ChatColor.BOLD + "ENABLED" : ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED") + ChatColor.GRAY + ")");
			ItemUtil.reloreItem(item, ChatColor.GRAY + "Arenas: " + ChatColor.GREEN + (arena.getStandaloneArenas().size() == 0 ? "Single Arena (Invisible Players)" : arena.getStandaloneArenas().size() + " Arenas"), ChatColor.GRAY + "Standalone Arenas: " + ChatColor.GREEN + (arena.getAvailableArenas().size() == 0 ? "None" : arena.getAvailableArenas().size() + " Arenas Available"), "", ChatColor.RED.toString() + ChatColor.BOLD + "LEFT CLICK " + ChatColor.GRAY + "Teleport to Arena", ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT CLICK " + ChatColor.GRAY + "Generate Standalone Arenas");
			inventory.addItem(new InventoryUI.AbstractClickableItem(item) {
				@Override
				public void onClick(InventoryClickEvent event) {
					Player player = (Player) event.getWhoClicked();

					if(event.getClick() == ClickType.LEFT) {
						player.teleport(arena.getA().toBukkitLocation());
					} else {

						InventoryUI generateInventory = new InventoryUI("Generate Arenas", true, 1);

						int[] batches = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};

						for(int batch : batches) {
							ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RED.toString() + ChatColor.BOLD + batch + " ARENAS");
							generateInventory.addItem(new InventoryUI.AbstractClickableItem(item) {
								@Override
								public void onClick(InventoryClickEvent event) {
									Player player = (Player) event.getWhoClicked();
									player.performCommand("arena generate " + arena.getName() + " " + batch);
									player.sendMessage(ChatColor.GREEN + "Generating " + batch + " arenas, please check console for progress.");
									player.closeInventory();
								}
							});
						}

						player.openInventory(generateInventory.getCurrentPage());
					}
				}
			});
		}

		player.openInventory(inventory.getCurrentPage());
	}

	public void createArena(String name) {
		this.arenas.put(name, new Arena(name));
	}
	public void deleteArena(String name) {
		this.arenas.remove(name);
	}

	public Arena getArena(String name) {
		return this.arenas.get(name);
	}

	public Arena getRandomArena(Kit kit) {
		List<Arena> enabledArenas = new ArrayList<>();

		for (Arena arena : this.arenas.values()) {
			if (!arena.isEnabled()) {
				continue;
			}

			if (kit.getExcludedArenas().contains(arena.getName())) {
				continue;
			}

			if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
				continue;
			}

			enabledArenas.add(arena);
		}

		if (enabledArenas.size() == 0) {
			return null;
		}

		return enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
	}

	public void removeArenaMatchUUID(StandaloneArena arena) {
		this.arenaMatchUUIDs.remove(arena);
	}

	public UUID getArenaMatchUUID(StandaloneArena arena) {
		return this.arenaMatchUUIDs.get(arena);
	}

	public void setArenaMatchUUID(StandaloneArena arena, UUID matchUUID) {
		this.arenaMatchUUIDs.put(arena, matchUUID);
	}
}
