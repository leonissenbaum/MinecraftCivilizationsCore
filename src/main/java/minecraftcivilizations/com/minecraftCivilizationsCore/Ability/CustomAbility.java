package minecraftcivilizations.com.minecraftCivilizationsCore.Ability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.NamespacedKey;

import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
public class CustomAbility {
    private String name;
    private String description;
    private int cooldown;
    private Consumer<?> abilityFunction;
    private AbilityCastEvent castEvent;
}
