package me.chris.eruption.setting;

import lombok.Data;
import me.chris.eruption.scoreboard.ScoreboardState;

@Data
public class SettingsInfo {

    private boolean duelRequests = true;
    private boolean altScoreboard = true;
    private boolean scoreboardToggled = true;
    private boolean spectatorsAllowed = true;
    private boolean playerVisibility = true;
    public boolean isPartyInvites = true;

    private ScoreboardState scoreboardState = ScoreboardState.DURATION;
}
