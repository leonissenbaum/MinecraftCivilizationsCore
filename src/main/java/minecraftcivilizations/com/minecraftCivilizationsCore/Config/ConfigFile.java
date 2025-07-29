package minecraftcivilizations.com.minecraftCivilizationsCore.Config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import minecraftcivilizations.com.minecraftCivilizationsCore.Options.Pair;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ConfigFile {
    private final String CONFIG_FILE;
    private final Properties properties;
    private final Plugin plugin;
    private final Logger logger;
    private boolean isEdited = false;
    private String comment = null;
    @Getter
    private final ArrayList<Pair<?, ?>> fields = new ArrayList<>(0);

    public ConfigFile(@NotNull Plugin plugin, @NotNull String fileName, @Nullable String comment, @NotNull Consumer<ArrayList<Pair<?, ?>>> consumer) {
        this.CONFIG_FILE = plugin.getDataFolder() + "/" + fileName + ".properties";
        if (comment != null) this.comment = comment;
        consumer.accept(this.fields);
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
            } catch (IOException e) {}
        }
        ConfigFilesManager.getConfigFiles().add(this);
        load();
    }


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


    
    public void load() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            for (Pair<?, ?> field : fields) {
                if (!properties.containsKey(new Gson().toJson(field.firstValue()))) {
                    set(field.firstValue(), field.secondValue());
                }
            }
            if (isEdited) {
                save();
            }
        } catch (IOException e) {
            logger.warning("ConfigFile file not found, using defaults");
        }
    }
    
    public boolean isEmpty() {
        return this.properties.isEmpty();
    }
    
    public boolean doesFieldExist(String key) {
        return this.properties.containsKey(key);
    }
    
    public void reload() {
        properties.clear();
        load();
        logger.info("Loaded config files for: " + CONFIG_FILE);
    }

    /**
     * Function works by converting the firstValue to a JSON string and then checking if a secondValue for that firstValue exists.
     *
     * @param key Key of type K
     * @param type Type of the secondValue of T
     * @return Value of type T
     */
    public <K, T> T get(K key, Type type) {
        return new Gson().fromJson(this.properties.getProperty(new Gson().toJson(key, key.getClass())), type);
    }

    public <K, T> void set(K key, T value) {
        properties.setProperty(new Gson().toJson(key), new Gson().toJson(value));
        isEdited = true;
    }

    @Deprecated(forRemoval = true, since = "0.4.3")
    public String getString(String key) {
        return properties.getProperty(key);
    }


    @Deprecated(forRemoval = true, since = "0.4.3")
    public void setString(String key, String value) {
        properties.setProperty(key, value);
        isEdited = true;
    }

    @Deprecated(forRemoval = true, since = "0.4.3")
    public Integer getInteger(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }

    @Deprecated(forRemoval = true, since = "0.4.3")
    public void setInteger(String key, Integer value) {
        properties.setProperty(key, value.toString());
        isEdited = true;
    }

    @Deprecated(forRemoval = true, since = "0.4.3")
    public Double getDouble(String key) {
        return Double.parseDouble(properties.getProperty(key));
    }

    @Deprecated(forRemoval = true, since = "0.4.3")
    public void setDouble(String key, Double value) {
        properties.setProperty(key, value.toString());
        isEdited = true;
    }


    @Deprecated(forRemoval = true, since = "0.4.3")
    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }


    @Deprecated(forRemoval = true, since = "0.4.3")
    public void setBoolean(String key, Boolean value) {
        properties.setProperty(key, value.toString());
        isEdited = true;
    }
}
