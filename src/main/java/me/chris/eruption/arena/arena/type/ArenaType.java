package me.chris.eruption.arena.arena.type;

public enum ArenaType {

    NONE("none"),
    SUMO("sumo"),
    OITC("oitc"),
    RUNNER("runner"),
    CORNERS("corners"),
    PARKOUR("parkour"),
    LMS("lms");

    String name;

    ArenaType(String name) {
        this.name = name;
    }

    public String getNiceName() {
        return name;
    }
}
