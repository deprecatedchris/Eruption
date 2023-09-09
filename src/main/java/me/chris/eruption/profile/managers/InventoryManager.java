package me.chris.eruption.profile.managers;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.match.menus.InventorySnapshot;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueType;
import me.chris.eruption.util.other.Clickable;
import me.chris.eruption.util.other.ItemUtil;
import me.chris.eruption.util.other.StringUtil;
import me.chris.eruption.util.inventory.InventoryUI;

import java.util.*;

public class InventoryManager {
	private static final String MORE_PLAYERS = ChatColor.RED + "There must be at least 2 players in your party to do this.";

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Getter
	private final InventoryUI unrankedInventory = new InventoryUI("Unranked Queue", true, 2);
	@Getter
	private final InventoryUI rankedInventory = new InventoryUI("Ranked Queue", true, 2);
	@Getter
	private final InventoryUI editorInventory = new InventoryUI("Kit Editor", true, 2);
	@Getter
	private final InventoryUI duelInventory = new InventoryUI("Send Request", true, 2);
	@Getter
	private final InventoryUI partySplitInventory = new InventoryUI("Split Fights", true, 2);
	@Getter
	private final InventoryUI partyFFAInventory = new InventoryUI("Party FFA", true, 2);
	@Getter
	private final InventoryUI partyEventInventory = new InventoryUI("Party Events", true, 1);
	@Getter
	private final InventoryUI partyInventory = new InventoryUI("Fight Other Parties", true, 6);

	private final Map<String, InventoryUI> duelMapInventories = new HashMap<>();
	private final Map<String, InventoryUI> partySplitMapInventories = new HashMap<>();
	private final Map<String, InventoryUI> partyFFAMapInventories = new HashMap<>();

	private final Map<UUID, InventoryUI> editorInventories = new HashMap<>();
	private final Map<UUID, InventorySnapshot> snapshots = new HashMap<>();

