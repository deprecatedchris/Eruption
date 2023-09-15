package me.chris.eruption.scoreboard;

import io.github.thatkawaiisam.assemble.AssembleAdapter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.oitc.OITCPlayer;
import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.events.types.sumo.SumoPlayer;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchState;
import me.chris.eruption.match.MatchTeam;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.queue.QueueEntry;
import me.chris.eruption.queue.QueueType;
import me.chris.eruption.setting.SettingsInfo;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.PlayerUtil;
import me.chris.eruption.util.other.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class ScoreboardAdapter implements AssembleAdapter {
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Override
    public String getTitle(Player player) {
        return CC.translate("&c&lWay&6&lBack &7[Beta])");
    }
    @Override
    public List<String> getLines(Player player) {
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s profile data is null");
            return null;
        }

        if (!playerData.getSettings().isScoreboardToggled()) {
            return null;
        }

        switch (playerData.getPlayerState()) {
            case LOADING:
            case EDITING:
            case SPAWN:
            case EVENT:
            case SPECTATING:
                return this.getLobbyBoard(player, false);
            case QUEUE:
                return this.getLobbyBoard(player, true);
            case FIGHTING:
                return this.getGameBoard(player);
        }

        return null;
    }

    private List<String> getLobbyBoard(Player player, boolean queuing) {
        PlayerData profile = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        SettingsInfo settings = profile.getSettings();
        List<String> strings = new LinkedList<>();
        strings.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());


        if (playerData.getPlayerState() != PlayerState.EVENT) {
            strings.add(ChatColor.WHITE.toString() + "Online&7: " + ChatColor.RED + this.plugin.getServer().getOnlinePlayers().size());
            strings.add(ChatColor.WHITE.toString() + "In Fights&7: " + ChatColor.RED + this.plugin.getMatchManager().getFighters());
        }
        if (System.currentTimeMillis() < this.plugin.getEventManager().getCooldown()) {
            strings.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Cooldown&7: " + ChatColor.RED + TimeUtil.convertToFormat(this.plugin.getEventManager().getCooldown()));
        }


        if (queuing) {
            QueueEntry queueEntry = party == null ? this.plugin.getQueueManager().getQueueEntry(player.getUniqueId()) : this.plugin.getQueueManager().getQueueEntry(party.getLeader());
            strings.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------");
            strings.add(ChatColor.RED + "Queued for: ");
            strings.add(ChatColor.GRAY + queueEntry.getQueueType().getName() + " " + queueEntry.getKitName());

            if (queueEntry.getQueueType() != QueueType.UNRANKED) {

                long queueTime = System.currentTimeMillis() -
                        (party == null ? this.plugin.getQueueManager().getPlayerQueueTime(player
                                .getUniqueId())
                                : this.plugin.getQueueManager().getPlayerQueueTime(party.getLeader()));

                int eloRange = playerData.getEloRange();

                int seconds = Math.round(queueTime / 1000L);
                if (seconds > 5) {
                    if (eloRange != -1) {
                        eloRange += seconds * 50;
                        if (eloRange >= 3000) {
                            eloRange = 3000;
                        }
                    }
                }

                int elo = 1000;

                if (queueEntry.getQueueType() == QueueType.RANKED) {
                    elo = playerData.getElo(queueEntry.getKitName());
                }

                String eloRangeString = "" + Math.max(elo - eloRange / 2, 0) + " -> " + Math.max(elo + eloRange / 2, 0) + "";
                strings.add(ChatColor.WHITE + "Range&7: " + ChatColor.GOLD + eloRangeString);
            }
        }

        if (party != null) {
            strings.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------");
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + "Party&7: ");
            strings.add(ChatColor.WHITE + "* " + ChatColor.RED + this.plugin.getServer().getPlayer(party.getLeader()).getName());
            strings.add(ChatColor.WHITE + "Members&7: " + ChatColor.RED + party.getMembers().size());
        }

        if (event != null) {
            strings.add(ChatColor.RED + "Event " + ChatColor.GRAY + "(" + event.getName() + ")");

            if (event instanceof SumoEvent) {
                SumoEvent sumoEvent = (SumoEvent) event;

                int playingSumo = sumoEvent.getByState(SumoPlayer.SumoState.WAITING).size() + sumoEvent.getByState(SumoPlayer.SumoState.FIGHTING).size() + sumoEvent.getByState(SumoPlayer.SumoState.PREPARING).size();
                strings.add(ChatColor.WHITE + "Players&7: &c" + playingSumo + "/" + event.getLimit());


                int countdown = sumoEvent.getCountdownTask().getTimeUntilStart();

                if (countdown > 0 && countdown <= 60) {
                    strings.add(ChatColor.WHITE + "Starting&7: &c" + countdown + "s");
                }

                if (sumoEvent.getPlayer(player) != null) {
                    SumoPlayer sumoPlayer = sumoEvent.getPlayer(player);
                    strings.add(ChatColor.WHITE + "State&7: &c" + StringUtils.capitalize(sumoPlayer.getState().name().toLowerCase()));
                }


                if (sumoEvent.getFighting().size() > 0) {
                    StringJoiner joiner = new StringJoiner(" vs. ");

                    for (Player fighter : sumoEvent.getFighting()) {
                        joiner.add(fighter.getName());
                    }

                    strings.add("");
                    strings.add(ChatColor.RED + joiner.toString());
                }
            } else if (event instanceof OITCEvent) {
                OITCEvent oitcEvent = (OITCEvent) event;

                int playingOITC = oitcEvent.getPlayers().size();
                strings.add(ChatColor.RED.toString() + "» " + ChatColor.WHITE + "Players&7: &c" + ChatColor.GOLD + playingOITC + "/" + event.getLimit());


                int countdown = oitcEvent.getCountdownTask().getTimeUntilStart();

                if (countdown > 0 && countdown <= 60) {
                    strings.add(ChatColor.WHITE + "Starting&7: &c" + ChatColor.GRAY + countdown + "s");
                }

                if (oitcEvent.getPlayer(player) != null) {
                    OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);

                    if (oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() == OITCPlayer.OITCState.RESPAWNING) {

                        List<OITCPlayer> sortedList = oitcEvent.sortedScores();

                        strings.add(ChatColor.WHITE + "Kills&7: &c" + ChatColor.GRAY + oitcPlayer.getScore());
                        strings.add(ChatColor.WHITE + "Lives&7: &c" + ChatColor.GRAY + oitcPlayer.getLives());


                        if (sortedList.size() >= 2) {
                            strings.add("");
                            strings.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "TOP KILLS");

                            Player first = Bukkit.getPlayer(sortedList.get(0).getUuid());
                            Player second = Bukkit.getPlayer(sortedList.get(1).getUuid());

                            if (first != null) {
                                strings.add(ChatColor.BLUE + "#1 " + ChatColor.WHITE.toString() + first.getName() + "&7: &a" + sortedList.get(0).getScore());
                            }

                            if (second != null) {
                                strings.add(ChatColor.GOLD + "#2 " + ChatColor.WHITE.toString() + second.getName() + "&7: &a" + sortedList.get(1).getScore());
                            }

                            if (sortedList.size() >= 3) {
                                Player third = Bukkit.getPlayer(sortedList.get(2).getUuid());

                                if (third != null) {
                                    strings.add(ChatColor.YELLOW + "#3 " + ChatColor.WHITE.toString() + ChatColor.WHITE + third.getName() + "&7: &a" + sortedList.get(2).getScore());
                                }
                            }
                        }
                    }
                }
            }
        }

        if (playerData.getPlayerState() != PlayerState.EVENT && this.plugin.getTournamentManager().getTournaments().size() >= 1) {

            for (Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
                strings.add("");
                strings.add(ChatColor.GRAY.toString() + ChatColor.BOLD + "Tournament " + ChatColor.GRAY + "(" + tournament.getTeamSize() + "v" + tournament.getTeamSize() + ")");
                strings.add(ChatColor.WHITE + "Ladder&7: &c" + ChatColor.GRAY + tournament.getKitName());
                strings.add(ChatColor.WHITE + "Stage&7: &c" + "Round #" + ChatColor.GRAY + tournament.getCurrentRound());
                strings.add(ChatColor.WHITE + "Players&7: &c" + tournament.getPlayers().size() + "/" + tournament.getSize());

                int countdown = tournament.getCountdown();

                if (countdown > 0 && countdown <= 30) {
                    strings.add(ChatColor.WHITE + "Starting&7: &c" + ChatColor.GRAY + countdown + "s");
                }
            }

        }
        strings.add("");
        strings.add(ChatColor.GRAY.toString() + "www.wayback.dev");
        strings.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");

        return strings;
    }
    private List<String> getGameBoard(Player player) {
        PlayerData profile = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        SettingsInfo settings = profile.getSettings();
        List<String> strings = new LinkedList<>();
        strings.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");

        Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

        Player opponentPlayer = null;

        if (!match.isParty() && !match.isFFA()) {
            opponentPlayer = match.getTeams().get(0).getPlayers().get(0) == player.getUniqueId()
                    ? this.plugin.getServer().getPlayer(match.getTeams().get(1).getPlayers().get(0))
                    : this.plugin.getServer().getPlayer(match.getTeams().get(0).getPlayers().get(0));
            if (opponentPlayer == null) {
                return this.getLobbyBoard(player, false);
            }
            if (match.getMatchState() == MatchState.STARTING || match.getMatchState() == MatchState.FIGHTING) {
                strings.add(ChatColor.WHITE.toString() + "Opponent: " + ChatColor.RED + opponentPlayer.getName());
                if (settings.isAltScoreboard()) {
                    strings.add(ChatColor.WHITE.toString()  + "Duration: &c" + ChatColor.RED + match.getDuration());
                    strings.add("");
                    strings.add(ChatColor.WHITE.toString() + "Your Ping: " + ChatColor.GREEN + PlayerUtil.getPing(player) + " ms");
                    strings.add(ChatColor.WHITE.toString() + "Enemy's Ping: " + ChatColor.RED + PlayerUtil.getPing(opponentPlayer) + " ms");
                } else {
                    strings.add(ChatColor.WHITE.toString()  + "Duration: &c" + ChatColor.RED + match.getDuration());
                }
            } else {
                strings.add("&c&lMatch Ended");
            }
        } else if (match.isParty() && !match.isFFA()) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

            MatchTeam opposingTeam = match.isFFA() ? match.getTeams().get(0) :
                    (playerData.getTeamID() == 0 ? match.getTeams().get(1) : match.getTeams().get(0));
            MatchTeam playerTeam = match.getTeams().get(playerData.getTeamID());

            strings.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Your Team: " + ChatColor.GREEN + playerTeam.getAlivePlayers().size() + " alive");
            strings.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Opponent(s): " + ChatColor.RED + opposingTeam.getAlivePlayers().size() + " alive");
            strings.add(ChatColor.WHITE.toString() + "Time: &f" + ChatColor.RED + match.getDuration());

        } else if (match.isFFA()) {
            int alive = (match.getTeams().get(0).getAlivePlayers().size() - 1);
            strings.add(ChatColor.WHITE.toString() + ChatColor.BOLD + "Remaining: " + ChatColor.RED + (match.getTeams().get(0).getAlivePlayers().size() - 1) + " profile" + (alive == 1 ? "" : "s"));
            strings.add(ChatColor.WHITE.toString() + "Time: &f" + ChatColor.RED + match.getDuration());
        }

        strings.add("");
        strings.add(ChatColor.GRAY.toString() + "www.wayback.dev");
        strings.add(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");

        return strings;
    }
}
