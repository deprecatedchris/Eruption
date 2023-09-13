package me.chris.eruption.menu.statistic;

import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.filling.FillingType;
import me.chris.eruption.util.CC;
import org.bukkit.entity.Player;

//Todo: Finish this. Im lost rn LOL
public class StatisticsMenu extends Menu {
    public StatisticsMenu(Player player, Player target) { super(player, CC.translate("&eStatistics - ") + target.getDisplayName(), 35);
        this.addFiller(FillingType.EMPTY_SLOTS);
    }
}
