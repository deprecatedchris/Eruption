package me.chris.eruption.events.types.parkour;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.events.EventCountdownTask;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.ItemBuilder;
import me.chris.eruption.util.other.PlayerUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParkourEvent extends PracticeEvent<ParkourPlayer> {

    private final Map<UUID, ParkourPlayer> players = new HashMap<>();

    @Getter
    private ParkourGameTask gameTask = null;
    private final ParkourCountdownTask countdownTask = new ParkourCountdownTask(this);
    @Getter
    private WaterCheckTask waterCheckTask;
    private List<UUID> visibility;

    public ParkourEvent() {
        super("Parkour");
    }

    @Override
    public Map<UUID, ParkourPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<LocationUtil> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getParkourLocation());
    }

    @Override
    public void onStart() {
        this.gameTask = new ParkourGameTask();
        this.gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
        this.waterCheckTask = new WaterCheckTask();
        this.waterCheckTask.runTaskTimer(getPlugin(), 0, 10L);
        this.visibility = new ArrayList<>();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new ParkourPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + player.getDisplayName() + ChatColor.WHITE + " has left the game.");
    }

    public void toggleVisibility(Player player) {
        if (visibility.contains(player.getUniqueId())) {
            getBukkitPlayers().forEach(player::showPlayer);
            visibility.remove(player.getUniqueId());
            player.sendMessage(ChatColor.WHITE + "You are now " + ChatColor.WHITE + "able" + ChatColor.WHITE + " to see players.");
            return;
        }

        getBukkitPlayers().forEach(player::hidePlayer);
        visibility.add(player.getUniqueId());
        player.sendMessage(ChatColor.WHITE + "You are now " + ChatColor.WHITE + "unable" + ChatColor.WHITE + " to see players.");

    }

    private void teleportToSpawnOrCheckpoint(Player player) {
        ParkourPlayer parkourPlayer = this.getPlayer(player.getUniqueId());

        if (parkourPlayer != null && parkourPlayer.getLastCheckpoint() != null) {
            player.teleport(parkourPlayer.getLastCheckpoint().toBukkitLocation());
            player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Teleporting back to last checkpoint.");
            return;
        }

        player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Teleporting back to the beginning.");
        player.teleport(this.getPlugin().getSpawnManager().getParkourGameLocation().toBukkitLocation());
    }

    private void giveItems(Player player) {
        this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, new ItemBuilder(Material.EYE_OF_ENDER).name("&aToggle Visibility").build());
            player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("&cLeave Event").build());
            player.updateInventory();
        });
    }

    private Player getRandomPlayer() {
        if (getByState(ParkourPlayer.ParkourState.INGAME).size() == 0) {
            return null;
        }

        List<UUID> fighting = getByState(ParkourPlayer.ParkourState.INGAME);

        Collections.shuffle(fighting);

        UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

        return getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(ParkourPlayer.ParkourState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(ParkourPlayer::getUuid).collect(Collectors.toList());
    }

    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();

        return strings;
    }

    public void addSpectator(Player player, PlayerData playerData) {
        if (getSpawnLocations().size() == 1) {
            player.teleport(getSpawnLocations().get(0).toBukkitLocation());
        } else {
            List<LocationUtil> spawnLocations = new ArrayList<>(getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }

        for (Player eventPlayer : getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.ADVENTURE);

        player.setAllowFlight(true);
        player.setFlying(true);
    }

    /**
     * To ensure that the fight does not go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class ParkourGameTask extends BukkitRunnable {

        private int time = 303;

        @Override
        public void run() {
            if (time == 303) {
                sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Starting in " + ChatColor.WHITE + "3 seconds" + ChatColor.WHITE + ".");
            } else if (time == 302) {
                sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Starting in " + ChatColor.WHITE + "2 seconds" + ChatColor.WHITE + ".");
            } else if (time == 301) {
                sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Starting in " + ChatColor.WHITE + "1 second" + ChatColor.WHITE + ".");
            } else if (time == 300) {
                sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "The game has started.");

                for (ParkourPlayer player : getPlayers().values()) {
                    player.setLastCheckpoint(null);
                    player.setState(ParkourPlayer.ParkourState.INGAME);
                    player.setCheckpointId(0);
                }

                for (Player player : getBukkitPlayers()) {
                    teleportToSpawnOrCheckpoint(player);
                    giveItems(player);
                }

            } else if (time <= 0) {
                Player winner = getRandomPlayer();

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setParkourWins(winnerData.getParkourWins() + 1);
                }

                end();
                cancel();
                return;
            }

            if (getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(ParkourPlayer.ParkourState.INGAME).get(0));

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setParkourWins(winnerData.getParkourWins() + 1);
                }

                end();
                cancel();
                return;
            }


            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
                sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Game ends in " + ChatColor.WHITE + time + " seconds" + ChatColor.WHITE + ".");
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
                sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Game is ending in " + ChatColor.WHITE + time + " seconds" + ChatColor.WHITE + ".");
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
                if (getPlayer(player) != null && getPlayer(player).getState() != ParkourPlayer.ParkourState.INGAME) {
                    return;
                }

                if (isStandingOn(player, Material.WATER) || isStandingOn(player, Material.STATIONARY_WATER)) {
                    teleportToSpawnOrCheckpoint(player);
                } else if (isStandingOn(player, Material.STONE_PLATE) || isStandingOn(player, Material.IRON_PLATE) || isStandingOn(player, Material.WOOD_PLATE)) {
                    ParkourPlayer parkourPlayer = getPlayer(player.getUniqueId());

                    if (parkourPlayer != null) {

                        boolean checkpoint = false;

                        if (parkourPlayer.getLastCheckpoint() == null) {
                            checkpoint = true;
                            parkourPlayer.setLastCheckpoint(LocationUtil.fromBukkitLocation(player.getLocation()));
                        } else if (parkourPlayer.getLastCheckpoint() != null && !isSameLocation(player.getLocation(), parkourPlayer.getLastCheckpoint().toBukkitLocation())) {
                            checkpoint = true;
                            parkourPlayer.setLastCheckpoint(LocationUtil.fromBukkitLocation(player.getLocation()));
                        }

                        if (checkpoint) {
                            parkourPlayer.setCheckpointId(parkourPlayer.getCheckpointId() + 1);
                            player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.WHITE + "Event" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Checkpoint " + ChatColor.WHITE + "#" + parkourPlayer.getCheckpointId() + ChatColor.WHITE + " has been set.");
                        }
                    }
                } else if (isStandingOn(player, Material.GOLD_PLATE)) {
                    handleWin(player);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
                    winnerData.setParkourWins(winnerData.getParkourWins() + 1);
                    end();
                    cancel();
                }
            });
        }
    }

    private boolean isStandingOn(Player player, Material material) {
        Block legs = player.getLocation().getBlock();
        Block head = legs.getRelative(BlockFace.UP);
        return legs.getType() == material || head.getType() == material;
    }

    private boolean isSameLocation(Location location, Location check) {
        return location.getWorld().getName().equalsIgnoreCase(check.getWorld().getName()) && location.getBlockX() == check.getBlockX() && location.getBlockY() == check.getBlockY() && location.getBlockZ() == check.getBlockZ();
    }
}