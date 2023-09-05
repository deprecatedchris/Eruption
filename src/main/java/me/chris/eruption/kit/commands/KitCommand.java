package me.chris.eruption.kit.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Flag;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.util.random.ItemUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KitCommand extends Command {
    private static final String NO_KIT = ChatColor.RED + "That kit doesn't exist!";
    private static final String NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public KitCommand() {
        super("kit");
        this.setDescription("Kit command.");
        this.setUsage(ChatColor.RED + "Usage: /kit <subcommand> [args]");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("practice." + this.getName().toLowerCase() + ".command")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return true;
        }

        Kit kit = this.plugin.getKitManager().getKit(args[1]);

        switch (args[0].toLowerCase()) {
            case "create":
                if (kit == null) {
                    this.plugin.getKitManager().createKit(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully created kit " + args[1] + ".");
                } else {
                    sender.sendMessage(ChatColor.RED + "That kit already exists!");
                }
                break;
            case "delete":
                if (kit != null) {
                    this.plugin.getKitManager().deleteKit(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "Successfully deleted kit " + args[1] + ".");
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "disable":
            case "enable":
                if (kit != null) {
                    kit.setEnabled(!kit.isEnabled());
                    sender.sendMessage(kit.isEnabled() ? ChatColor.GREEN + "Successfully enabled kit " + args[1] + "." :
                            ChatColor.RED + "Successfully disabled kit " + args[1] + ".");
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "flag":
                if (kit != null) {
                    if (args.length < 3) {
                        sender.sendMessage(this.usageMessage);
                    }
                    if (args.length == 3) {
                        final String flagString = args[2];

                        try {
                            final Flag flag = Flag.valueOf(flagString);

                            kit.setFlag(flag);

                            sender.sendMessage(ChatColor.GREEN + "Updated the flag to " + flag.name());
                        } catch (Exception ignored) {
                            sender.sendMessage(ChatColor.RED + "Invalid flag, here're some below:");
                            sender.sendMessage(ChatColor.GREEN + Arrays.stream(Flag.values()).map(Flag::name).collect(Collectors.joining(ChatColor.WHITE + ", " + ChatColor.GREEN)));
                        }
                    }
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "queue":
                if (kit != null) {
                    if (args.length < 3) {
                        sender.sendMessage(this.usageMessage);
                        return true;
                    }
                    if (args.length == 3) {
                        final String flagString = args[2];

                        try {
                            final int integer = Integer.parseInt(flagString);
                            kit.setQueueMenu(integer);

                            sender.sendMessage(ChatColor.GREEN + "Updated the queue pos to " + integer);
                        } catch (Exception ignored) {
                            sender.sendMessage(ChatColor.RED + "Invalid int");
                        }
                    }
                } else {
                    sender.sendMessage(NO_KIT);
                }
                break;
            case "ranked":
                if (kit != null) {
                    kit.setRanked(!kit.isRanked());
                    sender.sendMessage(
                            kit.isRanked() ? ChatColor.GREEN + "Successfully enabled ranked mode for kit " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled ranked mode for kit " + args[1] + ".");
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "excludearenafromallkitsbut":
                if (args.length < 2) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    Arena arena = this.plugin.getArenaManager().getArena(args[2]);

                    if (arena != null) {
                        for (Kit loopKit : this.plugin.getKitManager().getKits()) {
                            if (!loopKit.equals(kit)) {
                                player.performCommand("kit excludearena " + loopKit.getName() + " " + arena.getName());
                            }
                        }
                    } else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "excludearena":
                if (args.length < 3) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    Arena arena = this.plugin.getArenaManager().getArena(args[2]);

                    if (arena != null) {
                        kit.excludeArena(arena.getName());
                        sender.sendMessage(kit.getExcludedArenas().contains(arena.getName()) ?
                                ChatColor.GREEN + "Arena " + arena.getName() + " is now excluded from kit " + args[1] + "."
                                : ChatColor.GREEN + "Arena " + arena.getName() + " is no longer excluded from kit " + args[1] + ".");
                    } else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "whitelistarena":
                if (args.length < 3) {
                    sender.sendMessage(this.usageMessage);
                    return true;
                }
                if (kit != null) {
                    Arena arena = this.plugin.getArenaManager().getArena(args[2]);

                    if (arena != null) {
                        kit.whitelistArena(arena.getName());
                        sender.sendMessage(kit.getArenaWhiteList().contains(arena.getName()) ?
                                ChatColor.GREEN + "Arena " + arena.getName() + " is now whitelisted to kit " + args[1] + "."
                                : ChatColor.GREEN + "Arena " + arena.getName() + " is no longer whitelisted to kit " + args[1] + ".");
                    } else {
                        sender.sendMessage(KitCommand.NO_ARENA);
                    }
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "icon":
                if (kit != null) {
                    if (player.getItemInHand().getType() != Material.AIR) {
                        ItemStack icon = ItemUtil.renameItem(player.getItemInHand().clone(), ChatColor.GREEN + kit.getName());

                        kit.setIcon(icon);

                        sender.sendMessage(ChatColor.GREEN + "Successfully set icon for kit " + args[1] + ".");
                    } else {
                        player.sendMessage(ChatColor.RED + "You must be holding an item to set the kit icon!");
                    }
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "setinv":
                if (kit != null) {
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
                    } else {
                        player.updateInventory();

                        kit.setContents(player.getInventory().getContents());
                        kit.setArmor(player.getInventory().getArmorContents());

                        sender.sendMessage(ChatColor.GREEN + "Successfully set kit contents for " + args[1] + ".");
                    }
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "getinv":
                if (kit != null) {
                    player.getInventory().setContents(kit.getContents());
                    player.getInventory().setArmorContents(kit.getArmor());
                    player.updateInventory();
                    sender.sendMessage(ChatColor.GREEN + "Successfully retrieved kit contents from " + args[1] + ".");
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
                //todo: fix displayName and shit :)
            case "setdisplayname":
                String displayName = StringUtils.join(args, ' ', 2, args.length);
//                kit.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
                sender.sendMessage(ChatColor.GREEN + "Successfully set the display name as \"" + ChatColor.translateAlternateColorCodes('&', displayName) + ChatColor.GREEN + "\"");
                break;
            case "seteditinv":
                if (kit != null) {
                    if (player.getGameMode() == GameMode.CREATIVE) {
                        sender.sendMessage(ChatColor.RED + "You can't set item contents in creative mode!");
                    } else {
                        player.updateInventory();

                        kit.setKitEditContents(player.getInventory().getContents());

                        sender.sendMessage(ChatColor.GREEN + "Successfully set edit kit contents for " + args[1] + ".");
                    }
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            case "geteditinv":
                if (kit != null) {
                    player.getInventory().setContents(kit.getKitEditContents());
                    player.updateInventory();
                    sender.sendMessage(ChatColor.GREEN + "Successfully retrieved edit kit contents from " + args[1] + ".");
                } else {
                    sender.sendMessage(KitCommand.NO_KIT);
                }
                break;
            default:
                sender.sendMessage(usageMessage);
                break;
        }
        return true;
    }
}
