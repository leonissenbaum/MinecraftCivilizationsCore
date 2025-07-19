package minecraftcivilizations.com.minecraftCivilizationsCore.API;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import minecraftcivilizations.com.minecraftCivilizationsCore.MinecraftCivilizationsCore;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config implements ConfigAPI {
    private final String CONFIG_FILE;
    private final Properties properties;
    private final Plugin plugin;
    private final Logger logger;
    private boolean isEdited = false;
    private String comment = null;
    @Getter
    private final ArrayList<Field<?>> fields = new ArrayList<>(0);
    @Setter
    @Getter
    private Double defaultDoubleValue = 0D;
    @Setter
    @Getter
    private Integer defaultIntValue = 0;
    @Setter
    @Getter
    private Boolean defaultBooleanValue = false;
    @Setter
    @Getter
    private String defaultStringValue = "off";

    public Config(Plugin plugin, String fileName, String comment) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        this.comment = comment;
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.properties = new Properties();
        initialize();
    }

    public Config(Plugin plugin, String fileName) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.properties = new Properties();
        initialize();
    }

    public Config(Plugin plugin, String fileName, String comment, Field<?>... fields) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        this.comment = comment;
        this.fields.addAll(List.of(fields));
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.properties = new Properties();
        initialize();
    }

    public Config(Plugin plugin, String fileName, Field<?>... fields) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        this.fields.addAll(List.of(fields));
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

    @Override
    public void save() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            if (!isEdited) return;
            if (comment == null) {
                properties.store(output, "Application Configuration");
                isEdited = false;
                return;
            }
            properties.store(output, comment);
            isEdited = false;
        } catch (IOException e) {
            logger.severe("Could not save config file " + CONFIG_FILE + '\n' + e.getMessage());
        }
    }


    @Override
    public void load() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);


            for (Field<?> field : fields) {
                if (!properties.containsKey(field.getName())) {
                    addFieldToConfig(field, Optional.empty());
                }
            }
            logger.info("isEdited " + isEdited);

            if (isEdited) {
                save();
                logger.info("SAVEEEE");
            }
        } catch (IOException e) {
            logger.warning("Config file not found, using defaults");
        }
    }

    private void addFieldToConfig(Field<?> field, Optional<?> value) {
        if (value.isEmpty()) {
            if (field.getValueType().equals(String.class)) {
                setString(field.getName(), defaultStringValue);
                isEdited = true;
            } else if (field.getValueType().equals(Integer.class)) {
                setInteger(field.getName(), defaultIntValue);
                isEdited = true;
            } else if (field.getValueType().equals(Double.class)) {
                setDouble(field.getName(), defaultDoubleValue);
                isEdited = true;
            } else if (field.getValueType().equals(Boolean.class)) {
                setBoolean(field.getName(), defaultBooleanValue);
                isEdited = true;
            }
        } else {
            if (field.getValueType().equals(String.class) && value.get() instanceof String) {
                setString(field.getName(), (String) value.get());
                isEdited = true;
            } else if (field.getValueType().equals(Integer.class) && value.get() instanceof Integer) {
                setInteger(field.getName(), (Integer) value.get());
                isEdited = true;
            } else if (field.getValueType().equals(Double.class) && value.get() instanceof Double) {
                setDouble(field.getName(), ((Double) value.get()));
                isEdited = true;
            } else if (field.getValueType().equals(Boolean.class) && value.get() instanceof Boolean) {
                setBoolean(field.getName(), (Boolean) value.get());
                isEdited = true;
            }
        }
        logger.info("IS EDITED: " + isEdited);
    }

    @Override
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }

    @Override
    public boolean doesFieldExist(String key) {
        return this.properties.containsKey(key);
    }

    @Override
    public void reload() {
        properties.clear();
        load();
        logger.info("Loaded config files for: " + CONFIG_FILE);
    }

    @Override
    public String getString(String key) {
        return properties.getProperty(key, defaultStringValue);
    }


    @Override
    public void setString(String key, String value) {
        properties.setProperty(key, value);
        isEdited = true;
    }

    @Override
    public Integer getInteger(String key) {
        return Integer.parseInt(properties.getProperty(key, defaultDoubleValue.toString()));
    }

    @Override
    public void setInteger(String key, Integer value) {
        properties.setProperty(key, value.toString());
        isEdited = true;
    }

    @Override
    public Double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key, defaultDoubleValue.toString()));
    }

    @Override
    public void setDouble(String key, Double value) {
        properties.setProperty(key, value.toString());
        isEdited = true;
    }


    @Override
    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key, defaultBooleanValue.toString()));
    }


    @Override
    public void setBoolean(String key, Boolean value) {
        properties.setProperty(key, value.toString());
        isEdited = true;
    }
}
