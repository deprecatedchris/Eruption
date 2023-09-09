package me.chris.eruption.events.types.corners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventCountdownTask;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.util.cuboid.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class FourCornersEvent extends PracticeEvent<FourCornersPlayer> {

    private final Map<UUID, FourCornersPlayer> players = new HashMap<>();
    private final FourCornersCountdownTask countdownTask = new FourCornersCountdownTask(this);
    private RunnerGameTask gameTask;
    private MoveTask moveTask;
    private RemoveBlocksTask removeBlocksTask;
    private Map<Location, ItemStack> blocks;
    private int seconds, randomWool, round;
    private boolean running = false;
    private Cuboid zone;

    public FourCornersEvent() {
        super("4Corners");
    }

    @Override
    public Map<UUID, FourCornersPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<LocationUtil> getSpawnLocations() {
        return Collections.singletonList(getPlugin().getSpawnManager().getCornersLocation());
    }

    @Override
    public void onStart() {
        seconds = 11;
        round = 1;
        gameTask = new RunnerGameTask();
        gameTask.runTaskTimerAsynchronously(getPlugin(), 0L, 20L);
        blocks = new HashMap<>();
        zone = new Cuboid(getPlugin().getSpawnManager().getCornersMin().toBukkitLocation(), getPlugin().getSpawnManager().getCornersMax().toBukkitLocation());
    }

    private void cancelAll() {
        if (gameTask != null) {
            gameTask.cancel();
        }

        if (moveTask != null) {
            moveTask.cancel();
        }

        if (removeBlocksTask != null) {
            removeBlocksTask.cancel();
        }

        running = false;
        zone = null;
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new FourCornersPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            FourCornersPlayer data = getPlayer(player);

            if (data.getState() != FourCornersPlayer.FourCornerState.INGAME) {
                return;
            }

            data.setState(FourCornersPlayer.FourCornerState.ELIMINATED);
            players.remove(player.getUniqueId(), data);
            getPlugin().getPlayerManager().sendToSpawnAndReset(player);

            EruptionPlugin.getInstance().getPlayerManager().giveLobbyItems(player);

            sendMessage(ChatColor.RED + player.getName() + ChatColor.YELLOW + " was eliminated.");
            PlayerData playerCorenersData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            playerCorenersData.setCornersLosses(playerCorenersData.getCornersLosses() + 1);

            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "You have been eliminated from the commands. Better luck next time!");
            player.sendMessage(" ");

            if (getByState(FourCornersPlayer.FourCornerState.INGAME).size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(FourCornersPlayer.FourCornerState.INGAME).get(0));
                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setCornersWins(winnerData.getCornersWins() + 1);
                }

                end();
                cancelAll();

                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    blocks.forEach(((location, stack) ->
                            location.getBlock().setTypeIdAndData(stack.getTypeId(),
                                    (byte) stack.getDurability(), true)));

                    if (blocks.size() > 0) {
                        blocks.clear();
                    }
                }, 40L);
            }
        };
    }


    public List<UUID> getByState(FourCornersPlayer.FourCornerState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(FourCornersPlayer::getUuid).collect(Collectors.toList());
    }

    /**
     * To ensure that the fight does not go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class RunnerGameTask extends BukkitRunnable {

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
                // Teleport that jumped out of bounds back to the center of the map.
                getBukkitPlayers().stream().filter(player -> getPlayers().containsKey(player.getUniqueId())).forEach(player -> {
                    player.teleport(getPlugin().getSpawnManager().getCornersLocation().toBukkitLocation());
                });

                getPlayers().values().forEach(player -> player.setState(FourCornersPlayer.FourCornerState.INGAME));
                getBukkitPlayers().forEach(player -> player.getInventory().clear());

                moveTask = new MoveTask();
                moveTask.runTaskTimer(getPlugin(), 0, 1L);

                removeBlocksTask = new RemoveBlocksTask();
                removeBlocksTask.runTaskTimer(getPlugin(), 0L, 20L);
                running = true;
            } else if (time <= 0) {
                Player winner = getRandomPlayer();

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setCornersWins(winnerData.getCornersWins() + 1);
                }

                end();
                cancelAll();
                cancel();
                return;
            }

            if (getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(FourCornersPlayer.FourCornerState.INGAME).get(0));

                if (winner != null) {
                    handleWin(winner);
                    PlayerData winnerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setCornersWins(winnerData.getCornersWins() + 1);
                }

                end();
                cancelAll();
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

    private Player getRandomPlayer() {
        if (getByState(FourCornersPlayer.FourCornerState.INGAME).size() == 0) {
            return null;
        }

        List<UUID> fighting = getByState(FourCornersPlayer.FourCornerState.INGAME);

        Collections.shuffle(fighting);

        UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

        return getPlugin().getServer().getPlayer(uuid);
    }

    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();

        return strings;
    }

    @RequiredArgsConstructor
    private class MoveTask extends BukkitRunnable {
        @Override
        public void run() {
            getBukkitPlayers().forEach(player -> {
                if (getPlayer(player.getUniqueId()) != null && getPlayer(player.getUniqueId()).getState() == FourCornersPlayer.FourCornerState.INGAME) {
                    if (getPlayers().size() <= 1) {
                        return;
                    }

                    if (getPlayers().containsKey(player.getUniqueId())) {
                        Block legs = player.getLocation().getBlock();
                        Block head = legs.getRelative(BlockFace.UP);
                        if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                            onDeath().accept(player);
                        }
                    }
                }
            });
        }
    }

    @RequiredArgsConstructor
    private class RemoveBlocksTask extends BukkitRunnable {
        @Override
        public void run() {
            if (!running) {
                return;
            }

            seconds--;

            if (seconds <= 0) {
                running = false;
                handleRemoveBridges(true);
                Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                    handleRemoveBridges(false);

                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        blocks.forEach(((location, stack) ->
                                location.getBlock().setTypeIdAndData(stack.getTypeId(),
                                        (byte) stack.getDurability(), true)));

                        if (blocks.size() > 0) {
                            blocks.clear();
                        }
                    }, 60L);

                    Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
                        round++;
                        seconds = 11;
                        running = true;
                    }, 100L);
                }, 60L);
                return;
            }

            if (Arrays.asList(10, 5, 4, 3, 2, 1).contains(seconds)) {
                sendMessage(ChatColor.GRAY + "[" + "Round #" + round + "] " + ChatColor.YELLOW + "Bridges dropping in " + ChatColor.RED + seconds + " " + (seconds == 1 ? "second" : "seconds") + ChatColor.YELLOW + ".");
            }
        }
    }

    private void handleRemoveBridges(boolean bridges) {
        randomWool = getRandomWool();

        zone.forEach(block -> {
            if (bridges) {
                if (!block.getType().equals(Material.WOOL)) {
                    blocks.put(block.getLocation(), new ItemStack(block.getType(), 1, block.getData()));
                    block.setType(Material.AIR);
                }
            } else {
                if (block.getType().equals(Material.WOOL) && block.getData() == (byte) randomWool) {
                    blocks.put(block.getLocation(), new ItemStack(block.getType(), 1, (short) randomWool));
                    block.setType(Material.AIR);
                    //block.getLocation().getWorld().strikeLightningEffect(block.getLocation());
                }
            }
        });

        if (!bridges) {
            sendMessage(ChatColor.GRAY + "[Round #" + round + "] " + (randomWool == 14 ? "&cRed" : randomWool == 11 ? "&9Blue" : randomWool == 5 ? "&aGreen" : "&eYellow") + ChatColor.YELLOW + " was dropped.");
        }
    }

    private int getRandomWool() {
        List<Integer> wools = Arrays.asList(14, 11, 5, 4);
        return wools.get(ThreadLocalRandom.current().nextInt(wools.size()));
    }
}