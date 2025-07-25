package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Component.ComponentUtils;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class CustomItem {
    private final ItemStack item;
    private final Recipe recipe;

    public CustomItem(ItemStack item, Recipe recipe) {
        this.item = item;
        this.recipe = recipe;
        reloadItem(item);
    }

    public static ItemStack newCustomItem(Material material, Component name, List<Component> lore, Plugin plugin) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.addItemFlags(ItemFlag.values());
        addLore(item, lore, plugin);
        return item;
    }

    public static void addLore(ItemStack item, List<Component> lore, Plugin plugin) {
        item.editPersistentDataContainer(persistentDataContainer -> {
            List<String> loreList = new ArrayList<>(lore.size());
            for (Component component : lore) {
                loreList.add(ComponentUtils.serializeComponent(component));
            }
            persistentDataContainer.set(
                    new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase() + "-" + plugin.getName().toLowerCase(), "lore"),
                    PersistentDataType.STRING,
                    new Gson().toJson(loreList, new TypeToken<List<String>>() {}.getType()));
        });
        reloadItem(item);
    }

    public static void reloadItem(ItemStack item) {
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        Set<NamespacedKey> lore = persistentDataContainerView.getKeys();
        MinecraftCivilizationsCore.logger.info(String.valueOf(lore));
        List<Component> completeLore = new ArrayList<>();
        for (NamespacedKey key : lore) {
            MinecraftCivilizationsCore.logger.info(key.toString());
            MinecraftCivilizationsCore.logger.info(key.toString());
            if (key.getNamespace().contains(MinecraftCivilizationsCore.getInstance().getName().toLowerCase()) && key.getKey().equals("lore")) {
                PersistentDataContainerView persistentDataContainer = item.getPersistentDataContainer();
                String value = persistentDataContainer.get(key, PersistentDataType.STRING);
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(new TypeToken<List<Component>>() {}.getType(), new ItemLoreDeserializer())
                        .create();
                completeLore.addAll(gson.fromJson(value, new TypeToken<List<Component>>() {}.getType()));
            }
        }
        item.editMeta(meta -> meta.lore(completeLore));
    }

}
