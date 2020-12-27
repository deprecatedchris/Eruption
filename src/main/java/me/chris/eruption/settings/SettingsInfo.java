package me.chris.eruption.settings;

import lombok.Data;

@Data
public class SettingsInfo {

    private boolean duelRequests = true;
    private boolean altScoreboard = true;
    private boolean scoreboardToggled = true;
    private boolean spectatorsAllowed = true;
    private boolean playerVisibility = true;
    public boolean isPartyInvites = true;
}
