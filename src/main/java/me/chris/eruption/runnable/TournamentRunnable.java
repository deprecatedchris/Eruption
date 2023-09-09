package me.chris.eruption.runnable;

import io.netty.util.internal.ConcurrentSet;
import org.bukkit.ChatColor;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueType;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.tournament.TournamentState;
import me.chris.eruption.tournament.TournamentTeam;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class TournamentRunnable extends BukkitRunnable {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();
	private final Tournament tournament;

	@Override
	public void run() {
		if (this.tournament.getTournamentState() == TournamentState.STARTING) {
			int countdown = this.tournament.decrementCountdown();
			if (countdown == 0) {
				if (this.tournament.getCurrentRound() == 1) {
					Set<UUID> players = new ConcurrentSet<>();
					players.addAll(tournament.getPlayers());
					//Making Teams
					for (UUID player : players) {
						Party party = this.plugin.getPartyManager().getParty(player);

						if (party != null) {
							TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList(party.getMembers()));
							this.tournament.addAliveTeam(team);
							for (UUID member : party.getMembers()) {
								players.remove(member);
								tournament.setPlayerTeam(member, team);
							}
						}
					}

					List<UUID> currentTeam = null;

					for (UUID player : players) {
						if (currentTeam == null) {
							currentTeam = new ArrayList<>();
						}

						currentTeam.add(player);

						if (currentTeam.size() == this.tournament.getTeamSize()) {
							TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
							this.tournament.addAliveTeam(team);
							for (UUID teammate : team.getPlayers()) {
								tournament.setPlayerTeam(teammate, team);
							}
							currentTeam = null;
						}
					}
				}

				List<TournamentTeam> teams = this.tournament.getAliveTeams();

				Collections.shuffle(teams);

				for (int i = 0; i < teams.size(); i += 2) {
					TournamentTeam teamA = teams.get(i);

					if (teams.size() > i + 1) {
						TournamentTeam teamB = teams.get(i + 1);

						for (UUID playerUUID : teamA.getAlivePlayers()) {
							this.removeSpectator(playerUUID);
						}
						for (UUID playerUUID : teamB.getAlivePlayers()) {
							this.removeSpectator(playerUUID);
						}

						MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList<>(teamA.getAlivePlayers()), 0);
						MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList<>(teamB.getAlivePlayers()), 1);

						Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());

						Match match = new Match
								(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, matchTeamA, matchTeamB);

						Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
						Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

						match.broadcast(ChatColor.WHITE + "Starting tournament match. " + ChatColor.GREEN + "(" + leaderA.getName() + " vs " + leaderB.getName() + ")");

						this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                            plugin.getMatchManager().createMatch(match);
                            tournament.addMatch(match.getMatchId());
                            plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), tournament.getId());
                        });

					} else {
						for (UUID playerUUID : teamA.getAlivePlayers()) {
							Player player = this.plugin.getServer().getPlayer(playerUUID);

							player.sendMessage(ChatColor.WHITE + "You have been skipped to the next round.");
							player.sendMessage(ChatColor.WHITE + "There was no matching team for you.");
						}
					}
				}

				this.tournament.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
				this.tournament.broadcast(ChatColor.BLUE.toString() + ChatColor.BOLD + "[TOURNAMENT] (" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ") " + tournament.getKitName());
				this.tournament.broadcast(ChatColor.WHITE.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting Round #" + tournament.getCurrentRound());
				this.tournament.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");


				this.tournament.setTournamentState(TournamentState.FIGHTING);

			} else if (countdown <= 5) {
				String announce = ChatColor.BLUE + "(Tournament) " + ChatColor.GREEN + "Round #" + this.tournament.getCurrentRound() + " is starting in " + ChatColor.YELLOW +  countdown + ChatColor.GREEN + ".";
				this.tournament.broadcast(announce);
			}
		}
	}

	private void removeSpectator(UUID playerUUID) {
		Player player = this.plugin.getServer().getPlayer(playerUUID);

		if (player != null) {
			PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

			if (playerData.getPlayerState() == PlayerState.SPECTATING) {
				this.plugin.getMatchManager().removeSpectator(player);
			}
		}
	}
}
