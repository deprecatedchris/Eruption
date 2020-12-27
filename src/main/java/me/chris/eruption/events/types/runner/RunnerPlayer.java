package me.chris.eruption.events.types.runner;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.events.EventPlayer;
import me.chris.eruption.events.PracticeEvent;

import java.util.UUID;

@Setter
@Getter
public class RunnerPlayer extends EventPlayer {

    private RunnerState state = RunnerState.WAITING;
    private boolean wasEliminated = false;

    public RunnerPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum RunnerState {
        WAITING, INGAME, ELIMINATED
    }
}
