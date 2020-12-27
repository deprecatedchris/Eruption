package me.chris.eruption.events.types.sumo;


import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.events.EventPlayer;
import me.chris.eruption.events.PracticeEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Setter
@Getter
public class SumoPlayer extends EventPlayer {

    private SumoState state = SumoState.WAITING;
    private BukkitTask fightTask;
    private SumoPlayer fighting;

    public SumoPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum SumoState {
        WAITING, PREPARING, FIGHTING, ELIMINATED
    }
}

