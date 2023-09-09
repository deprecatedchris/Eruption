package me.chris.eruption.match.menus;

import lombok.Getter;
import me.chris.eruption.EruptionPlugin;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.json.simple.JSONObject;
import me.chris.eruption.match.Match;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.other.ItemUtil;
import me.chris.eruption.util.other.MathUtil;
import me.chris.eruption.util.other.StringUtil;
import me.chris.eruption.util.inventory.InventoryUI;

import java.util.*;

@Getter
public class InventorySnapshot {

	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764 ");
	private final InventoryUI inventoryUI;
	private final ItemStack[] originalInventory;
	private final ItemStack[] originalArmor;

	@Getter
	private final UUID snapshotId = UUID.randomUUID();

	public InventorySnapshot(Player player, Match match) {
		ItemStack[] contents = player.getInventory().getContents();
		ItemStack[] armor = player.getInventory().getArmorContents();

		this.originalInventory = contents;
		this.originalArmor = armor;

		PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

		double health = player.getHealth();
		double food = (double) player.getFoodLevel();

		List<String> potionEffectStrings = new ArrayList<>();

		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
			String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
			String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());

			potionEffectStrings.add(ChatColor.RED + effectName + " " + romanNumeral + ChatColor.GRAY + " (" + duration + ")");
		}

		this.inventoryUI = new InventoryUI(player.getName() + "'s Inventory", true, 6);

		for (int i = 0; i < 9; i++) {
			this.inventoryUI.setItem(i + 27, new InventoryUI.EmptyClickableItem(contents[i]));
			this.inventoryUI.setItem(i + 18, new InventoryUI.EmptyClickableItem(contents[i + 27]));
			this.inventoryUI.setItem(i + 9, new InventoryUI.EmptyClickableItem(contents[i + 18]));
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(contents[i + 9]));
		}

		boolean potionMatch = false;
		boolean soupMatch = false;

		for (ItemStack item : match.getKit().getContents()) {
			if (item == null) {
				continue;
			}
			if (item.getType() == Material.MUSHROOM_SOUP) {
				soupMatch = true;
				break;
			} else if (item.getType() == Material.POTION && item.getDurability() == (short) 16421) {
				potionMatch = true;
				break;
			}
		}

		if (potionMatch) {
			int potCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getDurability).filter(d -> d == 16421).count();

			this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(ItemUtil.createItem(Material.POTION, ChatColor.GREEN + "" + potCount + " health potions", potCount == 0 ? 1 : potCount, (short) 16421),
					ChatColor.GRAY + player.getName() + ChatColor.GRAY + " had " + potCount + " health potion" + (potCount > 1 ? "s" : "") + ChatColor.GRAY + " left")));
		} else if (soupMatch) {
			int soupCount = (int) Arrays.stream(contents).filter(Objects::nonNull).map(ItemStack::getType).filter(d -> d == Material.MUSHROOM_SOUP).count();

			this.inventoryUI.setItem(47, new InventoryUI.EmptyClickableItem(ItemUtil.createItem(
					Material.MUSHROOM_SOUP, ChatColor.GRAY + player.getName() + " had " + ChatColor.GRAY + soupCount + " soup" + (soupCount > 1 ? "s" : "") + ChatColor.GRAY + " left", soupCount, (short) 16421)));
		}

		final double roundedHealth = Math.round(health / 2.0 * 2.0) / 2.0;

		this.inventoryUI.setItem(48,
				new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.SPECKLED_MELON, ChatColor.GREEN + UNICODE_HEART + roundedHealth + " HP",1)));

		final double roundedFood = Math.round(health / 2.0 * 2.0) / 2.0;

		this.inventoryUI.setItem(49,
				new InventoryUI.EmptyClickableItem(ItemUtil.createItem(Material.COOKED_BEEF, ChatColor.GREEN.toString() + roundedFood + " Hunger", (int) Math.round(food / 2.0D))));

		this.inventoryUI.setItem(50,
				new InventoryUI.EmptyClickableItem(ItemUtil.reloreItem(
						ItemUtil.createItem(Material.POTION, ChatColor.GREEN + "Potion Effects", 0)
						, potionEffectStrings.toArray(new String[]{}))));

		if (potionMatch) {
			this.inventoryUI.setItem(51, new InventoryUI.EmptyClickableItem(
					ItemUtil.reloreItem(
							ItemUtil.createItem(Material.PAPER, ChatColor.GREEN + "Match Stats"),
							ChatColor.RED + "Potion Accuracy: " + ChatColor.GRAY + potionAccuracy(playerData.getMissedPots(), playerData.getThrownPots()),
							ChatColor.RED + "Hits: " + ChatColor.GRAY + playerData.getHits(),
							ChatColor.RED + "Longest Combo: " + ChatColor.GRAY + playerData.getLongestCombo(),
							ChatColor.RED + "Potions Missed:  " + ChatColor.GRAY + playerData.getMissedPots())));
		} else if (soupMatch) {
			this.inventoryUI.setItem(52, new InventoryUI.EmptyClickableItem(
					ItemUtil.reloreItem(
							ItemUtil.createItem(Material.PAPER, ChatColor.GREEN + "Match Stats"),
							ChatColor.RED + "Hits: " + ChatColor.GRAY + playerData.getHits(),
							ChatColor.RED + "Longest Combo: " + ChatColor.GRAY + playerData.getLongestCombo())));
		} else {
			this.inventoryUI.setItem(53, new InventoryUI.EmptyClickableItem(
					ItemUtil.reloreItem(
							ItemUtil.createItem(Material.PAPER, ChatColor.GREEN + "Match Stats"),
							ChatColor.RED + "Hits: " + ChatColor.GRAY + playerData.getHits(),
							ChatColor.RED + "Longest Combo: " + ChatColor.GRAY + playerData.getLongestCombo())));
		}


		for (int i = 36; i < 40; i++) {
			this.inventoryUI.setItem(i, new InventoryUI.EmptyClickableItem(armor[39 - i]));
		}
	}

	public JSONObject toJson() {
		JSONObject object = new JSONObject();

		JSONObject inventoryObject = new JSONObject();
		for (int i = 0; i < this.originalInventory.length; i++) {
			inventoryObject.put(i, this.encodeItem(this.originalInventory[i]));
		}
		object.put("menus", inventoryObject);

		JSONObject armourObject = new JSONObject();
		for (int i = 0; i < this.originalArmor.length; i++) {
			armourObject.put(i, this.encodeItem(this.originalArmor[i]));
		}
		object.put("armour", armourObject);

		return object;
	}

	private JSONObject encodeItem(ItemStack itemStack) {
		if (itemStack == null || itemStack.getType() == Material.AIR) {
			return null;
		}

		JSONObject object = new JSONObject();
		object.put("material", itemStack.getType().name());
		object.put("durability", itemStack.getDurability());
		object.put("amount", itemStack.getAmount());

		JSONObject enchants = new JSONObject();
		for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
			enchants.put(enchantment.getName(), itemStack.getEnchantments().get(enchantment));
		}
		object.put("enchants", enchants);

		return object;
	}

	private String potionAccuracy(int potionsMissed, int potionsThrown){
		if(potionsThrown == 0){
			return "N/A";
		}else if(potionsMissed == 0){
			return "100%";
		}else if(potionsThrown == potionsMissed){
			return "50%";
		}
		return Math.round(100.0D - ((double)potionsMissed / (double)potionsThrown) * 100.0D) + "%";
	}

}
