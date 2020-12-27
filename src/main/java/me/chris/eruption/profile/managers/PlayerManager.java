package me.chris.eruption.profile.managers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.config.Config;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.util.database.Mongo;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.settings.SettingsInfo;
import me.chris.eruption.util.random.InventoryUtil;
import me.chris.eruption.util.random.ItemUtil;
import me.chris.eruption.util.random.PlayerUtil;
import me.chris.eruption.util.timer.impl.EnderpearlTimer;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();
    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();

    public void createPlayerData(Player player) {
        PlayerData data = new PlayerData(player.getUniqueId());

        for (Kit ladder : this.plugin.getKitManager().getKits()) {
            data.getKits().put(ladder.getName(), new PlayerKit[4]);
        }

        this.playerData.put(data.getUniqueId(), data);
        this.loadData(data);
    }

    @SuppressWarnings("unchecked")
    private void loadData(PlayerData playerData) {
        ConfigurationSection playerDataSelection = plugin.getConfig().getConfigurationSection("playerdata");
        if (playerDataSelection != null) {
            if (playerDataSelection.getConfigurationSection("elo") != null) {
                playerDataSelection.getConfigurationSection("elo").getKeys(false).forEach((kit) -> {
                    Integer elo = playerDataSelection.getInt("elo." + kit);
                    playerData.setElo(kit, elo);
                });
            }

            if (playerDataSelection.getConfigurationSection("losses") != null) {
                playerDataSelection.getConfigurationSection("losses").getKeys(false).forEach((kit) -> {
                    Integer elo = playerDataSelection.getInt("losses." + kit);
                    playerData.setLosses(kit, elo);
                });
            }

            if (playerDataSelection.getConfigurationSection("wins") != null) {
                playerDataSelection.getConfigurationSection("wins").getKeys(false).forEach((kit) -> {
                    Integer elo = playerDataSelection.getInt("wins." + kit);
                    playerData.setWins(kit, elo);
                });
            }

            if (playerDataSelection.getConfigurationSection("partyelo") != null) {
                playerDataSelection.getConfigurationSection("partyelo").getKeys(false).forEach((kit) -> {
                    Integer elo = playerDataSelection.getInt("partyelo." + kit);
                    playerData.setPartyElo(kit, elo);
                });
            }

            playerData.setOitcDeaths(playerDataSelection.getInt("oitcEventDeaths"));
            playerData.setOitcWins(playerDataSelection.getInt("oitcEventWins"));
            playerData.setOitcLosses(playerDataSelection.getInt("oitcEventLosses"));
            playerData.setSumoWins(playerDataSelection.getInt("sumoEventWins"));
            playerData.setSumoLosses(playerDataSelection.getInt("sumoEventLosses"));
            playerData.setRematchID(playerDataSelection.getInt("rematchID"));
            playerData.setCornersWins(playerDataSelection.getInt("cornersEventWins"));
            playerData.setCornersLosses(playerDataSelection.getInt("cornersEventLosses"));
            playerData.setRunnerWins(playerDataSelection.getInt("runnerEventwins"));
            playerData.setRunnerLosses(playerDataSelection.getInt("runnerEventLosses"));
            playerData.setParkourWins(playerDataSelection.getInt("parkourEventWins"));
            playerData.setParkourLosses(playerDataSelection.getInt("parkourEventLosses"));
            playerData.setLmsWins(playerDataSelection.getInt("lmsEventWins"));
            playerData.setLmsLosses(playerDataSelection.getInt("lmsEventLosses"));
            playerData.setTournamentWins(playerDataSelection.getInt("tournamentWins"));
            playerData.setTournamentLosses(playerDataSelection.getInt("tournamentLosses"));
        }

        playerData.setPlayerState(PlayerState.SPAWN);

        /*
         * Loading profile stats.
         */

        Document document = Mongo.getInstance().getPlayers().find(Filters.eq("uuid", playerData.getUniqueId().toString())).first();

        if (document == null) {
            for (Kit kit : this.plugin.getKitManager().getKits()) {
                playerData.setElo(kit.getName(), PlayerData.DEFAULT_ELO);
                playerData.setWins(kit.getName(), 0);
                playerData.setLosses(kit.getName(), 0);
            }

            this.saveData(playerData);
            return;
        }

        Document statisticsDocument = (Document) document.get("statistics");

        Document globalDocument = (Document) document.get("global");
        Document kitsDocument = (Document) document.get("kitsDocument");
        Document settingsDocument = (Document) document.get("settings");



        playerData.getSettings().setDuelRequests(settingsDocument.getBoolean("duelRequests"));
        playerData.getSettings().setPartyInvites(settingsDocument.getBoolean("partyInvites"));
        playerData.getSettings().setScoreboardToggled(settingsDocument.getBoolean("scoreboardToggled"));
        playerData.getSettings().setSpectatorsAllowed(settingsDocument.getBoolean("spectatorsAllowed"));
        playerData.getSettings().setPlayerVisibility(settingsDocument.getBoolean("playerVisibility"));


        for (String key : kitsDocument.keySet()) {
            Kit ladder = EruptionPlugin.getInstance().getKitManager().getKit(key);

            if (ladder == null) {
                continue;
            }

            JsonArray kitsArray = EruptionPlugin.PARSER.parse(kitsDocument.getString(key)).getAsJsonArray();
            PlayerKit[] kits = new PlayerKit[4];

            for (JsonElement kitElement : kitsArray) {
                JsonObject kitObject = kitElement.getAsJsonObject();

                PlayerKit kit = new PlayerKit(kitObject.get("name").getAsString(), kitObject.get("index").getAsInt(), InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()), kitObject.get("name").getAsString());

                kit.setContents(InventoryUtil.deserializeInventory(kitObject.get("contents").getAsString()));

                kits[kitObject.get("index").getAsInt()] = kit;
            }

            playerData.getKits().put(ladder.getName(), kits);
        }


        statisticsDocument.keySet().forEach(key -> {
            Document ladderDocument = (Document) statisticsDocument.get(key);

            if (ladderDocument.containsKey("ranked-elo")) {
                playerData.getRankedElo().put(key, ladderDocument.getInteger("ranked-elo"));
            }

            if (ladderDocument.containsKey("ranked-wins")) {
                playerData.getRankedWins().put(key, ladderDocument.getInteger("ranked-wins"));
            }

            if (ladderDocument.containsKey("ranked-losses")) {
                playerData.getRankedLosses().put(key, ladderDocument.getInteger("ranked-losses"));
            }
        });
    }

    public void removePlayerData(UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, ()
                -> {
            PlayerManager.this.saveData(PlayerManager.this.playerData.get(uuid));
            PlayerManager.this.playerData.remove(uuid);
        });
    }

    @SuppressWarnings("unchecked")
    public void saveData(PlayerData playerData) {
        /*
         * Saving profile kits.
         */

        Config config = new Config("/players/" + playerData.getUniqueId().toString(), this.plugin);

        this.plugin.getKitManager().getKits().forEach(kit -> {
            Map<Integer, PlayerKit> playerKits = playerData.getPlayerKits(kit.getName());

            if (playerKits != null) {
                playerKits.forEach((key, value) -> {
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".displayName", value.getDisplayName());
                    config.getConfig().set("playerkits." + kit.getName() + "." + key + ".contents", value.getContents());
                });
                config.getConfig().set("playerdata.elo." + kit.getName(), playerData.getElo(kit.getName()));
                config.getConfig().set("playerdata.losses." + kit.getName(), playerData.getLosses(kit.getName()));
                config.getConfig().set("playerdata.wins." + kit.getName(), playerData.getWins(kit.getName()));
                config.getConfig().set("playerdata.partyelo." + kit.getName(), playerData.getPartyElo(kit.getName()));
            };
            config.getConfig().set("playerdata.oitcEventDeaths", playerData.getOitcDeaths());
            config.getConfig().set("playerdata.oitcEventWins", playerData.getOitcWins());
            config.getConfig().set("playerdata.oitcEventLosses", playerData.getOitcLosses());
            config.getConfig().set("playerdata.sumoEventWins", playerData.getSumoWins());
            config.getConfig().set("playerdata.sumoEventLosses", playerData.getSumoLosses());
            config.getConfig().set("playerdata.cornersEventWins", playerData.getCornersWins());
            config.getConfig().set("playerdata.cornersEventLosses", playerData.getCornersLosses());
            config.getConfig().set("playerdata.runnerEventWins", playerData.getRunnerWins());
            config.getConfig().set("playerdata.runnerEventLosses", playerData.getRunnerLosses());
            config.getConfig().set("playerdata.parkourEventWins", playerData.getParkourWins());
            config.getConfig().set("playerdata.parkourEventLosses", playerData.getParkourLosses());
            config.getConfig().set("playerdata.lmsEventWins", playerData.getLmsWins());
            config.getConfig().set("playerdata.lmsEventLosses", playerData.getLmsLosses());
            config.getConfig().set("playerdata.tournamentWins", playerData.getTournamentWins());
            config.save();
        });

        config.save();

        /*
         * Saving profile stats.
         */

        Document document = new Document();
        Document statisticsDocument = new Document();
        Document globalDocument = new Document();
        Document kitsDocument = new Document();
        Document settingsDocument = new Document();

        playerData.getRankedWins().forEach((key, value) -> {
            Document ladderDocument;

            if (statisticsDocument.containsKey(key)) {
                ladderDocument = (Document) statisticsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("ranked-wins", value);
            statisticsDocument.put(key, ladderDocument);
        });

        playerData.getRankedLosses().forEach((key, value) -> {
            Document ladderDocument;

            if (statisticsDocument.containsKey(key)) {
                ladderDocument = (Document) statisticsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("ranked-losses", value);
            statisticsDocument.put(key, ladderDocument);
        });

        playerData.getRankedElo().forEach((key, value) -> {
            Document ladderDocument;

            if (statisticsDocument.containsKey(key)) {
                ladderDocument = (Document) statisticsDocument.get(key);
            } else {
                ladderDocument = new Document();
            }

            ladderDocument.put("ranked-elo", value);
            statisticsDocument.put(key, ladderDocument);
        });

        for (Map.Entry<String, PlayerKit[]> entry : playerData.getKits().entrySet()) {
            JsonArray kitsArray = new JsonArray();

            for (int i = 0; i < 4; i++) {
                PlayerKit kit = entry.getValue()[i];

                if (kit != null) {
                    JsonObject kitObject = new JsonObject();

                    kitObject.addProperty("index", i);
                    kitObject.addProperty("name", kit.getName());
                    kitObject.addProperty("contents", InventoryUtil.serializeInventory(kit.getContents()));

                    kitsArray.add(kitObject);
                }
            }

            kitsDocument.put(entry.getKey(), kitsArray.toString());
        }


        settingsDocument.put("duelRequests", playerData.getSettings().isDuelRequests());
        settingsDocument.put("partyInvites", playerData.getSettings().isPartyInvites());
        settingsDocument.put("scoreboardToggled", playerData.getSettings().isScoreboardToggled());
        settingsDocument.put("spectatorsAllowed", playerData.getSettings().isSpectatorsAllowed());
        settingsDocument.put("playerVisibility", playerData.getSettings().isPlayerVisibility());

        document.put("uuid", playerData.getUniqueId().toString());
        document.put("statistics", statisticsDocument);
        document.put("global", globalDocument);
        document.put("kitsDocument", kitsDocument);
        document.put("settings", settingsDocument);

        Mongo.getInstance().getPlayers().replaceOne(Filters.eq("uuid", playerData.getUniqueId().toString()), document, new ReplaceOptions().upsert(true));
    }

    public Collection<PlayerData> getAllData() {
        return this.playerData.values();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    public void giveLobbyItems(Player player) {
        boolean inParty = this.plugin.getPartyManager().getParty(player.getUniqueId()) != null;
        boolean inTournament = this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null;
        boolean inEvent = this.plugin.getEventManager().getEventPlaying(player) != null;
        ItemStack[] items = this.plugin.getHotbarManager().getSpawnItems();

        if (inTournament) {
            items = this.plugin.getHotbarManager().getTournamentItems();
        } else if (inEvent) {
            items = this.plugin.getHotbarManager().getEventItems();
        } else if (inParty) {
            items = this.plugin.getHotbarManager().getPartyItems();
        }

        player.getInventory().setContents(items);


        player.updateInventory();
    }

    public void Reset(Player player) {
        PlayerData playerData = this.getPlayerData(player.getUniqueId());

        playerData.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

        this.giveLobbyItems(player);

        if (!player.isOnline()) {
            return;
        }

        SettingsInfo settings = playerData.getSettings();

        if (!settings.isPlayerVisibility()) {
            this.plugin.getServer().getOnlinePlayers().forEach(player::hidePlayer);
        } else {
            this.plugin.getServer().getOnlinePlayers().forEach(player::showPlayer);
        }

    }

    public void sendToSpawnAndReset(Player player) {
        PlayerData playerData = this.getPlayerData(player.getUniqueId());

        playerData.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

        this.giveLobbyItems(player);

        if (!player.isOnline()) {
            return;
        }

        SettingsInfo settings = playerData.getSettings();

        if (!settings.isPlayerVisibility()) {
            this.plugin.getServer().getOnlinePlayers().forEach(player::hidePlayer);
        } else {
            this.plugin.getServer().getOnlinePlayers().forEach(player::showPlayer);
        }

        player.teleport(this.plugin.getSpawnManager().getSpawnLocation().toBukkitLocation());

    }


    public void reset(Player player) {
        PlayerData playerData = this.getPlayerData(player.getUniqueId());

        playerData.setPlayerState(PlayerState.SPAWN);
        PlayerUtil.clearPlayer(player);
        this.plugin.getTimerManager().getTimer(EnderpearlTimer.class).clearCooldown(player.getUniqueId());

        this.giveLobbyItems(player);

        if (!player.isOnline()) {
            return;
        }

        SettingsInfo settings = playerData.getSettings();

        if (!settings.isPlayerVisibility()) {
            this.plugin.getServer().getOnlinePlayers().forEach(player::hidePlayer);
        } else {
            this.plugin.getServer().getOnlinePlayers().forEach(player::showPlayer);
        }
    }

    private void saveConfigPlayerData(PlayerData playerData) {
        Config config = new Config("/players/" + playerData.getUniqueId().toString(), PlayerManager.this.plugin);
        ConfigurationSection playerKitsSection = config.getConfig().getConfigurationSection("playerkits");

        if (playerKitsSection != null) {
            PlayerManager.this.plugin.getKitManager().getKits().forEach(kit -> {
                ConfigurationSection kitSection = playerKitsSection.getConfigurationSection(kit.getName());

                if (kitSection != null) {
                    kitSection.getKeys(false).forEach(kitKey -> {
                        Integer kitIndex = Integer.parseInt(kitKey);
                        String displayName = kitSection.getString(kitKey + ".displayName");

                        ItemStack[] contents = ((List<ItemStack>) kitSection.get(kitKey + ".contents")).toArray(new ItemStack[0]);

                        PlayerKit playerKit = new PlayerKit(kit.getName(), kitIndex, contents, displayName);

                        playerData.addPlayerKit(kitIndex, playerKit);
                    });
                }
            });
        }
    }

    public MongoCursor<Document> getPlayersSortByLadderElo(Kit ladder) {
        final Document sort = new Document();

        sort.put("statistics." + ladder.getName() + ".ranked-elo", -1);

        return Mongo.getInstance().getPlayers().find().sort(sort).limit(10).iterator();
    }
}
