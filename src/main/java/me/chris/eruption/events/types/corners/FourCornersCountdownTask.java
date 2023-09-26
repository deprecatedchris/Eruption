package me.chris.eruption.events.types.corners;

import me.chris.eruption.events.EventCountdownTask;
import me.chris.eruption.events.PracticeEvent;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class FourCornersCountdownTask extends EventCountdownTask {

    public FourCornersCountdownTask(PracticeEvent event) {
        super(event, 45);
    }

    @Override
    public boolean shouldAnnounce(int timeUntilStart) {
        return Arrays.asList(90, 60, 30, 15, 10, 5).contains(timeUntilStart);
    }

    @Override
    public boolean canStart() {
        return getEvent().getPlayers().size() >= 2;
    }

    @Override
    public void onCancel() {
        getEvent().sendMessage(ChatColor.RED + "There were not enough players to start the event.");
        getEvent().end();
        getEvent().getPlugin().getEventManager().setCooldown(0L);
    }
}
