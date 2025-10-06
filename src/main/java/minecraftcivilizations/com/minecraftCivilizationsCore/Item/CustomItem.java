package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.papermc.paper.persistence.PersistentDataContainerView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomAbility;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomItemAbilityRegistry;
import minecraftcivilizations.com.minecraftCivilizationsCore.Component.ComponentUtils;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

    private static final Gson GSON = new Gson();
    private static final NamespacedKey CUSTOM_ITEM_KEY =
            new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "customItem");
    private static final NamespacedKey LORE_KEY =
            new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "lore");
    private static final NamespacedKey ABILITIES_KEY =
            new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "abilities");

    @Getter @Setter
    private ItemStack item;

    @Getter
    private final Set<CustomAbility> abilities = new HashSet<>(0);

    public CustomItem(@NotNull org.bukkit.Material material, @NotNull Component name) {
        item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name.decoration(TextDecoration.ITALIC, false));
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        initializeEditingOfPersistentDataContainer();
    }

    public static boolean isCustomItem(@NotNull ItemStack item) {
        return item.getPersistentDataContainer().has(CUSTOM_ITEM_KEY);
    }

    private void initializeEditingOfPersistentDataContainer() {
        if (!isCustomItem(item)) {
            item.editPersistentDataContainer(pdc -> pdc.set(CUSTOM_ITEM_KEY, PersistentDataType.BOOLEAN, true));
        }
    }

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
        initializeEditingOfPersistentDataContainer();
        cleanLegacyLoreKeys();
        List<String> serialized = new ArrayList<>(lore.size());
        for (Component c : lore) serialized.add(ComponentUtils.serializeComponent(c));
        String newJson = GSON.toJson(serialized, new TypeToken<List<String>>() {}.getType());

        String oldJson = item.getPersistentDataContainer().get(LORE_KEY, PersistentDataType.STRING);
        if (!Objects.equals(oldJson, newJson)) {
            item.editPersistentDataContainer(pdc -> pdc.set(LORE_KEY, PersistentDataType.STRING, newJson));
            reloadItem();
        }
    }

    public void addLore(Plugin plugin, List<Component> lore) {
        initializeEditingOfPersistentDataContainer();
        cleanLegacyLoreKeys();

        List<Component> existing = getLore();
        LinkedHashSet<String> merged = new LinkedHashSet<>();
        if (existing != null) {
            for (Component c : existing) merged.add(ComponentUtils.serializeComponent(c));
        }
        for (Component c : lore) merged.add(ComponentUtils.serializeComponent(c));

        String newJson = GSON.toJson(new ArrayList<>(merged), new TypeToken<List<String>>() {}.getType());
        String oldJson = item.getPersistentDataContainer().get(LORE_KEY, PersistentDataType.STRING);
        if (!Objects.equals(oldJson, newJson)) {
            item.editPersistentDataContainer(pdc -> pdc.set(LORE_KEY, PersistentDataType.STRING, newJson));
            reloadItem();
        }
    }

    public void addAbility(NamespacedKey customAbility) {
        initializeEditingOfPersistentDataContainer();
        CustomAbility ability = CustomItemAbilityRegistry.getAbility(customAbility);
        if (ability == null) return;

        abilities.add(ability);

        PersistentDataContainerView pdcView = item.getPersistentDataContainer();
        String value = pdcView.get(ABILITIES_KEY, PersistentDataType.STRING);

        Set<NamespacedKey> customAbilities = new HashSet<>();
        if (value != null) {
            customAbilities = GSON.fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType());
            if (customAbilities == null) customAbilities = new HashSet<>();
        }
        customAbilities.add(customAbility);

        Set<NamespacedKey> finalCustomAbilities = customAbilities;
        item.editPersistentDataContainer(pdc -> pdc.set(
                ABILITIES_KEY,
                PersistentDataType.STRING,
                GSON.toJson(finalCustomAbilities, new TypeToken<Set<NamespacedKey>>() {}.getType())
        ));
    }

    public List<Component> getLore() {
        if (!isCustomItem(item)) return null;
        String loreStr = item.getPersistentDataContainer().get(LORE_KEY, PersistentDataType.STRING);
        List<String> loreList = GSON.fromJson(loreStr, new TypeToken<List<String>>() {}.getType());
        if (loreList == null) return null;
        List<Component> out = new ArrayList<>(loreList.size());
        for (String s : loreList) {
            out.add(ComponentUtils.deserializeComponent(s).decoration(TextDecoration.ITALIC, false));
        }
        return out;
    }

    public List<Component> getLoreFrom(Plugin plugin) {
        return getLore();
    }

    private Set<CustomAbility> getCustomAbilities() {
        if (!isCustomItem(item)) return null;
        String value = item.getPersistentDataContainer().get(ABILITIES_KEY, PersistentDataType.STRING);
        if (value == null) return null;
        Set<CustomAbility> customAbilities = new HashSet<>();
        Set<NamespacedKey> keys = GSON.fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType());
        if (keys != null) {
            for (NamespacedKey key : keys) {
                CustomAbility ability = CustomItemAbilityRegistry.getAbility(key);
                if (ability != null) customAbilities.add(ability);
            }
        }
        return customAbilities;
    }

    public void reloadItem() {
        if (!isCustomItem(item)) return;

        cleanLegacyLoreKeys();

        List<Component> pdcLore = getLore();
        if (pdcLore != null) {
            ItemMeta meta = item.getItemMeta();
            List<Component> current = meta.lore();
            if (current == null || !current.equals(pdcLore)) {
                meta.lore(pdcLore);
                item.setItemMeta(meta);
            }
        }

        Set<CustomAbility> customAbilities = getCustomAbilities();
        abilities.clear();
        if (customAbilities != null) abilities.addAll(customAbilities);
    }

    private void cleanLegacyLoreKeys() {
        item.editPersistentDataContainer(pdc -> {
            for (NamespacedKey key : new HashSet<>(pdc.getKeys())) {
                if ("lore".equals(key.getKey()) && !key.equals(LORE_KEY)) {
                    pdc.remove(key);
                }
            }
        });
    }
}
