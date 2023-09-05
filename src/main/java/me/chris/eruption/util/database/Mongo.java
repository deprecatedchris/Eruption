package me.chris.eruption.util.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.config.Config;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

@Getter
public class Mongo {

    @Getter
    private static Mongo instance;

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> players;

    public Mongo() {
        instance = this;
        FileConfiguration config = EruptionPlugin.getInstance().getMainConfig().getConfig();
        this.client = new MongoClient(new MongoClientURI(EruptionPlugin.getInstance().getConfig().getString("mongodb.url")));
        this.database = client.getDatabase(EruptionPlugin.getInstance().getConfig().getString("mongodb.database"));
        this.players = this.database.getCollection("players");

        EruptionPlugin.getInstance().logConsole("&a[Mongo] &eSetup the MongoDB Database!");
    }

}
