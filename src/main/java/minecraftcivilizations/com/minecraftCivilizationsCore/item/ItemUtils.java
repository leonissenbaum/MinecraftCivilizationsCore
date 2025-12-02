package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUIItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtils {

    public static boolean isGUIItemWithSpecificName(ItemStack item, String name) {
        if(item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.displayName().equals(Component.text(name).color(TextColor.fromHexString("#ffffff")).decoration(TextDecoration.ITALIC, false));
    }

    public static GUIItem makeItemGUIItem(ItemStack item, String name) {
        if(item == null || item.getItemMeta() == null) return null;
        ItemMeta meta = item.getItemMeta();
        if(name!=null){
            meta.displayName(Component.text(name).color(TextColor.fromHexString("#ffffff")).decoration(TextDecoration.ITALIC, false));
        }
        meta.addItemFlags(ItemFlag.values());
        meta.lore(new ArrayList<>());
        item.setItemMeta(meta);
        return new GUIItem(item, null);
    }

    public static GUIItem makeGUIItemOfType(Material material, String name) {
        return makeItemGUIItem(new ItemStack(material), name);
    }

    public static GUIItem makeGUIItemOfType(Material material) {
        return makeItemGUIItem(new ItemStack(material), getFriendlyName(material));
    }

    public static String getFriendlyName(Material material) {
        if (material == null) return null;
        // Split the enum name by underscores, capitalize each word, and join them
        String[] words = material.name().toLowerCase().split("_");
        StringBuilder friendlyName = new StringBuilder();
        for (String word : words) {
            friendlyName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }
        return friendlyName.toString().trim(); // Remove trailing space
    }
}
