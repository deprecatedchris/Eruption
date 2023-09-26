package me.chris.eruption.command.setting.menu;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.scoreboard.ScoreboardState;
import me.chris.eruption.setting.SettingsInfo;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

//Todo: recode this menu and add ping range button
public class SettingsMenu extends Menu {
    private int size = 9*3;

    @Override
    public String getTitle(Player player) {
        return CC.translate("&b&lSettings");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        return buttons;
    }

    /*
    public SettingsMenu(Player player) {
        super(player, CC.translate("&aSettings"), 26);
        this.addFiller(FillingType.BORDER);
        this.setFillerType(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getData()));
    }

    @Override
    public void tick() {
        final PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
        SettingsInfo settings = playerData.getSettings();

        this.buttons[11] = new Button(Material.DIAMOND_SWORD)
                .setDisplayName(CC.translate("&cAllow Duels"))
                .setLore(new String[]{
                        "",
                        CC.translate("&7Would you like to allow"),
                        CC.translate("&7players to duel you?"),
                        "",
                        (settings.isDuelRequests() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!settings.isDuelRequests() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
                }).setClickAction(event ->{
                    event.setCancelled(true);
                    this.getPlayer().performCommand("tdr");

                    this.updateMenu();
                });

        this.buttons[12] = new Button(Material.BLAZE_POWDER)
                .setDisplayName(CC.translate("&eScoreboard Style"))
                .setLore(new String[]{
                        "",
                        CC.translate("&7Switch between which style is"),
                        CC.translate("&7displayed on your scoreboard."),
                        "",
                        (settings.getScoreboardState().name().equals("PING") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fPing",
                        (settings.getScoreboardState().name().equals("ARENA") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fArena",
                        (settings.getScoreboardState().name().equals("LADDER") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fLadder",
                        (settings.getScoreboardState().name().equals("DURATION") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fDuration"

                }).setClickAction(event ->{
                    event.setCancelled(true);

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
                    this.updateMenu();
                });

        this.buttons[13] = new Button(Material.PAINTING)
                .setDisplayName(CC.translate("&dShow Scoreboard"))
                .setLore(new String[]{
                        "",
                        CC.translate("&7Would you like to see"),
                        CC.translate("&7your scoreboard?"),
                        "",
                        (settings.isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!settings.isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
                }).setClickAction(event ->{
                    event.setCancelled(true);
                    this.getPlayer().performCommand("tsb");

                    this.updateMenu();
                });

        this.buttons[14] = new Button(Material.ENDER_PEARL)
                .setDisplayName(CC.translate("&bAllow Spectators"))
                .setLore(new String[]{
                        "",
                        CC.translate("&7Would you like to allow"),
                        CC.translate("&7players to spectate?"),
                        "",
                        (settings.isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!settings.isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
                }).setClickAction(event ->{
                    event.setCancelled(true);
                    this.getPlayer().performCommand("tspec");

                    this.updateMenu();
                });

        this.buttons[15] = new Button(Material.EYE_OF_ENDER)
                .setDisplayName(CC.translate("&aSpawn Players"))
                .setLore(new String[]{
                        "",
                        CC.translate("&7Would you like to see"),
                        CC.translate("&7players at spawn?"),
                        "",
                        (settings.isPlayerVisibility() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!settings.isPlayerVisibility() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
                }).setClickAction(event ->{
                    event.setCancelled(true);
                    this.getPlayer().performCommand("tspec");

                    this.updateMenu();
                });
    }
    */

}

