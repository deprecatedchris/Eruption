package me.chris.eruption.tournament.managers;

import me.chris.eruption.EruptionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.runnable.TournamentRunnable;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.tournament.TournamentState;
import me.chris.eruption.tournament.TournamentTeam;
import me.chris.eruption.util.random.TeamUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TournamentManager {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	private final Map<UUID, Integer> players = new HashMap<>();
	private final Map<UUID, Integer> matches = new HashMap<>();
	private final Map<Integer, Tournament> tournaments = new HashMap<>();

	public boolean isInTournament(UUID uuid) {
		return this.players.containsKey(uuid);
	}

	public Tournament getTournament(UUID uuid) {
		Integer id = this.players.get(uuid);

		if (id == null) {
			return null;
		}

		return this.tournaments.get(id);
	}

	public Tournament getTournamentFromMatch(UUID uuid) {
		Integer id = this.matches.get(uuid);

		if (id == null) {
			return null;
		}

		return this.tournaments.get(id);
	}

	public void createTournament(CommandSender commandSender, int id, int teamSize, int size, String kitName) {
		Tournament tournament = new Tournament(id, teamSize, size, kitName);

		this.tournaments.put(id, tournament);

		new TournamentRunnable(tournament).runTaskTimerAsynchronously(this.plugin, 20L, 20L);

		commandSender.sendMessage(ChatColor.GREEN + "Successfully created tournament.");

		if(commandSender instanceof Player) {
			Player player = (Player) commandSender;
			player.performCommand("tournament alert " + id);
		}
	}

	private void playerLeft(Tournament tournament, Player player) {
		TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());

		tournament.removePlayer(player.getUniqueId());

		player.sendMessage(ChatColor.RED.toString() + "[Tournament] " + ChatColor.RED + "You left the tournament.");

		this.players.remove(player.getUniqueId());
		this.plugin.getPlayerManager().sendToSpawnAndReset(player);

		tournament.broadcast(ChatColor.RED.toString() + "[Tournament] " + ChatColor.GREEN + "" + player.getName() + " left the tournament. (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")");


		if (team != null) {
			team.killPlayer(player.getUniqueId());

			if (team.getAlivePlayers().size() == 0) {
				tournament.killTeam(team);

				if (tournament.getAliveTeams().size() == 1) {
					TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);

					String names = TeamUtil.getNames(tournamentTeam);


					for (int i = 0; i <= 2; i++) {
						String announce = ChatColor.RED + "[Tournament] " + ChatColor.GREEN.toString() + "Winner: " + names + ".";
						PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
						winnerData.setTournamentWins(winnerData.getTournamentWins() + 1);
						Bukkit.broadcastMessage(announce);
					}

					for (UUID playerUUID : tournamentTeam.getAlivePlayers()) {
						this.players.remove(playerUUID);
						Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
						this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
					}

					}

					this.plugin.getTournamentManager().removeTournament(tournament.getId());
				}
			} else {
				if (team.getLeader().equals(player.getUniqueId())) {
					team.setLeader(team.getAlivePlayers().get(0));
				}
			}
		}


	private void teamEliminated(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
		for (UUID playerUUID : losingTeam.getAlivePlayers()) {
			Player player = this.plugin.getServer().getPlayer(playerUUID);

			tournament.removePlayer(player.getUniqueId());

			player.sendMessage(ChatColor.YELLOW.toString() +  "[Tournament] " + ChatColor.RED + "You have been eliminated. ");
			PlayerData losserData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
			losserData.setTournamentLosses(losserData.getTournamentLosses() + 1);

			this.players.remove(player.getUniqueId());
		}

		String word = losingTeam.getAlivePlayers().size() > 1 ? "have" : "has";
		boolean loserParty = losingTeam.getAlivePlayers().size() > 1;
		boolean winnerParty = winnerTeam.getAlivePlayers().size() > 1;

		String announce = ChatColor.YELLOW.toString() +  "[Tournament] " + ChatColor.RED + (loserParty ? losingTeam.getLeaderName() + "'s Party" : losingTeam.getLeaderName()) + ChatColor.YELLOW + " " + word + " been eliminated by " + ChatColor.GREEN + (winnerParty ? winnerTeam.getLeaderName() + "'s Party" : winnerTeam.getLeaderName()) + ".";
		String alive = ChatColor.YELLOW.toString() +  "[Tournament] " + ChatColor.GRAY + "Players: (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")";

		tournament.broadcast(announce);
		tournament.broadcast(alive);

	}

	public void leaveTournament(Player player) {
		Tournament tournament = this.getTournament(player.getUniqueId());

		if (tournament == null) {
			return;
		}

		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party != null && tournament.getTournamentState() != TournamentState.FIGHTING) {
			if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
				for (UUID memberUUID : party.getMembers()) {
					Player member = this.plugin.getServer().getPlayer(memberUUID);
					this.playerLeft(tournament, member);
				}
			} else {
				player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
			}
		} else {
			this.playerLeft(tournament, player);
		}
	}




	public void joinTournament(Integer id, Player player) {
		Tournament tournament = this.tournaments.get(id);

		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party != null) {
			if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
				if ((party.getMembers().size() + tournament.getPlayers().size()) <= tournament.getSize()) {
					if (party.getMembers().size() != tournament.getTeamSize()) {
						player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
					} else {

						if(tournament.getPlayers().contains(player.getUniqueId())) {
							player.sendMessage(ChatColor.RED + "You are already in the tournament!");
							return;
						}

						player.sendMessage(ChatColor.GREEN + "You have joined the tournament!");

						for (UUID memberUUID : party.getMembers()) {
							Player member = this.plugin.getServer().getPlayer(memberUUID);
							PlayerData data = this.plugin.getPlayerManager().getPlayerData(memberUUID);
							tournament.addPlayer(memberUUID);
						}

						if(tournament.getPlayers().size() == tournament.getSize()) {
							addTournamentMatch(UUID.randomUUID(), id);
							tournament.setTournamentState(TournamentState.STARTING);
						}

						}
					}
				} else {
					player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
				}
			} else {
				player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
			}
	}

	public Tournament getTournament(Integer id) {
		return this.tournaments.get(id);
	}

	public void removeTournament(Integer id) {
		Tournament tournament = this.tournaments.get(id);

		if (tournament == null) {
			return;
		}

		this.tournaments.remove(id);
	}

	public void addTournamentMatch(UUID matchId, Integer tournamentId) {
		this.matches.put(matchId, tournamentId);
	}

	public void removeTournamentMatch(Match match) {
		Tournament tournament = this.getTournamentFromMatch(match.getMatchId());

		if (tournament == null) {
			return;
		}

		tournament.removeMatch(match.getMatchId());

		this.matches.remove(match.getMatchId());

		MatchTeam losingTeam = match.getWinningTeamId() == 0 ? match.getTeams().get(1) : match.getTeams().get(0);

		TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getPlayers().get(0));

		tournament.killTeam(losingTournamentTeam);

		MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());

		TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAlivePlayers().get(0));

		this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);


		if (tournament.getMatches().size() == 0) {
			if (tournament.getAliveTeams().size() > 1) {
				tournament.setTournamentState(TournamentState.STARTING);
				tournament.setCurrentRound(tournament.getCurrentRound() + 1);
				tournament.setCountdown(16);
			} else {
				String names = TeamUtil.getNames(winningTournamentTeam);

				for (int i = 0; i <= 2; i++) {
					String announce = ChatColor.YELLOW + "[Tournament] " + ChatColor.GREEN.toString() + "Winner: " + names + ".";
					Bukkit.broadcastMessage(announce);
				}

				for (UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
					this.players.remove(playerUUID);
					Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
					this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
				}


					}
				}

				this.plugin.getTournamentManager().removeTournament(tournament.getId());
			}

	public Map<Integer, Tournament> getTournaments() {
		return tournaments;
	}

}