	public InventoryManager() {
		this.setupInventories();
		this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, this::updateInventories, 20L, 20L);
	}

	private void setupInventories() {
		Collection<Kit> kits= this.plugin.getKitManager().getKits();

		for ( Kit kit : kits ) {
			if (kit.isEnabled()) {
				this.unrankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
					@Override
					public void onClick(InventoryClickEvent event) {
						Player player=(Player) event.getWhoClicked();
						InventoryManager.this.addToQueue(player,
								InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
								kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
								QueueType.UNRANKED);
					}
				});
				if (kit.isRanked()) {
					this.rankedInventory.addItem(new InventoryUI.AbstractClickableItem(kit.getIcon()) {
						@Override
						public void onClick(InventoryClickEvent event) {
							Player player=(Player) event.getWhoClicked();
							InventoryManager.this.addToQueue(player,
									InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()),
									kit, InventoryManager.this.plugin.getPartyManager().getParty(player.getUniqueId()),
									QueueType.RANKED);
						}
					});
				}
			}
		}


		for (Kit kit : kits) {
			if (kit.isEnabled()) {
				//
				this.duelInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {

					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleDuelClick((Player) event.getWhoClicked(), kit);
					}
				});


				/*
				this.duelInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
					@Override
					public void onClick(InventoryClickEvent event) {
						Player player = (Player) event.getWhoClicked();

						if(kit.getKitEditContents()[0] == null) {
							player.sendMessage(ChatColor.RED + "This kit is not editable.");
							player.closeInventory();
							return;
						}

						InventoryManager.this.plugin.getEditorManager().addEditor(player, kit);
						InventoryManager.this.plugin.getPlayerManager().getPlayerData(player.getUniqueId())
								.setPlayerState(PlayerState.EDITING);
					}
				});



				 */
				this.partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handlePartySplitClick((Player) event.getWhoClicked(), kit);
					}
				});
				this.partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(ItemUtil.createItem(kit.getIcon().getType(), ChatColor.GREEN + kit.getName(), 1, kit.getIcon().getDurability())) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleFFAClick((Player) event.getWhoClicked(), kit);
					}
				});
			}
		}

		this.partyEventInventory.setItem(3, new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.QUARTZ_BLOCK, ChatColor.RED + "Split Fights")) {
			@Override
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				player.closeInventory();
				player.openInventory(InventoryManager.this.getPartySplitInventory().getCurrentPage());
			}
		});
		this.partyEventInventory.setItem(5, new InventoryUI.AbstractClickableItem(
				ItemUtil.createItem(Material.PAINTING, ChatColor.RED + "Party FFA")) {
			@Override
			public void onClick(InventoryClickEvent event) {
				Player player = (Player) event.getWhoClicked();
				player.closeInventory();
				player.openInventory(InventoryManager.this.getPartyFFAInventory().getCurrentPage());
			}
		});


		for (Kit kit : this.plugin.getKitManager().getKits()) {
			InventoryUI duelInventory = new InventoryUI("Select Arena", true, 6);
			InventoryUI partySplitInventory = new InventoryUI("Select Arena", true, 6);
			InventoryUI partyFFAInventory = new InventoryUI("Select Arena", true, 6);

			for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
				if (!arena.isEnabled()) {
					continue;
				}
				if (kit.getExcludedArenas().contains(arena.getName())) {
					continue;
				}
				if (kit.getArenaWhiteList().size() > 0 && !kit.getArenaWhiteList().contains(arena.getName())) {
					continue;
				}

				ItemStack book = ItemUtil.createItem(Material.PAPER, ChatColor.YELLOW + arena.getName());

				duelInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleDuelMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});
				partySplitInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handlePartySplitMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});
				partyFFAInventory.addItem(new InventoryUI.AbstractClickableItem(book) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handlePartyFFAMapClick((Player) event.getWhoClicked(), arena, kit);
					}
				});

			}
			this.duelMapInventories.put(kit.getName(), duelInventory);
			this.partySplitMapInventories.put(kit.getName(), partySplitInventory);
			this.partyFFAMapInventories.put(kit.getName(), partyFFAInventory);
		}

	}

	private void updateInventories() {
		for (int i = 0; i < 18; i++) {
			InventoryUI.ClickableItem unrankedItem = this.unrankedInventory.getItem(i);
			if (unrankedItem != null) {
				unrankedItem.setItemStack(this.updateQueueLore(unrankedItem.getItemStack(), QueueType.UNRANKED));
				this.unrankedInventory.setItem(i, unrankedItem);
			}

			InventoryUI.ClickableItem rankedItem = this.rankedInventory.getItem(i);
			if (rankedItem != null) {
				rankedItem.setItemStack(this.updateQueueLore(rankedItem.getItemStack(), QueueType.RANKED));
				this.rankedInventory.setItem(i, rankedItem);
			}
		}
	}

	private ItemStack updateQueueLore(ItemStack itemStack, QueueType type) {
		if (itemStack == null) {
			return null;
		}
		String ladder;

		if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
			ladder = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
		} else {
			return null;
		}
		int queueSize = this.plugin.getQueueManager().getQueueSize(ladder, type);
		int inGameSize = this.plugin.getMatchManager().getFighters(ladder, type);

		return ItemUtil.reloreItem(itemStack, "§7§m--------------------", ChatColor.WHITE + "In Game§8: " + ChatColor.RED + inGameSize,
				ChatColor.WHITE + "In Queue§8: " + ChatColor.RED + queueSize, "§7§m--------------------", ChatColor.GRAY + "Click to join queue.");
	}

	private void addToQueue(Player player, PlayerData playerData, Kit kit, Party party, QueueType queueType) {
		if (kit != null) {
			if (party == null) {
				this.plugin.getQueueManager().addPlayerToQueue(player, playerData, kit.getName(), queueType);
			} else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
				this.plugin.getQueueManager().addPartyToQueue(player, party, kit.getName(), queueType);
			}
		}
	}

	public void addSnapshot(InventorySnapshot snapshot) {
		this.snapshots.put(snapshot.getSnapshotId(), snapshot);

		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () ->
				InventoryManager.this.removeSnapshot(snapshot.getSnapshotId()), 20L * 30L);
	}

	public void removeSnapshot(UUID snapshotId) {
		InventorySnapshot snapshot = this.snapshots.get(snapshotId);
		if (snapshot != null) {
			this.snapshots.remove(snapshotId);
		}
	}

	public InventorySnapshot getSnapshot(UUID snapshotId) {
		return this.snapshots.get(snapshotId);
	}


	//FIGHT OTHER PARTIES

	public void addParty(Player player) {
		ItemStack skull = ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.RED + player.getName() + ChatColor.GRAY + " (1)");

		this.partyInventory.addItem(new InventoryUI.AbstractClickableItem(skull) {
			@Override
			public void onClick(InventoryClickEvent inventoryClickEvent) {
				player.closeInventory();

				if(inventoryClickEvent.getWhoClicked() instanceof Player) {
					Player sender = (Player) inventoryClickEvent.getWhoClicked();
					sender.performCommand("duel " + player.getName());
				}
			}
		});
	}

	public void updateParty(Party party) {
		Player player = this.plugin.getServer().getPlayer(party.getLeader());

		for(int i = 0; i < this.partyInventory.getSize(); i++) {
			InventoryUI.ClickableItem item = this.partyInventory.getItem(i);

			if(item != null) {
				ItemStack stack = item.getItemStack();

				if(stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
					List<String> lores = new ArrayList<>();

					party.members().forEach(member -> lores.add(ChatColor.GRAY + " - " + ChatColor.RED + member.getName()));

					ItemUtil.reloreItem(stack, lores.toArray(new String[0]));
					ItemUtil.renameItem(stack, ChatColor.RED + player.getName() + ChatColor.GRAY + " (" + party.getMembers().size() + ")");
					item.setItemStack(stack);
					break;
				}
			}
		}

		handleInventoryUpdate(partyInventory.getCurrentPage());
	}

	public void removeParty(Party party) {
		Player player = this.plugin.getServer().getPlayer(party.getLeader());

		for(int i = 0; i < this.partyInventory.getSize(); i++) {
			InventoryUI.ClickableItem item = this.partyInventory.getItem(i);

			if(item != null) {
				ItemStack stack = item.getItemStack();

				if(stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName().contains(player.getName())) {
					this.partyInventory.removeItem(i);
					break;
				}
			}
		}

		handleInventoryUpdate(partyInventory.getCurrentPage());
	}

	public void addEditingKitInventory(Player player, Kit kit) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		Map<Integer, PlayerKit> kitMap = playerData.getPlayerKits(kit.getName());

		InventoryUI inventory = new InventoryUI("Managing Kit Layout", true, 4);

		for (int i = 1; i <= 7; i++) {
			ItemStack save = ItemUtil
					.createItem(Material.CHEST, ChatColor.YELLOW + "Save Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
			ItemStack load = ItemUtil
					.createItem(Material.BOOK, ChatColor.YELLOW + "Load Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
			ItemStack rename = ItemUtil.createItem(Material.NAME_TAG,
					ChatColor.YELLOW + "Rename Kit " + ChatColor.GREEN + kit.getName() + " #" + i);
			ItemStack delete = ItemUtil
					.createItem(Material.FLINT, ChatColor.YELLOW + "Delete Kit " + ChatColor.GREEN + kit.getName() + " #" + i);

			inventory.setItem(i, new InventoryUI.AbstractClickableItem(save) {
				@Override
				public void onClick(InventoryClickEvent event) {
					int kitIndex = event.getSlot();
					InventoryManager.this.handleSavingKit(player, playerData, kit, kitMap, kitIndex);
					inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
						@Override
						public void onClick(InventoryClickEvent event) {
							InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
						}
					});
					inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
						@Override
						public void onClick(InventoryClickEvent event) {
							InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
						}
					});
					inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
						@Override
						public void onClick(InventoryClickEvent event) {
							InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
						}
					});
				}
			});

			final int kitIndex = i;

			if (kitMap != null && kitMap.containsKey(kitIndex)) {
				inventory.setItem(kitIndex + 1, 2, new InventoryUI.AbstractClickableItem(load) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleLoadKit(player, kitIndex, kitMap);
					}
				});
				inventory.setItem(kitIndex + 1, 3, new InventoryUI.AbstractClickableItem(rename) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleRenamingKit(player, kitIndex, kitMap);
					}
				});
				inventory.setItem(kitIndex + 1, 4, new InventoryUI.AbstractClickableItem(delete) {
					@Override
					public void onClick(InventoryClickEvent event) {
						InventoryManager.this.handleDeleteKit(player, kitIndex, kitMap, inventory);
					}
				});
			}
		}

		this.editorInventories.put(player.getUniqueId(), inventory);
	}

	public void removeEditingKitInventory(UUID uuid) {
		InventoryUI inventoryUI = this.editorInventories.get(uuid);
		if (inventoryUI != null) {
			//UIListener.INVENTORIES.remove(inventoryUI);
			this.editorInventories.remove(uuid);
		}
	}

	public InventoryUI getEditingKitInventory(UUID uuid) {
		return this.editorInventories.get(uuid);
	}

	private void handleSavingKit(Player player, PlayerData playerData, Kit kit, Map<Integer, PlayerKit> kitMap, int kitIndex) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			kitMap.get(kitIndex).setContents(player.getInventory().getContents().clone());
			player.sendMessage(
					ChatColor.WHITE + "Successfully saved kit #" + ChatColor.RED + kitIndex + ChatColor.WHITE + ".");
			return;
		}

		PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, player.getInventory().getContents().clone(),
				kit.getName() + " Kit " + kitIndex);
		playerData.addPlayerKit(kitIndex, playerKit);

		player.sendMessage(ChatColor.WHITE + "Successfully saved kit #" + ChatColor.RED + kitIndex + ChatColor.WHITE + ".");
	}

	private void handleLoadKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			ItemStack[] contents = kitMap.get(kitIndex).getContents();
			for (ItemStack itemStack : contents) {
				if (itemStack != null) {
					if (itemStack.getAmount() <= 0) {
						itemStack.setAmount(1);
					}
				}
			}
			player.getInventory().setContents(contents);
			player.updateInventory();
		}
	}

	private void handleRenamingKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			this.plugin.getEditorManager().addRenamingKit(player.getUniqueId(), kitMap.get(kitIndex));

			player.closeInventory();
			player.sendMessage(ChatColor.WHITE + "Enter the name you want this kit to be referred to as.");
		}
	}

	private void handleDeleteKit(Player player, int kitIndex, Map<Integer, PlayerKit> kitMap, InventoryUI inventory) {
		if (kitMap != null && kitMap.containsKey(kitIndex)) {
			this.plugin.getEditorManager().removeRenamingKit(player.getUniqueId());

			kitMap.remove(kitIndex);

			player.sendMessage(
					ChatColor.WHITE + "Successfully removed kit " + ChatColor.RED + kitIndex + ChatColor.WHITE + ".");

			inventory.setItem(kitIndex + 1, 2, null);
			inventory.setItem(kitIndex + 1, 3, null);
			inventory.setItem(kitIndex + 1, 4, null);
		}
	}

	private void handleDuelClick(Player player, Kit kit) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
		if (selected == null) {
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
			return;
		}

		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "That profile is currently busy.");
			return;
		}

		Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		boolean partyDuel = party != null;

		if (partyDuel) {
			if (targetParty == null) {
				player.sendMessage(ChatColor.RED + "That profile is not in a party.");
				return;
			}
		}

		if (player.hasPermission("practice.duel.selectmap")) {
			player.closeInventory();
			player.openInventory(this.duelMapInventories.get(kit.getName()).getCurrentPage());
			return;
		}

		if (this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) !=
				null) {
			player.sendMessage(ChatColor.RED + "You have already sent a duel request to this profile, please wait.");
			return;
		}

		Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
		if (arena == null) {
			player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
			return;
		}

		this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
	}

	private void handlePartySplitClick(Player player, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}
		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {

			if (player.hasPermission("practice.duel.selectmap")) {
				player.closeInventory();
				player.openInventory(this.partySplitMapInventories.get(kit.getName()).getCurrentPage());
				return;
			}

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
			if (arena == null) {
				player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
				return;
			}

			this.createPartySplitMatch(party, arena, kit);
		}
	}

	private void handleFFAClick(Player player, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || kit == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}
		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {

			if (player.hasPermission("practice.duel.selectmap")) {
				player.closeInventory();
				player.openInventory(this.partyFFAMapInventories.get(kit.getName()).getCurrentPage());
				return;
			}

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);
			if (arena == null) {
				player.sendMessage(ChatColor.RED + "There are no arenas available at this moment.");
				return;
			}

			this.createFFAMatch(party, arena, kit);
		}
	}


	private void handleDuelMapClick(Player player, Arena arena, Kit kit) {
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Player selected = this.plugin.getServer().getPlayer(playerData.getDuelSelecting());
		if (selected == null) {
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, playerData.getDuelSelecting()));
			return;
		}

		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(selected.getUniqueId());
		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "That player is currently busy.");
			return;
		}

		Party targetParty = this.plugin.getPartyManager().getParty(selected.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		boolean partyDuel = party != null;
		if (partyDuel && targetParty == null) {
			player.sendMessage(ChatColor.RED + "That player is not in a party.");
			return;
		}
		if (InventoryManager.this.plugin.getMatchManager().getMatchRequest(player.getUniqueId(), selected.getUniqueId()) != null) {
			player.sendMessage(ChatColor.RED + "You have already sent a duel request to this player, please wait.");
			return;
		}

		this.sendDuel(player, selected, kit, partyDuel, party, targetParty, arena);
	}


	private void handlePartyFFAMapClick(Player player, Arena arena, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}

		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			this.createFFAMatch(party, arena, kit);
		}
	}

	private void handlePartySplitMapClick(Player player, Arena arena, Kit kit) {
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party == null || !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			return;
		}

		player.closeInventory();
		if (party.getMembers().size() < 2) {
			player.sendMessage(InventoryManager.MORE_PLAYERS);
		} else {
			this.createPartySplitMatch(party, arena, kit);
		}
	}

	private void sendDuel(Player player, Player selected, Kit kit, boolean partyDuel, Party party, Party targetParty, Arena arena) {
		this.plugin.getMatchManager().createMatchRequest(player, selected, arena, kit.getName(), partyDuel);

		player.closeInventory();

		Clickable requestMessage = new Clickable(ChatColor.RED + player.getName() + ChatColor.GRAY + " has requested a " + (partyDuel ? "party" : "") + "duel request" + (kit.getName() != null ? " with kit " + ChatColor.RED + kit.getName() + ChatColor.GRAY : "") + (arena == null ? "" : " on arena " + ChatColor.RED + arena.getName()) + ChatColor.GRAY + ". " + ChatColor.GREEN + "[Click Here]", ChatColor.GREEN + "Click to accept the duel.", "/accept " + player.getName() + " " + kit.getName());

		if(partyDuel) {
			targetParty.members().forEach(requestMessage::sendToPlayer);
			party.broadcast(ChatColor.GRAY + "Successfully sent " + ChatColor.RED + selected.getName() + ChatColor.GRAY + " a " + ChatColor.RED + kit.getName() + ChatColor.GRAY + (arena == null ? "." : " on " + ChatColor.RED + arena.getName() + ChatColor.GRAY + "."));
		} else {
			requestMessage.sendToPlayer(selected);
			player.sendMessage(ChatColor.GRAY + "Successfully sent " + ChatColor.RED + selected.getName() + ChatColor.GRAY + " a " + ChatColor.RED + kit.getName() + ChatColor.GRAY + (arena == null ? "." : " on " + ChatColor.RED + arena.getName() + ChatColor.GRAY + "."));
		}
	}
	private void createPartySplitMatch(Party party, Arena arena, Kit kit) {
		MatchTeam[] teams = party.split();
		Match match = new Match(arena, kit, QueueType.UNRANKED, teams);
		Player leaderA = this.plugin.getServer().getPlayer(teams[0].getLeader());
		Player leaderB = this.plugin.getServer().getPlayer(teams[1].getLeader());

		match.broadcast(ChatColor.YELLOW + "§lMatch started (Split)! Kit: " + ChatColor.RED + kit.getName() + ".");

		this.plugin.getMatchManager().createMatch(match);
	}

	private void createFFAMatch(Party party, Arena arena, Kit kit) {
		MatchTeam team = new MatchTeam(party.getLeader(), Lists.newArrayList(party.getMembers()), 0);
		Match match = new Match(arena, kit, QueueType.UNRANKED, team);

		match.broadcast(ChatColor.YELLOW + "§lMatch started (FFA)! Kit: " + ChatColor.RED + kit.getName() + ".");

		this.plugin.getMatchManager().createMatch(match);
	}
	
	private void handleInventoryUpdate(Inventory inventory) {
		inventory.getViewers().stream().filter(entity -> entity instanceof Player).forEach(entity ->
				((Player) entity).updateInventory());
	}
}
