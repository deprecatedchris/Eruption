package me.chris.eruption.party.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.party.Party;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.random.Clickable;
import me.chris.eruption.util.random.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PartyCommand extends Command {
	private final static String NOT_LEADER = ChatColor.RED + "You are not the leader of the party!";
	private final static String[] HELP_MESSAGE = new String[] {
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.BLUE + "PARTIES" + ChatColor.GRAY + " » All Commands For Parties." ,
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
			ChatColor.RED + "» /party help " + ChatColor.GRAY + "- Displays the help menu",
			ChatColor.RED + "» /party create " + ChatColor.GRAY + "- Creates a party instance",
			ChatColor.RED + "» /party leave " + ChatColor.GRAY + "- Leave your current party",
			ChatColor.RED + "» /party info " + ChatColor.GRAY + "- Displays your party information",
			ChatColor.RED + "» /party join (profile) " + ChatColor.GRAY + "- Join a party (invited or unlocked)",
			"",
			ChatColor.RED + "» /party open " + ChatColor.GRAY + "- Open your party for others to join",
			ChatColor.RED + "» /party lock " + ChatColor.GRAY + "- Lock your party for others to join",
			ChatColor.RED + "» /party setlimit (amount) " + ChatColor.GRAY + "- Set a limit to your party",
			ChatColor.RED + "» /party invite (profile) " + ChatColor.GRAY + "- Invites a profile to your party",
			ChatColor.RED + "» /party kick (profile) " + ChatColor.GRAY + "- Kicks a profile from your party",
			ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
	};

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	public PartyCommand() {
		super("party");
		this.setDescription("Party Command.");
		this.setUsage(ChatColor.RED + "Usage: /party <subcommand> [profile]");
		this.setAliases(Collections.singletonList("p"));
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());

		String subCommand = args.length < 1 ? "help" : args[0];

		switch (subCommand.toLowerCase()) {
			case "create":
				if (party != null) {
					player.sendMessage(ChatColor.RED + "You are already in a party.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
				} else {
					this.plugin.getPartyManager().createParty(player);
				}
				break;
			case "leave":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You are not in a party.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
				} else {
					this.plugin.getPartyManager().leaveParty(player);
				}
				break;
			case "inv":
			case "invite":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You are not in a party.");
				} else if (!this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
					player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
				} else if (this.plugin.getTournamentManager().getTournament(player.getUniqueId()) != null) {
					player.sendMessage(ChatColor.RED + "You are currently in a tournament.");
				} else if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /party invite (profile)");
				} else if (party.isOpen()) {
					player.sendMessage(ChatColor.RED + "This party is open, so anyone can join.");
				} else if (party.getMembers().size() >= party.getLimit()) {
					player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					Player target = this.plugin.getServer().getPlayer(args[1]);

					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}

					PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

					if (target.getUniqueId() == player.getUniqueId()) {
						player.sendMessage(ChatColor.RED + "You can't invite yourself.");
					} else if (this.plugin.getPartyManager().getParty(target.getUniqueId()) != null) {
						player.sendMessage(ChatColor.RED + "That player is currently in another party.");
					} else if (targetData.getPlayerState() != PlayerState.SPAWN) {
						player.sendMessage(ChatColor.RED + "That player is currently busy.");
					} else if (!targetData.getSettings().isPartyInvites()) {
						player.sendMessage(ChatColor.RED + "That player has the party invites disabled.");
					} else if (this.plugin.getPartyManager().hasPartyInvite(target.getUniqueId(), player.getUniqueId())) {
						player.sendMessage(ChatColor.RED + "You have already sent a party invitation to that player, please wait.");
					} else {
						this.plugin.getPartyManager().createPartyInvite(player.getUniqueId(), target.getUniqueId());

						Clickable partyInvite = new Clickable(ChatColor.GREEN + sender.getName() + ChatColor.YELLOW + " has invited you to their party! " + ChatColor.GRAY + "[Click to Accept]",
								ChatColor.GRAY + "Click to accept",
								"/party accept " + sender.getName());

						partyInvite.sendToPlayer(target);

						party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + target.getName() + " has been invited to the party.");

					}
				}
				break;
			case "accept":
				if (party != null) {
					player.sendMessage(ChatColor.RED + "You are already in a party.");
				} else if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /party accept <profile>.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
				} else {
					Player target = this.plugin.getServer().getPlayer(args[1]);
					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null) {
						player.sendMessage(ChatColor.RED + "That profile is not in a party.");
					} else if (targetParty.getMembers().size() >= targetParty.getLimit()) {
						player.sendMessage(ChatColor.RED + "Party size has reached it's limit");
					} else if (!this.plugin.getPartyManager().hasPartyInvite(player.getUniqueId(), targetParty.getLeader())) {
						player.sendMessage(ChatColor.RED + "You do not have any pending requests.");
					} else {
						this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
					}
				}
				break;
			case "join":
				if (party != null) {
					player.sendMessage(ChatColor.RED + "You are already in a party.");
				} else if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /party join <profile>.");
				} else if (playerData.getPlayerState() != PlayerState.SPAWN) {
					player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
				} else {
					Player target = this.plugin.getServer().getPlayer(args[1]);
					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null || !targetParty.isOpen() || targetParty.getMembers().size() >= targetParty.getLimit()) {
						player.sendMessage(ChatColor.RED + "You can't join this party.");
					} else {
						this.plugin.getPartyManager().joinParty(targetParty.getLeader(), player);
					}
				}
				break;
			case "kick":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You are not in a party.");
				} else if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /party kick <profile>.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					Player target = this.plugin.getServer().getPlayer(args[1]);

					if (target == null) {
						player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
						return true;
					}
					Party targetParty = this.plugin.getPartyManager().getParty(target.getUniqueId());

					if (targetParty == null || targetParty.getLeader() != party.getLeader()) {
						player.sendMessage(ChatColor.RED + "That player is not in your party.");
					} else {
						this.plugin.getPartyManager().leaveParty(target);
					}
				}
				break;
			case "setlimit":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You are not in a party.");
				} else if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Usage: /party setlimit <amount>.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					try {
						int limit = Integer.parseInt(args[1]);

						if (limit < 2 || limit > 50) {
							player.sendMessage(ChatColor.RED + "That is not a valid limit.");
						} else {
							party.setLimit(limit);
							player.sendMessage(ChatColor.GREEN + "You have set the party profile limit to " + ChatColor.YELLOW + limit + " players.");
						}
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "That is not a number.");
					}
				}
				break;
			case "open":
			case "lock":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You are not in a party.");
				} else {
					if (party.getLeader() != player.getUniqueId()) {
						player.sendMessage(PartyCommand.NOT_LEADER);
						return true;
					}
					party.setOpen(!party.isOpen());

					party.broadcast(ChatColor.GREEN.toString() + ChatColor.BOLD + "[*] " + ChatColor.YELLOW + "Your party is now " + ChatColor.BOLD + (party.isOpen() ? "OPEN" : "LOCKED"));
				}
				break;
			case "info":
				if (party == null) {
					player.sendMessage(ChatColor.RED + "You are not in a party.");
				} else {

					List<UUID> members = new ArrayList<>(party.getMembers());
					members.remove(party.getLeader());

					StringBuilder builder = new StringBuilder(ChatColor.WHITE.toString() + "Party Members§8" + ChatColor.GRAY  + " ("  + ChatColor.YELLOW + ((party.getMembers().size() - 1) + ChatColor.GRAY.toString() + ")§7:§f "));
					members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(member -> builder.append(ChatColor.WHITE).append(member.getName()).append(","));

					String[] information = new String[] {
							ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
							ChatColor.BLUE.toString() + ChatColor.BOLD + "Party Information§8:",
							ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
							ChatColor.WHITE + "Leader: " + ChatColor.RED + this.plugin.getServer().getPlayer(party.getLeader()).getName(),
							ChatColor.WHITE + "Party State§7: " + ChatColor.RED + (party.isOpen() ? "Open" : "Locked"),
							ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------",
							ChatColor.WHITE + builder.toString(),
							ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
					};

					player.sendMessage(information);
				}
				break;
			default:
				player.sendMessage(PartyCommand.HELP_MESSAGE);
				break;
		}
		return true;
	}
}
