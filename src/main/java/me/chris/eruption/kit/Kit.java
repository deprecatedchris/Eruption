package me.chris.eruption.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Kit {

    private final String name;
    private ItemStack[] contents = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];
    private ItemStack[] kitEditContents = new ItemStack[36];
    private ItemStack icon;
    private ItemStack leaderboardIcon;

    private List<String> excludedArenas = new ArrayList<>();
    private List<String> arenaWhiteList = new ArrayList<>();

    private boolean enabled;
    private boolean ranked;
    private int queueMenu = 0;

    private Flag flag = Flag.DEFAULT;

    public void applyToPlayer(Player player) {
        player.getInventory().setContents(contents);
        player.getInventory().setArmorContents(armor);
        player.updateInventory();
    }


    public void whitelistArena(String arena) {
        if (!this.arenaWhiteList.remove(arena)) {
            this.arenaWhiteList.add(arena);
        }
    }

    public void excludeArena(String arena) {
        if (!this.excludedArenas.remove(arena)) {
            this.excludedArenas.add(arena);
        }
    }

    public boolean isBuild(){
        return flag.equals(Flag.BUILD);
    }

    public boolean isSpleef(){
        return flag.equals(Flag.SPLEEF);
    }

    public boolean isCombo(){
        return flag.equals(Flag.COMBO);
    }

}
