package me.chris.eruption.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.event.EventStartEvent;
import me.chris.eruption.util.other.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import me.chris.eruption.events.types.lms.LMSEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.oitc.OITCPlayer;
import me.chris.eruption.events.types.parkour.ParkourEvent;
import me.chris.eruption.events.types.runner.RunnerEvent;
import me.chris.eruption.events.types.sumo.SumoPlayer;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class PracticeEvent<K extends EventPlayer> {
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    private final String name;

    private Player host;
    private int limit = 30;
    private EventState state = EventState.UNANNOUNCED;
    private List<UUID> playersX = new ArrayList<>();

    public void startCountdown() {
        // Restart Logic
        if (getCountdownTask().isEnded()) {
            getCountdownTask().setTimeUntilStart(getCountdownTask().getCountdownTime());
            getCountdownTask().setEnded(false);
        } else {
            getCountdownTask().runTaskTimer(plugin, 20L, 20L);
        }
    }

    public void sendMessage(String message) {
        getBukkitPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public Set<Player> getBukkitPlayers() {
        return getPlayers().keySet().stream()
                .filter(uuid -> plugin.getServer().getPlayer(uuid) != null)
                .map(plugin.getServer()::getPlayer)
                .collect(Collectors.toSet());
    }

    public void join(Player player) {
        if (getPlayers().size() >= limit) {
            return;
        }

        playersX.add(player.getUniqueId());

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.EVENT);

        PlayerUtil.clearPlayer(player);

        if (onJoin() != null) {
            onJoin().accept(player);
        }

        if (getSpawnLocations().size() == 1) {
            player.teleport(getSpawnLocations().get(0).toBukkitLocation());
        } else {
            List<LocationUtil> spawnLocations = new ArrayList<>(getSpawnLocations());
            player.teleport(spawnLocations.get(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }

        plugin.getPlayerManager().giveLobbyItems(player);

        getBukkitPlayers().forEach(other -> other.showPlayer(player));
        getBukkitPlayers().forEach(player::showPlayer);

        sendMessage(ChatColor.RED + player.getName() + ChatColor.WHITE + " has joined the game. " + ChatColor.YELLOW + "(" + getPlayers().size() + "/" + limit + ")");
        player.sendMessage(ChatColor.WHITE + "You have joined the " + name + " commands.");
    }

    public void leave(Player player) {
        if (this instanceof OITCEvent) {
            OITCEvent oitcEvent = (OITCEvent) this;
            OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
            oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
        }

        playersX.remove(player.getUniqueId());

        if (onDeath() != null) {
            onDeath().accept(player);
        }

        getPlayers().remove(player.getUniqueId());
        sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " has left the game. " + ChatColor.YELLOW + "(" + getPlayers().size() + "/" + limit + ")");
        player.sendMessage(ChatColor.YELLOW + "Returning you to spawn...");
        this.getPlayers().remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }


    public void start() {
        new EventStartEvent(this).call();


        setState(EventState.STARTED);

        onStart();

        plugin.getEventManager().setCooldown(0L);
    }

    public void handleWin(Player winner) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "                        "));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛⬛⬛⬛⬛⬛⬛"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛⬛⬛⬛&7⬛⬛"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛&7⬛⬛⬛⬛⬛ " + ChatColor.GREEN.toString() + ChatColor.BOLD + "[" + name + " Event]"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛⬛⬛⬛&7⬛⬛ " + winner.getName()) + ChatColor.YELLOW + " has won the commands.");
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛&7⬛⬛⬛⬛⬛ " + ChatColor.GRAY.toString() + ChatColor.ITALIC + "Good game!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛&4⬛⬛⬛⬛&7⬛⬛"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7⬛⬛⬛⬛⬛⬛⬛⬛"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "                        "));
    }

    public void end() {
        Bukkit.getScheduler().runTaskLater(plugin, () ->
                plugin.getEventManager().getEventWorld().getPlayers().forEach(player ->
                        plugin.getPlayerManager().sendToSpawnAndReset(player)), 2L);

        plugin.getEventManager().setCooldown(System.currentTimeMillis() + (60 * 3) * 1000L);

        playersX.clear();

        EruptionPlugin.getInstance().getEventManager().getSpectators().forEach((uuid, event) -> EruptionPlugin.getInstance().getEventManager().removeSpectator(Bukkit.getPlayer(uuid), this));

        if (this instanceof SumoEvent) {
            SumoEvent sumoEvent = (SumoEvent) this;

            sumoEvent.setRound(0);

            for (SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {
                if (sumoPlayer.getFightTask() != null) {
                    sumoPlayer.getFightTask().cancel();
                }
            }

            if (sumoEvent.getWaterCheckTask() != null) {
                sumoEvent.getWaterCheckTask().cancel();
            }
        } else if (this instanceof RunnerEvent) {
            RunnerEvent runnerEvent = (RunnerEvent) this;
            runnerEvent.cancelAll();
        } else if (this instanceof LMSEvent) {
            LMSEvent lmsEvent = (LMSEvent) this;
            lmsEvent.cancelAll();
            Bukkit.getWorld("commands").getEntities().stream().filter(entity -> entity instanceof Item).forEach(Entity::remove);
        } else if (this instanceof OITCEvent) {
            OITCEvent oitcEvent = (OITCEvent) this;

            if (oitcEvent.getGameTask() != null) {
                oitcEvent.getGameTask().cancel();
            }

            oitcEvent.setRunning(false);
        } else if (this instanceof ParkourEvent) {
            ParkourEvent parkourEvent = (ParkourEvent) this;

            if (parkourEvent.getGameTask() != null) {
                parkourEvent.getGameTask().cancel();
            }

            if (parkourEvent.getWaterCheckTask() != null) {
                parkourEvent.getWaterCheckTask().cancel();
            }
        }


        getPlayers().clear();
        setState(EventState.UNANNOUNCED);

        Iterator<UUID> iterator = plugin.getEventManager().getSpectators().keySet().iterator();

        while (iterator.hasNext()) {
            UUID spectatorUUID = iterator.next();
            Player spectator = Bukkit.getPlayer(spectatorUUID);

            if (spectator != null) {
                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getPlayerManager().sendToSpawnAndReset(spectator));
                iterator.remove();
            }
        }

        plugin.getEventManager().getSpectators().clear();
        getCountdownTask().setEnded(true);
    }

    public Player getRandomPlayerTest() {
        List<Player> list = new ArrayList<>();
        getPlayers().forEach((uuid, k) -> list.add(Bukkit.getPlayer(uuid)));
        if (list.size() == 0) {
            return null;
        }
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public K getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public K getPlayer(UUID uuid) {
        return getPlayers().get(uuid);
    }

    public abstract Map<UUID, K> getPlayers();

    public abstract EventCountdownTask getCountdownTask();

    public abstract List<LocationUtil> getSpawnLocations();

    public abstract void onStart();

    public abstract Consumer<Player> onJoin();

    public abstract Consumer<Player> onDeath();

}
