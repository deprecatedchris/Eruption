package me.chris.eruption.command.menu;

import io.github.nosequel.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

public class TestPaginatedMenu extends PaginatedMenu {

    public TestPaginatedMenu(Player player) {
        super(player, "Test Paginated Menu", 27);
    }

    @Override
    public void tick() {

    }
}
