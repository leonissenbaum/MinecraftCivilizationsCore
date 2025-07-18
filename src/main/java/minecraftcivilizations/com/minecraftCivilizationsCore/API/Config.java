package minecraftcivilizations.com.minecraftCivilizationsCore.API;

import lombok.Getter;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config implements ConfigAPI {
    private final String CONFIG_FILE;
    private final Properties properties;
    private final Plugin plugin;
    private final Logger logger;
    @Setter
    @Getter
    private Double defaultDoubleValue = 0D;
    @Setter
    @Getter
    private Integer defaultIntValue = 0;
    @Setter
    @Getter
    private Boolean defaultBooleanValue = false;

    public Config(Plugin plugin, String fileName) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.properties = new Properties();
        initialize();
    }

    private void initialize() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
            logger.info("Created plugin data folder: " + plugin.getDataFolder().getPath());
        }

        File f = new File(CONFIG_FILE);
        if (!f.exists()) {
            try {
                f.createNewFile();
                MinecraftCivilizationsCore.logger.log(Level.INFO, "Created config files for: " + plugin.getName());
            } catch (IOException e) {}
        }
        load();
    }

    public Config(Plugin plugin, String fileName, Logger logger) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        initialize();
        this.plugin = plugin;
        this.logger = logger;
        this.properties = new Properties();
    }

    @Override
    public void save() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Application Configuration");
        } catch (IOException e) {
            logger.warning("Could not save config file " + CONFIG_FILE + '\n' + e.getMessage());
        }
    }

    @Override
    public void save(String comment) {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, comment);
        } catch (IOException e) {
            logger.warning("Could not save config file " + CONFIG_FILE + '\n' + e.getMessage());
        }
    }

    @Override
    public void load() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            System.out.println("Config file not found, using defaults");
        }
    }

    @Override
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    @Override
    public void reload() {

    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key);
    }


    @Override
    public void setString(String key, String value) {
        properties.setProperty(key, value);
    }

    @Override
    public Integer getInteger(String key) {
        return Integer.parseInt(properties.getProperty(key, defaultDoubleValue.toString()));
    }

    @Override
    public void setInteger(String key, Integer value) {
        properties.setProperty(key, value.toString());
    }

    @Override
    public Double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key, defaultDoubleValue.toString()));
    }

    @Override
    public void setDouble(String key, Double value) {
        properties.setProperty(key, value.toString());
    }


    @Override
    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key, defaultBooleanValue.toString()));
    }


    @Override
    public void setBoolean(String key, Boolean value) {
        properties.setProperty(key, value.toString());
    }
}
