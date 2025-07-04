package me.chris.eruption;

import com.google.gson.JsonParser;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;
import lombok.Getter;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.command.parameter.ArenaParameter;
import me.chris.eruption.command.parameter.KitParameter;
import me.chris.eruption.database.DatabaseHandler;
import me.chris.eruption.events.managers.EventManager;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.managers.EditorManager;
import me.chris.eruption.kit.managers.KitManager;
import me.chris.eruption.match.listeners.MatchListener;
import me.chris.eruption.match.managers.MatchManager;
import me.chris.eruption.party.managers.PartyManager;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.listeners.*;
import me.chris.eruption.profile.managers.HotbarManager;
import me.chris.eruption.profile.managers.InventoryManager;
import me.chris.eruption.profile.managers.PlayerManager;
import me.chris.eruption.queue.managers.QueueManager;
import me.chris.eruption.scoreboard.ScoreboardAdapter;
import me.chris.eruption.arena.arena.managers.ArenaManager;
import me.chris.eruption.arena.managers.ChunkManager;
import me.chris.eruption.tournament.managers.TournamentManager;
import me.chris.eruption.util.inventory.UIListener;
import me.chris.eruption.util.other.ItemBuilder;
import me.chris.eruption.runnable.ExpBarRunnable;
import me.chris.eruption.runnable.SaveDataRunnable;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.util.timer.TimerManager;
import me.chris.eruption.util.timer.impl.EnderpearlTimer;
import me.vaperion.blade.Blade;
import me.vaperion.blade.command.Command;
import me.vaperion.blade.bukkit.BladeBukkitPlatform;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

@Getter
public class EruptionPlugin extends JavaPlugin {
    @Getter
    private static EruptionPlugin instance;

    @Getter
    public static JsonParser PARSER = new JsonParser();

    private InventoryManager inventoryManager;
    private EditorManager editorManager;
    private PlayerManager playerManager;
    private ArenaManager arenaManager;
    private MatchManager matchManager;
    private PartyManager partyManager;
    private QueueManager queueManager;
    private EventManager eventManager;
    private HotbarManager hotbarManager;
    private KitManager kitManager;
    private TournamentManager tournamentManager;
    private ChunkManager chunkManager;
    private TimerManager timerManager;

    private Location spawnLocation = null ;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();


        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        new DatabaseHandler(
                getConfig().getString("MONGO.HOST"),
                getConfig().getInt("MONGO.PORT"),
                getConfig().getBoolean("MONGO.AUTH.ENABLED"),
                getConfig().getString("MONGO.AUTH.USERNAME"),
                getConfig().getString("MONGO.AUTH.PASSWORD"),
                getConfig().getString("MONGO.DATABASE")
        );

        this.registerListeners();
        this.registerManagers();

        Blade.forPlatform(new BladeBukkitPlatform(this))
                .config(cfg -> {
                    cfg.setFallbackPrefix("eruption");
                    cfg.setDefaultPermissionMessage(ChatColor.RED + "You do not have permission to execute this command.");
                    cfg.setHelpSorter(Comparator.comparing(Command::getUsageAlias));
                })
                .bind(binder -> {
                    binder.bind(Arena.class, new ArenaParameter());
                    binder.bind(Kit.class, new KitParameter());
                })
                .build()
                .registerPackage(EruptionPlugin.class, "me.chris.eruption.command");

        Assemble board = new Assemble(this, new ScoreboardAdapter());
        board.setTicks(20);
        board.setAssembleStyle(AssembleStyle.VIPER);

        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(), 20L * 60L * 5L, 20L * 60L * 5L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);

        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity instanceof Player) {
                continue;
            }
            entity.remove();
        }

        ItemBuilder.registerGlow();

        if (getConfig().getString("LOCATION") != null) {
            spawnLocation = LocationUtil.stringToLocation(getConfig().getString("LOCATION")).toBukkitLocation();
        }

    }

    @Override
    public void onDisable() {
        for (PlayerData playerData : this.playerManager.getAllData()) {
            this.playerManager.saveData(playerData);
        }

        this.arenaManager.saveArenas();
        this.kitManager.saveKits();
    }

    private void registerListeners() {
        Arrays.asList(
                new EntityListener(),
                new PlayerListener(),
                new MatchListener(),
                new WorldListener(),
                new UIListener(),
                new InventoryListener()
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.arenaManager = new ArenaManager();
        this.chunkManager = new ChunkManager();
        this.editorManager = new EditorManager();
        this.hotbarManager = new HotbarManager();
        this.kitManager = new KitManager();
        this.matchManager = new MatchManager();
        this.partyManager = new PartyManager();
        this.playerManager = new PlayerManager();
        this.queueManager = new QueueManager();
        this.inventoryManager = new InventoryManager();
        this.eventManager = new EventManager();
        this.tournamentManager = new TournamentManager();
        this.timerManager = new TimerManager(this);

        if (this.timerManager.getTimer(EnderpearlTimer.class) == null) {
            this.timerManager.registerTimer(new EnderpearlTimer());
        }

        this.removeCrafting();
    }

    private void removeCrafting() {
        Iterator<Recipe> iterator = getServer().recipeIterator();

        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            if (recipe != null && recipe.getResult().getType() == Material.SNOW_BLOCK) {
                iterator.remove();
            }
        }
    }
}
