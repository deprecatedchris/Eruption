package me.chris.eruption.menu.setting;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import io.github.nosequel.menu.filling.FillingType;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.scoreboard.ScoreboardState;
import me.chris.eruption.setting.SettingsInfo;
import me.chris.eruption.util.CC;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SettingsMenu extends Menu {

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
                        (playerData.getSettings().isDuelRequests() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!playerData.getSettings().isDuelRequests() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
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
                        (playerData.getSettings().getScoreboardState().name().equals("PING") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fPing",
                        (playerData.getSettings().getScoreboardState().name().equals("ARENA") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fArena",
                        (playerData.getSettings().getScoreboardState().name().equals("LADDER") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fLadder",
                        (playerData.getSettings().getScoreboardState().name().equals("DURATION") ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ") + "&fDuration"

                }).setClickAction(event ->{
                    event.setCancelled(true);

                    switch (playerData.getSettings().getScoreboardState()) {
                        case PING:
                            playerData.getSettings().setScoreboardState(ScoreboardState.PING);
                            break;
                        case ARENA:
                            playerData.getSettings().setScoreboardState(ScoreboardState.ARENA);
                            break;
                        case LADDER:
                            playerData.getSettings().setScoreboardState(ScoreboardState.LADDER);
                            break;
                        default:
                            playerData.getSettings().setScoreboardState(ScoreboardState.DURATION);
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
                        (playerData.getSettings().isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!playerData.getSettings().isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
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
                        (playerData.getSettings().isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!playerData.getSettings().isScoreboardToggled() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
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
                        (playerData.getSettings().isPlayerVisibility() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ "),
                        (!playerData.getSettings().isPlayerVisibility() ? CC.GREEN + CC.BOLD + "■ " : CC.DARK_GRAY + CC.BOLD + "■ ")
                }).setClickAction(event ->{
                    event.setCancelled(true);
                    this.getPlayer().performCommand("tspec");

                    this.updateMenu();
                });
    }
}

