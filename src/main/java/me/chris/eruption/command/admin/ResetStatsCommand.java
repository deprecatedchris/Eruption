package me.chris.eruption.command.admin;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.other.StringUtil;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.*;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetStatsCommand {

	@Command({"statreset"})
	@Usage("/statreset <player>")
	@Description("Reset the stats of a player.")
	@Permission("practice.admin")
	public static void duel(@Sender Player sender, Player target, String s, String[] args) throws BladeExitMessage {
		PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(target.getUniqueId());

		if (target == null) {
			throw new BladeExitMessage(CC.translate("&cCannot find this player."));
		}

		for (Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()) {
			playerData.setElo(kit.getName(), PlayerData.DEFAULT_ELO);
			playerData.setLosses(kit.getName(), 0);
			playerData.setWins(kit.getName(), 0);
		}

		sender.sendMessage(ChatColor.GREEN + target.getName() + "'s stats have been wiped.");
	}

}
