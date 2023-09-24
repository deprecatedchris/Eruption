package me.chris.eruption.util.menu.pagination;

import lombok.Getter;
import me.chris.eruption.util.menu.Button;
import me.chris.eruption.util.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public abstract class PaginatedMenu extends Menu {

	@Getter private int page = 1;

	{
		setUpdateAfterClick(false);
	}

	@Override
	public String getTitle(Player player) {
		return getPrePaginatedTitle(player);
	}

	public final void modPage(Player player, int mod) {
		page += mod;
		getButtons().clear();
		openMenu(player);
	}

	public final int getPages(Player player) {
		int buttonAmount = getAllPagesButtons(player).size();

		if (buttonAmount == 0) {
			return 1;
		}

		return (int) Math.ceil(buttonAmount / (double) getMaxItemsPerPage(player));
	}

	@Override
	public final Map<Integer, Button> getButtons(Player player) {
		int minIndex = (int) ((double) (page - 1) * getMaxItemsPerPage(player));
		int maxIndex = (int) ((double) (page) * getMaxItemsPerPage(player));
		Button BLACK_PANE = Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, " ");
		HashMap<Integer, Button> buttons = new HashMap<>();

		buttons.put(0, new PageButton(-1, this));
		buttons.put(8, new PageButton(1, this));

		for (Map.Entry<Integer, Button> entry : getAllPagesButtons(player).entrySet()) {
			int ind = entry.getKey();
			if (ind >= minIndex && ind < maxIndex) {
				ind -= (int) ((double) (getMaxItemsPerPage(player)) * (page - 1)) - 9;
				buttons.put(ind, entry.getValue());
			}
		}

		Map<Integer, Button> global = getGlobalButtons(player);

		if (global != null) {
			for (Map.Entry<Integer, Button> gent : global.entrySet()) {
					buttons.put(gent.getKey(), gent.getValue());

			}
            for (int i = 0; i < 45; i++) {
                buttons.putIfAbsent(i, BLACK_PANE);
            }
		}

		return buttons;
	}

	public int getMaxItemsPerPage(Player player) {
		return 18;
	}

	protected void bottomTopButtons(boolean full, Map buttons, ItemStack itemStack) {
		IntStream.range(0, getSize()).filter(slot -> buttons.get(slot) == null).forEach(slot -> {
			if (slot < 9 || slot > getSize() - 10 || full && (slot % 9 == 0 || (slot + 1) % 9 == 0)) {
				buttons.put(slot, new Button() {
					@Override
					public ItemStack getButtonItem(Player player) {
						return itemStack;
					}
				});
			}
		});
	}

	public Map<Integer, Button> getGlobalButtons(Player player) {
		return null;
	}
	public abstract String getPrePaginatedTitle(Player player);
	public abstract Map<Integer, Button> getAllPagesButtons(Player player);

}
