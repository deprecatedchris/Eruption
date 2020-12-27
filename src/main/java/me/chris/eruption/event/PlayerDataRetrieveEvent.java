package me.chris.eruption.event;

import me.chris.eruption.profile.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class PlayerDataRetrieveEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();

	private final PlayerData playerData;

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}
