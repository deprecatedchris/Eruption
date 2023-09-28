package me.chris.eruption.events.menu;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.arena.arena.type.ArenaType;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectEventArenaMenu extends Menu {

    static PracticeEvent event;

    public SelectEventArenaMenu(PracticeEvent event) {
        SelectEventArenaMenu.event = event;
    }


    @Override
    public String getTitle(Player player) {
        return CC.translate("&6Select an arena for the event");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Button BLACK_PANE = Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, " ");
        Map<Integer, Button> buttons = new HashMap<>();

        int x = 1;
        int y = 0;

        for (Arena arena : EruptionPlugin.getInstance().getArenaManager().getArenas().values()) {

            if (arena.isEvent() && arena.getArenaType() == ArenaType.valueOf(event.getName().toLowerCase())) {
                buttons.put(getSlot(x++, y), new SelectArenaButton(arena));
            }

            if (x == 8) {
                y++;
                x = 1;
            }
        }

        for (int q=0; q<getSize(); q++) {
            buttons.putIfAbsent(q, BLACK_PANE);
        }

        return buttons;
    }
    private static class SelectArenaButton extends Button {

        Arena arena;

        public SelectArenaButton(Arena arena) {
            this.arena = arena;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.MAP)
                    .name(arena.getName())
                    .lore(Arrays.asList(
                            "",
                            "Click to select",
                            ""
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            EruptionPlugin.getInstance().getEventManager().hostEvent(EruptionPlugin.getInstance().getEventManager().getByName(event.getName()), player, arena);
            player.closeInventory();
        }
    }

    public int size(Player player) {
        return 9*5;
    }

}
