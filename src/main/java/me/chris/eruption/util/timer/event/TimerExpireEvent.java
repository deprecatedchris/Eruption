package me.chris.eruption.util.timer.event;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import me.chris.eruption.EruptionPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.chris.eruption.util.timer.Timer;

public class TimerExpireEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Optional<UUID> userUUID;
	private final Timer timer;

	private Optional<Player> player;

	public TimerExpireEvent(Timer timer) {
		this.userUUID = Optional.empty();
		this.timer = timer;
	}

	public TimerExpireEvent(UUID userUUID, Timer timer) {
		this.userUUID = Optional.ofNullable(userUUID);
		this.timer = timer;
	}

	public TimerExpireEvent(Player player, Timer timer) {
		Objects.requireNonNull(player);

		this.player = Optional.of(player);
		this.userUUID = Optional.of(player.getUniqueId());
		this.timer = timer;
	}

	public static HandlerList getHandlerList() {
		return TimerExpireEvent.HANDLERS;
	}

	public Optional<Player> getPlayer() {
		if (player == null) {
			player = this.userUUID.isPresent() ?
					Optional.of(EruptionPlugin.getInstance().getServer().getPlayer(this.userUUID.get())) : Optional.empty();
		}

		return player;
	}

	public Optional<UUID> getUserUUID() {
		return this.userUUID;
	}

	public Timer getTimer() {
		return this.timer;
	}

	@Override
	public HandlerList getHandlers() {
		return TimerExpireEvent.HANDLERS;
	}
}
