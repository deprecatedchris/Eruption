package me.chris.eruption.setup.managers;

import me.chris.eruption.util.other.LocationUtil;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.setup.arena.Arena;
import me.chris.eruption.setup.arena.StandaloneArena;
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

		LocationUtil spawnMin = this.plugin.getSpawnManager().getSpawnMin();
		LocationUtil spawnMax = this.plugin.getSpawnManager().getSpawnMax();

		if (spawnMin != null && spawnMax != null) {
			int spawnMinX = spawnMin.toBukkitLocation().getBlockX() >> 4;
			int spawnMinZ = spawnMin.toBukkitLocation().getBlockZ() >> 4;
			int spawnMaxX = spawnMax.toBukkitLocation().getBlockX() >> 4;
			int spawnMaxZ = spawnMax.toBukkitLocation().getBlockZ() >> 4;

			if (spawnMinX > spawnMaxX) {
				int lastSpawnMinX = spawnMinX;
				spawnMinX = spawnMaxX;
				spawnMaxX = lastSpawnMinX;
			}

			if (spawnMinZ > spawnMaxZ) {
				int lastSpawnMinZ = spawnMinZ;
				spawnMinZ = spawnMaxZ;
				spawnMaxZ = lastSpawnMinZ;
			}

			for (int x = spawnMinX; x <= spawnMaxX; x++) {
				for (int z = spawnMinZ; z <= spawnMaxZ; z++) {

					Chunk chunk = spawnMin.toBukkitWorld().getChunkAt(x, z);

					if(!chunk.isLoaded()) {
						chunk.load();
					}

				}
			}
		}

		LocationUtil editorMin = this.plugin.getSpawnManager().getEditorMin();
		LocationUtil editorMax = this.plugin.getSpawnManager().getEditorMax();

		if (editorMin != null && editorMax != null) {
			int editorMinX = editorMin.toBukkitLocation().getBlockX() >> 4;
			int editorMinZ = editorMin.toBukkitLocation().getBlockZ() >> 4;
			int editorMaxX = editorMax.toBukkitLocation().getBlockX() >> 4;
			int editorMaxZ = editorMax.toBukkitLocation().getBlockZ() >> 4;

			if (editorMinX > editorMaxX) {
				int lastEditorMinX = editorMinX;
				editorMinX = editorMaxX;
				editorMaxX = lastEditorMinX;
			}

			if (editorMinZ > editorMaxZ) {
				int lastEditorMinZ = editorMinZ;
				editorMinZ = editorMaxZ;
				editorMaxZ = lastEditorMinZ;
			}

			for (int x = editorMinX; x <= editorMaxX; x++) {
				for (int z = editorMinZ; z <= editorMaxZ; z++) {

					Chunk chunk = editorMin.toBukkitWorld().getChunkAt(x, z);

					if(!chunk.isLoaded()) {
						chunk.load();
					}
				}
			}
		}

		LocationUtil sumoMin = this.plugin.getSpawnManager().getSumoMin();
		LocationUtil sumoMax = this.plugin.getSpawnManager().getSumoMax();

		if (sumoMin != null && sumoMax != null) {
			int sumoMinX = sumoMin.toBukkitLocation().getBlockX() >> 4;
			int sumoMinZ = sumoMin.toBukkitLocation().getBlockZ() >> 4;
			int sumoMaxX = sumoMax.toBukkitLocation().getBlockX() >> 4;
			int sumoMaxZ = sumoMax.toBukkitLocation().getBlockZ() >> 4;

			if (sumoMinX > sumoMaxX) {
				int lastSumoMinX = sumoMinX;
				sumoMinX = sumoMaxX;
				sumoMaxX = lastSumoMinX;
			}

			if (sumoMinZ > sumoMaxZ) {
				int lastSumoMaxZ = sumoMinZ;
				sumoMinZ = sumoMaxZ;
				sumoMaxZ = lastSumoMaxZ;
			}

			for (int x = sumoMinX; x <= sumoMaxX; x++) {
				for (int z = sumoMinZ; z <= sumoMaxZ; z++) {


					Chunk chunk = sumoMin.toBukkitWorld().getChunkAt(x, z);

					if(!chunk.isLoaded()) {
						chunk.load();
					}
				}
			}
		}

		LocationUtil oitcMin = this.plugin.getSpawnManager().getOitcMin();
		LocationUtil oitcMax = this.plugin.getSpawnManager().getOitcMax();

		if (oitcMin != null && oitcMax != null) {
			int oitcMinX = oitcMin.toBukkitLocation().getBlockX() >> 4;
			int oitcMinZ = oitcMin.toBukkitLocation().getBlockZ() >> 4;
			int oitcMaxX = oitcMax.toBukkitLocation().getBlockX() >> 4;
			int oitcMaxZ = oitcMax.toBukkitLocation().getBlockZ() >> 4;

			if (oitcMinX > oitcMaxX) {
				int lastOitcMinX = oitcMinX;
				oitcMinX = oitcMaxX;
				oitcMaxX = lastOitcMinX;
			}

			if (oitcMinZ > oitcMaxZ) {
				int lastOitcMaxZ = oitcMinZ;
				oitcMinZ = oitcMaxZ;
				oitcMaxZ = lastOitcMaxZ;
			}

			for (int x = oitcMinX; x <= oitcMaxX; x++) {
				for (int z = oitcMinZ; z <= oitcMaxZ; z++) {


					Chunk chunk = oitcMin.toBukkitWorld().getChunkAt(x, z);

					if(!chunk.isLoaded()) {
						chunk.load();
					}
				}
			}
		}


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
