package minecraftcivilizations.com.minecraftCivilizationsCore.GUI;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GUIItem {
    private ItemStack item;
    private Runnable onClick;
}
