package me.chris.eruption.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chris.eruption.events.PracticeEvent;

@Getter
@RequiredArgsConstructor
public class EventStartEvent extends BaseEvent {
	private final PracticeEvent event;
}
