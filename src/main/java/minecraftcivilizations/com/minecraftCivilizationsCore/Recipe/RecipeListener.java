package minecraftcivilizations.com.minecraftCivilizationsCore.Recipe;

import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.Recipe;

public class RecipeListener implements Listener {
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();

        if (recipe instanceof Keyed keyedRecipe && event.getView().getPlayer() instanceof Player player) {
            NamespacedKey key = keyedRecipe.getKey();

            if (!player.hasDiscoveredRecipe(key)) {
                MinecraftCivilizationsCore.logger.info("Player " + player.getName() + " has not discovered recipe " + key.toString());
                event.getInventory().setResult(null); // Cancel the crafting result
            }
        }
    }

//    @EventHandler
//    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
//        event.setCancelled(true); // Block from learning it
//    }
}
