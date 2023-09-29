package me.chris.eruption.command.match;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.command.match.menu.InventorySnapshot;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Pattern;

public class InvCommand {
	private static final Pattern UUID_PATTERN;
	private static final String INVENTORY_NOT_FOUND;

	static {
		UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
		INVENTORY_NOT_FOUND = CC.RED + "Cannot find the requested inventory. Maybe it expired?";
	}

	private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Command({"inventory", "_"})
	@Description("Get the post match inventory of a player.")
	public static void createKit(@Sender Player sender, String name) throws BladeExitMessage {
		if (!name.matches(InvCommand.UUID_PATTERN.pattern())) {
			throw new BladeExitMessage(InvCommand.INVENTORY_NOT_FOUND);
		}

		final InventorySnapshot snapshot = plugin.getInventoryManager().getSnapshot(UUID.fromString(name));
		if (snapshot == null) {
			throw new BladeExitMessage(InvCommand.INVENTORY_NOT_FOUND);
		} else {
			sender.openInventory(snapshot.getInventoryUI().getCurrentPage());
		}
	}
}
