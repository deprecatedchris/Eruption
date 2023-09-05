package me.chris.eruption;

import com.bizarrealex.aether.Aether;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.chris.eruption.events.managers.EventManager;
import me.chris.eruption.kit.managers.EditorManager;
import me.chris.eruption.kit.managers.KitManager;
import me.chris.eruption.match.listeners.MatchListener;
import me.chris.eruption.match.managers.MatchManager;
import me.chris.eruption.party.managers.PartyManager;
import me.chris.eruption.profile.commands.toggleable.*;
import me.chris.eruption.profile.listeners.*;
import me.chris.eruption.profile.managers.HotbarManager;
import me.chris.eruption.profile.managers.InventoryManager;
import me.chris.eruption.profile.managers.PlayerManager;
import me.chris.eruption.queue.managers.QueueManager;
import me.chris.eruption.setup.arena.commands.ArenaCommand;
import me.chris.eruption.events.commands.*;
import me.chris.eruption.match.commands.InvCommand;
import me.chris.eruption.kit.commands.KitCommand;
import me.chris.eruption.kit.commands.KitHelpCommand;
import me.chris.eruption.leaderboards.commands.LeaderboardsCommand;
import me.chris.eruption.leaderboards.commands.StatsCommand;
import me.chris.eruption.party.commands.PartyCommand;
import me.chris.eruption.profile.commands.*;
import me.chris.eruption.profile.commands.donator.FlyCommand;
import me.chris.eruption.util.random.Style;
import me.chris.eruption.util.runnable.ExpBarRunnable;
import me.chris.eruption.util.runnable.SaveDataRunnable;
import me.chris.eruption.settings.commands.SettingsCommand;
import me.chris.eruption.setup.ResetStatsCommand;
import me.chris.eruption.setup.SpawnsCommand;
import me.chris.eruption.setup.arena.managers.ArenaManager;
import me.chris.eruption.setup.managers.ChunkManager;
import me.chris.eruption.setup.managers.SpawnManager;
import me.chris.eruption.tournament.commands.TournamentCommand;
import me.chris.eruption.tournament.managers.TournamentManager;
import me.vaperion.blade.Blade;
import me.vaperion.blade.bukkit.BladeBukkitPlatform;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;
import me.chris.eruption.scoreboard.ScoreboardAdapter;
import me.chris.eruption.profile.commands.duel.AcceptCommand;
import me.chris.eruption.profile.commands.duel.DuelCommand;
import me.chris.eruption.profile.commands.duel.SpectateCommand;
import me.chris.eruption.profile.commands.time.DayCommand;
import me.chris.eruption.profile.commands.time.NightCommand;
import me.chris.eruption.setup.WarpCommand;
import me.chris.eruption.util.config.Config;
import me.chris.eruption.util.CustomMovementListener;
import me.chris.eruption.util.database.Mongo;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.random.User;
import me.chris.eruption.util.inventory.UIListener;
import me.chris.eruption.util.menu.ButtonListener;
import me.chris.eruption.util.menu.MenuUpdateTask;
import me.chris.eruption.util.timer.TimerManager;
import me.chris.eruption.util.timer.impl.EnderpearlTimer;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class EruptionPlugin extends JavaPlugin {

    @Getter
    private static EruptionPlugin instance;

    private Config mainConfig;

    @Getter
    public static JsonParser PARSER = new JsonParser();

    public static List<User> users;
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
    private SpawnManager spawnManager;
    private TournamentManager tournamentManager;
    private ChunkManager chunkManager;
    private TimerManager timerManager;
    private Command cmd;

    @Override
    public void onDisable() {
        for (PlayerData playerData : this.playerManager.getAllData()) {
            this.playerManager.saveData(playerData);
        }

        this.arenaManager.saveArenas();
        this.kitManager.saveKits();
    }

    @Override
    public void onEnable() {
        EruptionPlugin.instance = this;

        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setTime(6_000L);
        }

        getCommand("kithelp").setExecutor(new KitHelpCommand());

        this.mainConfig = new Config("config", this);



        new Mongo();

        this.registerCommands();
        this.registerListeners();
        this.registerManagers();

        Blade.forPlatform(new BladeBukkitPlatform(this))
                .build()
                .registerPackage(EruptionPlugin.class, "me.chris.eruption");
        //nvm not as bad as i thought LOL


        new Aether(this, new ScoreboardAdapter());
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new SaveDataRunnable(), 20L * 60L * 5L, 20L * 60L * 5L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ExpBarRunnable(), 2L, 2L);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new MenuUpdateTask(), 5L, 5L);


        for (Entity entity : Bukkit.getWorlds().get(0).getEntities()) {
            if (entity instanceof Player) {
                continue;
            }
            entity.remove();
        }

    }

    private void registerCommands() {
        Arrays.asList(
                new SettingsCommand(),
                new ToggleSpectatorsCommand(),
                new ToggleScoreboardCommand(),
                new ToggleStyleCommand(),
                new ResetStatsCommand(),
                new JoinEventCommand(),
                new LeaveEventCommand(),
                new StatusEventCommand(),
                new HostCommand(),
                new EventManagerCommand(),
                new ArenaCommand(),
                new NightCommand(),
                new FlyCommand(),
                new PartyCommand(),
                new DuelCommand(),
                new SpectateCommand(),
                new DayCommand(),
                new KitCommand(),
                new AcceptCommand(),
                new EventSpectateCommand(),
                new EruptionCommand(),
                new InvCommand(),
                new StatsCommand(),
                new SpawnsCommand(),
                new WarpCommand(),
                new TournamentCommand(),
                new LeaderboardsCommand(),
                new ToggleDuelRequestsCommand(),
                new TogglePartyInvitesCommand(),
                new TogglePlayerVisibilityCommand(),
                new ToggleScoreboardCommand(),
                new ToggleSpectatorsCommand(),
                new SaveCommand()
        ).forEach(command -> this.registerCommand(command, getName()));
    }

    public static User getUser(final Player player) {
        for (final User u : EruptionPlugin.users) {
            if (u.getPlayer() == player) {
                return u;
            }
        }
        return null;
    }


    private void registerListeners() {
        Arrays.asList(
                new EntityListener(),
                new PlayerListener(),
                new MatchListener(),
                new WorldListener(),
                new ShutdownListener(),
                new UIListener(),
                new InventoryListener(),
                new CustomMovementListener(),
                new ButtonListener()
        ).forEach(listener -> this.getServer().getPluginManager().registerEvents(listener, this));
    }

    private void registerManagers() {
        this.spawnManager = new SpawnManager();
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



    public void logConsole(String message) {
        this.getServer().getConsoleSender().sendMessage(Style.translate(message));
    }


    /**
     * @param cmd
     */
    public void registerCommand(Command cmd, String fallbackPrefix) {
        MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
    }


    private void registerCommand(Command cmd) {
        this.registerCommand(cmd, this.getName());
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
