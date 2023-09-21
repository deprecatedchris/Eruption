package me.chris.eruption.arena.managers;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.arena.arena.Arena;
import me.chris.eruption.arena.arena.StandaloneArena;
import org.bukkit.Chunk;
import lombok.Getter;

public class ChunkManager {
	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Getter
	private boolean chunksLoaded;

	public ChunkManager() {
		//EruptionPlugin.getInstance().setSetupSupplier(() -> this.chunksLoaded);
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, this::loadChunks, 1L);
	}



	private void loadChunks() {
		this.plugin.getLogger().info("Started loading server chunks");


		//Is This needed?
//		LocationUtil spawnMin = this.plugin.getSpawnManager().getSpawnMin();
//		LocationUtil spawnMax = this.plugin.getSpawnManager().getSpawnMax();
//
//		if (spawnMin != null && spawnMax != null) {
//			int spawnMinX = spawnMin.toBukkitLocation().getBlockX() >> 4;
//			int spawnMinZ = spawnMin.toBukkitLocation().getBlockZ() >> 4;
//			int spawnMaxX = spawnMax.toBukkitLocation().getBlockX() >> 4;
//			int spawnMaxZ = spawnMax.toBukkitLocation().getBlockZ() >> 4;
//
//			if (spawnMinX > spawnMaxX) {
//				int lastSpawnMinX = spawnMinX;
//				spawnMinX = spawnMaxX;
//				spawnMaxX = lastSpawnMinX;
//			}
//
//			if (spawnMinZ > spawnMaxZ) {
//				int lastSpawnMinZ = spawnMinZ;
//				spawnMinZ = spawnMaxZ;
//				spawnMaxZ = lastSpawnMinZ;
//			}
//
//			for (int x = spawnMinX; x <= spawnMaxX; x++) {
//				for (int z = spawnMinZ; z <= spawnMaxZ; z++) {
//
//					Chunk chunk = spawnMin.toBukkitWorld().getChunkAt(x, z);
//
//					if(!chunk.isLoaded()) {
//						chunk.load();
//					}
//
//				}
//			}
//		}


		for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
			if (!arena.isEnabled()) {
				continue;
			}
			int arenaMinX = arena.getMin().toBukkitLocation().getBlockX() >> 4;
			int arenaMinZ = arena.getMin().toBukkitLocation().getBlockZ() >> 4;
			int arenaMaxX = arena.getMax().toBukkitLocation().getBlockX() >> 4;
			int arenaMaxZ = arena.getMax().toBukkitLocation().getBlockZ() >> 4;

			if (arenaMinX > arenaMaxX) {
				int lastArenaMinX = arenaMinX;
				arenaMinX = arenaMaxX;
				arenaMaxX = lastArenaMinX;
			}

			if (arenaMinZ > arenaMaxZ) {
				int lastArenaMinZ = arenaMinZ;
				arenaMinZ = arenaMaxZ;
				arenaMaxZ = lastArenaMinZ;
			}

			for (int x = arenaMinX; x <= arenaMaxX; x++) {
				for (int z = arenaMinZ; z <= arenaMaxZ; z++) {
					Chunk chunk = arena.getMin().toBukkitWorld().getChunkAt(x, z);

					if(!chunk.isLoaded()) {
						chunk.load();
					}
				}
			}

			for (StandaloneArena saArena : arena.getStandaloneArenas()) {
				arenaMinX = saArena.getMin().toBukkitLocation().getBlockX() >> 4;
				arenaMinZ = saArena.getMin().toBukkitLocation().getBlockZ() >> 4;
				arenaMaxX = saArena.getMax().toBukkitLocation().getBlockX() >> 4;
				arenaMaxZ = saArena.getMax().toBukkitLocation().getBlockZ() >> 4;

				if (arenaMinX > arenaMaxX) {
					int lastArenaMinX = arenaMinX;
					arenaMinX = arenaMaxX;
					arenaMaxX = lastArenaMinX;
				}

				if (arenaMinZ > arenaMaxZ) {
					int lastArenaMinZ = arenaMinZ;
					arenaMinZ = arenaMaxZ;
					arenaMaxZ = lastArenaMinZ;
				}

				for (int x = arenaMinX; x <= arenaMaxX; x++) {
					for (int z = arenaMinZ; z <= arenaMaxZ; z++) {

						Chunk chunk = saArena.getMin().toBukkitWorld().getChunkAt(x, z);

						if(!chunk.isLoaded()) {
							chunk.load();
						}
					}
				}
			}
		}

		this.plugin.getLogger().info("All Chunks were successfully loaded");
		this.chunksLoaded = true;
	}
}
