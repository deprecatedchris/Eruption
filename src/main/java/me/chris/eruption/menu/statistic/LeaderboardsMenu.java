package me.chris.eruption.menu.statistic;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.filling.FillingType;
import me.chris.eruption.util.CC;
import org.bukkit.entity.Player;


//Todo: Finish this shit aswell.
public class LeaderboardsMenu extends Menu {
    public LeaderboardsMenu(Player player) { super(player, CC.translate("&bLeaderboards"), 35);
        this.addFiller(FillingType.EMPTY_SLOTS);
    }

}
