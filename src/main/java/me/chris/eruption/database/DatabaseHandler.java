package me.chris.eruption.database;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.Collections;

@Getter
public class DatabaseHandler {

    @Getter
    private static DatabaseHandler instance;

    private final MongoCollection<Document> players;

    public DatabaseHandler(String host, int port, boolean authentication, String username, String password, String database) {
        instance = this;

        final MongoClientSettings.Builder clientSettingsBuilder = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(Collections.singletonList(new ServerAddress(host, port))));

        if (authentication) {
            final MongoCredential credential = MongoCredential.createCredential(
                    username,
                    database,
                    password.toCharArray()
            );

            clientSettingsBuilder.credential(credential);
        }

        final MongoDatabase mongoDatabase = MongoClients.create(clientSettingsBuilder.build())
                .getDatabase(database);

        this.players = mongoDatabase.getCollection("players");
    }
}
