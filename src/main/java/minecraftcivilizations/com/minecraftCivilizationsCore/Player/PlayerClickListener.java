package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

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

        if (action == Action.LEFT_CLICK_AIR) {
            CustomItem from = CustomItem.from(event.getPlayer().getInventory().getItemInMainHand());
            MinecraftCivilizationsCore.logger.info(String.valueOf(from));
            for (CustomAbility ability : from.getAbilities()) {
                ability.getAbilityFunction().accept(event.getPlayer());
            }
            event.getPlayer().sendMessage("You left-clicked in the air!");
        } else if (action == Action.RIGHT_CLICK_AIR) {
            event.getPlayer().sendMessage("You right-clicked in the air!");
        }
    }
}
