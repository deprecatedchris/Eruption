package me.chris.eruption.profile.commands;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SaveCommand extends Command {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public SaveCommand() {
        super("save");
        this.setDescription("Saves All Data.");
        this.setUsage(ChatColor.RED + "Usage: /save");
        this.setAliases(Arrays.asList("practicesave"));
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        final Player player = (Player)sender;
        if (!player.hasPermission("practice.staff")) {
            player.sendMessage (ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            for (PlayerData playerData : this.plugin.getPlayerManager().getAllData())
                this.plugin.getPlayerManager().saveData(playerData);
            this.plugin.getArenaManager().saveArenas();
            this.plugin.getSpawnManager().saveConfig();
            this.plugin.getKitManager().saveKits();
            this.plugin.getMainConfig().save();
        }
        return true;
    }

}
