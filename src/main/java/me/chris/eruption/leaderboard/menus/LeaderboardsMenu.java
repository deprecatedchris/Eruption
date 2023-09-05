package me.chris.eruption.leaderboard.menus;

import com.mongodb.client.MongoCursor;
import lombok.AllArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.util.random.ItemBuilder;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LeaderboardsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Leaderboards";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        AtomicInteger value = new AtomicInteger(0);

        for (Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()) {
            if (kit.isRanked()) {
                buttons.put(value.getAndIncrement(), new LadderButton(kit));
            }
        }
        return buttons;
    }

    @AllArgsConstructor
    public static class LadderButton extends Button {

        Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
            int lineNum = 0;

            lore.add("");

            try (MongoCursor<Document> iterator = EruptionPlugin.getInstance().getPlayerManager().getPlayersSortByLadderElo(kit)) {
                while (iterator.hasNext()) {
                    lineNum++;
                    try {
                        Document document = iterator.next();
                        UUID uuid = UUID.fromString(document.getString("uuid"));

                        if (!document.containsKey("statistics")) {
                            continue;
                        }

                        Document statistics = (Document) document.get("statistics");
                        Document ladder = (Document) statistics.get(this.kit.getName());
                        int amount = ladder.getInteger("ranked-elo");

                        OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

                        String text = ChatColor.RED.toString()  +  lineNum + ". " + ChatColor.WHITE + target.getName() + ChatColor.GRAY + " - " + ChatColor.RED + amount;

                        lore.add(text);
                    } catch (Exception ignored) {
                        //exception ignored anyways
                    }
                }
            }

            lore.add("");

            return new ItemBuilder(kit.getIcon().getType())
                    .name(ChatColor.RED.toString() + kit.getName() + ChatColor.GRAY + " (Top 10)")
                    .lore(lore)
                    .durability(kit.getIcon().getDurability())
                    .build();
        }
    }

}
