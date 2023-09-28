package me.chris.eruption.command.setting.menu;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.menu.EventManagerMenu;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.scoreboard.ScoreboardState;
import me.chris.eruption.setting.SettingsInfo;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Todo: Add ping range button and Time button
public class SettingsMenu extends Menu {

    public int getSize() {
        return 9*3;
    }



    @Override
    public String getTitle(Player player) {
        return CC.translate("&b&lSettings");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(11, new SettingsMenu.AllowDuelsButton());
        buttons.put(12, new SettingsMenu.SpectatorsButton());
        buttons.put(13, new SettingsMenu.SpawnPlayersButton());
        buttons.put(14, new SettingsMenu.ShowScoreboardButton());
        buttons.put(15, new SettingsMenu.ScoreboardStyleButton());

        return buttons;
    }

    private static class AllowDuelsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            return new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(CC.translate("&cAllow Duels"))
                    .lore(Arrays.asList(
                            "",
                            CC.translate("&7Would you like to allow"),
                            CC.translate("&7players to duel you?"),
                            "",
                            (settings.isDuelRequests() ? CC.GREEN + CC.BOLD + "■ " : CC.RED + CC.BOLD + "■ ")
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.performCommand("tdr");
            }
        }

    private static class SpectatorsButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            return new ItemBuilder(Material.ENDER_PEARL)
                    .name(CC.translate("&bAllow Spectators"))
                    .lore(Arrays.asList(
                            "",
                            CC.translate("&7Would you like to allow"),
                            CC.translate("&7spectators?"),
                            "",
                            (settings.isSpectatorsAllowed() ? CC.GREEN + CC.BOLD + "■ " : CC.RED + CC.BOLD + "■ ")
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.performCommand("tspec");
        }
    }


    private static class SpawnPlayersButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            return new ItemBuilder(Material.EYE_OF_ENDER)
                    .name(CC.translate("&aSpawn Players"))
                    .lore(Arrays.asList(
                            "",
                            CC.translate("&7Would you like to see"),
                            CC.translate("&7players at spawn?"),
                            "",
                            (settings.isPlayerVisibility()  ? CC.GREEN + CC.BOLD + "■ " : CC.RED + CC.BOLD + "■ ")
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.performCommand("tpv");
        }
    }

    private static class ShowScoreboardButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            return new ItemBuilder(Material.PAINTING)
                    .name(CC.translate("&dShow Scoreboard"))
                    .lore(Arrays.asList(
                            "",
                            CC.translate("&7Would you like to see"),
                            CC.translate("&7your scoreboard?"),
                            "",
                            (settings.isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.RED + CC.BOLD + "■ ")
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            player.performCommand("tsb");
        }
    }

    private static class ScoreboardStyleButton extends Button {

        @Override
        public ItemStack getButtonItem(Player player) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            return new ItemBuilder(Material.BLAZE_POWDER)
                    .name(CC.translate("&eScoreboard Style"))
                    .lore(Arrays.asList(
                            CC.translate("&7Switch between which style is"),
                            CC.translate("&7displayed on your scoreboard."),
                            "",
                            (settings.getScoreboardState().name().equals("PING") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fPing",
                            (settings.getScoreboardState().name().equals("ARENA") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fArena",
                            (settings.getScoreboardState().name().equals("LADDER") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fLadder",
                            (settings.getScoreboardState().name().equals("DURATION") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fDuration"
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
            SettingsInfo settings = playerData.getSettings();
            switch (settings.getScoreboardState()) {
                case PING:
                    settings.setScoreboardState(ScoreboardState.PING);
                    break;
                case ARENA:
                    settings.setScoreboardState(ScoreboardState.ARENA);
                    break;
                case LADDER:
                    settings.setScoreboardState(ScoreboardState.LADDER);
                    break;
                default:
                    settings.setScoreboardState(ScoreboardState.DURATION);
                    break;
        }
    }

    }
}

