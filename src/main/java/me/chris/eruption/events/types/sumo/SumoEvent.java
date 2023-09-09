package me.chris.eruption.events.types.sumo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.events.EventCountdownTask;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SumoEvent extends PracticeEvent<SumoPlayer> {

    private final Map<UUID, SumoPlayer> players = new HashMap<>();

    @Getter
    final List<Player> fighting = new ArrayList<>();
    private final SumoCountdownTask countdownTask = new SumoCountdownTask(this);
    @Getter
    private WaterCheckTask waterCheckTask;
    @Getter
    @Setter
    private int round;
    @Getter
    private Player playerA;
    @Getter
    private Player playerB;

    public SumoEvent() {
        super("Sumo");
    }

    @Override
    public Map<UUID, SumoPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<LocationUtil> getSpawnLocations() {
        return Collections.singletonList(getPlugin().getSpawnManager().getSumoLocation());
    }

    @Override
    public void onStart() {
        round = 0;
        waterCheckTask = new WaterCheckTask();
        waterCheckTask.runTaskTimer(getPlugin(), 0, 10L);
        selectPlayers();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {

        return player -> {

            SumoPlayer data = getPlayer(player);

            if (data == null || data.getFighting() == null) {
                return;
            }

            if (data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {

                SumoPlayer killerData = data.getFighting();
                Player killer = getPlugin().getServer().getPlayer(killerData.getUuid());

                data.getFightTask().cancel();
                killerData.getFightTask().cancel();


                PlayerData playerData = this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId());
                PlayerData killerPData = this.getPlugin().getPlayerManager().getPlayerData(killer.getUniqueId());


                if (playerData != null) {
                    playerData.setSumoLosses(playerData.getSumoLosses() + 1);
                }

                data.setState(SumoPlayer.SumoState.ELIMINATED);
                killerData.setState(SumoPlayer.SumoState.WAITING);

                PlayerUtil.clearPlayer(player);
                this.getPlugin().getPlayerManager().giveLobbyItems(player);

                PlayerUtil.clearPlayer(killer);
                this.getPlugin().getPlayerManager().giveLobbyItems(killer);

                if (getSpawnLocations().size() == 1) {
                    player.teleport(getSpawnLocations().get(0).toBukkitLocation());
                    killer.teleport(getSpawnLocations().get(0).toBukkitLocation());
                }

                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " was eliminated" + (killer == null ? "." : " by " + ChatColor.GOLD + killer.getName()) + ChatColor.YELLOW + ".");

                player.getInventory().clear();
                killer.getInventory().clear();

                EruptionPlugin.getInstance().getPlayerManager().sendToSpawnAndReset(player);

                if (getSpawnLocations().size() == 1) {
                    killer.teleport(getSpawnLocations().get(0).toBukkitLocation());
                } else {
                    List<LocationUtil> spawnLocations = new ArrayList<>(getSpawnLocations());
                    killer.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
                }

                PlayerUtil.clearPlayer(player);
                this.getPlugin().getPlayerManager().giveLobbyItems(player);

                PlayerUtil.clearPlayer(killer);
                this.getPlugin().getPlayerManager().giveLobbyItems(player);

                PracticeEvent practiceEvent = EruptionPlugin.getInstance().getEventManager().getEventPlaying(player);
                practiceEvent.leave(player);

                this.fighting.clear();

                if (this.getByState(SumoPlayer.SumoState.FIGHTING).size() == 0 && this.getByState(SumoPlayer.SumoState.PREPARING).size() == 0 && this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
                    Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));

                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setSumoWins(winnerData.getSumoWins() + 1);

                    player.getInventory().clear();
                    killer.getInventory().clear();

                    EruptionPlugin.getInstance().getPlayerManager().sendToSpawnAndReset(player);
                    EruptionPlugin.getInstance().getPlayerManager().sendToSpawnAndReset(killer);

                    PlayerUtil.clearPlayer(player);
                    this.getPlugin().getPlayerManager().giveLobbyItems(player);

                    PlayerUtil.clearPlayer(killer);
                    this.getPlugin().getPlayerManager().giveLobbyItems(killer);
                    practiceEvent.leave(player);
                    practiceEvent.leave(killer);
                    practiceEvent.leave(winner);


                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "                        "));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛⬛⬛⬛⬛⬛⬛"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛⬛⬛⬛&7⬛⬛"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛&7⬛⬛⬛⬛⬛ " + ChatColor.GREEN.toString() + ChatColor.BOLD + "[" + "Sumo" + " Event]"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛⬛⬛⬛&7⬛⬛ " + winner.getName()) + ChatColor.YELLOW + " has won the sumo event.");
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛&7⬛⬛⬛⬛⬛ " + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Good game!"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛⬛⬛⬛&7⬛⬛"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛⬛⬛⬛⬛⬛⬛"));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "                        "));

                    practiceEvent.end();
                    this.fighting.clear();
                    end();
                } else {
                    getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), this::selectPlayers, 3 * 20);
                }
            }
        };
    }

    private LocationUtil[] getSumoLocations() {
        LocationUtil[] array = new LocationUtil[2];
        array[0] = getPlugin().getSpawnManager().getSumoFirst();
        array[1] = getPlugin().getSpawnManager().getSumoSecond();
        return array;
    }

    private void selectPlayers() {



        if (getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
            Player winner = Bukkit.getPlayer(getByState(SumoPlayer.SumoState.WAITING).get(0));
            handleWin(winner);
            fighting.clear();
            end();
            return;
        }

        Player picked1 = getRandomPlayer();
        Player picked2 = getRandomPlayer();

        if (picked1 == null || picked2 == null) {
            selectPlayers();
            return;
        }

        fighting.clear();

        SumoPlayer picked1Data = getPlayer(picked1);
        SumoPlayer picked2Data = getPlayer(picked2);

        picked1Data.setFighting(picked2Data);
        picked2Data.setFighting(picked1Data);

        fighting.add(picked1);
        fighting.add(picked2);

        PlayerUtil.clearPlayer(picked1);
        PlayerUtil.clearPlayer(picked2);

        picked1.teleport(getSumoLocations()[0].toBukkitLocation());
        picked2.teleport(getSumoLocations()[1].toBukkitLocation());

        for (Player other : getBukkitPlayers()) {
            if (other != null) {
                other.showPlayer(picked1);
                other.showPlayer(picked2);
            }
        }

        for (UUID spectatorUUID : getPlugin().getEventManager().getSpectators().keySet()) {
            Player spectator = Bukkit.getPlayer(spectatorUUID);
            if (spectator != null) {
                spectator.showPlayer(picked1);
                spectator.showPlayer(picked2);
            }
        }

        picked1.showPlayer(picked2);
        picked2.showPlayer(picked1);

        round++;

        sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.GOLD + picked1.getName() + ChatColor.YELLOW + " vs. " + ChatColor.GOLD + picked2.getName());

        BukkitTask task = new SumoFightTask(picked1, picked2, picked1Data, picked2Data).runTaskTimer(getPlugin(), 0, 20);

        picked1Data.setFightTask(task);
        picked2Data.setFightTask(task);

        playerA = picked1;
        playerB = picked2;

    }

    private Player getRandomPlayer() {
        if (getByState(SumoPlayer.SumoState.WAITING).size() == 0) {
            return null;
        }

        List<UUID> waiting = getByState(SumoPlayer.SumoState.WAITING);

        Collections.shuffle(waiting);

        UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));

        getPlayer(uuid).setState(SumoPlayer.SumoState.PREPARING);

        return getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(SumoPlayer.SumoState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(SumoPlayer::getUuid).collect(Collectors.toList());
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
    public class SumoFightTask extends BukkitRunnable {
        private final Player player;
        private final Player other;

        private final SumoPlayer playerSumo;
        private final SumoPlayer otherSumo;

        private int time = 90;

        @Override
        public void run() {
            if (player == null || other == null || !player.isOnline() || !other.isOnline()) {
                cancel();
                return;
            }

            if (time == 90) {
                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Starting in" + ChatColor.GOLD + " 3 seconds.");
            } else if (time == 89) {
                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Starting in" + ChatColor.GOLD + " 2 seconds.");
            } else if (time == 88) {
                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Starting in" + ChatColor.GOLD + " 1 second.");
            } else if (time == 87) {
                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.GREEN + "Match started.");
                otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
                playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
            } else if (time <= 0) {
                List<Player> players = Arrays.asList(player, other);
                Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> onDeath().accept(pl));

                cancel();
                return;
            }

            if (Arrays.asList(30, 25, 20, 15, 10).contains(time)) {
                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.YELLOW + "The match ends in " + time + " seconds.");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
                sendMessage(ChatColor.GRAY + "[" + ChatColor.BLUE + "Round " + round + ChatColor.GRAY + "] " + ChatColor.YELLOW + "The match is ending in " + time + " seconds.");
            }

            time--;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class WaterCheckTask extends BukkitRunnable {
        @Override
        public void run() {
            if (getPlayers().size() <= 1) {
                return;
            }

            getBukkitPlayers().forEach(player -> {

                if (getPlayer(player) != null && getPlayer(player).getState() != SumoPlayer.SumoState.FIGHTING) {
                    return;
                }

                Block legs = player.getLocation().getBlock();
                Block head = legs.getRelative(BlockFace.UP);
                if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                    onDeath().accept(player);
                }
            });
        }
    }
}