package me.chris.eruption.events;

public enum EventState {
    UNANNOUNCED, // The event hasn't even been announced yet
    WAITING, // Waiting for players to join
    STARTED // The event has started and is in progress
}
