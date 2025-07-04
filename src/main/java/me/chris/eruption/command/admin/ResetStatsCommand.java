package me.chris.eruption.command.admin;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.profile.PlayerData;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ResetStatsCommand {

	@Command({"statreset"})
	@Usage("/statreset <player>")
	@Description("Reset the stats of a player.")
	@Permission("practice.admin")
	public static void duel(@Sender Player sender, Player target) throws BladeExitMessage {
		PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(target.getUniqueId());

		for (Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()) {
			playerData.setElo(kit.getName(), PlayerData.DEFAULT_ELO);
			playerData.setLosses(kit.getName(), 0);
			playerData.setWins(kit.getName(), 0);
		}

		sender.sendMessage(ChatColor.GREEN + target.getName() + "'s stats have been wiped.");
	}
}
