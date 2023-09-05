package me.chris.eruption.profile.commands.duel;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.annotation.command.UsageAlias;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.entity.Player;


public class DuelCommand {
	@Command({"duel", "fight"})
	@Usage("/duel <player>")
	@UsageAlias("/fight <player>")
	@Description("Send a Duel request to a player.")
	public static void duel(@Sender Player player, Player target) throws BladeExitMessage {
		PlayerData playerData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());

		if (playerData.getPlayerState() != PlayerState.SPAWN) {
			throw new BladeExitMessage(CC.translate("&cCannot issue this command in your current state."));
		}

		PlayerData targetData = EruptionPlugin.getInstance().getPlayerManager().getPlayerData(target.getUniqueId());

		if (target == null) {
			throw new BladeExitMessage(CC.translate("&cCannot find this player."));
		}

		if (targetData.getPlayerState() != PlayerState.SPAWN) {
			throw new BladeExitMessage(CC.translate("&cThis player is currently busy."));
		}

		if (!targetData.getSettings().isDuelRequests()) {
			throw new BladeExitMessage(CC.translate("&cThis player has duels disabled."));
		}

		if (EruptionPlugin.getInstance().getTournamentManager().getTournament(target.getUniqueId()) != null) {
			throw new BladeExitMessage(CC.translate("&cThat player is currently in a tournament."));
		}

		if (EruptionPlugin.getInstance().getTournamentManager().getTournament(player.getUniqueId()) != null) {
			throw new BladeExitMessage(CC.translate("&cYou are currently in a tournament."));
		}

		Party party = EruptionPlugin.getInstance().getPartyManager().getParty(player.getUniqueId());

		if ((party != null && EruptionPlugin.getInstance().getPartyManager().isInParty(target.getUniqueId(), party)) || player.getName().equals(target.getName())) {
			throw new BladeExitMessage(CC.translate("&cYou cannot duel yourself."));
		}
		if (party != null && !EruptionPlugin.getInstance().getPartyManager().isLeader(player.getUniqueId())) {
			throw new BladeExitMessage(CC.translate("&cYou are not the leader of this party."));
		}

		Party targetParty = EruptionPlugin.getInstance().getPartyManager().getParty(target.getUniqueId());
		if (party == null && targetParty != null) {
			throw new BladeExitMessage(CC.translate("&cThis player is currently in a party."));
		}

		if (party != null && targetParty == null) {
			throw new BladeExitMessage(CC.translate("&cYou are currently in a party."));
		}

		playerData.setDuelSelecting(target.getUniqueId());
		player.openInventory(EruptionPlugin.getInstance().getInventoryManager().getDuelInventory().getCurrentPage());
	}
}



//	private final EruptionPlugin plugin = EruptionPlugin.getInstance();
//
//	public DuelCommand() {
//		super("duel");
//		this.setDescription("Duel a profile.");
//		this.setUsage(ChatColor.RED + "Usage: /duel <profile>");
//	}
//
//	@Override
//	public boolean execute(CommandSender sender, String alias, String[] args) {
//		if (!(sender instanceof Player)) {
//			return true;
//		}
//		Player player = (Player) sender;
//
//		if (args.length < 1) {
//			player.sendMessage(usageMessage);
//			return true;
//		}
//		if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
//			player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
//			return true;
//		}
//		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
//
//		if (playerData.getPlayerState() != PlayerState.SPAWN) {
//			player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
//			return true;
//		}
//		Player target = this.plugin.getServer().getPlayer(args[0]);
//
//		if (target == null) {
//			player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
//			return true;
//		}
//		if (this.plugin.getTournamentManager().getTournament(target.getUniqueId()) != null) {
//			player.sendMessage(ChatColor.RED + "That profile is currently in a tournament.");
//			return true;
//		}
//		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
//
//		if ((party != null && this.plugin.getPartyManager().isInParty(target.getUniqueId(), party)) || player.getName().equals(target.getName())) {
//			player.sendMessage(ChatColor.RED + "You can't duel yourself.");
//			return true;
//		}
//		if (party != null && !this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
//			player.sendMessage(ChatColor.RED + "You are not the leader fo the party.");
//			return true;
//		}
//		PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
//
//		if (targetData.getPlayerState() != PlayerState.SPAWN) {
//			player.sendMessage(ChatColor.RED + "That profile is currently busy.");
//			return true;
//		}
//
//		if (!targetData.getSettings().isDuelRequests()) {
//			player.sendMessage(ChatColor.RED + "That profile has ignored duel requests.");
//			return true;
//		}
//
//		Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());
//		if (party == null && targetParty != null) {
//			player.sendMessage(ChatColor.RED + "That profile is currently in a party.");
//			return true;
//		}
//		if (party != null && targetParty == null) {
//			player.sendMessage(ChatColor.RED + "You are currently in a party.");
//			return true;
//		}
//		playerData.setDuelSelecting(target.getUniqueId());
//		player.openInventory(this.plugin.getInventoryManager().getDuelInventory().getCurrentPage());
//
//		return true;
//	}
//}
