package minecraftcivilizations.com.minecraftCivilizationsCore.Ability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Consumer;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomAbility {
    private String name;
    private String description;
    private int cooldown;
    private Consumer<?> abilityFunction;
    private AbilityCastEvent castEvent;

}
