package me.chris.eruption.profile.commands.duel;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchRequest;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueType;
import me.chris.eruption.util.random.StringUtil;
import org.bukkit.ChatColor;
import me.chris.eruption.party.managers.PartyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends Command {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public AcceptCommand() {
		super("accept");
		this.setDescription("Accept a profile's duel.");
		this.setUsage(ChatColor.RED + "Usage: /accept <profile>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}
		PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "Unable to accept a duel within your duel.");
			return true;
		}
		Player target = this.plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
			return true;
		}
		if (player.getName().equals(target.getName())) {
			player.sendMessage(ChatColor.RED + "You can't duel yourself.");
			return true;
		}
		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());

		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "That profile is currently busy.");
			return true;
		}
		MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());

		if (args.length > 1) {
			Kit kit = this.plugin.getKitManager().getKit(args[1]);

			if (kit != null) {
				request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
			}
		}
		if (request == null) {
			player.sendMessage(ChatColor.RED + "You do not have any pending requests.");
			return true;
		}
		if (request.getRequester().equals(target.getUniqueId())) {
			List<UUID> playersA = new ArrayList<>();
			List<UUID> playersB = new ArrayList<>();

			PartyManager partyManager = this.plugin.getPartyManager();

			Party party = partyManager.getParty(player.getUniqueId());
			Party targetParty = partyManager.getParty(target.getUniqueId());

			if (request.isParty()) {
				if (party != null && targetParty != null && partyManager.isLeader(target.getUniqueId()) &&
						partyManager.isLeader(target.getUniqueId())) {
					playersA.addAll(party.getMembers());
					playersB.addAll(targetParty.getMembers());

				} else {
					player.sendMessage(ChatColor.RED + "That profile is not a party leader.");
					return true;
				}
			} else {
				if (party == null && targetParty == null) {
					playersA.add(player.getUniqueId());
					playersB.add(target.getUniqueId());
				} else {
					player.sendMessage(ChatColor.RED + "That profile is already in a party.");
					return true;
				}
			}

			Kit kit = this.plugin.getKitManager().getKit(request.getKitName());

			MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, 0);
			MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, 1);

			Match match = new Match(request.getArena(), kit, QueueType.UNRANKED, teamA, teamB);

			Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
			Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

			match.broadcast(ChatColor.WHITE + "Starting duel match. " + ChatColor.GREEN + "(" + leaderA.getName() + " vs " + leaderB.getName() + ")");
			this.plugin.getMatchManager().createMatch(match);
		}

		return true;
	}
}
