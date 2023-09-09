package me.chris.eruption.profile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.chris.eruption.EruptionPlugin;
import me.chris.eruption.kit.Kit;
import me.chris.eruption.kit.PlayerKit;
import me.chris.eruption.setting.SettingsInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {

    public static final int DEFAULT_ELO = 1000;

    private final Map<String, Map<Integer, PlayerKit>> playerKits = new HashMap<>();
    private final Map<String, Integer> rankedLosses = new HashMap<>();
    private final Map<String, Integer> rankedWins = new HashMap<>();
    private final Map<String, Integer> rankedElo = new HashMap<>();
    private final Map<String, Integer> partyElo = new HashMap<>();
    private Map<String, PlayerKit[]> kits = new HashMap<>();

    @Getter private final UUID uniqueId;
    @Getter private PlayerState playerState = PlayerState.LOADING;
    @Getter private SettingsInfo settings = new SettingsInfo();
    @Getter private UUID currentMatchID;
    @Getter private UUID duelSelecting;

    @Getter private int eloRange = 250;
    @Getter private int pingRange = 50;


    @Getter private int teamID = -1;
    @Getter private int rematchID = -1;
    @Getter private int missedPots;
    @Getter private int thrownPots;
    @Getter private int longestCombo;

    @Getter private int combo;
    @Getter private int hits;


    @Getter private int oitcKills;
    @Getter private int oitcDeaths;
    @Getter private int oitcWins;
    @Getter private int oitcLosses;

    @Getter private int runnerWins;
    @Getter private int runnerLosses;

    @Getter private int lmsWins;
    @Getter private int lmsLosses;

    @Getter private int cornersWins;
    @Getter private int cornersLosses;

    @Getter private int sumoWins;
    @Getter private int sumoLosses;

    @Getter private int parkourWins;
    @Getter private int parkourLosses;

    @Getter private int tournamentWins;
    @Getter private int tournamentLosses;

    @Getter @Setter private Kit selectedLadder;
    @Getter @Setter private PlayerKit selectedKit;

    @Getter private boolean active;
    @Getter private boolean rename;


    public PlayerKit[] getKits(Kit ladder) {
        return this.kits.get(ladder.getName());
    }

    public PlayerKit getKit(Kit ladder, int index) {
        return this.kits.get(ladder.getName())[index];
    }

    public void replaceKit(Kit ladder, int index, PlayerKit kit) {
        PlayerKit[] kits = this.kits.get(ladder.getName());
        kits[index] = kit;

        this.kits.put(ladder.getName(), kits);
    }

    public void deleteKit(Kit ladder, PlayerKit kit) {
        if (kit == null) {
            return;
        }

        PlayerKit[] kits = this.kits.get(ladder.getName());

        for (int i = 0; i < 4; i++) {
            if (kits[i] != null && kits[i].equals(kit)) {
                kits[i] = null;
                break;
            }
        }

        this.kits.put(ladder.getName(), kits);
    }

    public int getWins(String kitName) {
        return this.rankedWins.computeIfAbsent(kitName, k -> 0);
    }

    public void setWins(String kitName, int wins) {
        this.rankedWins.put(kitName, wins);
    }

    public int getLosses(String kitName) {
        return this.rankedLosses.computeIfAbsent(kitName, k -> 0);
    }

    public void setLosses(String kitName, int losses) {
        this.rankedLosses.put(kitName, losses);
    }

    public int getElo(String kitName) {
        return this.rankedElo.computeIfAbsent(kitName, k -> PlayerData.DEFAULT_ELO);
    }

    public void setElo(String kitName, int elo) {
        this.rankedElo.put(kitName, elo);
    }

    public int getPartyElo(String kitName) {
        return this.partyElo.computeIfAbsent(kitName, k -> PlayerData.DEFAULT_ELO);
    }

    public void setPartyElo(String kitName, int elo) {
        this.partyElo.put(kitName, elo);
    }

    public void addPlayerKit(int index, PlayerKit playerKit) {
        this.getPlayerKits(playerKit.getName()).put(index, playerKit);
    }

    public boolean isInMatch() {
        return (this.playerState == PlayerState.FIGHTING);
    }

    public boolean isRenaming() {
        return this.active && this.rename && this.selectedKit != null;
    }

    public Map<Integer, PlayerKit> getPlayerKits(String kitName) {
        return this.playerKits.computeIfAbsent(kitName, k -> new HashMap<>());
    }

    public int getGlobalStats(String type) {
        int i = 0;
        int count = 0;

        for (Kit kit : EruptionPlugin.getInstance().getKitManager().getKits()) {

            switch (type.toUpperCase()) {
                case "ELO":
                    i += getElo(kit.getName());
                    break;
                case "WINS":
                    i += getWins(kit.getName());
                    break;
                case "LOSSES":
                    i += getLosses(kit.getName());
                    break;

            }

            count++;


        }

        if (i == 0) {
            i = 1;
        }
        if (count == 0) {
            count = 1;
        }

        return type.toUpperCase().equalsIgnoreCase("ELO") ? Math.round(i / count) : i;
    }

}
