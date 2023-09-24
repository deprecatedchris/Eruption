package me.chris.eruption.events.managers;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.corners.FourCornersEvent;
import me.chris.eruption.events.types.lms.LMSEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.parkour.ParkourEvent;
import me.chris.eruption.events.types.runner.RunnerEvent;
import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class EventManager {
    private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap<>();

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    private HashMap<UUID, PracticeEvent> spectators;

    @Setter
    private long cooldown;
    private final World eventWorld;

    public EventManager() {
        Arrays.asList(
                SumoEvent.class,
                LMSEvent.class,
                OITCEvent.class,
                ParkourEvent.class,
                RunnerEvent.class,
                FourCornersEvent.class
        ).forEach(this::addEvent);

        boolean newWorld;

        if (plugin.getServer().getWorld("commands") == null) {
            eventWorld = plugin.getServer().createWorld(new WorldCreator("commands"));
            newWorld = true;
        } else {
            eventWorld = plugin.getServer().getWorld("commands");
            newWorld = false;
        }

        this.spectators = new HashMap<>();

        this.cooldown = 0L;

        if (eventWorld != null) {

            if (newWorld) {
                plugin.getServer().getWorlds().add(eventWorld);
            }

            eventWorld.setTime(2000L);
            eventWorld.setGameRuleValue("doDaylightCycle", "false");
            eventWorld.setGameRuleValue("doMobSpawning", "false");
            eventWorld.setStorm(false);
            eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        }
    }

    public PracticeEvent getByName(String name) {
        return events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
    }

    public void hostEvent(PracticeEvent event, Player host, Arena eventArena) {

        event.setState(EventState.WAITING);
        event.setHost(host);
        event.setEventArena(eventArena);
        event.startCountdown();
    }

    public void addSpectator(Player player, PlayerData playerData, PracticeEvent event) {
        this.spectators.put(player.getUniqueId(), event);

        String spectatorMessage = ChatColor.RED + player.getName() + ChatColor.WHITE + " is now spectating.";

        event.sendMessage(spectatorMessage);

        playerData.setPlayerState(PlayerState.SPECTATING);

        player.teleport(event.getRandomPlayerTest().getLocation());

        player.setAllowFlight(true);
        player.setFlying(true);

        player.getInventory().setContents(this.plugin.getHotbarManager().getSpecItems());
        player.updateInventory();

        this.plugin.getServer().getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
            player.hidePlayer(online);
        });

        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setSaturation(12.8F);
        player.setMaximumNoDamageTicks(20);
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setLevel(0);
        player.setExp(0.0F);
        player.setWalkSpeed(0.2F);
        player.getInventory().setHeldItemSlot(0);
        player.setAllowFlight(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.closeInventory();

        player.setGameMode(GameMode.CREATIVE);
    }

    public void removeSpectator(Player player, PracticeEvent event) {
        this.spectators.remove(player.getUniqueId(), event);

        String spectatorMessage = ChatColor.RED + player.getName() + ChatColor.GRAY + " is no longer spectating.";

        event.sendMessage(spectatorMessage);

        EruptionPlugin.getInstance().getPlayerManager().sendToSpawnAndReset(player);
    }

    private void addEvent(Class<? extends PracticeEvent> clazz) {
        PracticeEvent event = null;

        try {
            event = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        events.put(clazz, event);
    }

    public boolean isPlaying(Player player, PracticeEvent event) {
        return event.getPlayers().containsKey(player.getUniqueId());
    }

    public PracticeEvent getEventPlaying(Player player) {
        return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
    }
}