package me.chris.eruption.party.managers;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.random.Style;
import me.chris.eruption.util.random.TtlHashMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PartyManager {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	private Map<UUID, List<UUID>> partyInvites = new TtlHashMap<>(TimeUnit.SECONDS, 15);
	private Map<UUID, Party> parties = new HashMap<>();
	private Map<UUID, UUID> partyLeaders = new HashMap<>();

	public boolean isLeader(UUID uuid) {
		return this.parties.containsKey(uuid);
	}

	public void removePartyInvites(UUID uuid) {
		this.partyInvites.remove(uuid);
	}

	public boolean hasPartyInvite(UUID player, UUID other) {
		return this.partyInvites.get(player) != null && this.partyInvites.get(player).contains(other);
	}

	public void createPartyInvite(UUID requester, UUID requested) {
		this.partyInvites.computeIfAbsent(requested, k -> new ArrayList<>()).add(requester);
	}

	public boolean isInParty(UUID player, Party party) {
		Party targetParty = this.getParty(player);
		return targetParty != null && targetParty.getLeader() == party.getLeader();
	}

	public Party getParty(UUID player) {
		if (this.parties.containsKey(player)) {
			return this.parties.get(player);
		}
		if (this.partyLeaders.containsKey(player)) {
			UUID leader = this.partyLeaders.get(player);
			return this.parties.get(leader);
		}
		return null;
	}

	public void createParty(Player player) {
		Party party = new Party(player.getUniqueId());


		this.parties.put(player.getUniqueId(), party);
		this.plugin.getInventoryManager().addParty(player);
		this.plugin.getPlayerManager().Reset(player);

		player.sendMessage(Style.translate("&7&m-----------------------------------"));
		player.sendMessage(Style.translate("&7You have successfully created a new &dParty&7."));
		player.sendMessage(Style.translate("&7To invite your friends do &c/party invite &7."));
		player.sendMessage(Style.translate(" "));
		player.sendMessage(Style.translate("&7Remember to tell them to join!"));
		player.sendMessage(Style.translate("&7&o/party for information on all commands, to use party chat do '&d@&7' <messsage>"));
		player.sendMessage(Style.translate("&7&m-----------------------------------"));	}

	private void disbandParty(Party party, boolean tournament) {
		this.plugin.getInventoryManager().removeParty(party);
		this.parties.remove(party.getLeader());

		party.broadcast(ChatColor.RED + "Your party has been disbanded.");

		party.members().forEach(member -> {
			PlayerData memberData = this.plugin.getPlayerManager().getPlayerData(member.getUniqueId());

			if (this.partyLeaders.get(memberData.getUniqueId()) != null) {
				this.partyLeaders.remove(memberData.getUniqueId());
			}
			if (memberData.getPlayerState() == PlayerState.SPAWN) {
				this.plugin.getPlayerManager().Reset(member);
			}
		});
	}

	public void leaveParty(Player player) {
		Party party = this.getParty(player.getUniqueId());

		if (party == null) {
			return;
		}

		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (this.parties.containsKey(player.getUniqueId())) {
			this.disbandParty(party, false);
		} else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
			this.disbandParty(party, true);
		} else {
			party.broadcast(ChatColor.RED.toString() + ChatColor.BOLD + "[-] " + ChatColor.RED + player.getName() + " left the party.");
			party.removeMember(player.getUniqueId());

			this.partyLeaders.remove(player.getUniqueId());

			this.plugin.getInventoryManager().updateParty(party);
		}

		switch (playerData.getPlayerState()) {
			case FIGHTING:
				this.plugin.getMatchManager().removeFighter(player, playerData, false);
				break;
			case SPECTATING:
				if(this.plugin.getEventManager().getSpectators().containsKey(player.getUniqueId())) {
					this.plugin.getEventManager().removeSpectator(player, this.plugin.getEventManager().getEventPlaying(player));
				} else {
					this.plugin.getMatchManager().removeSpectator(player);
				}
				break;
		}

		this.plugin.getPlayerManager().Reset(player);
	}

	public void joinParty(UUID leader, Player player) {
		Party party = this.getParty(leader);

		if (this.plugin.getTournamentManager().getTournament(leader) != null) {
			player.sendMessage(ChatColor.RED + "That player is in a tournament.");
			return;
		}

		this.partyLeaders.put(player.getUniqueId(), leader);
		party.addMember(player.getUniqueId());
		this.plugin.getInventoryManager().updateParty(party);

		this.plugin.getPlayerManager().Reset(player);

		party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "[+] " + ChatColor.RED + player.getName() + " joined the party.");
	}

}
