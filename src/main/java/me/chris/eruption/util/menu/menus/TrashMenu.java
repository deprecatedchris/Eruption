package me.chris.eruption.util.menu.menus;

import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TrashMenu extends Menu {

    public TrashMenu(){
        setNoncancellingInventory(true);
//        setAutoUpdate(true);

    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("Trash");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> map = new HashMap<>();
        return map;
    }
}
