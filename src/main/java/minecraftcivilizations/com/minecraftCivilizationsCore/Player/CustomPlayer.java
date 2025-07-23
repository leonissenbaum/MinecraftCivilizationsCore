package minecraftcivilizations.com.minecraftCivilizationsCore.Player;


import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.GUI.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CustomPlayer {
    private final UUID uuid;
    private final List<UUID> GUIHistory = new ArrayList<>();
    private UUID currentGUI;
    private UUID nextGUI;

    public CustomPlayer(UUID uuid) {
        this.uuid = uuid;
    }




}