package me.chris.eruption.util.runnable;

import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.profile.PlayerData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SaveDataRunnable implements Runnable {

	private final EruptionPlugin plugin = EruptionPlugin.getInstance();

	@Override
	public void run() {
		for (PlayerData playerData : this.plugin.getPlayerManager().getAllData()) {
			this.plugin.getPlayerManager().saveData(playerData);
			this.plugin.getArenaManager().saveArenas();
			this.plugin.getKitManager().saveKits();
			this.plugin.getMainConfig().save();
		}
	}

}
