package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.bukkit.inventory.Recipe;
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

//    public CustomItem(@NotNull NamespacedKey itemNamespacedKey, @NotNull NamespacedKey abilityNamespacedKey) {
//        CustomItem customItem = CustomItemRegistry.getItem(itemNamespacedKey);
//        CustomAbility customAbility = CustomItemAbilityRegistry.getAbility(abilityNamespacedKey);
//        customItem.getAbilities().add(customAbility);
//        ItemMeta meta = item.getItemMeta();
//        meta.displayName(name);
//        meta.addItemFlags(ItemFlag.values());
//        item.setItemMeta(meta);
//        addLore(List.of(lore));
//    }

    private static boolean isCustomItem(@NotNull ItemStack item) {
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        return persistentDataContainerView.has(new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "customItem"));
    }


    private void initializeEditingOfPersistentDataContainer() {
        if (!isCustomItem(item)) {
            item.editPersistentDataContainer(persistentDataContainer -> {
                persistentDataContainer.set(new NamespacedKey(MinecraftCivilizationsCore.getInstance(), "customItem"), PersistentDataType.BOOLEAN, true);
            });
        }
    }

    public static CustomItem from(@NotNull ItemStack item) {
        CustomItem customItem = new CustomItem();
        customItem.setItem(item);
        if (!isCustomItem(item)) {
            customItem.initializeEditingOfPersistentDataContainer();
            return customItem;
        }
        MinecraftCivilizationsCore.logger.info("Reloading custom item");
        customItem.reloadItem();
        return customItem;
    }

    public void setLore(Plugin plugin, List<Component> lore) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }
        item.editPersistentDataContainer(persistentDataContainer -> {
            Set<String> loreList = new HashSet<>(lore.size());
            for (Component component : lore) {
                loreList.add(ComponentUtils.serializeComponent(component));
            }
            persistentDataContainer.set(
                    new NamespacedKey(plugin.getName().toLowerCase(), "lore"),
                    PersistentDataType.STRING,
                    new Gson().toJson(loreList, new TypeToken<List<String>>() {}.getType()));
        });
        reloadItem();
    }

    public void addLore(Plugin plugin, List<Component> lore) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }

        item.editPersistentDataContainer(persistentDataContainer -> {
            Set<String> loreList = new HashSet<>(lore.size());
            for (Component component : lore) {
                loreList.add(ComponentUtils.serializeComponent(component));
            }
            List<Component> lore1 = getLoreFrom(plugin);
            if (lore1 != null) {
                for (Component component : lore1) {
                    loreList.add(ComponentUtils.serializeComponent(component));
                }
            }

            persistentDataContainer.set(
                    new NamespacedKey(plugin.getName().toLowerCase(), "lore"),
                    PersistentDataType.STRING,
                    new Gson().toJson(loreList, new TypeToken<List<String>>() {}.getType()));
        });
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

        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        String value = persistentDataContainerView.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"), PersistentDataType.STRING);
        Set<NamespacedKey> customAbilities = new  HashSet<>(0);
        customAbilities = new Gson().fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType());
        if (customAbilities == null) {
            customAbilities = new HashSet<>(0);
        }
        customAbilities.add(customAbility);

        Set<NamespacedKey> finalCustomAbilities = customAbilities;
        item.editPersistentDataContainer(persistentDataContainer -> {
            persistentDataContainer.set(
                    new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"),
                    PersistentDataType.STRING,
                    new Gson().toJson(finalCustomAbilities, new TypeToken<Set<NamespacedKey>>() {}.getType()));
        });
        MinecraftCivilizationsCore.logger.info("Added ability to item persistent data: " + ability.getName());
    }

    public List<Component> getLore() {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        List<Component> lore = new ArrayList<>(0);

        for (NamespacedKey key : persistentDataContainerView.getKeys()) {
            if (key.getKey().equals("lore")) {
                String value = persistentDataContainerView.get(key, PersistentDataType.STRING);
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
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        List<Component> lore = new ArrayList<>(0);
        String lore1 = persistentDataContainerView.get(new NamespacedKey(plugin.getName().toLowerCase(), "lore"), PersistentDataType.STRING);
        List<String> loreList = new Gson().fromJson(lore1, new TypeToken<List<String>>() {}.getType());
        if (loreList == null) return null;
        for (String s : loreList) {
            lore.add(ComponentUtils.deserializeComponent(s));
        }
        return lore;
    }

    private Set<CustomAbility> getCustomAbilities() {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        String value = persistentDataContainerView.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"), PersistentDataType.STRING);
        if (value == null) return null;
        Set<CustomAbility> customAbilities = new HashSet<>(0);
        MinecraftCivilizationsCore.logger.info(String.valueOf(value));
        Set<NamespacedKey> namespacedKeys = new Gson().fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType());
        MinecraftCivilizationsCore.logger.info(String.valueOf(namespacedKeys));
        for (NamespacedKey namespacedKey : namespacedKeys) {
            MinecraftCivilizationsCore.logger.info(String.valueOf(namespacedKey));
            customAbilities.add(CustomItemAbilityRegistry.getAbility(namespacedKey));
        }
        return customAbilities;
    }







    public void reloadItem() {
        if (!isCustomItem(item)) return;
        List<Component> lore = getLore();
        if (lore == null) return;
        item.editMeta(meta -> meta.lore(lore));
        Set<CustomAbility> customAbilities = getCustomAbilities();
        if (customAbilities == null) return;
        for (CustomAbility customAbility : customAbilities) {
            MinecraftCivilizationsCore.logger.info(String.valueOf(customAbility));
            MinecraftCivilizationsCore.logger.info(customAbility.getCastEvent().name());
        }
        abilities.clear();
        abilities.addAll(customAbilities);
        for (CustomAbility customAbility : abilities) {
            MinecraftCivilizationsCore.logger.info(String.valueOf(customAbility.getName()));
            MinecraftCivilizationsCore.logger.info(customAbility.getCastEvent().name());
        }
    }
}
