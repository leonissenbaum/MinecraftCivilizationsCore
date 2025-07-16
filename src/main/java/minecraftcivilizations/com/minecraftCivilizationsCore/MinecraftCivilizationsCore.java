package minecraftcivilizations.com.minecraftCivilizationsCore;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class MinecraftCivilizationsCore extends JavaPlugin {
    public static Logger logger;

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger = getLogger();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static MinecraftCivilizationsCore getInstance() {
        return getPlugin(MinecraftCivilizationsCore.class);
    }

}
