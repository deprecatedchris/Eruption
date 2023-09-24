package me.chris.eruption.events.menu;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.EventState;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventHostMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return CC.translate("&6&lSelect an event to host");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int size=EruptionPlugin.getInstance().getEventManager().getEvents().size();
        for (PracticeEvent event : EruptionPlugin.getInstance().getEventManager().getEvents().values()) {
            buttons.put(size, new EventButton(event));
            size++;
        }

        return buttons;
    }

    private static class EventButton extends Button {

        PracticeEvent practiceEvent;

        public EventButton(PracticeEvent event) {
            this.practiceEvent = event;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.EMERALD)
                    .name(CC.translate("&6Click to host" + practiceEvent.getName()))
                    .lore(Arrays.asList(
                            "",
                            CC.translate("&aClick to host"),
                            ""
                    )).build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {

            if (System.currentTimeMillis() < EruptionPlugin.getInstance().getEventManager().getCooldown()) {
                player.sendMessage(ChatColor.RED + "There is a cooldown. Event can't start at this moment.");
                return;
            }

            PracticeEvent event = EruptionPlugin.getInstance().getEventManager().getByName(practiceEvent.getName());
            if (event.getState() != EventState.UNANNOUNCED) {
                player.sendMessage(ChatColor.RED + "There is another " + event.getName() + " event being hosted.");
                return;
            }

            boolean eventBeingHosted = EruptionPlugin.getInstance().getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
            if (eventBeingHosted) {
                player.sendMessage(ChatColor.RED + "There is currently an active event.");
                return;
            }

            new SelectEventArenaMenu(practiceEvent).openMenu(player);
            player.closeInventory();

        }
    }


}
