package me.chris.eruption.setup;

import me.chris.eruption.util.random.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.random.Style;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SpawnsCommand extends Command {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();


	public SpawnsCommand() {
		super("setspawn");
		this.setDescription("Spawn command.");
		this.setUsage(ChatColor.RED + "Usage: /setspawn <subcommand>");
	}

	private void saveLocation(Player player, String location) {
		System.out.println(LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		config.set(location, LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
		this.plugin.getMainConfig().save();
		System.out.println(ChatColor.GREEN + "Successfully saved the location!");
	}

	private void saveStringsOitc(Player player) {
		System.out.println(LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		config.set("oitcSpawnpoints", EruptionPlugin.getInstance().getSpawnManager().fromLocations(EruptionPlugin.getInstance().getSpawnManager().getOitcSpawnpoints()));
		this.plugin.getMainConfig().save();
		System.out.println(ChatColor.GREEN + "Successfully saved the location!");
	}

	private void saveStringsLms(Player player) {
		System.out.println(LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		config.set("lmsLocations", EruptionPlugin.getInstance().getSpawnManager().fromLocations(EruptionPlugin.getInstance().getSpawnManager().getLmsLocations()));
		this.plugin.getMainConfig().save();
		System.out.println(ChatColor.GREEN + "Successfully saved the location!");
	}

	private void saveStringsPotion(Player player) {
		System.out.println(LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		config.set("potionLocations", EruptionPlugin.getInstance().getSpawnManager().fromLocations(EruptionPlugin.getInstance().getSpawnManager().getPotionLocations()));
		this.plugin.getMainConfig().save();
		System.out.println(ChatColor.GREEN + "Successfully saved the location!");
	}

	private void saveStringsRunner(Player player) {
		System.out.println(LocationUtil.locationToString(LocationUtil.fromBukkitLocation(player.getLocation())));
		FileConfiguration config = this.plugin.getMainConfig().getConfig();
		config.set("runnerLocations", EruptionPlugin.getInstance().getSpawnManager().fromLocations(EruptionPlugin.getInstance().getSpawnManager().getRunnerLocations()));
		this.plugin.getMainConfig().save();
		System.out.println(ChatColor.GREEN + "Successfully saved the location!");
	}


	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		if (!player.hasPermission("practice.admin")) {
			player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(Style.translate("&7&m-------------------------------------------"));
			sender.sendMessage(Style.translate("&cEruption &7» Spawn Help:"));
			sender.sendMessage(Style.translate("&7&m-------------------------------------------"));
			sender.sendMessage(Style.translate("&c» &f/setspawn spawnlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn spawnmin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn spawnmax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn cornerlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn cornermin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn cornermax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn sumolocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn sumofirst"));
			sender.sendMessage(Style.translate("&c» &f/setspawn sumosecond"));
			sender.sendMessage(Style.translate("&c» &f/setspawn sumomin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn sumomax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn tournamentlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn tournamentfirst"));
			sender.sendMessage(Style.translate("&c» &f/setspawn tournamentsecond"));
			sender.sendMessage(Style.translate("&c» &f/setspawn tournamentmin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn tournamentmax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn oitclocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn oitcmin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn oitcspawnpoints"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn parkourlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn parkourgamelocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn parkourmin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn parkourmax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn runnerlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn runnermin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn runnermax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn lmslocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn lmsmin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn lmsmax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn potionlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn potionmin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn potionmax"));
			sender.sendMessage(Style.translate("  "));
			sender.sendMessage(Style.translate("&c» &f/setspawn redroverlocation"));
			sender.sendMessage(Style.translate("&c» &f/setspawn redroverfirst"));
			sender.sendMessage(Style.translate("&c» &f/setspawn redroversecond"));
			sender.sendMessage(Style.translate("&c» &f/setspawn redrovermin"));
			sender.sendMessage(Style.translate("&c» &f/setspawn redrovermax"));
			sender.sendMessage(Style.translate("&7&m-------------------------------------------"));
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return true;
		}

		switch (args[0].toLowerCase()) {
			case "spawnlocation":
				this.plugin.getSpawnManager().setSpawnLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the spawn location.");
				saveLocation(player, "spawnLocation");
				break;
			case "spawnmin":
				this.plugin.getSpawnManager().setSpawnMin(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the spawn min.");
				saveLocation(player, "spawnMin");
				break;
			case "spawnmax":
				this.plugin.getSpawnManager().setSpawnMax(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the spawn max.");
				saveLocation(player, "spawnMax");
				break;
			case "cornerslocation":
				this.plugin.getSpawnManager().setCornersLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the corners location.");
				saveLocation(player, "cornersLocation");
				break;
			case "cornersmin":
				this.plugin.getSpawnManager().setCornersMin(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the corners min.");
				saveLocation(player, "cornersMin");
				break;
			case "cornersmax":
				this.plugin.getSpawnManager().setCornersMax(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the corners max.");
				saveLocation(player, "cornersMax");
				break;
			case "sumolocation":
				this.plugin.getSpawnManager().setSumoLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location.");
				saveLocation(player, "sumoLocation");
				break;
			case "sumofirst":
				this.plugin.getSpawnManager().setSumoFirst(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location A.");
				saveLocation(player, "sumoFirst");
				break;
			case "sumosecond":
				this.plugin.getSpawnManager().setSumoSecond(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo location B.");
				saveLocation(player, "sumoSecond");
				break;
			case "sumomin":
				this.plugin.getSpawnManager().setSumoMin(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo min.");
				saveLocation(player, "sumoMin");
				break;
			case "sumomax":
				this.plugin.getSpawnManager().setSumoMax(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the sumo max.");
				saveLocation(player, "sumoMax");
				break;
			case "oitclocation":
				this.plugin.getSpawnManager().setOitcLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC location.");
				saveLocation(player, "oitcLocation");
				break;
			case "oitcmin":
				this.plugin.getSpawnManager().setOitcMin(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC min.");
				saveLocation(player, "oitcMin");
				break;
			case "oitcmax":
				this.plugin.getSpawnManager().setOitcMax(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC max.");
				saveLocation(player, "oitcMax");
				break;
			case "oitcspawnpoints":
				this.plugin.getSpawnManager().getOitcSpawnpoints().add(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the OITC spawn-point #" + this.plugin.getSpawnManager().getOitcSpawnpoints().size() + ".");
				saveStringsOitc(player);
				break;
			case "parkourlocation":
				this.plugin.getSpawnManager().setParkourLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour location.");
				saveLocation(player, "parkourLocation");
				break;
			case "parkourgamelocation":
				this.plugin.getSpawnManager().setParkourGameLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour Game location.");
				saveLocation(player, "parkourGameLocation");
				break;
			case "parkourmax":
				this.plugin.getSpawnManager().setParkourMax(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour max location.");
				saveLocation(player, "parkourMax");
				break;
			case "parkourmin":
				this.plugin.getSpawnManager().setParkourMin(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the parkour min location.");
				saveLocation(player, "parkourMin");
				break;
			case "redroverlocation":
				this.plugin.getSpawnManager().setRedroverLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location.");
				break;
			case "redroverfirst":
				this.plugin.getSpawnManager().setRedroverFirst(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location A.");
				break;
			case "redroversecond":
				this.plugin.getSpawnManager().setRedroverSecond(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover location B.");
				break;
			case "redrovermin":
				this.plugin.getSpawnManager().setRedroverMin(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover min.");
				break;
			case "redrovermax":
				this.plugin.getSpawnManager().setRedroverMax(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the redrover max.");
				break;
			case "lmsmin": {
				this.plugin.getSpawnManager().setLmsLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the lms min location.");
				saveLocation(player, "lmsMin");
				break;
			}
			case "lmsmax": {
				this.plugin.getSpawnManager().setLmsLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the lms max location.");
				saveLocation(player, "lmsMax");
				break;
			}case "lmslocation": {
				this.plugin.getSpawnManager().setLmsLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the lms location.");
				saveLocation(player, "lmsLocation");
				break;
			}
			case "lms": {
				this.plugin.getSpawnManager().getLmsLocations().add(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the LMS spawn-point #" + this.plugin.getSpawnManager().getLmsLocations().size() + ".");
				saveStringsLms(player);
				break;
			}
			case "potionmin": {
				this.plugin.getSpawnManager().setPotionLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the potion min location.");
				saveLocation(player, "potionMin");
				break;
			}
			case "potionmax": {
				this.plugin.getSpawnManager().setPotionLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the potion max location.");
				saveLocation(player, "potionMax");
				break;
			}case "potionlocation": {
				this.plugin.getSpawnManager().setPotionLocation(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the potion location.");
				saveLocation(player, "potionLocation");
				break;
			}
			case "potion": {
				this.plugin.getSpawnManager().getPotionLocations().add(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the Potion spawn-point #" + this.plugin.getSpawnManager().getPotionLocations().size() + ".");
				saveStringsPotion(player);
				break;
			}
			case "runnerlocation": {
				this.plugin.getSpawnManager().getLmsLocations().add(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the runner location.");
				saveLocation(player, "runnerLocation");
				break;
			}
			case "runner": {
				this.plugin.getSpawnManager().getRunnerLocations().add(LocationUtil.fromBukkitLocation(player.getLocation()));
				player.sendMessage(ChatColor.GREEN + "Successfully set the Runner spawn-point #" + this.plugin.getSpawnManager().getRunnerLocations().size() + ".");
				saveStringsRunner(player);
				break;
			}

		}
		return false;
	}
}
