package me.chris.eruption.match;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.setup.arena.StandaloneArena;
import me.chris.eruption.match.menus.InventorySnapshot;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.util.other.Clickable;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import me.chris.eruption.queue.QueueType;
import me.chris.eruption.util.other.TimeUtils;

import java.util.*;
import java.util.stream.Stream;

@Setter
public class Match {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Getter
    private final Map<UUID, InventorySnapshot> snapshots = new HashMap<>();

    @Getter
    private final Set<Entity> entitiesToRemove = new HashSet<>();
    @Getter
    private final Set<BlockState> originalBlockChanges = new ConcurrentSet<>();
    @Getter
    private final Set<Location> placedBlockLocations = new ConcurrentSet<>();
    @Getter
    private final Set<UUID> spectators = new ConcurrentSet<>();
    @Getter
    private final Set<Integer> runnables = new HashSet<>();

    private final Set<UUID> haveSpectated = new HashSet<>();

    @Getter
    private final List<MatchTeam> teams;

    @Getter
    private final UUID matchId = UUID.randomUUID();
    @Getter
    private final QueueType type;
    @Getter
    private final Arena arena;
    @Getter
    private final Kit kit;
    @Getter
    private final boolean redrover;
    @Getter
    @Setter
    private String starting = "Starting";

    @Getter
    private StandaloneArena standaloneArena;
    @Getter
    private MatchState matchState = MatchState.STARTING;
    @Getter
    private int winningTeamId;
    @Getter
    private int countdown = 6;
    @Getter
    @Setter
    private int durationTimer;

    public Match(Arena arena, Kit kit, QueueType type, MatchTeam... teams) {
        this(arena, kit, type, false, teams);
    }

    public Match(Arena arena, Kit kit, QueueType type, boolean redrover, MatchTeam... teams) {
        this.arena = arena;
        this.kit = kit;
        this.type = type;
        this.redrover = redrover;
        this.teams = Arrays.asList(teams);
    }

    public String getDuration() {
        return TimeUtils.formatIntoMMSS(durationTimer);
    }

    public void addSpectator(UUID uuid) {
        this.spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        this.spectators.remove(uuid);
    }

    public void addHaveSpectated(UUID uuid) {
        this.haveSpectated.add(uuid);
    }

    public boolean haveSpectated(UUID uuid) {
        return this.haveSpectated.contains(uuid);
    }

    public void addSnapshot(Player player) {
        this.snapshots.put(player.getUniqueId(), new InventorySnapshot(player, this));
    }

    public String getStartingAnimation() {
        switch (starting) {
            case "Starting":
                starting = "Starting.";
                break;
            case "Starting.":
                starting = "Starting..";
                break;
            case "Starting..":
                starting = "Starting...";
                break;
            default:
                starting = "Starting";
                break;
        }

        return starting;
    }

    public boolean hasSnapshot(UUID uuid) {
        return this.snapshots.containsKey(uuid);
    }

    public InventorySnapshot getSnapshot(UUID uuid) {
        return this.snapshots.get(uuid);
    }

    public void addEntityToRemove(Entity entity) {
        this.entitiesToRemove.add(entity);
    }

    public void removeEntityToRemove(Entity entity) {
        this.entitiesToRemove.remove(entity);
    }

    public void clearEntitiesToRemove() {
        this.entitiesToRemove.clear();
    }

    public void addRunnable(int id) {
        this.runnables.add(id);
    }

    public void addOriginalBlockChange(BlockState blockState) {
        this.originalBlockChanges.add(blockState);
    }

    public void removeOriginalBlockChange(BlockState blockState) {
        this.originalBlockChanges.remove(blockState);
    }

    public void addPlacedBlockLocation(Location location) {
        this.placedBlockLocations.add(location);
    }

    public void removePlacedBlockLocation(Location location) {
        this.placedBlockLocations.remove(location);
    }

    public void broadcastWithSound(String message, Sound sound) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10, 1);
        }));
        this.spectatorPlayers().forEach(spectator -> {
            spectator.sendMessage(message);
            spectator.playSound(spectator.getLocation(), sound, 10, 1);
        });
    }

    public void broadcast(String message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
        this.spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
    }
    public void broadcast(BaseComponent[] message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.spigot().sendMessage(message)));
        this.spectatorPlayers().forEach(spectator -> spectator.spigot().sendMessage(message));
    }

    public void broadcast(Clickable message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
        this.spectatorPlayers().forEach(message::sendToPlayer);
    }

    public Stream<Player> spectatorPlayers() {
        return this.spectators.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public int decrementCountdown() {
        return --this.countdown;
    }

    public int incrementDuration() {
        return ++this.durationTimer;
    }

    public boolean isParty() {
        return this.isFFA() || this.teams.get(0).getPlayers().size() != 1 && this.teams.get(1).getPlayers().size() != 1;
    }

    public boolean isFFA() {
        return this.teams.size() == 1;
    }

}
