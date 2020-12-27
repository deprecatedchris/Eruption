package me.chris.eruption.settings.menu;

import lombok.AllArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.settings.SettingsInfo;
import me.chris.eruption.util.random.ItemBuilder;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;

import java.util.*;

public class SettingsMenu extends Menu {

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.DARK_GRAY + "Settings";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new SettingsMenu.SettingsButton("Toggle duel requests", Material.DIAMOND_SWORD, 0, Arrays.asList("", "&9Toggles duel requests", ""), "toggleduelrequests", "duelRequests"));
        buttons.put(12, new SettingsMenu.SettingsButton("Toggle scoreboard style", Material.PAINTING, 0, Arrays.asList("", "&9Toggles between scoreboard styles", ""), "togglestyle", "altScoreboard"));
        buttons.put(13, new SettingsMenu.SettingsButton("Toggle scoreboard", Material.STRING, 0, Arrays.asList("", "&9Toggles side scoreboard in-match", ""), "togglescoreboard", "scoreboardToggled"));
        buttons.put(14, new SettingsMenu.SettingsButton("Game Spectators", Material.ENDER_PEARL, 0, Arrays.asList("", "&9Disable to disallow match spectators", ""), "togglespectators", "spectatorsAllowed"));
        buttons.put(22, new SettingsMenu.SettingsButton("Toggle profile visibility", Material.EYE_OF_ENDER, 0, Arrays.asList("", "&9Toggles profile spawn visibility", ""), "toggleplayervisibility", "playerVisibility"));

        return buttons;
    }

    @AllArgsConstructor
    public class SettingsButton extends Button {

        private String name;
        private Material material;
        private int durabilty;
        private List<String> lore;
        private String command;
        private String type;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lines = new ArrayList<>(lore);
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            if (type.equalsIgnoreCase("duelRequests")) {
                lines.add((settings.isDuelRequests() ? "&a&l■ " : "&8&l■ ") +  "&fAllow Duels");
                lines.add((!settings.isDuelRequests() ? "&a&l■ " : "&8&l■ ") + "&fDon't Allow Duels");
            } else if (type.equalsIgnoreCase("altScoreboard")) {
                lines.add((settings.isAltScoreboard() ? "&a&l■ " : "&8&l■ ") +  "&fDefault Style");
                lines.add((!settings.isAltScoreboard() ? "&a&l■ " : "&8&l■ ") + "&fAlternative Style");
            } else if (type.equalsIgnoreCase("scoreboardToggled")) {
                lines.add((settings.isScoreboardToggled() ? "&a&l■ " : "&8&l■ ") +  "&fShow scoreboard");
                lines.add((!settings.isScoreboardToggled() ? "&a&l■ " : "&8&l■ ") + "&fHide scoreboard");
            } else if (type.equalsIgnoreCase("spectatorsAllowed")) {
                lines.add((settings.isSpectatorsAllowed() ? "&a&l■ " : "&8&l■ ") +  "&fAllow Spectators");
                lines.add((!settings.isSpectatorsAllowed() ? "&a&l■ " : "&8&l■ ") + "&fDon't Allow Spectators");
            } else if (type.equalsIgnoreCase("playerVisibility")) {
                lines.add((settings.isPlayerVisibility() ? "&a&l■ " : "&8&l■ ") +  "&fShow players at lobby");
                lines.add((!settings.isPlayerVisibility() ? "&a&l■ " : "&8&l■ ") + "&fDon't show players at lobby");
            }

            return new ItemBuilder(material)
                    .name(ChatColor.GREEN + name)
                    .amount(1)
                    .lore(lines)
                    .durability(durabilty)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            player.performCommand(command);
        }
    }

}
