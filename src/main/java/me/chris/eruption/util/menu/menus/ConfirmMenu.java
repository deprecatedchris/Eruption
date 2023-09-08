package me.chris.eruption.util.menu.menus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.Callback;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.menu.buttons.BooleanButton;
import me.chris.eruption.util.menu.buttons.GlassPaneButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ConfirmMenu extends Menu {

    private String title;
    @Getter private Callback<Boolean> response;

    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap();


        buttons.put(0, new BooleanButton(true, this.response));
        buttons.put(1, new BooleanButton(true, this.response));
        buttons.put(2, new BooleanButton(true, this.response));
        buttons.put(3, new BooleanButton(true, this.response));
        buttons.put(4, new GlassPaneButton((short) 7));
        buttons.put(5, new BooleanButton(false, this.response));
        buttons.put(6, new BooleanButton(false, this.response));
        buttons.put(7, new BooleanButton(false, this.response));
        buttons.put(8, new BooleanButton(false, this.response));

        player.sendMessage(CC.translate("&e&l(!)&e Please select '&a&lCONFIRM&e' or &e'&c&lDENY&e' in " + title + " GUI."));

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return title;
    }
}
