package me.chris.eruption.kit.managers;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.util.random.PlayerUtil;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

public class EditorManager {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();
	private final Map<UUID, String> editing = new HashMap<>();
	private final Map<UUID, PlayerKit> renaming = new HashMap<>();

	public void addEditor(Player player, Kit kit) {
		this.editing.put(player.getUniqueId(), kit.getName());
		this.plugin.getInventoryManager().addEditingKitInventory(player, kit);

		PlayerUtil.clearPlayer(player);
		player.getInventory().setContents(kit.getContents());
		player.sendMessage(ChatColor.GREEN + "You are editing kit " + ChatColor.RED + kit.getName() + ChatColor.GREEN + ".");
	}

	public void removeEditor(UUID editor) {
		this.renaming.remove(editor);
		this.editing.remove(editor);
		this.plugin.getInventoryManager().removeEditingKitInventory(editor);
	}

	public String getEditingKit(UUID editor) {
		return this.editing.get(editor);
	}

	public void addRenamingKit(UUID uuid, PlayerKit playerKit) {
		this.renaming.put(uuid, playerKit);
	}

	public void removeRenamingKit(UUID uuid) {
		this.renaming.remove(uuid);
	}

	public PlayerKit getRenamingKit(UUID uuid) {
		return this.renaming.get(uuid);
	}
}
