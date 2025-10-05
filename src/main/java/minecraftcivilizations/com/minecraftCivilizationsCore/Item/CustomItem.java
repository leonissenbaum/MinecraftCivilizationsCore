package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomAbility;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomItemAbilityRegistry;
import minecraftcivilizations.com.minecraftCivilizationsCore.Component.ComponentUtils;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@NoArgsConstructor
public class CustomItem {

    @Getter
    @Setter
    private ItemStack item;

    @Getter
    private final Set<CustomAbility> abilities = new HashSet<>(0);

    public CustomItem(@NotNull Material material, @NotNull Component name) {
        item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
    }

    public static boolean isCustomItem(@NotNull ItemStack item) {
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        return pdc.has(new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "customItem"));
    }

    private void initializeEditingOfPersistentDataContainer() {
        if (!isCustomItem(item)) {
            item.editPersistentDataContainer(pdc ->
                    pdc.set(new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "customItem"),
                            PersistentDataType.BOOLEAN, true)
            );
        }
    }

    // === FIXED: No more reloadItem() inside from() ===
    public static CustomItem from(@NotNull ItemStack item) {
        CustomItem customItem = new CustomItem();
        customItem.setItem(item);
        if (!isCustomItem(item)) {
            customItem.initializeEditingOfPersistentDataContainer();
            return customItem;
        }
        Set<CustomAbility> customAbilities = customItem.getCustomAbilities();
        customItem.abilities.clear();
        if (customAbilities != null) customItem.abilities.addAll(customAbilities);
        return customItem;
    }

    public void setLore(Plugin plugin, List<Component> lore) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }
        List<String> loreList = new ArrayList<>(lore.size());
        for (Component c : lore) {
            loreList.add(ComponentUtils.serializeComponent(c));
        }
        item.editPersistentDataContainer(pdc -> pdc.set(
                new NamespacedKey(plugin.getName().toLowerCase(), "lore"),
                PersistentDataType.STRING,
                new Gson().toJson(loreList, new TypeToken<List<String>>() {}.getType())
        ));
        reloadItem();
    }

    public void addLore(Plugin plugin, List<Component> lore) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }
        List<Component> existing = getLoreFrom(plugin);
        List<String> merged = new ArrayList<>();
        if (existing != null) {
            for (Component c : existing) merged.add(ComponentUtils.serializeComponent(c));
        }
        for (Component c : lore) {
            String s = ComponentUtils.serializeComponent(c);
            if (!merged.contains(s)) merged.add(s);
        }
        item.editPersistentDataContainer(pdc -> pdc.set(
                new NamespacedKey(plugin.getName().toLowerCase(), "lore"),
                PersistentDataType.STRING,
                new Gson().toJson(merged, new TypeToken<List<String>>() {}.getType())
        ));
        reloadItem();
    }

    public void addAbility(NamespacedKey customAbility) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }
        CustomAbility ability = CustomItemAbilityRegistry.getAbility(customAbility);
        if (ability == null) return;
        MinecraftCivilizationsCore.logger.info("Adding ability to item from registry: " + ability.getName());
        abilities.add(ability);

        PersistentDataContainerView pdcView = item.getPersistentDataContainer();
        String value = pdcView.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(),
                "abilities"), PersistentDataType.STRING);

        Set<NamespacedKey> customAbilities = new HashSet<>();
        if (value != null) {
            customAbilities = new Gson().fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType());
            if (customAbilities == null) customAbilities = new HashSet<>();
        }
        customAbilities.add(customAbility);

        Set<NamespacedKey> finalCustomAbilities = customAbilities;
        item.editPersistentDataContainer(pdc -> pdc.set(
                new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"),
                PersistentDataType.STRING,
                new Gson().toJson(finalCustomAbilities, new TypeToken<Set<NamespacedKey>>() {}.getType())
        ));
        MinecraftCivilizationsCore.logger.info("Added ability to item persistent data: " + ability.getName());
    }

    public List<Component> getLore() {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        List<Component> lore = new ArrayList<>();
        for (NamespacedKey key : pdc.getKeys()) {
            if (key.getKey().equals("lore")) {
                String value = pdc.get(key, PersistentDataType.STRING);
                List<String> loreList = new Gson().fromJson(value, new TypeToken<List<String>>() {}.getType());
                if (loreList == null) return null;
                for (String s : loreList) {
                    lore.add(ComponentUtils.deserializeComponent(s));
                }
            }
        }
        return lore;
    }

    public List<Component> getLoreFrom(Plugin plugin) {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        String loreStr = pdc.get(new NamespacedKey(plugin.getName().toLowerCase(), "lore"), PersistentDataType.STRING);
        List<String> loreList = new Gson().fromJson(loreStr, new TypeToken<List<String>>() {}.getType());
        if (loreList == null) return null;
        List<Component> lore = new ArrayList<>();
        for (String s : loreList) {
            lore.add(ComponentUtils.deserializeComponent(s));
        }
        return lore;
    }

    private Set<CustomAbility> getCustomAbilities() {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView pdc = item.getPersistentDataContainer();
        String value = pdc.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(),
                "abilities"), PersistentDataType.STRING);
        if (value == null) return null;
        Set<CustomAbility> customAbilities = new HashSet<>();
        Set<NamespacedKey> keys = new Gson().fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType());
        for (NamespacedKey key : keys) {
            customAbilities.add(CustomItemAbilityRegistry.getAbility(key));
        }
        return customAbilities;
    }

    public void reloadItem() {
        if (!isCustomItem(item)) return;
        List<Component> lore = getLore();
        if (lore != null) {
            ItemMeta meta = item.getItemMeta();
            List<Component> current = meta.lore();
            if (current == null || !current.equals(lore)) {
                meta.lore(lore);
                item.setItemMeta(meta);
            }
        }
        Set<CustomAbility> customAbilities = getCustomAbilities();
        abilities.clear();
        if (customAbilities != null) abilities.addAll(customAbilities);
    }
}
