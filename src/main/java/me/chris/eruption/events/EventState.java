package me.chris.eruption.events;

public enum EventState {
    UNANNOUNCED, // The commands hasn't even been announced yet
    WAITING, // Waiting for players to join while the commands counts down to start
    STARTED // The commands has started and is in progress
}
