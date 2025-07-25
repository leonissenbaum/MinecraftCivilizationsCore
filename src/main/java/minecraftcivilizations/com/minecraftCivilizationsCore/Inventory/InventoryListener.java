package minecraftcivilizations.com.minecraftCivilizationsCore.Inventory;

import minecraftcivilizations.com.minecraftCivilizationsCore.Item.CustomItem;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.ArrayList;

public class InventoryListener implements Listener {
    @EventHandler
    public void event(InventoryClickEvent event) {
        CustomItem.reloadItem(event.getCursor());
        CustomItem.reloadItem(event.getCurrentItem());
    }

    @EventHandler
    public void event(InventoryDragEvent event) {
        CustomItem.reloadItem(event.getCursor());
        CustomItem.reloadItem(event.getOldCursor());
    }

    @EventHandler
    public void event(InventoryCreativeEvent event) {
        CustomItem.reloadItem(event.getCurrentItem());
        CustomItem.reloadItem(event.getCursor());
    }

    @EventHandler
    public void event(InventoryInteractEvent event) {
        CustomItem.reloadItem(event.getView().getCursor());
    }

    @EventHandler
    public void event(EntityPickupItemEvent event) {
        CustomItem.reloadItem(event.getItem().getItemStack());
    }

    @EventHandler
    public void event(PlayerDropItemEvent event) {
        CustomItem.reloadItem(event.getItemDrop().getItemStack());
    }

    @EventHandler
    public void event(PlayerSwapHandItemsEvent event) {
        CustomItem.addLore(event.getMainHandItem(),
                new ArrayList<>() {
                    {
                        add(Component.text("Test"));
                    }
                },
                MinecraftCivilizationsCore.getInstance()
                );
        CustomItem.reloadItem(event.getMainHandItem());
        CustomItem.reloadItem(event.getOffHandItem());
    }

    @EventHandler
    public void event(PlayerItemHeldEvent event) {
        CustomItem.reloadItem(event.getPlayer().getInventory().getItem(event.getNewSlot()));
        CustomItem.reloadItem(event.getPlayer().getInventory().getItem(event.getPreviousSlot()));
    }
}
