package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import io.papermc.paper.persistence.PersistentDataContainerView;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.AbilityCastEvent;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomAbility;
import minecraftcivilizations.com.minecraftCivilizationsCore.Item.CustomItem;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        ItemStack inHand = event.getPlayer().getInventory().getItemInMainHand();
        if (!CustomItem.isCustomItem(inHand)) {
            return;
        }

        CustomItem from = CustomItem.from(inHand);
        for (CustomAbility ability : from.getAbilities()) {
            if (action.isLeftClick() && ability.getCastEvent() == AbilityCastEvent.LEFT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            } else if (action.isLeftClick() && event.getPlayer().isSneaking()
                    && ability.getCastEvent() == AbilityCastEvent.SNEAK_LEFT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            } else if (action.isRightClick() && ability.getCastEvent() == AbilityCastEvent.RIGHT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            } else if (action.isRightClick() && event.getPlayer().isSneaking()
                    && ability.getCastEvent() == AbilityCastEvent.SNEAK_RIGHT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            }
        }
    }

}