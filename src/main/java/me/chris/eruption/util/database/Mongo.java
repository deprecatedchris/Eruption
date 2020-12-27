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
        if (instance != null) {
            throw new RuntimeException("The database database has already been instantiated.");
        }

        instance = this;
        Plugin plugin = EruptionPlugin.getPlugin(EruptionPlugin.class);
        FileConfiguration config = EruptionPlugin.getInstance().getMainConfig().getConfig();

        this.client = new MongoClient(new MongoClientURI(config.getString("Mongo.URL")));
        this.database = this.client.getDatabase(config.getString("Mongo.Database"));
        this.players = this.database.getCollection("players");
    }

}
