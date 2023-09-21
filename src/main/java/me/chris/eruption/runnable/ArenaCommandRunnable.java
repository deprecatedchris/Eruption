package me.chris.eruption.runnable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.arena.arena.StandaloneArena;

/**
 * @since 11/25/2017
 */
@Getter
@AllArgsConstructor
public class ArenaCommandRunnable implements Runnable {

    private final EruptionPlugin plugin;
    private final Arena copiedArena;

    private int times;

    @Override
    public void run() {
        this.duplicateArena(this.copiedArena, 10000, 10000);
    }

    private void duplicateArena(Arena arena, int offsetX, int offsetZ) {

        new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {

            @Override
            public void onComplete() {
                double minX = arena.getMin().getX() + this.getOffsetX();
                double minZ = arena.getMin().getZ() + this.getOffsetZ();
                double maxX = arena.getMax().getX() + this.getOffsetX();
                double maxZ = arena.getMax().getZ() + this.getOffsetZ();

                double aX = arena.getA().getX() + this.getOffsetX();
                double aZ = arena.getA().getZ() + this.getOffsetZ();
                double bX = arena.getB().getX() + this.getOffsetX();
                double bZ = arena.getB().getZ() + this.getOffsetZ();

                LocationUtil min = new LocationUtil(minX, arena.getMin().getY(), minZ, arena.getMin().getYaw(), arena.getMin().getPitch());
                LocationUtil max = new LocationUtil(maxX, arena.getMax().getY(), maxZ, arena.getMax().getYaw(), arena.getMax().getPitch());
                LocationUtil a = new LocationUtil(aX, arena.getA().getY(), aZ, arena.getA().getYaw(), arena.getA().getPitch());
                LocationUtil b = new LocationUtil(bX, arena.getB().getY(), bZ, arena.getB().getYaw(), arena.getB().getPitch());

                StandaloneArena standaloneArena = new StandaloneArena(a, b, min, max);

                arena.addStandaloneArena(standaloneArena);
                arena.addAvailableArena(standaloneArena);

                if (--ArenaCommandRunnable.this.times > 0) {
                    ArenaCommandRunnable.this.plugin.getServer().getLogger().info("Placed a standalone arena of " + arena.getName() + " at " + (int) minX + ", " + (int) minZ
                            + ". " + ArenaCommandRunnable.this.times + " arenas remaining.");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage("Placed a standalone arena of " + arena.getName() + " at " + (int) minX + ", " + (int) minZ
                                    + ". " + ArenaCommandRunnable.this.times + " arenas remaining.");
                        }
                    }
                    ArenaCommandRunnable.this.duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.isOp()) {
                            player.sendMessage("Finished pasting " + ArenaCommandRunnable.this.copiedArena.getName() + "'s standalone arenas.");
                        }
                    }
                    ArenaCommandRunnable.this.plugin.getServer().getLogger().info("Finished pasting " + ArenaCommandRunnable.this.copiedArena.getName() + "'s standalone arenas.");
                    ArenaCommandRunnable.this.plugin.getArenaManager().setGeneratingArenaRunnables(ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnables() - 1);
                    this.getPlugin().getArenaManager().reloadArenas();
                }
            }
        }.run();
    }
}