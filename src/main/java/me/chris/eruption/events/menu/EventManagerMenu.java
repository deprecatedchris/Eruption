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
import java.util.List;
import java.util.Map;

public class EventManagerMenu extends Menu {

    private static PracticeEvent event;


    public EventManagerMenu(){
    }

    @Override
    public boolean useNormalSize() {
        return false;
    }

    @Override
    public int size(Player player) {
        return 9*3;
    }

    public EventManagerMenu(String eventName) {
        event = EruptionPlugin.getInstance().getEventManager().getByName(eventName);
    }

    @Override
    public String getTitle(Player player) {
        return CC.translate("&6Event Manager");
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(10, new StartButton());
        buttons.put(11, new StopButton());
        buttons.put(12, new StatusButton());
        buttons.put(13, new ResetCooldownButton());


        return buttons;
    }

    @Override
    public boolean isFill(Player player, Map<Integer, Button> buttons) {
        return true;
    }

    private static class StartButton extends Button {

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.EMERALD_ORE)
                    .name(CC.translate("Start Event"))
                    .lore(Arrays.asList(
                            "",
                            "Click to start the event",
                            ""
                    )).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (event.getState() == EventState.WAITING) {
                event.getCountdownTask().setTimeUntilStart(5);
                player.sendMessage(ChatColor.GREEN + "Event was force started.");
                player.closeInventory();
            } else {
                player.sendMessage(ChatColor.RED + "You can't do this right now.");
                player.closeInventory();
            }
        }
    }

    private static class StopButton extends Button {

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_ORE)
                    .name(CC.translate("Stop Event"))
                    .lore(Arrays.asList(
                            "",
                            "Click to stop the event",
                            ""
                    )).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (event != null) {
                event.end();
                player.sendMessage(ChatColor.RED + "Event was cancelled.");
                player.closeInventory();
            } else {
                player.sendMessage(CC.RED + "You can't do this right now.");
                player.closeInventory();
            }
        }
    }

    private static class StatusButton extends Button {

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.EMPTY_MAP)
                    .name(CC.translate("Event Status"))
                    .lore(Arrays.asList(
                            "",
                            ChatColor.GOLD + "Event: " + ChatColor.WHITE + event.getName(),
                            ChatColor.GOLD + "Host: " + ChatColor.WHITE + (event.getHost() == null ? "Player Left" : event.getHost().getName()),
                            ChatColor.GOLD + "Players: " + ChatColor.WHITE + event.getPlayers().size() + "/" + event.getLimit(),
                            ChatColor.GOLD + "State: " + ChatColor.WHITE + event.getState().name(),
                            ""
                    )).build();
        }
    }

    private static class ResetCooldownButton extends Button {

        @Override
        public String getName(Player player) {
            return null;
        }

        @Override
        public List<String> getDescription(Player player) {
            return null;
        }

        @Override
        public Material getMaterial(Player player) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_BLOCK)
                    .name(CC.translate("Reset global cooldown"))
                    .lore(Arrays.asList(
                            "",
                            "Click to reset",
                            ""
                    )).build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            EruptionPlugin.getInstance().getEventManager().setCooldown(0L);
            player.sendMessage(ChatColor.RED + "Event cooldown was cancelled.");
            player.closeInventory();
        }

    }


}
