package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItemRegistry {
    public static final Map<String, CustomItem> items = new HashMap<>();

    public static void register(String key, CustomItem item) {
        items.put(key, item);
    }

    public static CustomItem get(String key) {
        return items.get(key);
    }

    public static List<CustomItem> getAll() {
        return new ArrayList<>(items.values());
    }
}
