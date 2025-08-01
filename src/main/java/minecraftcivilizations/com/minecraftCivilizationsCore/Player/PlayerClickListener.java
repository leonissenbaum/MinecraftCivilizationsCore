package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.AbilityCastEvent;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomAbility;
import minecraftcivilizations.com.minecraftCivilizationsCore.Item.CustomItem;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        CustomItem from = CustomItem.from(event.getPlayer().getInventory().getItemInMainHand());
        for (CustomAbility ability : from.getAbilities()) {
            if (action.isLeftClick() && ability.getCastEvent() == AbilityCastEvent.LEFT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            } else if (action.isLeftClick() && event.getPlayer().isSneaking() && ability.getCastEvent() == AbilityCastEvent.SNEAK_LEFT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            } else if (action.isRightClick() && ability.getCastEvent() == AbilityCastEvent.RIGHT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            } else if (action.isRightClick() && event.getPlayer().isSneaking() && ability.getCastEvent() == AbilityCastEvent.SNEAK_RIGHT_CLICK) {
                ability.getAbilityFunction().accept(event.getPlayer());
            }
        }
    }
}
