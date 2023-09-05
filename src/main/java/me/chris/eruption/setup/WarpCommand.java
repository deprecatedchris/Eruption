package me.chris.eruption.setup;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//TODO: Recode this command to blade.
public class WarpCommand extends Command {

    private final EruptionPlugin plugin = EruptionPlugin.getInstance();

    public WarpCommand() {
        super("spawn");
        this.setDescription("Spawn command.");
        this.setUsage(ChatColor.RED + "Usage: /spawn [args]");
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

        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());


        if (args.length == 0) {
            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
            return true;
        }

        return true;
    }

}
