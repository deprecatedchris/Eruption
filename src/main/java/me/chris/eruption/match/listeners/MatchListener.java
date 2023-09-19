package me.chris.eruption.match.listeners;

import com.google.common.base.Joiner;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.match.menus.InventorySnapshot;
import me.chris.eruption.runnable.MatchRunnable;
import me.chris.eruption.util.CC;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.event.match.MatchEndEvent;
import me.chris.eruption.event.match.MatchStartEvent;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchState;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueType;
import me.chris.eruption.util.other.Clickable;
import me.chris.eruption.util.other.EloUtil;
import me.chris.eruption.util.other.PlayerUtil;
import org.bukkit.potion.PotionEffectType;


import java.util.*;
import java.util.stream.Collectors;


//Todo: Make this look like lounge and add Map rating.
public class MatchListener implements Listener {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		Match match = event.getMatch();
		Kit kit = match.getKit();

		if (!kit.isEnabled()) {
			match.broadcast(ChatColor.RED + "This kit is currently disabled.");
			this.plugin.getMatchManager().removeMatch(match);
			return;
		}

		if (kit.isBuild()|| kit.isSpleef()) {
			if (match.getArena().getAvailableArenas().isEmpty()){
				match.setStandaloneArena(match.getArena().getAvailableArena());
				this.plugin.getArenaManager().setArenaMatchUUID(match.getStandaloneArena(), match.getMatchId());
			} else {
				match.broadcast(ChatColor.RED + "There are no arenas available at this moment.");
				this.plugin.getMatchManager().removeMatch(match);
				return;
			}
		}

		Set<Player> matchPlayers = new HashSet<>();

		match.getTeams().forEach(team -> team.alivePlayers().forEach(player -> {
			matchPlayers.add(player);

			this.plugin.getMatchManager().removeMatchRequests(player.getUniqueId());

			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			player.setAllowFlight(false);
			player.setFlying(false);

			playerData.setCurrentMatchID(match.getMatchId());
			playerData.setTeamID(team.getTeamID());

			playerData.setMissedPots(0);
			playerData.setThrownPots(0);
			playerData.setLongestCombo(0);
			playerData.setCombo(0);
			playerData.setHits(0);


			PlayerUtil.clearPlayer(player);

			LocationUtil locationA = match.getStandaloneArena() != null ? match.getStandaloneArena().getA() : match.getArena().getA();
			LocationUtil locationB = match.getStandaloneArena() != null ? match.getStandaloneArena().getB() : match.getArena().getB();
			player.teleport(team.getTeamID() == 1 ? locationA.toBukkitLocation() : locationB.toBukkitLocation());

			if (kit.isCombo()) {
				player.setMaximumNoDamageTicks(2);
			}

			//todo: set kit items in this method
			if (kit.isBoxing()) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999, 1));

				player.sendMessage(CC.GREEN + "First to 100 hits wins the match.");
			}

