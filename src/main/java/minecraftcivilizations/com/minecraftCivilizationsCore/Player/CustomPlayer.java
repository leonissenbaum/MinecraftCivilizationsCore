package minecraftcivilizations.com.minecraftCivilizationsCore.Player;


import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Ability.CustomAbility;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;

import java.util.*;


public class CustomPlayer {
    @Getter
    @Setter
    private final UUID uuid;
    @Getter
    @Setter
    private UUID currentGUI;
    @Getter
    @Setter
    private UUID nextGUI;
    private String name;
    @Getter
    @Setter
    private Map<NamespacedKey, Long> abilitiesCastHistory = new HashMap<>();

    public CustomPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public Component getName() {
        return GsonComponentSerializer.gson().deserialize(name);
    }

    public void setName(Component name) {
        this.name = GsonComponentSerializer.gson().serialize(name);
    }

}