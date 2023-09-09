package me.chris.eruption.util.menu.buttons;

import lombok.AllArgsConstructor;
import me.chris.eruption.util.CC;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.other.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class ProcessButton extends Button {

    private String name;
    private List<String> description;
    private Material material;
    private int amount;
    private Prompt prompt;
    private ConversationFactory conversationFactory;

    @Override
    public String getName(Player var1) {
        return null;
    }

    @Override
    public List<String> getDescription(Player var1) {
        return null;
    }

    @Override
    public Material getMaterial(Player var1) {
        return null;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder it = ItemBuilder.of(material, amount).name(CC.translate(name)).lore(CC.translate(description));
        return it.build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        conversationFactory.withFirstPrompt(prompt).withPrefix(new NullConversationPrefix()).withLocalEcho(false).withEscapeSequence("cancel").buildConversation(player).begin();
    }
}
