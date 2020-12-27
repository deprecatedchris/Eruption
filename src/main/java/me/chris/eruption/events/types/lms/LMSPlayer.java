package me.chris.eruption.events.types.lms;

import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.events.EventPlayer;
import me.chris.eruption.events.PracticeEvent;

import java.util.UUID;

@Setter
@Getter
public class LMSPlayer extends EventPlayer {

    private LMSPlayer.LMSState state = LMSPlayer.LMSState.WAITING;

    public LMSPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum LMSState {
        WAITING, FIGHTING, ELIMINATED
    }
}

