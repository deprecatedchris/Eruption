package me.chris.eruption.kit.managers;

import lombok.Getter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Flag;
import me.chris.eruption.util.config.Config;
import me.chris.eruption.kit.Kit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class KitManager {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();
	private final Map<String, Kit> kits = new HashMap<>();
	private final List<String> rankedKits = new ArrayList<>();
	private final Config config = new Config("kits", this.plugin);

	public KitManager() {
		this.loadKits();
		this.kits.entrySet().stream()
				.filter(kit -> kit.getValue().isEnabled() && kit.getValue().isRanked())
				.forEach(kit -> this.rankedKits.add(kit.getKey()));
	}

	@SuppressWarnings("unchecked")
	private void loadKits() {
		FileConfiguration fileConfig = this.config.getConfig();
		ConfigurationSection kitSection = fileConfig.getConfigurationSection("kits");

		if (kitSection == null) {
			return;
		}

		kitSection.getKeys(false).forEach(name -> {
			ItemStack[] contents = ((List<ItemStack>) kitSection.get(name + ".contents")).toArray(new ItemStack[0]);
			ItemStack[] armor = ((List<ItemStack>) kitSection.get(name + ".armor")).toArray(new ItemStack[0]);
			ItemStack[] kitEditContents = ((List<ItemStack>) kitSection.get(name + ".kitEditContents")).toArray(new ItemStack[0]);

			ItemStack icon = (ItemStack) kitSection.get(name + ".icon");

			List<String> excludedArenas = kitSection.getStringList(name + ".excludedArenas");
			List<String> arenaWhiteList = kitSection.getStringList(name + ".arenaWhitelist");

			boolean enabled = kitSection.getBoolean(name + ".enabled");
			boolean ranked = kitSection.getBoolean(name + ".ranked");
			final int q = kitSection.getInt(name + ".queue", 0);
			final Flag kitFlag = Flag.valueOf(kitSection.getString(name + ".flag", "DEFAULT"));

			Kit kit = new Kit(name, contents, armor, kitEditContents, icon, excludedArenas, arenaWhiteList, enabled, ranked, q, kitFlag);
			this.kits.put(name, kit);
		});
	}

	public void saveKits() {
		FileConfiguration fileConfig = this.config.getConfig();

		fileConfig.set("kits", null);

		this.kits.forEach((kitName, kit) -> {
			if (kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
				fileConfig.set("kits." + kitName + ".displayName", kit.getDisplayName());
				fileConfig.set("kits." + kitName + ".contents", kit.getContents());
				fileConfig.set("kits." + kitName + ".armor", kit.getArmor());
				fileConfig.set("kits." + kitName + ".kitEditContents", kit.getKitEditContents());
				fileConfig.set("kits." + kitName + ".icon", kit.getIcon());
				fileConfig.set("kits." + kitName + ".excludedArenas", kit.getExcludedArenas());
				fileConfig.set("kits." + kitName + ".arenaWhitelist", kit.getArenaWhiteList());
				fileConfig.set("kits." + kitName + ".enabled", kit.isEnabled());
				fileConfig.set("kits." + kitName + ".ranked", kit.isRanked());
				fileConfig.set("kits." + kitName + ".flag", kit.getFlag().name());
				fileConfig.set("kits." + kitName + ".queue", kit.getQueueMenu());
			}
		});

		this.config.save();
	}

	public void deleteKit(String name) {
		this.kits.remove(name);
	}

	public void createKit(String name) {
		final Kit kit = new Kit(name);

		kit.setEnabled(true);
		kit.setRanked(true);

		this.kits.put(name, new Kit(name));
	}

	public Collection<Kit> getKits() {
		return this.kits.values();
	}

	public Kit getKit(String name) {
		return this.kits.get(name);
	}

}
