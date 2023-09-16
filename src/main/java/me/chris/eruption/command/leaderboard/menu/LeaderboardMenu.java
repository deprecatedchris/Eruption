package me.chris.eruption.command.leaderboard.menu;

import com.mongodb.client.MongoCursor;
import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaderboardMenu extends Menu {

    public LeaderboardMenu(Player player) {
        super(player, "Leaderboards", 54);
    }

    @Override
    public void tick() {
        int i = 0;
        final List<String> lore = new ArrayList<>();

        for (Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()) {
            if (kit.isRanked()) {
                lore.add("");

                try (MongoCursor<Document> iterator = EruptionPlugin.getInstance().getPlayerManager().getPlayersSortByLadderElo(kit)) {
                    while (iterator.hasNext()) {
                        try {
                            final Document document = iterator.next();
                            final UUID uuid = UUID.fromString(document.getString("uuid"));

                            if (!document.containsKey("statistics")) {
                                continue;
                            }

                            final Document statistics = (Document) document.get("statistics");
                            final Document ladder = (Document) statistics.get(kit.getName());
                            int amount = ladder.getInteger("ranked-elo");

                            final OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

                            final String text = ChatColor.RED.toString()  + ChatColor.WHITE + target.getName() + ChatColor.GRAY + " - " + ChatColor.RED + amount;

                            lore.add(text);
                        } catch (Exception ignored) {}
                    }
                }

                lore.add("");

                this.buttons[i] = new Button(kit.getIcon().getType())
                        .setDisplayName(ChatColor.RED + kit.getName() + ChatColor.GRAY + " (Top 10)")
// idk this is broke?              .setLore(lore)
                        .setClickAction(event -> event.setCancelled(true));
            }
        }
    }
}
