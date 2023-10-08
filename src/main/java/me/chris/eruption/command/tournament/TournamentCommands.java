package me.chris.eruption.command.tournament;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.tournament.Tournament;
import me.chris.eruption.util.CC;
import me.vaperion.blade.annotation.argument.Sender;
import me.vaperion.blade.annotation.command.Command;
import me.vaperion.blade.annotation.command.Description;
import me.vaperion.blade.annotation.command.Permission;
import me.vaperion.blade.annotation.command.Usage;
import me.vaperion.blade.exception.BladeExitMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TournamentCommands {

    private static final EruptionPlugin plugin = EruptionPlugin.getInstance();

    @Command({"tournament"})
    @Usage("/tournament <start|stop|alert>")
    @Permission("eruption.tournament.host")
    @Description("Host a tournament")
    public static void host(@Sender Player player, String type) throws BladeExitMessage {
        switch (type) {
            case "start" -> {
                if (plugin.getTournamentManager().getTournaments().isEmpty()) {
                    throw new BladeExitMessage(CC.translate("&cThere is no tournament to start."));
                }

                Tournament tournament = plugin.getTournamentManager().getTournaments().get(0);

                if (tournament.getPlayers().size() < tournament.getSize()) {
                    throw new BladeExitMessage(CC.translate("&cThere are not enough players to start the tournament."));
                }

                plugin.getTournamentManager().createTournament(
                        player,
                        tournament.getId(),
                        tournament.getTeamSize(),
                        tournament.getSize(),
                        tournament.getKitName());
            }
            case "stop" -> {
                if (plugin.getTournamentManager().getTournaments().isEmpty()) {
                    throw new BladeExitMessage(CC.translate("&cThere is no tournament to stop."));
                }

                Tournament tournament = plugin.getTournamentManager().getTournaments().get(0);

                if (tournament.getPlayers().size() < tournament.getSize()) {
                    throw new BladeExitMessage(CC.translate("&cThere are not enough players to stop the tournament."));
                }

                plugin.getTournamentManager().removeTournament(tournament.getId());
            }
            case "alert" -> {
                String[] alertMessage = new String[]{
                        " ",
                        CC.translate("&e&lA Tournament is being hosted."),
                        " ",
                        CC.translate("&eTeam Size: &c" + plugin.getTournamentManager().getTournaments().get(0).getTeamSize()),
                        CC.translate("&eLadder: &c" + plugin.getTournamentManager().getTournaments().get(0).getKitName()),
                        CC.translate("&eRequired Players: &c" + plugin.getTournamentManager().getTournaments().get(0).getSize()),
                        " ",
                };
                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.sendMessage(alertMessage);
                }
            }
        }
    }
}
