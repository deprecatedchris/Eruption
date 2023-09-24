package me.chris.eruption.events.types.oitc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventCountdownTask;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.util.other.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OITCEvent extends PracticeEvent<OITCPlayer> {

    private final Map<UUID, OITCPlayer> players = new HashMap<>();

    private Arena arena = getEventArena();

    @Getter private OITCGameTask gameTask = null;
    private final OITCCountdownTask countdownTask = new OITCCountdownTask(this);
    private List<LocationUtil> respawnLocations;

    @Getter
    @Setter
    private boolean running = false;

    public OITCEvent() {
        super("OITC");
    }

    @Override
    public Map<UUID, OITCPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<LocationUtil> getSpawnLocations() {
        return Collections.singletonList(arena.getEventJoinLocation());
    }

    @Override
    public void onStart() {
        this.respawnLocations = new ArrayList<>();
        this.gameTask = new OITCGameTask();
        this.running = true;
        this.gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {

        return player -> {
            OITCPlayer data = getPlayer(player);

            if (data == null || data.getState() == OITCPlayer.OITCState.WAITING) {
                return;
            }

            if (data.getState() == OITCPlayer.OITCState.FIGHTING || data.getState() == OITCPlayer.OITCState.PREPARING || data.getState() == OITCPlayer.OITCState.RESPAWNING) {
                String deathMessage = ChatColor.RED + player.getName() + "&7[" + data.getScore() + "] " + ChatColor.YELLOW + "was eliminated from the game.";

                if (data.getLastKiller() != null) {
                    OITCPlayer killerData = data.getLastKiller();
                    Player killer = Bukkit.getPlayer(killerData.getUuid());

                    int count = killerData.getScore() + 1;
                    killerData.setScore(count);

                    if (!killer.getInventory().contains(Material.ARROW)) {
                        killer.getInventory().setItem(8, new ItemStack(Material.ARROW, 1));
                    } else {
                        killer.getInventory().getItem(8).setAmount(killer.getInventory().getItem(8).getAmount() + 1);
                    }

                    killer.updateInventory();
                    killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1F, 1F);

                    data.setLastKiller(null);

                    deathMessage = ChatColor.RED + player.getName() + "&7[" + data.getScore() + "] " + ChatColor.YELLOW + "was killed by " + ChatColor.RED + killer.getName() + "&7[" + count + "]";

                    if (count == 20) {
                        handleWin(killer);
                        gameTask.cancel();
                        end();
                    }
                }

                if (data.getLastKiller() == null) {
                    // Respawn the profile
                    BukkitTask respawnTask = new RespawnTask(player, data).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                    data.setRespawnTask(respawnTask);

                }

                sendMessage(deathMessage);
            }
        };
    }

    public void teleportNextLocation(Player player) {
        player.teleport(getGameLocations().remove(ThreadLocalRandom.current().nextInt(getGameLocations().size())).toBukkitLocation());
    }

    private List<LocationUtil> getGameLocations() {
        if (this.respawnLocations != null && this.respawnLocations.size() == 0) {
            this.respawnLocations.add(arena.getA());
            this.respawnLocations.add(arena.getB());
            this.respawnLocations.add(arena.getEventJoinLocation());  //TODO: add more locations
        }

        return this.respawnLocations;
    }

    private void giveRespawnItems(Player player) {
        Bukkit.getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
            player.getInventory().setItem(1, new ItemStack(Material.BOW));
            player.getInventory().setItem(8, new ItemStack(Material.ARROW));
            player.updateInventory();
        });
    }

    private Player getWinnerPlayer() {
        return getByState(OITCPlayer.OITCState.FIGHTING).size() == 0 ? null : Bukkit.getPlayer(sortedScores().get(0).getUuid());
    }

    private List<UUID> getByState(OITCPlayer.OITCState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(OITCPlayer::getUuid).collect(Collectors.toList());
    }

    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();

        return strings;
    }

    @Getter
    @RequiredArgsConstructor
    public class RespawnTask extends BukkitRunnable {

        private final Player player;
        private final OITCPlayer oitcPlayer;
        private int time = 3;

        @Override
        public void run() {
        	/*
            if (oitcPlayer.getLives() == 0) {
                cancel();
                return;
            }
            */

            if (!running) {
                cancel();
                return;
            }

            if (time > 0) {
                player.teleport(arena.getEventJoinLocation().toBukkitLocation());
                player.sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Respawning in " + ChatColor.YELLOW + time + " " + (time == 1 ? "second" : "seconds") + ChatColor.YELLOW + ".");
            }

            if (time == 3) {
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    PlayerUtil.clearPlayer(player);
                    getBukkitPlayers().forEach(member -> member.hidePlayer(player));
                    getBukkitPlayers().forEach(player::hidePlayer);
                    player.setGameMode(GameMode.CREATIVE);
                });

                oitcPlayer.setState(OITCPlayer.OITCState.RESPAWNING);

            } else if (time <= 0) {
                player.sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Respawning...");
                player.teleport(arena.getEventJoinLocation().toBukkitLocation());

                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    giveRespawnItems(player);
                    player.teleport(getGameLocations().remove(ThreadLocalRandom.current().nextInt(getGameLocations().size())).toBukkitLocation());
                    getBukkitPlayers().forEach(member -> member.showPlayer(player));
                    getBukkitPlayers().forEach(player::showPlayer);
                }, 2L);

                oitcPlayer.setState(OITCPlayer.OITCState.FIGHTING);

                cancel();
            }

            time--;

        }
    }

    /**
     * To ensure that the fight does not go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class OITCGameTask extends BukkitRunnable {

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

                getPlayers().values().forEach(player -> {
                    player.setScore(0);
                    player.setState(OITCPlayer.OITCState.FIGHTING);
                });

                getBukkitPlayers().forEach(player -> {
                    OITCPlayer oitcPlayer = getPlayer(player.getUniqueId());

                    if (oitcPlayer != null) {
                        teleportNextLocation(player);
                        giveRespawnItems(player);
                    }
                });
            } else if (time <= 0) {
                Player winner = getWinnerPlayer();

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerdata = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerdata.setOitcWins(winnerdata.getOitcWins() + 1);
                }

                gameTask.cancel();
                end();
                cancel();
                return;
            }

            if (getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(OITCPlayer.OITCState.FIGHTING).get(0));

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerdata = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerdata.setOitcWins(winnerdata.getOitcWins() + 1);
                }

                cancel();
                end();
            }

            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Game ends in " + time + " seconds" +".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
                sendMessage(ChatColor.GRAY + "[Event] " + ChatColor.YELLOW + "Game is ending in " + time + " seconds" +".");
            }

            time--;
        }
    }

    public List<OITCPlayer> sortedScores() {
        List<OITCPlayer> list = new ArrayList<>(this.players.values());
        list.sort(new SortComparator().reversed());
        return list;
    }

    private class SortComparator implements Comparator<OITCPlayer> {

        @Override
        public int compare(OITCPlayer p1, OITCPlayer p2) {
            return Integer.compare(p1.getScore(), p2.getScore());
        }
    }
}