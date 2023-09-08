package me.chris.eruption.util.menu.menus;

import me.chris.eruption.util.Callback;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.menu.buttons.BooleanButton;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class Confirm extends Menu {

    private String title;
    private Callback<Boolean> response;
    private boolean closeAfterResponse;
    private Button[] centerButtons;

    public Confirm(String title, Callback<Boolean> response, boolean closeAfter, Button... centerButtons) {
        this.title = title;
        this.response = response;
        this.closeAfterResponse = closeAfter;
        this.centerButtons = centerButtons;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                buttons.put(getSlot(x, y), new BooleanButton(true, this.response));
                buttons.put(getSlot(x, y), new BooleanButton(false, this.response));
            }
        }

        if (centerButtons != null) {
            for (int i = 0; i < centerButtons.length; i++) {
                if (centerButtons[i] != null) {
                    buttons.put(getSlot(4, i), centerButtons[i]);
                }
            }
        }

        return buttons;
    }



    @Override
    public String getTitle(Player player) {
        return title;
    }

}

