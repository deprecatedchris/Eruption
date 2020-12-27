package me.chris.eruption.events.types.corners;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.events.EventPlayer;
import me.chris.eruption.events.PracticeEvent;

import java.util.UUID;

@Setter
@Getter
public class FourCornersPlayer extends EventPlayer {

    private FourCornerState state = FourCornerState.WAITING;
    private boolean wasEliminated = false;

    public FourCornersPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum FourCornerState {
        WAITING, INGAME, ELIMINATED
    }
}
