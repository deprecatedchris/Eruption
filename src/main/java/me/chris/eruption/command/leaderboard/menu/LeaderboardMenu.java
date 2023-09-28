package me.chris.eruption.command.leaderboard.menu;

import com.mongodb.client.MongoCursor;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.other.ItemBuilder;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

//todo: recode this menu.
public class LeaderboardMenu extends Menu {

    private int size = 9*6;
    @Override
    public String getTitle(Player player) {
        return CC.translate("&6Leaderboards");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        for(Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()){
            if(kit.isRanked()) {
                buttons.put(i++, new LeaderboardButton(kit));
            }
        }
        return buttons;
    }


    private static class LeaderboardButton extends Button {

        Kit kit;

        public LeaderboardButton(Kit kit) {
            this.kit = kit;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = new ArrayList<>();
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
                    } catch (Exception ignored) {

                    }
                }
            }

            lore.add("");

        return new ItemBuilder(kit.getIcon())
                    .name(ChatColor.RED + kit.getName() + ChatColor.GRAY + " (Top 10)")
                    .lore(CC.translate(lore))
                    .build();
        }
    }
}
