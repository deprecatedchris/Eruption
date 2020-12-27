package me.chris.eruption.setup.managers;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.util.random.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SpawnManager {
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    private LocationUtil spawnLocation;
    private LocationUtil spawnMin;
    private LocationUtil spawnMax;

    private LocationUtil editorLocation;
    private LocationUtil editorMin;
    private LocationUtil editorMax;

    private LocationUtil sumoLocation;
    private LocationUtil sumoFirst;
    private LocationUtil sumoSecond;
    private LocationUtil sumoMin;
    private LocationUtil sumoMax;

    private LocationUtil lmsLocation;
    private LocationUtil lmsMin;
    private LocationUtil lmsMax;
    private List<LocationUtil> lmsLocations;

    private LocationUtil potionLocation;
    private LocationUtil potionMin;
    private LocationUtil potionMax;
    private List<LocationUtil> potionLocations;

    private LocationUtil cornersLocation;
    private LocationUtil cornersMin;
    private LocationUtil cornersMax;

    private LocationUtil runnerLocation;
    private List<LocationUtil> runnerLocations;

    private LocationUtil oitcLocation;
    private List<LocationUtil> oitcSpawnpoints;
    private LocationUtil oitcMin;
    private LocationUtil oitcMax;

    private LocationUtil parkourLocation;
    private LocationUtil parkourGameLocation;
    private LocationUtil parkourMin;
    private LocationUtil parkourMax;

    private LocationUtil redroverLocation;
    private LocationUtil redroverFirst;
    private LocationUtil redroverSecond;
    private LocationUtil redroverMin;
    private LocationUtil redroverMax;

    public SpawnManager() {
        this.oitcSpawnpoints = new ArrayList<>();
        this.loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();

        if (config.contains("spawnLocation")) {
            this.spawnLocation = LocationUtil.stringToLocation(config.getString("spawnLocation"));
            this.spawnMin = LocationUtil.stringToLocation(config.getString("spawnMin"));
            this.spawnMax = LocationUtil.stringToLocation(config.getString("spawnMax"));
        }

        if (config.contains("cornersLocation")) {
            this.cornersLocation = LocationUtil.stringToLocation(config.getString("cornersLocation"));
            this.cornersMin = LocationUtil.stringToLocation(config.getString("cornersMin"));
            this.cornersMax = LocationUtil.stringToLocation(config.getString("cornersMax"));
        }

        if (config.contains("sumoLocation")) {
            this.sumoLocation = LocationUtil.stringToLocation(config.getString("sumoLocation"));
            this.sumoMin = LocationUtil.stringToLocation(config.getString("sumoMin"));
            this.sumoMax = LocationUtil.stringToLocation(config.getString("sumoMax"));
            this.sumoFirst = LocationUtil.stringToLocation(config.getString("sumoFirst"));
            this.sumoSecond = LocationUtil.stringToLocation(config.getString("sumoSecond"));
        }

        if (config.contains("oitcLocation")) {
            this.oitcLocation = LocationUtil.stringToLocation(config.getString("oitcLocation"));
            this.oitcMin = LocationUtil.stringToLocation(config.getString("oitcMin"));
            this.oitcMax = LocationUtil.stringToLocation(config.getString("oitcMax"));

            for (String spawnpoint : config.getStringList("oitcSpawnpoints")) {
                this.oitcSpawnpoints.add(LocationUtil.stringToLocation(spawnpoint));
            }
        }

        if (config.contains("runnerLocation")) {
            this.runnerLocation = LocationUtil.stringToLocation(config.getString("runnerLocation"));

            for (String spawnpoint : config.getStringList("runnerLocations")) {
                this.runnerLocations.add(LocationUtil.stringToLocation(spawnpoint));
            }
        }

        if (config.contains("lmsLocation")) {
            this.lmsLocation = LocationUtil.stringToLocation(config.getString("lmsLocation"));
            this.lmsMin = LocationUtil.stringToLocation(config.getString("lmsMin"));
            this.lmsMax = LocationUtil.stringToLocation(config.getString("lmsMax"));

            for (String spawnpoint : config.getStringList("lmsLocations")) {
                this.lmsLocations.add(LocationUtil.stringToLocation(spawnpoint));
            }
        }

        if (config.contains("potionLocation")) {
            this.potionLocation = LocationUtil.stringToLocation(config.getString("potionLocation"));
            this.potionMin = LocationUtil.stringToLocation(config.getString("potionMin"));
            this.potionMax = LocationUtil.stringToLocation(config.getString("potionMax"));

            for (String spawnpoint : config.getStringList("potionLocations")) {
                this.potionLocations.add(LocationUtil.stringToLocation(spawnpoint));
            }
        }

        if (config.contains("redroverLocation")) {
            this.redroverLocation = LocationUtil.stringToLocation(config.getString("redroverLocation"));
            this.redroverMin = LocationUtil.stringToLocation(config.getString("redroverMin"));
            this.redroverMax = LocationUtil.stringToLocation(config.getString("redroverMax"));
            this.redroverFirst = LocationUtil.stringToLocation(config.getString("redroverFirst"));
            this.redroverSecond = LocationUtil.stringToLocation(config.getString("redroverSecond"));
        }

        if (config.contains("parkourLocation")) {
            this.parkourLocation = LocationUtil.stringToLocation(config.getString("parkourLocation"));
            this.parkourGameLocation = LocationUtil.stringToLocation(config.getString("parkourGameLocation"));
            this.parkourMin = LocationUtil.stringToLocation(config.getString("parkourMin"));
            this.parkourMax = LocationUtil.stringToLocation(config.getString("parkourMax"));
        }
    }

    public void saveConfig() {
        FileConfiguration config = this.plugin.getMainConfig().getConfig();
        config.set("spawnLocation", LocationUtil.locationToString(this.spawnLocation));
        config.set("spawnMin", LocationUtil.locationToString(this.spawnMin));
        config.set("spawnMax", LocationUtil.locationToString(this.spawnMax));

        config.set("cornersLocation", LocationUtil.locationToString(this.cornersLocation));
        config.set("cornersMin", LocationUtil.locationToString(this.cornersMin));
        config.set("cornersMax", LocationUtil.locationToString(this.cornersMax));

        config.set("sumoLocation", LocationUtil.locationToString(this.sumoLocation));
        config.set("sumoMin", LocationUtil.locationToString(this.sumoMin));
        config.set("sumoMax", LocationUtil.locationToString(this.sumoMax));
        config.set("sumoFirst", LocationUtil.locationToString(this.sumoFirst));
        config.set("sumoSecond", LocationUtil.locationToString(this.sumoSecond));

        config.set("oitcLocation", LocationUtil.locationToString(this.oitcLocation));
        config.set("oitcMin", LocationUtil.locationToString(this.oitcMin));
        config.set("oitcMax", LocationUtil.locationToString(this.oitcMax));
        config.set("oitcSpawnpoints", this.fromLocations(this.oitcSpawnpoints));

        config.set("runnerLocations", this.fromLocations(this.runnerLocations));

        config.set("lmsLocation", LocationUtil.locationToString(this.lmsLocation));
        config.set("lmsMin", LocationUtil.locationToString(this.lmsMin));
        config.set("lmsMax", LocationUtil.locationToString(this.lmsMax));
        config.set("lmsLocations", this.fromLocations(this.lmsLocations));

        config.set("potionLocation", LocationUtil.locationToString(this.potionLocation));
        config.set("potionMin", LocationUtil.locationToString(this.potionMin));
        config.set("potionMax", LocationUtil.locationToString(this.potionMax));
        config.set("potionLocations", this.fromLocations(this.potionLocations));

        config.set("parkourLocation", LocationUtil.locationToString(this.parkourLocation));
        config.set("parkourGameLocation", LocationUtil.locationToString(this.parkourGameLocation));
        config.set("parkourMin", LocationUtil.locationToString(this.parkourMin));
        config.set("parkourMax", LocationUtil.locationToString(this.parkourMax));

        config.set("redroverLocation", LocationUtil.locationToString(this.redroverLocation));
        config.set("redroverMin", LocationUtil.locationToString(this.redroverMin));
        config.set("redroverMax", LocationUtil.locationToString(this.redroverMax));
        config.set("redroverFirst", LocationUtil.locationToString(this.redroverFirst));
        config.set("redroverSecond", LocationUtil.locationToString(this.redroverSecond));
        this.plugin.getMainConfig().save();
    }

    public List<String> fromLocations(List<LocationUtil> locations) {

        List<String> toReturn = new ArrayList<>();
        for (LocationUtil location : locations) {
            toReturn.add(LocationUtil.locationToString(location));
        }

        return toReturn;
    }
}
