package me.chris.eruption.util.random;

import org.bukkit.entity.Player;

public class User
{
    private long transping;
    private Player player;
    public long transstart;

    public User(final Player player) {
        this.player = player;
    }

    public long getTransping() {
        return this.transping;
    }

    public void setTransping(final long transping) {
        this.transping = transping;
    }

    public Player getPlayer() {
        return this.player;
    }
}
