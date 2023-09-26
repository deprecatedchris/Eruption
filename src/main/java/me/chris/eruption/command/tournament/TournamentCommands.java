package me.chris.eruption.command.tournament;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TournamentCommands {

    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"tournament"})
    @Usage("/tournament <start|stop|alert>")
    @Permission("eruption.tournament.host")
    @Description("Host a tournament")
    public static void tournamentHost(@Sender Player sender, String[] args) throws BladeExitMessage {

        switch (args[0].toLowerCase()) {

            case "start":
                if (args.length == 5) {
                    try {
                        int id = Integer.parseInt(args[1]);
                        int teamSize = Integer.parseInt(args[3]);
                        int size = Integer.parseInt(args[4]);
                        String kitName = args[2];

                        if (size % teamSize != 0) {
                            throw new BladeExitMessage(CC.translate("&cTournament size & team sizes are invalid. Please try again."));
                        }

                        if (plugin.getTournamentManager().getTournament(id) != null) {
                            throw new BladeExitMessage(CC.translate("&cThis tournament already exists."));
                        }

                        Kit kit = plugin.getKitManager().getKit(kitName);

                        if (kit == null) {
                            throw new BladeExitMessage(CC.translate("&cA kit with that name does not exists."));
                        }

                        plugin.getTournamentManager().createTournament(sender, id, teamSize, size, kitName);

                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size>");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /tournament start <id> <kit> <team size> <tournament size> ");
                }
                break;
            case "stop":

                if (args.length == 2) {
                    int id = Integer.parseInt(args[1]);
                    Tournament tournament = plugin.getTournamentManager().getTournament(id);

                    if (tournament != null) {
                        plugin.getTournamentManager().removeTournament(id);
                        sender.sendMessage(ChatColor.RED + "Successfully removed tournament " + id + ".");
                    } else {
                        sender.sendMessage(ChatColor.RED + "This tournament does not exist.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Usage: /tournament stop <id>");
                }
                break;

            case "alert":
                String[] alertMessage = new String[]{
                        " ",
                        CC.translate("&e&lA Tournament is being hosted."),
                        " ",
                        CC.translate("&eTeam Size: &c" + plugin.getTournamentManager().getTournaments().get(0).getTeamSize()),
                        CC.translate("&eLadder: &c" + plugin.getTournamentManager().getTournaments().get(0).getKitName()),
                        CC.translate("&eRequired Players: &c" +  plugin.getTournamentManager().getTournaments().get(0).getSize()),
                        " ",
                };
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(alertMessage);
                }
        }
    }
}
