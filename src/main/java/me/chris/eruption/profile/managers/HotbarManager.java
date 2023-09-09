package me.chris.eruption.profile.managers;

import lombok.Getter;
import me.chris.eruption.util.other.ItemUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class HotbarManager {

	private final ItemStack spawnItems[];
	private final ItemStack queueItems[];
	private final ItemStack partyItems[];
	private final ItemStack tournamentItems[];
	private final ItemStack eventItems[];
	private final ItemStack specItems[];
	private final ItemStack partySpecItems[];

	private final ItemStack defaultBook;

	public HotbarManager() {
		this.spawnItems = new ItemStack[]{
				ItemUtil.createItem(Material.IRON_SWORD, ChatColor.RED + "Join Unranked Queue" + ChatColor.GRAY + " (Right-Click)"),
				ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Join Ranked Queue" + ChatColor.GRAY + " (Right-Click)"),
				null,
				null,
				ItemUtil.createItem(Material.NAME_TAG, ChatColor.LIGHT_PURPLE + "Create Party" + ChatColor.GRAY + " (Right-Click)"),
				null,
				ItemUtil.createItem(Material.WATCH, ChatColor.BLUE + "Settings" + ChatColor.GRAY + " (Right-Click)"),
				ItemUtil.createItem(Material.EMERALD, ChatColor.YELLOW + "View Leaderboards" + ChatColor.GRAY + " (Right-Click)"),
				ItemUtil.createItem(Material.BOOK, ChatColor.DARK_GREEN + "Kit Editor" + ChatColor.GRAY + " (Right-Click)")
		};
		this.queueItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.INK_SACK, ChatColor.RED + "Leave Queue" + ChatColor.DARK_GRAY +  ChatColor.GRAY + " (Right-Click)", 1, (short) 1),
		};
		this.specItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED + "Leave Spectator Mode" + ChatColor.GRAY + " (Right-Click)"),
		};
		this.partySpecItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR,   ChatColor.RED + "Leave Party" + ChatColor.GRAY + " (Right-Click)"),
		};
		this.tournamentItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED + "Leave Tournament" + ChatColor.GRAY + " (Right-Click)"),
		};
		this.eventItems = new ItemStack[]{
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED + "Leave Event" + ChatColor.GRAY + " (Right-Click)"),
		};
		this.partyItems = new ItemStack[]{
				ItemUtil.createItem(Material.PAPER, ChatColor.RED + "Party Information" + ChatColor.GRAY + " (Right-Click)"),
				null,
				null,
				null,
				null,
				null,
				ItemUtil.createItem(Material.SKULL_ITEM, ChatColor.LIGHT_PURPLE + "Fight Other Parties" + ChatColor.GRAY + " (Right-Click)"),
				ItemUtil.createItem(Material.GOLD_AXE, ChatColor.DARK_PURPLE + "Party Events" + ChatColor.GRAY + " (Right-Click)"),
				ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED + "Leave Party" + ChatColor.GRAY + " (Right-Click)")
		};
		this.defaultBook = ItemUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.RED.toString() + "Default Kit" + ChatColor.GRAY + " (Right-Click)");
	}
}
