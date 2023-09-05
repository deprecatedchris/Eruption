package me.chris.eruption.queue.managers;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.util.random.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueEntry;
import me.chris.eruption.queue.QueueType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {

	private final Map<UUID, QueueEntry> queued = new ConcurrentHashMap<>();
	private final Map<UUID, Long> playerQueueTime = new HashMap<>();

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Getter
	@Setter
	private boolean rankedEnabled = true;

	public QueueManager() {
		this.plugin.getServer().getScheduler().runTaskTimer(this.plugin,
				() -> this.queued.forEach((key, value) -> {
					if (value.isParty()) {
						this.findMatch(this.plugin.getPartyManager().getParty(key), value.getKitName(),
								value.getElo(), value.getQueueType());
					} else {
						this.findMatch(this.plugin.getServer().getPlayer(key), value.getKitName(),
								value.getElo(), value.getQueueType());
					}
				}), 20L, 20L);
	}

	public void addPlayerToQueue(Player player, PlayerData playerData, String kitName, QueueType type) {
		if (type != QueueType.UNRANKED && !this.rankedEnabled) {
			player.closeInventory();
			return;
		}

		playerData.setPlayerState(PlayerState.QUEUE);

		int elo = type == QueueType.RANKED ? playerData.getElo(kitName) : 0;

		final QueueEntry entry = new QueueEntry(type, kitName, elo, PlayerUtil.getPing(player), false);

		this.queued.put(playerData.getUniqueId(), entry);

		this.giveQueueItems(player);

		String unrankedMessage = ChatColor.GRAY + "You have been added to the " + ChatColor.RED + "Unranked " + kitName + ChatColor.GRAY + " queue.";
		String rankedMessage = ChatColor.GRAY + "You have been added to the " + ChatColor.RED + "Ranked " + kitName + ChatColor.GRAY + " queue. " + ChatColor.RED + "[" + elo + "]";


		if (type == QueueType.RANKED) {
			player.sendMessage(rankedMessage);
		} else if (type == QueueType.UNRANKED) {
			player.sendMessage(unrankedMessage);
		}
		this.playerQueueTime.put(player.getUniqueId(), System.currentTimeMillis());
	}

	private void giveQueueItems(Player player) {
		player.closeInventory();
		player.getInventory().setContents(this.plugin.getHotbarManager().getQueueItems());
		player.updateInventory();
	}

	public QueueEntry getQueueEntry(UUID uuid) {
		return this.queued.get(uuid);
	}

	public long getPlayerQueueTime(UUID uuid) {
		return this.playerQueueTime.get(uuid);
	}

	public int getQueueSize(String ladder, QueueType type) {
		return (int) this.queued.entrySet().stream().filter(entry -> entry.getValue().getQueueType() == type)
				.filter(entry -> entry.getValue().getKitName().equals(ladder)).count();
	}

	private boolean findMatch(Player player, String kitName, int elo, QueueType type) {
		long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(player.getUniqueId());

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData == null) {
			this.plugin.getLogger().warning(player.getName() + "'s profile data is null");
			return false;
		}

		final QueueEntry queueEntry = this.queued.get(player.getUniqueId());

		int eloRange = playerData.getEloRange();
		int pingRange = playerData.getPingRange();
		int seconds = Math.round((float) (queueTime / 1000L));

		if (seconds > 5) {
			if (seconds % 15 == 0) {
				pingRange += 20;

				if (pingRange > 70) {
					pingRange = 70;
				} else {
					int ping = queueEntry.getPing();
					player.sendMessage(ChatColor.YELLOW + "Expanding your ping range by " + ChatColor.RED + "20ms" + ChatColor.YELLOW + "..." + ChatColor.GRAY + "[" + Math.max(0, ping - pingRange) + "ms - " + (ping + pingRange) + "ms]");
				}

				playerData.setPingRange(pingRange);
			}
			if (type.isRanked() && seconds % 10 == 0) {
				eloRange += 10;

				if (eloRange >= 3000)
					eloRange = 3000;

				playerData.setEloRange(eloRange);
			}
		}


		for (UUID opponent : this.queued.keySet()) {
			if (opponent.equals(player.getUniqueId()))
				continue;
			QueueEntry opponentQueueEntry = this.queued.get(opponent);
			if (!opponentQueueEntry.getKitName().equals(kitName))
				continue;
			if (opponentQueueEntry.getQueueType() != type)
				continue;
			if (opponentQueueEntry.isParty())
				continue;
			Player opponentPlayer = this.plugin.getServer().getPlayer(opponent);
			PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);
			if (opponentData.getPlayerState() != PlayerState.QUEUE)
				continue;
			if (type.isRanked()) {
				int eloDiff = Math.abs(opponentQueueEntry.getElo() - elo);
				if (eloDiff > eloRange)
					continue;
				if (eloDiff > opponentData.getEloRange())
					continue;
				int pingDiff = Math.abs(opponentQueueEntry.getPing() - queueEntry.getPing());

				if (playerData.getPingRange() != -1 && pingDiff > playerData.getPingRange())
					continue;
			}

			Kit kit = this.plugin.getKitManager().getKit(kitName);

			Arena arena = this.plugin.getArenaManager().getRandomArena(kit);


			if (type.isRanked()) {

				player.sendMessage("  ");
				player.sendMessage(ChatColor.RED + "§lMatch found! §f");
				player.sendMessage(ChatColor.GRAY + "");
				player.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Map: " + ChatColor.WHITE + arena.getName());
				player.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Opponent: " + ChatColor.WHITE + opponentPlayer.getName() + ChatColor.BLUE + " (" + this.queued.get(opponentPlayer.getUniqueId()).getElo() + " elo)");
				player.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Ping:" + ChatColor.WHITE + PlayerUtil.getPing(player));
				player.sendMessage("  ");

				opponentPlayer.sendMessage("  ");
				opponentPlayer.sendMessage(ChatColor.RED + "§lMatch found! §f");
				opponentPlayer.sendMessage(ChatColor.GRAY + "");
				opponentPlayer.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Map: " + ChatColor.WHITE + arena.getName());
				opponentPlayer.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Opponent: " + ChatColor.WHITE + player.getName() + ChatColor.BLUE + " (" + this.queued.get(player.getUniqueId()).getElo() + " elo)");
				opponentPlayer.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Ping:" + ChatColor.WHITE + PlayerUtil.getPing(player));
				opponentPlayer.sendMessage("  ");

			}

			player.sendMessage("  ");
			player.sendMessage(ChatColor.RED + "§lMatch found! §f");
			player.sendMessage(ChatColor.GRAY + "");
			player.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Map: " + ChatColor.WHITE + arena.getName());
			player.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Opponent: " + ChatColor.WHITE + opponentPlayer.getName());
			player.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Ping: " + ChatColor.WHITE + PlayerUtil.getPing(player));
			player.sendMessage("  ");

			opponentPlayer.sendMessage("  ");
			opponentPlayer.sendMessage(ChatColor.RED + "§lMatch found! §f");
			opponentPlayer.sendMessage(ChatColor.GRAY + "");
			opponentPlayer.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Map: " + ChatColor.WHITE + arena.getName());
			opponentPlayer.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Opponent: " + ChatColor.WHITE + player.getName());
			opponentPlayer.sendMessage(ChatColor.GRAY + "• " + ChatColor.RED + "Ping: " + ChatColor.WHITE + PlayerUtil.getPing(player));
			opponentPlayer.sendMessage("  ");

			MatchTeam teamA = new MatchTeam(player.getUniqueId(), Collections.singletonList(player.getUniqueId()), 0);
			MatchTeam teamB = new MatchTeam(opponentPlayer.getUniqueId(), Collections.singletonList(opponentPlayer.getUniqueId()), 1);

			Match match = new Match(arena, kit, type, teamA, teamB);

			this.plugin.getMatchManager().createMatch(match);

			this.queued.remove(player.getUniqueId());
			this.queued.remove(opponentPlayer.getUniqueId());

			this.playerQueueTime.remove(player.getUniqueId());

			return true;
		}

		return false;
	}

	public void removePlayerFromQueue(Player player) {
		QueueEntry entry = this.queued.get(player.getUniqueId());

		this.queued.remove(player.getUniqueId());

		this.plugin.getPlayerManager().Reset(player);

		player.sendMessage(ChatColor.RED + "You have left the " + entry.getQueueType().getName() + " " + entry.getKitName() + " queue.");
	}

	public void addPartyToQueue(Player leader, Party party, String kitName, QueueType type) {
		if (type.isRanked() && !this.rankedEnabled) {
			leader.closeInventory();
		} else if (party.getMembers().size() != 2) {
			leader.sendMessage(ChatColor.RED + "There must be at least 2 players in your party to do this.");
			leader.closeInventory();
		} else {
			party.getMembers().stream().map(this.plugin.getPlayerManager()::getPlayerData)
					.forEach(member -> member.setPlayerState(PlayerState.QUEUE));

			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(leader.getUniqueId());

			int elo = type.isRanked() ? playerData.getPartyElo(kitName) : -1;

			this.queued.put(playerData.getUniqueId(), new QueueEntry(type, kitName, elo, PlayerUtil.getPing(leader), true));

			this.giveQueueItems(leader);

			String unrankedMessage = ChatColor.GRAY + "Your party has been added to the " + ChatColor.RED + "Unranked 2v2 " + kitName + ChatColor.WHITE + " queue.";
			String rankedMessage = ChatColor.GRAY + "Your party has been added to the " + ChatColor.RED + "Ranked 2v2 " + kitName + ChatColor.WHITE + " queue with " + ChatColor.RED + elo + " elo" + ChatColor.WHITE + ".";

			party.broadcast(type.isRanked() ? rankedMessage : unrankedMessage);

			this.playerQueueTime.put(party.getLeader(), System.currentTimeMillis());

			this.findMatch(party, kitName, elo, type);
		}
	}

	private void findMatch(Party partyA, String kitName, int elo, QueueType type) {
		long queueTime = System.currentTimeMillis() - this.playerQueueTime.get(partyA.getLeader());

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(partyA.getLeader());

		int eloRange = playerData.getEloRange();
		final int seconds = Math.round(queueTime / 1000L);

		if (seconds > 5 && type.isRanked()) {
			eloRange += seconds * 50;

			if (eloRange >= 1000) {
				eloRange = 1000;
			}

			partyA.broadcast(ChatColor.YELLOW + "Searching in ELO range " + ChatColor.RED + "[" + (elo - eloRange / 2) + " -> " + (elo + eloRange / 2) + "]");
		}

		final int finalEloRange = eloRange;
		final UUID opponent = this.queued.entrySet().stream()
				.filter(entry -> entry.getKey() != partyA.getLeader() && this.plugin.getPlayerManager().getPlayerData(entry.getKey()).getPlayerState() == PlayerState.QUEUE)
				.filter(entry -> entry.getValue().isParty() && entry.getValue().getQueueType() == type)
				.filter(entry -> !type.isRanked() || Math.abs(entry.getValue().getElo() - elo) < finalEloRange && entry.getValue().getKitName().equals(kitName))
				.map(Map.Entry::getKey).findFirst().orElse(null);

		if (opponent == null) {
			return;
		}

		PlayerData opponentData = this.plugin.getPlayerManager().getPlayerData(opponent);


		if (opponentData.getPlayerState() == PlayerState.FIGHTING) {
			return;
		}

		if (playerData.getPlayerState() == PlayerState.FIGHTING) {
			return;
		}


		Player leaderA = this.plugin.getServer().getPlayer(partyA.getLeader());
		Player leaderB = this.plugin.getServer().getPlayer(opponent);

		Party partyB = this.plugin.getPartyManager().getParty(opponent);

		Kit kit = this.plugin.getKitManager().getKit(kitName);

		Arena arena = this.plugin.getArenaManager().getRandomArena(kit);

		String partyAFoundMatchMessage;
		String partyBFoundMatchMessage;


		if (type.isRanked()) {

			partyAFoundMatchMessage = ChatColor.GREEN + leaderB.getName() + "'s Party" + ChatColor.WHITE + " with " + ChatColor.GREEN + "" + this.queued.get(leaderB.getUniqueId()).getElo() + " elo";
			partyBFoundMatchMessage = ChatColor.GREEN + leaderA.getName() + "'s Party" + ChatColor.WHITE + " with " + ChatColor.GREEN + "" + this.queued.get(leaderA.getUniqueId()).getElo() + " elo";

		} else {
			partyAFoundMatchMessage = ChatColor.GREEN + leaderB.getName() + ChatColor.WHITE + "'s Party.";
			partyBFoundMatchMessage = ChatColor.GREEN + leaderA.getName() + ChatColor.WHITE + "'s Party.";

		}

		partyA.broadcast(ChatColor.RED + "§lMatch found! §b" + partyAFoundMatchMessage);
		partyB.broadcast(ChatColor.RED + "§lMatch found! §b" + partyBFoundMatchMessage);

		List<UUID> playersA = new ArrayList<>(partyA.getMembers());
		List<UUID> playersB = new ArrayList<>(partyB.getMembers());
		
		MatchTeam teamA = new MatchTeam(leaderA.getUniqueId(), playersA, 0);
		MatchTeam teamB = new MatchTeam(leaderB.getUniqueId(), playersB, 1);

		Match match = new Match(arena, kit, type, teamA, teamB);

		this.plugin.getMatchManager().createMatch(match);

		this.queued.remove(partyA.getLeader());
		this.queued.remove(partyB.getLeader());
	}

	public void removePartyFromQueue(Party party) {
		QueueEntry entry = this.queued.get(party.getLeader());

		this.queued.remove(party.getLeader());

		party.members().forEach(this.plugin.getPlayerManager()::Reset);

		String type = entry.getQueueType().isRanked() ? "Ranked" : "Unranked";

		party.broadcast(ChatColor.WHITE + "You party has left the " + type + " " + entry.getKitName() + " queue.");
	}
}
