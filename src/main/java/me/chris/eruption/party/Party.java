package me.chris.eruption.party;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.match.MatchTeam;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Party {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	private final UUID leader;
	private final Set<UUID> members = new HashSet<>();
	private int limit = 50;
	private boolean open;
	private boolean allInvite;

	public Party(UUID leader) {
		this.leader = leader;
		this.members.add(leader);
	}

	public void addMember(UUID uuid) {
		this.members.add(uuid);
	}

	public void removeMember(UUID uuid) {
		this.members.remove(uuid);
	}

	public void broadcast(String message) {
		this.members().forEach(member -> member.sendMessage(message));
	}

	public MatchTeam[] split() {
		List<UUID> teamA = new ArrayList<>();
		List<UUID> teamB = new ArrayList<>();

		for (UUID member : this.members) {
			if (teamA.size() == teamB.size()) {
				teamA.add(member);
			} else {
				teamB.add(member);
			}
		}

		return new MatchTeam[]{
				new MatchTeam(teamA.get(0), teamA, 0),
				new MatchTeam(teamB.get(0), teamB, 1)
		};
	}

	public Stream<Player> members() {
		return this.members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
	}

	public void messageAllMembers(String message){
		for(UUID uuid : members){
			Player player = Bukkit.getPlayer(uuid);

			if(player == null){
				return;
			}

			player.sendMessage(CC.translate(message));

		}
	}

}
