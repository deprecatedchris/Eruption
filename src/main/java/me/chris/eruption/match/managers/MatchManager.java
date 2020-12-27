package me.chris.eruption.match.managers;

import lombok.Getter;
import me.chris.eruption.util.random.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.event.match.MatchEndEvent;
import me.chris.eruption.event.match.MatchStartEvent;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchRequest;
import me.chris.eruption.match.MatchState;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueType;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MatchManager {

	private final Map<UUID, Set<MatchRequest>> matchRequests = new TtlHashMap<>(TimeUnit.SECONDS, 30);
	private final Map<UUID, UUID> rematchUUIDs = new TtlHashMap<>(TimeUnit.SECONDS, 30);
	private final Map<UUID, Arena> rematchArena = new TtlHashMap<>(TimeUnit.SECONDS, 30);
	private final Map<UUID, Kit> rematchKit = new TtlHashMap<>(TimeUnit.SECONDS, 30);
	private final Map<UUID, UUID> rematchInventories = new TtlHashMap<>(TimeUnit.SECONDS, 30);
	private final Map<UUID, UUID> spectators = new ConcurrentHashMap<>();
	@Getter
	private final Map<UUID, Match> matches = new ConcurrentHashMap<>();

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	private static Field STATUS_PACKET_ID_FIELD;
	private static Field STATUS_PACKET_STATUS_FIELD;
	private static Field SPAWN_PACKET_ID_FIELD;

	public int getFighters() {
		int i = 0;
		for (Match match : this.matches.values()) {
			for (MatchTeam matchTeam : match.getTeams()) {
				i += matchTeam.getAlivePlayers().size();
			}
		}
		return i;
	}

	public List<ItemStack> getKitItems(Player player, Kit kit, Match match) {
		List<ItemStack> toReturn = new ArrayList<>();
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (!match.getKit().isSumo()) {
			toReturn.add(this.plugin.getHotbarManager().getDefaultBook());

			for (PlayerKit playerKit : playerData.getKits().get(kit.getName())) {
				if (playerKit != null) {
					final ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
					final ItemMeta itemMeta = itemStack.getItemMeta();

					itemMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + playerKit.getName());
					itemMeta.setLore(Arrays.asList(
							ChatColor.GRAY + "Right-click with this book in your",
							ChatColor.GRAY + "hand to receive this kit."
					));
					itemStack.setItemMeta(itemMeta);

					toReturn.remove(this.plugin.getHotbarManager().getDefaultBook());
					toReturn.add(itemStack);
					player.getInventory().setItem(8, this.plugin.getHotbarManager().getDefaultBook());
				}
			}
		}
		return toReturn;
	}

	public int getFighters(String ladder, QueueType type) {
		return (int) this.matches.entrySet().stream().filter(match -> match.getValue().getType() == type)
				.filter(match -> match.getValue().getKit().getName().equals(ladder)).count();
	}

	public void createMatchRequest(Player requester, Player requested, Arena arena, String kitName, boolean party) {
		MatchRequest request = new MatchRequest(requester.getUniqueId(), requested.getUniqueId(), arena, kitName, party);

		this.matchRequests.computeIfAbsent(requested.getUniqueId(), k -> new HashSet<>()).add(request);
	}

	public MatchRequest getMatchRequest(UUID requester, UUID requested) {
		Set<MatchRequest> requests = this.matchRequests.get(requested);

		if (requests == null) {
			return null;
		}

		return requests.stream().filter(req -> req.getRequester().equals(requester)).findAny().orElse(null);
	}

	public MatchRequest getMatchRequest(UUID requester, UUID requested, String kitName) {
		Set<MatchRequest> requests = this.matchRequests.get(requested);

		if (requests == null) {
			return null;
		}
		return requests.stream().filter(req -> req.getRequester().equals(requester) && req.getKitName().equals
				(kitName))
				.findAny()
				.orElse(null);
	}

	public Match getMatch(PlayerData playerData) {
		return this.matches.get(playerData.getCurrentMatchID());
	}

	public Match getMatch(UUID uuid) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(uuid);
		return this.getMatch(playerData);
	}


	public Match getMatchFromUUID(UUID uuid) {
		return this.matches.get(uuid);
	}

	public Match getSpectatingMatch(UUID uuid) {
		return this.matches.get(this.spectators.get(uuid));
	}

	public void removeMatchRequests(UUID uuid) {
		this.matchRequests.remove(uuid);
	}

	public void createMatch(Match match) {

		this.matches.put(match.getMatchId(), match);
		this.plugin.getServer().getPluginManager().callEvent(new MatchStartEvent(match));
	}

	public void removeFighter(Player player, PlayerData playerData, boolean spectateDeath) {
		Match match = this.matches.get(playerData.getCurrentMatchID());

		Player killer = player.getKiller();
		if (player.isOnline() && killer != null) {
			killer.hidePlayer(player);
		}


		MatchTeam entityTeam = match.getTeams().get(playerData.getTeamID());
		MatchTeam winningTeam = match.isFFA() ? entityTeam : match.getTeams().get(entityTeam.getTeamID() == 0 ? 1 : 0);

		if (match.getMatchState() == MatchState.ENDING) {
			return;
		}

		String deathMessage = ChatColor.RED + player.getName() + ChatColor.GRAY + " was eliminated" + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName());

		match.broadcast(deathMessage);

		if (match.isRedrover()) {
			if (match.getMatchState() != MatchState.SWITCHING) {
				/**Clickable inventories = new Clickable(ChatColor.RED + "Inventories: ");
				 if (killer != null) {
				 InventorySnapshot snapshot = new InventorySnapshot(killer, match);
				 this.plugin.getInventoryManager().addSnapshot(snapshot);
				 inventories.add(ChatColor.GREEN + killer.getName() + " ",
				 ChatColor.RED + "View Inventory",
				 "/inv " + snapshot.getSnapshotId());
				 }
				 InventorySnapshot snapshot = new InventorySnapshot(profile, match);
				 this.plugin.getInventoryManager().addSnapshot(snapshot);
				 inventories.add(ChatColor.RED + profile.getName() + " ",
				 ChatColor.RED + "View Inventory",
				 "/inv " + snapshot.getSnapshotId());
				 match.broadcast(inventories);**/
				match.setMatchState(MatchState.SWITCHING);
				match.setCountdown(4);
			}
		} else {
			match.addSnapshot(player);
		}

		entityTeam.killPlayer(player.getUniqueId());

		int remaining = entityTeam.getAlivePlayers().size();

		if (remaining != 0) {
			Set<Item> items = new HashSet<>();
			for (ItemStack item : player.getInventory().getContents()) {
				if (item != null && item.getType() != Material.AIR) {
					items.add(player.getWorld().dropItemNaturally(player.getLocation(), item, player));
				}
			}
			for (ItemStack item : player.getInventory().getArmorContents()) {
				if (item != null && item.getType() != Material.AIR) {
					items.add(player.getWorld().dropItemNaturally(player.getLocation(), item, player));
				}
			}
			this.plugin.getMatchManager().addDroppedItems(match, items);
		}

		if (spectateDeath) {
			this.addDeathSpectator(player, playerData, match);
		}

		if ((match.isFFA() && remaining == 1) || remaining == 0) {
			this.plugin.getServer().getPluginManager().callEvent(new MatchEndEvent(match, winningTeam, entityTeam));
		}


		List<Player> players = new ArrayList<>();
		if (!match.isParty() && !match.isFFA()) {
			players.add(Bukkit.getPlayer(match.getTeams().get(0).getPlayers().get(0)));
			players.add(Bukkit.getPlayer(match.getTeams().get(1).getPlayers().get(0)));
		}
	}


	public void removeMatch(Match match) {
		this.matches.remove(match.getMatchId());
	}

	public void giveKits(Player player, Kit kit) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Collection<PlayerKit> playerKits = playerData.getPlayerKits(kit.getName()).values();

		if (playerKits.size() == 0) {
			kit.applyToPlayer(player);
		} else {
			player.getInventory().setItem(8, this.plugin.getHotbarManager().getDefaultBook());
			int slot = -1;
			for (PlayerKit playerKit : playerKits) {
				player.getInventory().setItem(++slot,
						ItemUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.RED.toString() + ChatColor.BOLD + playerKit.getDisplayName()));
			}
			player.updateInventory();
		}
	}

	private void addDeathSpectator(Player player, PlayerData playerData, Match match) {
		this.spectators.put(player.getUniqueId(), match.getMatchId());

		playerData.setPlayerState(PlayerState.SPECTATING);

		PlayerUtil.clearPlayer(player);

		CraftPlayer playerCp = (CraftPlayer) player;
		EntityPlayer playerEp = playerCp.getHandle();

		playerEp.getDataWatcher().watch(6, 0.0F);
		playerEp.setInvisible(true);

		match.addSpectator(player.getUniqueId());

		match.addRunnable(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
			match.getTeams().forEach(team -> team.alivePlayers().forEach(member -> {
				member.hidePlayer(player);
			}));

			match.spectatorPlayers().forEach(member -> member.hidePlayer(player));

			player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
			player.setWalkSpeed(0.2F);
			player.setAllowFlight(true);
		}, 20L));


		if (match.isParty() || match.isFFA()) {
			this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
					player.getInventory().setContents(this.plugin.getHotbarManager().getPartySpecItems()), 1L);
		}
		player.updateInventory();
	}

	public void addRedroverSpectator(Player player, Match match) {
		this.spectators.put(player.getUniqueId(), match.getMatchId());

		player.setAllowFlight(true);
		player.setFlying(true);
		player.getInventory().setContents(this.plugin.getHotbarManager().getPartySpecItems());
		player.updateInventory();

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		playerData.setPlayerState(PlayerState.SPECTATING);
	}

	public void addSpectator(Player player, PlayerData playerData, Player target, Match targetMatch) {
		this.spectators.put(player.getUniqueId(), targetMatch.getMatchId());

		if (targetMatch.getMatchState() != MatchState.ENDING) {
			if (!targetMatch.haveSpectated(player.getUniqueId())) {

				String spectatorMessage = ChatColor.RED + player.getName() + ChatColor.YELLOW + " is spectating your match.";

				if(!player.hasPermission("practice.staff")) {
					targetMatch.broadcast(spectatorMessage);
				}
			}
		}

		targetMatch.addSpectator(player.getUniqueId());

		playerData.setPlayerState(PlayerState.SPECTATING);

		player.teleport(target);
		player.setAllowFlight(true);
		player.setFlying(true);

		player.getInventory().setContents(this.plugin.getHotbarManager().getSpecItems());
		player.updateInventory();

		this.plugin.getServer().getOnlinePlayers().forEach(online -> {
			online.hidePlayer(player);
			player.hidePlayer(online);
		});
		targetMatch.getTeams().forEach(team -> team.alivePlayers().forEach(player::showPlayer));
	}

	public void addDroppedItem(Match match, Item item) {
		match.addEntityToRemove(item);
		match.addRunnable(this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
			match.removeEntityToRemove(item);
			item.remove();
		}, 100L).getTaskId());
	}

	public void addDroppedItems(Match match, Set<Item> items) {
		for (Item item : items) {
			match.addEntityToRemove(item);
		}
		match.addRunnable(this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
			for (Item item : items) {
				match.removeEntityToRemove(item);
				item.remove();
			}
		}, 100L).getTaskId());
	}

	public void removeSpectator(Player player) {
		Match match = this.matches.get(this.spectators.get(player.getUniqueId()));

		match.removeSpectator(player.getUniqueId());

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		if (match.getTeams().size() > playerData.getTeamID() && playerData.getTeamID() >= 0) {
			MatchTeam entityTeam = match.getTeams().get(playerData.getTeamID());
			//Kill the profile if they are in a redrover.
			if (entityTeam != null) {
				entityTeam.killPlayer(player.getUniqueId());
			}
		}

		if (match.getMatchState() != MatchState.ENDING) {
			if (!match.haveSpectated(player.getUniqueId())) {
				match.addHaveSpectated(player.getUniqueId());
			}
		}

		this.spectators.remove(player.getUniqueId());
		this.plugin.getPlayerManager().sendToSpawnAndReset(player);
	}

	public void pickPlayer(Match match) {
		Player playerA = this.plugin.getServer().getPlayer(match.getTeams().get(0).getAlivePlayers().get(0));
		PlayerData playerDataA = this.plugin.getPlayerManager().getPlayerData(playerA.getUniqueId());
		if (playerDataA.getPlayerState() != PlayerState.FIGHTING) {
			playerA.teleport(match.getArena().getA().toBukkitLocation());
			PlayerUtil.clearPlayer(playerA);
			if (match.getKit().isCombo()) {
				playerA.setMaximumNoDamageTicks(3);
			}
			for (ItemStack itemStack : this.plugin.getMatchManager().getKitItems(playerA, match.getKit(), match)) {
				playerA.getInventory().addItem(itemStack);
			}
			this.plugin.getMatchManager().giveKits(playerA, match.getKit());
			playerDataA.setPlayerState(PlayerState.FIGHTING);
		}
		Player playerB = this.plugin.getServer().getPlayer(match.getTeams().get(1).getAlivePlayers().get(0));
		PlayerData playerDataB = this.plugin.getPlayerManager().getPlayerData(playerB.getUniqueId());

		if (playerDataB.getPlayerState() != PlayerState.FIGHTING) {
			playerB.teleport(match.getArena().getB().toBukkitLocation());
			PlayerUtil.clearPlayer(playerB);
			if (match.getKit().isCombo()) {
				playerB.setMaximumNoDamageTicks(3);
			}
			for (ItemStack itemStack : this.plugin.getMatchManager().getKitItems(playerB, match.getKit(), match)) {
				playerB.getInventory().addItem(itemStack);

			}
			this.plugin.getMatchManager().giveKits(playerB, match.getKit());
			playerDataB.setPlayerState(PlayerState.FIGHTING);
		}

		for (MatchTeam team : match.getTeams()) {
			for (UUID uuid : team.getAlivePlayers()) {
				Player player = this.plugin.getServer().getPlayer(uuid);

				if (player != null) {
					if (!playerA.equals(player) && !playerB.equals(player)) {
						playerA.hidePlayer(player);
						playerB.hidePlayer(player);
					}
				}
			}
		}
		playerA.showPlayer(playerB);
		playerB.showPlayer(playerA);

		match.broadcast(Style.translate("&c&lOpponent Found!"));
		match.broadcast("");
		match.broadcast(ChatColor.GRAY + "* " + ChatColor.RED + "Opponent: " + ChatColor.GREEN + playerB.getName());
		match.broadcast(ChatColor.GRAY + "* " + ChatColor.RED + "Kit: " + ChatColor.RED + match.getKit().getName());
		match.broadcast(ChatColor.GRAY + "* " + ChatColor.RED + "Map: " + ChatColor.WHITE + match.getArena().getName());

	}
	public UUID getRematcher(UUID uuid) {
		return this.rematchUUIDs.get(uuid);
	}

	public UUID getRematcherInventory(UUID uuid) {
		return this.rematchInventories.get(uuid);
	}

	public boolean isRematching(UUID uuid) {
		return this.rematchUUIDs.containsKey(uuid);
	}
}

