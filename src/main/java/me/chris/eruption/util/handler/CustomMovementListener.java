package me.chris.eruption.util.handler;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.events.PracticeEvent;
import me.chris.eruption.events.types.oitc.OITCEvent;
import me.chris.eruption.events.types.oitc.OITCPlayer;
import me.chris.eruption.events.types.sumo.SumoEvent;
import me.chris.eruption.events.types.sumo.SumoPlayer;
import me.chris.eruption.match.Match;
import me.chris.eruption.match.MatchState;
import me.chris.eruption.profile.PlayerData;
import me.chris.eruption.profile.PlayerState;
import me.chris.eruption.util.random.BlockUtil;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class CustomMovementListener implements Listener {

    EruptionPlugin plugin = EruptionPlugin.getInstance();

    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e){
        Player player = e.getPlayer();
        Location to = e.getTo();
        Location from = e.getFrom();
        PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            this.plugin.getLogger().warning(player.getName() + "'s profile data is null");
            return;
        }

        if (playerData.getPlayerState() == PlayerState.FIGHTING) {
            Match match = this.plugin.getMatchManager().getMatch(player.getUniqueId());

            if (match == null) {
                return;
            }

            if (match.getKit().isSpleef() || match.getKit().isSumo()) {

                if (BlockUtil.isOnLiquid(to, 0) || BlockUtil.isOnLiquid(to, 1)) {
                    this.plugin.getMatchManager().removeFighter(player, playerData, true);
                }


                if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
                    if (match.getMatchState() == MatchState.STARTING) {
                        player.teleport(from);
                        ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                    }
                }
            }
        }

        PracticeEvent event = this.plugin.getEventManager().getEventPlaying(player);

        if (event != null) {

            if (event instanceof SumoEvent) {
                SumoEvent sumoEvent = (SumoEvent) event;

                if (sumoEvent.getPlayer(player).getFighting() != null && sumoEvent.getPlayer(player).getState() == SumoPlayer.SumoState.PREPARING) {
                    player.teleport(from);
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                }
            } else if (event instanceof OITCEvent) {
                OITCEvent oitcEvent = (OITCEvent) event;

                if (oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.RESPAWNING) {
                    player.teleport(from);
                    ((CraftPlayer) player).getHandle().playerConnection.checkMovement = false;
                } else if (oitcEvent.getPlayer(player).getState() == OITCPlayer.OITCState.FIGHTING) {
                    if (player.getLocation().getBlockY() >= 90) {
                        oitcEvent.teleportNextLocation(player);
                    }
                }
            }

        }
    }
}