			if (!match.isRedrover()) {
				for (ItemStack itemStack : this.plugin.getMatchManager().getKitItems(player, match.getKit(), match)) {
					player.getInventory().addItem(itemStack);
				}
				playerData.setPlayerState(PlayerState.FIGHTING);
			} else {
				this.plugin.getMatchManager().addRedroverSpectator(player, match);
			}
		}));

		for (Player player : matchPlayers) {
			for (Player online : this.plugin.getServer().getOnlinePlayers()) {
				online.hidePlayer(player);
				player.hidePlayer(online);
			}
		}

		for (Player player : matchPlayers) {
			for (Player other : matchPlayers) {
				player.showPlayer(other);
			}
		}

		List<Player> suitablePlayers = matchPlayers.stream()
				.filter(player -> plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getElo(kit.getName()) > 1200)
				.collect(Collectors.toList());

		if (match.getType() == QueueType.RANKED && suitablePlayers.size() > 1) {
			Clickable message = new Clickable(
					CC.translate("&6[&fHigh Elo&6] &fA high elo match has been started between &c" + suitablePlayers.get(0).getName() + " &fand &c" + suitablePlayers.get(1).getName()),
					CC.translate("&6Click to view."),
					"/spec " + suitablePlayers.get(0).getName());

			for (Player player : Bukkit.getOnlinePlayers()) {
				message.sendToPlayer(player);
			}
		}

		new MatchRunnable(match).runTaskTimer(this.plugin, 20L, 20L);
	}
	@EventHandler
	public void onMatchEnd(MatchEndEvent event) {
		Match match = event.getMatch();
		Clickable inventories = new Clickable(ChatColor.YELLOW + "Inventories (click to view): ");
		match.setMatchState(MatchState.ENDING);
		match.setWinningTeamId(event.getWinningTeam().getTeamID());
		match.setCountdown(4);
		List<UUID> spectatorUuids = new ArrayList<>(match.getSpectators());

		Player winnerPlayer = Bukkit.getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
		Player loserPlayer = Bukkit.getPlayer(event.getLosingTeam().getLeader());

		match.broadcast(" ");

		if (match.isFFA()) {
			Player winner = this.plugin.getServer().getPlayer(event.getWinningTeam().getAlivePlayers().get(0));
			String winnerMessage = ChatColor.RED + "Winner: " + ChatColor.GREEN + winner.getName();

			event.getWinningTeam().players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}
				inventories.add((player.getUniqueId() == winner.getUniqueId() ? ChatColor.GREEN : ChatColor.RED)
								+ player.getName() + " ",
						ChatColor.RED + "View Inventory",
						"/_ " + match.getSnapshot(player.getUniqueId()).getSnapshotId());
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				player.setSaturation(12.8F);
				player.setFireTicks(0);

			});
			for (InventorySnapshot snapshot : match.getSnapshots().values()) {
				this.plugin.getInventoryManager().addSnapshot(snapshot);
			}

			match.broadcast(winnerMessage);
			match.broadcast(inventories);
		} else if (match.isRedrover()) {
			match.broadcast(ChatColor.RED + event.getWinningTeam().getLeaderName() + ChatColor.WHITE + " has won the redrover party game!");
		} else {
			Map<UUID, InventorySnapshot> inventorySnapshotMap = new LinkedHashMap<>();
			match.getTeams().forEach(team -> team.players().forEach(player -> {
				if (!match.hasSnapshot(player.getUniqueId())) {
					match.addSnapshot(player);
				}

				inventorySnapshotMap
						.put(player.getUniqueId(), match.getSnapshot(player.getUniqueId()));

				boolean onWinningTeam =
						this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()).getTeamID() ==
								event.getWinningTeam().getTeamID();
				inventories.add((onWinningTeam ? ChatColor.GREEN : ChatColor.RED)
								+ player.getName() + " ",
						ChatColor.RED + "Click to view inventory",
						"/_ " + match.getSnapshot(player.getUniqueId()).getSnapshotId());

				player.setMaximumNoDamageTicks(19); // Double checking the damage ticks.

				// NameTag start
				// TODO: make sure too remove nametag after end
				MatchTeam otherTeam = team == match.getTeams().get(0) ? match.getTeams().get(1) : match.getTeams().get(0);
				// NameTag end
				// Pvplounge death animation
				player.getInventory().clear();
				player.getInventory().setArmorContents(null);
				player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
				player.setHealth(20.0D);
				player.setFoodLevel(20);
				player.setSaturation(12.8F);
				player.setFireTicks(0);

			}));
			for (InventorySnapshot snapshot : match.getSnapshots().values()) {
				this.plugin.getInventoryManager().addSnapshot(snapshot);
			}

			String winnerMessage = ChatColor.WHITE + (match.isParty() ? "Winning Team: " : "Winner: ")
					+ ChatColor.RED + event.getWinningTeam().getLeaderName();

			TextComponent first = new TextComponent();
			first.setText(CC.translate("&aWinner: &7"));
			TextComponent winner = new TextComponent();
			winner.setText(CC.translate("&a" + winnerPlayer.getName()));
			winner.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_ " + match.getSnapshot(winnerPlayer.getUniqueId()).getSnapshotId()));
			winner.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate("&fClick to see the &c" + winnerPlayer.getName() + "&f's inventory")).create()));
			TextComponent second = new TextComponent();
			second.setText(CC.translate(" &7- "));
			TextComponent third = new TextComponent();
			third.setText(CC.translate( "&cLoser: &7"));
			TextComponent loser = new TextComponent();
			loser.setText(CC.translate("&c" + loserPlayer.getName()));
			loser.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/_ " + match.getSnapshot(loserPlayer.getUniqueId()).getSnapshotId()));
			loser.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate("&fClick to see the &c" + loserPlayer.getName() + "&f's inventory")).create()));

			BaseComponent[] bases = {
					first,
					winner,
					second,
					third,
					loser
			};

			match.broadcast(ChatColor.RED + "Match Results " + ChatColor.GRAY + "(click name to view inventory)");
			match.broadcast(bases);

			if (match.getType().isRanked()) {
				String kitName = match.getKit().getName();

				Player winnerLeader = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(0));
				PlayerData winnerLeaderData = this.plugin.getPlayerManager()
						.getPlayerData(winnerLeader.getUniqueId());
				Player loserLeader = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(0));
				PlayerData loserLeaderData = this.plugin.getPlayerManager()
						.getPlayerData(loserLeader.getUniqueId());

				String eloMessage;

				int[] preElo = new int[2];
				int[] newElo = new int[2];
				int winnerElo;
				int loserElo;
				int newWinnerElo;
				int newLoserElo;

				if (event.getWinningTeam().getPlayers().size() == 2) {
					Player winnerMember = this.plugin.getServer().getPlayer(event.getWinningTeam().getPlayers().get(1));
					PlayerData winnerMemberData = this.plugin.getPlayerManager().getPlayerData(winnerMember.getUniqueId());

					Player loserMember = this.plugin.getServer().getPlayer(event.getLosingTeam().getPlayers().get(1));
					PlayerData loserMemberData = this.plugin.getPlayerManager().getPlayerData(loserMember.getUniqueId());

					winnerElo = winnerLeaderData.getPartyElo(kitName);
					loserElo = loserLeaderData.getPartyElo(kitName);

					preElo[0] = winnerElo;
					preElo[1] = loserElo;

					newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
					newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

					newElo[0] = newWinnerElo;
					newElo[1] = newLoserElo;

					winnerMemberData.setPartyElo(kitName, newWinnerElo);
					loserMemberData.setPartyElo(kitName, newLoserElo);

					eloMessage = ChatColor.BLUE + "Updated Elo: " + ChatColor.GREEN + winnerLeader.getName() + ", " +
							winnerMember.getName() + " " + newWinnerElo +
							" (+" + (newWinnerElo - winnerElo) + ") " + ChatColor.RED + loserLeader.getName() + "," +
							" " +
							loserMember.getName() + " " +
							newLoserElo + " (" + (newLoserElo - loserElo) + ")";
				} else {
					winnerElo = winnerLeaderData.getElo(kitName);
					loserElo = loserLeaderData.getElo(kitName);


					preElo[0] = winnerElo;
					preElo[1] = loserElo;

					newWinnerElo = EloUtil.getNewRating(winnerElo, loserElo, true);
					newLoserElo = EloUtil.getNewRating(loserElo, winnerElo, false);

					newElo[0] = newWinnerElo;
					newElo[1] = newLoserElo;

					eloMessage = ChatColor.BLUE + "Updated Elo: " + ChatColor.GREEN + winnerLeader.getName() + " " + newWinnerElo +
							" (+" + (newWinnerElo - winnerElo) + ") " +
							ChatColor.RED + loserLeader.getName() + " " + newLoserElo + " (" +
							(newLoserElo - loserElo) + ")";

					if (match.getType() == QueueType.RANKED) {
						winnerLeaderData.setElo(kitName, newWinnerElo);
						loserLeaderData.setElo(kitName, newLoserElo);

						winnerLeaderData.setWins(kitName, winnerLeaderData.getWins(kitName) + 1);
						loserLeaderData.setLosses(kitName, loserLeaderData.getLosses(kitName) + 1);
					} else {
						winnerLeaderData.setWins(kitName,winnerLeaderData.getWins(kitName) + 1);
						loserLeaderData.setLosses(kitName,loserLeaderData.getLosses(kitName) + 1);
					}


					match.broadcast(eloMessage);

					// Match data (only for 1v1's atm)
					if (event.getWinningTeam().getPlayers().size() == 1) {

						InventorySnapshot snapshotA = inventorySnapshotMap.get(event.getWinningTeam().getLeader());
						InventorySnapshot snapshotB = inventorySnapshotMap.get(event.getLosingTeam().getLeader());
					}
				}
			}
			if (spectatorUuids.size() > 2) {
				List<String> spectatorNames = new ArrayList<>();
				for (UUID uuid : spectatorUuids) {
					spectatorNames.add(Bukkit.getPlayer(uuid).getName());
					spectatorNames.remove(Bukkit.getPlayer(event.getLosingTeam().getLeader()).getName());
				}

				spectatorNames.sort(String::compareToIgnoreCase);

				String firstFourNames = Joiner.on(", ").join(
						spectatorNames.subList(
								0,
								Math.min(spectatorNames.size(), 4)
						)
				);

				if (spectatorNames.size() > 4) {
					firstFourNames += " (+" + (spectatorNames.size() - 4) + " more)";
				}

				String spectators = ChatColor.LIGHT_PURPLE + "Spectators (" + spectatorNames.size() + "): " + ChatColor.GRAY + firstFourNames;
				match.broadcast(spectators);
			}
			match.broadcast(" ");

			TextComponent rateMapBase = new TextComponent(CC.translate("&eClick the stars to rate the &6" + match.getArena().getName() + " &earena! "));

			TextComponent[] rateComponents = new TextComponent[6];
			String[] ratings = {"Terrible", "NotBest", "Okay", "Good", "Amazing"};
			String[] colors = {"c", "6", "e", "3", "a"};

			for (int i = 0; i < 5; i++) {
				rateComponents[i] = new TextComponent(CC.translate("&" + colors[i] + "&l[" + (i + 1) + "⭐]"));
				rateComponents[i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate("&eClick to rate &6" + match.getArena().getName() + " &eas &" + colors[i] + ratings[i] + "&e.")).create()));
				rateComponents[i].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ratethemap " + match.getArena().getName() + " " + ratings[i]));
			}

			BaseComponent[] rateBases = {
					rateMapBase,
					rateComponents[0],
					rateComponents[1],
					rateComponents[2],
					rateComponents[3],
					rateComponents[4]
			};
			match.broadcast(rateBases);
		}
	}
}
