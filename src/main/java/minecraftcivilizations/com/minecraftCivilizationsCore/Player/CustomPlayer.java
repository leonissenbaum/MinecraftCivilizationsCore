package minecraftcivilizations.com.minecraftCivilizationsCore.Player;


import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.*;


public class CustomPlayer {
    @Getter
    @Setter
    private UUID uuid;
    @Getter
    @Setter
    private UUID currentGUI;
    @Getter
    @Setter
    private UUID nextGUI;
    @Getter
    @Setter
    private Map<NamespacedKey, Long> abilitiesCastHistory = new HashMap<>();

    public CustomPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Component getName() {
        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
        return Component.text(playerName == null ? uuid.toString() : playerName);
    }

    public void setName(Component name) {
        // Player names are no longer obfuscated; this is intentionally a no-op.
    }

}
