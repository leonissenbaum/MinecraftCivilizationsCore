package minecraftcivilizations.com.minecraftCivilizationsCore.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerClickListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();

        if (action == Action.LEFT_CLICK_AIR) {
            event.getPlayer().sendMessage("You left-clicked in the air!");
        } else if (action == Action.RIGHT_CLICK_AIR) {
            event.getPlayer().sendMessage("You right-clicked in the air!");
        }
    }
}
