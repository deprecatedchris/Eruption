package me.chris.eruption.util.runnable;

import me.chris.eruption.EruptionPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockRemoveRunnable extends BukkitRunnable {

    private final Location location;

    private int ticks;

    public BlockRemoveRunnable(Location location) {
        this.location = location;
        this.ticks = 14;

        this.runTaskTimer(EruptionPlugin.getInstance(), 20L, 20L);
    }

    @Override
    public void run() {
        this.ticks--;

        if (this.location == null) {
            this.cancel();
            return;
        }

        if (this.ticks == 0) {
            if (this.location.getBlock() != null) {
                this.location.getBlock().setType(Material.AIR);
            }
        }
    }
}

