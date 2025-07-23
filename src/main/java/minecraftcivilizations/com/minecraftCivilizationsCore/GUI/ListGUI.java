package minecraftcivilizations.com.minecraftCivilizationsCore.GUI;

import lombok.Getter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.GUIPlaceOption;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.Option;
import minecraftcivilizations.com.minecraftCivilizationsCore.item.ItemUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListGUI extends GUI {
    List<ItemStack> show = new ArrayList<>();


    /**
     *
     * Doesn't work if you have an inventory size less then 27
     *
     * @param title
     * @param items
     */
    public ListGUI(Component title, ArrayList<ItemStack> items) {
        super(title, 54, Map.of(GUIPlaceOption.SHOULD_PLACE_EXIT, true, GUIPlaceOption.SHOULD_PLACE_SEARCH, true));
        show.addAll(items);
        int itemCount = 0;
        for (int placementIndex = 10; placementIndex < 44; placementIndex++) {
            if (placementIndex % 9 != 0 && placementIndex % 9 != 8 && itemCount < items.size()) {
                if(items.get(itemCount).getType().isItem() && items.get(itemCount).getType() != Material.AIR) {
                    this.getItems().put(placementIndex, ItemUtils.makeItemGUIItem(items.get(itemCount), ItemUtils.getFriendlyName(items.get(itemCount).getType())));
                }
                itemCount++;
            }
        }
        if (items.size() > getSize() - (18 + (getSize()/9 - 2) * 2)) {
            this.getItems().put(getSize() - 1, new GUIItem(ItemUtils.makeGUIItemOfType(Material.ARROW, "Next").getItem(), () -> next((Player) getInventory().getViewers().getFirst())));
        }
    }

    @Override
    public void open(Player player) {
        if (getParentGUI() != null) {
            if (getParentGUI() instanceof ListGUI && (getOptions().get(GUIPlaceOption.SHOULD_PLACE_BACK) == null || !getOptions().get(GUIPlaceOption.SHOULD_PLACE_BACK))) {
                this.getItems().put(getSize() - 9, new GUIItem(ItemUtils.makeGUIItemOfType(Material.ARROW, "Back").getItem(), () -> getParentGUI().open((Player) getInventory().getViewers().getFirst())));
            } else {
                getOptions().put(GUIPlaceOption.SHOULD_PLACE_BACK, true);
            }
        }
        super.open(player);
    }

    public void next(Player player) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = getSize() - (18 + (getSize()/9 - 2) * 2); i < show.size(); i++) {
            items.add(show.get(i));
//            if (i > getSize() - (18 + (getSize()/9 - 2) * 2)) {
//            }
        }
        new ListGUI(Component.text("Search Results"), items).setParentGUI(this).open(player);
    }
}
