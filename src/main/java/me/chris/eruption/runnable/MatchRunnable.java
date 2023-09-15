package me.chris.eruption.runnable;

import lombok.RequiredArgsConstructor;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import me.chris.eruption.match.Match;
import me.chris.eruption.kit.Flag;
import me.chris.eruption.match.MatchState;

@RequiredArgsConstructor
public class MatchRunnable extends BukkitRunnable {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();
	private final Match match;

	@Override
	public void run() {
		switch (this.match.getMatchState()) {
			case STARTING:
				if (this.match.decrementCountdown() == 0) {
					this.match.setMatchState(MatchState.FIGHTING);
					this.match.broadcastWithSound(ChatColor.GREEN + "Match starting now!", Sound.FIREWORK_BLAST);
					this.match.broadcast("");
					this.match.broadcast(CC.translate("&c&lReminder: &fButterfly clicking is &cdiscouraged &fand could result in a &cban. Use at your own risk."));
					this.match.broadcast("");
					if (this.match.isRedrover()) {
						this.plugin.getMatchManager().pickPlayer(this.match);
					}
				} else {
					this.match.broadcastWithSound(
							ChatColor.WHITE + "Match starting " + ChatColor.RED + this.match.getCountdown() +
									(match.getCountdown() == 1 ? ChatColor.WHITE + " second ..." : ChatColor.WHITE + " seconds ..."), Sound.CLICK);
				}
				break;
			case SWITCHING:
				if (this.match.decrementCountdown() == 0) {
					this.match.getEntitiesToRemove().forEach(Entity::remove);
					this.match.clearEntitiesToRemove();
					this.match.setMatchState(MatchState.FIGHTING);
					this.plugin.getMatchManager().pickPlayer(this.match);
				}
				break;
			case FIGHTING:
				match.incrementDuration();
				break;
			case ENDING:
				if (this.match.decrementCountdown() == 0) {
					this.plugin.getTournamentManager().removeTournamentMatch(this.match);
					this.match.getRunnables().forEach(id -> this.plugin.getServer().getScheduler().cancelTask(id));
					this.match.getEntitiesToRemove().forEach(Entity::remove);
					this.match.getTeams().forEach(team ->
							team.alivePlayers().forEach(this.plugin.getPlayerManager()::sendToSpawnAndReset));
					this.match.spectatorPlayers().forEach(this.plugin.getMatchManager()::removeSpectator);
					this.match.getPlacedBlockLocations().forEach(location -> location.getBlock().setType(Material.AIR));
					this.match.getOriginalBlockChanges().forEach((blockState) -> blockState.getLocation().getBlock().setType(blockState.getType()));
					if (this.match.getKit().getFlag().equals(Flag.BUILD) || this.match.getKit().getFlag().equals(Flag.SPLEEF)) {
						this.match.getArena().addAvailableArena(this.match.getStandaloneArena());
						this.plugin.getArenaManager().removeArenaMatchUUID(this.match.getStandaloneArena());
					}
					this.plugin.getMatchManager().removeMatch(this.match);
					new MatchResetRunnable(this.match).runTaskTimer(this.plugin, 20L, 20L);
					this.cancel();
				}
				break;
		}
	}
}
