package me.chris.eruption.kit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.chris.eruption.EruptionPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@AllArgsConstructor
public class PlayerKit {

    private final String name;
    private final int index;

    private ItemStack[] contents;
    private String displayName;

    public void applyToPlayer(Player player) {
        for (ItemStack itemStack : this.contents) {
            if (itemStack != null) {
                if (itemStack.getAmount() <= 0) {
                    itemStack.setAmount(1);
                }
            }
        }

        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(EruptionPlugin.getInstance().getKitManager().getKit(this.name).getArmor());
        player.updateInventory();
    }
}