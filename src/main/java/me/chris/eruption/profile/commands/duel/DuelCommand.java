package me.chris.eruption.profile.commands.duel;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.random.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand extends Command {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public DuelCommand() {
		super("duel");
		this.setDescription("Duel a profile.");
		this.setUsage(ChatColor.RED + "Usage: /duel <profile>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;

		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return true;
		}
		if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
			player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
			return true;
		}
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
			return true;
		}
		Player target = this.plugin.getServer().getPlayer(args[0]);

		if (target == null) {
			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
			return true;
		}
		if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
			player.sendMessage(ChatColor.RED + "That profile is currently in a tournament.");
			return true;
		}
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		if ((party != null && this.plugin.getPartyManager().isInParty(target.getUniqueId(), party)) || player.getName().equals(target.getName())) {
			player.sendMessage(ChatColor.RED + "You can't duel yourself.");
			return true;
		}
		if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "You are not the leader fo the party.");
			return true;
		}
		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());

		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			player.sendMessage(ChatColor.RED + "That profile is currently busy.");
			return true;
		}

		if (!targetData.getSettings().isDuelRequests()) {
			player.sendMessage(ChatColor.RED + "That profile has ignored duel requests.");
			return true;
		}

		Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
		if (party == null && targetParty != null) {
			player.sendMessage(ChatColor.RED + "That profile is currently in a party.");
			return true;
		}
		if (party != null && targetParty == null) {
			player.sendMessage(ChatColor.RED + "You are currently in a party.");
			return true;
		}
		playerData.setDuelSelecting(target.getUniqueId());
		player.openInventory(this.plugin.getInventoryManager().getDuelInventory().getCurrentPage());

		return true;
	}
}
