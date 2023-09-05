package me.chris.eruption.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class QueueEntry {

	private final QueueType queueType;
	private final String kitName;

	private final int elo;
	private final int ping;

	private final boolean party;

}
