package me.chris.eruption.command.menu;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import me.chris.eruption.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestMenu extends Menu {

    public TestMenu(Player player) {
        super(player, "Test Menu", 9);
    }

    @Override
    public void tick() {
        this.buttons[4] = new Button(Material.DIAMOND_SWORD)
                .setDisplayName(CC.translate("&aTest Button"))
                .setLore(new String[] {
                        CC.translate("&7This is a test button.")
                }).setClickAction(event -> {
                    new TestPaginatedMenu(this.getPlayer()).updateMenu();
                    event.setCancelled(true);
                });
    }
}
