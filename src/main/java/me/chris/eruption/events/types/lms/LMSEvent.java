package me.chris.eruption.events.types.lms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.random.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventCountdownTask;
import me.chris.eruption.events.PracticeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class LMSEvent extends PracticeEvent<LMSPlayer> {

    private final Map<UUID, LMSPlayer> players = new HashMap<>();
    private final LMSCountdownTask countdownTask = new LMSCountdownTask(this);
    private LMSEvent.LMSGameTask gameTask;

    public LMSEvent() {
        super("LMS");
    }

    @Override
    public Map<UUID, LMSPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<LocationUtil> getSpawnLocations() {
        return getPlugin().getSpawnManager().getLmsLocations();
    }

    @Override
    public void onStart() {
        gameTask = new LMSEvent.LMSGameTask();
        gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
    }

    public void cancelAll() {
        if (gameTask != null) {
            gameTask.cancel();
        }
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new LMSPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            LMSPlayer data = getPlayer(player);

            if (data.getState() != LMSPlayer.LMSState.FIGHTING) {
                return;
            }

            Player killer = player.getKiller();

            data.setState(LMSPlayer.LMSState.ELIMINATED);

            getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                getPlugin().getPlayerManager().sendToSpawnAndReset(player);
                if (getPlayers().size() >= 2) {
                    getPlugin().getPlayerManager().sendToSpawnAndReset(player);
                    getPlugin().getPlayerManager().giveLobbyItems(player);
                }
            });

            getPlayers().remove(player.getUniqueId());

            sendMessage(ChatColor.RED + player.getName() + ChatColor.YELLOW + " was eliminated" + (killer == null ? "." : " by " + ChatColor.RED + killer.getName()) + ChatColor.YELLOW + ".");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "You have been eliminated from the event. Better luck next time!");
            player.sendMessage(" ");
            PlayerData playerLMSData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            playerLMSData.setLmsLosses(playerLMSData.getLmsLosses() + 1);

            if (getByState(LMSPlayer.LMSState.FIGHTING).size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(LMSPlayer.LMSState.FIGHTING).get(0));
                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setLmsWins(winnerData.getLmsWins() + 1);
                }

                end();
                cancelAll();
            }
        };
    }

    private Player getRandomPlayer() {
        if (getByState(LMSPlayer.LMSState.FIGHTING).size() == 0) {
            return null;
        }

        List<UUID> fighting = getByState(LMSPlayer.LMSState.FIGHTING);

        Collections.shuffle(fighting);

        UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

        return getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(LMSPlayer.LMSState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(LMSPlayer::getUuid).collect(Collectors.toList());
    }

    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();

        return strings;
    }

    /**
     * To ensure that the fight does not go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class LMSGameTask extends BukkitRunnable {

        private int time = 303;

        @Override
        public void run() {
            if (time == 303) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Starting in 3 seconds.");
            } else if (time == 302) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Starting in 2 seconds.");
            } else if (time == 301) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Starting in 1 second.");
            } else if (time == 300) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.GREEN + "The game has started.");
                getPlayers().values().forEach(player -> player.setState(LMSPlayer.LMSState.FIGHTING));
                getBukkitPlayers().forEach(LMSEvent.this::handleInventory);
            } else if (time <= 0) {
                Player winner = getRandomPlayer();

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setLmsWins(winnerData.getLmsWins() + 1);
                }

                end();
                cancel();
                return;
            }

            if (getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(LMSPlayer.LMSState.FIGHTING).get(0));

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setLmsWins(winnerData.getLmsWins() + 1);
                }

                end();
                cancel();
                return;

            }
            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Game ends in " + time + " seconds" + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Game is ending in " + time + " seconds" + ".");
            }


            time--;
        }
    }

    private void handleInventory(Player player) {
        EruptionPlugin.getInstance().getKitManager().getKit("Soup").applyToPlayer(player);
    }
}