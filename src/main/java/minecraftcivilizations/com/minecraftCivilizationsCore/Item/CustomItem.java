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
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NoArgsConstructor
public class CustomItem {
    @Getter
    @Setter
    private ItemStack item;
    @Getter
    private Set<CustomAbility> abilities = new HashSet<>(0);

    public CustomItem(@NotNull Material material, @NotNull Component name, @NotNull Component... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);
        meta.addItemFlags(ItemFlag.values());
        item.setItemMeta(meta);
        addLore(List.of(lore));
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
        }
        customItem.reloadItem();
        return customItem;
    }

    public void addLore(List<Component> lore) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }

        item.editPersistentDataContainer(persistentDataContainer -> {
            List<String> loreList = new ArrayList<>(lore.size());
            for (Component component : lore) {
                loreList.add(ComponentUtils.serializeComponentAsString(component));
            }
            List<Component> lore1 = getLore();
            if (lore1 != null) {
                for (Component component : lore1) {
                    loreList.add(ComponentUtils.serializeComponentAsString(component));
                }
            }

            persistentDataContainer.set(
                    new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "lore"),
                    PersistentDataType.STRING,
                    new Gson().toJson(loreList, new TypeToken<List<String>>() {}.getType()));
        });
        reloadItem();
    }

    public void addAbility(NamespacedKey customAbility) {
        if (!isCustomItem(item)) {
            initializeEditingOfPersistentDataContainer();
        }
        PersistentDataContainerView persistentDataContainer = item.getPersistentDataContainer();
        Set<NamespacedKey> abilitiesNamespacedKeys = new Gson().fromJson(persistentDataContainer.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"), PersistentDataType.STRING), new TypeToken<Set<NamespacedKey>>() {}.getType());

        CustomAbility ability = CustomItemAbilityRegistry.getAbility(customAbility);
        if (ability != null) {
            abilities.add(ability);
        }

        item.editPersistentDataContainer(dataContainer -> {
            dataContainer.set(
                    new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"),
                    PersistentDataType.STRING,
                    new Gson().toJson(abilities, new TypeToken<Set<NamespacedKey>>() {}.getType()));
        });
    }

    public List<Component> getLore() {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        String value = persistentDataContainerView.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "lore"), PersistentDataType.STRING);
        List<String> loreList = new Gson().fromJson(value, new TypeToken<List<String>>() {}.getType());
        if (loreList == null) return null;
        List<Component> lore = new ArrayList<>(0);
        for (String s : loreList) {
            lore.add(ComponentUtils.deserializeStringAsComponent(s));
        }
        return lore;
    }

    private Set<CustomAbility> getCustomAbilities() {
        if (!isCustomItem(item)) return null;
        PersistentDataContainerView persistentDataContainerView = item.getPersistentDataContainer();
        String value = persistentDataContainerView.get(new NamespacedKey(MinecraftCivilizationsCore.getInstance().getName().toLowerCase(), "abilities"), PersistentDataType.STRING);
        if (value == null) return null;
        Set<CustomAbility> customAbilities = new HashSet<>(0);
        for (NamespacedKey namespacedKey : (Set<NamespacedKey>) new Gson().fromJson(value, new TypeToken<Set<NamespacedKey>>() {}.getType())) {
            customAbilities.add(CustomItemAbilityRegistry.getAbility(namespacedKey));
        }
        return customAbilities;
    }







    public void reloadItem() {
        if (!isCustomItem(item)) return;
        List<Component> lore = getLore();
        if (lore == null) return;
        item.editMeta(meta -> meta.lore(lore));
    }



}
