package minecraftcivilizations.com.minecraftCivilizationsCore.Ability;

import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class CustomItemAbilityRegistry {
    private static final Map<String, CustomAbility> abilities = new HashMap<>();

    public static void register(String id, CustomAbility ability) {
        abilities.put(id, ability);
    }

    public static void register(Plugin plugin, CustomAbility ability) {
        abilities.put(plugin.getName().toLowerCase(), ability);
    }

    public static CustomAbility get(String id) {
        return abilities.get(id);
    }

    public static CustomAbility get(Plugin plugin) {
        return abilities.get(plugin.getName().toLowerCase());
    }
}
