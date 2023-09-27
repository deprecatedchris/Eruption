package me.chris.eruption.command.kit;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.kit.Flag;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.ItemUtil;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.*;
import me.vaperion.blade.exception.BladeExitMessage;
import me.vaperion.blade.exception.BladeUsageMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KitCommands {
    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"kit create", "kit add"})
    @Usage("/kit create <name>")
    @Permission("eruption.kit")
    @Description("Create a kit.")
    public static void createKit(@Sender Player player, String name) throws BladeExitMessage {
        Kit kit = plugin.getKitManager().getKit(name);

        if (kit == null) {
            plugin.getKitManager().createKit(name);
            player.sendMessage(CC.GREEN + "Successfully created kit " + name + ".");
        } else {
            throw new BladeExitMessage("A kit with that name already exists."); }
    }

    @Command({"kit delete", "kit remove"})
    @Usage("/kit delete <kit>")
    @Permission("eruption.kit")
    @Description("Delete a kit.")
    public static void deleteKit(@Sender Player player, Kit kit) throws BladeExitMessage {
            plugin.getKitManager().deleteKit(kit.getName());
            player.sendMessage(CC.GREEN + "Successfully deleted kit " + kit.getName() + ".");
        }

    @Command({"kit toggle"})
    @Usage("/kit toggle <kit>")
    @Permission("eruption.kit")
    @Description("Toggle a kit.")
    public static void toggleKit(@Sender Player player, Kit kit) throws BladeExitMessage {

        kit.setEnabled(!kit.isEnabled());
        player.sendMessage(kit.isEnabled() ? CC.GREEN + "Successfully enabled kit " + kit.getName() + "." : CC.RED + "Successfully disabled kit " + kit.getName() + ".");
    }

    @Command({"kit flag"})
    @Usage("/kit flag <kit> <flag>")
    @Permission("eruption.kit")
    @Description("Add a flag to a kit.")
    public static void flagKit(@Sender Player player, Kit kit, String[] args) throws BladeExitMessage {
        if (kit != null) {
            if (args.length < 3) {
                throw new BladeUsageMessage();
            }

            if (args.length == 3) {
                final String flagString = args[2];

                try {
                    final Flag flag = Flag.valueOf(flagString);
                    kit.setFlag(flag);

                    player.sendMessage(CC.GREEN + "Updated the flag to " + flag.name());
                } catch (Exception ignored) {
                    player.sendMessage(CC.RED + "Invalid flag, here are some below:");
                    player.sendMessage(CC.GREEN + Arrays.stream(Flag.values()).map(Flag::name).collect(Collectors.joining(ChatColor.WHITE + ", " + ChatColor.GREEN)));
                }
            }
        } else {
            throw new BladeExitMessage("A kit with that name does not exists."); }
    }

    @Command({"kit icon"})
    @Usage("/kit icon <kit>")
    @Permission("eruption.kit")
    @Description("Set icon for a kit.")
    public static void iconKit(@Sender Player player, Kit kit) throws BladeExitMessage {

        if (player.getItemInHand().getType() != Material.AIR) {
            ItemStack icon = ItemUtil.renameItem(player.getItemInHand().clone(), CC.GREEN + kit.getName());
            kit.setIcon(icon);
            player.sendMessage(CC.GREEN + "Successfully set icon for kit " + kit.getName() + ".");

        } else {
            player.sendMessage(CC.RED + "You must be holding an item to set the kit icon!"); }
    }

    @Command({"kit setinv"})
    @Usage("/kit setinv <kit>")
    @Permission("eruption.kit")
    @Description("Set inventory of a kit.")
    public static void setInvKit(@Sender Player player, Kit kit) throws BladeExitMessage {

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(CC.RED + "You can't set item contents in creative mode!");
        } else {
            player.updateInventory();
            kit.setContents(player.getInventory().getContents());
            kit.setArmor(player.getInventory().getArmorContents());
            player.sendMessage(CC.GREEN + "Successfully set kit contents for " + kit.getName() + "."); }
    }

    @Command({"kit getinv"})
    @Usage("/kit getinv <kit>")
    @Permission("eruption.kit")
    @Description("Get inventory of a kit.")
    public static void getInvKit(@Sender Player player, Kit kit) throws BladeExitMessage {
        player.getInventory().setContents(kit.getContents());
        player.getInventory().setArmorContents(kit.getArmor());
        player.updateInventory();
        player.sendMessage(CC.GREEN + "Successfully retrieved kit contents from " + kit.getName() + ".");

    }

    @Command({"kit setedit"})
    @Usage("/kit setedit <kit>")
    @Permission("eruption.kit")
    @Description("Set edit inventory of a kit.")
    public static void setEditKit(@Sender Player player, Kit kit) throws BladeExitMessage {

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(CC.RED + "You can't set item contents in creative mode!");
        } else {
            player.updateInventory();
            kit.setKitEditContents(player.getInventory().getContents());
            player.sendMessage(CC.GREEN + "Successfully set edit kit contents for " + kit.getName() + ".");
        }
    }

    @Command({"kit getedit"})
    @Usage("/kit getedit <kit>")
    @Permission("eruption.kit")
    @Description("Get edit inventory of a kit.")
    public static void getEditKit(@Sender Player player, Kit kit) throws BladeExitMessage {

        player.getInventory().setContents(kit.getKitEditContents());
        player.updateInventory();
        player.sendMessage(CC.GREEN + "Successfully retrieved edit kit contents from " + kit.getName() + ".");
    }

    @Command({"kit ranked"})
    @Usage("/kit ranked <kit>")
    @Permission("eruption.kit")
    @Description("Toggle ranked for a kit.")
    public static void rankedKit(@Sender Player player, Kit kit) throws BladeExitMessage {
        kit.setRanked(!kit.isRanked());
        player.sendMessage(kit.isRanked() ? CC.GREEN + "Successfully enabled ranked mode for kit " + kit.getName() + "." : CC.RED + "Successfully disabled ranked mode for kit " + kit.getName() + ".");
    }

    @Command({"kit name"})
    @Usage("/kit name <kit>")
    @Permission("eruption.kit")
    @Description("Set display name of a kit.")
    public static void nameKit(@Sender Player player, String[] args) throws BladeExitMessage {
        Kit kit = plugin.getKitManager().getKit(args[1]);
        String displayName = StringUtils.join(args, ' ', 2, args.length);
        kit.setDisplayName(CC.translate(displayName));
        player.sendMessage(CC.GREEN + "Successfully set the display name as \"" + CC.translate(displayName) + CC.GREEN + "\"");
    }

    @Command({"kit exclude"})
    @Usage("/kit exclude <kit> <arena>")
    @Permission("eruption.kit")
    @Description("Exclude all kits from an arena except selected kit.")
    public static void excludeKit(@Sender Player player, Kit kit, Arena arena) throws BladeExitMessage {
        kit.excludeArena(arena.getName());
        player.sendMessage(kit.getExcludedArenas().contains(arena.getName()) ?
                CC.GREEN + "Arena " + arena.getName() + " is now excluded from the kit " + kit.getName() + "." : CC.GREEN + "Arena " + arena.getName() + " is no longer excluded from the kit " + kit.getName() + ".");

            }

    @Command({"kit excludeall"})
    @Usage("/kit excludeall <kit> <arena>")
    @Permission("eruption.kit")
    @Description("Exclude all kits from an arena except selected kit.")
    public static void excludeAllKit(@Sender Player player, Kit kit, Arena arena) throws BladeExitMessage {

        for(Kit loopKit : plugin.getKitManager().getKits()){
            if(!loopKit.equals(kit)){
                player.performCommand("kit excludearena " + loopKit.getName() + " " + arena.getName());
            }
        }
    }

    @Command({"kit whitelist"})
    @Usage("/kit whitelist <kit> <arena>")
    @Permission("eruption.kit")
    @Description("Toggle ranked for a kit.")
    public static void rankedKit(@Sender Player player, Kit kit, Arena arena) throws BladeExitMessage {
        kit.whitelistArena(arena.getName());
        player.sendMessage(kit.getArenaWhiteList().contains(arena.getName()) ?
                ChatColor.GREEN + "Arena " + arena.getName() + " is now whitelisted to kit " + kit.getName() + "." : ChatColor.GREEN + "Arena " + arena.getName() + " is no longer whitelisted to kit " + kit.getName() + ".");
    }

}
