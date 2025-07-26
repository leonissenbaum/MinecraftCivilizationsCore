package minecraftcivilizations.com.minecraftCivilizationsCore.Player;


import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomAbility;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.*;

@Getter
@Setter
public class CustomPlayer {
    private final UUID uuid;
    private UUID currentGUI;
    private UUID nextGUI;
    private Component name;
    private Map<NamespacedKey, Long> abilitiesCastHistory = new HashMap<>();

    public CustomPlayer(UUID uuid) {
        this.uuid = uuid;
    }




}